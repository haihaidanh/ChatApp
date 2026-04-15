package com.example.chat_app1204.data.source.remote

import android.util.Log
import com.example.chat_app1204.data.enums.MessageCategory
import com.example.chat_app1204.data.enums.MessageType
import com.example.chat_app1204.data.model.Conversation
import com.example.chat_app1204.data.model.GroupInfo
import com.example.chat_app1204.data.model.Message
import com.example.chat_app1204.data.model.MessageRequest
import com.example.chat_app1204.data.model.NotificationRequest
import com.example.chat_app1204.data.model.User
import com.example.chat_app1204.data.model.UserResponse
import com.example.chat_app1204.data.request.LogInRequest
import com.example.chat_app1204.data.source.local.MyPreference
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class RemoteDataSource @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val appService: AppService,
    private val myPreference: MyPreference
) {
    suspend fun login(loginRequest: LogInRequest): UserResponse {
        val result = appService.login(
            loginRequest
        )
        if (result.isSuccessful) {
            val user = result.body()
            if (user != null) {
                myPreference.saveInfo(user)
                return user
            } else {
                throw Exception("Login failed: Empty response")
            }
        } else {
            throw Exception("Login failed: ${result.code()} ${result.message()}")
        }
    }

    fun setStatus(userId: String) {
        val userRef = FirebaseDatabase.getInstance()
            .getReference("users")
            .child(userId)

        val connectedRef = FirebaseDatabase.getInstance()
            .getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java) ?: false

                if (connected) {
                    val statusUpdate = mapOf("online" to true)
                    userRef.updateChildren(statusUpdate)

                    val offlineUpdate = mapOf(
                        "online" to false,
                        "lastSeen" to System.currentTimeMillis()
                    )
                    userRef.onDisconnect().updateChildren(offlineUpdate)
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }


    suspend fun getFriendOnline(callback: (List<User>) -> Unit) {
        val result = appService.getFriendList()
        if (result.isSuccessful) {

            val users = result.body()?.friends!!.map {
                User(
                    id = it.friendId,
                    name = it.friend.username,
                    avatarUrl = it.friend.avatarUrl,
                    online = false,
                    lastSeen = 0L
                )
            }

            val friends = result.body()?.friends!!.map {
                it.friendId
            }

            firebaseDatabase.reference.child("users").child(myPreference.getId() ?: "")
                .child("friends").setValue(friends)


            result.body()?.friends?.forEach { friend ->
                val id =
                    if (friend.friendId < friend.userId) "${friend.friendId}-${friend.userId}" else "${friend.userId}-${friend.friendId}"
                val ref = firebaseDatabase.reference
                    .child("conversations")
                    .child(id)

                ref.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        ref.setValue(
                            Conversation(
                                senderId = friend.userId,
                                receiverId = friend.friendId,
                                seen = true,
                                lastMessage = ""
                            )
                        )
                    }
                }
            }

            users.forEach { user ->
                val ref = firebaseDatabase.reference
                    .child("users")
                    .child(user.id)

                ref.get().addOnSuccessListener { snapshot ->
                    if (!snapshot.exists()) {
                        ref.setValue(user)
                    }
                }
            }


            firebaseDatabase.reference.child("users")
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        snapshot.children.forEach { userSnapshot ->
                            val list =
                                snapshot.children.mapNotNull { it.getValue(User::class.java) }
                                    .filter { it.online }
                            Log.d("hai", "Friend list: $list")
                            callback(list)
                        }

                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
        } else {
            throw Exception("Failed to get friend list: ${result.code()} ${result.message()}")
        }
    }

    fun getFriends(
        userId: String,
        callback: (List<User>) -> Unit
    ) {
        val db = firebaseDatabase.reference

        val friendsRef = db.child("users").child(userId).child("friends")
        val usersRef = db.child("users")

        friendsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(friendSnapshot: DataSnapshot) {

                Log.d("hai", userId)
                val friendIds = friendSnapshot.children.mapNotNull { it.value }
                Log.d("hai", "Friend IDs: $friendIds")
                usersRef.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(userSnapshot: DataSnapshot) {

                        val friendList = userSnapshot.children
                            .mapNotNull { it.getValue(User::class.java) }
                            .filter { user ->
                                friendIds.contains(user.id) // ⚠️ user phải có id
                            }

                        Log.d("hai", "Friend list: $friendList")
                        callback(friendList)
                    }

                    override fun onCancelled(error: DatabaseError) {}
                })
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    suspend fun logout() {
        appService.logout()
        myPreference.clearInfo()
    }

    fun getConversationList(userId: String, callback: (List<Conversation>) -> Unit) {
        val userRef = firebaseDatabase.reference.child("users").child(userId).child("friends")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendIds = snapshot.children.mapNotNull { it.getValue(String::class.java) }

                CoroutineScope(Dispatchers.IO).launch {
                    val conversationList = friendIds.map { friendId ->
                        val chatId =
                            if (userId < friendId) "$userId-$friendId" else "$friendId-$userId"
                        Log.d("hai", "Chat ID: $chatId")
                        val conversationSnapshot = firebaseDatabase.reference
                            .child("conversations")
                            .child(chatId)
                            .get()
                            .await()
                        val friend = firebaseDatabase.reference
                            .child("users")
                            .child(friendId)
                            .get()
                            .await()
                            .getValue(User::class.java)
                        val conversation = conversationSnapshot.getValue(Conversation::class.java)
                        conversation?.avatarUrl = friend?.avatarUrl
                        conversation?.name = friend?.name
                        conversation
                    }.filterNotNull()

                    withContext(Dispatchers.Main) {
                        Log.d("hai", "Conversation list: $conversationList")
                        callback(conversationList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getConversationList2(userId: String, callback: (List<Conversation>) -> Unit) {
        val userRef = firebaseDatabase.reference.child("users").child(userId).child("friends")

        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val friendIds = snapshot.children.mapNotNull { it.getValue(String::class.java) }

                CoroutineScope(Dispatchers.IO).launch {
                    // Sử dụng async để tạo danh sách các công việc chạy song song
                    val deferredConversations = friendIds.map { friendId ->
                        async {
                            try {
                                val chatId = if (userId < friendId) "$userId-$friendId" else "$friendId-$userId"

                                // Chạy 2 request lấy data của 1 dòng chat cùng lúc
                                val convTask = firebaseDatabase.reference.child("conversations").child(chatId).get()
                                val userTask = firebaseDatabase.reference.child("users").child(friendId).get()

                                val convSnap = convTask.await()
                                val userSnap = userTask.await()

                                val conversation = convSnap.getValue(Conversation::class.java)
                                val friend = userSnap.getValue(User::class.java)

                                conversation?.apply {
                                    avatarUrl = friend?.avatarUrl
                                    name = friend?.name
                                }
                            } catch (e: Exception) {
                                null
                            }
                        }
                    }

                    // Đợi tất cả các "mảnh ghép" hoàn thành cùng lúc
                    val conversationList = deferredConversations.awaitAll().filterNotNull()

                    withContext(Dispatchers.Main) {
                        callback(conversationList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}
        })
    }

    fun getAllConversations(userId: String, callback: (List<Conversation>) -> Unit) {
        val database = firebaseDatabase.reference

        val friendsRef = database.child("users").child(userId).child("friends")
        val groupsRef = database.child("users").child(userId).child("groups")

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // 1. Lấy IDs song song
                val friendsTask = friendsRef.get()
                val groupsTask = groupsRef.get()

                val friendIds = friendsTask.await().children.mapNotNull { it.getValue(String::class.java) }
                val groupIds = groupsTask.await().children.mapNotNull { it.key }

                Log.d("hai", "Friend IDs: $friendIds")
                Log.d("hai", "Group IDs: $groupIds")

                // 2. Fetch dữ liệu chi tiết cho từng loại (Parallel)
                val personalConvs = friendIds.map { friendId ->
                    async {
                        val chatId = if (userId < friendId) "$userId-$friendId" else "$friendId-$userId"
                        fetchPersonalData(chatId, friendId)
                    }
                }

                val groupConvs = groupIds.map { groupId ->
                    async {
                        fetchGroupData(groupId)
                    }
                }

                // 3. Kết hợp và sắp xếp
                val allList = (personalConvs.awaitAll() + groupConvs.awaitAll())
                    .filterNotNull()
                    .sortedByDescending { it.lastMessageTime ?: 0L }

                withContext(Dispatchers.Main) {
                    callback(allList)
                }
            } catch (e: Exception) {
                Log.e("hai", "Error: ${e.message}")
            }
        }
    }

    private suspend fun fetchPersonalData(chatId: String, friendId: String): Conversation? {
        val convSnap = firebaseDatabase.reference.child("conversations").child(chatId).get().await()
        val friendSnap = firebaseDatabase.reference.child("users").child(friendId).get().await()

        val conversation = convSnap.getValue(Conversation::class.java)
        val friend = friendSnap.getValue(User::class.java)

        return conversation?.apply {
            avatarUrl = friend?.avatarUrl
            name = friend?.name
        }
    }

    private suspend fun fetchGroupData(groupId: String): Conversation? {
        val groupSnap = firebaseDatabase.reference.child("groups").child(groupId).get().await()
        val groupInfo = groupSnap.getValue(GroupInfo::class.java)

        return groupInfo?.let {
            Conversation(
                groupId = it.groupId,
                name = it.name,
                avatarUrl = null,
                seen = true,
                lastMessage = ""
            )
        }
    }


    fun sendMessage(
        senderId: String,
        receiverId: String? = null,
        message: String,
        avatarUrl: String,
        groupId: String? = null,
        type: String,
        category: String
    ) {
        if (groupId != null) {
            //Log.d("hai", "Message: $message")
            val messageData = MessageRequest(
                senderId = senderId,
                groupId = groupId,
                message = message,
                avatarUrl = avatarUrl,
                timestamp = System.currentTimeMillis(),
                type = type,
                category = category,
            )
            firebaseDatabase.reference.child("groups").child(groupId).child("group_messages").push()
                .setValue(messageData)

            val groupMap = mapOf(
                "seen" to false,
                "senderId" to senderId,
                "lastMessage" to message,
                "lastMessageTime" to System.currentTimeMillis(),
                "type" to type,
            )


            firebaseDatabase.reference
                .child("conversations")
                .child(groupId)
                .updateChildren(groupMap.toMap())

            return
        }

        val chatId =
            if (senderId < receiverId!!) "$senderId-$receiverId" else "$receiverId-$senderId"
        val messageData = MessageRequest(
            senderId = senderId,
            receiverId = receiverId,
            avatarUrl = avatarUrl,
            message = message,
            timestamp = System.currentTimeMillis(),
            type = type,
            category = category
        )
        firebaseDatabase.reference.child("messages").child(chatId).push().setValue(messageData)

        val conversation = mapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "lastMessage" to message,
            "timestamp" to System.currentTimeMillis(),
            "type" to type,
        )

        firebaseDatabase.reference
            .child("conversations")
            .child(chatId)
            .updateChildren(conversation)
    }

    fun getGroupMessages(
        groupId: String,
        callback: (List<Message>) -> Unit
    ) {
        firebaseDatabase.reference
            .child("groups")
            .child(groupId)
            .child("group_messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val messages = snapshot.children.mapNotNull {
                        it.getValue(Message::class.java)
                    }

                    callback(messages)
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    fun receiveMessage(
        senderId: String,
        receiverId: String?,
        onMessageReceived: (List<Message>) -> Unit
    ) {
        val chatId = if (senderId < receiverId!!) "$senderId-$receiverId" else "$receiverId-$senderId"
        Log.d("hai", "Chat ID for receiving messages: $chatId")
        firebaseDatabase.reference.child("messages").child(chatId)
            .orderByChild("timestamp") // Sắp xếp theo thời gian tăng dần
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = mutableListOf<Message>()

                    for (data in snapshot.children) {
                        val msg = data.getValue(Message::class.java)
                        if (msg != null) {
                            // Gán key của Firebase vào trường id nếu bạn cần dùng
                            messageList.add(msg.copy(id = data.key ?: ""))
                        }
                    }

                    Log.d("hai", "Message list: $messageList")
                    onMessageReceived(messageList)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Firebase", "Lỗi: ${error.message}")
                }
            })
    }

    fun createGroup(name: String, memberIds: List<String>) {
        val database = firebaseDatabase.reference
        val groupId = database.child("groups").push().key ?: return

        val groupData = GroupInfo(
            groupId = groupId,
            name = name,
            members = memberIds
        )

        val conversationData = Conversation(
            groupId = groupId,
            name = name,
            seen = true,
            lastMessage = ""
        )

        // Tạo một Map để cập nhật dữ liệu ở nhiều nơi cùng lúc (Atomic Update)
        val updates = hashMapOf<String, Any>()

        // 1. Thêm vào node groups
        updates["/groups/$groupId"] = groupData

        // 2. Thêm vào node conversations
        updates["/conversations/$groupId"] = conversationData

        // 3. Cập nhật danh sách group cho TỪNG thành viên
        memberIds.forEach { userId ->
            Log.d("MainActivity", "Updating group for user: $userId")
            updates["/users/$userId/groups/$groupId"] = true
        }

        // Thực hiện update một lần duy nhất
        database.updateChildren(updates).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("hai", "Tạo group thành công!")
            }
        }
    }

    suspend fun sendNotification(notification: NotificationRequest){
        appService.sendNotification(notification)
    }

    suspend fun checkStatus(userId: String): Boolean {
        val userRef = firebaseDatabase.reference
            .child("users")
            .child(userId)

        val snapshot = userRef.get().await()
        val user = snapshot.getValue(User::class.java)
        return user?.online ?: false
    }
}
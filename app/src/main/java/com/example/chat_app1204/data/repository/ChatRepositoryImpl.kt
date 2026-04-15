package com.example.chat_app1204.data.repository

import com.example.chat_app1204.data.model.Message
import com.example.chat_app1204.data.model.MessageRequest
import com.example.chat_app1204.data.source.remote.RemoteDataSource
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : ChatRepository {
    override suspend fun sendMessage(messageRequest: MessageRequest): Result<Unit> {
        return try {
            remoteDataSource.sendMessage(
                senderId = messageRequest.senderId,
                receiverId = messageRequest.receiverId,
                groupId = messageRequest.groupId,
                type = messageRequest.type,
                category = messageRequest.category,
                message = messageRequest.message,
                avatarUrl = messageRequest.avatarUrl
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getMessages(userId: String, friendId: String?): Result<List<Message>> {
        TODO("Not yet implemented")
    }

    override suspend fun seenMessage(userId: String, groupId: String?, friendId: String?) {
        remoteDataSource.seenMessage(userId, groupId, friendId)
    }

}
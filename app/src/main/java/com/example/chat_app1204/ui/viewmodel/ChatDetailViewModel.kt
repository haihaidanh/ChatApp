package com.example.chat_app1204.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app1204.data.enums.MessageCategory
import com.example.chat_app1204.data.enums.MessageType
import com.example.chat_app1204.data.model.Message
import com.example.chat_app1204.data.model.MessageRequest
import com.example.chat_app1204.data.model.UserResponse
import com.example.chat_app1204.data.repository.ChatRepository
import com.example.chat_app1204.data.repository.FriendsRepository
import com.example.chat_app1204.data.source.remote.RemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatDetailViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val remoteDataSource: RemoteDataSource,
    private val friendsRepository: FriendsRepository
) : ViewModel() {
    private val _messages = MutableLiveData<List<Message>>()
    val messages: LiveData<List<Message>> = _messages

    private val _checkStatus = MutableLiveData(false)
    val checkStatus: LiveData<Boolean> = _checkStatus

    fun sendMessage(
        senderId: String,
        receiverId: String? = null,
        groupId: String? = null,
        avatarUrl: String,
        type: String,
        category: String,
        message: String
    ) {
        viewModelScope.launch {
            chatRepository.sendMessage(
                MessageRequest(
                    senderId = senderId,
                    receiverId = receiverId,
                    groupId = groupId,
                    avatarUrl = avatarUrl,
                    type = type,
                    category = category,
                    message = message
                )
            )

        }
    }

    fun getMessages(userId: String, friendId: String? = null, groupId: String? = null) {
        viewModelScope.launch {
            if(groupId != null){
                remoteDataSource.getGroupMessages(groupId){
                    _messages.value = it
                }
            }else{

            remoteDataSource.receiveMessage(userId, friendId){
                _messages.value = it
            }
            }

        }
    }

    fun checkStatus(userId: String){
        viewModelScope.launch {
            val status = friendsRepository.checkStatus(userId)
            _checkStatus.value = status
        }
    }
}
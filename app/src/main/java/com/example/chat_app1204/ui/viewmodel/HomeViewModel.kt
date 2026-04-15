package com.example.chat_app1204.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app1204.data.model.Conversation
import com.example.chat_app1204.data.model.User
import com.example.chat_app1204.data.repository.ChatRepository
import com.example.chat_app1204.data.source.local.MyPreference
import com.example.chat_app1204.data.source.remote.RemoteDataSource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val myPreference: MyPreference,
    private val remoteDataSource: RemoteDataSource,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _friendList = MutableLiveData<List<User>>()
    val friendList: LiveData<List<User>> = _friendList

    private val _checkLogIn = MutableLiveData(false)
    val checkLogIn: LiveData<Boolean> = _checkLogIn

    private val _conversations = MutableLiveData<List<Conversation>>(emptyList())
    val conversation: LiveData<List<Conversation>> = _conversations

    private val _avatar = MutableLiveData("")
    val avatar: LiveData<String> = _avatar

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _friends = MutableLiveData<List<User>>(emptyList())
    val friends: LiveData<List<User>> = _friends

    fun getFriendOnline() {
        viewModelScope.launch {
            remoteDataSource.getFriendOnline {
                _friendList.value = it
            }
        }
    }

    fun getFriends() {
        viewModelScope.launch {
            val userId = myPreference.getId() ?: ""
            remoteDataSource.getFriends(userId) {
                _friends.value = it
            }
        }
    }

    fun getUserId(): String? {
        return myPreference.getId()
    }


    fun setOnline() {
        viewModelScope.launch {
            val userId = myPreference.getId() ?: ""
            remoteDataSource.setStatus(userId)
        }
    }

    fun getConversationList() {
        viewModelScope.launch {
            val userId = myPreference.getId() ?: ""
            remoteDataSource.getAllConversations(userId) {
                _conversations.value = it
            }
        }
    }

    fun checkLogIn() {
        viewModelScope.launch {
            _checkLogIn.value = myPreference.getAccessToken() != null
        }
    }

    fun logOut() {

        myPreference.clearInfo()
        _checkLogIn.value = false
    }

    fun getUsername() {
        _username.value = myPreference.getName()
    }

    fun getAvatarUrl() {
        viewModelScope.launch {
            val avatarUrl = myPreference.getAvatarUrl()
            _avatar.value = avatarUrl
        }
    }

    fun seenMessage(groupId: String? = null, friendId: String? = null) {
        viewModelScope.launch {
            myPreference.getId()?.let { userId ->
                chatRepository.seenMessage(userId, groupId, friendId)
            }
        }
    }

    fun getFriendsConversation() {
        viewModelScope.launch {
            myPreference.getId()?.let { userId ->
                remoteDataSource.getFriendConversations(userId) {
                    _conversations.value = it
                }
            }
        }
    }

    fun getGroupConversation() {
        viewModelScope.launch {
            myPreference.getId()?.let { userId ->
                remoteDataSource.getGroupConversations(userId) {
                    _conversations.value = it
                }
            }
        }
    }

}
package com.example.chat_app1204.data.repository

import com.example.chat_app1204.data.model.Friend
import com.example.chat_app1204.data.model.User

interface FriendsRepository {
    suspend fun setStatus(userId: String)
    suspend fun checkStatus(userId: String): Boolean
}
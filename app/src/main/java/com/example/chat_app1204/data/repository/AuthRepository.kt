package com.example.chat_app1204.data.repository

import com.example.chat_app1204.data.model.UserResponse
import com.example.chat_app1204.data.request.LogInRequest

interface AuthRepository {
    suspend fun login(loginRequest: LogInRequest): UserResponse
    suspend fun logout()
}
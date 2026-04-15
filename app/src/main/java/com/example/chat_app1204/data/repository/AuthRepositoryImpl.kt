package com.example.chat_app1204.data.repository

import com.example.chat_app1204.data.model.UserResponse
import com.example.chat_app1204.data.request.LogInRequest
import com.example.chat_app1204.data.source.remote.RemoteDataSource
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
): AuthRepository {
    override suspend fun login(loginRequest: LogInRequest): UserResponse {
        val result =  remoteDataSource.login(loginRequest)
        return result
    }

    override suspend fun logout() {
        remoteDataSource.logout()
    }
}
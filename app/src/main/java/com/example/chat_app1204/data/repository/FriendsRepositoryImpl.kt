package com.example.chat_app1204.data.repository

import com.example.chat_app1204.data.source.remote.RemoteDataSource
import javax.inject.Inject

class FriendsRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
): FriendsRepository {

    override suspend fun setStatus(userId: String) {
        remoteDataSource.setStatus(userId)

    }

    override suspend fun checkStatus(userId: String): Boolean {
        return remoteDataSource.checkStatus(userId)
    }
}
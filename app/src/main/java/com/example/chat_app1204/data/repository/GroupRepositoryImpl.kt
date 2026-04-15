package com.example.chat_app1204.data.repository

import com.example.chat_app1204.data.source.remote.RemoteDataSource
import javax.inject.Inject

class GroupRepositoryImpl @Inject constructor(
    private val remoteDataSource: RemoteDataSource
) : GroupRepository {
    override suspend fun createGroup(
        groupName: String,
        memberIds: List<String>
    ) {
        remoteDataSource.createGroup(groupName, memberIds)
    }

    override suspend fun receiveGroupMessages(groupId: String) {
        TODO("Not yet implemented")
    }
}
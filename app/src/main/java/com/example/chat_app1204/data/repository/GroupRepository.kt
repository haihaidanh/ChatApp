package com.example.chat_app1204.data.repository

interface GroupRepository {
    suspend fun createGroup(groupName: String, memberIds: List<String>)
    suspend fun receiveGroupMessages(groupId: String)
}
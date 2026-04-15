package com.example.chat_app1204.data.repository

import android.content.Context
import android.net.Uri
import com.example.chat_app1204.data.model.Message
import com.example.chat_app1204.data.model.MessageRequest
import com.example.chat_app1204.data.model.UrlResponse

interface ChatRepository {
    suspend fun sendMessage(messageRequest: MessageRequest): Result<Unit>
    suspend fun getMessages(userId: String, friendId: String? = null): Result<List<Message>>
    suspend fun seenMessage(userId: String, groupId: String? = null, friendId: String? = null)
    suspend fun sendImageMessage(uri: Uri, context: Context): UrlResponse
}
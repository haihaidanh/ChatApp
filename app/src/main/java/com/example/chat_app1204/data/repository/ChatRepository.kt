package com.example.chat_app1204.data.repository

import com.example.chat_app1204.data.model.Message
import com.example.chat_app1204.data.model.MessageRequest

interface ChatRepository {
    suspend fun sendMessage(messageRequest: MessageRequest): Result<Unit>
    suspend fun getMessages(userId: String, friendId: String? = null): Result<List<Message>>
}
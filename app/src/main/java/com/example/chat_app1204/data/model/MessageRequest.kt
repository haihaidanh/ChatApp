package com.example.chat_app1204.data.model

import com.example.chat_app1204.data.enums.MessageCategory
import com.example.chat_app1204.data.enums.MessageType

data class MessageRequest(
    val senderId: String="",
    val receiverId: String?=null,
    val groupId: String? = null,
    val avatarUrl: String="",
    val type: String = MessageType.TEXT.name,
    val category: String = MessageCategory.PERSONAL.name,
    val message: String = "",
    val timestamp: Long = 0L
)

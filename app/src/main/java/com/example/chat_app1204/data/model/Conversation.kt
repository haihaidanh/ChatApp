package com.example.chat_app1204.data.model

import com.example.chat_app1204.data.enums.MessageType
import java.io.Serializable

data class Conversation(
    val senderId: String? = null,
    val receiverId: String? = null,
    val groupId: String? = null,
    var name: String? = null,
    var avatarUrl: String? = null,
    val seen: Boolean? = null,
    val lastMessage: String? = null,
    val lastMessageTime: Long? = null,
    val type: String? = MessageType.TEXT.name,
) : Serializable

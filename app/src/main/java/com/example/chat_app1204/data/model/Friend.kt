package com.example.chat_app1204.data.model

data class Friend(
    val id: String,
    val userId: String,
    val friendId: String,
    val createdAt: String,
    val updatedAt: String,
    val friend: UserResponse
)

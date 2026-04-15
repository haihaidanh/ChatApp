package com.example.chat_app1204.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val avatarUrl: String = "",
    val online: Boolean = false,
    val lastSeen: Long = 0L,
    val friends: List<String> = emptyList()
)

package com.example.chat_app1204.data.model

data class GroupInfo(
    val groupId: String="",
    val name: String="",
    val avatarUrl: String?= null,
    val members: List<String> = emptyList(),
)

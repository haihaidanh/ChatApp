package com.example.chat_app1204.data.model

data class UserResponse(
    val id: String="",
    val errCode: Int =1,
    val message: String="",
    val accessToken: String="",
    val refreshToken: String="",
    val name: String="",
    val avatarUrl: String="",
    val username: String=""
)


package com.example.chat_app1204.data.source.local

import android.content.Context
import com.example.chat_app1204.data.model.UserResponse
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class MyPreference @Inject constructor(@ApplicationContext context: Context) {
    private val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

    fun saveInfo(user: UserResponse){
        sharedPreferences.edit().apply {
            putString("avatarUrl", user.avatarUrl)
            putString("username", user.username)
            putString("accessToken", user.accessToken)
            putString("id", user.id)
            apply()
        }
    }

    fun putId(id: String){
        sharedPreferences.edit().putString("id", id).apply()
    }

    fun getId(): String? {
        return sharedPreferences.getString("id", null)
    }


    fun getName(): String {
        return sharedPreferences.getString("username", "") ?: ""
    }

    fun getAvatarUrl(): String {
        return sharedPreferences.getString("avatarUrl", "") ?: ""
    }

    fun getAccessToken(): String? {
        return sharedPreferences.getString("accessToken", null)
    }

    fun clearInfo() {
        sharedPreferences.edit().clear().apply()
    }
}
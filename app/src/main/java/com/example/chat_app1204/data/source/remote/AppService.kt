package com.example.chat_app1204.data.source.remote

import com.example.chat_app1204.data.model.FriendList
import com.example.chat_app1204.data.model.NotificationRequest
import com.example.chat_app1204.data.model.UrlResponse
import com.example.chat_app1204.data.model.UserResponse
import com.example.chat_app1204.data.request.LogInRequest
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface AppService {
    @POST("/user/log-in")
    suspend fun login(
        @Body request: LogInRequest
    ) : Response<UserResponse>

    @GET("/log-out")
    suspend fun logout()

    @GET("/friend")
    suspend fun getFriendList(): Response<FriendList>

    @POST("/send-notification")
    suspend fun sendNotification(
        @Body notificationRequest: NotificationRequest
    )

    @Multipart
    @POST("/send-image-message")
    suspend fun sendImageMessage(
        @Part file: MultipartBody.Part,
    ): Response<UrlResponse>
}
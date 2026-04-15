package com.example.chat_app1204.data.di

import com.example.chat_app1204.data.repository.AuthRepository
import com.example.chat_app1204.data.repository.AuthRepositoryImpl
import com.example.chat_app1204.data.repository.ChatRepository
import com.example.chat_app1204.data.repository.ChatRepositoryImpl
import com.example.chat_app1204.data.repository.FriendsRepository
import com.example.chat_app1204.data.repository.FriendsRepositoryImpl
import com.example.chat_app1204.data.repository.GroupRepository
import com.example.chat_app1204.data.repository.GroupRepositoryImpl
import com.example.chat_app1204.data.repository.NotificationRepository
import com.example.chat_app1204.data.repository.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    abstract fun bindAuthRepository(
        authRepositoryImpl: AuthRepositoryImpl
    ): AuthRepository

    @Binds
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository

    @Binds
    abstract fun bindFriendsRepository(
        friendsRepositoryImpl: FriendsRepositoryImpl
    ): FriendsRepository

    @Binds
    abstract fun bindGroupRepository(
        groupRepositoryImpl: GroupRepositoryImpl
    ): GroupRepository

    @Binds
    abstract fun bindNotificationRepository(
        notificationRepositoryImpl: NotificationRepositoryImpl
    ): NotificationRepository
}


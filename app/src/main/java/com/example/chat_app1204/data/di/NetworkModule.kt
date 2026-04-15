package com.example.chat_app1204.data.di

import com.example.chat_app1204.data.repository.AuthInterceptor
import com.example.chat_app1204.data.source.local.MyPreference
import com.example.chat_app1204.data.source.remote.AppService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {


    private const val BASE_URL = "http://10.0.2.2:8080"
    //private const val BASE_URL = "https://be-socialmedia-production.up.railway.app"

    @Provides
    @Singleton
    @Named("auth")
    fun provideAuthRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @Named("authService")
    fun provideAuthService(
        @Named("auth") retrofit: Retrofit
    ): AppService {
        return retrofit.create(AppService::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        myPreference: MyPreference
    ): AuthInterceptor {
        return AuthInterceptor(myPreference)
    }

    // 🔹 RefreshTokenInterceptor
//    @Provides
//    @Singleton
//    fun provideRefreshTokenInterceptor(
//        myPreference: MyPreference,
//        @Named("authService") authService: AppService
//    ): RefreshTokenInterceptor {
//        return RefreshTokenInterceptor(myPreference, authService)
//    }

    // 🔹 OkHttp (có interceptor)
    @Provides
    @Singleton
    fun provideOkHttp(
        authInterceptor: AuthInterceptor,
        //refreshTokenInterceptor: RefreshTokenInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            //.addInterceptor(refreshTokenInterceptor)
            .addInterceptor(authInterceptor)
            .build()
    }

    // 🔹 Retrofit chính
    @Provides
    @Singleton
    @Named("main")
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    // 🔹 AppService chính
    @Provides
    @Singleton
    fun provideAppService(
        @Named("main") retrofit: Retrofit
    ): AppService {
        return retrofit.create(AppService::class.java)
    }
}
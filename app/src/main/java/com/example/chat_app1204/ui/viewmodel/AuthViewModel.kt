package com.example.chat_app1204.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app1204.data.model.UserResponse
import com.example.chat_app1204.data.repository.AuthRepository
import com.example.chat_app1204.data.request.LogInRequest
import com.example.chat_app1204.data.source.local.MyPreference
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val myPreference: MyPreference
): ViewModel() {

    private val _user = MutableLiveData<UserResponse>()
    val user: LiveData<UserResponse> = _user

    fun logIn(email: String, password: String){
        viewModelScope.launch {
            val result = authRepository.login(LogInRequest(email, password))
            _user.value = result
        }
    }


}
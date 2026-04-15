package com.example.chat_app1204.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chat_app1204.data.repository.GroupRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GroupViewModel @Inject constructor(
    private val groupRepository: GroupRepository
): ViewModel(){
    fun createGroup(groupName: String, memberIds: List<String>) {
        viewModelScope.launch {
            groupRepository.createGroup(groupName, memberIds)
        }
    }

}
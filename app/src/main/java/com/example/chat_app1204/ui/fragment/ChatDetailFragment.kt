package com.example.chat_app1204.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat_app1204.R
import com.example.chat_app1204.data.enums.MessageCategory
import com.example.chat_app1204.data.enums.MessageType
import com.example.chat_app1204.data.model.Conversation
import com.example.chat_app1204.databinding.FragmentChatDetailBinding
import com.example.chat_app1204.ui.activity.CallActivity

import com.example.chat_app1204.ui.adapter.ChatDetailAdapter
import com.example.chat_app1204.ui.viewmodel.ChatDetailViewModel
import com.example.chat_app1204.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatDetailFragment : Fragment() {

    private lateinit var mBinding: FragmentChatDetailBinding
    private val viewModel: HomeViewModel by viewModels()
    private val chatDetailViewModel: ChatDetailViewModel by viewModels()
    private lateinit var chatAdapter: ChatDetailAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentChatDetailBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var avatar: String? = null
        val conservation = arguments?.getSerializable("conversation") as? Conversation

        viewModel.getAvatarUrl()
        viewModel.avatar.observe(viewLifecycleOwner) { avatarUrl ->
            avatar = avatarUrl
        }

        val receiverId = if (viewModel.getUserId() == conservation?.senderId) {
            conservation?.receiverId
        } else {
            conservation?.senderId
        }

        conservation?.let {
            viewModel.seenMessage(
                it.groupId,
                receiverId,
            )
        }

        receiverId?.let {
            chatDetailViewModel.checkStatus(it)
        }

        chatDetailViewModel.checkStatus.observe(viewLifecycleOwner) { isOnline ->
            if (isOnline) {
                mBinding.status.text = "Online"
                mBinding.activeStatus.visibility = View.VISIBLE
            } else {
                mBinding.status.text = "Offline"
                mBinding.activeStatus.visibility = View.GONE
            }
        }

        viewModel.getUserId()?.let {
            chatAdapter = ChatDetailAdapter(
                currentUserId = it
            )
        }

        mBinding.chatRecyclerView.adapter = chatAdapter
        mBinding.backButton.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack()
        }
        Glide.with(requireContext())
            .load(conservation?.avatarUrl)
            .placeholder(R.drawable.active_light)
            .into(mBinding.profileImage)

        mBinding.username.text = conservation?.name ?: ""

        chatDetailViewModel.getMessages(
            userId = viewModel.getUserId() ?: "",
            friendId = receiverId,
            groupId = conservation?.groupId
        )
        chatDetailViewModel.messages.observe(viewLifecycleOwner) { messages ->
            //Log.d("hai", "Messages: $messages")
            chatAdapter.submitList(messages) {
                val size = messages.size
                if (size > 0) {
                    mBinding.chatRecyclerView.smoothScrollToPosition(messages.size - 1)
                }

            }
        }

        //Log.d("hai", "Receiver ID: $receiverId")
        viewModel.getUserId()?.let { userId ->
            mBinding.btnSend.setOnClickListener {
                Log.d("hai", avatar ?: "Avatar URL is null")
                val message = mBinding.etMessage.text.toString()
                if (message.isNotBlank()) {
                    chatDetailViewModel.sendMessage(
                        senderId = userId,
                        receiverId = receiverId,
                        type = MessageType.TEXT.name,
                        groupId = conservation?.groupId,
                        avatarUrl = avatar ?: "",
                        category = MessageCategory.PERSONAL.name,
                        message = message
                    )
                    mBinding.etMessage.text.clear()
                }
            }

        }

        mBinding.voiceCallButton.setOnClickListener {
            requireActivity().startActivity(Intent(requireContext(), CallActivity::class.java))
        }
        mBinding.chatOption.setOnClickListener {
            mBinding.chatOption.visibility = View.GONE
            mBinding.stickerOption.visibility = View.VISIBLE
            mBinding.imageOption.visibility = View.VISIBLE
        }
        mBinding.etMessage.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                mBinding.chatOption.visibility = View.VISIBLE
                mBinding.stickerOption.visibility = View.GONE
                mBinding.imageOption.visibility = View.GONE
            }
        }
    }
}

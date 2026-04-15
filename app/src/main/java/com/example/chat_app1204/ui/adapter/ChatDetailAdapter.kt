package com.example.chat_app1204.ui.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat_app1204.data.model.Message

import com.example.chat_app1204.databinding.ReceiverItemBinding
import com.example.chat_app1204.databinding.SenderItemBinding

class ChatDetailAdapter(
    private val currentUserId: String
) :
    ListAdapter<Message, RecyclerView.ViewHolder>(MessageDiffCallback()) {

    companion object {
        private const val TYPE_SENT = 1
        private const val TYPE_RECEIVED = 2
    }


    override fun getItemViewType(position: Int): Int {
        val message = getItem(position)
        return if (message.senderId == currentUserId) TYPE_SENT else TYPE_RECEIVED
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_SENT) {
            val binding = SenderItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            SentViewHolder(binding)
        } else {
            val binding = ReceiverItemBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ReceivedViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = getItem(position)
        if (holder is SentViewHolder) {
            holder.bind(message)
        } else if (holder is ReceivedViewHolder) {
            holder.bind(message)
        }
    }
    inner class SentViewHolder(private val binding: SenderItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textContent.text = message.message
            Glide.with(itemView.context)
                .load(message.avatarUrl)
                .circleCrop()
                .into(binding.senderProfileImage)
        }
    }

    inner class ReceivedViewHolder(private val binding: ReceiverItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            binding.textContent.text = message.message
            Log.d("hai", "Friend avatar URL: ${message.avatarUrl}")
            Glide.with(itemView.context)
                .load(message.avatarUrl)
                .circleCrop()
                .into(binding.receiverProfileImage)
        }
    }
}

// DiffUtil để tối ưu hóa việc cập nhật danh sách
class MessageDiffCallback : DiffUtil.ItemCallback<Message>() {
    override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem.id == newItem.id
    }
    override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
        return oldItem == newItem
    }
}
package com.example.chat_app1204.ui.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat_app1204.R
import com.example.chat_app1204.data.model.Conversation


class ConversationsAdapter(
    private val onItemClick: ((Conversation) -> Unit)? = null,
    private val userId: String
) : RecyclerView.Adapter<ConversationsAdapter.ConversationViewHolder>() {

    private val items = mutableListOf<Conversation>()

    inner class ConversationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgAvatar: ImageView = itemView.findViewById(R.id.imgAvatar)
        private val tvUserName: TextView = itemView.findViewById(R.id.tvUserName)
        private val tvLastMessage: TextView = itemView.findViewById(R.id.tvLastMessage)
        private val vStatus: View = itemView.findViewById(R.id.img_status_seen)

        fun bind(item: Conversation) {
            tvUserName.text = item.name
            if (item.groupId != null) {
                Log.d("hai", "Last message: ${item.lastMessage}")
                tvLastMessage.text = item.lastMessage
            } else {
                tvLastMessage.text =
                    if (item.senderId == userId) "bạn: ${item.lastMessage}" else item.lastMessage
            }
            if (item.seen == true || item.senderId == userId) {
                tvLastMessage.setTypeface(null, android.graphics.Typeface.NORMAL)
                vStatus.visibility = View.GONE
            } else {
                tvLastMessage.setTypeface(null, android.graphics.Typeface.BOLD)
                vStatus.visibility = View.VISIBLE
            }

            Glide.with(itemView.context)
                .load(item.avatarUrl)
                .placeholder(R.drawable.active_light)
                .into(imgAvatar)

            itemView.setOnClickListener {
                onItemClick?.invoke(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConversationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ConversationViewHolder(view)
    }

    override fun onBindViewHolder(holder: ConversationViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<Conversation>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

}
package com.example.chat_app1204.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat_app1204.data.model.Friend
import com.example.chat_app1204.data.model.User
import com.example.chat_app1204.databinding.ActiveItemBinding

class FriendAdapter(
    private val onItemClick: ((String) -> Unit)? = null
) : RecyclerView.Adapter<FriendAdapter.FriendViewHolder>() {

    private val items: MutableList<User> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FriendViewHolder {
        val binding = ActiveItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return FriendViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FriendViewHolder, position: Int) {
        val user = items[position]
        holder.bind(user)
        //holder.itemView.setOnClickListener { onItemClick?.invoke(name) }
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<User>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()
    }

    class FriendViewHolder(
        private val mBinding: ActiveItemBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(user: User) {
            Glide.with(mBinding.root.context)
                .load(user.avatarUrl)
                .circleCrop()
                .into(mBinding.activeItemImage)
                mBinding.activeStatusIndicator.visibility = ViewGroup.VISIBLE
        }
    }
}
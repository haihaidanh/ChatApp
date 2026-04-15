package com.example.chat_app1204.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chat_app1204.R
import com.example.chat_app1204.data.model.Friend
import com.example.chat_app1204.data.model.User
import com.example.chat_app1204.databinding.UserSelectItemBinding


class InviteFriendAdapter(
    private val onSelectionChanged: ((selectedIds: List<String>) -> Unit)? = null
) : RecyclerView.Adapter<InviteFriendAdapter.InviteFriendViewHolder>() {

    private val items = mutableListOf<User>()
    private val selectedIds = mutableSetOf<String>()

    inner class InviteFriendViewHolder(
        private val mBinding: UserSelectItemBinding
    ) : RecyclerView.ViewHolder(mBinding.root) {

        fun bind(user: User) {
            mBinding.tvUserName.text = user.name
            Glide.with(mBinding.root.context)
                .load(user.avatarUrl)
                .placeholder(R.drawable.active_light)
                .into(mBinding.imgAvatar)

            mBinding.cbSelect.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    selectedIds.add(user.id)
                } else {
                    selectedIds.remove(user.id)
                }
                onSelectionChanged?.invoke(selectedIds.toList())
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): InviteFriendViewHolder {

        val view = UserSelectItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)

        return InviteFriendViewHolder(view)
    }

    override fun onBindViewHolder(holder: InviteFriendViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    @SuppressLint("NotifyDataSetChanged")
    fun submitList(newItems: List<User>) {
        items.clear()
        items.addAll(newItems)
        notifyDataSetChanged()

    }
}
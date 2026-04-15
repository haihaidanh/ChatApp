package com.example.chat_app1204.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chat_app1204.R
import com.example.chat_app1204.databinding.FragmentHomeBinding
import com.example.chat_app1204.ui.activity.LogInActivity
import com.example.chat_app1204.ui.adapter.ConversationsAdapter
import com.example.chat_app1204.ui.adapter.FriendAdapter
import com.example.chat_app1204.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var mBinding: FragmentHomeBinding
    private val viewModel: HomeViewModel by viewModels()
    private val friendListAdapter: FriendAdapter by lazy { FriendAdapter() }
    private lateinit var conversationsAdapter: ConversationsAdapter

    override fun onCreateView(
        inflater: android.view.LayoutInflater,
        container: android.view.ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.swipeRefreshLayout.setOnRefreshListener {
            if (viewModel.checkLogIn.value == true) {
                viewModel.getFriendOnline()
                viewModel.getConversationList()
            } else {
                mBinding.swipeRefreshLayout.isRefreshing = false
            }
        }

        viewModel.getUserId()?.let {
            conversationsAdapter = ConversationsAdapter(
                onItemClick = { conversation ->
                    val bundle = Bundle().apply {
                        putSerializable("conversation", conversation)
                    }

                    findNavController().navigate(
                        R.id.action_homeFragment_to_chatDetailFragment,
                        bundle
                    )
                },
                userId = it
            )
        }

        mBinding.chip.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.chipAll -> {

                }

                R.id.chipChat -> {
                    mBinding.activeRecyclerView.visibility = View.VISIBLE
                    mBinding.userRecyclerView.visibility = View.GONE
                }
                R.id.chipGroup -> {
                    mBinding.activeRecyclerView.visibility = View.VISIBLE
                    mBinding.userRecyclerView.visibility = View.GONE
                }
                else -> {

                }
            }
        }

        mBinding.menuButton.setOnClickListener {
            requireActivity()
                .findViewById<DrawerLayout>(R.id.drawer_layout)
                .openDrawer(GravityCompat.START)
        }
        viewModel.checkLogIn.observe(viewLifecycleOwner) { isLoggedIn ->
            if (!isLoggedIn) {
                requireActivity().startActivity(
                    Intent(
                        requireActivity(),
                        LogInActivity::class.java
                    )
                )
            } else {
                viewModel.getFriendOnline()
                viewModel.getConversationList()
            }
        }
        viewModel.checkLogIn()
        mBinding.activeRecyclerView.adapter = friendListAdapter
        viewModel.friendList.observe(viewLifecycleOwner) { friendList ->
            friendListAdapter.submitList(friendList)
        }

        Log.d("hai", viewModel.getUserId() ?: "User ID is null")

        mBinding.userRecyclerView.adapter = conversationsAdapter
        viewModel.conversation.observe(viewLifecycleOwner) { conversationList ->
            conversationsAdapter.submitList(conversationList)
            mBinding.swipeRefreshLayout.isRefreshing = false
        }
    }

}
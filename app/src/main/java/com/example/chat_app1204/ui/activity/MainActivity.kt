package com.example.chat_app1204.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.example.chat_app1204.R
import com.example.chat_app1204.databinding.ActivityMainBinding
import com.example.chat_app1204.databinding.DialogCreateGroupBinding
import com.example.chat_app1204.ui.adapter.InviteFriendAdapter
import com.example.chat_app1204.ui.viewmodel.GroupViewModel
import com.example.chat_app1204.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var drawerToggle: ActionBarDrawerToggle
    private val viewModel: HomeViewModel by viewModels()
    private lateinit var mBinding: ActivityMainBinding
    private val groupViewModel: GroupViewModel by viewModels()
    private lateinit var inviteFriendAdapter: InviteFriendAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController
        mBinding.bottomNavigation.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            if (destination.id == R.id.chatDetailFragment) {
                mBinding.bottomNavigation.visibility = View.GONE
                mBinding.toolbar.visibility = View.GONE
            } else {
                mBinding.bottomNavigation.visibility = View.VISIBLE
                mBinding.toolbar.visibility = View.VISIBLE
            }
        }

        viewModel.setOnline()

        setSupportActionBar(mBinding.toolbar)
        headerDrawer()


        viewModel.checkLogIn()
        viewModel.checkLogIn.observe(this) { isLoggedIn ->
            if (!isLoggedIn) {
                startActivity(Intent(this, LogInActivity::class.java))
            }
        }

//        drawerToggle = ActionBarDrawerToggle(
//            this,
//            mBinding.drawerLayout,
//            mBinding.toolbar,
//            R.string.open,
//            R.string.close
//        )
//        mBinding.drawerLayout.addDrawerListener(drawerToggle)
//        drawerToggle.syncState()


        mBinding.navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_create_group -> {

                    val binding = DialogCreateGroupBinding.inflate(layoutInflater)

                    val ids = mutableListOf<String>()
                    inviteFriendAdapter = InviteFriendAdapter { selectedIds ->
                        ids.clear()
                        ids.add(viewModel.getUserId() ?: "")
                        ids.addAll(selectedIds)
                        Log.d("MainActivity", "Selected friend IDs: ${ids.size}")
                        binding.btnCreate.isEnabled = ids.size > 1
                    }

                    viewModel.getFriends()
                    viewModel.friends.observe(this) { friends ->
                        inviteFriendAdapter.submitList(friends)
                        Log.d("MainActivity", "Friends list updated: ${friends.size} friends available")
                    }

                    binding.rvFriends.adapter = inviteFriendAdapter
                    val dialog = AlertDialog.Builder(this)
                        .setView(binding.root)
                        .create()

                    binding.btnCreate.setOnClickListener {
                        val groupName = binding.edtGroupName.text.toString()
                        groupViewModel.createGroup(groupName, ids)
                        dialog.dismiss()
                    }



                    dialog.show()
                    true
                }

                R.id.nav_setting -> {
                    // TODO: Navigate to Notifications
                    true
                }

                R.id.nav_logout -> {
                    viewModel.setOnline()
                    viewModel.logOut()
                    true
                }

                else -> false
            }.also {
                if (it) mBinding.drawerLayout.closeDrawer(GravityCompat.START)
            }
        }

    }

    override fun onBackPressed() {
        if (mBinding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            mBinding.drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    fun headerDrawer() {

        val headerView = mBinding.navigationView.getHeaderView(0)

        val imgAvatar = headerView.findViewById<ImageView>(R.id.imgAvatar)
        val tvUserName = headerView.findViewById<TextView>(R.id.tvUserName)

        viewModel.getAvatarUrl()
        viewModel.avatar.observe(this) {
            Glide.with(this)
                .load(it)
                .circleCrop()
                .into(imgAvatar)
        }
        viewModel.getUsername()
        viewModel.username.observe(this) {
            tvUserName.text = it
        }
    }
}

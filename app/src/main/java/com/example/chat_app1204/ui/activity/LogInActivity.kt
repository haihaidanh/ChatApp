package com.example.chat_app1204.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.chat_app1204.databinding.ActivityLogInBinding
import com.example.chat_app1204.ui.viewmodel.AuthViewModel
import com.example.chat_app1204.ui.viewmodel.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LogInActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityLogInBinding
    private val viewModel: AuthViewModel by viewModels()
    private val homeViewModel: HomeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLogInBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(mBinding.root)

        viewModel.user.observe(this) { user ->
            if (user.errCode == 0) {
                Toast.makeText(this, "Login successful: ${user.name}", Toast.LENGTH_SHORT).show()
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Toast.makeText(this, "Login failed", Toast.LENGTH_SHORT).show()
            }
            mBinding.progressBar.visibility = View.GONE
        }

        mBinding.btnLogin.setOnClickListener {
            mBinding.progressBar.visibility = View.VISIBLE
            viewModel.logIn(
                mBinding.edtUsername.text.toString().trim(),
                mBinding.edtPassword.text.toString().trim()
            )
        }
    }
}
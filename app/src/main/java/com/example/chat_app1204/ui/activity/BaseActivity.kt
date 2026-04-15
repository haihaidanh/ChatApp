package com.example.chat_app1204.ui.activity

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.chat_app1204.R
import com.example.chat_app1204.data.utils.LanguageHelper

open class BaseActivity : AppCompatActivity() {
    override fun attachBaseContext(newBase: Context) {
        val lang = LanguageHelper.getLanguage(newBase)
        val context = LanguageHelper.setLocale(newBase, lang)
        super.attachBaseContext(context)
    }
}
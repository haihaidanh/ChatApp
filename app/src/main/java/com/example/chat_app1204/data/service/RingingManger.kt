package com.example.chat_app1204.data.service

import android.content.Context
import android.media.MediaPlayer
import com.example.chat_app1204.R

object RingingManager {

    private var mediaPlayer: MediaPlayer? = null

    fun start(context: Context) {
        if (mediaPlayer != null) return

        mediaPlayer = MediaPlayer.create(context, R.raw.ringtone).apply {
            isLooping = true
            start()
        }
    }

    fun stop() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
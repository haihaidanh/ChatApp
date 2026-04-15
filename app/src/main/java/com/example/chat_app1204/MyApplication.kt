package com.example.chat_app1204

import android.app.Application
import android.content.Intent
import com.example.chat_app1204.data.service.SocketSignalingClient
import com.example.chat_app1204.ui.activity.CallActivity
import dagger.hilt.android.HiltAndroidApp


@HiltAndroidApp
class MyApplication : Application() {

    lateinit var signalingClient: SocketSignalingClient

    override fun onCreate() {
        super.onCreate()
        signalingClient = SocketSignalingClient(
            serverUrl = "http://10.0.2.2:8080",
            roomId = "",
            listener = object : SocketSignalingClient.Listener {
                override fun onConnected() {}

                override fun onOffer(sdp: String) {}
                override fun onAnswer(sdp: String) {}
                override fun onIceCandidate(mid: String?, mLineIndex: Int, candidate: String) {}
                override fun onEndCall() {}

                override fun onIncomingCall(roomId: String) {
                    val intent = CallActivity.newIntent(this@MyApplication, roomId, false)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent) // OK trong Application
                }
            }
        )

        signalingClient.connect()
    }
}
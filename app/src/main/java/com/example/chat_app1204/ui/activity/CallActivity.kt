package com.example.chat_app1204.ui.activity

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.ImageButton
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.chat_app1204.R
import com.example.chat_app1204.data.service.SocketSignalingClient
import com.example.chat_app1204.data.service.WebRtcClient

import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.SessionDescription
import org.webrtc.SurfaceViewRenderer

class CallActivity : AppCompatActivity() {

    companion object {
        private const val EXTRA_ROOM_ID = "extra_room_id"
        private const val EXTRA_IS_CALLER = "extra_is_caller"

        fun newIntent(context: Context, roomId: String, isCaller: Boolean): Intent {
            return Intent(context, CallActivity::class.java).apply {
                putExtra(EXTRA_ROOM_ID, roomId)
                putExtra(EXTRA_IS_CALLER, isCaller)
            }
        }
    }

    private lateinit var localView: SurfaceViewRenderer
    private lateinit var remoteView: SurfaceViewRenderer
    private lateinit var btnMic: ImageButton
    private lateinit var btnCam: ImageButton
    private lateinit var btnEnd: ImageButton

    private val eglBase by lazy { EglBase.create() }
    private lateinit var webRtcClient: WebRtcClient
    private lateinit var signalingClient: SocketSignalingClient

    private var isMicEnabled = true
    private var isCamEnabled = true

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
            val camGranted = result[Manifest.permission.CAMERA] == true
            val micGranted = result[Manifest.permission.RECORD_AUDIO] == true
            if (camGranted && micGranted) {
                initCall()
            } else {
                finish()
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_call)

        localView = findViewById(R.id.localView)
        remoteView = findViewById(R.id.remoteView)
        btnMic = findViewById(R.id.btnMic)
        btnCam = findViewById(R.id.btnCam)
        btnEnd = findViewById(R.id.btnEnd)



        btnMic.setOnClickListener {
            isMicEnabled = !isMicEnabled
            if (::webRtcClient.isInitialized) {
                webRtcClient.setAudioEnabled(isMicEnabled)
            }
            btnMic.alpha = if (isMicEnabled) 1f else 0.45f
        }

        btnCam.setOnClickListener {
            isCamEnabled = !isCamEnabled
            if (::webRtcClient.isInitialized) {
                webRtcClient.setVideoEnabled(isCamEnabled)
            }
            btnCam.alpha = if (isCamEnabled) 1f else 0.45f
        }

        btnEnd.setOnClickListener {
            if (::signalingClient.isInitialized) signalingClient.sendEndCall()
            finish()
        }

        requestPermissionsAndStart()
    }

    private fun requestPermissionsAndStart() {
        val camGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED

        val micGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (camGranted && micGranted) {
            initCall()
        } else {
            permissionLauncher.launch(
                arrayOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            )
        }
    }

    private fun initCall() {
        val roomId = intent.getStringExtra(EXTRA_ROOM_ID).orEmpty()
        val isCaller = intent.getBooleanExtra(EXTRA_IS_CALLER, false)

        webRtcClient = WebRtcClient(
            context = this,
            eglBase = eglBase,
            localRenderer = localView,
            remoteRenderer = remoteView,
            listener = object : WebRtcClient.Listener {
                override fun onLocalOffer(sdp: SessionDescription) {
                    signalingClient.sendOffer(sdp.description)
                }

                override fun onLocalAnswer(sdp: SessionDescription) {
                    signalingClient.sendAnswer(sdp.description)
                }

                override fun onIceCandidate(candidate: IceCandidate) {
                    signalingClient.sendIceCandidate(
                        candidate.sdpMid,
                        candidate.sdpMLineIndex,
                        candidate.sdp
                    )
                }
            }
        )

        signalingClient = SocketSignalingClient(
            // Emulator Android: 10.0.2.2 ; Máy thật: IP máy tính chạy Node.js
            serverUrl = "http://10.0.2.2:8080",
            roomId = roomId,
            listener = object : SocketSignalingClient.Listener {
                override fun onConnected() {
                    signalingClient.joinRoom(
                        userId = "user-${System.currentTimeMillis()}"
                    )
                    if (isCaller) {
                        webRtcClient.createOffer()
                    }
                }

                override fun onOffer(sdp: String) {
                    webRtcClient.setRemoteDescription(
                        SessionDescription(SessionDescription.Type.OFFER, sdp)
                    )
                    webRtcClient.createAnswer()
                }

                override fun onAnswer(sdp: String) {
                    webRtcClient.setRemoteDescription(
                        SessionDescription(SessionDescription.Type.ANSWER, sdp)
                    )
                }

                override fun onIceCandidate(mid: String?, mLineIndex: Int, candidate: String) {
                    webRtcClient.addIceCandidate(IceCandidate(mid, mLineIndex, candidate))
                }

                override fun onEndCall() {
                    finish()
                }

                override fun onIncomingCall(roomId: String) {
                        val intent = newIntent(
                            this@CallActivity,
                            roomId,
                            false
                        )
                    startActivity(intent)
                }
            }
        )

        signalingClient.connect()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (::signalingClient.isInitialized) signalingClient.release()
        if (::webRtcClient.isInitialized) webRtcClient.release()
        localView.release()
        remoteView.release()
        eglBase.release()
    }
}

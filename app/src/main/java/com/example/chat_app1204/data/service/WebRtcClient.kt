package com.example.chat_app1204.data.service


import android.content.Context
import org.webrtc.AudioSource
import org.webrtc.AudioTrack
import org.webrtc.Camera2Enumerator
import org.webrtc.DataChannel
import org.webrtc.DefaultVideoDecoderFactory
import org.webrtc.DefaultVideoEncoderFactory
import org.webrtc.EglBase
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.RtpReceiver
import org.webrtc.RtpTransceiver
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription
import org.webrtc.SurfaceTextureHelper
import org.webrtc.SurfaceViewRenderer
import org.webrtc.VideoCapturer
import org.webrtc.VideoSource
import org.webrtc.VideoTrack

class WebRtcClient(
    private val context: Context,
    private val eglBase: EglBase,
    private val localRenderer: SurfaceViewRenderer,
    private val remoteRenderer: SurfaceViewRenderer,
    private val listener: Listener
) {

    interface Listener {
        fun onLocalOffer(sdp: SessionDescription)
        fun onLocalAnswer(sdp: SessionDescription)
        fun onIceCandidate(candidate: IceCandidate)
    }

    private val factory: PeerConnectionFactory
    private val peerConnection: PeerConnection

    private var videoCapturer: VideoCapturer? = null
    private var surfaceTextureHelper: SurfaceTextureHelper? = null
    private var videoSource: VideoSource? = null
    private var audioSource: AudioSource? = null
    private var localVideoTrack: VideoTrack? = null
    private var localAudioTrack: AudioTrack? = null

    init {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions.builder(context)
                .createInitializationOptions()
        )

        factory = PeerConnectionFactory.builder()
            .setVideoEncoderFactory(DefaultVideoEncoderFactory(eglBase.eglBaseContext, true, true))
            .setVideoDecoderFactory(DefaultVideoDecoderFactory(eglBase.eglBaseContext))
            .createPeerConnectionFactory()

        initRenderers()
        initLocalMedia()

        val iceServers = listOf(
            PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer()
        )

        peerConnection = factory.createPeerConnection(
            iceServers,
            object : PeerConnection.Observer {
                override fun onIceCandidate(candidate: IceCandidate) {
                    listener.onIceCandidate(candidate)
                }

                override fun onTrack(transceiver: RtpTransceiver?) {
                    val track = transceiver?.receiver?.track()
                    if (track is VideoTrack) {
                        track.addSink(remoteRenderer)
                    }
                }

                override fun onSignalingChange(newState: PeerConnection.SignalingState?) {}
                override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {}
                override fun onIceConnectionReceivingChange(receiving: Boolean) {}
                override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {}
                override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate>?) {}
                override fun onAddStream(stream: MediaStream?) {}
                override fun onRemoveStream(stream: MediaStream?) {}
                override fun onDataChannel(dataChannel: DataChannel?) {}
                override fun onRenegotiationNeeded() {}
                override fun onAddTrack(receiver: RtpReceiver?, mediaStreams: Array<out MediaStream>?) {}
            }
        ) ?: throw IllegalStateException("Failed to create PeerConnection")

        localAudioTrack?.let { peerConnection.addTrack(it) }
        localVideoTrack?.let { peerConnection.addTrack(it) }
    }

    private fun initRenderers() {
        localRenderer.init(eglBase.eglBaseContext, null)
        localRenderer.setMirror(true)
        localRenderer.setZOrderMediaOverlay(true)

        remoteRenderer.init(eglBase.eglBaseContext, null)
        remoteRenderer.setMirror(false)
    }

    private fun initLocalMedia() {
        videoCapturer = createCameraCapturer()
        surfaceTextureHelper = SurfaceTextureHelper.create("CaptureThread", eglBase.eglBaseContext)

        videoSource = factory.createVideoSource(false)
        videoCapturer?.initialize(surfaceTextureHelper, context, videoSource?.capturerObserver)
        videoCapturer?.startCapture(720, 1280, 30)

        localVideoTrack = factory.createVideoTrack("localVideoTrack", videoSource)
        localVideoTrack?.addSink(localRenderer)

        audioSource = factory.createAudioSource(MediaConstraints())
        localAudioTrack = factory.createAudioTrack("localAudioTrack", audioSource)
    }

    private fun createCameraCapturer(): VideoCapturer? {
        val enumerator = Camera2Enumerator(context)
        val frontCamera = enumerator.deviceNames.firstOrNull { enumerator.isFrontFacing(it) }
        if (frontCamera != null) return enumerator.createCapturer(frontCamera, null)

        val fallback = enumerator.deviceNames.firstOrNull()
        return if (fallback != null) enumerator.createCapturer(fallback, null) else null
    }

    fun createOffer() {
        peerConnection.createOffer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                desc ?: return
                peerConnection.setLocalDescription(SdpObserverAdapter(), desc)
                listener.onLocalOffer(desc)
            }
        }, MediaConstraints())
    }

    fun createAnswer() {
        peerConnection.createAnswer(object : SdpObserverAdapter() {
            override fun onCreateSuccess(desc: SessionDescription?) {
                desc ?: return
                peerConnection.setLocalDescription(SdpObserverAdapter(), desc)
                listener.onLocalAnswer(desc)
            }
        }, MediaConstraints())
    }

    fun setRemoteDescription(desc: SessionDescription) {
        peerConnection.setRemoteDescription(SdpObserverAdapter(), desc)
    }

    fun addIceCandidate(candidate: IceCandidate) {
        peerConnection.addIceCandidate(candidate)
    }

    fun setAudioEnabled(enabled: Boolean) {
        localAudioTrack?.setEnabled(enabled)
    }

    fun setVideoEnabled(enabled: Boolean) {
        localVideoTrack?.setEnabled(enabled)
    }

    fun release() {
        try {
            videoCapturer?.stopCapture()
        } catch (_: Exception) {
        }

        videoCapturer?.dispose()
        surfaceTextureHelper?.dispose()
        localVideoTrack?.dispose()
        localAudioTrack?.dispose()
        videoSource?.dispose()
        audioSource?.dispose()

        peerConnection.close()
        peerConnection.dispose()
        factory.dispose()
    }
}

open class SdpObserverAdapter : SdpObserver {
    override fun onCreateSuccess(sessionDescription: SessionDescription?) {}
    override fun onSetSuccess() {}
    override fun onCreateFailure(error: String?) {}
    override fun onSetFailure(error: String?) {}
}

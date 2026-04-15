package com.example.chat_app1204.data.service


import io.socket.client.IO
import io.socket.client.Socket
import org.json.JSONObject

class SocketSignalingClient(
    serverUrl: String,
    private val roomId: String,
    private val listener: Listener
) {

    interface Listener {
        fun onConnected()
        fun onOffer(sdp: String)
        fun onAnswer(sdp: String)
        fun onIceCandidate(mid: String?, mLineIndex: Int, candidate: String)
        fun onEndCall()
        fun onIncomingCall(roomId: String)
    }

    private val socket: Socket = IO.socket(serverUrl)

    fun connect() {
        socket.on(Socket.EVENT_CONNECT) {
            listener.onConnected()
        }

        socket.on("offer") { args ->
            val data = args.firstOrNull() as? JSONObject ?: return@on
            listener.onOffer(data.optString("sdp"))
        }

        socket.on("answer") { args ->
            val data = args.firstOrNull() as? JSONObject ?: return@on
            listener.onAnswer(data.optString("sdp"))
        }

        socket.on("ice-candidate") { args ->
            val data = args.firstOrNull() as? JSONObject ?: return@on
            listener.onIceCandidate(
                data.optString("sdpMid"),
                data.optInt("sdpMLineIndex"),
                data.optString("candidate")
            )
        }

        socket.on("incoming-call") { args ->
            val data = args.firstOrNull() as? JSONObject ?: return@on
            listener.onIncomingCall(data.optString("roomId"))
        }

        socket.on("end-call") {
            listener.onEndCall()
        }

        socket.connect()
    }

    fun joinRoom(userId: String) {
        socket.emit(
            "join_room",
            JSONObject()
                .put("roomId", roomId)
                .put("userId", userId)
        )
    }

    fun sendOffer(sdp: String) {
        socket.emit(
            "offer",
            JSONObject()
                .put("roomId", roomId)
                .put("sdp", sdp)
        )
    }

    fun sendCallInvite(toUserId: String, fromUserId: String) {
        socket.emit(
            "call-user",
            JSONObject()
                .put("roomId", roomId)
                .put("toUserId", toUserId)
                .put("fromUserId", fromUserId)
        )
    }

    fun sendAnswer(sdp: String) {
        socket.emit(
            "answer",
            JSONObject()
                .put("roomId", roomId)
                .put("sdp", sdp)
        )
    }

    fun sendIceCandidate(mid: String?, mLineIndex: Int, candidate: String) {
        socket.emit(
            "ice-candidate",
            JSONObject()
                .put("roomId", roomId)
                .put("sdpMid", mid)
                .put("sdpMLineIndex", mLineIndex)
                .put("candidate", candidate)
        )
    }

    fun sendEndCall() {
        socket.emit(
            "end-call",
            JSONObject().put("roomId", roomId)
        )
    }

    fun release() {
        socket.off()
        socket.disconnect()
        socket.close()
    }
}

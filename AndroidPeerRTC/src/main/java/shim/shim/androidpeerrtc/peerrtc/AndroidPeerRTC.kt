package shim.shim.androidpeerrtc.peerrtc

import android.app.Activity
import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import shim.shim.androidpeerrtc.javascriptinterface.MediaConnectionJavascriptInterface
import shim.shim.androidpeerrtc.javascriptinterface.PeerJavascriptInterface
import shim.shim.androidpeerrtc.view.MediaSourceView
import shim.shim.androidpeerrtc.view.MediatorView

class AndroidPeerRTC(
    context: Context,
    val serverURL: String?,
    val configuration: String?,
    private val onReady: (peer: AndroidPeerRTC) -> Unit
) {
    private val mediatorView = MediatorView(context, null)
    private val connectionInterface = MediaConnectionJavascriptInterface(
        context as Activity,
        mediatorView
    )

    var isConnectedToServer = false
    var id: String? = null
    var peerId: String? = null

    var onStart: (() -> Unit)? = null
    var onTextMessage: ((message: String) -> Unit)? = null
    var onSendFileMessage: ((fileBytesArray: ByteArray, fileSizeSent: Int) -> Unit)? = null
    var onFileMessage: ((
        fileName: String,
        fileTotalSize: Int,
        fileBytesArray: ByteArray,
        done: Boolean
    ) -> Unit)? = null
    var onCloseP2P: (() -> Unit)? = null
    var onClose: (() -> Unit)? = null
    var onNewPayload: ((payload: String) -> Unit)? = null
    var onNewPrivatePayload: ((payload: String) -> Unit)? = null
    var onPeerPayloads: ((payloads: JSONArray) -> Unit)? = null
    var onPeerIds: ((ids: JSONArray) -> Unit)? = null
    var onPeerConnectRequest: ((
        peerId: String,
        accept: () -> Unit,
        decline: () -> Unit
    ) -> Unit)? = null
    var onPeerConnectionDecline: ((peerId: String) -> Unit)? = null
    var onPeerConnectSuccess: ((peerId: String) -> Unit)? = null
    var onAdminBroadcastData: ((data: String) -> Unit)? = null
    var onAdminGetAllClientsData: ((clientsData: JSONArray) -> Unit)? = null
    var onAdminActionDecline: (() -> Unit)? = null
    var onServerError: ((errorMessage: String) -> Unit)? = null

    var onServerPing: (() -> Unit)? = null

    init {

        mediatorView.addConnectionInterface(connectionInterface)
        mediatorView.addConnectionInterface(
            PeerJavascriptInterface(
                activity = context as Activity,
                peer = this,
                mediatorView = mediatorView
            )
        )
        mediatorView.loadView {
            val url = if (serverURL == null) "null" else "'$serverURL'"
            val config = if (configuration == null) "null" else "'$configuration'"
            mediatorView.evaluateJavascript("initPeer($url, $config)")
            onReady(this)
        }
    }

    fun setMediaSources(
        ownedMediaSourcesView: MediaSourceView?,
        receivedSourceView: MediaSourceView?
    ) {
        connectionInterface.mediaSourceView = ownedMediaSourcesView
        connectionInterface.mediaReceivedView = receivedSourceView

        ownedMediaSourcesView?.addConnectionInterface(connectionInterface)
        receivedSourceView?.addConnectionInterface(connectionInterface)


        ownedMediaSourcesView?.loadView {
            ownedMediaSourcesView.loadElement()
        }

        receivedSourceView?.loadView(null)

    }


    fun pingServer(millis: Long) {
        mediatorView.evaluateJavascript("pingServer($millis)")
    }

    fun clearServerPinger() {
        mediatorView.evaluateJavascript("clearServerPinger()")
    }

    fun sendText(text: String) {
        mediatorView.evaluateJavascript("sendText('$text')")
    }


    fun sendFile(fileName: String, fileBytesArray: ByteArray, chunkSize: Int = 1024) {
        val unsignedBytes = mutableListOf<Int>()
        for (byte in fileBytesArray){
            unsignedBytes.add(byte.toUByte().toInt())
        }
        val fileBytesArrayString =unsignedBytes.toString()
        mediatorView.evaluateJavascript("sendFile('$fileName', '$fileBytesArrayString', $chunkSize)")

    }

    fun addPayload(jsonObject: JSONObject) {
        addPayload(jsonString = jsonObject.toString())
    }

    fun addPrivatePayload(jsonObject: JSONObject) {
        addPrivatePayload(jsonString = jsonObject.toString())
    }

    fun addPayload(jsonString: String) {
        mediatorView.evaluateJavascript("addPayload('$jsonString')")
    }

    fun addPrivatePayload(jsonString: String) {
        mediatorView.evaluateJavascript("addPrivatePayload('$jsonString')")
    }


    fun getAllPeerPayloads() {
        mediatorView.evaluateJavascript("getAllPeerPayloads()")
    }


    fun getPeerPayload(peerId: String) {
        mediatorView.evaluateJavascript("getPeerPayload('$peerId')")
    }

    fun closeP2P(){
        mediatorView.evaluateJavascript("closeP2P()")
    }

    fun close(){
        mediatorView.evaluateJavascript("close()")
    }


    fun getAllPeerIds(){
        mediatorView.evaluateJavascript("getAllPeerIds()")
    }


    fun connect(peerId: String) {
        mediatorView.evaluateJavascript("peer.connect('$peerId')")
    }

    fun start(isSecure: Boolean) {
        mediatorView.evaluateJavascript("start($isSecure)")
    }

    fun adminBroadcastData(key:String, data:String){
        mediatorView.evaluateJavascript("adminBroadcastData('$key', '$data')")
    }

    fun adminGetAllClientsData(key: String){
        mediatorView.evaluateJavascript("adminGetAllClientsData('$key')")
    }

}
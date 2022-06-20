package shim.shim.androidpeerrtc.peerrtc

import android.app.Activity
import android.content.Context
import shim.shim.androidpeerrtc.javascriptinterface.MediaConnectionJavascriptInterface
import shim.shim.androidpeerrtc.javascriptinterface.PeerJavascriptInterface
import shim.shim.androidpeerrtc.view.MediaSourceView
import shim.shim.androidpeerrtc.view.MediatorView

class AndroidPeerRTC(
    context: Context,
    private val serverURL: String?,
    private val configuration: String?,
    private val onReady: (peer: AndroidPeerRTC) -> Unit
) {
    private val mediatorView = MediatorView(context, null)
    private val connectionInterface = MediaConnectionJavascriptInterface(
        context as Activity,
        mediatorView
    )

    var onStart: (() -> Unit)? = null
    var onCloseP2P: (() -> Unit)? = null


    init {

        mediatorView.addConnectionInterface(connectionInterface)
        mediatorView.addConnectionInterface(PeerJavascriptInterface(context as Activity, this))
        mediatorView.loadView {
            val url = if (serverURL == null) "null" else "'$serverURL'"
            val config = if (configuration == null) "null" else "'$configuration'"
            mediatorView.evaluateJavascript("initPeer($url, $config)")
            onReady(this)
        }
    }

    fun setMediaSourcesView(
        clientSourceView: MediaSourceView?,
        receivedSourceView: MediaSourceView?
    ) { 
        connectionInterface.mediaSourceView = clientSourceView
        connectionInterface.mediaReceivedView = receivedSourceView

        clientSourceView?.addConnectionInterface(connectionInterface)
        receivedSourceView?.addConnectionInterface(connectionInterface)


        clientSourceView?.loadView {
            clientSourceView.loadElement()
        }

        receivedSourceView?.loadView(null)

    }

    fun connect(peerId: String) {
        mediatorView.evaluateJavascript("peer.connect('$peerId')")
    }


    fun start(isSecure: Boolean) {
        mediatorView.evaluateJavascript("start($isSecure)")
    }
}
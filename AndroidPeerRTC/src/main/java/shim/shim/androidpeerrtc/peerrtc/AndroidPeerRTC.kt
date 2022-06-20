package shim.shim.androidpeerrtc.peerrtc

import android.app.Activity
import android.content.Context
import shim.shim.androidpeerrtc.javascriptinterface.MediaConnectionJavascriptInterface
import shim.shim.androidpeerrtc.javascriptinterface.PeerJavascriptInterface
import shim.shim.androidpeerrtc.view.MediaSourceView
import shim.shim.androidpeerrtc.view.MediatorView

class AndroidPeerRTC(
    private val context: Context,
    private val onReady: (peer: AndroidPeerRTC) -> Unit
) {
    private val mediatorView = MediatorView(context, null)

    var onCloseP2P: (() -> Unit)? = null

    init {
        mediatorView.loadView {
            mediatorView.addConnectionInterface(PeerJavascriptInterface(context as Activity, this))
            onReady(this)
        }
    }

    fun setMediaSourcesView(
        clientSourceView: MediaSourceView?,
        receivedSourceView: MediaSourceView?
    ) {
        val connectionInterface = MediaConnectionJavascriptInterface(
            context as Activity,
            mediatorView,
            clientSourceView,
            receivedSourceView
        )
        clientSourceView?.addConnectionInterface(connectionInterface)
        receivedSourceView?.addConnectionInterface(connectionInterface)
        mediatorView.addConnectionInterface(connectionInterface)

        clientSourceView?.loadView {
            clientSourceView.loadElement()
        }

        receivedSourceView?.loadView(null)


    }

    fun connect(peerId: String) {
        mediatorView.evaluateJavascript("peer.connect('$peerId')")
    }
}
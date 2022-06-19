package shim.shim.androidpeerrtc.peerrtc

import android.app.Activity
import android.content.Context
import shim.shim.androidpeerrtc.javascriptinterface.MediaConnectionJavascriptInterface
import shim.shim.androidpeerrtc.view.MediaSourceView
import shim.shim.androidpeerrtc.view.MediatorView

class AndroidPeerRTC(
    private val context: Context,
    private val onReady: (peer: AndroidPeerRTC) -> Unit
) {
    private val mediatorView = MediatorView(context, null)

    init {
        mediatorView.loadView {
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
}
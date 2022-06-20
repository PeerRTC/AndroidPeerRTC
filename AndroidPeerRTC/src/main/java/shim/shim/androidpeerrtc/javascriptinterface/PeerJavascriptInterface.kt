package shim.shim.androidpeerrtc.javascriptinterface

import android.app.Activity
import android.content.Context
import android.webkit.JavascriptInterface
import shim.shim.androidpeerrtc.peerrtc.AndroidPeerRTC

class PeerJavascriptInterface(private val activity: Activity, private val peer: AndroidPeerRTC) : AndroidPeerInterface {
    override val name: String = "AndroidPeer"


    @JavascriptInterface
    fun onCloseP2P() {
        activity.runOnUiThread {
            peer.onCloseP2P?.invoke()
        }

    }
}
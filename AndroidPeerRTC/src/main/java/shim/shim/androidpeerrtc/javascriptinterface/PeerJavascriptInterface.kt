package shim.shim.androidpeerrtc.javascriptinterface

import android.util.Log
import android.webkit.JavascriptInterface

class PeerJavascriptInterface : AndroidPeerInterface{
    override val name: String = "AndroidPeer"

    @JavascriptInterface
    fun test(){
        Log.e("ee", "Just test")
    }
}
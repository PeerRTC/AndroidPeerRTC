package shim.shim.androidpeerrtc.javascriptinterface

import android.util.Log
import android.webkit.JavascriptInterface

class PeerJavascriptInterface {
    companion object{
        const val NAME = "AndroidPeer"
    }

    @JavascriptInterface
    fun test(){
        Log.e("ee", "Just test")
    }
}
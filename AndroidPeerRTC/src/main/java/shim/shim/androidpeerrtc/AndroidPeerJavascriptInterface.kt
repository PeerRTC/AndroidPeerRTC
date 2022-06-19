package shim.shim.androidpeerrtc

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView

class AndroidPeerJavascriptInterface(
    private val activity: Activity,
    private val mediatorView: WebView,
    private val mediaSourceView:WebView
) {
    @JavascriptInterface
    fun onMediaStreamSourceSDP(sdp: String) {
        activity.runOnUiThread {
            mediatorView.evaluateJavascript("sourceConn.createAnswer($sdp)", null)
        }

    }

    @JavascriptInterface
    fun onMediatorStreamSourceAnswerSDP(sdp: String) {
        activity.runOnUiThread {
            mediaSourceView.evaluateJavascript("saveAnswer($sdp)", null)
        }

    }
}
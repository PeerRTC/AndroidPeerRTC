package shim.shim.androidpeerrtc.javascriptinterface

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView

/**
 * Javascript interface for connecting media sources like
 * media streams from client and incoming media streams from other peers.
 */
class MediaConnectionJavascriptInterface(
    private val activity: Activity,
    private val mediatorView: WebView,
    private val mediaSourceView: WebView,
    private val mediaReceivedView: WebView
) {
    companion object {
        const val NAME = "AndroidMediaConnection"
    }

    @JavascriptInterface
    fun onMediaStreamSourceSDP(sdp: String) {
        activity.runOnUiThread {
            mediatorView.evaluateJavascript("sourceConnCreateAnswer($sdp)", null)
        }

    }

    @JavascriptInterface
    fun onMediatorStreamSourceAnswerSDP(sdp: String) {
        activity.runOnUiThread {
            mediaSourceView.evaluateJavascript("saveAnswer($sdp)", null)
        }

    }


    @JavascriptInterface
    fun initReceiveMediaStreamSource() {
        activity.runOnUiThread {
            mediaReceivedView.evaluateJavascript("receiveStream(2);", null)
        }
    }


    @JavascriptInterface
    fun connectMediaStreamReceivedToMediator() {
        activity.runOnUiThread {
            mediatorView.evaluateJavascript("receivedConn.createOffer()", null)
        }
    }


    @JavascriptInterface
    fun onMediatorOfferSDP(sdp: String) {
        activity.runOnUiThread {
            mediaReceivedView.evaluateJavascript("createAnswer($sdp)", null)
        }
    }

    @JavascriptInterface
    fun onMediaStreamReceivedAnswerSDP(sdp: String) {
        activity.runOnUiThread {
            mediatorView.evaluateJavascript("receivedConn.saveAnswer($sdp)", null)
        }
    }

    @JavascriptInterface
    fun onMediaStreamSourceInitialized() {
        Log.e("ee", "Media Stream source is running")
    }

    @JavascriptInterface
    fun onMediaStreamReceivedInitialized() {
        Log.e("ee", "Media Stream received is running")
    }

    @JavascriptInterface
    fun onMediaStreamSourceClosed() {
        Log.e("aa", "Media source closed")
    }


    @JavascriptInterface
    fun onMediaStreamReceivedClosed() {
        Log.e("aa", "Media received closed")
    }
}
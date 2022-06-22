package shim.shim.androidpeerrtc.javascriptinterface

import android.app.Activity
import android.util.Log
import android.webkit.JavascriptInterface
import shim.shim.androidpeerrtc.view.MediaSourceView
import shim.shim.androidpeerrtc.view.MediatorView

/**
 * Javascript interface for connecting media sources like
 * media streams from client and incoming media streams from other peers.
 */
class MediaConnectionJavascriptInterface(
    private val activity: Activity,
    private val mediatorView: MediatorView,
) : AndroidPeerInterface {
    var mediaSourceView: MediaSourceView? = null
    var mediaReceivedView: MediaSourceView? = null

    override val name: String = "AndroidMediaConnection"

    @JavascriptInterface
    fun onMediaStreamSourceSDP(sdp: String) {
        activity.runOnUiThread {
            mediatorView.evaluateJavascript("sourceConnCreateAnswer($sdp)")
        }

    }

    @JavascriptInterface
    fun onMediatorStreamSourceAnswerSDP(sdp: String) {
        activity.runOnUiThread {
            mediaSourceView?.evaluateJavascript("saveAnswer($sdp)")
        }

    }


    @JavascriptInterface
    fun initReceiveMediaStreamSource() {
        activity.runOnUiThread {
            mediaReceivedView?.evaluateJavascript("receiveStream(2);")
        }
    }


    @JavascriptInterface
    fun connectMediaStreamReceivedToMediator() {
        activity.runOnUiThread {
            mediatorView.evaluateJavascript("receivedConn.createOffer()")
        }
    }


    @JavascriptInterface
    fun onMediatorOfferSDP(sdp: String) {
        activity.runOnUiThread {
            mediaReceivedView?.evaluateJavascript("createAnswer($sdp)")
        }
    }

    @JavascriptInterface
    fun onMediaStreamReceivedAnswerSDP(sdp: String) {
        activity.runOnUiThread {
            mediatorView.evaluateJavascript("receivedConn.saveAnswer($sdp)")
        }
    }

    @JavascriptInterface
    fun onMediaStreamSourceInitialized() {
        // when media stream source successfully connected to mediator
        Log.e("ee", "Media Stream source is running")
    }

    @JavascriptInterface
    fun onMediaStreamReceivedInitialized() {
        // when media stream received successfully connected to mediator

    }

    @JavascriptInterface
    fun onMediaStreamSourceClosed() {
        activity.runOnUiThread {
            mediaSourceView?.onMediaNotAvailable?.invoke()
        }
    }


    @JavascriptInterface
    fun onMediaStreamReceivedClosed() {
        activity.runOnUiThread {
            mediaReceivedView?.onMediaNotAvailable?.invoke()
        }
    }


    @JavascriptInterface
    fun onMediaStreamSourceMediaAvailable() {
        activity.runOnUiThread {
            mediaSourceView?.onMediaAvailable?.invoke()
        }

    }

    @JavascriptInterface
    fun onMediaStreamReceivedMediaAvailable() {
        activity.runOnUiThread {
            mediaReceivedView?.onMediaAvailable?.invoke()
        }
    }

}
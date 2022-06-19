package shim.shim.androidpeerrtc.view

import android.content.Context
import android.util.AttributeSet
import shim.shim.androidpeerrtc.javascriptinterface.MediaConnectionJavascriptInterface

class MediaSourceView(context: Context, attr: AttributeSet?) : AndroidPeerInterfaceView(context,attr) {
    companion object {
        private const val HTML_SOURCE = "file:///android_asset/mediator/mediastream-source.html"

        const val TYPE_AUDIO = 1
        const val TYPE_VIDEO = 2
        const val TYPE_AUDIO_VIDEO = 3

        const val FRONT_CAM = 0
        const val BACK_CAM = 1
    }

    private var mediaConnectionInterfaceAdded =false


    override val htmlUrl: String = "file:///android_asset/mediator/mediastream-source.html"
    override val TAG: String = "MediaSourceView"

    init {
        webView.loadUrl(htmlUrl)
    }




    fun loadElement(type: Int, whichCam: Int) {
        if (!mediaConnectionInterfaceAdded) throw (Exception("Call addConnectionInterface method first"))
        webView.evaluateJavascript("startStream($type, $whichCam)", null)
    }

    fun addConnectionInterface(connectionInterface: MediaConnectionJavascriptInterface) {
        mediaConnectionInterfaceAdded = true
        webView.addJavascriptInterface(connectionInterface, connectionInterface.name)
    }




}
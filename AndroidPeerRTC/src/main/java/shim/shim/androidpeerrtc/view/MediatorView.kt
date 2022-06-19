package shim.shim.androidpeerrtc.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import shim.shim.androidpeerrtc.javascriptinterface.AndroidPeerInterface

class MediatorView(context: Context, attr: AttributeSet?) : AndroidPeerInterfaceView(context, attr) {
    override val htmlUrl: String = "file:///android_asset/mediator/mediator.html"
    override val TAG: String = "MediatorView"

    @SuppressLint("JavascriptInterface")
    fun addConnectionInterface(connectionInterface: AndroidPeerInterface) {
        webView.addJavascriptInterface(connectionInterface, connectionInterface.name)
    }

    init {
        webView.loadUrl(htmlUrl)
    }
}
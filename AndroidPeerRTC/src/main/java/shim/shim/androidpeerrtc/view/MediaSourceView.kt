package shim.shim.androidpeerrtc.view

import android.content.Context
import android.util.AttributeSet
import shim.shim.androidpeerrtc.R
import shim.shim.androidpeerrtc.javascriptinterface.MediaConnectionJavascriptInterface

class MediaSourceView(context: Context, attr: AttributeSet?) :
    AndroidPeerInterfaceView(context, attr) {
    companion object{
        const val TYPE_AUDIO = 1
        const val  TYPE_VIDEO = 2
        const val  TYPE_AUDIO_VIDEO =3

        const val FRONT_CAM = 0
        const val BACK_CAM = 1
    }
    override val htmlUrl: String = "file:///android_asset/mediator/mediastream-source.html"
    override val TAG: String = "MediaSourceView"

    var mediaType = 0
    var cameraType = 0


    init {
        val array = context.theme.obtainStyledAttributes(attr, R.styleable.MediaSourceView, 0, 0)
        mediaType = array.getInt(R.styleable.MediaSourceView_mediaType, 1)
        cameraType = array.getInt(R.styleable.MediaSourceView_cameraType, 0)


    }


    fun loadElement() {
        webView.evaluateJavascript("startStream($mediaType, $cameraType)", null)
    }

    fun addConnectionInterface(connectionInterface: MediaConnectionJavascriptInterface) {
        webView.addJavascriptInterface(connectionInterface, connectionInterface.name)
    }


}
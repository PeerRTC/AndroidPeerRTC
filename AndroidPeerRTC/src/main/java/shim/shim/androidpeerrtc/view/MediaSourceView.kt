package shim.shim.androidpeerrtc.view

import android.content.Context
import android.util.AttributeSet
import shim.shim.androidpeerrtc.R
import shim.shim.androidpeerrtc.javascriptinterface.MediaConnectionJavascriptInterface

/**
 * @property audioConstraints
 *
 * Json string of settings to be used to configure the audio. Device Id constraint should not by included
 * here since it is ignored. The constraints provided are passed down to
 * the navigator.mediaDevices.getUserMedia method in the internal WebView. It should also be remembered that
 * some constraints will not be available depending on the supported constraints returned by
 * navigator.mediaDevices.getSupportedConstraints method. For more detailed documentation
 * of the possible audio constraints, refer to the Audio MediaTrackConstraints Documentation.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/MediaTrackConstraints#properties_of_audio_tracks">Audio MediaTrackConstraints Documentation</a>
 *
 *
 * @property videoConstraints
 *
 * Json string of settings to be used to configure the video. Device Id constraint should not by included
 * here since it is ignored. The constraints provided are passed down to
 * the navigator.mediaDevices.getUserMedia method in the internal WebView. It should also be remembered that
 * some constraints will not be available depending on the supported constraints returned by
 * navigator.mediaDevices.getSupportedConstraints method. For more detailed documentation
 * of the possible video constraints, refer to the Video MediaTrackConstraints Documentation.
 *
 * @see <a href="https://developer.mozilla.org/en-US/docs/Web/API/MediaTrackConstraints#properties_of_video_tracks">Video MediaTrackConstraints documentation </a>
 */

class MediaSourceView(context: Context, attr: AttributeSet?) :
    AndroidPeerInterfaceView(context, attr) {
    companion object {
        const val TYPE_AUDIO = 1
        const val TYPE_VIDEO = 2
        const val TYPE_AUDIO_VIDEO = 3

        const val FRONT_CAM = 0
        const val BACK_CAM = 1
    }

    override val htmlUrl: String = "file:///android_asset/mediator/mediastream-source.html"
    override val TAG: String = "MediaSourceView"

    var mediaType = 0
    var cameraType = 0
    var mute = false
        set(value) {
            field = value
            webView.evaluateJavascript("muteAudio($value)", null)
        }

    var enableVideo = true
        set(value) {
            field = value
            webView.evaluateJavascript("enableVideo($value)", null)
        }

    var audioConstraints: String? = null
    var videoConstraints: String? = null

    var onMediaAvailable: (() -> Unit)? = null


    init {
        val array = context.theme.obtainStyledAttributes(attr, R.styleable.MediaSourceView, 0, 0)
        mediaType = array.getInt(R.styleable.MediaSourceView_mediaType, TYPE_AUDIO)
        cameraType = array.getInt(R.styleable.MediaSourceView_cameraType, FRONT_CAM)


    }


    fun loadElement() {
        webView.evaluateJavascript(
            "startStream($mediaType, $cameraType, $audioConstraints, $videoConstraints)",
            null
        )
    }

    fun addConnectionInterface(connectionInterface: MediaConnectionJavascriptInterface) {
        webView.addJavascriptInterface(connectionInterface, connectionInterface.name)
    }


}
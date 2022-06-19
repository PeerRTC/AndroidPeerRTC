package shim.shim.androidpeerrtc.view

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.LinearLayout

abstract class AndroidPeerInterfaceView(context: Context, attr: AttributeSet?) : LinearLayout(context, attr) {
    protected abstract val htmlUrl: String
    protected abstract val TAG: String

    protected val webView = WebView(context)
    var isLoaded = false


    init {
        webView.also {
            it.layoutParams = LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
            )

            it.settings.javaScriptEnabled = true
            it.settings.mediaPlaybackRequiresUserGesture = false
            it.webChromeClient = object : WebChromeClient() {
                override fun onConsoleMessage(
                    message: String?,
                    lineNumber: Int,
                    sourceID: String?
                ) {
                    Log.e(TAG, message.toString())

                }

                override fun onPermissionRequest(request: PermissionRequest?) {
                    request?.grant(request.resources)
                }


            }

            it.webViewClient = object : WebViewClient() {
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    isLoaded = true


                }
            }


        }

        this.addView(webView)


    }

    fun evaluateJavascript(script: String) {
        webView.evaluateJavascript(script, null)
    }
}
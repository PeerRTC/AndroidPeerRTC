package shim.shim.androidpeerrtc.view

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.webkit.*
import android.widget.LinearLayout
import shim.shim.androidpeerrtc.javascriptinterface.OnFinishLoadingJavascriptInterface

@SuppressLint("SetJavaScriptEnabled")
abstract class AndroidPeerInterfaceView(context: Context, attr: AttributeSet?) :
    LinearLayout(context, attr) {
    protected abstract val htmlUrl: String
    protected abstract val TAG: String

    protected val webView = WebView(context)
    private var onPageLoaded: (() -> Unit)? = null

    init {
//        val onFinishLoadingInterface = object : OnFinishLoadingJavascriptInterface() {
//            @JavascriptInterface
//            override fun onFinishLoading() {
//                (context as Activity).runOnUiThread {
//
//                }
//
//            }
//
//        }

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

            it.webViewClient = object :WebViewClient(){
                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onPageLoaded?.invoke()
                }
            }





        }

        this.addView(webView)

    }

    fun loadView(onLoad: (() -> Unit)?) {
        this.onPageLoaded = onLoad
        webView.loadUrl(htmlUrl)
    }

    fun evaluateJavascript(script: String) {
        webView.evaluateJavascript(script, null)
    }
}
package shim.shim.androidpeerrtc.javascriptinterface

import android.webkit.JavascriptInterface

abstract class OnFinishLoadingJavascriptInterface : AndroidPeerInterface {
    override val name: String = "AndroidOnFinishLoading"

    @JavascriptInterface
    abstract fun onFinishLoading()
}
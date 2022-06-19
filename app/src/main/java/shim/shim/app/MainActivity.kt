package shim.shim.app

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.webkit.PermissionRequest
import android.webkit.WebChromeClient
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import shim.shim.androidpeerrtc.AndroidPeerJavascriptInterface
import shim.shim.app.databinding.ActivityMainBinding
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionReq = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!it.map { a -> a.value }.contains(false)) {
                run()
            } else {
                Toast.makeText(this@MainActivity, "Declined", Toast.LENGTH_SHORT).show()
            }
        }

        if (ContextCompat.checkSelfPermission(this@MainActivity, android.Manifest.permission.RECORD_AUDIO) ==
            PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.CAMERA
            ) ==
            PackageManager.PERMISSION_GRANTED
        ) {
            run()
        } else {
            permissionReq.launch(
                arrayOf(
                    android.Manifest.permission.RECORD_AUDIO,
                    android.Manifest.permission.CAMERA
                )
            )
        }



    }

    private fun run() {
        val mediatorView = binding.mediatorView
        val mediaSourceView = binding.mediaSourceView
        val mediatorInterface = AndroidPeerJavascriptInterface(this,mediatorView, mediaSourceView)

        mediatorView.settings.javaScriptEnabled = true
        mediatorView.webChromeClient = object:WebChromeClient(){
            override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
                Log.e("eee",message.toString())

            }

        }
        mediatorView.addJavascriptInterface(mediatorInterface, "Android")
        mediatorView.loadUrl("file:///android_asset/mediator/mediator.html")



        mediaSourceView.settings.javaScriptEnabled = true
        mediaSourceView.settings.mediaPlaybackRequiresUserGesture = false
        mediaSourceView.webChromeClient = object:WebChromeClient(){
            override fun onConsoleMessage(message: String?, lineNumber: Int, sourceID: String?) {
                Log.e("mediaSourceView",message.toString())

            }

            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
                Log.e("Ee","Ee")
            }
        }
        mediaSourceView.addJavascriptInterface(mediatorInterface, "Android")
        mediaSourceView.loadUrl("file:///android_asset/mediator/mediastream-source.html")

        Executors.newSingleThreadExecutor().submit {
            TimeUnit.SECONDS.sleep(2)
            runOnUiThread {
                mediaSourceView.evaluateJavascript("startStream(2, 1);", null)
            }

            TimeUnit.SECONDS.sleep(5)
            runOnUiThread {
                mediaSourceView.evaluateJavascript("createOffer()", null)
            }
        }


    }


}
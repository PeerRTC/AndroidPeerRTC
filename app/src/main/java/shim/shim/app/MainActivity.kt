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
import shim.shim.androidpeerrtc.javascriptinterface.MediaConnectionJavascriptInterface
import shim.shim.androidpeerrtc.javascriptinterface.PeerJavascriptInterface
import shim.shim.androidpeerrtc.view.MediaSourceView
import shim.shim.androidpeerrtc.view.MediatorView
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
        val mediatorView = MediatorView(this,null)
        val mediaSourceView = binding.mediaSourceView
        val mediaReceivedView = binding.mediaReceivedView
        val mediatorInterface = MediaConnectionJavascriptInterface(this,mediatorView, mediaSourceView, mediaReceivedView)


        mediatorView.addConnectionInterface(mediatorInterface)
        mediatorView.addConnectionInterface(PeerJavascriptInterface())


        mediaSourceView.addConnectionInterface(mediatorInterface)
        mediaReceivedView.addConnectionInterface(mediatorInterface)

        Executors.newSingleThreadExecutor().submit {
            TimeUnit.SECONDS.sleep(2)
            runOnUiThread {
                mediaSourceView.loadElement(MediaSourceView.TYPE_VIDEO, MediaSourceView.BACK_CAM)
            }

            TimeUnit.SECONDS.sleep(5)
            runOnUiThread {
                mediaSourceView.evaluateJavascript("createOffer()")
            }
        }




    }


}
package shim.shim.app

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import shim.shim.androidpeerrtc.peerrtc.AndroidPeerRTC
import shim.shim.app.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {


    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val permissionReq =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
                if (!it.map { a -> a.value }.contains(false)) {
                    run()
                } else {
                    Toast.makeText(this@MainActivity, "Declined", Toast.LENGTH_SHORT).show()
                }
            }

        if (ContextCompat.checkSelfPermission(
                this@MainActivity,
                android.Manifest.permission.RECORD_AUDIO
            ) ==
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
        val peer = AndroidPeerRTC(this) {
            val mediaSourceView = binding.mediaSourceView
            val mediaReceivedView = binding.mediaReceivedView

            mediaSourceView.onMediaAvailable = {
                it.start(true)
            }

            mediaReceivedView.onMediaAvailable = {
//                Log.e("Ee","available")
//                mediaSourceView.enableVideo = false
            }

            it.setMediaSourcesView(mediaSourceView, mediaReceivedView)


        }

        peer.onCloseP2P = {
            peer.connect("8461cdca-34e9-4708-9b12-dc7d515cdd7a")
        }

        peer.onStart = {
            peer.connect("8461cdca-34e9-4708-9b12-dc7d515cdd7a")
        }

    }


}
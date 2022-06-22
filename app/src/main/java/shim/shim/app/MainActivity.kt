package shim.shim.app

import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import shim.shim.androidpeerrtc.peerrtc.AndroidPeerRTC
import shim.shim.app.databinding.ActivityMainBinding
import java.util.concurrent.TimeUnit


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
        val peer = AndroidPeerRTC(context = this, serverURL = null, configuration = null) {
            TimeUnit.SECONDS.sleep(2)
            val mediaSourceView = binding.mediaSourceView
            val mediaReceivedView = binding.mediaReceivedView

            mediaSourceView.onMediaAvailable = {
                it.start(true)
            }
            mediaSourceView.onMediaNotAvailable = {
                Log.e("aa", "Media source closed")
            }

            mediaReceivedView.onMediaAvailable = {
//                Log.e("Ee","available")
//                mediaSourceView.enableVideo = false
            }

            mediaReceivedView.onMediaNotAvailable = {
                Log.e("aa", "Media received closed")
            }
            it.setMediaSourcesView(mediaSourceView, mediaReceivedView)
        }

        var byteArray:ByteArray? = null
        var offset = 0

        peer.onFileMessage = { fileName: String,
                               fileTotalSize: Int,
                               fileBytesArray: ByteArray,
                               done: Boolean ->
            if (byteArray == null){
                byteArray = ByteArray(fileTotalSize)
            }

            for (byte:Byte in fileBytesArray){
                byteArray?.set(offset,byte)
                offset++
            }

            if (done){
                peer.sendFile(fileName=fileName, byteArray!!)
                val bitmap = BitmapFactory.decodeByteArray(byteArray!!,0, byteArray!!.size)
                binding.testImageView.setImageBitmap(bitmap)
            }

        }
        peer.onPeerConnectSuccess = {
            Log.e("connected", it)
        }
        peer.onCloseP2P = {
            peer.connect("74e330f2-37de-482c-92cc-f726e7c09fb5")
        }

        peer.onStart = {
            peer.pingServer(10000)

            peer.adminBroadcastData("","aaaaaaaaa")
            peer.connect("74e330f2-37de-482c-92cc-f726e7c09fb5")
        }

        peer.onServerPing = {

        }

        peer.onAdminActionDecline = {
            Log.e("ee","declined")
        }
        peer.onTextMessage = {
            peer.sendText(it)
            Log.e("new message", it)
        }

    }


}
package shim.shim.app

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import org.json.JSONArray
import shim.shim.androidpeerrtc.peerrtc.AndroidPeerRTC
import shim.shim.app.databinding.ActivityMainBinding
import java.io.File
import java.io.FileOutputStream


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "MainActivity"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var peer: AndroidPeerRTC

    private val incomingFiles = mutableMapOf<String, MutableList<Byte>>()
    private var fileAlreadySentSize = 0

    private val newFileResult =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                sendFile(uri = it.data!!.data!!)
            }
        }

    private val permissionReq =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) {
            if (!it.map { a -> a.value }.contains(false)) {
                startPeerRtcConnection()
            } else {
                showMessage(message = "Please enable audio and camera permissions")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)


        val isPermissionGranted = { permission: String ->
            ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
        }

        if (isPermissionGranted(RECORD_AUDIO) && isPermissionGranted(CAMERA)) {
            startPeerRtcConnection()
            initButtons()
        } else {
            permissionReq.launch(arrayOf(RECORD_AUDIO, CAMERA))
        }


    }

    private fun startPeerRtcConnection() {
        peer = AndroidPeerRTC(context = this, serverURL = null, configuration = null) {
            initPeerListeners()
            initMediaSources()
        }

    }


    private fun initMediaSources() {
        val ownVideoView = binding.ownVideoView
        val receivedVideoView = binding.receivedVideoView

        ownVideoView.onMediaAvailable = {
            peer.start(isSecure = true)
        }
        ownVideoView.onMediaNotAvailable = {
            showMessage(message = "You had stop streaming video")
        }

        receivedVideoView.onMediaAvailable = {
            showMessage(message = "New incoming video")
        }

        receivedVideoView.onMediaNotAvailable = {
            showMessage(message = "Other peer had stop streaming video")
        }

        peer.setMediaSources(
            ownedMediaSourcesView = ownVideoView,
            receivedSourceView = receivedVideoView
        )
    }


    private fun initPeerListeners() {


        peer.onFileMessage = { fileName: String,
                               fileTotalSize: Int,
                               fileBytesArray: ByteArray,
                               done: Boolean ->
            var fileBytes = incomingFiles[fileName]

            if (fileBytes == null) {
                fileBytes = mutableListOf()
                incomingFiles[fileName] = fileBytes
            }

            fileBytes.addAll(fileBytesArray.toList())

            if (done) {
                /**
                 * Preview file. If file is not image, then no previews.
                 */
                val bytesArray = fileBytes.toByteArray()
                incomingFiles.remove(fileName)
                val bitmap = BitmapFactory.decodeByteArray(bytesArray, 0, bytesArray.size)
                binding.filePreviewBitmap.setImageBitmap(bitmap)

                /**
                 * Show the download file option
                 */
                binding.fileNamePreview.text = fileName
                val downloadFileButton = binding.downloadFileButton
                downloadFileButton.visibility = View.VISIBLE
                downloadFileButton.setOnClickListener {
                    val fileExt = try {
                        fileName.split(".")[1]
                    } catch (e: IndexOutOfBoundsException) {
                        ""
                    }

                    /**
                     * getExternalStoragePublicDirectory is already deprecated but
                     * only using it for demo purposes
                     */
                    val file = File(
                        Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                        System.currentTimeMillis().toString() + ".$fileExt"
                    )
                    val fos = FileOutputStream(file)
                    fos.write(bytesArray)
                    fos.flush()
                    fos.close()
                    showMessage(message = "Save to downloads")
                }


            }

        }


        peer.onStart = {
            peer.pingServer(10000)
            binding.idDisplayView.text = "My id: ${peer.id}"
        }


        peer.onTextMessage = { message: String ->
            addMessageToMessageBox(isSender = false, message = message)
        }



        peer.onSendFileMessage = { fileBytesArray: ByteArray, fileSizeSent: Int ->
            fileAlreadySentSize += fileSizeSent
            val fileSize = fileBytesArray.size
            binding.downloadingBytesView.text = if (fileSize > fileAlreadySentSize) {
                "$fileAlreadySentSize/$fileSize bytes sent"
            } else null
        }

        peer.onCloseP2P = {
            binding.connectedPeerIdView.text = null
        }

        peer.onClose = {
            showMessage("Connection to server closed")
        }

        peer.onNewPayload = { payload: String ->
            Log.i(TAG, payload)
        }

        peer.onNewPrivatePayload = { payload: String ->
            Log.i(TAG, payload)
        }

        peer.onPeerPayloads = { payloads: JSONArray ->
            Log.i(TAG, payloads.toString())
        }

        peer.onPeerIds = { ids: JSONArray ->
            Log.i(TAG, ids.toString())
        }

        peer.onPeerConnectRequest = { peerId: String, accept: () -> Unit, decline: () -> Unit ->
            val requestView = binding.incomingConnectRequestView
            requestView.visibility = View.VISIBLE

            binding.incomingRequestMessageView.text = "$peerId is requesting to connect"

            val acceptRequest = { isAccept: Boolean ->
                if (isAccept) accept() else decline()
                requestView.visibility = View.GONE
            }

            binding.acceptRequestButton.setOnClickListener { acceptRequest(true) }
            binding.declineRequestButton.setOnClickListener { acceptRequest(false) }
        }

        peer.onPeerConnectionDecline = { peerId: String ->
            showMessage("Connection request to $peerId declined")
        }

        peer.onPeerConnectSuccess = { peerId: String ->
            binding.connectedPeerIdView.text = "Connected to: $peerId"
        }

        peer.onAdminBroadcastData = { data: String ->
            Log.i(TAG, data)
        }

        peer.onAdminGetAllClientsData = { clientsData: JSONArray ->
            Log.i(TAG, clientsData.toString())
        }

        peer.onAdminActionDecline = {
            Log.i(TAG, "The action is for admin only")
        }

        peer.onServerError = { errorMessage: String ->
            showMessage("Server error occured: $errorMessage")
        }

        peer.onServerPing = {
            Log.i(TAG, "Ping sent to PeerRTC server")
        }

    }

    private fun showMessage(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun addMessageToMessageBox(isSender: Boolean, message: String) {
        val messageBox = binding.messageBox
        val existingText = messageBox.text.toString()
        val owner = if (isSender) "Me" else "Other"
        messageBox.text = "$existingText\n$owner: $message"
    }

    private fun sendFile(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri)

        /**
         * Getting the file name using
         * content resolver
         */
        val cursor = contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        cursor.moveToFirst()
        val fileName = cursor.getString(nameIndex)
        cursor.close()

        peer.sendFile(fileName = fileName, fileBytesArray = inputStream!!.readBytes())
    }

    private fun initButtons() {
        binding.connectToPeerButton.setOnClickListener {
            val inputBox = binding.peerIdInputBox
            val peerId = inputBox.text.toString().trim()
            inputBox.text = null
            peer.connect(peerId = peerId)
        }

        binding.sendMessageButton.setOnClickListener {
            val inputBox = binding.messageInputBox
            val message = inputBox.text.toString()
            inputBox.text = null
            addMessageToMessageBox(isSender = true, message = message)
            peer.sendText(text = message)
        }

        binding.endConnectionButton.setOnClickListener {
            peer.closeP2P()
        }

        binding.sendFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            newFileResult.launch(intent)

        }

    }


}
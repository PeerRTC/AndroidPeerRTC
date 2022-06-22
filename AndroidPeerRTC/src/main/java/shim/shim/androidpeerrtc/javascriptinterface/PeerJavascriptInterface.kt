package shim.shim.androidpeerrtc.javascriptinterface

import android.app.Activity
import android.webkit.JavascriptInterface
import org.json.JSONArray
import shim.shim.androidpeerrtc.peerrtc.AndroidPeerRTC
import shim.shim.androidpeerrtc.view.MediatorView


class PeerJavascriptInterface(
    private val activity: Activity,
    private val peer: AndroidPeerRTC,
    private val mediatorView: MediatorView
) :
    AndroidPeerInterface {
    override val name: String = "AndroidPeer"

    @JavascriptInterface
    fun onTextMessage(message: String) {
        activity.runOnUiThread {
            peer.onTextMessage?.invoke(message)
        }
    }

    @JavascriptInterface
    fun onSendFileMessage(fileArrayStrings: String, fileSizeSent: Int) {
        activity.runOnUiThread {
            val fileBytesArray = fileArrayStringsToByteArray(arrayString = fileArrayStrings)
            peer.onSendFileMessage?.invoke(fileBytesArray, fileSizeSent)
        }


    }


    @JavascriptInterface
    fun onFileMessage(
        fileName: String,
        fileTotalSize: Int,
        fileArrayStrings: String,
        done: Boolean
    ) {
        activity.runOnUiThread {
            val fileBytesArray = fileArrayStringsToByteArray(arrayString = fileArrayStrings)
            peer.onFileMessage?.invoke(fileName, fileTotalSize, fileBytesArray, done)
        }

    }

    @JavascriptInterface
    fun onCloseP2P() {
        activity.runOnUiThread {
            peer.peerId = null
            peer.onCloseP2P?.invoke()
        }

    }

    @JavascriptInterface
    fun onClose() {
        activity.runOnUiThread {
            peer.id = null
            peer.isConnectedToServer = false
            peer.onClose?.invoke()
        }
    }


    @JavascriptInterface
    fun onNewPayload(payloadStringJson: String) {
        activity.runOnUiThread {
            peer.onNewPayload?.invoke(payloadStringJson)
        }
    }


    @JavascriptInterface
    fun onNewPrivatePayload(payloadStringJson: String) {
        activity.runOnUiThread {
            peer.onNewPrivatePayload?.invoke(payloadStringJson)
        }
    }


    @JavascriptInterface
    fun onPeerPayloads(payloadsStringJson: String) {
        activity.runOnUiThread {
            val jsonArray = JSONArray(payloadsStringJson)
            peer.onPeerPayloads?.invoke(jsonArray)
        }
    }

    @JavascriptInterface
    fun onPeerIds(idsStringJson: String) {
        activity.runOnUiThread {
            val jsonArray = JSONArray(idsStringJson)
            peer.onPeerIds?.invoke(jsonArray)
        }
    }

    @JavascriptInterface
    fun onPeerConnectRequest(peerId: String, requestId: String) {
        activity.runOnUiThread {
            val acceptDeclineRequest = { accept: Boolean ->
                activity.runOnUiThread {
                    mediatorView.evaluateJavascript("acceptDeclineConnectRequest('$requestId', $accept)")
                }
            }
            val accept = {
                acceptDeclineRequest(true)
            }

            val decline = {
                acceptDeclineRequest(false)
            }
            peer.onPeerConnectRequest?.invoke(peerId, accept, decline)
        }
    }


    @JavascriptInterface
    fun onPeerConnectionDecline(peerId: String) {
        activity.runOnUiThread {
            peer.onPeerConnectionDecline?.invoke(peerId)
        }
    }

    @JavascriptInterface
    fun onPeerConnectSuccess(peerId: String) {
        activity.runOnUiThread {
            peer.peerId = peerId
            peer.onPeerConnectSuccess?.invoke(peerId)
        }
    }

    @JavascriptInterface
    fun onAdminBroadcastData(data: String) {
        activity.runOnUiThread {
            peer.onAdminBroadcastData?.invoke(data)
        }
    }


    @JavascriptInterface
    fun onAdminGetAllClientsData(clientsDataJsonString: String) {
        activity.runOnUiThread {
            val jsonArray = JSONArray(clientsDataJsonString)
            peer.onAdminGetAllClientsData?.invoke(jsonArray)
        }
    }

    @JavascriptInterface
    fun onAdminActionDecline() {
        activity.runOnUiThread {
            peer.onAdminActionDecline?.invoke()
        }
    }


    @JavascriptInterface
    fun onServerError(errorMessage: String) {
        activity.runOnUiThread {
            peer.onServerError?.invoke(errorMessage)
        }
    }

    @JavascriptInterface
    fun onStart(id: String) {
        activity.runOnUiThread {
            peer.id = id
            peer.isConnectedToServer = true
            peer.onStart?.invoke()
        }
    }

    @JavascriptInterface
    fun onServerPing() {
        activity.runOnUiThread {
            peer.onServerPing?.invoke()
        }
    }


    /**
     * For convert file array strings from
     * the mediator.js to bytes array in
     * this interface.
     */
    private fun fileArrayStringsToByteArray(arrayString: String): ByteArray {
        return arrayString.replace(Regex("\\s+"), "")
            .split(",")
            .filter { it.matches(Regex("\\d+")) }
            .map { (it.toUInt().toByte()) }.toByteArray()
    }
}
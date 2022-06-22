var sourceConn
var receivedConn
var peer

// stores the connections request via id
const connectionRequests = new Map()


function initPeer(serverURL, configuration){
	peer = new PeerRTC(serverURL, configuration)
	peer.pingServer(10000)


	peer.ontextmessage = message=>{
		AndroidPeer.onTextMessage(message)
	}



	peer.onsendfilemessage = (file, fileSizeSent)=>{
		const reader = new FileReader()
		reader.onload = event => {
			const buffer = new Uint8Array(event.target.result)
			const bytes = []
		    for(byte of buffer){
		        bytes.push(byte)
		    }
			AndroidPeer.onSendFileMessage(bytes.toString(), fileSizeSent)
		}
		reader.readArrayAsBuffer(file)
	}


	peer.onfilemessage = (fname, fileTotalSize, fileBytesArray, done)=>{
		AndroidPeer.onFileMessage(fname, fileTotalSize, fileBytesArray.toString(), done)
	}

	peer.oncloseP2P = ()=>{
		AndroidPeer.onCloseP2P()
	}


	peer.onclose = ()=>{
		AndroidPeer.onClose()
	}

	peer.onnewpayload = payload=>{
		AndroidPeer.onNewPayload(JSON.stringify(payload))
	}


	peer.onnewprivatepayload = payload =>{
		AndroidPeer.onNewPrivatePayload(JSON.stringify(payload))
	}

	peer.onpeerpayloads = payloads=>{
		const stringPayloads = []
		for(const payload of payloads){
			stringPayloads.push(JSON.stringify(payload))
		}
		Android.onPeerPayloads(JSON.stringify(stringPayloads))
	}

	peer.onpeerids = (ids) => {
		AndroidPeer.onPeerIds(JSON.stringify(ids))
		
	}


	peer.onpeerconnectrequest = (peerId, accept, decline)=>{
		const requestId = Date.now().toString()
		connectionRequests.set(requestId, [accept, decline])
		AndroidPeer.onPeerConnectRequest(peerId, requestId)

	}

	peer.onpeerconnectdecline = peerId => {
		AndroidPeer.onPeerConnectionDecline(peerId)
	}

	peer.onnewtrack = (newTrack, trackStreams) => {
		if (trackStreams) {
			const stream = trackStreams[0]
			if (stream) {
				const existingConn = receivedConn
				if (existingConn) {
					existingConn.close()
				}

				receivedConn = new MediaStreamConnection(stream)
				receivedConn.onConnectionEstablished = () => {
					AndroidMediaConnection.onMediaStreamReceivedInitialized()
				}
				receivedConn.onicecandididate = sdp => {
					AndroidMediaConnection.onMediatorOfferSDP(JSON.stringify(sdp))
				}
				receivedConn.oncloseP2P = ()=>{
					AndroidMediaConnection.onMediaStreamReceivedClosed()
				}

				receivedConn.start()

				AndroidMediaConnection.initReceiveMediaStreamSource()
			}
		}
	}


	peer.onpeerconnectsuccess = peerId =>{
		AndroidPeer.onPeerConnectSuccess(peerId)
	}


	peer.onadminbroadcastdata = data =>{
		var finalData = data
		if (typeof finalData != "string") {
			finalData = JSON.stringify(data)
		}
		AndroidPeer.onAdminBroadcastData(finalData)
	}

	peer.onadmingetallclientsdata = clientsData =>{
		AndroidPeer.onAdminGetAllClientsData(JSON.stringify(clientsData))
	}

	peer.onadminactiondecline = ()=> {
		AndroidPeer.onAdminActionDecline()
	}

	peer.onservererror = event =>{
		AndroidPeer.onServerError(event.message)
	}

}



function start(isSecure){
	peer.start(isSecure, p=>{
		AndroidPeer.onStart(p.id)
	})
}


function sourceConnCreateAnswer(sdp){
	const existingConn = sourceConn
	if (existingConn) {
		existingConn.close()
	}


	sourceConn =  new MediaStreamConnection()
	sourceConn.onnewtrack = (newTrack, trackStreams) => {
		if (trackStreams) {
			const stream = trackStreams[0]
			if (stream) {
				peer.setMediaStream(stream)
				AndroidMediaConnection.onMediaStreamSourceMediaAvailable()
			}
				
		}

	}

	sourceConn.oncloseP2P = ()=>{
		AndroidMediaConnection.onMediaStreamSourceClosed()
	}

	sourceConn.onConnectionEstablished = () => {
		AndroidMediaConnection.onMediaStreamSourceInitialized()
	}

	sourceConn.onicecandididate = sdp=> {
		AndroidMediaConnection.onMediatorStreamSourceAnswerSDP(JSON.stringify(sdp))
	}


	sourceConn.start()
	sourceConn.createAnswer(sdp)
}


function acceptDeclineConnectRequest(requestId, accept){
	const request = connectionRequests.get(requestId)
	if (request) {
		if (isAccept) {
			request[0]()
		} else{
			request[1]()
		}
		connectionRequests.delete(requestId)
	}
}





















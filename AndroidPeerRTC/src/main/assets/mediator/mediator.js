var sourceConn
var receivedConn
var peer


function initPeer(serverURL, configuration){
	peer = new PeerRTC(serverURL, configuration)
	peer.pingServer(10000)

	peer.oncloseP2P = ()=>{
		AndroidPeer.onCloseP2P()
	}


	peer.ontextmessage = m =>{
		console.log(m)
	}

	peer.onnewtrack = (newTrack, trackStreams) =>{

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

}



function start(isSecure){
	peer.start(isSecure, p=>{
		AndroidPeer.onStart()
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









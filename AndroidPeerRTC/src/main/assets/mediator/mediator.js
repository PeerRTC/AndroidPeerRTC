const peer = new PeerRTC()

var sourceConn
var receivedConn

peer.pingServer(10000)

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




peer.start(true, p=>{
	console.log("Connected to server")
})


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
				peer.addMediaStream(stream)
				peer.connect("d8f8ce7e-c686-46f3-9e99-3aa3902e0360")
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









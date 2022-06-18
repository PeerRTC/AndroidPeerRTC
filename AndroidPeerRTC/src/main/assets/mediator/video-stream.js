var mediaStreamConn = null

navigator.mediaDevices.getUserMedia({audio:true, video:true}).then(stream=>{
	document.getElementById("myVideo").srcObject = stream
	mediaStreamConn = new MediaStreamConnection(stream)

	mediaStreamConn.onnewtrack = (newTrack, trackStreams) => {
		// send to peer connection
		console.log("New track attached")
	}

	mediaStreamConn.onConnectionEstablished = () => {
		console.log("New media stream connected")
	}

	mediaStreamConn.onicecandididate = (iceCandidates, sdp) => {
		console.log(JSON.stringify(iceCandidates))
		console.log(JSON.stringify(sdp))
	}


	mediaStreamConn.start()
})


const peer = new PeerRTC()

const sourceConn = new MediaStreamConnection()
const receivedConn = new MediaStreamConnection()


peer.onnewtrack = (newTrack, trackStreams) =>{
	const stream = trackStreams[0]
	if (trackStreams && stream) {
		for(track of stream.getTracks()){
			receivedConn.addTrack(track, stream)
		}
	}
}

peer.start(true, p=>{
	console.log("A")
})



sourceConn.onnewtrack = (newTrack, trackStreams) => {
	// send to peer connection
	console.log("New track attached")
	const stream = trackStreams[0]
	if (trackStreams && stream) {
		peer.addMediaStream(stream)
	}

}

sourceConn.onConnectionEstablished = () => {
	console.log("New media stream connected")
}

sourceConn.onicecandididate = sdp=> {
	Android.onMediatorStreamSourceAnswerSDP(JSON.stringify(sdp))
}


sourceConn.start()




receivedConn.onConnectionEstablished = () => {
	console.log("New media stream connected")
}
receivedConn.onicecandididate = (iceCandidates, sdp) => {
	console.log(JSON.stringify(iceCandidates))
	console.log(JSON.stringify(sdp))
}
receivedConn.start()




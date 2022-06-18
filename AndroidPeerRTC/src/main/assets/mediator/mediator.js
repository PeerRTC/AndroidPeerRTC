const peer = new PeerRTC()

const sourceConn = new MediaStreamConnection()
const sourceMediaStream = new MediaStream()

const receivedMediaStream = new MediaStream()
const receivedConn = new MediaStreamConnection(receivedMediaStream)

sourceConn.onnewtrack = (newTrack, trackStreams) => {
	// send to peer connection
	console.log("New track attached")
	if (trackStreams && trackStreams[0]) {
		for(track of trackStreams[0].getTracks()){
			sourceMediaStream.addTrack(track)
		}
	}

}

sourceConn.onConnectionEstablished = () => {
	console.log("New media stream connected")
}

sourceConn.onicecandididate = (iceCandidates, sdp) => {
	console.log(JSON.stringify(iceCandidates))
	console.log(JSON.stringify(sdp))
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


peer.onnewtrack = (newTrack, trackStreams) =>{
	if (trackStreams && trackStreams[0]) {
		for(track of trackStreams[0].getTracks()){
			receivedMediaStream	.addTrack(track)
		}
	}
}

peer.addMediaStream(sourceMediaStream)

peer.start(true, p=>{
console.log("A")
})

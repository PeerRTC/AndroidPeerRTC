class MediaStreamConnection{

	#mediaStreamConn = null
	#mediaStream = null
	constructor(mediaStream){
		this.#mediaStream = mediaStream
		this.onnewtrack = null
		this.onConnectionEstablished = null
		this.onicecandididate = null
		this.onnegotiationneeded = null
		this.oncloseP2P = null
	}

	start(){
		const mediaStreamConn = new BrowserRTC(null, this.#mediaStream)
		this.#mediaStreamConn =mediaStreamConn

		const onicecandididate = (iceCandidates, sdp) => {
			const onicecandididate = this.onicecandididate
			if (onicecandididate) {
				onicecandididate(sdp)
			}
			
		}

		const onConnectionEstablished = ()=>{
			const onConnectionEstablished = this.onConnectionEstablished
			if (onConnectionEstablished) {
				onConnectionEstablished()
			}
		}

		const onnegotiationneeded = ()=>{
			const onnegotiationneeded = this.onnegotiationneeded
			if (onnegotiationneeded) {
				onnegotiationneeded()
			}
		}

		mediaStreamConn.addStreamToConnection()
		mediaStreamConn.setCallbacks(onConnectionEstablished, this.oncloseP2P, onicecandididate , null, null, null, onnegotiationneeded, this.onnewtrack)
	}


	createOffer(){
		this.#mediaStreamConn.createOffer()
	}
	
	createAnswer(sdp){
		this.#mediaStreamConn.createAnswer(null, sdp)
	}

	saveAnswer(sdp){
		const mediaStreamConn = this.#mediaStreamConn
		mediaStreamConn.setRemoteDescription(null, sdp)
	}

	
	addTrack(track, stream){
		this.#mediaStreamConn.addTrack(track, stream)
	}


	

}
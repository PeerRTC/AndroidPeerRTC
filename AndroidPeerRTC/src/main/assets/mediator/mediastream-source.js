class MediaStreamSource{
	static TYPE_AUDIO = 1
	static TYPE_VIDEO = 2
	static TYPE_AUDIO_VIDEO =3
	static TYPE_SCREEN = 4

	constructor(){
		this.mediaStreamConn = null
	}


	startStream(type){
		const htmlElement = this.#getHtmlElement(type)
		var promise = null

		if (type == MediaStreamSource.TYPE_SCREEN) {
			promise = navigator.mediaDevices.getDisplayMedia()
		} else if([MediaStreamSource.TYPE_AUDIO, MediaStreamSource.TYPE_VIDEO, MediaStreamSource.TYPE_AUDIO_VIDEO].includes(type)) {
			var video = false
			var audio = false
			switch(type){
				case MediaStreamSource.TYPE_AUDIO:
					audio = true
					break
				case MediaStreamSource.TYPE_VIDEO:
					video = true
					break
				case MediaStreamSource.TYPE_AUDIO_VIDEO:
					audio = true
					video = true
					break
			}

			promise = navigator.mediaDevices.getUserMedia({audio:audio, video:video})
		}


		if (promise) {
			promise.then(stream=>{
				if (htmlElement) {
					htmlElement.srcObject = stream
				}

				this.#initMediaStreamConnection(stream, htmlElement)
			})
		}
		

	}

	receiveStream(type){
		const htmlElement = this.#getHtmlElement(type)
		this.#initMediaStreamConnection(null, htmlElement)
	}



	#getHtmlElement(type){
		var htmlElement = null
		if (type == MediaStreamSource.TYPE_AUDIO) {
			htmlElement = document.getElementById("audio")
		} else if ([MediaStreamSource.TYPE_VIDEO, MediaStreamSource.TYPE_AUDIO_VIDEO, MediaStreamSource.TYPE_SCREEN].includes(type)) {
			htmlElement = document.getElementById("video")
		}
		

		if (htmlElement) {
			htmlElement.style.visibility = "visible"
		}

		return htmlElement
	}

	#initMediaStreamConnection(stream, htmlElement){
		const mediaStreamConn = new MediaStreamConnection(stream)
		this.mediaStreamConn = mediaStreamConn

		var firstNegotation = true

		mediaStreamConn.onnewtrack = (newTrack, trackStreams) => {
			htmlElement.srcObject = trackStreams[0]
			console.log("New track attached")
		}

		mediaStreamConn.onConnectionEstablished = () => {
			console.log("New media stream connected")
		}

		mediaStreamConn.onicecandididate = (iceCandidates, sdp) => {
			console.log(JSON.stringify(iceCandidates))
			console.log(JSON.stringify(sdp))
		}


		mediaStreamConn.onnegotiationneeded = ()=>{
			if (firstNegotation) {
				firstNegotation = false
			} else{
				mediaStreamConn.start()
			}
			
		}

		mediaStreamConn.start()
	}


}
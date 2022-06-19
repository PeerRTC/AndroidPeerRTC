class MediaStreamSource{
	static TYPE_AUDIO = 1
	static TYPE_VIDEO = 2
	static TYPE_AUDIO_VIDEO =3
	static TYPE_SCREEN = 4

	constructor(){
		this.mediaStreamConn = null
	}


	startStream(type, audioDeviceID, videoDeviceID){
		const htmlElement = this.#getHtmlElement(type)
		var promise = null

		if (type == MediaStreamSource.TYPE_SCREEN) {
			promise = navigator.mediaDevices.getDisplayMedia()
		} else if([MediaStreamSource.TYPE_AUDIO, MediaStreamSource.TYPE_VIDEO, MediaStreamSource.TYPE_AUDIO_VIDEO].includes(type)) {
			const video = {
				deviceId: videoDeviceID,
				frameRate: {
	                ideal: 60,
	                min: 10
	            }
			}

			const audio = {
				deviceId: audioDeviceID,
				echoCancellation:true ,
				noiseSuppression:true
			}

			var hasVideo = false
			var hasAudio = false
			switch(type){
				case MediaStreamSource.TYPE_AUDIO:
					hasAudio = true
					break
				case MediaStreamSource.TYPE_VIDEO:
					hasVideo = true
					break
				case MediaStreamSource.TYPE_AUDIO_VIDEO:
					hasAudio = true
					hasVideo = true
					break
			}


			const constraints = {}

			if (hasVideo) {
				constraints.video = video
			}

			if (hasAudio) {
				constraints.audio = audio
			}

			promise = navigator.mediaDevices.getUserMedia(constraints)
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

	getMediaDevices(onDevices){
		navigator.mediaDevices.enumerateDevices().then(devices=>{
			const results = {
				audio:[],
				video:[]
			}
			for(const device of devices){
				const id = device.deviceId
				switch(device.kind){
					case "videoinput":
						results.video.push(id)
					case "audioinput":
						results.audio.push(id)
				}
			}

			onDevices(results)
		})
		

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

		mediaStreamConn.onicecandididate = (sdp) => {
			Android.onMediaStreamSourceSDP(JSON.stringify(sdp))
		
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


const source = new MediaStreamSource()

function startStream(type, whichCam){
	source.getMediaDevices(devices=>{
		source.startStream(type, devices.audio[0], devices.video[whichCam])
	})
}

function receiveStream(type){
	source.receiveStream(type)
}

function createOffer(){
	source.mediaStreamConn.createOffer()
}

function saveAnswer(sdp){
	source.mediaStreamConn.saveAnswer(sdp)
}
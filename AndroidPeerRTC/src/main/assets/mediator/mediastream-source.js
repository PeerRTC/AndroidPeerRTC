class MediaStreamSource{
	static TYPE_AUDIO = 1
	static TYPE_VIDEO = 2
	static TYPE_AUDIO_VIDEO =3

	constructor(){
		this.mediaStreamConn = null
	}


	startStream(type, mediaConstraints){
		const htmlElement = this.#getHtmlElement(type)
		var promise = null

		if([MediaStreamSource.TYPE_AUDIO, MediaStreamSource.TYPE_VIDEO, MediaStreamSource.TYPE_AUDIO_VIDEO].includes(type)) {
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


			if (!hasVideo) {
				mediaConstraints.video = false
			}

			
			if (!hasAudio) {} {
				mediaConstraints.audio = false
			}

			promise = navigator.mediaDevices.getUserMedia(mediaConstraints)
		}


		if (promise) {
			promise.then(stream=>{
				if (htmlElement) {
					htmlElement.srcObject = stream
				}

				AndroidMediaConnection.onMediaStreamSourceMediaAvailable()
				this.#initMediaStreamConnection(stream, htmlElement, false)
				source.mediaStreamConn.createOffer()
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
		this.#initMediaStreamConnection(null, htmlElement, true)
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

	#initMediaStreamConnection(stream, htmlElement, isReceivingSource){
		const mediaStreamConn = new MediaStreamConnection(stream)
		this.mediaStreamConn = mediaStreamConn

		mediaStreamConn.onnewtrack = (newTrack, trackStreams) => {
			htmlElement.srcObject = trackStreams[0]
			if (isReceivingSource) {
				AndroidMediaConnection.onMediaStreamReceivedMediaAvailable()
			}
		}

		mediaStreamConn.onConnectionEstablished = () => {
			console.log("New media stream connected")
		}

		mediaStreamConn.onicecandididate = (sdp) => {
			const stringifiedSDP = JSON.stringify(sdp)
			if (isReceivingSource) {
				AndroidMediaConnection.onMediaStreamReceivedAnswerSDP(stringifiedSDP)
			} else{
				AndroidMediaConnection.onMediaStreamSourceSDP(stringifiedSDP)
			}
			
		
		}


		
		mediaStreamConn.start()
	}


}


const source = new MediaStreamSource()

function startStream(type, whichCam, audioConstraints, videoConstraints){
	source.getMediaDevices(devices=>{
		var videoDeviceId = ""
		var audioDeviceId =  ""

		try{
			videoDeviceId = devices.video[whichCam]
		}catch(e){

		}

		try{
			audioDeviceId = devices.audio[0]
		}catch(e){

		}

		const constraints = {
			video: {
				deviceId: videoDeviceId,
				frameRate: {
	                ideal: 60,
	                min: 10
	            } 
			} ,

            audio: {
            	deviceId: audioDeviceId,
				echoCancellation:true ,
				noiseSuppression:true
            }

		}

		if (audioConstraints) {
			audioConstraints.deviceId = audioDeviceId
			constraints.audio = audioConstraints

		}

		if (videoConstraints) {
			videoConstraints.deviceId = videoDeviceId
			constraints.video = videoConstraints
		}

		console.log(JSON.stringify(constraints))
		source.startStream(type, constraints)
	})
}


function receiveStream(type){
	source.receiveStream(type)
	AndroidMediaConnection.connectMediaStreamReceivedToMediator()
}


function createAnswer(sdp){
	source.mediaStreamConn.createAnswer(sdp)
}

function saveAnswer(sdp){
	source.mediaStreamConn.saveAnswer(sdp)
}

function muteAudio(mute){

	const video = document.getElementById("video").srcObject
	const audio =  document.getElementById("audio").srcObject
	var audioTracks = []
	if (video) {
		audioTracks = audioTracks.concat(video.getAudioTracks())
	}

	if (audio) {
		audioTracks = audioTracks.concat(audio.getAudioTracks())
	}
	

	for(track of audioTracks){
		track.enabled = !mute

	}
}

function enableVideo(enable){
	const video = document.getElementById("video").srcObject
	var videoTracks = []
	if (video) {
		videoTracks = video.getVideoTracks()
	}

	for(track of videoTracks){
		track.enabled = enable
	}
}

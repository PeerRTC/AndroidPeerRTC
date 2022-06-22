# AndroidPeerRTC 📱
An android library built on top of [PeerRTC](https://github.com/ShimShim27/PeerRTC) module by utilizing the built in WebView module
in android. This library is packed with easy to call api for native android that utilizes peer to peer connection like sending raw text, 
sending raw files, audio streaming, video streaming, connecting to peers via unique id and more. The library is created with Kotlin. However,
it can be also used with Java.

## ❗ Note
* There are no documentations for this library yet. Any contributors are welcome.
* Add credits and attribution to this [repository](https://github.com/ShimShim27/AndroidPeerRTC) when using this library.
* This library is still in beta phase and can be unstable. 
* Source code contributions and bug reports are encouraged.

## 📖 Sample Project
* [Video calling app](https://github.com/ShimShim27/AndroidPeerRTC/tree/master/app)

## ⚙️ Setup

1. If you will be using the default PeerRTC backend server provided by us for testing purposes, you can skip this step. For own
managed backend server, refer to [PeerRTC server's](https://github.com/ShimShim27/PeerRTC-Server) repository. Server owned by us is unstable and not managed so it is
recommended to host your own.<br/>

2. Addd the following in your root [build.gradle](https://github.com/ShimShim27/AndroidPeerRTC/blob/master/build.gradle) file
```
allprojects {
  repositories {
    ...
    maven { url 'https://jitpack.io' }
  }
}
```

3. Add in [build.gradle](https://github.com/ShimShim27/AndroidPeerRTC/blob/master/app/build.gradle) (app level) file
```
dependencies {
 implementation 'com.github.ShimShim27:AndroidPeerRTC:v.1.0-beta'
}
```

## 👨‍🏫 Sample Usage 
```
val onReady = {p:AndroidPeerRTC->
  p.start(isSecure=true)
}

val peer = AndroidPeerRTC(context = this, serverURL = null, configuration = null, onReady=onReady) 

peer.onStart = {p:AndroidPeerRTC-> 
  println("Successfully connected to the server")
  println("My Id " + p.id)
}
```

## 📚 Api Reference
There are no documentations api reference and documentations yet. Feel free to contribute 😉.



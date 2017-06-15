// VideoChatActivity#onCreate()
// Returns the number of cams & front/back face device name
int camNumber = VideoCapturerAndroid.getDeviceCount();
String frontFacingCam = VideoCapturerAndroid.getNameOfFrontFacingDevice();
String backFacingCam = VideoCapturerAndroid.getNameOfBackFacingDevice();

// Creates a VideoCapturer instance for the device name
VideoCapturer capturer = VideoCapturerAndroid.create(frontFacingCam);

// First create a Video Source, then we can make a Video Track
localVideoSource = pcFactory.createVideoSource(capturer, this.pnRTCClient.videoConstraints());
VideoTrack localVideoTrack = pcFactory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);

// First we create an AudioSource then we can create our AudioTrack
AudioSource audioSource = pcFactory.createAudioSource(this.pnRTCClient.audioConstraints());
AudioTrack localAudioTrack = pcFactory.createAudioTrack(AUDIO_TRACK_ID, audioSource);
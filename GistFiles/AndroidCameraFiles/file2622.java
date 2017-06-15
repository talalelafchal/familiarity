// VideoChatActivity#onCreate()
MediaStream mediaStream = pcFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);

// Now we can add our tracks.
mediaStream.addTrack(localVideoTrack);
mediaStream.addTrack(localAudioTrack);
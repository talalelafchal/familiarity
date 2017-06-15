// Ex: Create a Video Source, then we can make a Video Track to stream
localVideoSource = pcFactory.createVideoSource(capturer, this.pnRTCClient.videoConstraints());
VideoTrack localVideoTrack = pcFactory.createVideoTrack(VIDEO_TRACK_ID, localVideoSource);

// Note that LOCAL_MEDIA_STREAM_ID can be any string
MediaStream mediaStream = pcFactory.createLocalMediaStream(LOCAL_MEDIA_STREAM_ID);
mediaStream.addTrack(localAudioTrack);
pnRTCClient.attachLocalMediaStream(mediaStream);
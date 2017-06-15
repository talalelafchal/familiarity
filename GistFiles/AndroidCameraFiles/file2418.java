// VideoChatActivity#onCreate()
PeerConnectionFactory.initializeAndroidGlobals(
        this,  // Context
        true,  // Audio Enabled
        true,  // Video Enabled
        true,  // Hardware Acceleration Enabled
        null); // Render EGL Context

PeerConnectionFactory pcFactory = new PeerConnectionFactory();
this.pnRTCClient = new PnRTCClient(Constants.PUB_KEY, Constants.SUB_KEY, this.username);
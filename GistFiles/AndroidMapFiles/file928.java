package com.Instax.sdk;

class NetworkCommunicationParam {
	static final String def_IpAddress = "192.168.0.251"; 
	static final int def_Port = 8080;
	static final int def_SndTimeOut = 3000;
	static final int def_RcvTimeOut =3000;
	static final int def_RetryCnt = 3;
	static final int def_MaxFrameSize = 480;
	static final int def_DelayTime = 0;
	static final int def_IMAGE_Format = 2;	// default JPG
	static final int def_IMAGE_Option = 0;
	static final int def_Password = 1111;
	
	String IpAddress;
	int Port;
	int SndTimeOut;
	int RcvTimeOut;
	int RetryCnt;
	int MaxFrameSize;
	int DelayTime;
	int IMAGE_Format;
	int IMAGE_Option;
	int Password;
	
	NetworkCommunicationParam() {
		IpAddress = def_IpAddress;
		Port = def_Port;
		SndTimeOut = def_SndTimeOut;
		RcvTimeOut = def_RcvTimeOut;
		RetryCnt = def_RetryCnt;
		MaxFrameSize = def_MaxFrameSize;
		DelayTime = def_DelayTime;
		IMAGE_Format = def_IMAGE_Format;
		IMAGE_Option = def_IMAGE_Option;
		Password = def_Password;
	}
	
	void setPassword(int pass) {
		if( -1 <= pass && pass <= 9999 ) {
			this.Password = pass;
		}
		else {
			this.Password = def_Password;
		}
	}
	/**
	 * setNetworkParam
	 * @param ipAddress
	 * @param port
	 */
	void setNetworkParam(String ipAddress, int port) {
		if( ipAddress != null ) {
			this.IpAddress = ipAddress;
		}
		if( 0 <= port && port <= 65535 ) {
			this.Port = port;
		}
		else {
			this.Port = def_Port;	// ”ÍˆÍŠO‚Í‰Šú’l‚ð“ü‚ê‚é
		}
	}
	
	/**
	 * setCommunicationParam
	 * @param sndTimeout
	 * @param rcvTimeout
	 * @param retryCnt
	 */
	void setCommunicationParam(int sndTimeout, int rcvTimeout, int retryCnt) {
		if(sndTimeout < 0 || rcvTimeout < 0 || retryCnt < 0) {
			this.SndTimeOut = def_SndTimeOut;
			this.RcvTimeOut = def_RcvTimeOut;
			this.RetryCnt = def_RetryCnt;
		}
		else {
			this.SndTimeOut = sndTimeout;
			this.RcvTimeOut = rcvTimeout;
			this.RetryCnt = retryCnt;
		}
	}
	
	/**
	 * 
	 * @param sndMaxSize
	 * @param delayTime
	 */
	void setDebugParam(int maxDataSize, int delayTime, int jpgFlg, int jpgOpt) {
		final int SND_BUF_SIZE = 1024*64;
		if( maxDataSize > SND_BUF_SIZE-1-4-NetworkFrameData.MIN_REQ_FRAME_SIZE ) {
			this.MaxFrameSize = SND_BUF_SIZE-1-4-NetworkFrameData.MIN_REQ_FRAME_SIZE;
		}
		else if( maxDataSize < 1 ) {
			this.MaxFrameSize = def_MaxFrameSize;
		}
		else {
			this.MaxFrameSize = maxDataSize;
		}
		
		if( delayTime < 0 ) {
			this.DelayTime = def_DelayTime;
		}
		else {
			this.DelayTime = delayTime;
		}
		
		if( jpgFlg == 1 || jpgFlg == 2 ) {
			this.IMAGE_Format = jpgFlg;
		}
		else {
			this.IMAGE_Format = def_IMAGE_Format;
		}
		
//		if( jpgOpt > 0x0B && jpgOpt != 0x10 ) {
//			this.IMAGE_Option = 0x00;
//		}
//		else {
		//this.IMAGE_Option = jpgOpt;
//		}
	}
	
}

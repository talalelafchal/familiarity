package com.Instax.sdk;

import java.io.*;
import java.util.*;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.util.Log;

import com.Instax.sdk.InstaxStatus.ErrCode;
import com.Instax.sdk.InstaxStatus.InstaxState;

class InstaxProtocol {
	private NetworkCommunicationParam Param = new NetworkCommunicationParam();
	private InstaxStatus State = new InstaxStatus();
	private NetworkCommunicationSndImageThread CommSndImageThread = null;
	private NetworkCommunicationPrintThread CommPrintThread = null;
	private int UID = (int)(System.currentTimeMillis());
	private final int EMERGENCY_ERR_LEVEL = 2;
	
	/**
	 * setNetworkParam
	 * @param ipAddress
	 * @param port
	 */
	void setNetworkParam(String ipAddress, int port) {
		InstaxLog.putLogToSD("IP:"+ipAddress);
		InstaxLog.putLogToSD("Port:"+String.valueOf(port));
		Param.setNetworkParam(ipAddress, port);
	}
	
	/**
	 * 
	 * @param pass
	 */
	void setSecurityPassword(int pass) {
		InstaxLog.putLogToSD("Pass:"+String.valueOf(pass));
		Param.setPassword(pass);
	}
	/**
	 * InstaxLock
	 * @param flg
	 * @return
	 */
	InstaxStatus InstaxLock(byte flg) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 4;
		byte[] Data = new byte[DataSize];
		short Sid = 0xB3;
		ErrCode err = ErrCode.RET_OK;
		
		// ‘—ŽóM
		Data[0] = flg;
		Data[1] = 0x00;
		Data[2] = 0x00;
		Data[3] = 0x00;
		Frame.CreateSndData( UID, Sid, Data, DataSize, Param.Password );
		err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK ) {
			State.setInstaxStatus(InstaxState.ST_BUSY, err);
			return State;
		}
		// ŽóMƒf[ƒ^ƒ`ƒFƒbƒN
		State = Frame.checkResponseFrame(0);
		
		return State;
	}
	/**
	 * getShadingData(0x40)
	 * @param shadingData
	 * @param size 
	 * @return
	 */
	InstaxStatus getShadingData(byte[] shadingData, int size) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 4;
		byte[] Data = new byte[DataSize];
		short Sid = 0x40;
		ErrCode err = ErrCode.RET_OK;
		
		for(int i=0; i<3; i++) {
			Data[0] = 0x00;
			Data[1] = 0x00;
			Data[2] = 0x00;
			Data[3] = (byte)i;
			// ‘—ŽóM
			Frame.CreateSndData( UID, Sid, Data, DataSize, Param.Password );
			err = Sock.SndRcvData( Param, Frame );
			if( err != ErrCode.RET_OK ) {
				State.setInstaxStatus(InstaxState.ST_BUSY, err);
				return State;
			}
			// ŽóMƒf[ƒ^ƒ`ƒFƒbƒN
			State = Frame.checkResponseFrame( size );
			//if( State.ErrCode != ErrCode.RET_OK ) {
			if( judgeErrLevel(State)  >= EMERGENCY_ERR_LEVEL ) {
				return State;
			}
			byte[] tmpData = new byte[size];
			if( Frame.getResponseArrayData(0, tmpData, size) != 0 ) {
				err = ErrCode.E_RCV_FRAME;
				State.setInstaxStatus(InstaxState.ST_BUSY,err);
				break;
			}
			System.arraycopy(tmpData, 0, shadingData, i*size, size);
		}
		return State;
	}
	
	/**
	 * 
	 * @param imageData
	 * @param printWidth
	 * @param printHeight
	 * @param sndFinish
	 * @param shadingData 
	 * @param option
	 * @return
	 */
	//InstaxStatus sndImage(Bitmap imageData, int printWidth, int printHeight, InstaxCallBack sndFinish, byte[] shadingData) {
	InstaxStatus sndImage(Bitmap imageData, int printWidth, int printHeight, InstaxCallBack sndFinish, byte[] shadingData, String option) {
		NetworkCommunication sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		ErrCode err = ErrCode.RET_OK;
		int DataSize;
		byte[] Data;
		short Sid;
		
		if( CommSndImageThread != null ) {
			if( CommSndImageThread.isAlive() ) {
				// ‘OƒXƒŒƒbƒh‚ª‚Ü‚¾¶‚«‚Ä‚¢‚é
				State.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_CONNECT);
				sndFinish.FinishCallBack(State);
				return State;
			}
		}
		
		// 0x50
		DataSize = 0;
		Data = new byte[DataSize];
		Sid = 0x50;
		//Data[0] = 0x00;	// RAM page
		// ‘—ŽóM
		Frame.CreateSndData( UID, Sid, Data, DataSize, Param.Password );
		err = sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK ) {
			//InstaxLock((byte)0x00);
			State.setInstaxStatus(InstaxState.ST_BUSY, err);
			sndFinish.FinishCallBack(State);
			return State;
		}
		State = Frame.checkResponseFrame( 0 );
		if( State.ErrCode != ErrCode.RET_OK ) {
			//InstaxLock((byte)0x00);
			sndFinish.FinishCallBack(State);
			return State;
		}
		
		// ‰æ‘œƒf[ƒ^‰ÁH
		int sndMaxSize = Param.MaxFrameSize;
		int width = imageData.getWidth();			// ‰æ‘œ•
		int height = imageData.getHeight();			// ‰æ‘œ‚‚³
		int imageSize = width * height;				// ‰æ‘œƒTƒCƒY
		int printSize = printWidth * printHeight;	// ˆóüƒTƒCƒY
		int[] printRGB = new int[printSize];
		int[] imageRGB = new int[imageSize];
		int x,y;
		
		// ‰Šú‰»
		Arrays.fill(printRGB, 0x00FFFFFF);		// ”’‰æ–Ê‚ð—pˆÓ
		// ƒsƒNƒZƒ‹î•ñGET
		imageData.getPixels( imageRGB, 0, width, 0, 0, width, height );
		// ƒRƒs[ŠJŽnÀ•WŽZo
		x = (printWidth - width)/2;
		y = (printHeight - height)/2;
		// ƒRƒs[
		for(int i=0; i<height; i++) {
			// ”’‰æ–Ê‚É‰æ‘œ‚ðƒRƒs[
			System.arraycopy(imageRGB, i*width, printRGB, (i+y)*printWidth+x, width);
		}
		imageRGB = null;
		// ‚±‚±‚Ü‚Å‚Å”’–„‚ß‰æ‘œ‚ªo—ˆ‚Ä‚¢‚é

		// RBG•Ê‚É•ª‰ð+ƒVƒF[ƒfƒBƒ“ƒO•â³
		// R(0,0),R(0,1),...,R(0,479), G(0,0),G(0,1),...,G(0,479), B(0,0),B(0,1),...,B(0,479),
		// R(1,0),R(1,1),...
		if( shadingData.length < 3*printWidth ) {
			// ƒVƒF[ƒfƒBƒ“ƒOƒf[ƒ^Žæ“¾‚µ‚Ä‚¢‚È‚©‚Á‚½‚ç‚Æ‚è‚ ‚¦‚¸rank‚O‚Å•â³‚©‚¯‚¿‚á‚¤
			shadingData = new byte[3*printWidth];
			Arrays.fill(shadingData, (byte)0x00);
		}
		Data = new byte[printSize*3];
		ShadingTableAccess shadingTableAccess = new ShadingTableAccess();
		int beforeR, beforeG, beforeB;
		for(int i=0; i<printHeight; i++) {
			for(int j=0; j<printWidth; j++ ) {
				// •â³Œã‚Ì’l‚ð“ü‚ê‚é
				int idx = i*printWidth+j;
				beforeR = Color.red(printRGB[idx]);
				beforeG = Color.green(printRGB[idx]);
				beforeB = Color.blue(printRGB[idx]);
				
				// (i*printWidth*3)+j+0*printWidth ¨ (i*3+0)*printWidth+j
				Data[(i*3+0)*printWidth+j] = shadingTableAccess.getTableDataR( ((int)shadingData[j+0*printWidth] & 0xFF),beforeR );
				Data[(i*3+1)*printWidth+j] = shadingTableAccess.getTableDataG( ((int)shadingData[j+1*printWidth] & 0xFF),beforeG );
				Data[(i*3+2)*printWidth+j] = shadingTableAccess.getTableDataB( ((int)shadingData[j+2*printWidth] & 0xFF),beforeB );
			}
		}
		shadingTableAccess = null;
		
		// JPG:ˆ³k
		if(Param.IMAGE_Format == 2)
		{
			final int encodedSizeMax = 150*1024;		// 150k
			// byteŒ^”z—ñ‚ðintŒ^”z—ñ‚É–ß‚·
			int alpha = 0xFF000000;		// ƒ¿’l‚ðŽg‚í‚È‚¢‚Æ‚«‚Í0xFF‚ð“ü‚ê‚é
			for(int i=0; i<printHeight; i++) {
				for(int j=0; j<printWidth; j++ ) {
					beforeR = ((((int)Data[(i*3+0)*printWidth+j]) << 16) & 0xFF0000);
					beforeG = ((((int)Data[(i*3+1)*printWidth+j]) <<  8) & 0x00FF00);
					beforeB = ((((int)Data[(i*3+2)*printWidth+j]) <<  0) & 0x0000FF);
					printRGB[i*printWidth+j] = (alpha | beforeR | beforeG | beforeB);
				}
			}
			
			// Android‚ÌBitmapƒNƒ‰ƒX‚ð—˜—p‚µ‚ÄJPGˆ³k
			//if(Param.Port == 8080)
			{
				Bitmap bmp = Bitmap.createBitmap(printRGB, printWidth, printHeight, Bitmap.Config.ARGB_8888);
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				int encodedSize;
				int jpgQuality = 100;	// ‰Šú’l100
				Log.d("jpeg","encodedSizeMax:"+String.valueOf(encodedSizeMax));
				while(true) {
					bmp.compress(CompressFormat.JPEG, jpgQuality, baos);
					encodedSize = baos.size();
					
					Log.d("jpeg","Quality:"+String.valueOf(jpgQuality));
					Log.d("jpeg","encodedSize:"+String.valueOf(encodedSize));
					
					if(encodedSize <= encodedSizeMax) {
						// ˆ³kŒãƒTƒCƒY‚ªÅ‘å—e—Ê‚æ‚è¬‚³‚©‚Á‚½‚ç”²‚¯‚é
						String log = String.format("JPG Quality:%d", jpgQuality);
						InstaxLog.putLogToSD(log);
						break;
					}
					baos.reset();
					jpgQuality -= 10;		// •iŽ¿‚ð—Ž‚Æ‚·
					if(jpgQuality < 0) {
						// Å’á•iŽ¿‚Å‚àÅ‘å—e—ÊˆÈ‰º‚É‚¨‚³‚Ü‚ç‚È‚¢‚Ì‚ÅƒGƒ‰[
						encodedSize = 0;
						baos.reset();
						Log.e("jpeg","encode fail");
						break;
					}
				}
				if(encodedSize > 0) {
					Data = new byte[encodedSize];
					Data = baos.toByteArray();
				}
				else {
					printRGB = null;
					State.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_MEMORY);
					sndFinish.FinishCallBack(State);
					return State;
				}
			} 
			// MT»‚ÌJPGˆ³k
			//else
//			{
//				// ˆ³k—¦‚Ì•ÏX•û–@•s–¾
//				for(int i=0; i<printWidth*printHeight; i++) {
//					Data[i*3+0] = (byte)((printRGB[i] >> 16) & 0xFF);
//					Data[i*3+1] = (byte)((printRGB[i] >>  8) & 0xFF);
//					Data[i*3+2] = (byte)((printRGB[i] >>  0) & 0xFF);
//				}
//				//Arrays.fill(Data, (byte)0xFF);
//				JpegEncode jpg = new JpegEncode(Data);
//				JpegEncodeInfo info = new JpegEncodeInfo();
//				info.uswNumSamplesPerLine = printWidth;
//				info.uswNumLines = printHeight;
//				jpg.jpeg_encode(info);
//				Data = new byte[(int)info.uinInputFileSize];
//				//Log.d("jpeg", "size:"+String.valueOf(info.uinInputFileSize));
//				System.arraycopy(info.pbOutputData, 0, Data, 0, (int)info.uinInputFileSize);
//			}
		}
		printRGB = null;
	/*
	 * ƒXƒŒƒbƒh“à‚ÉˆÚ“®
		// 0x51
		int imgSize = Data.length;
		DataSize = 12;
		Data = new byte[DataSize];
		Sid = 0x51;
		Data[0] = (byte)Param.JpgFlg;	// ‰æ‘œƒtƒH[ƒ}ƒbƒg
		Data[1] = 0x00;		// pic opt
		Data[2] = (byte)(((int)imgSize >> 24) & 0xFF);		// size
		Data[3] = (byte)(((int)imgSize >> 16) & 0xFF);
		Data[4] = (byte)(((int)imgSize >> 8) & 0xFF);;
		Data[5] = (byte)(((int)imgSize >> 0) & 0xFF);;		
		Data[6] = 0x00;		// jpg param
		Data[7] = 0x00;
		Data[8] = 0x00;
		Data[9] = 0x00;
		Data[10] = 0x00;	// reserve
		Data[11] = 0x00;	// reserve
		// ‘—ŽóM
		Frame.CreateSndData( UID, Sid, Data, DataSize );
		err = sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK ) {
			ModeReset();
			State.setInstaxStatus(InstaxState.ST_BUSY, err);
			return State;
		}
		State = Frame.checkResponseFrame( 0 );
		if( State.ErrCode != ErrCode.RET_OK ) {
			ModeReset();
			return State;
		}
		*/
		String log = String.format("IMG Size:%d", Data.length);
		InstaxLog.putLogToSD(log);
		
		Param.IMAGE_Option = AnalyzeOption(option);
		// 0x51,0x52,0x53
		Sid = 0x52;
		CommSndImageThread = new NetworkCommunicationSndImageThread(Param, UID, Sid, Data, sndMaxSize, sndFinish);
		CommSndImageThread.start();
		
		return State;
	}
	
	/*
	 * 
	 */
	private int AnalyzeOption(String option) {
		int ret = 0;
		if( option == null ) {
			return ret;
		}
		InstaxLog.putLogToSD("Tag:"+option);
		// ‚·‚×‚Ä‘å•¶Žš‚É•ÏŠ·
		option = option.toUpperCase();
		
		// FujifilmOptionŒŸõ
		if( option.indexOf("FUJIFILMOPTION") == -1 ) {
			InstaxLog.putLogToSD("Tag:"+"0");
			return ret;
		}
		//@CLEARALLŒŸõ
		if( option.indexOf("CLEARALL") != -1 ) {
			ret |= 0x10;
		}
		// FORCEPRINTŒŸõ
		if( option.indexOf("FORCEPRT") != -1 ) {
			ret |= 0x30;
		}
		// LUTŒŸõ
		int index;
		if( (index=option.indexOf("LUT")) != -1 ) {
			// "LUT"‚ÌŽŸ‚Ì•¶Žš‚ð’Šo
			String tmp = option.substring(index+3, index+4);
			int num = 0;
			try {
				// ”’l‚É•ÏŠ·
				num = Integer.parseInt(tmp);
			} catch(NumberFormatException e) {
				num = 0;
			}
			ret |= (0x10+num);
		}
		String log = String.format("Tag:%02X", ret);
		InstaxLog.putLogToSD(log);
		return ret;
	}

	 /**
	  * getSndImageProgress
	  * @return
	  */
	int getSndImageProgress() {
		if( CommSndImageThread == null || !CommSndImageThread.isAlive() ) {
				return 0;
		}
		return CommSndImageThread.getProgress();
	}

	/**
	 * sndImageCancel
	 * @param cancelFinish
	 * @return
	 */
	InstaxStatus sndImageCancel(InstaxCallBack cancelFinish) {
		if( CommSndImageThread == null || !CommSndImageThread.isAlive() ) {
			State.setInstaxStatus(InstaxState.ST_IDLE,ErrCode.RET_OK);
			cancelFinish.FinishCallBack(State);
			return State;
		}
		CommSndImageThread.SndImageCancel(cancelFinish);
		
		State.setInstaxStatus(InstaxState.ST_IDLE,ErrCode.RET_OK);
		return State;
	}

	/**
	 * startPrint(0xB0)
	 * @param printFinish
	 * @return
	 */
	InstaxStatus startPrint(InstaxCallBack printFinish) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 0;
		byte[] Data = new byte[DataSize];
		short Sid = 0xB0;
		ErrCode err = ErrCode.RET_OK;
		
		if( CommPrintThread != null ) {
			if( CommPrintThread.isAlive() ) {
				// ‘OƒXƒŒƒbƒh‚ª‚Ü‚¾¶‚«‚Ä‚¢‚é
				State.setInstaxStatus(InstaxState.ST_BUSY,err);
				printFinish.FinishCallBack(State);
				return State;
			}
		}
		
		// ‘—M
		Frame.CreateSndData( UID, Sid, Data, DataSize, Param.Password );
		err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			printFinish.FinishCallBack(State);
			return State;
		}
		
		// ŽóMƒf[ƒ^ƒ`ƒFƒbƒN
		State = Frame.checkResponseFrame(4);
		if( State.ErrCode != ErrCode.RET_OK ) {
			printFinish.FinishCallBack(State);
			return State;
		}
		int printTime = Frame.getResponselongData(0)/10;	// 100ms’PˆÊ‚Å‚­‚é‚Ì‚Å1/10‚µ‚Ä•b‚É‚·‚é
		
		CommPrintThread = new NetworkCommunicationPrintThread(Param, UID, (short)0xC3, printTime, printFinish);
		CommPrintThread.start();
		
		return State;
	}

	/**
	 * getPrintProgress
	 * @return
	 */
	int getPrintProgress() {
		if( CommPrintThread == null || !CommPrintThread.isAlive() ) {
			return 0;
		}
		return CommPrintThread.getProgress();
	}

	/**
	 * printCancel
	 * @param cancelFinish
	 * @return
	 */
	InstaxStatus printCancel(InstaxCallBack cancelFinish) {
		if( CommPrintThread == null || !CommPrintThread.isAlive() ) {
			State.setInstaxStatus(InstaxState.ST_IDLE,ErrCode.RET_OK);
			cancelFinish.FinishCallBack(State);
			return State;
		}
		CommPrintThread.PrintCancel(cancelFinish);
		
		State.setInstaxStatus(InstaxState.ST_IDLE,ErrCode.RET_OK);
		return State;
	}
	
	/**
	 * getVersion(0xC0)
	 * @param info
	 * @return
	 */
	InstaxStatus getVersion(InstaxInfo info) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		ErrCode err = ErrCode.RET_OK;
		int DataSize = 0;
		byte[] Data = new byte[DataSize];
		short Sid = 0xC0;
		
		// ƒf[ƒ^ì¬
		Frame.CreateSndData( UID, Sid, Data, DataSize, Param.Password );
		// ‘—ŽóM
		err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(8);
		//if( State.ErrCode != ErrCode.RET_OK ) {
		if( judgeErrLevel(State)  >= EMERGENCY_ERR_LEVEL ) {
			return State;
		}
		info.BootVer = Frame.getResponseShortData( 0 );
		info.FwVer   = Frame.getResponseShortData( 2 );
		info.FpgaVer = Frame.getResponseShortData( 4 );
		
		return State;
	}
	
	/**
	 * getInstaxParameter(0xC1)
	 * @param info
	 * @return
	 */
	InstaxStatus getInstaxParameter(InstaxInfo info) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 0;
		byte[] Data = new byte[DataSize];
		short Sid = 0xC1;
		
		// ƒf[ƒ^ì¬
		Frame.CreateSndData( UID, Sid, Data, DataSize, Param.Password );
		// ‘—ŽóM
		ErrCode err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(12);
		//if( State.ErrCode != ErrCode.RET_OK ) {
		if( judgeErrLevel(State) >= EMERGENCY_ERR_LEVEL ) {
			return State;
		}
		int temp;
		info.PrintCount = Frame.getResponselongData(0);
		
		//temp = Frame.getResponseSignedShortData( 4 );
		//info.InstaxTemp = (float)temp/10;
		
		switch(Frame.getInfoBattery()) {
		case 0x00:
		case 0x01:
		case 0x02:
		case 0x03:
			temp = Frame.getInfoBattery();
			break;
		case 0x04:
		default:
			temp = -1;		// DC
			break;
		}
		info.Battery = temp;
		
		info.FilmNum = Frame.getInfoFilm();
		if( info.FilmNum > 10 ) {
			info.FilmNum = 0;
		}
		info.FilmMax = 10;
		info.ImageHeight = 640;
		info.ImageWidth = 480;
		
		return State;	
	}
	
	/**
	 * getInstaxName(0xC2)
	 * @param info
	 * @return
	 */
	InstaxStatus getInstaxName(InstaxInfo info) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 0;
		byte[] Data = new byte[DataSize];
		short Sid = 0xC2;
		
		// ƒf[ƒ^ì¬
		Frame.CreateSndData( UID, Sid, Data, DataSize, Param.Password );
		// ‘—ŽóM
		ErrCode err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(0);
		//if( State.ErrCode != ErrCode.RET_OK ) {
		if( judgeErrLevel(State)  >= EMERGENCY_ERR_LEVEL ) {
			return State;
		}
		info.TargetName = Frame.getResponseStringData(0);
		
		return State;	
	}

	/**
	 * getInstaxStatus
	 * @return
	 */
	InstaxStatus getInstaxStatus() {
		InstaxInfo info = new InstaxInfo();
		State =  getInstaxName(info);
		return State;
	}
	
	/**
	 * setCommunicationParam
	 * @param sndTimeOut
	 * @param rcvTimeOut
	 * @param retryCnt
	 */
	void setCommunicationParam(int sndTimeOut, int rcvTimeOut, int retryCnt) {
		InstaxLog.putLogToSD("SndTimeOut:"+String.valueOf(sndTimeOut));
		InstaxLog.putLogToSD("RcvTimeOut:"+String.valueOf(rcvTimeOut));
		InstaxLog.putLogToSD("RetryCount:"+String.valueOf(retryCnt));
		Param.setCommunicationParam(sndTimeOut, rcvTimeOut, retryCnt);
	}

	/**
	 * 
	 * @param sndMaxSize
	 * @param delayTime
	 */
	void setDebugParam(int sndMaxSize, int delayTime, int jpgFlg, int jpgOpt) {
		Param.setDebugParam(sndMaxSize, delayTime, jpgFlg, jpgOpt);
	}
	
	/**
	 * 
	 * @param param
	 */
	void getDebugParam(InstaxPrintSetting param) {
		param.MaxFrameSize = this.Param.MaxFrameSize;
		param.DelayTime = this.Param.DelayTime;
		param.IMAGE_Format = this.Param.IMAGE_Format;
		param.IMAGE_Option = this.Param.IMAGE_Option;
	}
	/**
	 * setLedElectricAndWhiteMark(0x30)
	 * @param param
	 * @return
	 */
/*
	InstaxStatus setLedElectricAndWhiteMark(InstaxPrintSetting param) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 8;
		byte[] Data = new byte[DataSize];
		short Sid = 0x30;
		
		// ƒf[ƒ^ì¬
		Data[0] = (byte)((param.LedRed >> 8) & 0xFF);
		Data[1] = (byte)((param.LedRed >> 0) & 0xFF);
		Data[2] = (byte)((param.LedGreen >> 8) & 0xFF);
		Data[3] = (byte)((param.LedGreen >> 0) & 0xFF);
		Data[4] = (byte)((param.LedBlue >> 8) & 0xFF);
		Data[5] = (byte)((param.LedBlue >> 0) & 0xFF);
		Data[6] = 0x00;
		Data[7] = 0x00;
//		Data[6] = (byte)((param.BeforeWhite >> 8) & 0xFF);
//		Data[7] = (byte)((param.BeforeWhite >> 0) & 0xFF);
//		Data[8] = (byte)((param.AfterWhite >> 8) & 0xFF);
//		Data[9] = (byte)((param.AfterWhite >> 0) & 0xFF);
		
		Frame.CreateSndData( UID, Sid, Data, DataSize );
		// ‘—ŽóM
		ErrCode err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(0);
		
		return State;	
	}
*/
	/**
	 * setLcs(0x31)
	 * @param param
	 * @return
	 */
/*
	InstaxStatus setLcs(InstaxPrintSetting param) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 22;
		byte[] Data = new byte[DataSize];
		short Sid = 0x31;
		
		// ƒf[ƒ^ì¬
		Data[0] = (byte)((param.LpCycle >> 8) & 0xFF);
		Data[1] = (byte)((param.LpCycle >> 0) & 0xFF);
		Data[2] = (byte)((param.ExposureRed >> 8) & 0xFF);
		Data[3] = (byte)((param.ExposureRed >> 0) & 0xFF);
		Data[4] = (byte)((param.ExposureGreen >> 8) & 0xFF);
		Data[5] = (byte)((param.ExposureGreen >> 0) & 0xFF);
		Data[6] = (byte)((param.ExposureBlue >> 8) & 0xFF);
		Data[7] = (byte)((param.ExposureBlue >> 0) & 0xFF);
		Data[8] = (byte)((param.OpenRed >> 8) & 0xFF);
		Data[9] = (byte)((param.OpenRed >> 0) & 0xFF);
		Data[10] = (byte)((param.OpenGreen >> 8) & 0xFF);
		Data[11] = (byte)((param.OpenGreen >> 0) & 0xFF);
		Data[12] = (byte)((param.OpenBlue >> 8) & 0xFF);
		Data[13] = (byte)((param.OpenBlue >> 0) & 0xFF);
		Data[14] = (byte)((param.CloseRed >> 8) & 0xFF);
		Data[15] = (byte)((param.CloseRed >> 0) & 0xFF);
		Data[16] = (byte)((param.CloseGreen >> 8) & 0xFF);
		Data[17] = (byte)((param.CloseGreen >> 0) & 0xFF);
		Data[18] = (byte)((param.CloseDummy >> 8) & 0xFF);
		Data[19] = (byte)((param.CloseDummy >> 0) & 0xFF);
		Data[20] = (byte)((param.ExposureDummy >> 8) & 0xFF);
		Data[21] = (byte)((param.ExposureDummy >> 0) & 0xFF);
		
		Frame.CreateSndData( UID, Sid, Data, DataSize );
		// ‘—ŽóM
		ErrCode err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(0);
		
		return State;	
	}
*/
	/**
	 * setMovementAndMotorSpeedAndEnergisationMode(0xE1)
	 * @param param
	 * @return
	 */
/*
	InstaxStatus setMovementAndMotorSpeedAndEnergisationMode(InstaxPrintSetting param) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 28;
		byte[] Data = new byte[DataSize];
		short Sid = 0xE1;
		
		// ƒf[ƒ^ì¬
		Data[0] = (byte)((param.BeforeWhite >> 8) & 0xFF);
		Data[1] = (byte)((param.BeforeWhite >> 0) & 0xFF);
		Data[2] = (byte)((param.AfterWhite >> 8) & 0xFF);
		Data[3] = (byte)((param.AfterWhite >> 0) & 0xFF);
		Data[4] = (byte)((param.StepPerLine >> 8) & 0xFF);
		Data[5] = (byte)((param.StepPerLine >> 0) & 0xFF);
		Data[6] = (byte)((param.OrgToPiStep >> 8) & 0xFF);
		Data[7] = (byte)((param.OrgToPiStep >> 0) & 0xFF);
		Data[8] = (byte)((param.SpeedUpStep >> 8) & 0xFF);
		Data[9] = (byte)((param.SpeedUpStep >> 0) & 0xFF);
		Data[10] = (byte)((param.SpeedDnStep >> 8) & 0xFF);
		Data[11] = (byte)((param.SpeedDnStep >> 0) & 0xFF);
		Data[12] = (byte)((param.MainStep >> 8) & 0xFF);
		Data[13] = (byte)((param.MainStep >> 0) & 0xFF);
		Data[14] = (byte)((param.AfterPrintStep >> 8) & 0xFF);
		Data[15] = (byte)((param.AfterPrintStep >> 0) & 0xFF);
		Data[16] = (byte)((param.ReturnStep >> 8) & 0xFF);
		Data[17] = (byte)((param.ReturnStep >> 0) & 0xFF);
		Data[18] = (byte)((param.LowSpeed >> 8) & 0xFF);
		Data[19] = (byte)((param.LowSpeed >> 0) & 0xFF);
		Data[20] = (byte)((param.PrintSpeed >> 8) & 0xFF);
		Data[21] = (byte)((param.PrintSpeed >> 0) & 0xFF);
		Data[22] = (byte)((param.ReturnSpeed >> 8) & 0xFF);
		Data[23] = (byte)((param.ReturnSpeed >> 0) & 0xFF);
		Data[24] = (byte)((param.Excitation >> 0) & 0xFF);
		Data[25] = (byte)((param.LedOpen >> 0) & 0xFF);
		Data[26] = 0x00;
		Data[27] = 0x00;
		
		Frame.CreateSndData( UID, Sid, Data, DataSize );
		// ‘—ŽóM
		ErrCode err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(0);
		
		return State;	
	}
*/
	/**
	 * getLedElectricAndWhiteMark(0x40)
	 * @param param
	 * @return
	 */
/*
	InstaxStatus getLedElectricAndWhiteMark(InstaxPrintSetting param) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		ErrCode err = ErrCode.RET_OK;
		int DataSize = 0;
		byte[] Data = new byte[DataSize];
		short Sid = 0x40;
		
		// ƒf[ƒ^ì¬
		Frame.CreateSndData( UID, Sid, Data, DataSize );
		// ‘—ŽóM
		err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(8);
		//if( State.ErrCode != ErrCode.RET_OK ) {
		if( judgeErrLevel(State)  >= EMERGENCY_ERR_LEVEL ) {
			return State;
		}
		param.LedRed      = Frame.getResponseShortData( 0 );
		param.LedGreen    = Frame.getResponseShortData( 2 );
		param.LedBlue     = Frame.getResponseShortData( 4 );
//		param.BeforeWhite = Frame.getResponseShortData( 6 );
//		param.AfterWhite  = Frame.getResponseShortData( 8 );
		
		return State;
	}
*/
	/**
	 * getLcs(0x41)
	 * @param param
	 * @return
	 */
/*
	InstaxStatus getLcs(InstaxPrintSetting param) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		ErrCode err = ErrCode.RET_OK;
		int DataSize = 0;
		byte[] Data = new byte[DataSize];
		short Sid = 0x41;
		
		// ƒf[ƒ^ì¬
		Frame.CreateSndData( UID, Sid, Data, DataSize );
		// ‘—ŽóM
		err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(22);
		//if( State.ErrCode != ErrCode.RET_OK ) {
		if( judgeErrLevel(State)  >= EMERGENCY_ERR_LEVEL ) {
			return State;
		}
		param.LpCycle       = Frame.getResponseShortData( 0 );
		param.ExposureRed   = Frame.getResponseShortData( 2 );
		param.ExposureGreen = Frame.getResponseShortData( 4 );
		param.ExposureBlue  = Frame.getResponseShortData( 6 );
		param.OpenRed       = Frame.getResponseShortData( 8 );
		param.OpenGreen     = Frame.getResponseShortData( 10 );
		param.OpenBlue      = Frame.getResponseShortData( 12 );
		param.CloseRed      = Frame.getResponseShortData( 14 );
		param.CloseGreen    = Frame.getResponseShortData( 16 );
		param.CloseDummy    = Frame.getResponseShortData( 18 );
		param.ExposureDummy = Frame.getResponseShortData( 20 );
		
		return State;
	}
*/
	/**
	 * getMovementAndMotorSpeedAndEnergisationMode(0xE2)
	 * @param param
	 * @return
	 */
/*
	InstaxStatus getMovementAndMotorSpeedAndEnergisationMode(InstaxPrintSetting param) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		ErrCode err = ErrCode.RET_OK;
		int DataSize = 0;
		byte[] Data = new byte[DataSize];
		short Sid = 0xE2;
		
		// ƒf[ƒ^ì¬
		Frame.CreateSndData( UID, Sid, Data, DataSize );
		// ‘—ŽóM
		err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^Žæ“¾
		State = Frame.checkResponseFrame(28);
		//if( State.ErrCode != ErrCode.RET_OK ) {
		if( judgeErrLevel(State)  >= EMERGENCY_ERR_LEVEL ) {
			return State;
		}
		param.BeforeWhite    = Frame.getResponseShortData( 0 );
		param.AfterWhite     = Frame.getResponseShortData( 2 );
		param.StepPerLine    = Frame.getResponseShortData( 4 );
		param.OrgToPiStep    = Frame.getResponseShortData( 6 );
		param.SpeedUpStep    = Frame.getResponseShortData( 8 );
		param.SpeedDnStep    = Frame.getResponseShortData( 10 );
		param.MainStep       = Frame.getResponseShortData( 12 );
		param.AfterPrintStep = Frame.getResponseShortData( 14 );
		param.ReturnStep     = Frame.getResponseShortData( 16 );
		param.LowSpeed       = Frame.getResponseShortData( 18 );
		param.PrintSpeed     = Frame.getResponseShortData( 20 );
		param.ReturnSpeed    = Frame.getResponseShortData( 22 );
		param.Excitation     = Frame.getResponseByteData( 24 );
		param.LedOpen        = Frame.getResponseByteData( 25 );
		
		return State;
	}
*/
	/**
	 * GetFunctionVersion(0xC4)
	 * @param FuncMap
	 * @return
	 */
	//InstaxStatus GetFunctionVersion(int functionId, Integer version) {
	InstaxStatus GetFunctionVersion(Map<Integer, Integer> FuncMap) {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		ErrCode err = ErrCode.RET_OK;
		int DataSize = 4;
		byte[] Data = new byte[DataSize];
		short Sid = 0xC4;
		
		for(Integer key : FuncMap.keySet()) {
			// ƒf[ƒ^ì¬
			Data[0] = 0x00;
			Data[1] = 0x00;
			Data[2] = (byte)((key.intValue() >> 8) & 0xFF);
			Data[3] = (byte)((key.intValue() >> 0) & 0xFF);
			Frame.CreateSndData( UID, Sid, Data, DataSize, Param.Password );
			// ‘—ŽóM
			err = Sock.SndRcvData( Param, Frame );
			if( err != ErrCode.RET_OK) {
				State.setInstaxStatus(InstaxState.ST_BUSY,err);
				return State;
			}
			// ŽóMƒf[ƒ^Žæ“¾
			State = Frame.checkResponseFrame(4);
			//if( State.ErrCode != ErrCode.RET_OK ) {
			if( judgeErrLevel(State)  >= EMERGENCY_ERR_LEVEL ) {
				return State;
			}
			FuncMap.put(key, Integer.valueOf(Frame.getResponseShortData(2)));
		}
		return State;
	}

	/**
	 * InstaxPowerOff(0xB4)
	 * @return
	 */
	InstaxStatus InstaxPowerOff() {
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		NetworkCommunicationParam PowerOffParam = new NetworkCommunicationParam();
		ErrCode err = ErrCode.RET_OK;
		int DataSize = 4;
		byte[] Data = new byte[DataSize];
		short Sid = 0xB4;
		
		// PowerOff‚ÍƒŒƒXƒ|ƒ“ƒX‚ð‘Ò‚½‚È‚¢‚Ì‚Å‘¼ƒRƒ}ƒ“ƒh‚Æ‚Íƒpƒ‰ƒ[ƒ^[‚ð•ÏX‚·‚é
		PowerOffParam.setNetworkParam(Param.IpAddress, Param.Port);
		PowerOffParam.setCommunicationParam(Param.SndTimeOut, 0, 0);
		PowerOffParam.setPassword(Param.Password);
	
		// ƒf[ƒ^ì¬
		Data[0] = 0x00;
		Data[1] = 0x00;
		Data[2] = 0x00;
		Data[3] = 0x00;
		Frame.CreateSndData( UID, Sid, Data, DataSize, PowerOffParam.Password );
		
		// ‘—M‚ªŠ®—¹‚µ‚½Žž“_‚Å”²‚¯‚é‚½‚ß‚É‚±‚Ì‘w‚ÅƒŠƒgƒ‰ƒCˆ—‚ðs‚¤
		for(int i=0; i<=Param.RetryCnt; i++) {
			// ‘—ŽóM
			err = Sock.SndRcvData( PowerOffParam, Frame );
			if( err == ErrCode.RET_OK || err == ErrCode.E_RCV_TIMEOUT ) {	//ƒ^ƒCƒ€ƒAƒEƒg‚ÍƒXƒ‹[
				State.setInstaxStatus(InstaxState.ST_IDLE, ErrCode.RET_OK);
				break;
			}
			else {
				State.setInstaxStatus(InstaxState.ST_BUSY,err);
			}
		}
		
		// ŽóMƒf[ƒ^Žæ“¾
		//State = Frame.checkResponseFrame(0);

		return State;
	}
	
	/**
	 * 
	 * @param stat
	 * @return
	 */
	int judgeErrLevel(InstaxStatus stat) {
		int ret;

		// 0:ƒGƒ‰[‚È‚µ
		// 1:ˆ—Œp‘±
		// 2:ˆ—’†Ž~
		
		//@’è”‚¶‚á‚È‚¢‚Ì‚Åswitch•¶–³—
		if( stat.ErrCode == ErrCode.RET_OK ) {
			ret = 0;
		}
		else if( stat.ErrCode == ErrCode.E_FILM_EMPTY 
				|| stat.ErrCode == ErrCode.E_BATTERY_EMPTY
				|| stat.ErrCode == ErrCode.E_CAM_POINT
				|| stat.ErrCode == ErrCode.E_MOTOR 
				|| stat.ErrCode == ErrCode.E_COVER_OPEN
				|| stat.ErrCode == ErrCode.E_PI_SENSOR) {
			ret = 1;
		}
		else {
			ret = 2;
		}
		return ret;
	}

	/**
	 * 
	 * @param newPassword
	 * @param oldPassword
	 * @return
	 */
	InstaxStatus InstaxChangePassword(int newPassword, int oldPassword) {
		InstaxLog.putLogToSD("NewPass:"+String.valueOf(newPassword));
		InstaxLog.putLogToSD("OldPass:"+String.valueOf(oldPassword));
		// ‹ŒƒpƒXƒ[ƒh‚ª•sˆê’v
		//if( Param.Password != oldPassword && Param.Password != -1 ) {
		//	State.setInstaxStatus(InstaxState.ST_BUSY,ErrCode.E_UNMATCH_PASS);
		//	return State;
		//}
		
		NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		ErrCode err = ErrCode.RET_OK;
		int DataSize = 4;
		byte[] Data = new byte[DataSize];
		short Sid = 0xB6;
		
		// ƒf[ƒ^ì¬
		Data[0] = 0x00;
		Data[1] = 0x00;
		Data[2] = (byte)((newPassword >> 8) & 0xFF);
		Data[3] = (byte)((newPassword >> 0) & 0xFF);
		Frame.CreateSndData( UID, Sid, Data, DataSize, oldPassword );
		// ‘—ŽóM
		err = Sock.SndRcvData( Param, Frame );
		if( err != ErrCode.RET_OK) {
			State.setInstaxStatus(InstaxState.ST_BUSY,err);
			return State;
		}
		// ŽóMƒf[ƒ^ƒ`ƒFƒbƒN
		State = Frame.checkResponseFrame(0);
		if( judgeErrLevel(State) >= EMERGENCY_ERR_LEVEL ) {
			return State;
		}
		
		// ƒpƒXƒ[ƒh•ÏX
		Param.setPassword(newPassword);
		return State;
	}

	/**
	 * 
	 * @param filePath
	 * @return
	 */
	boolean InstaxSetOutputLog(String filePath) {
		return InstaxLog.SetLogPath(filePath);
	}
}

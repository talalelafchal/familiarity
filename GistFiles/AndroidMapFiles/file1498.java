package com.Instax.sdk;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.channels.IllegalBlockingModeException;
import java.util.Arrays;

import android.util.Log;

import com.Instax.sdk.InstaxStatus.ErrCode;
import com.Instax.sdk.InstaxStatus.InstaxState;

class NetworkCommunicationSndImageThread extends Thread {
    private NetworkCommunicationParam param;
    private byte[] AllSndData;			// ‘—M‚·‚é‘Sƒf[ƒ^
    private InstaxCallBack sndCallback;	// ‘—MŠ®—¹ƒR[ƒ‹ƒoƒbƒN
    private InstaxCallBack cancelCallback;	// ‘—M’†Ž~ƒR[ƒ‹ƒoƒbƒN
    private int SndMaxSize;				// ‚P‰ñ‚É‘—‚éÅ‘åƒTƒCƒY
    private short Sid;
    private int UID;
    volatile private int Prog;			// i’»
    volatile private boolean cancelFlg;	// ƒLƒƒƒ“ƒZƒ‹ƒtƒ‰ƒO
    private int DelayTime;

    /**
     * 
     * @param param
     * @param sid
     * @param sndData
     * @param length
     * @param sndMaxSize
     * @param sndFinish
     */
    NetworkCommunicationSndImageThread(NetworkCommunicationParam param, int uid, short sid, byte[] sndData, int sndMaxSize, InstaxCallBack sndFinish) {
        this.param = param;
        this.AllSndData= sndData; 
        this.sndCallback = sndFinish;
        this.SndMaxSize = sndMaxSize;
        this.Sid = sid;
        this.Prog = 0;
        this.cancelFlg = false;
        this.cancelCallback = null;
        this.UID = uid;
        this.DelayTime = param.DelayTime;	// ms
    }

    /**
     * getProgress
     * @return
     */
    int getProgress() {
    	return Prog;
    }
    
    /**
     * 
     * @param cancelFinish
     */
    void SndImageCancel(InstaxCallBack cancelFinish) {
    	cancelCallback = cancelFinish;
		cancelFlg = true;
	}
    
    /**
     * 
     * @return
     */
    /*
    private InstaxStatus ModeReset() {
    	InstaxStatus state = new InstaxStatus();
    	NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
		int DataSize = 1;
		byte[] Data = new byte[DataSize];
		short Sid = 0xB3;
		ErrCode err = ErrCode.RET_OK;
		
		// ‘—ŽóM
		Data[0] = 0;
		Frame.CreateSndData( UID, Sid, Data, DataSize );
		err = Sock.SndRcvData( param, Frame );
		if( err != ErrCode.RET_OK ) {
			state.setInstaxStatus(InstaxState.ST_BUSY, err);
			return state;
		}
		// ŽóMƒf[ƒ^ƒ`ƒFƒbƒN
		state = Frame.checkResponseFrame(0);

		return state;
	}
    */
    // 0x51
    private InstaxStatus TransferStart() {
    	InstaxStatus state = new InstaxStatus();
    	NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
 		int DataSize = 12;
 		byte[] Data = new byte[DataSize];
 		int imgSize = AllSndData.length;
 		short Sid = 0x51;
 		ErrCode err = ErrCode.RET_OK;
 		
 		Data[0] = (byte)param.IMAGE_Format;		// ‰æ‘œƒtƒH[ƒ}ƒbƒg
 		Data[1] = (byte)param.IMAGE_Option;		// pic opt
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
 		Frame.CreateSndData( UID, Sid, Data, DataSize, param.Password );
 		err = Sock.SndRcvData( param, Frame );
 		if( err != ErrCode.RET_OK ) {
 			state.setInstaxStatus(InstaxState.ST_BUSY, err);
 			return state;
 		}
 		state = Frame.checkResponseFrame( 0 );
 		
 		return state;
 	}
 		
    // 0x53
    private InstaxStatus TransferEnd() {
    	InstaxStatus state = new InstaxStatus();
    	NetworkCommunication Sock = new NetworkCommunication();
		NetworkFrameData Frame = new NetworkFrameData();
 		int DataSize = 0;
 		byte[] Data = new byte[DataSize];
 		short Sid = 0x53;
 		ErrCode err = ErrCode.RET_OK;
 		
 		// ‘—ŽóM
 		Frame.CreateSndData( UID, Sid, Data, DataSize, param.Password );
 		err = Sock.SndRcvData( param, Frame );
 		if( err != ErrCode.RET_OK ) {
 			state.setInstaxStatus(InstaxState.ST_BUSY, err);
 			return state;
 		}
 		//state = Frame.checkResponseFrame( 4 );
 		state = Frame.checkResponseFrame( 0 );
 		//if( state.ErrCode != ErrCode.RET_OK ) {
		//	return state;
		//}
		// ‰æ‘œƒf[ƒ^ƒ`ƒFƒbƒNƒTƒ€‚È‚ñ‚Ä‚à‚Ì‚Í‚È‚©‚Á‚½
		//int picSum = 0;
        //for(int i=0; i<AllSndData.length; i++) {
        //	picSum += ((int)AllSndData[i] & 0xFF);
        //}
        //picSum = picSum & 0xFFFFFFFF;
		//if( Frame.getResponselongData(0) != picSum ) {
		//	// ‰æ‘œƒf[ƒ^ƒ`ƒFƒbƒNƒTƒ€•sˆê’v
		//	state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_RCV_FRAME);
		//	Log.e("thread", "picSum Error:"+String.valueOf(Frame.getResponseShortData(0)));
		//	Log.e("thread", "picSum Error:"+String.valueOf(picSum));
		//}
 		return state;
 	}
    
	@Override
    public void run() {
		Log.d("thread", "thread start");
		InstaxStatus state = new InstaxStatus();
		NetworkFrameData Frame = new NetworkFrameData();
		Socket socket;
		OutputStream os;
		InputStream is;
		int i, loop, lastSize, seqCnt;
		byte[] data = new byte[SndMaxSize+4];	// ƒV[ƒPƒ“ƒXƒf[ƒ^•ª+4
		boolean finishFlg = true;
		ErrCode err = ErrCode.RET_OK;
		boolean RcvFlg = false;
		
		loop = (int)(AllSndData.length/SndMaxSize);
		lastSize = (int)(AllSndData.length%SndMaxSize);
		
		Log.d("thread","AllSndDataLength:"+String.valueOf(AllSndData.length));
		Log.d("thread","SndMaxSize:"+String.valueOf(SndMaxSize));
		Log.d("thread","loop:"+String.valueOf(loop));
		Log.d("thread","lastSize:"+String.valueOf(lastSize));
		Log.d("thread", "SendTimeout:"+String.valueOf(param.SndTimeOut));
		Log.d("thread", "RecvTimeout:"+String.valueOf(param.RcvTimeOut));
		Log.d("thread", "Retry:"+String.valueOf(param.RetryCnt));
				
		seqCnt = 0;
		Prog = 0;
		// ‘—MƒLƒƒƒ“ƒZƒ‹
		if( cancelFlg == true && cancelCallback != null )
		{
			Log.d("thread", "SndImageCanncel");
			state.setInstaxStatus(InstaxState.ST_IDLE, ErrCode.RET_OK);
			cancelCallback.FinishCallBack(state);
			//ModeReset();
			return;
		}
		// ˜A‘±‘—M
		// Nullƒ`ƒFƒbƒN
        if( param == null || Frame == null ) {
        	Log.e("thread", "NullObject");
    		state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_CONNECT);
    		sndCallback.FinishCallBack(state);
    		//ModeReset();
    		return;
        }
        
        // 0x51 ‘—M
        state = TransferStart();
        if( state.InstaxState != InstaxState.ST_IDLE || state.ErrCode != ErrCode.RET_OK ) {
        	sndCallback.FinishCallBack(state);
        	//ModeReset();
    		return;
        }
        
		try {
			// ƒ\ƒPƒbƒgì¬
			socket = new Socket();
			// Ú‘±
			socket.connect(new InetSocketAddress(param.IpAddress,param.Port),param.SndTimeOut);
			os = socket.getOutputStream();
    		is = socket.getInputStream();
    		Log.d("thread", "Connect Socket");
			
    		// 0x52‘—M
			for( i=0; i<loop; i++) {
				if( cancelFlg == true ) {
					Log.d("thread", "SndImageCanncel");
					break;
				}
				data[0] = (byte)((seqCnt >> 24) & 0xFF);
				data[1] = (byte)((seqCnt >> 16) & 0xFF);
				data[2] = (byte)((seqCnt >> 8) & 0xFF);
				data[3] = (byte)((seqCnt >> 0) & 0xFF);
				System.arraycopy(AllSndData, i*SndMaxSize, data, 4, SndMaxSize);
				Frame.CreateSndData(UID, Sid, data, SndMaxSize+4, param.Password);
///////////////////////////////////////////////////////////////////////////////////////////////////////////
// ŠÖ”‰»‚·‚é‚Æsocket’ÊM‚ª‚¤‚Ü‚­‚¢‚©‚È‚­‚È‚é‚Ì‚Åƒxƒ^‘‚«‚·‚é
				for( int retry=0; retry<=param.RetryCnt; retry++) {
					if( cancelFlg == true ) {
						Log.d("thread", "SndImageCanncel");
						break;
					}
	        		// ƒf[ƒ^‘—M
					if( !socket.isConnected() ) {
						Log.d("thread", "socket disconnect");
						// ƒ\ƒPƒbƒgì¬
						socket = new Socket();
						// Ú‘±
						socket.connect(new InetSocketAddress(param.IpAddress,param.Port),param.SndTimeOut);
						os = socket.getOutputStream();
			    		is = socket.getInputStream();
					}
	        		os.write(Frame.getSndData(), 0, Frame.getSndSize());
	        		os.flush();
		   
	        		// “Ç‚Ýž‚Þ‚Ü‚Å‘Ò‹@
	        		long s = System.currentTimeMillis();
	        		while(true) {
	        			if( is.available() != 0 ) {
	        				// ŽóM
	        				RcvFlg = true;
	        				break;
	        			}
	        			if( (System.currentTimeMillis() - s) >= param.RcvTimeOut ) {
	        				// ŽóMƒ^ƒCƒ€ƒAƒEƒg
	        				RcvFlg = false;
	        				os.close();
	        				is.close();
	        				socket.close();
	        				socket = new Socket();
							// ÄÚ‘±
							socket.connect(new InetSocketAddress(param.IpAddress,param.Port),param.SndTimeOut);
							os = socket.getOutputStream();
				    		is = socket.getInputStream();
	        				break;
	        			}
	        		}
		        	if( RcvFlg == true ) {
		        		// “Ç‚Ýž‚ñ‚¾‚à‚Ì‚ð‘‚«‚¾‚·
		        		Frame.setRcvSize(is.available());
		        		Frame.setRcvData(new byte[Frame.getRcvSize()]);
		        		is.read(Frame.getRcvData(), 0, Frame.getRcvSize());
		        	}
	        		if( RcvFlg == false ) {
	        			Log.e("SndRcvData", "RcvTimeOut");
	        			err = ErrCode.E_RCV_TIMEOUT;
	        			continue;
	        		}
		        	err = ErrCode.RET_OK;
		        	break;
		        }// retry
///////////////////////////////////////////////////////////////////////////////////////////////////////////
				if( err != ErrCode.RET_OK ) {
					// ‘—ŽóMƒGƒ‰[
					finishFlg = false;
					state.setInstaxStatus(InstaxState.ST_BUSY, err);
					Log.e("thread", "SndRcvData Error:"+err.toString());
					break;
				}
				state = Frame.checkResponseFrame(4);
				if( state.ErrCode != ErrCode.RET_OK ) {
					// ƒŒƒXƒ|ƒ“ƒXƒGƒ‰[
					finishFlg = false;
					state.setInstaxStatus(InstaxState.ST_BUSY, state.ErrCode);
					Log.e("thread", "Response Error:"+state.ErrCode.toString());
					break;
				}
				if( Frame.getResponselongData(0) != seqCnt ) {
					// ƒV[ƒPƒ“ƒX•sˆê’v
					finishFlg = false;
					state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_RCV_FRAME);
					Log.e("thread", "Sequence Error:"+String.valueOf(seqCnt));
					break;
				}
				//Log.d("thread", "Sequence:"+String.valueOf(seqCnt));
				seqCnt++;
				if( lastSize > 0 ) {
					Prog = (i*100)/(loop+1);		// i’»—¦ŒvŽZ
				}
				else {
					Prog = (i*100)/(loop);
				}
				if( DelayTime > 0 ) {
					Thread.sleep(DelayTime);
				}
			}// loop
			
			// —]‚è‘—M
			if( finishFlg == true && lastSize > 0 && cancelFlg == false ) {
				Arrays.fill(data, (byte)0x00);
				data[0] = (byte)((seqCnt >> 24) & 0xFF);
				data[1] = (byte)((seqCnt >> 16) & 0xFF);
				data[2] = (byte)((seqCnt >> 8) & 0xFF);
				data[3] = (byte)((seqCnt >> 0) & 0xFF);
				System.arraycopy(AllSndData, i*SndMaxSize, data, 4, lastSize);
				//Frame.CreateSndData(UID, Sid, data, lastSize+4);
				// ’[”‚ª‚ ‚Á‚½ê‡‚Í’[”•ª‚¾‚¯‘—‚Á‚Ä‚¢‚½‚ª0–„‚ß‚ÅƒtƒŒ[ƒ€ƒTƒCƒY‚ð‡‚í‚¹‚é‚±‚Æ‚É‚È‚Á‚½
				Frame.CreateSndData(UID, Sid, data, SndMaxSize+4, param.Password);
///////////////////////////////////////////////////////////////////////////////////////////////////////////
// ŠÖ”‰»‚·‚é‚Æsocket’ÊM‚ª‚¤‚Ü‚­‚¢‚©‚È‚­‚È‚é‚Ì‚Åƒxƒ^‘‚«‚·‚é
				for( int retry=0; retry<=param.RetryCnt; retry++) {
					if( cancelFlg == true ) {
						Log.d("thread", "SndImageCanncel");
						break;
					}
					// ƒf[ƒ^‘—M
					if( !socket.isConnected() ) {
						// ƒ\ƒPƒbƒgì¬
						socket = new Socket();
						// Ú‘±
						socket.connect(new InetSocketAddress(param.IpAddress,param.Port),param.SndTimeOut);
						os = socket.getOutputStream();
			    		is = socket.getInputStream();
					}
					os.write(Frame.getSndData(), 0, Frame.getSndSize());
					os.flush();

					// “Ç‚Ýž‚Þ‚Ü‚Å‘Ò‹@
					long s = System.currentTimeMillis();
					while(true) {
						if( is.available() != 0 ) {
							// ŽóM
							RcvFlg = true;
							break;
						}
						if( (System.currentTimeMillis() - s) >= param.RcvTimeOut ) {
							// ŽóMƒ^ƒCƒ€ƒAƒEƒg
							RcvFlg = false;
							os.close();
	        				is.close();
	        				socket.close();
	        				socket = new Socket();
							// ÄÚ‘±
							socket.connect(new InetSocketAddress(param.IpAddress,param.Port),param.SndTimeOut);
							os = socket.getOutputStream();
				    		is = socket.getInputStream();
				    		break;
						}
					}
					if( RcvFlg == true ) {
						// “Ç‚Ýž‚ñ‚¾‚à‚Ì‚ð‘‚«‚¾‚·
						Frame.setRcvSize(is.available());
						Frame.setRcvData(new byte[Frame.getRcvSize()]);
						is.read(Frame.getRcvData(), 0, Frame.getRcvSize());
					}
					if( RcvFlg == false ) {
						Log.e("SndRcvData", "RcvTimeOut");
						err = ErrCode.E_RCV_TIMEOUT;
						continue;
					}
					err = ErrCode.RET_OK;
					break;
				}// retry
///////////////////////////////////////////////////////////////////////////////////////////////////////////
				if( err != ErrCode.RET_OK ) {
					// ‘—ŽóMƒGƒ‰[
					finishFlg = false;
					state.setInstaxStatus(InstaxState.ST_BUSY, err);
					Log.e("thread", "SndRcvData Error:"+err.toString());
				}
				state = Frame.checkResponseFrame(4); 
				if( state.ErrCode != ErrCode.RET_OK ) {
					// ƒŒƒXƒ|ƒ“ƒXƒGƒ‰[
					finishFlg = false;
					state.setInstaxStatus(InstaxState.ST_BUSY, state.ErrCode);
					Log.e("thread", "Response Error:"+state.ErrCode.toString());
				}
				if( Frame.getResponselongData(0) != seqCnt ) {
					// ƒV[ƒPƒ“ƒX•sˆê’v
					finishFlg = false;
					state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_RCV_FRAME);
					Log.e("thread", "Sequence Error:"+String.valueOf(seqCnt));
				}
				if( DelayTime > 0 ) {
					Thread.sleep(DelayTime);
				}
			}// 0x52‘—M
			
			// Ø’f
			os.close();
			is.close();
			socket.close();
			
			// 0x53 ‘—M
			if( finishFlg == true && cancelFlg == false ) {
				state = TransferEnd();
				if( state.InstaxState != InstaxState.ST_IDLE || state.ErrCode != ErrCode.RET_OK ) {
					finishFlg = false;
				}
			}
		} catch (SocketTimeoutException e) {		// ƒ\ƒPƒbƒgì¬Ž¸”s
			InstaxLog.putLogToSD("SocketTimeoutException");
    		Log.e("thread", "SocketTimeOut");
    		state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_MAKE_SOCKET);
    		finishFlg = false;
    	} catch (IllegalBlockingModeException e) {	// ƒCƒŠ[ƒKƒ‹ƒGƒ‰[
    		InstaxLog.putLogToSD("IllegalBlockingModeException");
    		Log.e("thread","IllegalBlockingMode");
    		state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_CONNECT);
    		finishFlg = false;
    	} catch (IllegalArgumentException e) {
    		InstaxLog.putLogToSD("IllegalArgumentException");
    		Log.e("thread", "IllegalArgument");	// ˆø”ˆÙí
    		state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_CONNECT);
    		finishFlg = false;
    	} catch (UnknownHostException e) {
    		InstaxLog.putLogToSD("UnknownHostException");
    		Log.e("thread", "UnknownHost");		// ƒzƒXƒg‚ªŒ©‚Â‚©‚ç‚È‚¢
    		state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_CONNECT);
    		finishFlg = false;
    	} catch (InterruptedException e) {
    		InstaxLog.putLogToSD("InterruptedException");
    		Log.e("thread", "InterruptedException");		// ƒzƒXƒg‚ªŒ©‚Â‚©‚ç‚È‚¢
    		state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_CONNECT);
    		finishFlg = false;
    	} catch (IOException e) {					// ‚»‚Ì‘¼
    		InstaxLog.putLogToSD("IOException " + e.getMessage());
    		Log.e("thread", "IOException " + e.getMessage());
    		state.setInstaxStatus(InstaxState.ST_BUSY, ErrCode.E_CONNECT);
    		finishFlg = false;
    	}
		// ƒXƒe[ƒ^ƒXƒZƒbƒg
		if( cancelFlg == true && cancelCallback != null ) {
			Prog = 0;
			state.setInstaxStatus(InstaxState.ST_IDLE, ErrCode.RET_OK);
			cancelCallback.FinishCallBack(state);
			Log.d("thread", "SndImageCanncel");
			//ModeReset();
			return;
		}
		if( finishFlg == true ) {
			Prog = 100;
			state.setInstaxStatus(InstaxState.ST_IDLE, ErrCode.RET_OK);
		}
		else {
			Prog = 0;
			//ModeReset();
		}
		// ƒR[ƒ‹ƒoƒbƒN
		sndCallback.FinishCallBack( state );
        Log.d("thread", "thread end");
    }
}
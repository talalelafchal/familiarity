package com.wificar.component;

import android.util.Log;
import com.wificar.WificarActivity;
import com.wificar.util.BlowFish;
import com.wificar.util.ByteUtility;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.http.util.ByteArrayBuffer;

public class CommandEncoder
{
  public static final int AUDIO_DATA = 2;
  public static final int AUDIO_END = 10;
  public static final int AUDIO_START_REQ = 8;
  public static final int AUDIO_START_RESP = 9;
  public static final int DECODER_CONTROL_REQ = 14;
  public static final int DEVICE_CONTROL_REQ = 250;
  public static final int FETCH_BATTERY_POWER_REQ = 251;
  public static final int FETCH_BATTERY_POWER_RESP = 252;
  public static final int HEAD_LEN = 23;
  public static final int KEEP_ALIVE = 255;
  public static final int LOGIN_REQ = 0;
  public static final int LOGIN_RESP = 1;
  public static final int MEDIA_LOGIN_REQ = 0;
  public static final int TALK_DATA = 3;
  public static final int TALK_END = 13;
  public static final int TALK_START_REQ = 11;
  public static final int TALK_START_RESP = 12;
  public static final int VERIFY_REQ = 2;
  public static final int VERIFY_RESP = 3;
  public static final int VIDEO_DATA = 1;
  public static final int VIDEO_END = 6;
  public static final int VIDEO_FRAMEINTERVAL = 7;
  public static final int VIDEO_START_REQ = 4;
  public static final int VIDEO_START_RESP = 5;
  public static final String WIFICAR_OP = "MO_O";
  public static final String WIFICAR_VIDEO_OP = "MO_V";

  public static String byteArrayToHex(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[paramInt2];
    for (int i = 0; ; i++)
    {
      if (i >= paramInt2)
        return ByteUtility.bytesToHex(arrayOfByte);
      arrayOfByte[i] = paramArrayOfByte[(i + paramInt1)];
    }
  }

  public static int byteArrayToInt(byte[] paramArrayOfByte, int paramInt)
    throws Exception
  {
    return (paramArrayOfByte[(paramInt + 0)] << 24) + ((0xFF & paramArrayOfByte[(paramInt + 1)]) << 16) + ((0xFF & paramArrayOfByte[(paramInt + 2)]) << 8) + (0xFF & paramArrayOfByte[(paramInt + 3)]);
  }

  public static int byteArrayToInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    int i = 0;
    int j = 0;
    if (j >= paramInt2)
      return i;
    if ((j == 0) && (paramArrayOfByte[(paramInt1 + (paramInt2 - 1) - j)] < 0))
      i |= 0xFFFFFFFF & paramArrayOfByte[(paramInt1 + (paramInt2 - 1) - j)];
    while (true)
    {
      if (j < paramInt2 - 1)
        i <<= 8;
      j++;
      break;
      i |= 0xFF & paramArrayOfByte[(paramInt1 + (paramInt2 - 1) - j)];
    }
  }

  public static long byteArrayToLong(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    long l = 0L;
    int i = 0;
    if (i >= paramInt2)
      return l;
    if ((i == 0) && (paramArrayOfByte[(paramInt1 + (paramInt2 - 1) - i)] < 0));
    for (l |= 0xFFFFFFFF & paramArrayOfByte[(paramInt1 + (paramInt2 - 1) - i)]; ; l |= 0xFF & paramArrayOfByte[(paramInt1 + (paramInt2 - 1) - i)])
    {
      if (i < paramInt2 - 1)
        l <<= 8;
      i++;
      break;
    }
  }

  public static String byteArrayToString(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    byte[] arrayOfByte = new byte[paramInt2];
    for (int i = 0; ; i++)
    {
      if (i >= paramInt2)
        return new String(arrayOfByte).trim();
      arrayOfByte[i] = paramArrayOfByte[(i + paramInt1)];
    }
  }

  public static byte[] cmdAudioEnd()
    throws IOException
  {
    return new Protocol("MO_O".getBytes(), 10, 0, new byte[0]).output();
  }

  public static byte[] cmdAudioStartReq()
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
    localByteBuffer.put(int8ToByteArray(1));
    return new Protocol("MO_O".getBytes(), 8, 1, localByteBuffer.array()).output();
  }

  public static byte[] cmdDataLoginReq(int paramInt)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
    return new Protocol("MO_V".getBytes(), 0, localByteBuffer.capacity(), localByteBuffer.array()).output();
  }

  public static byte[] cmdDecoderControlReq(int paramInt)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
    localByteBuffer.put(int8ToByteArray(paramInt));
    return new Protocol("MO_O".getBytes(), 14, 1, localByteBuffer.array()).output();
  }

  public static byte[] cmdDeviceControlReq(int paramInt1, int paramInt2)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(2);
    localByteBuffer.put(int8ToByteArray(paramInt1));
    localByteBuffer.put(int8ToByteArray(paramInt2));
    return new Protocol("MO_O".getBytes(), 250, 2, localByteBuffer.array()).output();
  }

  public static byte[] cmdFetchBatteryPowerReq()
    throws IOException
  {
    return new Protocol("MO_O".getBytes(), 251, 0, new byte[0]).output();
  }

  public static byte[] cmdKeepAlive()
    throws IOException
  {
    return new Protocol("MO_O".getBytes(), 255, 0, new byte[0]).output();
  }

  public static byte[] cmdLoginReq(int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IOException
  {
    byte[] arrayOfByte1 = int32ToByteArray(paramInt1);
    byte[] arrayOfByte2 = int32ToByteArray(paramInt2);
    byte[] arrayOfByte3 = int32ToByteArray(paramInt3);
    byte[] arrayOfByte4 = int32ToByteArray(paramInt4);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localByteArrayOutputStream.write(arrayOfByte1);
    localByteArrayOutputStream.write(arrayOfByte2);
    localByteArrayOutputStream.write(arrayOfByte3);
    localByteArrayOutputStream.write(arrayOfByte4);
    return new Protocol("MO_O".getBytes(), 0, 16, localByteArrayOutputStream.toByteArray()).output();
  }

  public static byte[] cmdMediaLoginReq(int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = int32ToByteArray(paramInt);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localByteArrayOutputStream.write(arrayOfByte);
    return new Protocol("MO_V".getBytes(), 0, localByteArrayOutputStream.size(), localByteArrayOutputStream.toByteArray()).output();
  }

  public static byte[] cmdTalkEnd()
    throws IOException
  {
    return new Protocol("MO_O".getBytes(), 13, 1, new byte[0]).output();
  }

  public static byte[] cmdTalkStartReq(int paramInt)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(1);
    localByteBuffer.put(int8ToByteArray(paramInt));
    return new Protocol("MO_O".getBytes(), 11, 1, localByteBuffer.array()).output();
  }

  public static byte[] cmdVerifyReq(String paramString, int paramInt1, int paramInt2, int paramInt3, int paramInt4)
    throws IOException
  {
    Log.d("wild0", "cmdVerifyReq");
    BlowFish localBlowFish = new BlowFish();
    int[] arrayOfInt1 = { paramInt1 };
    int[] arrayOfInt2 = { paramInt2 };
    int[] arrayOfInt3 = { paramInt3 };
    int[] arrayOfInt4 = { paramInt4 };
    localBlowFish.InitBlowfish(paramString.getBytes(), paramString.length());
    localBlowFish.Blowfish_encipher(arrayOfInt1, arrayOfInt2);
    localBlowFish.Blowfish_encipher(arrayOfInt3, arrayOfInt4);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localByteArrayOutputStream.write(int32ToByteArray(arrayOfInt1[0]));
    localByteArrayOutputStream.write(int32ToByteArray(arrayOfInt2[0]));
    localByteArrayOutputStream.write(int32ToByteArray(arrayOfInt3[0]));
    localByteArrayOutputStream.write(int32ToByteArray(arrayOfInt4[0]));
    Protocol localProtocol = new Protocol("MO_O".getBytes(), 2, localByteArrayOutputStream.size(), localByteArrayOutputStream.toByteArray());
    Log.d("wild0", "============================verify");
    return localProtocol.output();
  }

  public static byte[] cmdVideoEnd()
    throws IOException
  {
    return new Protocol("MO_O".getBytes(), 6, 0, new byte[0]).output();
  }

  public static byte[] cmdVideoFrameInterval(int paramInt)
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
    localByteBuffer.put(int32ToByteArray(1));
    return new Protocol("MO_O".getBytes(), 7, localByteBuffer.capacity(), localByteBuffer.array()).output();
  }

  public static byte[] cmdVideoStartReq()
    throws IOException
  {
    ByteBuffer localByteBuffer = ByteBuffer.allocate(4);
    localByteBuffer.put(int8ToByteArray(1));
    return new Protocol("MO_O".getBytes(), 4, 1, localByteBuffer.array()).output();
  }

  public static Protocol createTalkData(TalkData paramTalkData)
    throws IOException
  {
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    byte[] arrayOfByte1 = int32ToByteArray(paramTalkData.getTicktime());
    byte[] arrayOfByte2 = int32ToByteArray(paramTalkData.getSerial());
    byte[] arrayOfByte3 = int32ToByteArray(paramTalkData.getTimestamp());
    byte[] arrayOfByte4 = int8ToByteArray(0);
    byte[] arrayOfByte5 = int32ToByteArray(paramTalkData.getData().length);
    localByteArrayOutputStream.write(arrayOfByte1);
    localByteArrayOutputStream.write(arrayOfByte2);
    localByteArrayOutputStream.write(arrayOfByte3);
    localByteArrayOutputStream.write(arrayOfByte4);
    localByteArrayOutputStream.write(arrayOfByte5);
    localByteArrayOutputStream.write(paramTalkData.getData());
    return new Protocol("MO_V".getBytes(), 3, localByteArrayOutputStream.size(), localByteArrayOutputStream.toByteArray());
  }

  public static int getPrefixCount(byte[] paramArrayOfByte, int paramInt)
  {
    int i = 0;
    for (int j = paramInt; ; j++)
    {
      if (j >= -4 + paramArrayOfByte.length)
        return i;
      if ((paramArrayOfByte[j] == 77) && (paramArrayOfByte[(j + 1)] == 79) && (paramArrayOfByte[(j + 2)] == 95) && (paramArrayOfByte[(j + 3)] == 86))
        i++;
    }
  }

  public static int getPrefixPosition1(byte[] paramArrayOfByte, int paramInt)
  {
    return getPrefixPosition1(paramArrayOfByte, paramInt, paramArrayOfByte.length);
  }

  public static int getPrefixPosition1(byte[] paramArrayOfByte, int paramInt1, int paramInt2)
  {
    for (int i = paramInt1; ; i++)
    {
      if (i >= paramInt2 - 4)
        i = -1;
      do
      {
        return i;
        if (i > paramInt2)
          return -1;
      }
      while ((paramArrayOfByte[i] == 77) && (paramArrayOfByte[(i + 1)] == 79) && (paramArrayOfByte[(i + 2)] == 95) && (paramArrayOfByte[(i + 3)] == 86));
    }
  }

  public static byte[] int16ToByteArray(int paramInt)
  {
    byte[] arrayOfByte = new byte[2];
    for (int i = 0; ; i++)
    {
      if (i >= 2)
        return arrayOfByte;
      arrayOfByte[i] = ((byte)(0xFF & paramInt >>> i * 8));
    }
  }

  public static byte[] int32ToByteArray(int paramInt)
  {
    byte[] arrayOfByte = new byte[4];
    for (int i = 0; ; i++)
    {
      if (i >= 4)
        return arrayOfByte;
      arrayOfByte[i] = ((byte)(0xFF & paramInt >>> i * 8));
    }
  }

  public static byte[] int32ToByteArrayR(int paramInt)
  {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = ((byte)(0xFF & paramInt >> 24));
    arrayOfByte[1] = ((byte)(0xFF & paramInt >> 16));
    arrayOfByte[2] = ((byte)(0xFF & paramInt >> 8));
    arrayOfByte[3] = ((byte)(0xFF & paramInt >> 0));
    return arrayOfByte;
  }

  public static String int32ToByteHex(int paramInt)
  {
    byte[] arrayOfByte = new byte[4];
    for (int i = 0; ; i++)
    {
      if (i >= 4)
        return ByteUtility.bytesToHex(arrayOfByte);
      arrayOfByte[i] = ((byte)(0xFF & paramInt >>> i * 8));
    }
  }

  public static String int32ToByteHexR(int paramInt)
  {
    byte[] arrayOfByte = new byte[4];
    arrayOfByte[0] = ((byte)(0xFF & paramInt >> 24));
    arrayOfByte[1] = ((byte)(0xFF & paramInt >> 16));
    arrayOfByte[2] = ((byte)(0xFF & paramInt >> 8));
    arrayOfByte[3] = ((byte)(0xFF & paramInt >> 0));
    return ByteUtility.bytesToHex(arrayOfByte);
  }

  public static byte[] int8ToByteArray(int paramInt)
  {
    byte[] arrayOfByte = new byte[1];
    for (int i = 0; ; i++)
    {
      if (i >= 1)
        return arrayOfByte;
      arrayOfByte[i] = ((byte)(0xFF & paramInt >>> i * 8));
    }
  }

  public static byte[] longToByteArray(long paramLong)
  {
    byte[] arrayOfByte = new byte[8];
    arrayOfByte[0] = ((byte)(int)(0xFF & paramLong >> 56));
    arrayOfByte[1] = ((byte)(int)(0xFF & paramLong >> 48));
    arrayOfByte[2] = ((byte)(int)(0xFF & paramLong >> 40));
    arrayOfByte[3] = ((byte)(int)(0xFF & paramLong >> 32));
    arrayOfByte[4] = ((byte)(int)(0xFF & paramLong >> 24));
    arrayOfByte[5] = ((byte)(int)(0xFF & paramLong >> 16));
    arrayOfByte[6] = ((byte)(int)(0xFF & paramLong >> 8));
    arrayOfByte[7] = ((byte)(int)(0xFF & paramLong >> 0));
    return arrayOfByte;
  }

  public static void parseAudioData(WifiCar paramWifiCar, byte[] paramArrayOfByte)
  {
    paramWifiCar.enableAudioFlag();
    byte[] arrayOfByte = new Protocol(paramArrayOfByte, 0).getContent();
    int i = byteArrayToInt(arrayOfByte, 0, 4);
    int j = byteArrayToInt(arrayOfByte, 4, 4);
    int k = byteArrayToInt(arrayOfByte, 8, 4);
    int m = byteArrayToInt(arrayOfByte, 12, 1);
    int n = byteArrayToInt(arrayOfByte, 13, 4);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localByteArrayOutputStream.write(arrayOfByte, 17, n);
    int i1 = byteArrayToInt(arrayOfByte, n + 17, 2);
    int i2 = byteArrayToInt(arrayOfByte, n + 19, 1);
    AudioData localAudioData = new AudioData(i, j, k, m, localByteArrayOutputStream.toByteArray(), i1, i2);
    localAudioData.setPCMData(AudioComponent.decodeADPCMToPCM(localByteArrayOutputStream.toByteArray(), localByteArrayOutputStream.size(), i1, i2));
    try
    {
      paramWifiCar.appendAudioDataToFlim(localAudioData);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public static void parseAudioStartResp(WifiCar paramWifiCar, byte[] paramArrayOfByte, int paramInt)
  {
    if (byteArrayToInt(paramArrayOfByte, 0, 2) == 0)
    {
      WificarActivity localWificarActivity = WificarActivity.getInstance();
      WificarActivity.getInstance();
      localWificarActivity.sendMessage(8701);
    }
  }

  public static ByteArrayBuffer parseCommand(WifiCar paramWifiCar, ByteArrayBuffer paramByteArrayBuffer)
    throws IOException
  {
    byte[] arrayOfByte = paramByteArrayBuffer.toByteArray();
    int i;
    if (arrayOfByte.length > 23)
    {
      ByteUtility.byteArrayToInt(arrayOfByte, 4, 2);
      i = ByteUtility.byteArrayToInt(arrayOfByte, 15, 4);
      if (arrayOfByte.length >= i + 23);
    }
    else
    {
      return paramByteArrayBuffer;
    }
    int j = i + 23;
    Protocol localProtocol = new Protocol(arrayOfByte, 0);
    paramByteArrayBuffer.clear();
    paramByteArrayBuffer.append(arrayOfByte, j, arrayOfByte.length - j);
    switch (localProtocol.getOp())
    {
    default:
      return paramByteArrayBuffer;
    case 1:
      parseLoginResp(paramWifiCar, localProtocol.getContent(), 1);
      return paramByteArrayBuffer;
    case 3:
      parseVerifyResp(paramWifiCar, localProtocol.getContent(), 1);
      return paramByteArrayBuffer;
    case 5:
      parseVideoStartResp(paramWifiCar, localProtocol.getContent(), 1);
      return paramByteArrayBuffer;
    case 9:
      parseAudioStartResp(paramWifiCar, localProtocol.getContent(), 1);
      return paramByteArrayBuffer;
    case 12:
      parseTalkStartResp(paramWifiCar, localProtocol.getContent(), 1);
      return paramByteArrayBuffer;
    case 252:
    }
    parseFetchBatteryPowerResp(paramWifiCar, localProtocol.getContent(), 1);
    return paramByteArrayBuffer;
  }

  public static byte[] parseFetchBatteryPowerResp(WifiCar paramWifiCar, byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    if (paramInt == 0);
    for (Protocol localProtocol = new Protocol(paramArrayOfByte, 0); ; localProtocol = new Protocol("MO_O".getBytes(), 252, paramArrayOfByte.length, paramArrayOfByte))
    {
      byteArrayToInt(localProtocol.getContent(), 0, 1);
      return localProtocol.output();
    }
  }

  public static boolean parseLoginResp(WifiCar paramWifiCar, byte[] paramArrayOfByte, int paramInt)
  {
    if (paramInt == 0);
    try
    {
      Protocol localProtocol2 = new Protocol(paramArrayOfByte, 0);
      Object localObject = localProtocol2;
      byte[] arrayOfByte = ((Protocol)localObject).getContent();
      if (byteArrayToInt(arrayOfByte, 0, 2) == 0)
      {
        String str1 = byteArrayToString(arrayOfByte, 2, 13);
        int[] arrayOfInt1 = new int[4];
        for (int i = 0; ; i++)
        {
          if (i >= 4)
          {
            paramWifiCar.setDeviceId(arrayOfInt1[0] + "." + arrayOfInt1[1] + "." + arrayOfInt1[2] + "." + arrayOfInt1[3]);
            paramWifiCar.setCameraId(str1);
            int j = byteArrayToInt(arrayOfByte, 27);
            int k = byteArrayToInt(arrayOfByte, 31);
            int m = byteArrayToInt(arrayOfByte, 35);
            int n = byteArrayToInt(arrayOfByte, 39);
            int i1 = byteArrayToInt(arrayOfByte, 43, 4);
            int i2 = byteArrayToInt(arrayOfByte, 47, 4);
            int i3 = byteArrayToInt(arrayOfByte, 51, 4);
            int i4 = byteArrayToInt(arrayOfByte, 55, 4);
            paramWifiCar.setChallengeReverse(0, i1);
            paramWifiCar.setChallengeReverse(1, i2);
            paramWifiCar.setChallengeReverse(2, i3);
            paramWifiCar.setChallengeReverse(3, i4);
            BlowFish localBlowFish = new BlowFish();
            localBlowFish.InitBlowfish(paramWifiCar.getKey().getBytes(), paramWifiCar.getKey().length());
            int i5 = paramWifiCar.getChallenge(0);
            int i6 = paramWifiCar.getChallenge(1);
            int i7 = paramWifiCar.getChallenge(2);
            int i8 = paramWifiCar.getChallenge(3);
            int[] arrayOfInt2 = { i5 };
            int[] arrayOfInt3 = { i6 };
            int[] arrayOfInt4 = { i7 };
            int[] arrayOfInt5 = { i8 };
            localBlowFish.Blowfish_encipher(arrayOfInt2, arrayOfInt3);
            localBlowFish.Blowfish_encipher(arrayOfInt4, arrayOfInt5);
            String str2 = int32ToByteHexR(arrayOfInt2[0]);
            String str3 = int32ToByteHexR(arrayOfInt3[0]);
            String str4 = int32ToByteHexR(arrayOfInt4[0]);
            String str5 = int32ToByteHexR(arrayOfInt5[0]);
            String str6 = int32ToByteHex(j);
            String str7 = int32ToByteHex(k);
            String str8 = int32ToByteHex(m);
            String str9 = int32ToByteHex(n);
            if ((!str2.equals(str6)) || (!str3.equals(str7)) || (!str4.equals(str8)) || (!str5.equals(str9)))
              break label538;
            Log.d("wild0", "===============================");
            paramWifiCar.verifyCommand();
            return true;
            Protocol localProtocol1 = new Protocol("MO_O".getBytes(), 1, paramArrayOfByte.length, paramArrayOfByte);
            localObject = localProtocol1;
            break;
          }
          arrayOfInt1[i] = byteArrayToInt(arrayOfByte, i + 23, 1);
        }
      }
      WificarActivity localWificarActivity = WificarActivity.getInstance();
      WificarActivity.getInstance();
      localWificarActivity.sendMessage(8903);
      return false;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
      return false;
    }
    label538: return false;
  }

  public static ByteArrayBuffer parseMediaCommand(WifiCar paramWifiCar, ByteArrayBuffer paramByteArrayBuffer, int paramInt)
    throws IOException
  {
    byte[] arrayOfByte = paramByteArrayBuffer.toByteArray();
    int i = 0;
    int j = 0;
    if (0 < 0)
      return paramByteArrayBuffer;
    int k = 0;
    if (i < 0);
    int m;
    int n;
    do
    {
      do
      {
        paramByteArrayBuffer.clear();
        paramByteArrayBuffer.append(arrayOfByte, j, arrayOfByte.length - j);
        return paramByteArrayBuffer;
        k++;
      }
      while (arrayOfByte.length - i < 23);
      ByteUtility.byteArrayToInt(arrayOfByte, i + 4, 2);
      m = ByteUtility.byteArrayToInt(arrayOfByte, i + 15, 4);
      n = m + 23;
    }
    while (arrayOfByte.length - i < m + 23);
    Protocol localProtocol;
    if ((paramInt < 20000) && (paramByteArrayBuffer.length() < 65536))
    {
      localProtocol = new Protocol(arrayOfByte, i);
      if (localProtocol != null)
        switch (localProtocol.getOp())
        {
        default:
        case 2:
        case 1:
        }
    }
    while (true)
    {
      i += n;
      if (i < 0)
        break;
      j = i;
      break;
      parseAudioData(paramWifiCar, localProtocol.output());
      continue;
      parseVideoData(paramWifiCar, localProtocol.output());
    }
  }

  public static void parseTalkStartResp(WifiCar paramWifiCar, byte[] paramArrayOfByte, int paramInt)
  {
    int i = byteArrayToInt(paramArrayOfByte, 0, 2);
    if ((i == 0) && (paramArrayOfByte.length > 2))
      byteArrayToInt(paramArrayOfByte, 2, 4);
    if (i == 0)
      paramWifiCar.getAudioComponent().startRecord();
  }

  public static int parseVerifyResp(WifiCar paramWifiCar, byte[] paramArrayOfByte, int paramInt)
  {
    Log.d("wild0", "cmdVerifyResp");
    Protocol localProtocol;
    if (paramInt == 0)
      localProtocol = new Protocol(paramArrayOfByte, 0);
    int i;
    while (true)
    {
      i = byteArrayToInt(localProtocol.getContent(), 0, 2);
      try
      {
        paramWifiCar.enableVideo();
        return i;
        localProtocol = new Protocol("MO_O".getBytes(), 3, paramArrayOfByte.length, paramArrayOfByte);
      }
      catch (IOException localIOException)
      {
        localIOException.printStackTrace();
      }
    }
    return i;
  }

  public static void parseVideoData(WifiCar paramWifiCar, byte[] paramArrayOfByte)
  {
    byte[] arrayOfByte1 = new Protocol(paramArrayOfByte, 0).getContent();
    int i = byteArrayToInt(arrayOfByte1, 0, 4);
    int j = byteArrayToInt(arrayOfByte1, 4, 4);
    byteArrayToInt(arrayOfByte1, 8, 1);
    int k = byteArrayToInt(arrayOfByte1, 9, 4);
    ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
    localByteArrayOutputStream.write(arrayOfByte1, 13, k);
    byte[] arrayOfByte2 = localByteArrayOutputStream.toByteArray();
    VideoData localVideoData = new VideoData(i, j, arrayOfByte2);
    try
    {
      paramWifiCar.appendVideoDataToFlim(localVideoData);
      return;
    }
    catch (Exception localException)
    {
      localException.printStackTrace();
    }
  }

  public static void parseVideoStartResp(WifiCar paramWifiCar, byte[] paramArrayOfByte, int paramInt)
    throws IOException
  {
    byteArrayToInt(paramArrayOfByte, 0, 2);
    paramWifiCar.connectMediaReceiver(byteArrayToInt(paramArrayOfByte, 2, 4));
    paramWifiCar.enableAudio();
  }

  static class Protocol
  {
    byte[] content = new byte[0];
    int contentLength = 0;
    byte[] header;
    int op = 0;
    byte preserve1 = 0;
    byte[] preserve2 = new byte[8];
    long preserve3 = 0L;

    public Protocol(byte[] paramArrayOfByte)
    {
      this(paramArrayOfByte, 0);
    }

    public Protocol(byte[] paramArrayOfByte, int paramInt)
    {
      this.header = "MO_V".getBytes();
      this.op = CommandEncoder.byteArrayToInt(paramArrayOfByte, paramInt + 4, 2);
      this.contentLength = CommandEncoder.byteArrayToInt(paramArrayOfByte, paramInt + 15, 4);
      if (this.contentLength > 0)
      {
        this.content = new byte[this.contentLength];
        System.arraycopy(paramArrayOfByte, paramInt + 23, this.content, 0, this.contentLength);
      }
    }

    public Protocol(byte[] paramArrayOfByte1, int paramInt1, int paramInt2, byte[] paramArrayOfByte2)
    {
      this.header = paramArrayOfByte1;
      this.op = paramInt1;
      this.contentLength = paramInt2;
      this.content = paramArrayOfByte2;
    }

    public byte[] getContent()
    {
      return this.content;
    }

    public int getOp()
    {
      return this.op;
    }

    public byte[] output()
      throws IOException
    {
      this.content.length;
      byte[] arrayOfByte1 = CommandEncoder.int16ToByteArray(this.op);
      byte[] arrayOfByte2 = CommandEncoder.int32ToByteArray(this.content.length);
      ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
      localByteArrayOutputStream.write(this.header);
      localByteArrayOutputStream.write(arrayOfByte1);
      localByteArrayOutputStream.write(new byte[1]);
      localByteArrayOutputStream.write(new byte[8]);
      localByteArrayOutputStream.write(arrayOfByte2);
      localByteArrayOutputStream.write(new byte[4]);
      localByteArrayOutputStream.write(this.content);
      return localByteArrayOutputStream.toByteArray();
    }
  }
}
package com.prismadroid.devices;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.util.Log;
import com.prismadroid.colors.Rgb24;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class LightpackDevice
  implements Device
{
  private final String TAG = getClass().getName();
  private UsbDeviceConnection connection = null;
  private UsbDevice dev = null;
  private double gamma = 2.0D;
  private UsbInterface intf = null;
  private AtomicBoolean isBusy = new AtomicBoolean(false);
  private TransferCompleteListener transferCompleteListener = null;
  private UsbManager usbManager = null;
  private double wbaB = 1.0D;
  private double wbaG = 1.0D;
  private double wbaR = 1.0D;

  public LightpackDevice(Context paramContext, UsbDevice paramUsbDevice)
  {
    this.usbManager = ((UsbManager)paramContext.getSystemService("usb"));
    this.dev = paramUsbDevice;
  }

  public void close()
  {
    this.connection.releaseInterface(this.intf);
    this.connection.close();
    this.connection = null;
  }

  public String getName()
  {
    if (this.dev == null);
    for (String str = null; ; str = this.dev.getDeviceName())
      return str;
  }

  public String getSerial()
  {
    if (this.connection == null);
    for (String str = null; ; str = this.connection.getSerial())
      return str;
  }

  public double getWbaB()
  {
    return this.wbaB;
  }

  public double getWbaG()
  {
    return this.wbaG;
  }

  public double getWbaR()
  {
    return this.wbaR;
  }

  public boolean isBusy()
  {
    return this.isBusy.get();
  }

  public boolean open()
  {
    boolean bool = false;
    if (this.dev != null)
    {
      this.intf = this.dev.getInterface(0);
      this.connection = this.usbManager.openDevice(this.dev);
      if (this.connection != null)
        break label45;
    }
    while (true)
    {
      return bool;
      label45: bool = this.connection.claimInterface(this.intf, true);
    }
  }

  public void setColors(List<Rgb24> paramList)
  {
    if (paramList.size() > 10)
      Log.w(this.TAG, "colors.size > 10");
    byte[] arrayOfByte;
    int i;
    Iterator localIterator;
    if (!this.isBusy.get())
    {
      arrayOfByte = new byte[61];
      arrayOfByte[0] = 1;
      i = 1;
      localIterator = paramList.iterator();
    }
    while (true)
    {
      if (!localIterator.hasNext())
      {
        SendTask localSendTask = new SendTask();
        byte[][] arrayOfByte1 = new byte[1][];
        arrayOfByte1[0] = arrayOfByte;
        localSendTask.execute(arrayOfByte1);
        return;
      }
      Rgb24 localRgb24 = (Rgb24)localIterator.next();
      double d1 = localRgb24.r * this.wbaR;
      if (d1 > 255.0D)
        d1 = 255.0D;
      double d2 = localRgb24.g * this.wbaG;
      if (d2 > 255.0D)
        d2 = 255.0D;
      double d3 = localRgb24.b * this.wbaB;
      if (d3 > 255.0D)
        d3 = 255.0D;
      int j = (short)(int)(255.0D * Math.pow(d1 / 255.0D, this.gamma));
      int k = (short)(int)(255.0D * Math.pow(d2 / 255.0D, this.gamma));
      int m = (short)(int)(255.0D * Math.pow(d3 / 255.0D, this.gamma));
      int n = i + 1;
      arrayOfByte[i] = ((byte)(j & 0xFF));
      int i1 = n + 1;
      arrayOfByte[n] = ((byte)(k & 0xFF));
      int i2 = i1 + 1;
      arrayOfByte[i1] = ((byte)(m & 0xFF));
      int i3 = i2 + 1;
      arrayOfByte[i2] = 0;
      int i4 = i3 + 1;
      arrayOfByte[i3] = 0;
      i = i4 + 1;
      arrayOfByte[i4] = 0;
    }
  }

  public void setGamma(double paramDouble)
  {
    this.gamma = paramDouble;
  }

  public void setSmoothness(int paramInt)
  {
    if (!this.isBusy.get())
    {
      byte[] arrayOfByte = new byte[2];
      arrayOfByte[0] = 5;
      arrayOfByte[1] = ((byte)paramInt);
      SendTask localSendTask = new SendTask();
      byte[][] arrayOfByte1 = new byte[1][];
      arrayOfByte1[0] = arrayOfByte;
      localSendTask.execute(arrayOfByte1);
    }
  }

  public void setTransferCompleteListener(TransferCompleteListener paramTransferCompleteListener)
  {
    this.transferCompleteListener = paramTransferCompleteListener;
  }

  public void setWbaB(double paramDouble)
  {
    this.wbaB = paramDouble;
  }

  public void setWbaG(double paramDouble)
  {
    this.wbaG = paramDouble;
  }

  public void setWbaR(double paramDouble)
  {
    this.wbaR = paramDouble;
  }

  class SendTask extends AsyncTask<byte[], Void, Void>
  {
    SendTask()
    {
    }

    protected Void doInBackground(byte[][] paramArrayOfByte)
    {
      if (LightpackDevice.this.isBusy.compareAndSet(false, true));
      try
      {
        if (LightpackDevice.this.connection != null)
        {
          int i = LightpackDevice.this.connection.controlTransfer(33, 9, 512, 0, paramArrayOfByte[0], paramArrayOfByte[0].length, 1000);
          if (LightpackDevice.this.transferCompleteListener != null)
            LightpackDevice.this.transferCompleteListener.onTransferComplete(paramArrayOfByte[0].length, i);
        }
        return null;
      }
      catch (Exception localException)
      {
        while (true)
        {
          Log.e(LightpackDevice.this.TAG, localException.getMessage(), localException);
          localException.printStackTrace();
        }
      }
    }

    protected void onPostExecute(Void paramVoid)
    {
      super.onPostExecute(paramVoid);
      LightpackDevice.this.isBusy.set(false);
    }
  }

  public static abstract interface TransferCompleteListener
  {
    public abstract void onTransferComplete(int paramInt1, int paramInt2);
  }
}
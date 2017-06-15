package com.prismadroid.devices;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;
import com.prismadroid.PrismContext;
import com.prismadroid.ProfileManager;
import com.prismadroid.colors.Rgb24;
import com.prismadroid.colors.WhiteBalance;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class DeviceManager
{
  private static final String ACTION_USB_PERMISSION = "com.prismadroid.USB_PERMISSION";
  private List<Rgb24> colors = null;
  protected final Context context;
  private List<Device> devices = new ArrayList();
  private boolean devicesOpened = false;
  private PendingIntent mPermissionIntent;
  private UsbManager mUsbManager;
  private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver()
  {
    public void onReceive(Context paramAnonymousContext, Intent paramAnonymousIntent)
    {
      if ("com.prismadroid.USB_PERMISSION".equals(paramAnonymousIntent.getAction()))
        try
        {
          UsbDevice localUsbDevice = (UsbDevice)paramAnonymousIntent.getParcelableExtra("device");
          if (paramAnonymousIntent.getBooleanExtra("permission", false))
          {
            if (localUsbDevice != null)
            {
              LightpackDevice localLightpackDevice = new LightpackDevice(paramAnonymousContext.getApplicationContext(), localUsbDevice);
              if (localLightpackDevice.open())
                DeviceManager.this.addDevice(localLightpackDevice);
            }
          }
          else
            Log.d("usb", "permission denied for device " + localUsbDevice);
        }
        finally
        {
        }
    }
  };
  protected PowerManager powerManager = null;
  protected final PrismContext prismContext;
  private int smoothness = 50;
  private WhiteBalance whiteBalance = WhiteBalance.defaultWhiteBalance();

  public DeviceManager(PrismContext paramPrismContext)
  {
    this.prismContext = paramPrismContext;
    this.context = paramPrismContext.getApplicationContext();
  }

  /** @deprecated */
  private void addDevice(Device paramDevice)
  {
    try
    {
      this.devices.add(paramDevice);
      Collections.sort(this.devices, new Comparator()
      {
        public int compare(Device paramAnonymousDevice1, Device paramAnonymousDevice2)
        {
          String str1 = paramAnonymousDevice1.getSerial();
          if (TextUtils.isEmpty(str1))
            str1 = paramAnonymousDevice1.getName();
          String str2 = paramAnonymousDevice2.getSerial();
          if (TextUtils.isEmpty(str2))
            str2 = paramAnonymousDevice2.getName();
          int i;
          if ((str1 == null) && (str2 == null))
            i = 0;
          while (true)
          {
            return i;
            if (str1 == null)
              i = -1;
            else if (str2 == null)
              i = 1;
            else
              i = str1.compareTo(str2);
          }
        }
      });
      paramDevice.setGamma(2.0D);
      pushWhiteBalanceToDevice(paramDevice);
      onDevicesChanged();
      return;
    }
    finally
    {
      localObject = finally;
      throw localObject;
    }
  }

  private void pushSettingsToDevice()
  {
    Iterator localIterator = this.devices.iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return;
      Device localDevice = (Device)localIterator.next();
      localDevice.setSmoothness(getSmoothness());
      pushWhiteBalanceToDevice(localDevice);
    }
  }

  private void pushWhiteBalanceToDevice(Device paramDevice)
  {
    WhiteBalance localWhiteBalance = getWhiteBalance();
    if (localWhiteBalance == null)
      localWhiteBalance = WhiteBalance.defaultWhiteBalance();
    float f1 = Math.max(localWhiteBalance.r, Math.max(localWhiteBalance.r, localWhiteBalance.b));
    float f2;
    float f3;
    if (f1 == 0.0F)
    {
      f2 = 1.0F;
      if (f1 != 0.0F)
        break label134;
      f3 = 1.0F;
      label50: if (f1 != 0.0F)
        break label145;
    }
    label134: label145: for (float f4 = 1.0F; ; f4 = localWhiteBalance.b / f1)
    {
      float f5 = 1.0F - 0.5F + f2 * 0.5F;
      float f6 = 1.0F - 0.5F + f3 * 0.5F;
      float f7 = 1.0F - 0.5F + f4 * 0.5F;
      paramDevice.setWbaR(f5);
      paramDevice.setWbaG(f6);
      paramDevice.setWbaB(f7);
      return;
      f2 = localWhiteBalance.r / f1;
      break;
      f3 = localWhiteBalance.g / f1;
      break label50;
    }
  }

  public void changeColors(Rgb24 paramRgb24)
  {
    changeColors(colorListFromSingleColor(paramRgb24));
  }

  public void changeColors(List<Rgb24> paramList)
  {
    if (isScreenOn());
    for (this.colors = paramList; ; this.colors = colorListFromSingleColor(new Rgb24(0, 0, 0)))
    {
      WriteToUsbTask localWriteToUsbTask = new WriteToUsbTask();
      List[] arrayOfList = new List[1];
      arrayOfList[0] = paramList;
      localWriteToUsbTask.execute(arrayOfList);
      return;
    }
  }

  public void closeDevices()
  {
    if (!this.devicesOpened)
      return;
    this.devicesOpened = false;
    resetColorsForce();
    pushSettingsToDevice();
    Iterator localIterator = this.devices.iterator();
    while (true)
    {
      if (!localIterator.hasNext())
      {
        this.devices.clear();
        onDevicesChanged();
        this.context.unregisterReceiver(this.mUsbReceiver);
        break;
      }
      ((Device)localIterator.next()).close();
    }
  }

  protected List<Rgb24> colorListFromSingleColor(Rgb24 paramRgb24)
  {
    int i = 10 * this.devices.size();
    ArrayList localArrayList = new ArrayList(i);
    for (int j = 0; ; j++)
    {
      if (j >= i)
        return localArrayList;
      localArrayList.add(paramRgb24);
    }
  }

  public int getDeviceCount()
  {
    return this.devices.size();
  }

  public List<Device> getDevices()
  {
    return this.devices;
  }

  public int getSmoothness()
  {
    return this.smoothness;
  }

  public WhiteBalance getWhiteBalance()
  {
    return this.whiteBalance;
  }

  public boolean isDevicesOpened()
  {
    return this.devicesOpened;
  }

  protected boolean isScreenOn()
  {
    if (this.powerManager == null)
      this.powerManager = ((PowerManager)this.prismContext.getApplicationContext().getSystemService("power"));
    return this.powerManager.isScreenOn();
  }

  protected void onDevicesChanged()
  {
    this.prismContext.getProfileManager().checkForProfileChanging();
  }

  public void openDevices()
  {
    if (this.devicesOpened)
      return;
    this.devicesOpened = true;
    this.mUsbManager = ((UsbManager)this.context.getSystemService("usb"));
    this.mPermissionIntent = PendingIntent.getBroadcast(this.context, 0, new Intent("com.prismadroid.USB_PERMISSION"), 0);
    IntentFilter localIntentFilter = new IntentFilter("com.prismadroid.USB_PERMISSION");
    this.context.registerReceiver(this.mUsbReceiver, localIntentFilter);
    Iterator localIterator = this.mUsbManager.getDeviceList().values().iterator();
    while (true)
    {
      if (!localIterator.hasNext())
      {
        pushSettingsToDevice();
        break;
      }
      UsbDevice localUsbDevice = (UsbDevice)localIterator.next();
      Object[] arrayOfObject = new Object[2];
      arrayOfObject[0] = Integer.valueOf(localUsbDevice.getVendorId());
      arrayOfObject[1] = Integer.valueOf(localUsbDevice.getProductId());
      Log.d("device", String.format("Device. vid: %d; pid: %d", arrayOfObject));
      if (((localUsbDevice.getVendorId() == 7504) && (localUsbDevice.getProductId() == 24610)) || ((localUsbDevice.getVendorId() == 1003) && (localUsbDevice.getProductId() == 8271)))
      {
        Toast.makeText(this.context, "found Lightpack" + localUsbDevice.getDeviceName(), 0).show();
        this.mUsbManager.requestPermission(localUsbDevice, this.mPermissionIntent);
      }
    }
  }

  public void resetColors()
  {
    changeColors(new Rgb24(0, 0, 0));
  }

  protected void resetColorsForce()
  {
    writeColorsToUsb(colorListFromSingleColor(new Rgb24(0, 0, 0)));
  }

  public void setSmoothness(int paramInt)
  {
    if (this.smoothness != paramInt)
    {
      this.smoothness = paramInt;
      pushSettingsToDevice();
    }
  }

  public void setWhiteBalance(WhiteBalance paramWhiteBalance)
  {
    this.whiteBalance = paramWhiteBalance;
    pushSettingsToDevice();
  }

  protected void writeColorsToUsb(List<Rgb24> paramList)
  {
    int i = 0;
    ArrayList localArrayList = new ArrayList(10);
    Iterator localIterator = paramList.iterator();
    while (true)
    {
      if (!localIterator.hasNext())
        return;
      localArrayList.add((Rgb24)localIterator.next());
      if (((i + 1) % 10 == 0) || (i == -1 + paramList.size()))
      {
        if (this.devices.size() >= 1 + i / 10)
          ((Device)this.devices.get(i / 10)).setColors(localArrayList);
        localArrayList.clear();
      }
      i++;
    }
  }

  class WriteToUsbTask extends AsyncTask<List<Rgb24>, Void, Void>
  {
    WriteToUsbTask()
    {
    }

    protected Void doInBackground(List<Rgb24>[] paramArrayOfList)
    {
      List<Rgb24> localList = paramArrayOfList[0];
      DeviceManager.this.writeColorsToUsb(localList);
      return null;
    }
  }
}
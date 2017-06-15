package com.prismadroid.devices;

import com.prismadroid.colors.Rgb24;
import java.util.List;

public abstract interface Device
{
  public abstract void close();

  public abstract String getName();

  public abstract String getSerial();

  public abstract boolean isBusy();

  public abstract void setColors(List<Rgb24> paramList);

  public abstract void setGamma(double paramDouble);

  public abstract void setSmoothness(int paramInt);

  public abstract void setWbaB(double paramDouble);

  public abstract void setWbaG(double paramDouble);

  public abstract void setWbaR(double paramDouble);
}
package com.prismadroid.colors;

import java.io.Serializable;
import java.util.Locale;

public class WhiteBalance
  implements Serializable
{
  private static final long serialVersionUID = 6568496020292312857L;
  public float b;
  public float g;
  public float r;

  public WhiteBalance()
  {
  }

  public WhiteBalance(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.r = paramFloat1;
    this.g = paramFloat2;
    this.b = paramFloat3;
  }

  public static WhiteBalance defaultWhiteBalance()
  {
    return new WhiteBalance(0.5F, 0.5F, 0.5F);
  }

  public WhiteBalance clone()
  {
    return new WhiteBalance(this.r, this.g, this.b);
  }

  public String toString()
  {
    Locale localLocale = Locale.getDefault();
    Object[] arrayOfObject = new Object[3];
    arrayOfObject[0] = Float.valueOf(this.r);
    arrayOfObject[1] = Float.valueOf(this.g);
    arrayOfObject[2] = Float.valueOf(this.b);
    return String.format(localLocale, "r: %f; g: %f; b: %f", arrayOfObject);
  }
}
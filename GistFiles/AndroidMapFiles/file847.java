package com.prismadroid.colors;

import android.graphics.Color;
import java.io.Serializable;

public class Rgb24
  implements Serializable
{
  private static final long serialVersionUID = -8860050607675414854L;
  public int b;
  public int g;
  public int r;

  public Rgb24(int paramInt1, int paramInt2, int paramInt3)
  {
    this.r = paramInt1;
    this.g = paramInt2;
    this.b = paramInt3;
  }

  public static Rgb24 fromAndroidColor(int paramInt)
  {
    return new Rgb24(Color.red(paramInt), Color.green(paramInt), Color.blue(paramInt));
  }

  public static Rgb24 fromInt(int paramInt)
  {
    return new Rgb24(0xFF & paramInt >> 16, 0xFF & paramInt >> 8, paramInt & 0xFF);
  }

  public static Rgb24 mix2(Rgb24 paramRgb241, float paramFloat1, Rgb24 paramRgb242, float paramFloat2)
  {
    return new Rgb24(Math.round(paramFloat1 * paramRgb241.r + paramFloat2 * paramRgb242.r), Math.round(paramFloat1 * paramRgb241.g + paramFloat2 * paramRgb242.g), Math.round(paramFloat1 * paramRgb241.b + paramFloat2 * paramRgb242.b));
  }

  public static Rgb24 mix3(Rgb24 paramRgb241, float paramFloat1, Rgb24 paramRgb242, float paramFloat2, Rgb24 paramRgb243, float paramFloat3)
  {
    return new Rgb24(Math.round(paramFloat1 * paramRgb241.r + paramFloat2 * paramRgb242.r + paramFloat3 * paramRgb243.r), Math.round(paramFloat1 * paramRgb241.g + paramFloat2 * paramRgb242.g + paramFloat3 * paramRgb243.g), Math.round(paramFloat1 * paramRgb241.b + paramFloat2 * paramRgb242.b + paramFloat3 * paramRgb243.b));
  }

  public int toAndroidColor()
  {
    return Color.argb(255, this.r, this.g, this.b);
  }

  public int toInt()
  {
    return ((0xFF & this.r) << 16) + ((0xFF & this.g) << 8) + (0xFF & this.b);
  }
}
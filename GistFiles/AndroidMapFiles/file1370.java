package com.prismadroid.colors;

import android.graphics.Color;

public class Hsv
{
  public float h;
  public float s;
  public float v;

  public Hsv(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.h = paramFloat1;
    this.s = paramFloat2;
    this.v = paramFloat3;
  }

  public static Hsv fromAndroidColor(int paramInt)
  {
    float[] arrayOfFloat = new float[3];
    Color.colorToHSV(paramInt, arrayOfFloat);
    return new Hsv(arrayOfFloat[0], arrayOfFloat[1], arrayOfFloat[2]);
  }

  public static Hsv fromRgb24(Rgb24 paramRgb24)
  {
    return fromAndroidColor(paramRgb24.toAndroidColor());
  }

  public int toAndroidColor()
  {
    float[] arrayOfFloat = new float[3];
    arrayOfFloat[0] = this.h;
    arrayOfFloat[1] = this.s;
    arrayOfFloat[2] = this.v;
    return Color.HSVToColor(arrayOfFloat);
  }

  public Rgb24 toRgb24()
  {
    return Rgb24.fromAndroidColor(toAndroidColor());
  }
}
package com.prismadroid.colors;

public class Yuv
{
  public float u;
  public float v;
  public float y;

  public Yuv()
  {
  }

  public Yuv(float paramFloat1, float paramFloat2, float paramFloat3)
  {
    this.y = paramFloat1;
    this.u = paramFloat2;
    this.v = paramFloat3;
  }

  public static Yuv fromRgb(Rgb24 paramRgb24)
  {
    return new Yuv(0.299F * paramRgb24.r + 0.587F * paramRgb24.g + 0.114F * paramRgb24.b, 128.0F + (-0.14713F * paramRgb24.r - 0.28886F * paramRgb24.g + 0.436F * paramRgb24.b), 128.0F + (0.615F * paramRgb24.r - 0.51499F * paramRgb24.g - 0.10001F * paramRgb24.b));
  }

  public Rgb24 toRgb()
  {
    float f1 = this.y + 1.13983F * (this.v - 128.0F);
    float f2 = this.y - 0.39465F * (this.u - 128.0F) - 0.5806F * (this.v - 128.0F);
    float f3 = this.y + 2.03211F * (this.u - 128.0F);
    return new Rgb24(Math.round(f1), Math.round(f2), Math.round(f3));
  }
}
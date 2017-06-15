package com.prismadroid.colors;

import java.util.Random;

public class ColorUtils
{
  private static Random random = new Random(System.currentTimeMillis());

  public static Rgb24 randomColor()
  {
    return new Rgb24(random.nextInt(256), random.nextInt(256), random.nextInt(256));
  }

  public static Rgb24 randomColorHue()
  {
    return randomColorHue(1.0F, 1.0F);
  }

  public static Rgb24 randomColorHue(float paramFloat1, float paramFloat2)
  {
    return new Hsv(360.0F * random.nextFloat(), paramFloat1, paramFloat2).toRgb24();
  }
}
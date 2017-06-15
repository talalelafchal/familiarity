package com.google.android.gms.car;

public class CarInfoInternal
  extends CarInfo
{
  public String r;
  public boolean s = false;
  public long t;
  public long u;
  
  public CarInfoInternal(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2)
  {
    this.b = paramString1;
    this.c = paramString2;
    this.d = paramString3;
    this.r = paramString4;
    this.f = paramInt1;
    this.g = paramInt2;
  }
  
  public CarInfoInternal(String paramString1, String paramString2, String paramString3, String paramString4, int paramInt1, int paramInt2, int paramInt3, int paramInt4, String paramString5, String paramString6, String paramString7, String paramString8, boolean paramBoolean)
  {
    this.b = paramString1;
    this.c = paramString2;
    this.d = paramString3;
    this.r = paramString4;
    this.h = a(paramInt3, 1);
    this.f = paramInt1;
    this.g = paramInt2;
    this.i = paramInt4;
    this.j = paramString5;
    this.k = paramString6;
    this.l = paramString7;
    this.m = paramString8;
    this.n = paramBoolean;
    this.o = a(paramInt3, 2);
    this.p = a(paramInt3, 4);
  }
  
  private final boolean a(int paramInt1, int paramInt2)
  {
    return (paramInt1 & paramInt2) == paramInt2;
  }
  
  public String toString()
  {
    StringBuilder localStringBuilder = new StringBuilder().append(getClass().getName()).append("[").append("dbId=").append(this.t).append(",").append("manufacturer=").append(this.b).append(",model=").append(this.c).append(",headUnitProtocolVersion=").append(this.f).append(".").append(this.g).append(",modelYear=").append(this.d).append(",vehicleId=");
    if (Flags.a(CarServiceLogging.a)) {}
    for (String str = this.r;; str = this.e) {
      return str + ",bluetoothAllowed=" + this.s + ",hideProjectedClock=" + this.h + ",driverPosition=" + this.i + ",headUnitMake=" + this.j + ",headUnitModel=" + this.k + ",headUnitSoftwareBuild=" + this.l + ",headUnitSoftwareVersion=" + this.m + ",canPlayNativeMediaDuringVr=" + this.n + ",hidePhoneSignal=" + this.o + ",hideBatteryLevel=" + this.p + "]";
    }
  }
}

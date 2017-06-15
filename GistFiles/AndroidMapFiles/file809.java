 public static String fe(int paramInt)
  {
    String str = "code=" + Integer.toHexString(paramInt) + ", status = " + paramInt;
    if ((paramInt & 0x1) != 0)
      str = str + ", open";
    if ((paramInt & 0x2) != 0)
      str = str + ", email-verified";
    if ((paramInt & 0x4) != 0)
      str = str + ", mobile-verified";
    if ((paramInt & 0x8) != 0)
      str = str + ", hide-qq-search";
    if ((paramInt & 0x10) != 0)
      str = str + ", hide-qq-promote";
    if ((paramInt & 0x20) != 0)
      str = str + ", need-verify";
    if ((paramInt & 0x40) != 0)
      str = str + ", has-qq-msg";
    if ((paramInt & 0x80) != 0)
      str = str + ", no-qq-promote";
    if ((paramInt & 0x100) != 0)
      str = str + ", no-mobile-promote";
    if ((paramInt & 0x200) != 0)
      str = str + ", hide-mobile_search";
    if ((paramInt & 0x1000) != 0)
      str = str + ", open-float-bottle";
    if ((0x20000 & paramInt) != 0)
      str = str + ", bind but not upload";
    return str;
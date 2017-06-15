/**
 * Class containing some static utility methods.
 */
public class OSVersionUtils {

  private OSVersionUtils() {
  }

  public static void enableStrictMode() {
  }

  /** 获得操作系统版本 */
  public static String getOs_Version() {
    if (null != Build.VERSION.RELEASE) {
      return Build.VERSION.RELEASE;
    }
    return "";
  }

  /**
   * 2.2 API 8
   */
  public static boolean hasFroyo() {
    // Can use static final constants like FROYO, declared in later versions
    // of the OS since they are inlined at compile time. This is guaranteed
    // behavior.
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO;
  }

  /**
   * 2.3 API 9
   */
  public static boolean hasGingerbread() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD;
  }

  /**
   * 3.0 API 11
   */
  public static boolean hasHoneycomb() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB;
  }

  /**
   * 3.1 API 12
   */
  public static boolean hasHoneycombMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1;
  }

  /**
   * 4.0 API 14
   */
  public static boolean hasIceCreamSandwich() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
  }

  /**
   * 4.0 API 15
   */
  public static boolean hasIceCreamSandwichMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1;
  }

  /**
   * 4.0 API 16
   */
  public static boolean hasJellyBean() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;
  }

  /**
   * 4.2 API 17
   */
  public static boolean hasJellyBeanMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1;
  }

  /**
   * 4.4 API19
   */
  public static boolean hasKitKat() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
  }

  /**
   * 5.0.1 API 21
   */
  public static boolean hasL() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP;
  }

  /**
   * 5.1.1 API 22
   */
  public static boolean hasLMR1() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1;
  }

  /**
   * 6.0 API 23
   */
  public static boolean hasM() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
  }

  /**
   * 7.0 API 24
   */
  public static boolean hasN() {
    return Build.VERSION.SDK_INT >= Build.VERSION_CODES.N;
  }

  /** 读取外存储权限 */
  public static String READ_EXTERNAL_STORAGE() {
    if (hasJellyBean()) {
      return Manifest.permission.READ_EXTERNAL_STORAGE;
    } else {
      return "android.permission.READ_EXTERNAL_STORAGE";
    }
  }
}
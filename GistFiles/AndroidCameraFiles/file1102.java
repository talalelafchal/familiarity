public class ScreenOrientationUtils {

    public static int getRotation(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        return windowManager.getDefaultDisplay().getRotation();
    }

    public static String getRotationString(int rotation) {
        String rotationString = "";
        switch (rotation) {
            case Surface.ROTATION_0:
                rotationString = "Surface.ROTATION_0";
                break;
            case Surface.ROTATION_90:
                rotationString = "Surface.ROTATION_90";
                break;
            case Surface.ROTATION_180:
                rotationString = "Surface.ROTATION_180";
                break;
            case Surface.ROTATION_270:
                rotationString = "Surface.ROTATION_270";
                break;
            default:
                rotationString = "Unknown";
                break;
        }
        return rotationString;
    }

    public static int getOrientation(Context context) {
        return context.getResources().getConfiguration().orientation;
    }

    public static ScreenOrientation getScreenOrientation(Context context) {
        ScreenOrientation screenOrientation = null;

        int o = getOrientation(context);
        int r = getRotation(context);

        switch (o) {
            case Configuration.ORIENTATION_LANDSCAPE:
                switch (r) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_90:
                        screenOrientation = ScreenOrientation.SCREEN_ORIENTATION_LANDSCAPE;
                        break;
                    case Surface.ROTATION_180:
                    case Surface.ROTATION_270:
                        screenOrientation = ScreenOrientation.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                        break;
                    default:
                        screenOrientation = ScreenOrientation.SCREEN_ORIENTATION_UNSPECIFIED;
                        break;
                }
                break;
            case Configuration.ORIENTATION_PORTRAIT:
                switch (r) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_90:
                        screenOrientation = ScreenOrientation.SCREEN_ORIENTATION_PORTRAIT;
                        break;
                    case Surface.ROTATION_180:
                    case Surface.ROTATION_270:
                        screenOrientation = ScreenOrientation.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                        break;
                    default:
                        screenOrientation = ScreenOrientation.SCREEN_ORIENTATION_UNSPECIFIED;
                        break;
                }
                break;
            default:
                screenOrientation = ScreenOrientation.SCREEN_ORIENTATION_UNSPECIFIED;
                break;
        }

        return screenOrientation;
    }

  public enum ScreenOrientation {
  
      SCREEN_ORIENTATION_UNSPECIFIED(-1),
      SCREEN_ORIENTATION_PORTRAIT(0),
      SCREEN_ORIENTATION_REVERSE_PORTRAIT(1),
      SCREEN_ORIENTATION_LANDSCAPE(2),
      SCREEN_ORIENTATION_REVERSE_LANDSCAPE(3),
      ;
  
      private int mId;
  
      private ScreenOrientation(int id) {
          this.mId = id;
      }
  
      public int getId() {
          return mId;
      }
  
      public void setId(int id) {
          this.mId = id;
      }
  
      public boolean compareTo(int id) {
          return this.mId == id;
      }
  
      public static ScreenOrientation getValue(int id) {
          ScreenOrientation[] orientations = ScreenOrientation.values();
          for(int i = 0; i < orientations.length; i++) {
              if (orientations[i].compareTo(id)) {
                  return orientations[i];
              }
          }
          return SCREEN_ORIENTATION_UNSPECIFIED;
      }
  
      public static ScreenOrientation fromId(int id) {
          switch (id) {
              case 0:
                  return SCREEN_ORIENTATION_PORTRAIT;
              case 1:
                  return SCREEN_ORIENTATION_REVERSE_PORTRAIT;
              case 2:
                  return SCREEN_ORIENTATION_LANDSCAPE;
              case 3:
                  return SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
              default:
                  return SCREEN_ORIENTATION_UNSPECIFIED;
          }
      }
  
  }
}
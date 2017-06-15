import android.content.Context;
import android.content.Intent;
import android.support.annotation.StringDef;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public final class DemoModeHelper {

  public static void enter(Context context) {
    new Enter(context).send();
  }

  public static void exit(Context context) {
    new Exit(context).send();
  }

  private static final class Enter extends Action<Enter> {

    public Enter(Context context) {
      super(context, "enter");
    }
  }

  private static final class Exit extends Action<Enter> {

    public Exit(Context context) {
      super(context, "exit");
    }
  }

  public static final class Clock extends Action<Clock> {

    public Clock(Context context) {
      super(context, "clock");
    }

    /**
     * Set value of the clock hours/minutes
     *
     * @param hhmm "hhmm" string, such as "1200" for 12:00
     */
    public Clock time(String hhmm) {
      return putExtra("hhmm", hhmm);
    }
  }

  public static final class Battery extends Action<Battery> {

    public Battery(Context context) {
      super(context, "battery");
    }

    /**
     * Set the battery level
     *
     * @param level [0, 100]
     */
    public Battery level(int level) {
      if (level > 100 || level < 0) {
        throw new IllegalArgumentException("Battery percentage must be [0, 100], not: " + level);
      }
      return putExtra("level", Integer.toString(level));
    }

    public Battery plugged(boolean plugged) {
      return putExtra("plugged", trueFalse(plugged));
    }
  }

  // TODO: Volume command is no-op in current MNC Preview 2
  /*public static final class Volume extends Action {

    public Volume(Context context) {
      super(context, "volume");
    }
  }*/

  private abstract static class AbsNetwork<T extends AbsNetwork> extends Action<T> {

    public AbsNetwork(Context context) {
      super(context, "network");
    }

    public T airplane(boolean airplane) {
      return putExtra("airplane", showHide(airplane));
    }

    /**
     * Set number of SIM card slot icons
     *
     * @param sims a positive integer
     */
    public T sims(int sims) {
      if (sims < 1) {
        throw new IllegalArgumentException("Sims must be a positive integer, not: " + sims);
      }
      return putExtra("sims", Integer.toString(sims));
    }

    public T nosim(boolean nosim) {
      return putExtra("nosim", showHide(nosim));
    }
  }

  public static final class Network extends AbsNetwork<Network> {

    public Network(Context context) {
      super(context);
    }
  }

  private abstract static class InternetNetwork<T extends InternetNetwork> extends AbsNetwork<T> {

    @Retention(RetentionPolicy.SOURCE) @StringDef({
        LEVEL_NO_SERVICE, LEVEL_LOWEST, LEVEL_LOW, LEVEL_MIDDLE, LEVEL_HIGH, LEVEL_FULL_BARS
    }) public @interface Level {
    }

    public static final String LEVEL_NO_SERVICE = "-1";
    public static final String LEVEL_LOWEST = "0";
    public static final String LEVEL_LOW = "1";
    public static final String LEVEL_MIDDLE = "2";
    public static final String LEVEL_HIGH = "3";
    public static final String LEVEL_FULL_BARS = "4";

    public InternetNetwork(Context context) {
      super(context);
    }

    public T level(@Level String level) {
      return putExtra("level", level);
    }

    /**
     * Show the associated network as connected
     */
    public InternetNetwork fully(boolean fully) {
      return putExtra("fully", trueFalse(fully));
    }
  }

  public static final class Mobile extends InternetNetwork<Mobile> {

    @Retention(RetentionPolicy.SOURCE) @StringDef({
        DATATYPE_ONE_X, DATATYPE_THREE_G, DATATYPE_FOUR_G, DATATYPE_EDGE, DATATYPE_GPRS,
        DATATYPE_HSDPA_OR_HSPA_PLUS, DATATYPE_LTE, DATATYPE_ROAM
    }) public @interface Datatype {
    }

    public static final String DATATYPE_ONE_X = "1x";
    public static final String DATATYPE_THREE_G = "3g";
    public static final String DATATYPE_FOUR_G = "4g";
    public static final String DATATYPE_EDGE = "e";
    public static final String DATATYPE_GPRS = "g";
    public static final String DATATYPE_HSDPA_OR_HSPA_PLUS = "h";
    public static final String DATATYPE_LTE = "lte";
    public static final String DATATYPE_ROAM = "roam";

    public Mobile(Context context, boolean show) {
      super(context);
      putExtra("mobile", showHide(show));
    }

    public Mobile datatype(@Datatype String datatype) {
      return putExtra("datatype", datatype);
    }
  }

  public static final class Wifi extends InternetNetwork {

    public Wifi(Context context, boolean show) {
      super(context);
      putExtra("wifi", showHide(show));
    }
  }

  public static final class Status extends Action<Status> {

    @Retention(RetentionPolicy.SOURCE)
    @StringDef({ BLUETOOTH_CONNECTED, BLUETOOTH_DISCONNECTED, BLUETOOTH_NONE })
    public @interface Bluetooth {
    }

    public static final String BLUETOOTH_CONNECTED = "connected";
    public static final String BLUETOOTH_DISCONNECTED = "disconnected";
    public static final String BLUETOOTH_NONE = "none";

    public Status(Context context) {
      super(context, "status");
    }

    /**
     * Display vibrate icon
     */
    public Status volume(boolean vibrate) {
      return putExtra("volume", vibrate ? "vibrate" : "none");
    }

    public Status buetooth(@Bluetooth String bluetooth) {
      return putExtra("bluetooth", bluetooth);
    }

    public Status location(boolean show) {
      return putExtra("location", showHide(show));
    }

    public Status alarm(boolean show) {
      return putExtra("alarm", showHide(show));
    }

    public Status zen(boolean zen) {
      return putExtra("zen", zen ? "important" : "none");
    }

    public Status mute(boolean mute) {
      return putExtra("mute", showHide(mute));
    }

    public Status speakerphone(boolean speakerphone) {
      return putExtra("speakerphone", showHide(speakerphone));
    }

    public Status managedProfile(boolean managedProfile) {
      return putExtra("managed_profile", showHide(managedProfile));
    }

    public Status cast(boolean cast) {
      return putExtra("cast", showHide(cast));
    }

    public Status hotspot(boolean hotspot) {
      return putExtra("hotspot", hotspot ? "connected" : "none");
    }
  }

  public static final class Notifications extends Action<Notifications> {

    public Notifications(Context context) {
      super(context, "notifications");
    }

    public Notifications visible(boolean visible) {
      return putExtra("visible", trueFalse(visible));
    }
  }

  public static final class Bars extends Action<Bars> {

    @Retention(RetentionPolicy.SOURCE) @StringDef({
        MODE_OPAQUE, MODE_TRANSLUCENT, MODE_SEMI_TRANSPARENT, MODE_TRANSPARENT, MODE_WARNING
    }) public @interface Mode {
    }

    public static final String MODE_OPAQUE = "opaque";
    public static final String MODE_TRANSLUCENT = "translucent";
    public static final String MODE_SEMI_TRANSPARENT = "semi-transparent";
    public static final String MODE_TRANSPARENT = "transparent";
    public static final String MODE_WARNING = "warning";

    public Bars(Context context) {
      super(context, "bars");
    }

    public Bars mode(@Mode String mode) {
      return putExtra("mode", mode);
    }
  }

  private abstract static class Action<T extends Action> {

    private static final String ACTION_DEMO_SYSTEM_UI = "com.android.systemui.demo";

    private final Context context;
    private final Intent intent;

    /**
     * @param context the Context from which to send the demo broadcasts
     */
    public Action(Context context, String command) {
      this.context = context;
      intent = new Intent(ACTION_DEMO_SYSTEM_UI);
      intent.putExtra("command", command);
    }

    public final void send() {
      context.sendBroadcast(intent);
    }

    @SuppressWarnings("unchecked") protected final T putExtra(String name, String value) {
      intent.putExtra(name, value);
      return (T) this;
    }

    protected static String showHide(boolean value) {
      return value ? "show" : "hide";
    }

    protected static String trueFalse(boolean value) {
      return value ? "true" : "false";
    }
  }

  private DemoModeHelper() {
    throw new AssertionError("No instances");
  }
}

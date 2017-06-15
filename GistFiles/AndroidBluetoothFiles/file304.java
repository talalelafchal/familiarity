package jp.naver.android.npush.register;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import jp.naver.android.npush.common.Logger;
import jp.naver.android.npush.common.NPushIntent;

public class NPushMessaging
{
  private static final int CATEGORY_ID = 0;
  private static final long REQUEST_INTERVAL = 3600000L;

  private static Intent generateIntent(Context paramContext, String paramString1, String paramString2)
  {
    return generateIntent(paramContext, paramString1, paramString2, false);
  }

  private static Intent generateIntent(Context paramContext, String paramString1, String paramString2, boolean paramBoolean)
  {
    Intent localIntent = new Intent(paramString2);
    localIntent.putExtra("app", PendingIntent.getBroadcast(paramContext, 0, new Intent(), 0));
    localIntent.putExtra("serviceid", paramString1);
    localIntent.putExtra("keepalive", paramBoolean);
    localIntent.putExtra("categoryid", 0);
    return localIntent;
  }

  public static void getState(Context paramContext, String paramString)
  {
    paramContext.startService(generateIntent(paramContext, paramString, NPushIntent.REQUEST_GETSTATE_INTENT));
  }

  public static void getVersion(Context paramContext, String paramString)
  {
    paramContext.startService(generateIntent(paramContext, paramString, NPushIntent.REQUEST_GETVERSION_INTENT));
  }

  @Deprecated
  public static boolean requestCheckKeepAlive(Context paramContext, String paramString)
  {
    return requestCheckKeepAlive(paramContext, paramString, null);
  }

  public static boolean requestCheckKeepAlive(Context paramContext, String paramString1, String paramString2)
  {
    int i = 0;
    if (paramContext != null)
    {
      i = 0;
      if (paramString1 != null)
        break label14;
    }
    while (true)
    {
      return i;
      label14: boolean bool = NPushUserData.isRegistered(paramContext);
      i = 0;
      if (!bool)
        continue;
      Intent localIntent = generateIntent(paramContext, paramString1, NPushIntent.REQUEST_SUBSCRIBE_INTENT, true);
      if (paramString2 != null)
        localIntent.putExtra("targetId", paramString2);
      paramContext.startService(localIntent);
      i = 1;
    }
  }

  public static boolean requestSubscribe(Context paramContext, String paramString)
  {
    return requestSubscribe(paramContext, paramString, true);
  }

  @Deprecated
  public static boolean requestSubscribe(Context paramContext, String paramString, boolean paramBoolean)
  {
    int i = 0;
    if (paramContext == null);
    while (true)
    {
      return i;
      i = 0;
      if (paramString == null)
        continue;
      paramContext.startService(generateIntent(paramContext, paramString, NPushIntent.REQUEST_SUBSCRIBE_INTENT, false));
      stopRepeatRequest(paramContext);
      Logger.d("NPushMessaging requestSubscribe : isKeepAlive=" + paramBoolean);
      if (paramBoolean)
        startRepeatRequest(paramContext, generateIntent(paramContext, paramString, NPushIntent.REQUEST_SUBSCRIBE_INTENT, true));
      NPushUserData.setRegistered(paramContext, true);
      i = 1;
    }
  }

  public static boolean requestUnsubscribe(Context paramContext, String paramString)
  {
    int i = 0;
    if (paramContext == null);
    while (true)
    {
      return i;
      i = 0;
      if (paramString == null)
        continue;
      paramContext.startService(generateIntent(paramContext, paramString, NPushIntent.REQUEST_UNSUBSCRIBE_INTENT));
      stopRepeatRequest(paramContext);
      NPushUserData.setRegistered(paramContext, false);
      i = 1;
    }
  }

  private static void startRepeatRequest(Context paramContext, Intent paramIntent)
  {
    AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
    PendingIntent localPendingIntent = PendingIntent.getService(paramContext, 0, paramIntent, 134217728);
    localAlarmManager.setRepeating(0, 3600000L + System.currentTimeMillis(), 3600000L, localPendingIntent);
    Logger.d("NPushMessaging requestSubscribe : startRepeatRequest");
  }

  private static void stopRepeatRequest(Context paramContext)
  {
    AlarmManager localAlarmManager = (AlarmManager)paramContext.getSystemService("alarm");
    Intent localIntent1 = new Intent(NPushIntent.REQUEST_SUBSCRIBE_INTENT);
    Intent localIntent2 = new Intent(NPushIntent.REQUEST_UNSUBSCRIBE_INTENT);
    localAlarmManager.cancel(PendingIntent.getService(paramContext, 0, localIntent1, 134217728));
    localAlarmManager.cancel(PendingIntent.getService(paramContext, 0, localIntent2, 134217728));
  }
}
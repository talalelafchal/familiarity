package com.tencent.mm.modelstat;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.tencent.mm.sdk.platformtools.l;
import com.tencent.mm.sdk.platformtools.q;
import com.tencent.mm.t.a.i;
import com.tencent.mm.t.au;

public class WatchDogPushReceiver extends BroadcastReceiver
{
  public static void a(b paramb)
  {
    Intent localIntent = new Intent(q.getContext(), WatchDogPushReceiver.class);
    localIntent.setAction("com.tencent.mm.WatchDogPushReceiver");
    localIntent.putExtra("type", 1);
    localIntent.putExtra("rtType", paramb.BY);
    localIntent.putExtra("beginTime", paramb.BZ);
    localIntent.putExtra("endTime", paramb.Ca);
    localIntent.putExtra("rtType", paramb.BY);
    localIntent.putExtra("dataLen", paramb.Cb);
    localIntent.putExtra("isSend", paramb.Cd);
    localIntent.putExtra("cost", paramb.Ce);
    localIntent.putExtra("doSceneCount", paramb.Cc);
    q.getContext().sendBroadcast(localIntent);
  }

  public static void kW()
  {
    Intent localIntent = new Intent(q.getContext(), WatchDogPushReceiver.class);
    localIntent.setAction("com.tencent.mm.WatchDogPushReceiver");
    localIntent.putExtra("type", 2);
    q.getContext().sendBroadcast(localIntent);
  }

  public static void kX()
  {
    Intent localIntent = new Intent(q.getContext(), WatchDogPushReceiver.class);
    localIntent.setAction("com.tencent.mm.WatchDogPushReceiver");
    localIntent.putExtra("type", 3);
    q.getContext().sendBroadcast(localIntent);
  }

  public void onReceive(Context paramContext, Intent paramIntent)
  {
    if (paramIntent == null)
      l.W("MicroMsg.WatchDogPushReceiver", "onReceive intent == null");
    while (true)
    {
      return;
      int i = paramIntent.getIntExtra("type", 0);
      l.Z("MicroMsg.WatchDogPushReceiver", "onReceive type:" + i);
      if (i == 1)
      {
        com.tencent.mm.t.a.j localj = new com.tencent.mm.t.a.j();
        localj.JD = paramIntent.getIntExtra("rtType", 0);
        localj.BZ = paramIntent.getLongExtra("beginTime", 0L);
        localj.Ca = paramIntent.getLongExtra("endTime", 0L);
        boolean bool = paramIntent.getBooleanExtra("isSend", false);
        if (!bool)
          localj.JI = paramIntent.getLongExtra("dataLen", 0L);
        while (true)
        {
          localj.Ce = paramIntent.getLongExtra("cost", 0L);
          localj.JJ = paramIntent.getLongExtra("doSceneCount", 0L);
          l.Z("MicroMsg.WatchDogPushReceiver", "onRecv: rtType:" + localj.JD + " isSend:" + bool + " tx:" + localj.JH + " rx:" + localj.JI + " begin:" + localj.BZ + " end:" + localj.Ca);
          if ((localj.JJ != 0L) && (localj.JD != 0L) && (localj.BZ != 0L) && (localj.Ca != 0L) && (localj.Ca - localj.BZ > 0L))
            break label367;
          l.X("MicroMsg.WatchDogPushReceiver", "onRecv: count:" + localj.JJ + " rtType:" + localj.JD + " begin:" + localj.BZ + " end:" + localj.Ca);
          break;
          localj.JH = paramIntent.getLongExtra("dataLen", 0L);
        }
        label367: au.ne().a(10401, 0, null, localj);
        continue;
      }
      if (i == 2)
      {
        ((j)au.ne()).kQ();
        continue;
      }
      if (i != 3)
        continue;
      au.ne().a(99999, 0, null, null);
    }
  }
}
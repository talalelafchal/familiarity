public class PlusService
    extends SimplePlusIntentService
{

/** default constructor */
public PlusService() {
  super(PlusService.class.getSimpleName());
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// actually do something for service execution
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** service is executing ... and {@link PlusClient} is connected */
public void onServiceStarted() {

  String email = plusClient.getAccountName();
  Log.i("Social", "PlusService - PlusClient connected, email address is" + email);

}

/** {@link PlusClient} is connected, and {@link PlusClient#loadPerson} was called */
public void onProfileDataLoaded(ConnectionResult status, Person person) {
  Log.i("Social", "PlusService - onPersonLoaded running");

  try {

    if (status.getErrorCode() == ConnectionResult.SUCCESS) {
      Log.i("Social", "Display Name" +  person.getDisplayName());
    }

  }
  catch (Exception e) {AndroidUtils.logErr(IconPaths.Social, "PlusService", "onPersonLoaded had a problem", e);}


}

/** {@link PlusClient} is connected, and {@link PlusClient#loadPeople} was called */
public void onCircleDataLoaded(ConnectionResult status, PersonBuffer personBuffer, String nextPageToken) {
  Log.i("Social", "PlusService - onPeopleLoaded running");

  try {

    if (status.getErrorCode() == ConnectionResult.SUCCESS) {
      try {
        int count = personBuffer.getCount();
        for (int i = 0; i < count; i++) {
          Log.i("Social", "Circle.Display Name: " + personBuffer.get(i).getDisplayName());
        }
      }
      finally {
        personBuffer.close();
      }
    }
    else {
      AndroidUtils.logErr(Social, "Error listing people: " + status.getErrorCode());
    }
  }
  catch (Exception e) {AndroidUtils.logErr(IconPaths.Social, "PlusService", "onPeopleLoaded had a problem", e);}

}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// load/save data
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX


//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// service startup
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** schedules recurring alarm & starts the service */
public static void startServiceNow(Context ctx) {

  // actually start service
  ctx.startService(new Intent(ctx, PlusService.class));

  Log.i("Social", "PlusService startServiceNow called");

  // schedule the alarm (even though it might have been scheduled by {@link BootReceiver}
  setRecurringAlarm(ctx);

}

public static void setRecurringAlarm(Context ctx) {
  scheduleRecurringAlarm(ctx, PlusService.class, true);
}

public static void cancelRecurringAlarm(Context ctx) {
  scheduleRecurringAlarm(ctx, PlusService.class, false);
}


}// end class PlusService
public abstract class SimplePlusIntentService
    extends IntentService
    implements GooglePlayServicesClient.ConnectionCallbacks,
               GooglePlayServicesClient.OnConnectionFailedListener,
               PlusClient.OnPersonLoadedListener,
               PlusClient.OnPeopleLoadedListener
{
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// abstract methods
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

protected abstract void onServiceStarted();

protected abstract void onCircleDataLoaded(ConnectionResult result, PersonBuffer persons, String nextPageToken);

protected abstract void onProfileDataLoaded(ConnectionResult result, Person person);

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// data
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * {@link PlusClient} instance managed by this service; all the 'work' happens in the callback
 * methods that are registered with this object, and orchestrated by it (on the main thread)
 */
protected PlusClient  plusClient;
/** used to keep track of whether {@link #plusClient} can disconnect or not */
protected TaskMonitor taskMonitor;

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// constructor
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** @param name only useful for debugging */
public SimplePlusIntentService(String name) {
  super(name);
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// android service lifecycle hooks
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * this simply tells the {@link PlusClient#connect()} to run, which will cause
 * {@link #onConnected(Bundle)}, where all the real work actually happens.
 */
protected void onHandleIntent(Intent intent) {

  Log.i("Social", "PlusService Service Started!");

  plusClient = new PlusClient.Builder(this, this, this)
      .setScopes(PlusUtils.getScopeArray(getApplicationContext()))
      .setVisibleActivities(PlusUtils.getMomentTypeArray(getApplicationContext()))
      .build();

  plusClient.connect();

}

public void onCreate() {
  super.onCreate();
  taskMonitor = new TaskMonitor();
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// callbacks
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public void onPeopleLoaded(ConnectionResult result, PersonBuffer persons, String nextPageToken) {
  try {
    onCircleDataLoaded(result, persons, nextPageToken);
  }
  finally {
    _initiateDisconnect(TaskMonitor.TaskName.CircleInformation);
  }
}

public void onPersonLoaded(ConnectionResult result, Person person) {
  try {
    onProfileDataLoaded(result, person);
  }
  finally {
    _initiateDisconnect(TaskMonitor.TaskName.MyProfileInformation);
  }
}

protected void _initiateDisconnect(TaskMonitor.TaskName task) {

  // mark this task as complete
  taskMonitor.done(task);
  Log.i("Social", "PlusService - marking task as done" + task.toString());

  // check to see if all tasks are done before exiting
  if (taskMonitor.areAllDone()) {
    plusClient.disconnect();
    Log.i("Social", "PlusService - disconnected!");
  }
  else {
    Log.i("Social", "PlusService - can't disconnect yet - a task is pending execution!");
  }

}

/**
 * this is where the actual work of this service happens.
 * <p/>
 * only do something if the {@link PlusClient} can connect (ie, the user has already
 * granted their consent earlier
 */
public void onConnected(Bundle bundle) {
  try {
    onServiceStarted();
    plusClient.loadPerson(this, "me");
    plusClient.loadPeople(this, Person.Collection.VISIBLE);
  }
  catch (Exception e) {
    AndroidUtils.logErr(IconPaths.Social, "PlusService had a problem", e);
  }
}

/** ignore this */
public void onDisconnected() {
  Log.i("Social", "PlusService - PlusClient disconnected, ending service");
}

/** ignore this */
public void onConnectionFailed(ConnectionResult result) {
  Log.i("Social", "PlusService - PlusClient connection failed, ending service");
}


//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// determine when you can call PlusClient.disconnect
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * holds information on whether all the tasks are completed or not;
 * used to decide when to disconnect {@link #plusClient}
 */
static class TaskMonitor {

  /** list all the tasks you want monitored */
  enum TaskName {
    MyProfileInformation, CircleInformation
  }

  HashMap<TaskName, Boolean> doneMap = new HashMap<TaskName, Boolean>();

  /** init the {@link #doneMap} */
  public TaskMonitor() {
    for (TaskName task : TaskName.values()) {
      notDone(task);
    }
  }

  /** mark a {@link TaskName} not done */
  public void notDone(TaskName task) {
    doneMap.put(task, false);
  }

  /** mark a {@link TaskName} done */
  public void done(TaskName task) {
    doneMap.put(task, true);
  }

  /** check to see if a {@link TaskName} is done or not */
  public boolean isDone(TaskName task) {
    try {
      return doneMap.get(task);
    }
    catch (Exception e) {return false;}
  }

  /** check to see if all {@link TaskName} are done or not */
  public boolean areAllDone() {
    boolean allDone = true;
    for (TaskName task : TaskName.values()) {
      allDone = isDone(task) && allDone;
    }
    return allDone;
  }
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// alarm
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * helper method to create a recurring alarm that's used to fire off this intent service.
 * there's no need to save a reference to the previously created alarm (in the case of removing
 * the alarm), since you can just create a matching {@link PendingIntent}, see docs
 * <a href="http://goo.gl/9izcQ">here</a>.
 *
 * @param ctx    caller has to supply this since this is a static method
 * @param enable true means create the alarm, and start the service (but this might not happen until the 2nd alarm
 *               cycle, and this is non deterministic, since this is inexact repeating).
 *               false means cancel it
 */
public static void scheduleRecurringAlarm(Context ctx, Class claz, boolean enable) {
  PendingIntent pendingIntent = PendingIntent.getService(ctx,
                                                         -1,
                                                         new Intent(ctx, claz),
                                                         PendingIntent.FLAG_UPDATE_CURRENT
  );
  AlarmManager mgr = (AlarmManager) ctx.getSystemService(Context.ALARM_SERVICE);
  if (enable) {
    // remove any previously scheduled alarms that match the pendingIntent
    mgr.cancel(pendingIntent);
    // add an alarm to fire the pendingIntent
    mgr.setInexactRepeating(AlarmManager.RTC,
                            java.lang.System.currentTimeMillis() + MyIntentServiceStartDelay_ms,
                            MyIntentServiceRepeatDelay_ms,
                            //AlarmManager.INTERVAL_FIFTEEN_MINUTES,
                            pendingIntent);
    AndroidUtils.log(Social, "PlusService - alarm added to Android OS");
  }
  else {
    // this cancels all alarms that match the pendingIntent
    mgr.cancel(pendingIntent);
    AndroidUtils.log(Social, "PlusService - alarm removed from Android OS");
  }
}

}//end class SimplePlusIntentService

# Activity Lifecycle
https://github.com/xxv/android-lifecycle/blob/master/README.md

Lifecycle Method	Description	Common Uses
onCreate()	The activity is starting (but not visible to the user)	Most of the activity initialization code goes here. This is where you setContentView() for the activity, initialize views, set up any adapters, etc.
onStart()	The activity is now visible (but not ready for user interaction)	This lifecycle method isn't used much, but can come in handy to register a BroadcastReceiver to monitor for changes that impact the UI (since the UI is now visible to the user).
onResume()	The activity is now in the foreground and ready for user interaction	This is a good place to start animations, open exclusive-access devices like the camera, etc.
onPause()	Counterpart to onResume(). The activity is about to go into the background and has stopped interacting with the user. This can happen when another activity is launched in front of the current activity.	It's common to undo anything that was done in onResume() and to save any global state (such as writing to a file).
onStop()	Counterpart to onStart(). The activity is no longer visible to the user.	It's common to undo anything that was done in onStart().
onDestroy()	Counterpart to onCreate(...). This can be triggered because finish() was called on the activity or the system needed to free up some memory.	It's common to do any cleanup here. For example, if the activity has a thread running in the background to download data from the network, it may create that thread in onCreate() and then stop the thread here in onDestroy()
onRestart()	Called when the activity has been stopped, before it is started again	It isnt very common to need to implement this callback.

# Recreating an Activity
https://developer.android.com/training/basics/activity-lifecycle/recreating.html#SaveState
. khi ta chủ động hủy activity:
	presses the Back button
	your activity signals its own destruction by calling finish(). 
	=> nhớ là nếu chủ động thì ko có gọi "onSaveInstanceState"

. khi system phải hủy app của ta do:
	+ The system may also destroy your activity if it's currently stopped and hasn't been used in a long time.
	+ the foreground activity requires more resources so the system must shut down background processes to recover memory. -> app sử dụng quá nhiều memory.
	+ rotates the screen.
	+ b/c of memory pressure or configuration change
	-> là do system nen nó cho ta dc quyền save state cua activity nhờ "Bundle".
. Mặc dịnh nếu ta ko save gì thì system tự save trong bundle các state của "view" vd như:
	+ such as the text value entered into an EditText object.
	+ vị trí trong list.
	-> if your activity instance is destroyed and recreated, the state of the layout is restored to its previous state with no code required by you.
	-> In order for the Android system to restore the state of the views in your activity, each view must have a unique ID, supplied by the android:id attribute.

. if your activity might have more state information that you'd like to restore, such as member variables that track the user's progress in the activity.
	+ To save additional data about the activity state, you must override the onSaveInstanceState() callback method. lưu trong bundle
	static final String STATE_SCORE = "playerScore";
	static final String STATE_LEVEL = "playerLevel";
	...

	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
	    // Save the user's current game state
	    savedInstanceState.putInt(STATE_SCORE, mCurrentScore);
	    savedInstanceState.putInt(STATE_LEVEL, mCurrentLevel);
	    
	    // Always call the superclass so it can save the view hierarchy state
	    super.onSaveInstanceState(savedInstanceState);
	}

	+  So why not just save state in onPause? 
	Just because the activity loses focus doesnt mean it has been killed. 
	It is still in memory. Basically you dont want to save state every time you are paused but rather when you are paused and about to become invisible (i.e go from foreground to background).

	+ So what should you do in onPause? 
	Ideally you should release resources that drain your battery e.g network connections, turn off geo or accelerometer, pause a video (all of this depends on your app). 
	And restore these resources in onResume which, as you might have guessed, gets called when your activity gains focus.

	+ cần restore, thì bundle cũ dc pass wa 2 hàm là "onCreate(), onRestoreInstanceState()"
	Because the onCreate() method is called whether the system is creating a new instance of your activity or recreating a previous one, you must check whether the state Bundle is null before you attempt to read it. 
	If it is null, then the system is creating a new instance of the activity, instead of restoring a previous one that was destroyed.
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState); // Always call the superclass first
	   
	    // Check whether we're recreating a previously destroyed instance
	    if (savedInstanceState != null) {
	        // Restore value of members from saved state
	        mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
	        mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
	    } else {
	        // Probably initialize members with default values for a new instance
	    }
	    ...
	}

	Instead of restoring the state during onCreate() you may choose to implement onRestoreInstanceState(), which the system calls after the onStart() method.
	The system calls onRestoreInstanceState() only if there is a saved state to restore, so you do not need to check whether the Bundle is null:

	public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Always call the superclass so it can restore the view hierarchy
	    super.onRestoreInstanceState(savedInstanceState);
	   
	    // Restore state members from saved instance
	    mCurrentScore = savedInstanceState.getInt(STATE_SCORE);
	    mCurrentLevel = savedInstanceState.getInt(STATE_LEVEL);
	}

	+ Are onCreate and onRestoreInstanceState mutually exclusive?
	onRestoreInstanceState is redundant because you can easily restore state in onCreate.
	So for best practice, lay out your view hierarchy in onCreate and restore the previous state in onRestoreInstanceState. 
	If you do that, anyone who subclasses your Activity can chose to override your onRestoreInstanceState to augment or replace your restore state logic.


# Saving and Restoring Fragment State
Fragments also have a onSaveInstanceState() method which is called when their state needs to be saved:

public class MySimpleFragment extends Fragment {
    private int someStateValue;
    private final String SOME_VALUE_KEY = "someValueToSave";
   
    // Fires when a configuration change occurs and fragment needs to save state
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(SOME_VALUE_KEY, someStateValue);
        super.onSaveInstanceState(outState);
    }
}

Then we can pull data out of this saved state in onCreateView:

public class MySimpleFragment extends Fragment {
   // ...

   // Inflate the view for the fragment based on layout XML
   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.my_simple_fragment, container, false);
        if (savedInstanceState != null) {
            someStateValue = savedInstanceState.getInt(SOME_VALUE_KEY);
            // Do something with value if needed
        }
        return view;
   }
}


# Chú ý: 
This requires us to be careful to include a tag for lookup whenever putting a fragment into the activity within a transaction:

public class ParentActivity extends AppCompatActivity {
    private MySimpleFragment fragmentSimple;
    private final String SIMPLE_FRAGMENT_TAG = "myfragmenttag";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // ... fragment lookup or instantation from above...
        // Always add a tag to a fragment being inserted into container
        if (!fragmentSimple.isInLayout()) {
            getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, fragmentSimple, SIMPLE_FRAGMENT_TAG)
                .commit();
        }
    }
}
With this simple pattern, we can properly re-use fragments and restore their state across configuration changes.

# Retaining Fragments
        setRetainInstance(true);
https://gist.github.com/PhongHuynh93/8450a0c440f368a8ab9f0214c517f52b#file-1_fragmentlifecycle-java-L1-L7


# Locking Screen Orientation
<activity
    android:name="com.techblogon.screenorientationexample.MainActivity"
    android:screenOrientation="portrait"
    android:label="@string/app_name" >
    <!-- ... -->
</activity>
Now that activity is forced to always be displayed in "portrait" mode.

# Manually Managing Configuration Changes
If your application doesnt need to update resources during a specific configuration change and you have a performance limitation that requires you to avoid the activity restart, then you can declare that your activity handles the configuration change itself, which prevents the system from restarting your activity.

<activity android:name=".MyActivity"
          android:configChanges="orientation|screenSize|keyboardHidden"
          android:label="@string/app_name">

Now, when one of these configurations change, the activity does not restart but instead receives a call to onConfigurationChanged():

##############################################################################
Activity Revival and the case of the Rotating Device
https://medium.com/google-developers/activity-revival-and-the-case-of-the-rotating-device-167e34f9a30d#.xsif47g2l
##############################################################################
Android: is onDestroy the new onStop?
http://curioustechizen.blogspot.de/2013/01/android-ondestroy-is-new-onstop.html
##############################################################################
##############################################################################
##############################################################################
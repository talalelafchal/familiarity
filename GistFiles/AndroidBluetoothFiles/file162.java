public class PhoneAnswerIntentService extends IntentService {
    /**
     * Creates an IntentService.  Invoked by the parent class's constructor.
     */
    public PhoneAnswerIntentService() {
        super("PhoneAnswerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Turn the ringer on if it isn't already, so we know the phone is ringing and about to be answered.
        switch (audioManager.getRingerMode()) {
            // Fall through since we want Silent and Vibrate to do the same thing
            case AudioManager.RINGER_MODE_SILENT:
            case AudioManager.RINGER_MODE_VIBRATE:
                audioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
                break;
            default:
                break;
        }

        // Let the phone ring for a short period, so that I can notice that it is about to answer itself.
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            // That's fine, we can continue on.
        }

        // Make sure the phone is still ringing
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        switch (telephonyManager.getCallState()) {
            case TelephonyManager.CALL_STATE_RINGING:
                //Attempt to answer the call via AIDL.
                try {
                    Class aClass = Class.forName(telephonyManager.getClass().getName());
                    Method aMethod = aClass.getDeclaredMethod("getITelephony");
                    aMethod.setAccessible(true);
                    ITelephony iTelephony = (ITelephony) aMethod.invoke(telephonyManager);

                    // Silence the ringer and then answer the call
                    iTelephony.silenceRinger();
                    iTelephony.answerRingingCall();

                } catch (Exception e) {
                    // catch one of the many exceptions: ClassNotFoundException, InvocationTargetException, NoSuchMethodException, RemoteException, IllegalAccessException
                    Log.e("PhoneAnswerIntentService", "Failed to Answer Via AIDL, Falling Back to Headset");

                    // If we failed to answer the call via AIDL, fall back to simulating a press of a Bluetooth  headset.
                    // Simulate a press of the headset button to pick up the call
                    Intent buttonDown = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    buttonDown.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK));
                    sendOrderedBroadcast(buttonDown, "android.permission.CALL_PRIVILEGED");

                    // Froyo and above answer the call on buttonUp instead of buttonDown, so simulate that.
                    Intent buttonUp = new Intent(Intent.ACTION_MEDIA_BUTTON);
                    buttonUp.putExtra(Intent.EXTRA_KEY_EVENT, new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK));
                    sendOrderedBroadcast(buttonUp, "android.permission.CALL_PRIVILEGED");
                }
                break;
            default:
                // If it's not ringing anymore, go ahead and stop trying.
                return;
        }

        return;
    }
}
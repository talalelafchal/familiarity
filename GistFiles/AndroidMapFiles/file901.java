public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Profiler p = new Profiler();
        p.profileClass("com.android.internal.telephony.IccSmsInterfaceManager");
        p.profileClass("com.android.internal.telephony.Phone");
        p.profileClass("com.android.internal.telephony.PhoneFactory");
        p.profileClass("com.android.internal.telephony.SMSDispatcher");
        p.profileClass("android.telephony.SmsManager");
        p.profileClass("android.telephony.SmsMessage");
        p.profileClass("com.android.internal.telephony.ISms");
        p.logcatBigText("Profiler");

    }
}
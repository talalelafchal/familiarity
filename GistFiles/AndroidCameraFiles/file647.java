import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Camera;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final int DELAY_MILLIS = 10000;
    public static final int FLASH_ON_TIME = 1000;
    boolean locked = false;
    Handler handler;
    NfcAdapter nfcAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        handler = new Handler();
    }

    @Override
    public void onResume() {
        super.onResume();
        //start NFC here, when app is foregrounded
        initNfc();
    }

    //this whole method was ripped pretty much directly from: http://goo.gl/upS8b1
    private void initNfc() {
        nfcAdapter = NfcAdapter.getDefaultAdapter(getApplicationContext());

        PendingIntent mPendingIntent = PendingIntent.getActivity(this, 0, new Intent(
                this, getClass())
                .addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP), 0);

        IntentFilter ndef = new IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);
        IntentFilter ndef2 = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);
        IntentFilter ndef3 = new IntentFilter(NfcAdapter.ACTION_TECH_DISCOVERED);

        try {
            ndef2.addDataType("*/*");
        } catch (IntentFilter.MalformedMimeTypeException e) {
            throw new RuntimeException("fail", e);
        }

        IntentFilter[] mFilters = new IntentFilter[]{ndef, ndef2, ndef3};

        String[][] mTechLists = new String[][]{new String[]{
                android.nfc.tech.NfcA.class.getName(),
                android.nfc.tech.IsoDep.class.getName()}};

        nfcAdapter.enableForegroundDispatch(this, mPendingIntent, mFilters, mTechLists);
    }

    @Override
    public void onPause() {
        super.onPause();
        //kill NFC as app is backgrounded
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(this);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        switch (intent.getAction()) {
            case "android.nfc.action.TAG_DISCOVERED":
            case "android.nfc.action.NDEF_DISCOVERED":
            case "android.nfc.action.TECH_DISCOVERED":
                //We got one!
                startPost();
                break;
        }
    }

    private void startPost() {
        //if (locked), we're not done waiting for the last request
        if (locked) return;

        //post message, and prevent any more requests from going out for the next 10 seconds
        post();
        locked = true;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                locked = false;
            }
        }, DELAY_MILLIS);
    }

    //turn flash on for FLASH_ON_TIME, then back off
    private void notifyUserWithFlash() {
        //blah, blah deprecated blah, blah
        final Camera camera = Camera.open();
        Camera.Parameters p = camera.getParameters();
        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        camera.setParameters(p);
        camera.startPreview();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                camera.stopPreview();
                camera.release();
            }
        }, FLASH_ON_TIME);
    }
    
    //quick and dirty post request
    private void post() {
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest sr = new StringRequest(Request.Method.POST, "https://hooks.slack.com/services/NOT_A_REAL_URL", new com.android.volley.Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                notifyUserWithFlash();
            }
        }, new com.android.volley.Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Uhhhhh
            }
        }) {

            @Override
            public byte[] getBody() throws AuthFailureError {
                //Your POST body should go here
                //This JSON formatting is Slack-specific. <!here> -> @here
                return "{\"text\":\"<!here> Ding Dong! Can someone get the door?\"}".getBytes();
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Content-Type", "application/x-www-form-urlencoded");
                return params;
            }
        };
        queue.add(sr);
    }
}

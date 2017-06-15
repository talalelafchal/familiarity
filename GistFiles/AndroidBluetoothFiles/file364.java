package com.thelogbox;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

class ViewConnnTest extends LinearLayout implements OnClickListener {

  Context context = null;
  TextView tv = null;
  ConnectivityManager connman = null;
  NetworkInfo netinfo = null;

  public ViewConnnTest(Context context) {

    super(context);
    this.context = context;
    tv = new TextView(context);
    addView(tv);
    setOnClickListener(this);

  }

  public void onClick(View v){

    StringBuilder sb = new StringBuilder();
    connman = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    netinfo = connman.getActiveNetworkInfo(); 

    switch(netinfo.getType()) {
      case ConnectivityManager.TYPE_MOBILE:
        sb.append("\nMobile data OK.");
        break;
      case ConnectivityManager.TYPE_WIFI:
        sb.append("\nWIFI OK.");
        break;
      case ConnectivityManager.TYPE_WIMAX:
        sb.append("\nWIMAX OK.");
        break;
      case ConnectivityManager.TYPE_ETHERNET:
        sb.append("\nOn Ethernet.");
        break;
      case ConnectivityManager.TYPE_BLUETOOTH:
        sb.append("\nBluetooth OK.");
        break;
      default:
        sb.append("\nNot connected. Not OK.");
    }
    sb.append("\nState= "); 
    sb.append(netinfo.getState().toString());

    tv.setText(sb.toString());
  }
}

public class ConnTest extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(new ViewConnnTest(this));
    }
}
package vn.tpf.andping;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class MainActivity extends Activity {

	private Button btnPing, btnStop;
	private EditText edtIp;
	private TextView txRs, txNPackets;
	private ProgressBar prStart;
	private SeekBar skTimes;
	private PingTask pt;
	private ScrollView svScroll;	
	private static final int TASK_DONE = 1;
	private static final int TASK_CANCELLED = 0;
	private static final int ERROR = -1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ping_layout);
		
		//Views
		btnPing = (Button)findViewById(R.id.btPing);
		edtIp = (EditText)findViewById(R.id.edAddress);
		txRs = (TextView)findViewById(R.id.txResult);
		prStart = (ProgressBar)findViewById(R.id.progressBar);
		skTimes = (SeekBar)findViewById(R.id.seekTimes);
		btnStop = (Button)findViewById(R.id.btStop);
		txNPackets = (TextView)findViewById(R.id.txPackets);
	
		//Add On Click Listener
		btnPing.setOnClickListener(lstPing);
		btnStop.setOnClickListener(lstStop);	
		skTimes.setOnSeekBarChangeListener(lstSeek);
	}
	
	
	//Execute when click ping
	android.view.View.OnClickListener lstPing = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String address = edtIp.getText().toString();			
			int times = skTimes.getProgress();
			pt = new PingTask(times);
			pt.execute(address);
		}
	};

	
	//Cancel ping
	
	View.OnClickListener lstStop = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Implement onStopPing code
			pt.cancel(true);
		}
	};
	
	
	//Change total number packet
	SeekBar.OnSeekBarChangeListener lstSeek = new SeekBar.OnSeekBarChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			if (progress == 0){
				txNPackets.setText("Unlimit");
			}
			else{
				txNPackets.setText(progress + " Packets");
			}
			
		}
	};
	
	public static String executeCmd(String cmd, boolean sudo){
	    try {

	        Process p;
	        if(!sudo)
	            p= Runtime.getRuntime().exec(cmd);
	        else{
	            p= Runtime.getRuntime().exec(new String[]{"su", "-c", cmd});
	        }
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));

	        String s;
	        String res = "";
	        
	        while ((s = stdInput.readLine()) != null) {
	        	
	            res += s + "\n";
	            Log.v("rs", s);
	        }
	        p.destroy();
	        return res;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return "";

	}
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

private class PingTask extends AsyncTask<String, String, Integer>{
	
	private int times;
	
	public PingTask(int times){
		this.times = times;
	}
	
	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		prStart.setVisibility(View.VISIBLE);
		txRs.setText("");
	}
	
	@Override
	protected void onProgressUpdate(String... values) {
		// TODO Auto-generated method stub
		txRs.append("\n" + values[0]);
		
	}
	@Override
	protected Integer doInBackground(String... params) {
		// TODO Auto-generated method stub
		String address = params[0];
		String cmd = "";
		if (times == 0){
			cmd  = "ping " + address;
		}
		else {
			cmd  = "ping -c " + times + " " + address;
		}
		try {

	        Process p;	     
	        
	        p= Runtime.getRuntime().exec(cmd);	       
	        BufferedReader stdInput = new BufferedReader(new InputStreamReader(p.getInputStream()));
	        String s ;	        
	        while ((s  = stdInput.readLine())!= null) {	           
	            publishProgress(s);
	            Log.v("rs", s);
	            if (isCancelled()){
	            	return TASK_CANCELLED;
	            }
	        }
	        p.destroy();
	        return TASK_DONE;
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	    return ERROR;
		
	}
	
	@Override
	protected void onPostExecute(Integer result) {
		// TODO Auto-generated method stub		
		prStart.setVisibility(View.GONE);
		switch (result) {
		case TASK_DONE:
			txRs.append("\n Task Done");
			break;
		case ERROR:
			txRs.append("Sorry got some error");
		case TASK_CANCELLED:
			txRs.append("\n Task canceled by user");
			break;
		default:
			break;
		}
	}

	@Override
	protected void onCancelled() {
		// TODO Auto-generated method stub
		txRs.append("\n Task canceled by user");
		prStart.setVisibility(View.GONE);
	}
	
	
	
	public int getTimes() {
		return times;
	} 

	public void setTimes(int times) {
		this.times = times;
	}
	
}
}

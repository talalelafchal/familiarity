package linz.jku;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

public class ProgressSharing extends ProgressDialog {

	public ProgressSharing(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.shake);
	        
	    }

}

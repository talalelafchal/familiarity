/* this is not a mandatory class for the process of doing HTTP requests. It is only neccessary for those working with API.
* it is important to notice that it might be changed regarding the needs of the project
*/
package br.com.say2me.app.async;

import br.com.say2me.app.MainActivity;
import android.os.AsyncTask;

public class AuthLayer extends AsyncTask<Object, Object, Object>{

	private MainActivity activity;
	private int status;
	
	public AuthLayer(MainActivity activity){
		this.activity = activity;
	}
	
	// This method can be implemented in children classes as needed (GET/POST)
	@Override
	protected Object doInBackground(Object... params) {
		return null;
	}
	
	// This method is used to ensure that all children classes of authLayer do an authorization prosess in case of 401
	@Override
	protected void onPostExecute(Object result) {
		super.onPostExecute(result);
		
		this.checkAuth();
		this.postExecuteAction(result);
	}
	
	// This is the method to be called on after execution processes
	protected void postExecuteAction(Object result){}
	
	protected void checkAuth(){
		if(this.status == 401){ // if it gets authorization problems
			new SendAuthData(this.activity).execute(); // utilizes another class to handle the authorization
		}
	}
	
	// helper methods needed in childrend classes to access the status of the resquest	
	protected MainActivity getActivity(){
		return this.activity;
	}
	
	protected void setProtocolStatus(int status){
		this.status = status;
	}
	
	protected int getProtocolStatus(){
		return this.status;
	}
}


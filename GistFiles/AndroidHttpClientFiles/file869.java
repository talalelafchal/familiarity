package com.mmyuksel.proje;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import android.app.ProgressDialog;
import android.os.AsyncTask;

class Common_UserList_Async extends AsyncTask<String, Integer, List<ListObject>> {

	private ProgressDialog dialog;
	private UserListActivity _activity;
	List<ListObject>  DataList = new ArrayList<ListObject>();

	public Common_UserList_Async(UserListActivity activity) {
		dialog = new ProgressDialog(activity);
		this._activity = activity;
	}
 
	@Override
	protected void onPreExecute() {
		// super.onPreExecute();

		dialog.setIndeterminate(true);
		dialog.setCancelable(false);
		dialog.setTitle("Please Wait");
		dialog.setMessage("Loading Users..");
		dialog.setMax(100);
		dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		dialog.show();
	}

	@Override
	protected List<ListObject> doInBackground(String... params) {
		try {
			publishProgress(10);
			String ret = Common.connect(params[0]);
			publishProgress(20);
			ret = ret.trim();
			JSONObject jsonObj = new JSONObject(ret);
			publishProgress(40);
			 DataList =Common.UserList(jsonObj, _activity);
			return  DataList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	protected void onProgressUpdate(String... progress) {
		dialog.setProgress(Integer.parseInt(progress[0]));

	}

	protected void onPostExecute(List<ListObject> result) {
		// TODO Auto-generated method stub
		super.onPostExecute(result);
		if (result != null) {
			
			_activity.setList(result);
			
		} else {

			// Toast.makeText(mcon, "test", Toast.LENGTH_LONG).show();
		}
		if (dialog.isShowing())
			dialog.dismiss();

	}
}

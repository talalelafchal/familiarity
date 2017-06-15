package com.mmyuksel.proje;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.Context;
import android.widget.Toast;

public class Common {

	static Context mContext;

	public static List<ListObject> UserList(JSONObject JsonData, Context mcon) {

		try {

			List<ListObject> dataList = new ArrayList<ListObject>();
			JSONArray jsonArray = JsonData.getJSONArray("GetListUserResult");

			for (int i = 0; i < jsonArray.length(); i++) {

				JSONObject jObject = (JSONObject) jsonArray.get(i);

				ListObject o = new ListObject();
				o.ID = jObject.getInt("UserID");
				o.Label = jObject.getString("UserKimlikNo");

				dataList.add(o);
			}

			return dataList;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


}

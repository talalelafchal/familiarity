package com.irontec.saremobile.fragments;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.alexd.jsonrpc.JSONRPCClient;
import org.alexd.jsonrpc.JSONRPCException;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.irontec.saremobile.LoginActivity;
import com.irontec.saremobile.R;
import com.irontec.saremobile.api.NetworkingClient;
import com.irontec.saremobile.database.AgendaDataSource;
import com.irontec.saremobile.database.CallHistoryDataSource;
import com.irontec.saremobile.database.CallStatus;
import com.irontec.saremobile.database.UserDataSource;
import com.irontec.saremobile.helpers.RightDrawableOnTouchListener;
import com.irontec.saremobile.helpers.Utils;
import com.irontec.saremobile.models.APIError;
import com.irontec.saremobile.models.Contact;
import com.irontec.saremobile.models.User;

public class DialerFragment extends Fragment implements OnClickListener {

	private final static String TAG = DialerFragment.class.getSimpleName();
	
	private TextView phoneNumber; 

	private Button one;
	private Button two;
	private Button three;
	private Button four;
	private Button five;
	private Button six;
	private Button seven;
	private Button eight;
	private Button nine;
	private Button zero;
	private Button asterisk;
	private Button hashtag;
	private ImageButton call;

	private final static String ONE = "1";
	private final static String TWO = "2";
	private final static String THREE = "3";
	private final static String FOUR = "4";
	private final static String FIVE = "5";
	private final static String SIX = "6";
	private final static String SEVEN = "7";
	private final static String EIGHT = "8";
	private final static String NINE = "9";
	private final static String ZERO = "0";
	private final static String ASTERISK = "*";
	private final static String HASHTAG = "#";

	private AgendaDataSource datasource;
	private CallHistoryDataSource callDatasource;
	private List<Contact> contacts = new ArrayList<Contact>();
	private Contact foundContact = new Contact();
	private User mUser;
	private UserDataSource userDatasource;
	private String mPhone;
	private NetworkingClient nc = NetworkingClient.getInstance();

	public static DialerFragment newInstance() {
		DialerFragment f = new DialerFragment();
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		datasource = new AgendaDataSource(getActivity());
		callDatasource = new CallHistoryDataSource(getActivity());
		userDatasource = new UserDataSource(getActivity());
		userDatasource.open();
		callDatasource.open();
		contacts = datasource.getAllContacts();
		mUser = userDatasource.getUser();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		setHasOptionsMenu(true);
		View view = inflater.inflate(R.layout.fragment_dialer, container, false);

		phoneNumber = (TextView) view.findViewById(R.id.phoneNumber);
		phoneNumber.setOnTouchListener(new RightDrawableOnTouchListener(phoneNumber) {
			@Override
			public boolean onDrawableTouch(final MotionEvent event) {
				String str = phoneNumber.getText().toString().trim();
				if(str.length()!=0){
					str  = str.substring( 0, str.length() - 1 ); 
					phoneNumber.setText ( str );
				}
				return true;
			}
		});

		one = (Button) view.findViewById(R.id.one);
		two = (Button) view.findViewById(R.id.two);
		three = (Button) view.findViewById(R.id.three);
		four = (Button) view.findViewById(R.id.four);
		five = (Button) view.findViewById(R.id.five);
		six = (Button) view.findViewById(R.id.six);
		seven = (Button) view.findViewById(R.id.seven);
		eight = (Button) view.findViewById(R.id.eight);
		nine = (Button) view.findViewById(R.id.nine);
		zero = (Button) view.findViewById(R.id.zero);
		asterisk = (Button) view.findViewById(R.id.asterisk);
		hashtag = (Button) view.findViewById(R.id.hashtag);
		call = (ImageButton) view.findViewById(R.id.call);

		one.setOnClickListener(this);
		two.setOnClickListener(this);
		three.setOnClickListener(this);
		four.setOnClickListener(this);
		five.setOnClickListener(this);
		six.setOnClickListener(this);
		seven.setOnClickListener(this);
		eight.setOnClickListener(this);
		nine.setOnClickListener(this);
		zero.setOnClickListener(this);
		asterisk.setOnClickListener(this);
		hashtag.setOnClickListener(this);
		call.setOnClickListener(this);

		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.one:
			phoneNumber.append(ONE);
			break;
		case R.id.two:
			phoneNumber.append(TWO);
			break;
		case R.id.three:
			phoneNumber.append(THREE);
			break;
		case R.id.four:
			phoneNumber.append(FOUR);
			break;
		case R.id.five:
			phoneNumber.append(FIVE);
			break;
		case R.id.six:
			phoneNumber.append(SIX);
			break;
		case R.id.seven:
			phoneNumber.append(SEVEN);
			break;
		case R.id.eight:
			phoneNumber.append(EIGHT);
			break;
		case R.id.nine:
			phoneNumber.append(NINE);
			break;
		case R.id.zero:
			phoneNumber.append(ZERO);
			break;
		case R.id.asterisk:
			phoneNumber.append(ASTERISK);
			break;
		case R.id.hashtag:
			phoneNumber.append(HASHTAG);
			break;
		case R.id.call:
			mPhone = phoneNumber.getText().toString();
			ArrayList<String> phones = new ArrayList<String>();
			phones.add(mPhone);
			foundContact.setPhones(phones);
			for (Contact contact : contacts) {
				if (contact.getPhones().contains(mPhone)) {
					foundContact.setId(contact.getId());
					foundContact.setName(contact.getName());
					foundContact.setPhoto(contact.getPhoto());
					break;
				}
			}
			new CallTask().execute();
			break;
		default:
			break;
		}

	}

	/*
	 * ============ CALL =============
	 * {
	 *     "method": "call",
	 *     "params": {
	 *         "userId": "1",
	 *         "destinyNumber": "1234",
	 *         "tokenId": "7e89fab9722ff32f486f5331b0e9e319"
	 *         "device": "android",
	 *         "deviceUuid": "54545465468798721321654687687456",
	 *         "countryCode": "es"
	 *     },
	 *     "id": 1234
	 *  }
	 */

	private class CallTask extends AsyncTask<String, Void, String> {
		protected String doInBackground(String... urls) {
			JSONRPCClient client = nc.getClient();
			Object[] parameters = {
					mUser.getId(), mPhone, mUser.getToken(),
					Utils.getDevice(),
					Utils.getDeviceId(getActivity()),
					Utils.getNetworkCountryIso(getActivity()),
			};
			Log.d(TAG, Arrays.toString(parameters));
			JSONObject data = null;
			try {
				data = client.callJSONObject(NetworkingClient.CALL, parameters);
			} catch (JSONRPCException e) {
				e.printStackTrace();
			}
			return (data == null) ? null : data.toString();
		}
		protected void onPostExecute(String data) {
			if (data != null) {
				JSONObject response = null;
				try {
					response = new JSONObject(data);
					Log.d(TAG, response.toString());
					if (response.has("error")) {
						APIError error = new APIError(getActivity().getBaseContext(), response);
						Toast.makeText(getActivity(), error.toString(), Toast.LENGTH_SHORT).show();
						if (error.getError() == 2) {
							authExpired();
						}
						callDatasource.registerCall(foundContact, new Date(), CallStatus.NO_CONECTADO);
					} else if (response.has("call")) {
						callDatasource.registerCall(foundContact, new Date(), CallStatus.CONECTADO);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	private void authExpired() {
		userDatasource.deleteUser(mUser);
		userDatasource.close();
		Intent intent = new Intent(getActivity(), LoginActivity.class);
		getActivity().startActivity(intent);
	}

	@Override
	public void onResume() {
		callDatasource.open();
		super.onResume();
		contacts = datasource.getAllContacts();
	}

	@Override
	public void onPause() {
		callDatasource.close();
		super.onPause();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
	}

}

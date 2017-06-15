package com.abc.drawer_fragment;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.abc.model.R;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class People_tag extends Fragment {

	public Button peopleButton, tagButton, btntagadd;
	private EditText editname;
	public ListView listView;
	public View v;
	public ArrayList<HashMap<String, Object>> contactsArrayList;
	private ProgressDialog progressDialog;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.people_tag, container, false);

		peopleButton = (Button) v.findViewById(R.id.peopleButton);
		tagButton = (Button) v.findViewById(R.id.tagButton);
		listView = (ListView) v.findViewById(R.id.lvTAGPEOPLE);
		btntagadd = (Button) v.findViewById(R.id.button1);
		editname = (EditText) v.findViewById(R.id.editText1);
		progressDialog = new ProgressDialog(getActivity());
		getParseTagDate();

		peopleButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new People()).commit();
			}
		});

		tagButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, new People_tag()).commit();
			}
		});

		btntagadd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			/*	ParseObject testObject = new ParseObject("Tag");// DATABASE_TABLE_NAME
				testObject.put("name", editname.getText().toString());
				testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));

				testObject.saveInBackground(new SaveCallback() {

					@Override
					// 彈跳視窗
					public void done(ParseException ex) {
						// TODO Auto-generated method stub
						if (ex == null) {
							Toast.makeText(getActivity(), "存檔成功",
									Toast.LENGTH_LONG).show();
							FragmentManager fragmentManager = getFragmentManager();
							fragmentManager
									.beginTransaction()
									.replace(R.id.content_frame,
											new People_tag()).commit();
						} else {
							Toast.makeText(getActivity(),
									"存檔失敗:" + ex.getMessage().toString(),
									Toast.LENGTH_LONG).show();
						}
					}
				});*/
				
				People_tag_add ppadd = new People_tag_add();
				ppadd.setTag("");
				ppadd.setMode("add");
				Fragment fg = ppadd;
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fg).commit();
				
			}
		});

		return v;
	}

	public void setListView() {
		People_lv_BtnAdapter Btnadapter = new People_lv_BtnAdapter(
				getActivity(), contactsArrayList,
				R.layout.people_contact_entry,
				new String[] { "NAME", "NUMBER" }, new int[] {
						R.id.txtNAMEPHONE, R.id.txtDATAPHONE,
						R.id.group_list_item_text, R.id.imageButton1 },
				getFragmentManager());
		listView.setAdapter(Btnadapter);

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				final String Oid = contactsArrayList.get(position).get("ID")
						.toString();
				final String name = contactsArrayList.get(position).get("NAME")
						.toString();
				final String number = contactsArrayList.get(position)
						.get("NUMBER").toString();
				new AlertDialog.Builder(getActivity())
						.setTitle(number)
						.setItems(new String[] { "Detail","Delete" ,"Call" },
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										switch (which) {

										case 0:
											People_add ppadd = new People_add();
											ppadd.setMode("edit");
											ppadd.setID(Oid);

											Fragment fg = ppadd;
											FragmentManager fragmentManager = getFragmentManager();
											fragmentManager
													.beginTransaction()
													.replace(
															R.id.content_frame,
															fg).commit();

											break;

										case 1:
											ParseQuery<ParseObject> query = ParseQuery
											.getQuery("Client");
									// Log.v("","ID="+ID);

									query.getInBackground(
											Oid,
											new GetCallback<ParseObject>() {

												public void done(
														ParseObject gameScore,
														ParseException e) {
													// progressDialog
													// .dismiss();
													if (e == null) {

														gameScore
																.put("tag",
																		"");

														gameScore
																.saveInBackground(new SaveCallback() {

																	@Override
																	// 彈跳視窗
																	public void done(
																			ParseException ex) {
																		// TODO
																		// Auto-generated
																		// method
																		// stub
																		if (ex == null) {
																			//Toast.makeText(getActivity(),
																			//"刪除成功:"+ex.getMessage().toString(),
																			//Toast.LENGTH_LONG).show();
																			FragmentManager fragmentManager = getFragmentManager();
																			fragmentManager.beginTransaction()
																					.replace(R.id.content_frame, new People_tag()).commit();
																		} else {
																			 Toast.makeText(getActivity(),
																			 "刪除失敗:"+ex.getMessage().toString(),
																			 Toast.LENGTH_LONG).show();
																			
																		}
																	}
																});
													}
												}
											});
											
											
											

											break;
											
											
										case 2:
											Intent call = new Intent(
													Intent.ACTION_CALL, Uri
															.parse("tel:"
																	+ number));
											startActivity(call);

											break;
										}
									}

								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										// TODO Auto-generated method stub
									}
								}).show();
			}

		});
	};

	public void getParseTagDate() {
		contactsArrayList = new ArrayList<HashMap<String, Object>>();

		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> querytag = ParseQuery.getQuery("Tag");
		querytag.findInBackground(new FindCallback<ParseObject>() {

			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				if (e == null) {
					progressDialog.dismiss();
					String[] tag = new String[objects.size()];
					for (int tagi = 0; tagi < objects.size(); tagi++) {
						// HashMap<String, Object> hm = new HashMap<String,
						// Object>();
						// hm.put("ID",
						// objects.get(tagi).getObjectId().toString());
						// hm.put("NAME", "tag");
						// hm.put("NUMBER",
						// objects.get(tagi).get("name").toString());
						// contactsArrayList.add(hm);
						// Log.v("",
						// ""+objects.get(tagi).get("name").toString());
						tag[tagi] = objects.get(tagi).get("name").toString();
						Log.v("", ": "
								+ objects.get(tagi).get("name").toString());
					}
					getParseDate(tag);
				} else {
					Log.v("score", "Error: " + e.getMessage());
				}

			}
		});
		// Log.v("", ""+tag.length);

	}

	public void getParseDate(final String[] tag) {

		progressDialog.setCancelable(false);
		progressDialog.setTitle("Loading...");
		progressDialog.show();
		ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");
		// query.whereEqualTo("ID", ParseUser.getCurrentUser().getObjectId());
		query.findInBackground(new FindCallback<ParseObject>() {
			@Override
			public void done(List<ParseObject> objects, ParseException e) {
				// TODO Auto-generated method stub
				progressDialog.dismiss();
				if (e == null) {
					// Log.v("score", "Retrieved " + objects.size() +
					// " scores");
					String[][] people = new String[4][objects.size()];
					for (int i = 0; i < objects.size(); i++) {
						// HashMap<String, Object> hm = new HashMap<String,
						// Object>();
						// hm.put("ID",
						// objects.get(i).getObjectId().toString());
						// hm.put("NAME",
						// objects.get(i).get("name").toString());
						// hm.put("NUMBER",
						// objects.get(i).get("tel").toString());
						// contactsArrayList.add(hm);
						people[0][i] = objects.get(i).getObjectId().toString();
						people[1][i] = objects.get(i).get("name").toString();
						people[2][i] = objects.get(i).get("tel").toString();
						people[3][i] = objects.get(i).get("tag").toString();
						Log.v("", "" + objects.get(i).get("name").toString()
								+ "  " + objects.size());
					}
					int pi = objects.size();
					for (int tagi = 0; tagi < tag.length; tagi++) {
						HashMap<String, Object> hm = new HashMap<String, Object>();
						hm.put("ID", "");
						hm.put("NAME", "tag");
						hm.put("NUMBER", tag[tagi]);
						contactsArrayList.add(hm);
						Log.v("", "" + tag[tagi]);
						for (int peoplei = 0; peoplei < pi; peoplei++) {
							Log.v("", "tag:" + people[3][peoplei] + " name:"
									+ people[1][peoplei] + " i:"
									+ people.length);
							if (people[3][peoplei].equals(tag[tagi])) {
								HashMap<String, Object> hmp = new HashMap<String, Object>();
								hmp.put("ID", people[0][peoplei]);
								hmp.put("NAME", people[1][peoplei]);
								hmp.put("NUMBER", people[2][peoplei]);
								contactsArrayList.add(hmp);
							}
						}
					}
					// Log.v("score", "111: " + objects.size());

					setListView();

				} else {
					Log.v("score", "Error: " + e.getMessage());
				}
			}
		});

		setListView();
	}

	public void setParseData() {
		String id;
		String mimetype;

		ContentResolver contentResolver = getActivity().getContentResolver();
		// 只需要從Contacts中獲取ID，其他的都可以不要，通過查看上面編譯後的SQL語句，可以看出將第二個參數
		// 設置成null，默認返回的列非常多，是一種資源浪費。
		Cursor cursor = contentResolver
				.query(android.provider.ContactsContract.Contacts.CONTENT_URI,
						new String[] { android.provider.ContactsContract.Contacts._ID },
						null, null, null);
		while (cursor.moveToNext()) {
			ParseObject testObject = new ParseObject("Client");
			id = cursor
					.getString(cursor
							.getColumnIndex(android.provider.ContactsContract.Contacts._ID));

			// 從一個Cursor獲取所有的信息
			Cursor contactInfoCursor = contentResolver.query(
					android.provider.ContactsContract.Data.CONTENT_URI,
					new String[] {
							android.provider.ContactsContract.Data.CONTACT_ID,
							android.provider.ContactsContract.Data.MIMETYPE,
							android.provider.ContactsContract.Data.DATA1 },
					android.provider.ContactsContract.Data.CONTACT_ID + "="
							+ id, null, null);
			while (contactInfoCursor.moveToNext()) {
				mimetype = contactInfoCursor
						.getString(contactInfoCursor
								.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
				String value = contactInfoCursor
						.getString(contactInfoCursor
								.getColumnIndex(android.provider.ContactsContract.Data.DATA1));
				if (mimetype.contains("/name")) {
					System.out.println("Name=" + value);
					testObject.put("name", value);
				} else if (mimetype.contains("/email")) {
					System.out.println("Email=" + value);
					testObject.put("email", value);
				} else if (mimetype.contains("/phone")) {
					System.out.println("Tel=" + value);
					testObject.put("tel", value);
				} else if (mimetype.contains("/postal")) {
					System.out.println("Address=" + value);
					testObject.put("add", value);
				} else if (mimetype.contains("/birthday")) {
					System.out.println("birthday=" + value);
					testObject.put("birthday", value);
				}
				progressDialog.dismiss();

				// testObject.put("ID",
				// ParseUser.getCurrentUser().getObjectId());
				testObject.setACL(new ParseACL(ParseUser.getCurrentUser()));
				Log.v("", "" + mimetype);

			}
			testObject.saveInBackground(new SaveCallback() {
				@Override
				// 彈跳視窗
				public void done(ParseException ex) {
					// TODO Auto-generated method stub
					if (ex == null) {
						// Toast.makeText(getActivity(), "存檔成功",
						// Toast.LENGTH_LONG).show();
					} else {
						// Toast.makeText(getActivity(),
						// "存檔失敗:"+ex.getMessage().toString(),
						// Toast.LENGTH_LONG).show();
					}
				}
			});

			System.out.println("*********");
			contactInfoCursor.close();
		}
		cursor.close();
	}

	private void setContentView(int peopleAdd) {
		// TODO Auto-generated method stub

	}

	public class People_lv_BtnAdapter extends BaseAdapter {

		private ArrayList<HashMap<String, Object>> mAppList;
		private LayoutInflater mInflater;
		private Context mContext;
		private String[] keyString;
		private int[] valueViewID;
		private ItemView itemView;
		private List<String> ls;
		public ArrayList<HashMap<String, Object>> contactsArrayList;
		public FragmentManager fragmentManager;
		public boolean checksave;
		private ProgressDialog progressDialog;

		private class ItemView {
			TextView tvname;
			TextView tvnumber;
			TextView tvtag;
			ImageButton imgbt;

		}

		public People_lv_BtnAdapter(Context c,
				ArrayList<HashMap<String, Object>> appList, int resource,
				String[] from, int[] to, FragmentManager fm) {
			mAppList = appList;
			mContext = c;
			fragmentManager = fm;
			mInflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			keyString = new String[from.length];
			valueViewID = new int[to.length];

			System.arraycopy(from, 0, keyString, 0, from.length);
			System.arraycopy(to, 0, valueViewID, 0, to.length);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			// return 0;
			return mAppList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			// return null;
			return mAppList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			// return 0;
			return position;
		}

		@Override
		public boolean isEnabled(int position) {
			if (mAppList.get(position).get(keyString[0]).toString()
					.equals("tag")) {
				return false;
			}
			return super.isEnabled(position);
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			// return null;

			if (convertView != null) {
				itemView = (ItemView) convertView.getTag();
			} else {
				convertView = mInflater
						.inflate(R.layout.people_tag_entry, null);
				itemView = new ItemView();
				itemView.tvname = (TextView) convertView
						.findViewById(valueViewID[0]);
				itemView.tvnumber = (TextView) convertView
						.findViewById(valueViewID[1]);
				itemView.tvtag = (TextView) convertView
						.findViewById(valueViewID[2]);
				itemView.imgbt = (ImageButton) convertView
						.findViewById(valueViewID[3]);
				convertView.setTag(itemView);
			}

			// HashMap<String, Object> appInfo = mAppList.get(position);
			if (mAppList != null) {

				if (mAppList.get(position).get(keyString[0]).toString()
						.equals("tag")) {
					itemView.tvtag.setText(mAppList.get(position)
							.get(keyString[1]).toString());

					itemView.tvtag.setEnabled(false);

					itemView.tvtag.setVisibility(0);

					itemView.imgbt.setVisibility(0);
					itemView.imgbt.setOnClickListener(new ItemButton_Click(
							position));

					itemView.tvname.setVisibility(8);
					itemView.tvnumber.setVisibility(8);
				} else {
					itemView.tvtag.setEnabled(true);

					itemView.tvtag.setVisibility(8);

					itemView.imgbt.setVisibility(8);

					itemView.tvname.setVisibility(0);
					itemView.tvnumber.setVisibility(0);

					itemView.tvname.setText(mAppList.get(position)
							.get(keyString[0]).toString());
					itemView.tvnumber.setText(mAppList.get(position)
							.get(keyString[1]).toString());
				}

			}

			return convertView;
		}

		class ItemButton_Click implements OnClickListener {
			private int position;

			ItemButton_Click(int pos) {
				position = pos;
			}

			@Override
			public void onClick(View v) {
				/*int vid = v.getId();
				if (vid == itemView.imgbt.getId())
					Log.v("ola_log", mAppList.get(position).get(keyString[1])
							.toString()
							+ "=" + position);

				contactsArrayList = new ArrayList<HashMap<String, Object>>();

				LayoutInflater factory = LayoutInflater.from(mContext);
				final View v1 = factory.inflate(
						R.layout.people_tag_listview_entry, null);
				final ListView alertlistView = (ListView) v1
						.findViewById(R.id.listView1);

				ParseQuery<ParseObject> query = ParseQuery.getQuery("Client");


				query.findInBackground(new FindCallback<ParseObject>() {
					@Override
					public void done(List<ParseObject> objects, ParseException e) {
						// TODO Auto-generated method stub

						if (e == null) {
							// Log.v("score", "Retrieved " + objects.size() +
							// " scores");

							for (int i = 0; i < objects.size(); i++) {
								HashMap<String, Object> hm = new HashMap<String, Object>();
								hm.put("ID", objects.get(i).getObjectId()
										.toString());
								hm.put("NAME", objects.get(i).get("name")
										.toString());
								hm.put("NUMBER", objects.get(i).get("tel")
										.toString());
								hm.put("TAG", objects.get(i).get("tag")
										.toString());
								contactsArrayList.add(hm);

							}

							final People_lv_BtnAdapter_check Btnadapter = new People_lv_BtnAdapter_check(
									mContext, contactsArrayList,
									R.layout.people_tag_checkbox, new String[] {
											"NAME", "NUMBER", "TAG" },
									new int[] { R.id.txt1, R.id.txt2,
											R.id.txt3, R.id.checkBox1 });
							alertlistView.setAdapter(Btnadapter);

							AlertDialog.Builder dialog = new AlertDialog.Builder(
									mContext);
							dialog.setTitle("Check people of tag");
							dialog.setView(v1);
							dialog.setPositiveButton("Cancel",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface dialog,
												int whichButton) {

										}
									});
							dialog.setNegativeButton("Done",
									new DialogInterface.OnClickListener() {

										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											// TODO Auto-generated method stub
											boolean[] b = Btnadapter
													.getIsCheck();
											checksave = true;
											for (int i = 0; i < b.length; i++) {
												Log.v("", "123123 id:" + i
														+ " ck:" + b[i]);
												if (b[i]) {
													// progressDialog
													// .setCancelable(false);
													// progressDialog
													// .setTitle("Loading...");
													// progressDialog.show();
													ParseQuery<ParseObject> query = ParseQuery
															.getQuery("Client");
													// Log.v("","ID="+ID);

													query.getInBackground(
															contactsArrayList
																	.get(i)
																	.get("ID")
																	.toString(),
															new GetCallback<ParseObject>() {

																public void done(
																		ParseObject gameScore,
																		ParseException e) {
																	// progressDialog
																	// .dismiss();
																	if (e == null) {

																		gameScore
																				.put("tag",
																						mAppList.get(
																								position)
																								.get(keyString[1])
																								.toString());

																		gameScore
																				.saveInBackground(new SaveCallback() {

																					@Override
																					// 彈跳視窗
																					public void done(
																							ParseException ex) {
																						// TODO
																						// Auto-generated
																						// method
																						// stub
																						if (ex == null) {

																						} else {
																							// Toast.makeText(getActivity(),
																							// "存檔失敗:"+ex.getMessage().toString(),
																							// Toast.LENGTH_LONG).show();
																							checksave = false;
																						}
																					}
																				});
																	}
																}
															});

												}

											}
											if (checksave) {

												fragmentManager
														.beginTransaction()
														.replace(
																R.id.content_frame,
																new People_tag())
														.commit();
											}
										}
									});
							dialog.show();

						} else {
							Log.v("score", "Error: " + e.getMessage());
						}
					}
				});*/

				// SimpleAdapter adapter = new
				// SimpleAdapter(mContext,alertit,R.layout.alertdialog_listview,new
				// String[] { "1","2","3" },
				// new int[] { R.id.txt1,R.id.txt2,R.id.txt3 });
				
				People_tag_add ppadd = new People_tag_add();
				ppadd.setTag(mAppList.get(
						position)
						.get(keyString[1])
						.toString());
				ppadd.setMode("gadd");
				Fragment fg = ppadd;
				FragmentManager fragmentManager = getFragmentManager();
				fragmentManager.beginTransaction()
						.replace(R.id.content_frame, fg).commit();
			}
		}

	}

	

}

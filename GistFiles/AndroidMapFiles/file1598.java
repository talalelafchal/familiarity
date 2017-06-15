package cn.android.water.contactsmerge;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProviderOperation;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MergeActivity extends ActionBarActivity {

    private SimpleAdapter ca;
    private List<HashMap<String,Object>> contactlist;
    private ImageButton button_merge;
    private EditText contact_key;
    private ListView contact_lv;
    private ImageButton button_search;
    private CheckBox chk_all;
    private  ProgressDialog progressDialog;
    private AlertDialog ad;
    private static final String GET_ALL_CONTACTS = "1";
    //private static final String GET_SEL_CONTACTS = "2";
    private static final String MERGE_ALL_CONTACTS = "3";
    //private static final String MERGE_SEL_CONTACTS = "4";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_merge);

        button_merge = (ImageButton)findViewById(R.id.button_merge);
        contact_key = (EditText)findViewById(R.id.ContactKey);
        contact_lv = (ListView)findViewById(R.id.contactListView);
        button_search = (ImageButton)findViewById(R.id.button_search);
        chk_all = (CheckBox)findViewById(R.id.chkAll);

        AsyncGetContacts task = new AsyncGetContacts();
        task.execute(GET_ALL_CONTACTS);

        contact_lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //TextView contact_id = (TextView)view.findViewById(R.id.item_id);
                TextView contact_name = (TextView)view.findViewById(R.id.item_name);
                contact_key.setText(contact_name.getText().toString());
                contactlist = getContactByName(contact_name.getText().toString());
                ca = new SimpleAdapter(MergeActivity.this,contactlist,R.layout.contact_item,new String[]{"id","name","phone"},new int[]{R.id.item_id,R.id.item_name,R.id.item_phone});
                contact_lv.setAdapter(ca);
            }
        });

        button_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(contact_key.getText().toString().trim().length()==0)// if key words is empty,async load all contacts
                {
                    AsyncGetContacts task = new AsyncGetContacts();
                    task.execute(GET_ALL_CONTACTS);
                }else {
                    contactlist = getContactByName(contact_key.getText().toString());
                    ca = new SimpleAdapter(MergeActivity.this, contactlist, R.layout.contact_item, new String[]{"id", "name", "phone"}, new int[]{R.id.item_id, R.id.item_name, R.id.item_phone});
                    contact_lv.setAdapter(ca);
                }
            }
        });
        button_merge.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(MergeActivity.this);
                builder.setIcon(R.drawable.abc_list_longpressed_holo);
                builder.setTitle("");
                builder.setMessage(R.string.alert_dialog_message);
                builder.setPositiveButton(R.string.button_label_merge_sel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //merge selected contacts
                                contactlist = mergeSelContacts();
                                ca = new SimpleAdapter(MergeActivity.this,contactlist , R.layout.contact_item, new String[]{"id", "name", "phone"}, new int[]{R.id.item_id, R.id.item_name, R.id.item_phone});
                                contact_lv.setAdapter(ca);
                                chk_all.setChecked(false);
                            }
                        });
                builder.setNeutralButton(R.string.button_label_mergeall,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                //merge all contacts
                                AsyncGetContacts task = new AsyncGetContacts();
                                task.execute(MERGE_ALL_CONTACTS);
                            }
                        });
                builder.setNegativeButton(R.string.button_label_cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                ad.dismiss();
                            }
                        });
                ad = builder.show();
            }
        });
        chk_all.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                {
                    setCheckBoxStatus(true);
                }else{
                    setCheckBoxStatus(false);
                }
            }
        });

    }

    private void setCheckBoxStatus(Boolean check)
    {
        for (int i = 0; i < contact_lv.getChildCount(); i++)
        {
            TableLayout tl = (TableLayout)contact_lv.getChildAt(i);
            for(int j=0;j < tl.getChildCount(); j++)
            {
                TableRow tr = (TableRow)tl.getChildAt(j);
                CheckBox chk = (CheckBox)tr.getChildAt(0);
                chk.setChecked(check);
            }
        }
    }
    private ArrayList<String> getSelectedContactsID()
    {
        ArrayList<String> IDs = new ArrayList<String>();
        for (int i = 0; i < contact_lv.getChildCount(); i++)
        {
            TableLayout tl = (TableLayout)contact_lv.getChildAt(i);
            for(int j=0;j < tl.getChildCount(); j++)
            {
                TableRow tr = (TableRow)tl.getChildAt(j);

                //for(int m=0;m < tr.getChildCount(); m ++) {
                    CheckBox chk = (CheckBox)tr.getChildAt(0);
                    TextView id = (TextView)tr.getChildAt(1);
                    if(chk.isChecked())
                    {
                        //Toast.makeText(MergeActivity.this, id.getText().toString(), Toast.LENGTH_SHORT).show();
                        IDs.add(id.getText().toString());
                    }
               // }
            }
        }

        return IDs;
    }
    private List<HashMap<String,Object>> getContactByName(String keyname)
    {
        List<HashMap<String,Object>> contacts = new ArrayList<HashMap<String,Object>>();
        try{
            String id;
            String mimetype;
            ContentResolver contentResolver = this.getContentResolver();
            Cursor cursor = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI,
                    new String[]{ContactsContract.RawContacts._ID},
                    ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " LIKE '%"+keyname+"%'",
                    null,
                    ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " ASC");
            while(cursor.moveToNext()) {
                id = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("id",id);

                //从一个Cursor获取所有的信息
                Cursor contactInfoCursor = contentResolver.query(
                        android.provider.ContactsContract.Data.CONTENT_URI,
                        new String[]{
                                android.provider.ContactsContract.Data.MIMETYPE,
                                android.provider.ContactsContract.Data.DATA1
                        },
                        ContactsContract.Data.RAW_CONTACT_ID+"="+id, null, null);
                while(contactInfoCursor.moveToNext()) {
                    mimetype = contactInfoCursor.getString(
                            contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
                    String value = contactInfoCursor.getString(
                            contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.DATA1));

                    switch(mimetype)
                    {
                        case "vnd.android.cursor.item/name":
                            if(map.get("name")==null) map.put("name",value);
                            break;
                        case "vnd.android.cursor.item/phone_v2":
                            if(map.get("phone")==null) map.put("phone",value);
                            break;
                        //case "vnd.android.cursor.item/group_membership":
                        //        map.put("group",value);
                        //    break;
                    }
                }
                contactInfoCursor.close();
                contacts.add(map);
            }
            cursor.close();

        }catch(Exception x)
        {
            Log.e("Exceptions","Get Contacts Error:" + x.getMessage());
        }
        return contacts;
    }
    private List<HashMap<String,Object>> mergeSelContacts()
    {
        List<HashMap<String,Object>> contacts = new ArrayList<HashMap<String,Object>>();
        try{
            String mimetype;
            String value;
            ArrayList<String> IDs = getSelectedContactsID();
            //Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
            ContentResolver contentResolver = this.getContentResolver();
            ArrayList<ContactObj> contactlist = new ArrayList<ContactObj>();
            for(int i=0;i< IDs.size();i++) {
                Cursor cursor = contentResolver.query( android.provider.ContactsContract.Data.CONTENT_URI,
                        new String[]{
                                android.provider.ContactsContract.Data.MIMETYPE,
                                android.provider.ContactsContract.Data.DATA1
                        },
                        ContactsContract.Data.RAW_CONTACT_ID+"="+IDs.get(i), null, null);
                contactlist.add(new ContactObj(IDs.get(i)));
                while(cursor.moveToNext()) {
                    mimetype = cursor.getString(
                            cursor.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
                    value = cursor.getString(
                            cursor.getColumnIndex(android.provider.ContactsContract.Data.DATA1));

                    switch(mimetype)
                    {
                        case "vnd.android.cursor.item/name":
                            contactlist.get(contactlist.size()-1).setName(value);
                            break;
                        case "vnd.android.cursor.item/phone_v2":
                            contactlist.get(contactlist.size()-1).setPhone(value);
                            break;
                        //case "vnd.android.cursor.item/group_membership":
                        //        map.put("group",value);
                        //    break;
                    }
                }
                cursor.close();
                if(contactlist.get(contactlist.size()-1).getPhone()==null)//delete
                {
                    try {
                        //根据id删除data中的相应数据
                        DeleteContactsById(contentResolver,IDs.get(i));

                        contactlist.remove(contactlist.size() - 1);//remove the last item
                    }catch(Exception c)
                    {
                        Log.e("Delete Contact Error:" , c.getMessage());
                    }
                }else{
                        if(contactlist.size() > 1)
                        {
                            if( (contactlist.get(contactlist.size()-1).getPhone().equals( contactlist.get(contactlist.size()-2).getPhone()))// if name equals and phone equals, delete
                                &&
                                (contactlist.get(contactlist.size()-1).getName().equals( contactlist.get(contactlist.size()-2).getName())))
                            {
                                //根据id删除data中的相应数据
                                DeleteContactsById(contentResolver,IDs.get(i));

                                contactlist.remove(contactlist.size()-1);//remove the last item
                            }else if(contactlist.get(contactlist.size()-1).getName().equals( contactlist.get(contactlist.size()-2).getName()))
                            {
                                // if only name equals, insert new phone to old contact, delete current contact
                                InsertNewPhone(contentResolver,contactlist.get(contactlist.size()-2).getId(),contactlist.get(contactlist.size()-1).getPhone());

                                DeleteContactsById(contentResolver,IDs.get(i));
                                contactlist.remove(contactlist.size()-1);//remove the last item
                            }
                        }
                }
            }
            for(int m=0;m<contactlist.size();m++)
            {
                HashMap<String, Object> map = new HashMap<String, Object>();
                map.put("id",contactlist.get(m).getId());
                map.put("name",contactlist.get(m).getName());
                map.put("phone",contactlist.get(m).getPhone());

                contacts.add(map);
            }

        }catch (Exception e)
        {
            Log.e("Exceptions","Get Contacts Error:" + e.getMessage());
        }
        return contacts;
    }
    private void DeleteContactsById(ContentResolver r, String id)
    {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

            ops.add(ContentProviderOperation.newDelete(ContactsContract.RawContacts.CONTENT_URI)
                    .withSelection(ContactsContract.RawContacts._ID + "=?", new String[]{String.valueOf(id)}).build());
            Log.v("delete contact......................_id=",id);

        try {
            r.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
    private void InsertNewPhone(ContentResolver r,String id,String phone)
    {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();

        String where=ContactsContract.Data.RAW_CONTACT_ID + " = ? AND " +
                ContactsContract.Data.MIMETYPE + " = ? AND " +
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE) + " = ?";
        String[] params = new String[]{id,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE,
                String.valueOf(ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)};
        Cursor phoneCur = r.query(ContactsContract.Data.CONTENT_URI, null, where, params, null);
        if ( (null == phoneCur) || (!phoneCur.moveToFirst()) )
        {
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID, id)
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DATA1, phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DATA2, ContactsContract.CommonDataKinds.Phone.TYPE_CUSTOM)
                    .build());

        }else{// if exists, add new phone for other type
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValue(ContactsContract.Data.RAW_CONTACT_ID,id)
                    .withValue(ContactsContract.Data.MIMETYPE,ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DATA1,phone)
                    .withValue(ContactsContract.CommonDataKinds.Phone.DATA2,ContactsContract.CommonDataKinds.Phone.TYPE_OTHER)
                    .build());
        }

        try {
            r.applyBatch(ContactsContract.AUTHORITY, ops);
            phoneCur.close();
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_merge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    class AsyncGetContacts extends AsyncTask<String,Integer,List<HashMap<String,Object>>> {

        private int ContactCount;
        private SimpleAdapter ca;
        //get contact count
        /*private int getContactCount(){
            Cursor c=context.getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, new String[]{ContactsContract.Contacts._COUNT}, null, null, null);
            try{
                c.moveToFirst();
                return c.getInt(0);
            }catch(Exception e){
                return 0;
            }finally{
                c.close();
            }
        }*/

        @Override
        protected List<HashMap<String, Object>> doInBackground(String... params) {
            publishProgress(0);
            List<HashMap<String,Object>> contacts = new ArrayList<HashMap<String,Object>>();
            try{
                String id;
                String mimetype;
                String value;
                ContentResolver contentResolver = MergeActivity.this.getContentResolver();
                switch (params[0])
                {
                    case GET_ALL_CONTACTS:
                        Cursor cursor = contentResolver.query(ContactsContract.RawContacts.CONTENT_URI,
                                new String[]{ContactsContract.RawContacts._ID}, null, null, ContactsContract.RawContacts.DISPLAY_NAME_PRIMARY + " ASC");
                        ContactCount = cursor.getCount();
                        while(cursor.moveToNext()) {
                            id = cursor.getString(cursor.getColumnIndex(ContactsContract.RawContacts._ID));
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            map.put("id",id);

                            //从一个Cursor获取所有的信息
                            Cursor contactInfoCursor = contentResolver.query(
                                    android.provider.ContactsContract.Data.CONTENT_URI,
                                    new String[]{android.provider.ContactsContract.Data.MIMETYPE,
                                            android.provider.ContactsContract.Data.DATA1
                                    },
                                    ContactsContract.Data.RAW_CONTACT_ID+"="+id, null, ContactsContract.Data._ID + " ASC");
                            while(contactInfoCursor.moveToNext()) {
                                mimetype = contactInfoCursor.getString(
                                        contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.MIMETYPE));
                                value = contactInfoCursor.getString(
                                        contactInfoCursor.getColumnIndex(android.provider.ContactsContract.Data.DATA1));

                                switch(mimetype)
                                {
                                    case "vnd.android.cursor.item/name":
                                        map.put("name",value);
                                        break;
                                    case "vnd.android.cursor.item/phone_v2":
                                        map.put("phone",value);
                                        break;
                                    //case "vnd.android.cursor.item/group_membership":
                                    //        map.put("group",value);
                                    //    break;
                                }
                            }
                            contactInfoCursor.close();
                            contacts.add(map);
                            publishProgress((int)((100*contacts.size())/ContactCount));
                        }
                        cursor.close();
                        break;
                    case MERGE_ALL_CONTACTS:
                        try{
                            contacts = new ArrayList<HashMap<String,Object>>();
                            for(int i=0;i< contactlist.size();i++) {
                                HashMap<String, Object> map = new HashMap<String, Object>();
                                map.put("id",contactlist.get(i).get("id").toString());
                                if(contactlist.get(i).get("phone")==null)//delete
                                {
                                    try {
                                        //根据id删除data中的相应数据
                                        DeleteContactsById(contentResolver,contactlist.get(i).get("id").toString());

                                    }catch(Exception c)
                                    {
                                        Log.e("Delete Contact Error:" , c.getMessage());
                                    }
                                }else{
                                    if(contacts.size() > 0)
                                    {
                                        if( (contactlist.get(i).get("phone").equals( contacts.get(contacts.size()-1).get("phone")))// if name equals and phone equals, delete
                                                &&
                                                (contactlist.get(i).get("name").equals( contacts.get(contacts.size()-1).get("name"))))
                                        {
                                            //根据id删除data中的相应数据
                                            DeleteContactsById(contentResolver,contactlist.get(i).get("id").toString());

                                        }else if((contactlist.get(i).get("name").equals( contacts.get(contacts.size()-1).get("name")))){// if only name equals, merge phone number

                                            InsertNewPhone(contentResolver,
                                                           contacts.get(contacts.size()-1).get("id").toString(),
                                                           contactlist.get(i).get("phone").toString());

                                            DeleteContactsById(contentResolver,contactlist.get(i).get("id").toString());

                                        }else{
                                            map.put("name",contactlist.get(i).get("name").toString());
                                            map.put("phone",contactlist.get(i).get("phone").toString());

                                            contacts.add(map);
                                        }
                                    }else{
                                        map.put("name",contactlist.get(i).get("name").toString());
                                        map.put("phone",contactlist.get(i).get("phone").toString());

                                        contacts.add(map);
                                    }
                                }
                                publishProgress((int)((100 * i )/contactlist.size()));
                            }

                        }catch (Exception e)
                        {
                            Log.e("Exceptions","Get Contacts Error:" + e.getMessage());
                        }
                        break;
                }


            }catch(Exception x)
            {
                Log.e("Exceptions", "Get Contacts Error:" + x.getMessage());
            }
            return contacts;
        }
        @Override
        protected void onProgressUpdate(Integer... progress) {//在调用publishProgress之后被调用，在ui线程执行
            // mProgressBar.setProgress(progress[0]);//更新进度条的进度
            //Log.v("onProgressUpdate", "updating: " + progress[0]);
            progressDialog.setProgress(progress[0]);
        }
        protected void onPostExecute(List<HashMap<String,Object>> result) {//后台任务执行完之后被调用，在ui线程执行
            if(result != null) {
                progressDialog.dismiss();
                // Toast.makeText(context, "Load Success", Toast.LENGTH_LONG).show();
                contactlist = result;
                ca = new SimpleAdapter(MergeActivity.this,result,R.layout.contact_item,new String[]{"id","name","phone"},new int[]{R.id.item_id,R.id.item_name,R.id.item_phone});
                contact_lv.setAdapter(ca);
            }else {
                progressDialog.dismiss();
                Toast.makeText(MergeActivity.this, "Load Error", Toast.LENGTH_LONG).show();
            }
        }
        protected void onPreExecute () {//在 doInBackground(Params...)之前被调用，在ui线程执行
            // mImageView.setImageBitmap(null);
            progressDialog = new ProgressDialog(MergeActivity.this);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progressDialog.setIcon(R.drawable.progressbar);
            progressDialog.setMessage("Loading...");
            progressDialog.setCancelable(false);
            progressDialog.setProgress(0);//进度条复位
            progressDialog.setMax(100);
            progressDialog.show();
        }

        protected void onCancelled () {//在ui线程执行
            progressDialog.dismiss();
        }
    }
}

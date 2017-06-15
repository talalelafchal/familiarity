package com.fuckeyah.todos;

import android.app.ListActivity;
import android.os.Bundle;

import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Dialog;
import android.app.ProgressDialog;
import org.apache.http.client.HttpClient;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.HttpResponse;
import android.os.AsyncTask;
import org.xml.sax.*;

public class TodosList extends ListActivity
{
    String[] todoLists = new String[] { };

    @Override
    public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      setListAdapter(new ArrayAdapter<String>(this, R.layout.todos_list_item, getTodoLists()));

      ListView lv = getListView();
      lv.setTextFilterEnabled(true);

      lv.setOnItemClickListener(new OnItemClickListener() {
          public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
              Toast.makeText(getApplicationContext(), ((TextView) view).getText(), Toast.LENGTH_SHORT).show();
          }
      });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, 1, 0, "Reload list of todos");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      if (item.getItemId() == 1) {
        showDialog(1);
        new TasksDownloader(this).execute();
      }
      return true;
    }

    @Override
    protected Dialog onCreateDialog (int id, Bundle args) {
      ProgressDialog dialog = ProgressDialog.show(this, "", "Loading. Please wait...", true);
      return (Dialog)dialog;
    }


    protected String[] getTodoLists() {
      return this.todoLists;
    }

    protected void clearTodoLists() {
      this.todoLists = new String[] {};
    }

    protected void addTodoList(String item) {
      this.todoLists = new String[1];
      this.todoLists[0] = item;
    }


    private class TasksDownloader extends AsyncTask<Void, Void, Boolean> {
      TodosList parent;

      public TasksDownloader(TodosList activity) {
        super();
        this.parent = activity;
      }

      @Override
      protected Boolean doInBackground(Void... unused) {
         try {
           HttpClient httpClient = new DefaultHttpClient();
           HttpContext localContext = new BasicHttpContext();
           HttpGet httpGet = new HttpGet("http://10.0.2.2:3000/todo_lists.json");
           HttpResponse response = httpClient.execute(httpGet, localContext);
           DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
           DocumentBuilder db = dbf.newDocumentBuilder();
           Document dom = db.parse(response);
           Element docEle = dom.getDocumentElement();
           NodeList nl = docEle.getElementsByTagName("todo_list");
           if (nl != null && nl.getLength() > 0) {
             for (int i = 0 ; i < nl.getLength(); i++) {
               Element entry = (Element)nl.item(i);
               Element title = (Element)entry.getElementsByTagName("name").item(0);
               addTodoList(title.getFirstChild().getNodeValue());
             }
           }

           clearTodoLists();
           addTodoList(response.toString());
         } catch (Exception e) {
           return false;
         }

         return true;
      }

      @Override
      protected void onProgressUpdate(Void... unused) {
      }

      @Override
      protected void onPostExecute(Boolean result) {
        if (result) {
          removeDialog(1);
          setListAdapter(new ArrayAdapter<String>(this.parent, R.layout.todos_list_item, getTodoLists()));
        } else {
          removeDialog(1);
          Toast.makeText(getApplicationContext(), "Could not load list of todos...", Toast.LENGTH_SHORT).show();
        }
      }
    }
}

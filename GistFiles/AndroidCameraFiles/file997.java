package software.is.com.icommunity.activity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.squareup.otto.Subscribe;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import software.is.com.icommunity.IcrmApp;
import software.is.com.icommunity.MainActivity;
import software.is.com.icommunity.PrefManager;
import software.is.com.icommunity.R;
import software.is.com.icommunity.adapter.GroupBasesAdapter;
import software.is.com.icommunity.adapter.RecyclerViewTimelineListAdapter;
import software.is.com.icommunity.event.ActivityResultBus;
import software.is.com.icommunity.event.ApiBus;
import software.is.com.icommunity.event.GetGroupReceivedEvent;
import software.is.com.icommunity.event.GetGroupRequestedEvent;
import software.is.com.icommunity.event.ImagesReceivedEvent;
import software.is.com.icommunity.model.PostGroup;

public class MyGroupActvivity extends AppCompatActivity {
    ListView listView;
    GroupBasesAdapter groupBasesAdapter;
    ArrayList<PostGroup> list = new ArrayList<>();
    String vendor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_group_activity);
        listView = (ListView) findViewById(R.id.listView);
        vendor = getIntent().getStringExtra("vendor");
        ApiBus.getInstance().postQueue(new GetGroupRequestedEvent(vendor));

    }


    @Override
    protected void onResume() {
        super.onResume();
        ActivityResultBus.getInstance().register(this);
        ApiBus.getInstance().register(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        ActivityResultBus.getInstance().unregister(this);
        ApiBus.getInstance().unregister(this);
    }

    @Subscribe
    public void GetList(final GetGroupReceivedEvent event) {
        if (event != null) {
            Log.e("event", event.getPost().getPost().get(0).getGroup_name());
            for (int i = 0; i < event.getPost().getPost().size(); i++) {
                list.add(event.getPost());
            }

            groupBasesAdapter = new GroupBasesAdapter(getApplicationContext(),list);
//            Log.e("BGGG", event.getPost().getBg() + "");
            listView.setAdapter(groupBasesAdapter);
        }

    }

}

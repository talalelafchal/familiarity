package putugunation.com.mapsroute.activities;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.Comparator;
import java.util.List;

import putugunation.com.mapsroute.R;
import putugunation.com.mapsroute.adapters.AdapterListVIew;
import putugunation.com.mapsroute.helpers.Constant;
import putugunation.com.mapsroute.helpers.Utils;
import putugunation.com.mapsroute.models.Data;


public class ListDestinationActivity extends AppCompatActivity {

    private ListView listView;
    private AdapterListVIew adapter;
    private List<Data> listData;
    private ProgressDialog progressDialog;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_latitudes);
        listView = (ListView) findViewById(R.id.recyclerView_data);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);

        setListAdapter(MainActivity.data);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intents = new Intent(ListDestinationActivity.this, ViewRouteActivity.class);
                startActivity(intents);

                Utils.saveString(ListDestinationActivity.this, Constant.LAT_CHOSEN, MainActivity.data.get(position).getLatitude());
                Utils.saveString(ListDestinationActivity.this, Constant.LONG_CHOSEN, MainActivity.data.get(position).getLongitude());
                Utils.saveString(ListDestinationActivity.this,Constant.NAME_CHOSEN, MainActivity.data.get(position).getModified());
                Utils.saveString(ListDestinationActivity.this,Constant.DURATION_CHOSEN, MainActivity.data.get(position).getLegs().getDuration().getText());
                Utils.saveString(ListDestinationActivity.this,Constant.DISTANCE_CHOSEN, MainActivity.data.get(position).getLegs().getDistance().getText());
            }
        });


    }

    private void setListAdapter(final List<Data> list){
        try {
            adapter = new AdapterListVIew(ListDestinationActivity.this,R.layout.row_item_data,list);
            listView.setAdapter(adapter);
            adapter.sort(new Comparator<Data>() {
                @TargetApi(Build.VERSION_CODES.KITKAT)
                @Override
                public int compare(Data lhs, Data rhs) {
                    int value1 = Integer.parseInt(lhs.getLegs().getDistance().getValue());
                    int value2 = Integer.parseInt(lhs.getLegs().getDuration().getValue());
                    return Integer.compare(value1, value2);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id== android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);

    }
}

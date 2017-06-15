import java.util.ArrayList;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class ListViewActivity extends ListActivity {
  
	private static final String[] items={"Android","Bluetooth","Chrome","Docs","Email",
		"Facebook","Google","Hungary","Iphone","Korea","Machintosh",
		"Nokia","Orkut","Picasa","Singapore","Turkey","Windows","Youtube"};
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setListAdapter(new ArrayAdapter(this,android.R.layout.simple_list_item_multiple_choice,items));
    }
    
    
    public void onListItemClick(ListView parent,View v,int position,long id){
      //A small textbox appears on the right of the row.
      //Something happens code here
    }
}
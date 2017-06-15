/* SIMPLE LIST VIEW ACTIVITY

If your main view is simply the list, you don’t actually really need to provide a layout.
Subclass your Activity to ListActivity. ListActivity will build a full-screen list for you.
*/

import java.util.ArrayList;
import android.app.Activity;
import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class SimpleListViewActivity extends ListActivity {
  
	private static final String[] items={"Android","Bluetooth","Chrome","Docs","Email",
		"Facebook","Google","Hungary","Iphone","Korea","Machintosh",
		"Nokia","Orkut","Picasa","Singapore","Turkey","Windows","Youtube"};


    //Called when the activity is first created.
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        //Set the layout, not really necessary since the holw view is the listview
        setContentView(R.layout.main);
        
        //ListView calls the functions of Adapter to get the views of list item and populates in the container.
        
        //items is our array list
        setListAdapter(new ArrayAdapter(this, android.R.layout.simple_list_item_1, items));
        //first argument is the Context to access system services and resources
        //( you need layout inflater to create list item view )
        //The second parameter to our ArrayAdapter is android.R.layout.simple_list_item_1
        //which controls the look of the row.
        
        /*
        List of Layout Constants:
        
        activity_list_item
        browser_link_context_header
        expandable_list_content
        list_content
        preference_category
        select_dialog_item
        select_dialog_multichoice
        select_dialog_singlechoice
        simple_dropdown_item_1line
        simple_expandable_list_item_1
        simple_expandable_list_item_2
        simple_gallery_item
        simple_list_item_1
        simple_list_item_2
        simple_list_item_activated_1
        simple_list_item_activated_2
        simple_list_item_checked
        simple_list_item_multiple_choice
        simple_list_item_single_choice
        simple_selectable_list_item
        simple_spinner_dropdown_item
        simple_spinner_item
        test_list_item
        two_line_list_item
        */
    }
    
    
    public void onListItemClick(ListView parent,View v,int pos,long id){
    	//Something happens
    }
}
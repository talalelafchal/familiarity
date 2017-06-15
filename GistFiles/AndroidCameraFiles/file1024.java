package sagarpreet97.reminder;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    static ExpandableListView expandableListView ;
    static FAQAdapter adapter ;
    static ArrayList<faq> mdata=new ArrayList<>() ;
    static ArrayList<Reminder_listview_data> reminderdata=new ArrayList<>() ;
    static RecyclerView recyclerView ;
    static RCVAdapter radapter ;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SearchView searchView ;
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);

//        listView=(ListView)findViewById(R.id.listView) ;
//        adapter = new ListView_Adapter(this , reminderdata);
//        listView.setAdapter(adapter);
        DatabaseHelper helper = new DatabaseHelper(this);
        SQLiteDatabase db = helper.getReadableDatabase();

        Cursor c = db.query(DatabaseHelper.DB_TABLE , null , null, null, null, null, null);
        reminderdata.clear();
        Reminder_listview_data t=new Reminder_listview_data("REMINDER 1" , "Complete this app " , null) ;
        reminderdata.add(t);
        while (c.moveToNext()) {
            String title = c.getString(c.getColumnIndex(DatabaseHelper.KEY_TITLE));
            String desc = c.getString(c.getColumnIndex(DatabaseHelper.KEY_DESC));
            byte[] imagebyte=c.getBlob(c.getColumnIndex(DatabaseHelper.KEY_IMAGE)) ;
            //  String id = String.valueOf(c.getInt(c.getColumnIndex(CNOpenHelper.BATCH_TABLE_ID)));
            //        int vote = c.getInt(c.getColumnIndex(CNOpenHelper.BATCH_VOTE_COUNT));
            Bitmap bitmap=DbBitmapUtility.getImage(imagebyte) ;

            //Batch b = new Batch(name,instuctorName,strength,id);
            //data.add(b);
            Reminder_listview_data x=new Reminder_listview_data(title , desc , bitmap) ;

            reminderdata.add(x);
        }

        mdata.add(new faq("Click On Button" , "To add reminder or a quick note , " +
                "BLUE for Quick Note and PINK for Reminder :)")) ;
//        mdata.add(new faq("Title2" , "Desc2")) ;

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.reminders_white_18dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.faq);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==1)
        {
            if(resultCode==2)
            {


//                DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
//                SQLiteDatabase db = helper.getReadableDatabase();
//
//                Cursor c = db.query(DatabaseHelper.DB_TABLE , null , null, null, null, null, null);
//                reminderdata.clear();
//                while (c.moveToNext()) {
//                    String title = c.getString(c.getColumnIndex(DatabaseHelper.KEY_TITLE));
//                    String desc = c.getString(c.getColumnIndex(DatabaseHelper.KEY_DESC));
//                    byte[] imagebyte=c.getBlob(c.getColumnIndex(DatabaseHelper.KEY_IMAGE)) ;
//                  //  String id = String.valueOf(c.getInt(c.getColumnIndex(CNOpenHelper.BATCH_TABLE_ID)));
//                    //        int vote = c.getInt(c.getColumnIndex(CNOpenHelper.BATCH_VOTE_COUNT));
//                    Bitmap bitmap=DbBitmapUtility.getImage(imagebyte) ;
//
//                    //Batch b = new Batch(name,instuctorName,strength,id);
//                    //data.add(b);
//                    Reminder_listview_data x=new Reminder_listview_data(title , desc , bitmap) ;
//
//                    reminderdata.add(x);
//                }
                DatabaseHelper helper = new DatabaseHelper(MainActivity.this);
                SQLiteDatabase db = helper.getWritableDatabase();

                ContentValues cv = new ContentValues();
                cv.put(DatabaseHelper.KEY_TITLE, data.getStringExtra("title") );
                cv.put(DatabaseHelper.KEY_DESC , data.getStringExtra("desc") );
                //cv.put(CNOpenHelper.BATCH_INSTRUCTOR_NAME, b.getInstructorName());
                cv.put(DatabaseHelper.KEY_IMAGE , data.getByteArrayExtra("image") );


                db.insert(DatabaseHelper.DB_TABLE, null, cv);
                Bitmap bitmap=DbBitmapUtility.getImage(data.getByteArrayExtra("image")) ;
                reminderdata.add(new Reminder_listview_data(data.getStringExtra("title") , data.getStringExtra("desc") , bitmap));
                radapter.notifyDataSetChanged();

            }
        }
        else  if(requestCode==2)
        {
            if(resultCode==3)
            {
                String mt=data.getStringExtra("title") ;
                String md=data.getStringExtra("desc") ;
                    mdata.add(new faq(mt , md));
                    adapter.notifyDataSetChanged() ;

            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:

                animateFAB();
                break;
            case R.id.fab1:
                Intent intent=new Intent() ;
                intent.setClass(MainActivity.this , FlipCard_activity.class) ;
                startActivityForResult(intent , 2 );
                break;
            case R.id.fab2:
                Intent i=new Intent() ;
                i.setClass(MainActivity.this , AddListview_activity.class);

                startActivityForResult(i , 1);
                break;
        }
    }

    public void animateFAB(){

        if(isFabOpen){

            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            isFabOpen = false;
            Log.d("Raj", "close");

        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            isFabOpen = true;
            Log.d("Raj","open");

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
       getMenuInflater().inflate(R.menu.menu_main, menu);

        final MenuItem myActionMenuItem = menu.findItem( R.id.action_search);
        searchView = (SearchView) myActionMenuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

              //  Toast.makeText(MainActivity.this , query , Toast.LENGTH_LONG) ;
                if( ! searchView.isIconified()) {
                    searchView.setIconified(true);
                }

                myActionMenuItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                // UserFeedback.show( "SearchOnQueryTextChanged: " + s);
                return false;
            }
        });
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView=null ;
            if( getArguments().getInt(ARG_SECTION_NUMBER)==1) {
                 rootView = inflater.inflate(R.layout.reminder_fragment, container, false);

                LinearLayoutManager lm=new LinearLayoutManager(getContext()) ;
                lm.setOrientation(LinearLayoutManager.VERTICAL);
                recyclerView=(RecyclerView)rootView.findViewById(R.id.listview) ;
                recyclerView.setLayoutManager(lm);
                radapter=new RCVAdapter(getContext() , reminderdata ) ;
                recyclerView.setAdapter(radapter);


                ItemTouchHelper.SimpleCallback callback=new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN |ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT , 0){

                    @Override
                    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                        int first=viewHolder.getAdapterPosition() ;
                        int second=target.getAdapterPosition() ;
                        Reminder_listview_data b1=reminderdata.get(first) ;
                        Reminder_listview_data b2=reminderdata.get(second) ;
                        reminderdata.set(first , b2) ;
                        reminderdata.set(second , b1) ;
                        radapter.notifyItemMoved(first , second);
                        return true;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
//                        int position=viewHolder.getAdapterPosition() ;
//                        Reminder_listview_data temp=reminderdata.get(position) ;
//
//                        DatabaseHelper helper = new DatabaseHelper(getContext());
//                        SQLiteDatabase db = helper.getWritableDatabase();
//
//                     //   db.delete(DatabaseHelper.DB_TABLE, DatabaseHelper.KEY_TITLE + "=" + temp.getTitle() + " and " + DatabaseHelper.KEY_DESC + "=" + temp.getDesc() , null);
//
//                        reminderdata.remove(position) ;
//                        radapter.notifyItemRemoved(position);

                    }
                };
                ItemTouchHelper helper=new ItemTouchHelper(callback) ;
                helper.attachToRecyclerView(recyclerView);
         //       onActivityResult(1 , 2 , null) ;


            }
            else if( getArguments().getInt(ARG_SECTION_NUMBER)==2)
            {
                rootView = inflater.inflate(R.layout.faq_fragment, container, false);
                 expandableListView=(ExpandableListView)rootView.findViewById(R.id.expandableListView) ;
                adapter=new FAQAdapter(getContext() , mdata) ;
                expandableListView.setAdapter(adapter);
            }
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "REMINDERS";
                case 1:
                    return "QUICK NOTE";
            }
            return null;
        }
    }
}

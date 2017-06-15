/* If we're not extending ListView to our activity */

public class SimpleListViewActivity extends Activity {  
    
  /** Called when the activity is first created. */  
  @Override  
  public void onCreate(Bundle savedInstanceState) {  
    super.onCreate(savedInstanceState);  
    setContentView(R.layout.main);  
      
    // Find the ListView resource.   
    private ListView mainListView = (ListView) findViewById( R.id.mainListView );  
  
    // Create and populate a List of planet names.  
    String[] planets = new String[] { "Mercury", "Venus", "Earth", "Mars",  
                                      "Jupiter", "Saturn", "Uranus", "Neptune"};
                                      
    ArrayList<String> planetList = new ArrayList<String>();  
    planetList.addAll( Arrays.asList(planets) );  
      
    // Create ArrayAdapter using the planet list.  
    private ArrayAdapter<String> listAdapter = new ArrayAdapter<String>(this, R.layout.simplerow, planetList);  
      
    // Add more planets. If you passed a String[] instead of a List<String>   
    // into the ArrayAdapter constructor, you must not add more items.   
    // Otherwise an exception will occur.  
    listAdapter.add( "Ceres" );  
    listAdapter.add( "Pluto" );  
    listAdapter.add( "Haumea" );  
    listAdapter.add( "Makemake" );  
    listAdapter.add( "Eris" );  
      
    // Set the ArrayAdapter as the ListView's adapter.
    //ListView calls the functions of Adapter to get the views of list item and populates in the container.
    mainListView.setAdapter( listAdapter );
    
    //If the list items are changed Adapter can let the ListView know about it by calling notifyDataSetChanged.
    listAdapter.notifyDataSetChanged();
  }  
}  
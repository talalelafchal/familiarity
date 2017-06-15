//Create ListView and populate it with data from String Array using ArrayAdapter
//And then do something onClick to each list item
String[] myStringArray = new String[]{
            "Art House",
            "Bike Shop",
            "Camera Fix",
            "YETspace",
            "Secret Space Pad",
            "Taylor's Tailor",
            "Boathouse",
            "Not Apple Store",
            "Tool Battleground",
            "Travelpediocity",
            "UFO Pick-a-part",
            "Spawrk's House",
    };
    private ListView mListView;
    private ArrayAdapter mArrayAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view);
        mListView = (ListView) findViewById(R.id.myListView);
        //create new ArrayAdapter - giving it arguments - context, single row xml(default one from android in this case,
        //and actual array to take data from)
        mArrayAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1 ,myStringArray);
        //if mListView is not null, then set Adapter to this ListView
        if(mListView != null) {
            mListView.setAdapter(mArrayAdapter);
        }
        //setting OnItemClickListener to ListView, so when user clicks on rows they got this functionality
        //i in onItemClick stands for position of selected item in ListView
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.v("PLACE", myStringArray[i]);
                //We also wanna toast this texts on screen when list items are clicked
                Toast.makeText(getApplicationContext(),myStringArray[i],Toast.LENGTH_SHORT).show();
            }
        });
    }
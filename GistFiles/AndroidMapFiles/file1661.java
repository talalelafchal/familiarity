    // On create
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

	      // Get data instance
	      instance = Data.getInstance(this);

    // On resume
    @Override
    protected void onResume()
    {
        super.onResume();

	      // Get data instance, connect callbacks
	      instance = Data.getInstance(this);

        // Check data instance
        if (instance != null)
        {
            // Get the saved select list
            List<Integer> list = instance.getList();

            // Get the saved value map
            valueMap = instance.getMap();
        }

        // Start the task
        if (instance != null)
            instance.startParseTask(URL);;
    }

    // On pause
    @Override
    protected void onPause()
    {
        super.onPause();

        // Save the select list and value map in the data instance
        if (instance != null)
        {
            instance.setList(selectList);
            instance.setMap(valueMap);
        }

        // Disconnect callbacks      
        instance = Data.getInstance(null);
    }

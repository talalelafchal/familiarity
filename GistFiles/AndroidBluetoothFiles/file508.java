public class MainActivity extends Activity {

    private ListView listView1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        //Array of Weather objects
        Weather weather_data[] = new Weather[]
        {
            new Weather(R.drawable.weather_cloudy, "Cloudy"),
            new Weather(R.drawable.weather_showers, "Showers"),
            new Weather(R.drawable.weather_snow, "Snow"),
            new Weather(R.drawable.weather_storm, "Storm"),
            new Weather(R.drawable.weather_sunny, "Sunny")
        };
        
        WeatherAdapter adapter = new WeatherAdapter(this, 
                R.layout.listview_item_row, weather_data);
        
        
        listView1 = (ListView)findViewById(R.id.listView1);
         
        View header = (View)getLayoutInflater().inflate(R.layout.listview_header_row, null);
        listView1.addHeaderView(header);
        
        listView1.setAdapter(adapter);
    }
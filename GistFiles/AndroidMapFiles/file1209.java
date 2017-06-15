 import com.mapbox.MapBoxView
 
 public class SimpleMapBoxExample extends ApplicationContext {
 	MapBoxView map;
 	
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        
        // We can initialize the map with the MapBox name, coordinates and zoom level
        map = new MapBoxView("examples.map-9ijuk24y", 42.65742, 2.347682, 12);
        
        // We now set the map as the View for our Activity 
        this.setContentView(map);
    }
 }
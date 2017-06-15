import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import java.util.ArrayList;
import java.util.HashMap;

public class MyCustomListView extends Activity{

    static final ArrayList<HashMap<String,String>> myList = 
        	 new ArrayList<HashMap<String,String>>(); 

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
       
        ListView leaderboard = (ListView) findViewById(R.id.leaderboard);
        
        SimpleAdapter adapter = new SimpleAdapter(
        		this,
        		myList,
        		R.layout.leaderboard_item,
        		new String[] {"standing","score","user"},
        		new int[] {R.id.standingText,R.id.scoreText, R.id.userText}
        		);
        populateList();
  		leaderboard.setAdapter(adapter);

    }

    private void populateList() {
    	HashMap<String,String> temp = new HashMap<String,String>();
    	
    	temp.put("standing","FIRST");
    	temp.put("score", "2.4sec");
    	temp.put("user", "daredevil");
    	myList.add(temp);
    	
    	temp.put("standing","SECOND");
    	temp.put("score", "1.8sec");
    	temp.put("user", "hardcoredropper");
    	myList.add(temp);
    	
    	temp.put("standing","THIRD");
    	temp.put("score", "1.6sec");
    	temp.put("user", "batman");
    	myList.add(temp);
    }
    
}
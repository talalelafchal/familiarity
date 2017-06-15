import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class ActCityMap extends Activity {

	TextView tvCityName;
	
	@Override
	protected void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.map);
		
		tvCityName = (TextView)findViewById(R.id.tvCityName);
		
		Intent startupIntent = getIntent();
		String cityName = startupIntent.getStringExtra("cityname");
		
		tvCityName.setText(cityName);
	}
}
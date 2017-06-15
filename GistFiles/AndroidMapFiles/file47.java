import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class SettingActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setting);

		Map<Integer, String[]> spinerList = new HashMap<Integer, String[]>();
		spinerList.put(R.id.spinner1, new String[] {"3秒","10秒","30秒"});
		spinerList.put(R.id.spinner2, new String[] {"りんご","みかん","パイナップル"});
		spinerList.put(R.id.spinner3, new String[] {"寺","神社"});
		spinerList.put(R.id.spinner4, new String[] {"車","自転車","飛行機","徒歩","ランニング"});
		spinerList.put(R.id.spinner5, new String[] {"山","川","海","キャンプ"});

		for(Map.Entry<Integer, String[]> spinerImte : spinerList.entrySet()) {
			spinerSet(spinerImte.getKey(),spinerImte.getValue());
		}
	}

	private void spinerSet(int resourceId,String[] addItems){
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		for(String addItem : addItems){
			adapter.add(addItem);
		}
		Spinner spinner = (Spinner) findViewById(resourceId);
		spinner.setAdapter(adapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
			}
			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
			}
		});
	}
}

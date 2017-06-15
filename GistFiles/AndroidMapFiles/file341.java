package hoge.hoge;

import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	Map<Integer, ByteWrapper> testMap = new HashMap<Integer, ByteWrapper>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);


		StringBuilder builder = new StringBuilder();

		//create 60kbyte text
		for (int i = 0; i < 10000; i++)
			builder.append(Constants.STR);

		//fill hashmap
		for (int i = 0; i < 100; i++)
			this.testMap.put(i, new ByteWrapper(builder.toString().getBytes()));


		//ハッシュマップを解放しないままfinish
		Button btnNext = (Button) findViewById(R.id.btnNext);
		btnNext.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this, SubActivity.class));
				MainActivity.this.finish();
			}
		});
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		//解放しないとメモリ使用量は減らない
		this.testMap.clear();
		this.testMap = null;
	}
}

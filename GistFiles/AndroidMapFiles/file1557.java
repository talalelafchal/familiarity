package jp.kaki.sazae_san;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleAdapter;

public class MainActivity extends Activity {

	// メインテキスト(リストビュー)
	String[] mainText = new String[] { "サザエ", "マスオ", "タラオ", "波平", "カツオ", "ワカメ",
			"フネ" };
	// サブテキスト(リストビュー)
	String[] subText = new String[] { "フグ田家", "フグ田家", "フグ田家", "磯野家", "磯野家",
			"磯野家", "磯野家" };
	List<String> items = new ArrayList<String>();
	List<String> imagePaths = new ArrayList<String>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// // ListView に表示する文字列を生成
		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		for (int i = 0; i < mainText.length; i++) {
			Map<String, String> map = new HashMap<String, String>();
			map.put("main", mainText[i]);
			map.put("sub", subText[i]);
			list.add(map);
		}
		// // ListView にテキストを２行表示
		SimpleAdapter adapter = new SimpleAdapter(this, list,
				android.R.layout.simple_list_item_2, new String[] { "main",
						"sub" }, new int[] { android.R.id.text1,
						android.R.id.text2 });

		// ListView にデータを追加
		ListView listView = (ListView) findViewById(R.id.listView);
		listView.setAdapter(adapter);

		listView.setAdapter(adapter);

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {

				Intent intent = new Intent(MainActivity.this,
						SecondActivity.class);
				intent.putExtra("msg", position);
				startActivity(intent);

			}
		});
	}
}

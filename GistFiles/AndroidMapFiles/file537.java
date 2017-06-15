

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.lovo.R;

public class TestListActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_test_listview);
		// 放在listView中的文本内容
		String[] strAry = new String[] { "图片1", "图片2", "图片3", "图片4" };
		// 放在listView中的图片资源id
		int[] imgAry = new int[] { R.drawable.image1, R.drawable.image2,
				R.drawable.image3, R.drawable.image4 };
		// 创建List集合对象
		List list = new ArrayList();
		// 为List集合添加数据
		for (int i = 0; i < strAry.length; i++) {
			Map map = new HashMap();
			map.put("image", imgAry[i]);
			map.put("text", strAry[i]);
			list.add(map);
		}
		// 参数1：context - 上下文对象
		// 参数2：data - 设置到listView的数据集合
		// 参数3：resource - 放在listView中每一行的布局资源文件
		// 参数4：from - 指定每一行数据的键（和to里面的id对应）
		// 参数5：to - 指定每一行数据应用到哪个组件上
		SimpleAdapter adapter = new SimpleAdapter(this, list,
				R.layout.listview_content, new String[] { "image", "text" },
				new int[] { R.id.listview_content_img,
						R.id.listview_content_text });
		// 如果只有文本可以使用下面的ArrayAdapter适配器
		// ArrayAdapter adapter = new ArrayAdapter(this,
		// android.R.layout.simple_list_item_checked, strAry);
		// 得到ListView对象
		ListView listView = (ListView) findViewById(R.id.main_test_listview);
		// 设置ListView的内容
		listView.setAdapter(adapter);
	}
}

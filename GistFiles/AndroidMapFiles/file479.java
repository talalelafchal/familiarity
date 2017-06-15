package nu.rinu;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

public class MainActivity extends Activity {
	private static final String TAG = MainActivity.class.getSimpleName();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		testBitmap2();
	}

	private static final int WIDTH = 1000;
	private static final int HEIGHT = 1000;

	private void testBitmap() {
		Map<Integer, SoftReference<Bitmap>> map = new HashMap<Integer, SoftReference<Bitmap>>();

		for (int i = 0; i < 1000; i++) {
			Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
			map.put(i % 4, new SoftReference<Bitmap>(bitmap));
			printMemory(i);
		}
	}

	private void testBitmap2() {
		// ある程度メモリを使用した状態にしておく(量は端末依存)
		List<Bitmap> used = new ArrayList<Bitmap>(100);
		for (int i = 0; i < 4; ++i) {
			used.add(Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888));
			printMemory(0);
		}

		Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
		printMemory(0);
		Log.d(TAG, "この確保が成功するなら、下の確保も成功しないかお。。");

		Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
		Log.d(TAG, "できたお！！");
		printMemory(1);
	}

	private static void printMemory(int no) {
		Log.d(TAG,
				String.format("■ %d native %d-%d / %d", no, Debug.getNativeHeapFreeSize(),
						Debug.getNativeHeapAllocatedSize(), Debug.getNativeHeapSize()));
	}
}

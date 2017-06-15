package cc.cdcat.android.note.activity;

import java.io.File;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.widget.ImageView;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxStatus;
import com.androidquery.callback.BitmapAjaxCallback;
import cc.cdcat.android.note.R;

/**
 * @author Cyrus
 * 判斷本機圖檔方向, 轉正並顯示在 ImageView.
 */
public class PhotoDigree extends Activity {
	private AQuery aq;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.photo_digree);
		
		aq = new AQuery(this);
		
		//本機圖檔路徑
		String path = "/storage/sdcard0/DCIM/Camera/20140214_192829.jpg";
		
        	final int digree = getDigree(path);
		aq.id(R.id.image).image(new File(path), true, 1280, new BitmapAjaxCallback(){
			@Override
			public void callback(String url, ImageView iv, Bitmap bm, AjaxStatus status){
				try {
					//角度不正確才旋轉
					if(digree!=0) {
						Matrix matrix = new Matrix();  
						matrix.postRotate(digree);  
						bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				iv.setImageBitmap(bm);
			}
		});
	}
	
	/**
	 * @param path 本機圖檔路徑
	 * @return 旋轉角度
	 */
	private int getDigree(String path) {
        	int digree = 0;
		try {
			ExifInterface exif = new ExifInterface(path);
			int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);
			switch (ori) {  
				case ExifInterface.ORIENTATION_ROTATE_90:  
					digree = 90;  
					break;  
				case ExifInterface.ORIENTATION_ROTATE_180:  
					digree = 180;  
					break;  
				case ExifInterface.ORIENTATION_ROTATE_270:  
					digree = 270;  
					break;  
				default:  
					digree = 0;  
					break;  
			}  
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return digree; 
	}
}
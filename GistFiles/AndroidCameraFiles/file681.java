package org.dpst.pureexe.ismewhite;




import java.io.FileNotFoundException;
import java.io.IOException;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Camera.Face;
import android.media.FaceDetector;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

public class GetSharedPictureFragment extends Fragment {

	public GetSharedPictureFragment() {
	}
	private CameraPreview mPreview;
	private FrameLayout previewFrame;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_getsharedpicture, container,
				false);
		ImageView imageView = (ImageView) rootView.findViewById(R.id.imageView1);
		Intent intent = getActivity().getIntent();
	    String action = intent.getAction();
	    String type = intent.getType();
        Uri imageUri = null;
	    if (Intent.ACTION_SEND.equals(action) && type != null) {
	        if (type.startsWith("image/")) {
	             imageUri = (Uri) intent.getParcelableExtra(Intent.EXTRA_STREAM);
	            if (imageUri != null) {
	            	try{
	            	imageView.setImageURI(imageUri);
	            	}
	            	catch(Exception e)
	            	{
	            	}
	            }
	            
	        }
	    }
	    BitmapFactory.Options bitmapFatoryOptions=new BitmapFactory.Options();
	    bitmapFatoryOptions.inPreferredConfig=Bitmap.Config.RGB_565;
//	 mybitmapss=BitmapFactory.decodeResource(getResources(), R.drawable.familyportrait2,bitmapFatoryOptions);
	    Bitmap orig = null;
	    try {
			 orig = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    Bitmap bitmap = Bitmap.createBitmap( orig.getWidth(), orig.getHeight(), Bitmap.Config.RGB_565 );
	    Canvas c = new Canvas();
	    c.setBitmap(bitmap);
	    Paint p = new Paint();
	    p.setFilterBitmap(true); // possibly not nessecary as there is no scaling
	    c.drawBitmap(orig,0,0,p);
	    orig.recycle();
	    
	    int maxNumFaces = 10; // Set this to whatever you want
	    FaceDetector fd = new FaceDetector(bitmap.getWidth(),bitmap.getHeight(),maxNumFaces);
	    FaceDetector.Face[] faces = new FaceDetector.Face[maxNumFaces];
	    int numFacesFound = fd.findFaces(bitmap, faces);
	    Toast.makeText(getActivity(), "Face = "+numFacesFound , Toast.LENGTH_LONG).show();
	    
		return rootView;
	}
}

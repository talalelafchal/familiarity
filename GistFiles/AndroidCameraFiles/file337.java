package com.example.anvil.empty;

import android.app.Activity;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import java.io.IOException;

import static trikita.anvil.v15.Attrs.*;

import trikita.anvil.RenderableView;

public class MainActivity extends Activity {

	private Camera mCamera = Camera.open();

	private SurfaceHolder.Callback mHolderCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {}
		public void surfaceDestroyed(SurfaceHolder holder) {}
		public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
			try {
				System.out.println("Surface changed");
				mCamera.setPreviewDisplay(holder);
				mCamera.startPreview();
			} catch (IOException e){
				e.printStackTrace();
			}
		}
	};

	private ConfigListener mConfigListener = new ConfigListener() {
		public void onConfig(View v) {
			System.out.println("onConfig(): v=" + v);
			SurfaceView sv = (SurfaceView) v;
			SurfaceHolder holder = sv.getHolder();
			holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
			holder.addCallback(mHolderCallback);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(new RenderableView(this) {
			public ViewNode view() {
				System.out.println("view()");
				return
					v(FrameLayout.class,
						v(SurfaceView.class,
							config(mConfigListener)));
			}
		});
	}
}
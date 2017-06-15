package io.trigger.forge.android.modules.camera;

import io.trigger.forge.android.core.ForgeActivity;
import io.trigger.forge.android.core.ForgeApp;
import io.trigger.forge.android.core.ForgeLog;
import io.trigger.forge.android.core.ForgeTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.google.common.base.Throwables;

public class ModalView {
	View view = null;
	ForgeTask task = null;
	int requestedOrientation = -10;

	public void openModal(final ForgeTask task) {
		this.task = task;
		task.performUI(new Runnable() {
			public void run() {
				requestedOrientation = ForgeApp.getActivity().getRequestedOrientation();
				ForgeApp.getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
				
				// Create new layout
				RelativeLayout layout = new RelativeLayout(ForgeApp.getActivity());
				view = layout;
				layout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
				layout.setBackgroundColor(Color.BLACK);

				int width = task.params.has("width") ? task.params.get("width").getAsInt() : 0;
				int height = task.params.has("height") ? task.params.get("height").getAsInt() : 0;
				
				final CameraView preview = new CameraView(ForgeApp.getActivity(), width, height);
				
				layout.addView(preview);
				
				ImageButton captureButton = new ImageButton(ForgeApp.getActivity());
				
				captureButton.setImageDrawable(ForgeApp.getActivity().getResources().getDrawable(ForgeApp.getResourceId("camera_capture", "drawable")));
				RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
				params.addRule(RelativeLayout.CENTER_HORIZONTAL);
				params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
				params.bottomMargin = 20;
				captureButton.setLayoutParams(params);
				captureButton.setOnTouchListener(/* ... */);
				
				layout.addView(captureButton);
				
				// Add to the view group and switch
				ForgeApp.getActivity().addModalView(layout);
				layout.requestFocus(View.FOCUS_DOWN);
			}
		});
	}
}
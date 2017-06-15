package org.example.test;

import android.app.Activity;
import android.app.Fragment;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class Activity_main extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        getActionBar().setHomeButtonEnabled(true);

        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                break;

            case R.id.action_settings:
                return true;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


/**
 *  以下プレースホルダ
 *
 *
 *
 *
 */
    public static class PlaceholderFragment extends Fragment implements SurfaceHolder.Callback {

        private Camera _camera;

        public PlaceholderFragment() {
        }

        @Override
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.frgmt_main, container, false);

            SurfaceView   surfaceView   = (SurfaceView) view.findViewById(R.id.surface_view);
            SurfaceHolder surfaceHolder = surfaceView.getHolder();
            surfaceHolder.addCallback(this);
         // surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            return view;
        }

        @Override
        public void surfaceCreated(SurfaceHolder surfaceHolder) {
            _camera = Camera.open();
            try {
                _camera.setPreviewDisplay(surfaceHolder);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder surfaceHolder, int format, int width,
                                   int height) {

            // 何故だかカメラが傾いて起動されるので、90°補正をかけている
            int d = 90;
            _camera.setDisplayOrientation(d);

            Camera.Parameters parameters = _camera.getParameters();
            _camera.setParameters(parameters);
            _camera.startPreview();
        }



    @Override
        public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
            _camera.release();
            _camera = null;
        }

    }
}

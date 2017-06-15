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
import android.view.WindowManager.LayoutParams;

public class Activity_main extends Activity {


    private MyView _myView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        _myView         = new MyView(this);
        LayoutParams tmp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        addContentView(_myView, tmp);


        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }


/*
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

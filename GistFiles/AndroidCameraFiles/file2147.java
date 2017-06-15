package com.dokokano.screen2camera;

import java.io.FileOutputStream;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.app.ActionBar.LayoutParams;
import android.hardware.Camera;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.os.Build;

public class MainActivity extends ActionBarActivity {
    final static private String TAG = "screen2camera";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.container, new CameraFragment()).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * カメラ撮影用フラグメント
     */
    public static class CameraFragment extends Fragment {

        // ------------------------------------------------------------
        // メンバー変数
        // ------------------------------------------------------------
        private Camera camera_;     // カメラインスタンス
        View rootView_;                 // ルートView
        SurfaceView surfaceView_;       // プレビュー用SurfaceView

        // ------------------------------------------------------------
        // リスナー
        // ------------------------------------------------------------

        // Surfaceリスナー
        private SurfaceHolder.Callback surfaceListener_ = new SurfaceHolder.Callback() {
            // Surface作成
            public void surfaceCreated(SurfaceHolder holder) {
                // カメラインスタンスを取得
                camera_ = Camera.open();
                try {
                    camera_.setPreviewDisplay(holder);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            // Surface破棄時
            public void surfaceDestroyed(SurfaceHolder holder) {
                // カメラインスタンス開放
                camera_.release();
                camera_ = null;
            }

            // Surface変更時
            // プレビューのパラメーターを設定し、プレビューを開始する
            public void surfaceChanged(SurfaceHolder holder, int format,    int width, int height) {
                Log.d(TAG, "surfaceChanged width:" + width + " height:" + height);

                Camera.Parameters parameters = camera_.getParameters();

                // デバッグ用表示
                Size size = parameters.getPictureSize();
                Log.d(TAG, "getPictureSize width:" + size.width + " size.height:" + size.height);
                size = parameters.getPreviewSize();
                Log.d(TAG, "getPreviewSize width:" + size.width + " size.height:" + size.height);

                // プレビューのサイズを変更
                // parameters.setPreviewSize(width, height);    // 画面サイズに合わせて変更しようとしたが失敗する
                parameters.setPreviewSize(640, 480);            // 使用できるサイズはカメラごとに決まっている

                // パラメーターセット
                camera_.setParameters(parameters);
                // プレビュー開始
                camera_.startPreview();
            }
        };

        // シャッターが押されたときに呼ばれるコールバック
        private Camera.ShutterCallback shutterListener_ = new Camera.ShutterCallback() {
            public void onShutter() {
            }
        };

        // JPEGイメージ生成後に呼ばれるコールバック
        private Camera.PictureCallback pictureListener_ = new Camera.PictureCallback() {
            // データ生成完了
            public void onPictureTaken(byte[] data, Camera camera) {
                // SDカードにJPEGデータを保存する
                if (data != null) {
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(Environment.getExternalStorageDirectory().getPath()+ "/camera_test.jpg");
                        fos.write(data);
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    // プレビューを再開する
                    camera.startPreview();
                }
            }
        };

        // 画面タッチ時のコールバック
        OnTouchListener ontouchListener_ = new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (camera_ != null) {
                        // 撮影実行
                        camera_.takePicture(shutterListener_, null, pictureListener_);
                    }
                }
                return false;
            }
        };

        // ------------------------------------------------------------
        // Fragment
        // ------------------------------------------------------------

        // Fragmentコンストラクタ
        public CameraFragment() {
        }

        // View作成
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,  Bundle savedInstanceState) {
            // View作成
            rootView_ = inflater.inflate(R.layout.fragment_main, container, false);

            // View内のView取得
            surfaceView_ = (SurfaceView) rootView_  .findViewById(R.id.surface_view);

            // SurfaceHolder設定
            SurfaceHolder holder = surfaceView_.getHolder();
            holder.addCallback(surfaceListener_);
            holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            // タッチリスナー設定
            rootView_.setOnTouchListener(ontouchListener_);

            return rootView_;
        }
    }

}
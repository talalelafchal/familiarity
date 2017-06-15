import android.app.Activity;
import android.media.MediaPlayer;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.rajawali3d.view.SurfaceView;

import java.io.IOException;

@EActivity(R.layout.activity_main)
public class MainActivity extends Activity {

    @ViewById
    SurfaceView rajawaliView;

    @Bean
    MyRenderer renderer;

    @AfterViews
    void init() {
        rajawaliView.setSurfaceRenderer(renderer);
    }

    MediaPlayer mediaPlayer;

    @Override
    protected void onResume() {
        super.onResume();
        mediaPlayer = new MediaPlayer();
        
        // This is just a simple example.
        // I recommend to load resource in background thread in production.
        try {
            mediaPlayer.setDataSource("/sdcard/video.mp4"); // video file path
            mediaPlayer.prepare();
        } catch (IOException e) {
            mediaPlayer.release();
            throw new RuntimeException(e);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.release();
        mediaPlayer = null;
    }
}
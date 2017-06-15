import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.hustunique.jianguo.ijkplayerdemo.media.AndroidMediaController;
import com.hustunique.jianguo.ijkplayerdemo.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class MainActivity extends AppCompatActivity {
    private AndroidMediaController mMediaController;
    private IjkVideoView mVideoView;
    private boolean mBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVideoView = (IjkVideoView) findViewById(R.id.video_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        mMediaController = new AndroidMediaController(this, false);
        mMediaController.setSupportActionBar(actionBar);
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        mVideoView.setMediaController(mMediaController);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                // Note that the duration is counted in seconds.
                String data = "ffconcat version 1.0\n" +
                        "file http://k.youku.com/player/getFlvPath/sid/048455411693812bdd99a_00/st/flv/fileid/030001010058774A2CB9A9059E49FE220E3CC1-C869-0180-6843-BF9932E316E8?ypp=0&myp=0&K=afa53a85a7e5c378282c0be9%26sign%3D253e61392b8eff662013212ab183c891&ctype=12&token=0544&ev=1&ep=ciacHkyIUs8B4yrcgD8bNXi2fX5eXP4J9h%2BFgNJjALshQO%2B4nU%2FTtO%2B5P%2FZCE%2FBsditwZenzq6XkGTMVYYNLr2EQ30%2BgOfrm9vTg5d8lzZkDZGw1c8uivFSeRjT1&hd=1&oip=1939659569\n" +
                        "duration 178.667\n" +
                        "file http://k.youku.com/player/getFlvPath/sid/048455417446012115ebe_00/st/flv/fileid/0300010100587615E18FCC059E49FE0E9FBA38-2F1A-8CA4-CF5B-6D259E2DB1AB?ypp=0&myp=0&K=fb386578479b75df282c0be9%26sign%3D76b64c31b582d61b852d66229b9e9677&ctype=12&token=0504&ev=1&ep=ciacHkyIUs8H4SfZiD8bZi3nISVaXP4J9h%2BFgNJjALshQO%2B5mDukxpXGPYxCE%2FBsditwZeuEoqaVG0McYfI1qBkQ1z3ZPfqTiPWR5asnxpUJExtCAMymxlSeRjD1&hd=1&oip=1939659569\n" +
                        "duration 156.200";
                Util.writeToFile("test.ffconcat", data, MainActivity.this);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                mVideoView.setVideoPath(MainActivity.this.getFileStreamPath("test.ffconcat").getAbsolutePath());
                mVideoView.start();
            }
        }.execute();

    }


    @Override
    public void onBackPressed() {
        mBackPressed = true;
        super.onBackPressed();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mBackPressed) {
            mVideoView.stopPlayback();
            mVideoView.release(true);
        }
        IjkMediaPlayer.native_profileEnd();
    }
}

package com.koziodigital.bluetooth;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DSFDS";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 23;
    private MediaRecorder myRecorder;
    private AudioManager am;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "DSFDS";
    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 23;
    private MediaRecorder myRecorder;
    private AudioManager am;
    private Button button;
    private static final String environment = (Environment.getExternalStorageDirectory().getAbsolutePath());
    private String[] list = new String[] {environment + "/one.mp3", environment + "/two.mp3"};
    private int listIndex = 0;
    private Button savethem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        button = (Button) findViewById(R.id.button);
        savethem = (Button) findViewById(R.id.button2);
        button.setText("Start");

        button
                .setOnClickListener(new View.OnClickListener() {
                    boolean started = false;
                    @Override
                    public void onClick(View view) {
                        if(started) {
                            myRecorder.stop();
                            myRecorder.reset();
                            myRecorder.release();
                            am.setMode(AudioManager.MODE_NORMAL);
                            am.setBluetoothScoOn(false);
                            button.setText("Start");
                        }
                        else {
                            setupAudio();
                            button.setText("Stop");
                        }
                        started = !started;
                    }
                });

        savethem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mergeMediaFiles(true, list, environment + "/gogoglelkf.mp3");
            }
        });

        am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
    }

    private void setupAudio() {
        am.setMode(AudioManager.MODE_IN_COMMUNICATION);
        am.setSpeakerphoneOn(false);

        am.startBluetoothSco();
        myRecorder = new MediaRecorder();
        myRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        myRecorder.setOutputFile(list[listIndex]);
        try {
            myRecorder.prepare();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
        myRecorder.start();
        listIndex ++;
        if(listIndex == 2)
            listIndex = 0;
    }

    public static boolean mergeMediaFiles(boolean isAudio, String sourceFiles[], String targetFile) {
        try {
            String mediaKey = isAudio ? "soun" : "vide";
            List<Movie> listMovies = new ArrayList<>();
            for (String filename : sourceFiles) {
                listMovies.add(MovieCreator.build(filename));
            }
            List<Track> listTracks = new LinkedList<>();
            for (Movie movie : listMovies) {
                for (Track track : movie.getTracks()) {
                    if (track.getHandler().equals(mediaKey)) {
                        listTracks.add(track);
                    }
                }
            }
            Movie outputMovie = new Movie();
            if (!listTracks.isEmpty()) {
                outputMovie.addTrack(new AppendTrack(listTracks.toArray(new Track[listTracks.size()])));
            }
            Container container = new DefaultMp4Builder().build(outputMovie);
            FileChannel fileChannel = new RandomAccessFile(String.format(targetFile), "rw").getChannel();
            container.writeContainer(fileChannel);
            fileChannel.close();
            return true;
        }
        catch (IOException e) {
            Log.e("SDFDS", "Error merging media files. exception: "+e.getMessage());
            return false;
        }
    }

}

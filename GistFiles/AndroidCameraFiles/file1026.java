package com.intel.camerawebserver;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;


public class Server extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        // Start updating the camera image
        start_camera();
        // Serve the image
        start_server( 8080 );
    }

    public void start_server( final int port ) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    run_server( port );
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };
        thread.start();
    }

    public void run_server( int port ) throws IOException {
        ServerSocket listener = new ServerSocket( port );
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                    // out.println(new Date().toString());
                    out.println( frame.toString() );
                } finally {
                    socket.close();
                }
            }
        }
        finally {
            listener.close();
        }
    }

    public void start_camera() {
        Thread thread = new Thread() {
            @Override
            public void run() {
                update_frame();
            }
        };

        thread.start();
    }

    public void update_frame() {
        while ( true )
        {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);
        }
    }

    private Bitmap frame;
    private static final int CAMERA_REQUEST = 1888;

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if ( resultCode == RESULT_OK) {
            frame = (Bitmap) data.getExtras().get("data");
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_server, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

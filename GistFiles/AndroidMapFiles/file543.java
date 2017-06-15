package com.example.TrafficJam;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
    }

    public void buttonClick( View view ) {
        int id = view.getId();

        Intent intent = null;
        switch ( id ) {
            case R.id.button_play:
                intent = new Intent( this, GameActivity.class );
                startActivity( intent );
                break;
        }
        switch ( id ) {
            case R.id.button_puzzles:
                intent = new Intent( this, SelectPuzzleActivity.class );
                startActivity( intent );
                break;
        }
    }
}

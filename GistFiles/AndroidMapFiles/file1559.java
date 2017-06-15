package com.example.TrafficJam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import com.example.TrafficJam.Logic.*;

/**
 * Created with IntelliJ IDEA.
 * User: steinar
 * Date: 3/24/13
 * Time: 10:33 AM
 * To change this template use File | Settings | File Templates.
 */
public class GameActivity extends Activity {

    GameView mGameView;
    TextView mScoreView;

    GameLogic mGameLogic;

    private int mColorFirst;
    private int mColorSecond;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game);

        mGameView = (GameView) findViewById( R.id.game_view );
        mScoreView = (TextView) findViewById(R.id.score);
        mGameView.post(new Runnable() {
            @Override
            public void run() {
                mGameView.addShape(Color.GREEN);
                mGameView.addShape(Color.CYAN);
            }
        });

        Intent intent = getIntent();
        mColorFirst = intent.getIntExtra("ColorFirst", Color.GREEN);
        mColorSecond = intent.getIntExtra("ColorSecond", Color.CYAN);


        mGameView.setCustomEventHandler(new GameEventHandler() {
            @Override
            public void onShapeMoved() {
                System.out.println("EVENT onShapeMoved");
                Integer score = Integer.parseInt(mScoreView.getText().toString());
                ++score;
                mScoreView.setText(score.toString());
            }
        });

    }
}
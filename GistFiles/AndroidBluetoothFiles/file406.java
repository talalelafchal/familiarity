package com.zoucher.pxlrt;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.zoucher.pxlrt.BluetoothService.LocalBinder;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.content.ComponentName;

import java.io.IOException;
import java.util.Random;
import java.util.UUID;

public class snakeGame extends AppCompatActivity {

    // variables
    Button btnLeft, btnRight, btnStart, btnStop, btnPause;
    TextView txtPoints, txtLevel;
    int color;
    boolean statusGame = false;
    boolean pauzeGame = false;
    boolean stopGame = false;
    static final int maxLengthSnake = 64;
    int SnakeLenght;
    /*
        1 up
        2 under
        3 left
        4 right
     */
    static final int TOP = 0;
    static final int RIGHT = 1;
    static final int LEFT = 3;
    static final int BOTTOM = 2;
    int Direction = TOP;
    int Level = 1;
    int Points;
    static final int delay = 350;
    int DelayTime = 1000;
    int Position[] = new int[2];

    // array of buttons
    Button buttons[][] = new Button[8][8];
    int fruitX;
    int fruitY;
    int snakeX[] = new int[maxLengthSnake];
    int snakeY[] = new int[maxLengthSnake];

    Handler m_handler;
    Runnable m_handlerTask;

    Button btnSnakeLeft, btnSnakeRight, btnSnakeRestart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // import settings
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String strUserName = prefs.getString("example_text","User");
        String strChooseTheme = prefs.getString("example_list", "1");

        themeUtils.onActivityCreateSetTheme(this, strChooseTheme);

        setContentView(R.layout.activity_snake_game);

        // change title in action bar
        setTitle("Snake");

        // set back arrow in action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        // change color to black
        color = Color.BLACK;

        // call widgets
        txtLevel = (TextView)findViewById(R.id.txtLevel);
        txtPoints = (TextView)findViewById(R.id.txtPoints);
        btnPause = (Button)findViewById(R.id.btnPause);
        btnLeft = (Button)findViewById(R.id.btnLeft);
        btnRight = (Button)findViewById(R.id.btnRight);
        btnStart = (Button)findViewById(R.id.btnStart);
        btnStop = (Button)findViewById(R.id.btnStop);
        buttons[0][0] = (Button) findViewById(R.id.btn00);
        buttons[0][1] = (Button) findViewById(R.id.btn01);
        buttons[0][2] = (Button) findViewById(R.id.btn02);
        buttons[0][3] = (Button) findViewById(R.id.btn03);
        buttons[0][4] = (Button) findViewById(R.id.btn04);
        buttons[0][5] = (Button) findViewById(R.id.btn05);
        buttons[0][6] = (Button) findViewById(R.id.btn06);
        buttons[0][7] = (Button) findViewById(R.id.btn07);
        buttons[1][0] = (Button) findViewById(R.id.btn10);
        buttons[1][1] = (Button) findViewById(R.id.btn11);
        buttons[1][2] = (Button) findViewById(R.id.btn12);
        buttons[1][3] = (Button) findViewById(R.id.btn13);
        buttons[1][4] = (Button) findViewById(R.id.btn14);
        buttons[1][5] = (Button) findViewById(R.id.btn15);
        buttons[1][6] = (Button) findViewById(R.id.btn16);
        buttons[1][7] = (Button) findViewById(R.id.btn17);
        buttons[2][0] = (Button) findViewById(R.id.btn20);
        buttons[2][1] = (Button) findViewById(R.id.btn21);
        buttons[2][2] = (Button) findViewById(R.id.btn22);
        buttons[2][3] = (Button) findViewById(R.id.btn23);
        buttons[2][4] = (Button) findViewById(R.id.btn24);
        buttons[2][5] = (Button) findViewById(R.id.btn25);
        buttons[2][6] = (Button) findViewById(R.id.btn26);
        buttons[2][7] = (Button) findViewById(R.id.btn27);
        buttons[3][0] = (Button) findViewById(R.id.btn30);
        buttons[3][1] = (Button) findViewById(R.id.btn31);
        buttons[3][2] = (Button) findViewById(R.id.btn32);
        buttons[3][3] = (Button) findViewById(R.id.btn33);
        buttons[3][4] = (Button) findViewById(R.id.btn34);
        buttons[3][5] = (Button) findViewById(R.id.btn35);
        buttons[3][6] = (Button) findViewById(R.id.btn36);
        buttons[3][7] = (Button) findViewById(R.id.btn37);
        buttons[4][0] = (Button) findViewById(R.id.btn40);
        buttons[4][1] = (Button) findViewById(R.id.btn41);
        buttons[4][2] = (Button) findViewById(R.id.btn42);
        buttons[4][3] = (Button) findViewById(R.id.btn43);
        buttons[4][4] = (Button) findViewById(R.id.btn44);
        buttons[4][5] = (Button) findViewById(R.id.btn45);
        buttons[4][6] = (Button) findViewById(R.id.btn46);
        buttons[4][7] = (Button) findViewById(R.id.btn47);
        buttons[5][0] = (Button) findViewById(R.id.btn50);
        buttons[5][1] = (Button) findViewById(R.id.btn51);
        buttons[5][2] = (Button) findViewById(R.id.btn52);
        buttons[5][3] = (Button) findViewById(R.id.btn53);
        buttons[5][4] = (Button) findViewById(R.id.btn54);
        buttons[5][5] = (Button) findViewById(R.id.btn55);
        buttons[5][6] = (Button) findViewById(R.id.btn56);
        buttons[5][7] = (Button) findViewById(R.id.btn57);
        buttons[6][0] = (Button) findViewById(R.id.btn60);
        buttons[6][1] = (Button) findViewById(R.id.btn61);
        buttons[6][2] = (Button) findViewById(R.id.btn62);
        buttons[6][3] = (Button) findViewById(R.id.btn63);
        buttons[6][4] = (Button) findViewById(R.id.btn64);
        buttons[6][5] = (Button) findViewById(R.id.btn65);
        buttons[6][6] = (Button) findViewById(R.id.btn66);
        buttons[6][7] = (Button) findViewById(R.id.btn67);
        buttons[7][0] = (Button) findViewById(R.id.btn70);
        buttons[7][1] = (Button) findViewById(R.id.btn71);
        buttons[7][2] = (Button) findViewById(R.id.btn72);
        buttons[7][3] = (Button) findViewById(R.id.btn73);
        buttons[7][4] = (Button) findViewById(R.id.btn74);
        buttons[7][5] = (Button) findViewById(R.id.btn75);
        buttons[7][6] = (Button) findViewById(R.id.btn76);
        buttons[7][7] = (Button) findViewById(R.id.btn77);

        // set text in buttons
        btnLeft.setText("Left");
        btnRight.setText("Right");
        btnStart.setText("Start");
        btnStop.setText("Stop");
        btnPause.setText("Pause");
        txtLevel.setText("Level: 1");
        txtPoints.setText("Points: 1");

        // make buttons not clickable and grey them out
        btnPause.setAlpha(.5f);
        btnPause.setClickable(false);
        btnStop.setAlpha(.5f);
        btnStop.setClickable(false);

        // give buttons black background
        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                buttons[i][j].setBackgroundColor(color);
            }
        }

        // make snake array
        for(int i = 1; i < maxLengthSnake; i++) {
            snakeX[i] = snakeY[i] = -1;
        }

        // start clock
        startClock();

        // when start is pressed, the game can begin
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSnake(1,RIGHT);
                statusGame = true;
                checkButtons();
                txtLevel.setText("Level: 1");
                txtPoints.setText("Points: 1");
                btnPause.setAlpha(1f);
                btnPause.setClickable(true);
                btnStop.setAlpha(1f);
                btnStop.setClickable(true);
                stopGame = false;
            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(statusGame) {
                    statusGame = false;
                }
                // give buttons black background
                for(int i=0;i<8;i++) {
                    for(int j=0;j<8;j++) {
                        buttons[i][j].setBackgroundColor(color);
                    }
                }
                btnPause.setAlpha(.5f);
                btnPause.setClickable(false);
                btnStop.setAlpha(.5f);
                btnStop.setClickable(false);
                openScoreboard();
            }
        });
        btnPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pauzeGame) {
                    pauzeGame = true;
                    statusGame = false;
                    btnStop.setAlpha(.5f);
                    btnStop.setClickable(false);
                    btnPause.setText("Resume");
                } else if(pauzeGame) {
                    pauzeGame = false;
                    statusGame = true;
                    btnStop.setAlpha(1f);
                    btnStop.setClickable(true);
                    btnPause.setText("Pause");
                }
            }
        });
    }

	// clock function, gives a tick when the delaytime is passed to do the next step
    public void startClock() {
        m_handler = new Handler();
        m_handlerTask = new Runnable()
        {
            @Override
            public void run() {
                snakeGame();
                m_handler.postDelayed(m_handlerTask, DelayTime);
            }
        };
        m_handlerTask.run();
    }
	
	// when a new game is started open this function to give the variables a new value
    public void startSnake(int InitLevel, int InitDirection) {
        Level = InitLevel;
        Direction = InitDirection;
        snakeX[0] = 3;
        snakeY[0] = 3;
        SnakeLenght = 1;
        maakFruit();
        snakeGame();
        // make snake array
        for(int i = 1; i < maxLengthSnake; i++) {
            snakeX[i] = snakeY[i] = -1;
        }
    }

	// function to generate fruit
    public void maakFruit() {
        int x, y;
        Random randomX = new Random();
        x = randomX.nextInt(8);
        Random randomY = new Random();
        y = randomY.nextInt(8);
		// when the new fruit position is on a snake position, generate new x and y values
        while (isPartOfSnake(x,y)){
            x = randomX.nextInt(8);
            y = randomY.nextInt(8);
        }
        fruitX = x;
        fruitY = y;
    }

	// function to control if the next position is at a position where the body of the snake is
    public boolean isPartOfSnake(int x, int y) {
        for(int i = 0; i<SnakeLenght-1;i++) {
            if((x == snakeX[i]) && (y == snakeY[i])) {
                return true;
            }
        }
        return false;
    }

	// function that controles the direction
    public void checkButtons() {
		// when btnRight is pressed, change direction with one step to right
        btnRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int momenteleDirection = Direction;
                Direction--;
                if(Direction<0) {
                    Direction = LEFT;
                }
            }
        });
		// when btnLeft is pressed, change direction with one step to left
        btnLeft.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int momenteleDirection = Direction;
                Direction++;
                if(Direction>3) {
                    Direction = TOP;
                }
            }
        });
    }
	
	// function to draw the snake
    public void drawSnake() {
        for(int i = 0; i<SnakeLenght; i++) {
            if(!stopGame) {
                if (snakeX[i] == -1 && snakeY[i] == -1) {

                } else {
                    try {
                        buttons[snakeX[i]][snakeY[i]].setBackgroundColor(Color.BLUE);
                    } catch (Exception e) {
                        if (statusGame) {
                            statusGame = false;
                        }
                        // give buttons black background
                        for (int j = 0; j < 8; j++) {
                            for (int k = 0; k < 8; k++) {
                                buttons[j][k].setBackgroundColor(color);
                            }
                        }
                        stopGame = true;
                        btnPause.setAlpha(.5f);
                        btnPause.setClickable(false);
                        btnStop.setAlpha(.5f);
                        btnStop.setClickable(false);
                        openScoreboard();
                    }
                }
            }
        }
    }

	// function to draw fruit at generated x and y positions
    public void drawFruit() {
        if(!stopGame) {
            buttons[fruitX][fruitY].setBackgroundColor(Color.YELLOW);
        }
    }

	// make everything visible
    public void draw() {
        // give buttons black background
        for(int i=0;i<8;i++) {
            for(int j=0;j<8;j++) {
                buttons[i][j].setBackgroundColor(color);
            }
        }
        drawSnake();
        drawFruit();
    }

    public void openScoreboard() {
        /* Intent intent = new Intent(snakeGame.this, ScoreboardActivity.class);
        intent.putExtra("SCORE", String.valueOf(Points));
        startActivity(intent); */
    }

	// generate next step
    public void nextStep() {
        for(int i = SnakeLenght - 1;i>0;i--) {
            snakeX[i] = snakeX[i-1];
            snakeY[i] = snakeY[i-1];
        }

        switch (Direction) {
            case TOP:
                if(isPartOfSnake(snakeX[0], snakeY[0]-1)) {
                    if (statusGame) {
                        statusGame = false;
                    }
                    // give buttons black background
                    for (int j = 0; j < 8; j++) {
                        for (int k = 0; k < 8; k++) {
                            buttons[j][k].setBackgroundColor(color);
                        }
                    }
                    stopGame = true;
                    btnPause.setAlpha(.5f);
                    btnPause.setClickable(false);
                    btnStop.setAlpha(.5f);
                    btnStop.setClickable(false);
                    openScoreboard();
                } else {
                    snakeY[0] = snakeY[0] - 1;
                    break;
                }
            case RIGHT:
                if(isPartOfSnake(snakeX[0]+1, snakeY[0])) {
                    if (statusGame) {
                        statusGame = false;
                    }
                    // give buttons black background
                    for (int j = 0; j < 8; j++) {
                        for (int k = 0; k < 8; k++) {
                            buttons[j][k].setBackgroundColor(color);
                        }
                    }
                    stopGame = true;
                    btnPause.setAlpha(.5f);
                    btnPause.setClickable(false);
                    btnStop.setAlpha(.5f);
                    btnStop.setClickable(false);
                    openScoreboard();
                } else {
                    snakeX[0] = snakeX[0] + 1;
                    break;
                }
            case BOTTOM:
                if(isPartOfSnake(snakeX[0], snakeY[0]+1)) {
                    if (statusGame) {
                        statusGame = false;
                    }
                    // give buttons black background
                    for (int j = 0; j < 8; j++) {
                        for (int k = 0; k < 8; k++) {
                            buttons[j][k].setBackgroundColor(color);
                        }
                    }
                    stopGame = true;
                    btnPause.setAlpha(.5f);
                    btnPause.setClickable(false);
                    btnStop.setAlpha(.5f);
                    btnStop.setClickable(false);
                    openScoreboard();
                } else {
                    snakeY[0] = snakeY[0] + 1;
                    break;
                }
            case LEFT:
                if(isPartOfSnake(snakeX[0]-1, snakeY[0])) {
                    if (statusGame) {
                        statusGame = false;
                    }
                    // give buttons black background
                    for (int j = 0; j < 8; j++) {
                        for (int k = 0; k < 8; k++) {
                            buttons[j][k].setBackgroundColor(color);
                        }
                    }
                    stopGame = true;
                    btnPause.setAlpha(.5f);
                    btnPause.setClickable(false);
                    btnStop.setAlpha(.5f);
                    btnStop.setClickable(false);
                    openScoreboard();
                } else {
                    snakeX[0] = snakeX[0] - 1;
                    break;
                }
        }

        if((snakeX[0] == fruitX) && (snakeY[0] == fruitY)){
            if(SnakeLenght < maxLengthSnake){
                maakFruit();
            }
            else {
                fruitX = fruitY = -1;
            }
            SnakeLenght++;
        }
    }

    public void isLockationFree() {
        for(int i = 0; i<SnakeLenght;i++) {

        }
    }

	// calculate the delay
    public void calculateDelay() {
        if(SnakeLenght == 1) {
            Level = 1;
        } else if(SnakeLenght == 3 ) {
            Level = 2;
        } else if(SnakeLenght == 8) {
            Level = 3;
        } else if(SnakeLenght == 16) {
            Level = 4;
        } else if(SnakeLenght == 30) {
            Level = 5;
        } else if(SnakeLenght == 45) {
            Level = 6;
        } else if(SnakeLenght == 60) {
            Level = 7;
        }
        DelayTime = delay / Level * 3;
    }

	// change interface with new points and level status
    public void updateInterface() {
        txtLevel.setText("Level: " + Level);
        Points = Level * SnakeLenght;
        txtPoints.setText("Points: " + Points);
    }

	// main program
    public void snakeGame() {
        if(statusGame) {
            calculateDelay();
            updateInterface();
            nextStep();
            draw();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
        }

        if (id == R.id.action_help) {
            Intent i = new Intent(this, HelpActivity.class);
            startActivity(i);
        }

        if (id == R.id.action_contact) {
            Intent i = new Intent(this, SendEmail.class);
            startActivity(i);
        }

        if (id == android.R.id.home) {
            finish();
            Intent i = new Intent(snakeGame.this, MenuScreen.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        finish();
        Intent i = new Intent(snakeGame.this, MenuScreen.class);
        startActivity(i);
    }
}

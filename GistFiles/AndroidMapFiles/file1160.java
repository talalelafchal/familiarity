package hhp.firstapp;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;



public class HelloWorld extends ActionBarActivity implements View.OnClickListener{
    private Button YesClick, NoClick, AgainBtn, playBtn;
    private int firstNum, secondNum, resultNum;
    private int scorePlayer, trueOverall, falseOverall;
    private int numButtons = 2, previousLineId;
    private TextView equationTv, resultText, scoreTv;
    private List<Button> listOfSkin;
    private long startTime = 0L;
    private Handler customHandler = new Handler();
    private long timeInMilliseconds, timeSwapBuff, updatedTime;
    private TextView timerValue;
    private int percentWin;
    private final double percentRightCalculation = 3.0/5;
    private Runnable updateTimerThread = new Runnable() {
        public void run() {
            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;
            int secs = (int) (updatedTime / 1000);
            secs = secs % 60;
            int milliseconds = (int) (updatedTime % 1000);
            timerValue.setText(""
                            + String.format("%02d", secs) + ":"
                            + String.format("%03d", milliseconds));
            if (secs*1000+milliseconds>=5*1000*percentWin/100.0)
                UpdateScore(false);
            customHandler.postDelayed(this, 0);
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen); // set content view to set the view in smart phone
        playBtn = (Button) findViewById(R.id.playBtn);
        playBtn.setOnClickListener(this);
        listOfSkin = new ArrayList<>();
        // Advanced: lam 1 activity voi 2 layout hoan toan khac nhau nhung van chay tot
        // duplicate id name ... nhung khac vi tri cua cac button ... v.v cho thanh giao dien moi
        //Note: 1 layout the object khac ID, khac layout thi wahtever
            //khai bao tu dong hoac khai bao tu dau trong ham onCreate
        // co the map nhieu object vao nhieu ham khac nhau (event)
        //su dung tru catch de thu xem objcet do la type nao
        //---nhan 1 nut xuat hien 1 control ma minh mong muon
        //HomeWork:
        //Beg: lam FreakMath
    }
    private int randomNumber(int range) {
        Random rand = new Random();
        return rand.nextInt(range);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hello_world, menu);
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


    @Override
    public void onClick(View v) {
        if (v == playBtn){
            setContentView(R.layout.choose_skin);
            findViewById(R.id.skin1Btn).setOnClickListener(this);
            findViewById(R.id.skin2Btn).setOnClickListener(this);
            findViewById(R.id.updateSkinBtn).setOnClickListener(this);
            //init for adding new button dinamically
            listOfSkin.add((Button) findViewById(R.id.skin2Btn));
            previousLineId = R.id.textView3;
        }
        else if (v == findViewById(R.id.skin1Btn)){
            setContentView(R.layout.activity_hello_world);
            timerValue = (TextView) findViewById(R.id.timerValue);
            InitializeData();
        }
        else if (v == findViewById(R.id.skin2Btn)){
            setContentView(R.layout.activity_hello_world_2);
            timerValue = (TextView) findViewById(R.id.timerValue);
            InitializeData();
        }
        else if (v == YesClick){
            if (checkEquation()) {
                ++trueOverall;
                UpdateScore(true);
            }
            else
                UpdateScore(false);

        }else if (v == NoClick){
            if (!checkEquation()) {
                ++falseOverall;
                UpdateScore(true);
            }
            else
                UpdateScore(false);
        }
        else if (v == AgainBtn){
            AgainBtn.setVisibility(View.INVISIBLE);
            resultText.setVisibility(View.INVISIBLE);
            timerValue.setVisibility(View.VISIBLE);
            YesClick.setVisibility(View.VISIBLE);
            NoClick.setVisibility(View.VISIBLE);
            InitGame();
        }
        else if (v == findViewById(R.id.updateSkinBtn)){
            Button myButton = new Button(this, null, android.R.attr.buttonStyleSmall);
            numButtons++;
            myButton.setText(Integer.toString(numButtons));
            myButton.setTextSize(22);

            myButton.setId(numButtons);
            RelativeLayout ll = (RelativeLayout)findViewById(R.id.choseSkin);
            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
            Button tmpBtn = listOfSkin.get(listOfSkin.size() - 1);

            if ((numButtons-1)%6 == 0 ) { //at most 6 button in 1 line
                previousLineId = tmpBtn.getId();
                lp.addRule(RelativeLayout.BELOW, previousLineId);
                lp.setMarginStart(((RelativeLayout.LayoutParams) findViewById(R.id.skin1Btn).getLayoutParams()).leftMargin); // the left boundary
            }
            else {
                lp.addRule(RelativeLayout.RIGHT_OF, tmpBtn.getId());
                lp.addRule(RelativeLayout.BELOW, previousLineId);
            }
            myButton.setLayoutParams(lp);
            ll.addView(myButton, lp);
            listOfSkin.add(myButton);
        }

    }


    private void InitializeData() {
        scorePlayer = 0;
        percentWin = 100;
        AgainBtn = (Button) findViewById(R.id.againBtn);
        YesClick = (Button) findViewById(R.id.answerYes);
        NoClick = (Button) findViewById(R.id.answerNo);
        scoreTv = (TextView) findViewById(R.id.scoreTv);
        resultText = (TextView) findViewById(R.id.resultText);
        equationTv = (TextView)findViewById(R.id.equationTv);

        YesClick.setOnClickListener(this);
        NoClick.setOnClickListener(this);
        AgainBtn.setOnClickListener(this);

        resultText.setVisibility(View.INVISIBLE);
        AgainBtn.setVisibility(View.INVISIBLE);
        //init
        InitGame();
    }

    private void UpdateScore(boolean index) {
        resultText.setVisibility(View.VISIBLE);
        if (index) {
            resultText.setText("Next ...");
            ++scorePlayer;
            if (percentWin> 25) --percentWin;
            InitGame();
        }
        else {
            //STOP THE CLOCK
            timeSwapBuff += timeInMilliseconds;
            customHandler.removeCallbacks(updateTimerThread);
            scorePlayer = 0;
            trueOverall = 0;
            falseOverall = 0;
            percentWin = 100;
            timerValue.setVisibility(View.INVISIBLE);
            YesClick.setVisibility(View.INVISIBLE);
            NoClick.setVisibility(View.INVISIBLE);
            AgainBtn.setVisibility(View.VISIBLE);
            resultText.setText("You Lose !!!");
        }

    }

    private void InitGame() {
        //Time countdown
        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        firstNum=randomNumber(10);
        secondNum=randomNumber(10);
        float sum = trueOverall+falseOverall;
        if ( ((float)trueOverall)/sum <= percentRightCalculation )
            resultNum = firstNum + secondNum;
        else
            resultNum=randomNumber(20);
        equationTv.setText(Integer.toString(firstNum) + " + " + Integer.toString(secondNum) + " = " + Integer.toString(resultNum));
        scoreTv.setText("Scores:" + Integer.toString(scorePlayer));
    }

    private boolean checkEquation() {
        if (firstNum + secondNum == resultNum)
            return true;
        return false;
    }
}

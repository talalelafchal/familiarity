package com.example.android.quizapp;

import android.os.CountDownTimer;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Handler;
import android.widget.ViewFlipper;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import static android.R.id.input;
import static android.R.id.message;
import static android.media.CamcorderProfile.get;
import static android.widget.Toast.makeText;
import static com.example.android.quizapp.R.id.BQ1;
import static com.example.android.quizapp.R.id.RadioB;
import static com.example.android.quizapp.R.layout.toast;
import static com.example.android.quizapp.R.layout.toast_two;
import static com.example.android.quizapp.R.string.question_number;
import static com.example.android.quizapp.R.string.results;

public class MainActivity extends AppCompatActivity {

    // Variables for the Storing
    static final String STATE_QUESTION = "questionAtThisMoment";
    static final String STATE_RESULTS = "resultsAtThisMoment";
    boolean buttonA = false;
    boolean buttonB = false;
    boolean buttonC = false;
    boolean buttonD = false;
    int currentQuestion = 1;
    boolean answeredWrong = false;
    int result = 10;
    int bonusquestnr = 0;
    int resultBonus = 5;

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putInt(STATE_QUESTION, currentQuestion);
        savedInstanceState.putInt(STATE_RESULTS, result);

        savedInstanceState.putInt(STATE_QUESTION, bonusquestnr);
        savedInstanceState.putInt(STATE_QUESTION, resultBonus);

        // Call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    public void onRestoreInstanceState(Bundle savedInstanceState) {
        // Call the superclass so it can restore the view hierarchy
        super.onRestoreInstanceState(savedInstanceState);

        // Restore state members from saved instance
        currentQuestion = savedInstanceState.getInt(STATE_QUESTION);
        result = savedInstanceState.getInt(STATE_RESULTS);

        bonusquestnr = savedInstanceState.getInt(STATE_QUESTION);
        resultBonus = savedInstanceState.getInt(STATE_RESULTS);

        Button resultsButton = (Button) findViewById(R.id.get_results);
        resultsButton.setVisibility(View.INVISIBLE);
        Button bonusButton = (Button) findViewById(R.id.bonus_button);
        bonusButton.setVisibility(View.INVISIBLE);

        if (bonusquestnr == 0) {
            ViewFlipper vf = (ViewFlipper) findViewById( R.id.viewFlipper );
            vf.showPrevious();
            switch (currentQuestion) {
                case 1:
                    currentQuestion = 1;
                    TextView questionNr = (TextView) findViewById(R.id.questionNumber);
                    questionNr.setText(getString(R.string.question_number, currentQuestion));
                    break;
                case 2:
                    currentQuestion = 2;
                    updateQuestions(getString(R.string.question2), getString(R.string.q2_a), getString(R.string.q2_b), getString(R.string.q2_c), getString(R.string.q2_d));
                    break;
                case 3:
                    currentQuestion = 3;
                    updateQuestions(getString(R.string.question3), getString(R.string.q3_a), getString(R.string.q3_b), getString(R.string.q3_c), getString(R.string.q3_d));
                    break;
                case 4:
                    currentQuestion = 4;
                    updateQuestions(getString(R.string.question4), getString(R.string.q4_a), getString(R.string.q4_b), getString(R.string.q4_c), getString(R.string.q4_d));
                    break;
                case 5:
                    currentQuestion = 5;
                    updateQuestions(getString(R.string.question5), getString(R.string.q5_a), getString(R.string.q5_b), getString(R.string.q5_c), getString(R.string.q5_d));
                    break;
                case 6:
                    currentQuestion = 6;
                    updateQuestions(getString(R.string.question6), getString(R.string.q6_a), getString(R.string.q6_b), getString(R.string.q6_c), getString(R.string.q6_d));
                    break;
                case 7:
                    currentQuestion = 7;
                    updateQuestions(getString(R.string.question7), getString(R.string.q7_a), getString(R.string.q7_b), getString(R.string.q7_c), getString(R.string.q7_d));
                    break;
                case 8:
                    currentQuestion = 8;
                    updateQuestions(getString(R.string.question8), getString(R.string.q8_a), getString(R.string.q8_b), getString(R.string.q8_c), getString(R.string.q8_d));
                    break;
                case 9:
                    currentQuestion = 9;
                    updateQuestions(getString(R.string.question9), getString(R.string.q9_a), getString(R.string.q9_b), getString(R.string.q9_c), getString(R.string.q9_d));
                    break;
                case 10:
                    currentQuestion = 10;
                    updateQuestions(getString(R.string.question10), getString(R.string.q10_a), getString(R.string.q10_b), getString(R.string.q10_c), getString(R.string.q10_d));
                    break;
                case 11:
                    currentQuestion = 11;
                    showResults(result);
                    break;
            }
        } else if (bonusquestnr > 0){
            // Load right activity
            ViewFlipper vf = (ViewFlipper) findViewById( R.id.viewFlipper );
            vf.showNext();
            TextView questionNr = (TextView) findViewById(R.id.questionNumber);
            // Get all views
            EditText BQ1 = (EditText) findViewById(R.id.BQ1); // TextInput Q1 & Q5
            Button ETB = (Button) findViewById(R.id.editTextButton); // Button for TextInput (Q1/Q5)
            View BQ2 = (View) findViewById(R.id.checkboxLayout); // Checkboxes for Q2 & Q4
            RadioGroup BQ3 = (RadioGroup) findViewById(R.id.BonusRadioGroup); // RadioButtons for Q3
            Button Check = (Button) findViewById(R.id.CheckBoxButton);
            // Set all views to invisible
            BQ1.setVisibility(View.INVISIBLE);
            ETB.setVisibility(View.INVISIBLE);
            BQ2.setVisibility(View.INVISIBLE);
            BQ3.setVisibility(View.INVISIBLE);
            Check.setVisibility(View.INVISIBLE);
            // Update activity depending on the question nr.
            switch (bonusquestnr) {
                case 1:
                    bonusquestnr = 1;
                    questionNr.setText(getString(R.string.question_number, bonusquestnr));
                    updateBonusQuestions(bonusquestnr);
                    break;
                case 2:
                    bonusquestnr = 2;
                    questionNr.setText(getString(R.string.question_number, bonusquestnr));
                    updateBonusQuestions(bonusquestnr);
                    break;
                case 3:
                    bonusquestnr = 3;
                    questionNr.setText(getString(R.string.question_number, bonusquestnr));
                    updateBonusQuestions(bonusquestnr);
                    break;
                case 4:
                    bonusquestnr = 4;
                    questionNr.setText(getString(R.string.question_number, bonusquestnr));
                    updateBonusQuestions(bonusquestnr);
                    break;
                case 5:
                    bonusquestnr = 5;
                    questionNr.setText(getString(R.string.question_number, bonusquestnr));
                    updateBonusQuestions(bonusquestnr);
                    break;
                }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView questionNr = (TextView) findViewById(R.id.questionNumber);
        questionNr.setText(getString(R.string.question_number, currentQuestion));
    }

    /**
     * When button A is pressed, boolean is set to true and Masterbrain is called.
     */
    public void setButtonA(View view) {
        buttonA = true;
        masterBrain(view);
    }

    /**
     * When button B is pressed, boolean is set to true and Masterbrain is called.
     */
    public void setButtonB(View view) {
        buttonB = true;
        masterBrain(view);
    }

    /**
     * When button C is pressed, boolean is set to true and Masterbrain is called.
     */
    public void setButtonC(View view) {
        buttonC = true;
        masterBrain(view);
    }

    /**
     * When button D is pressed, boolean is set to true and Masterbrain is called.
     */
    public void setButtonD(View view) {
        buttonD = true;
        masterBrain(view);
    }

    /**
     * Masterbrain of the app that changes the strings and keeps track of the score.
     */
    public void masterBrain(View view) {

        // Create Local Variables
        String answerGiven = "X";

        if (buttonA) {
            answerGiven = "A";
        } else if (buttonB) {
            answerGiven = "B";
        } else if (buttonC) {
            answerGiven = "C";
        } else {
            answerGiven = "D";
        }

        //Creating the LayoutInflater instance
        //LayoutInflater li = getLayoutInflater();
        //Getting the View object as defined in the customtoast.xml file
        //View layout = li.inflate(toast,
        //       (ViewGroup) findViewById(R.id.custom_toast_layout));

        // Correct Answer, show toast, increase current question by 1
        if (currentQuestion == 1 && answerGiven == "D") {
            showToast(getString(R.string.q1_d_msg));
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            currentQuestion += 1;
            updateQuestions(getString(R.string.question2), getString(R.string.q2_a), getString(R.string.q2_b), getString(R.string.q2_c), getString(R.string.q2_d));
        } else if (currentQuestion == 2 && answerGiven == "B") {
            showToast(getString(R.string.q2_b_msg));
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            currentQuestion += 1;
            updateQuestions(getString(R.string.question3), getString(R.string.q3_a), getString(R.string.q3_b), getString(R.string.q3_c), getString(R.string.q3_d));
        } else if (currentQuestion == 3 && answerGiven == "C") {
            showToast(getString(R.string.q3_c_msg));
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            currentQuestion += 1;
            updateQuestions(getString(R.string.question4), getString(R.string.q4_a), getString(R.string.q4_b), getString(R.string.q4_c), getString(R.string.q4_d));
        } else if (currentQuestion == 4 && answerGiven == "C") {
            showToast(getString(R.string.q4_c_msg));
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            currentQuestion += 1;
            updateQuestions(getString(R.string.question5), getString(R.string.q5_a), getString(R.string.q5_b), getString(R.string.q5_c), getString(R.string.q5_d));
        } else if (currentQuestion == 5 && answerGiven == "A") {
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            showToast(getString(R.string.q5_a_msg));
            currentQuestion += 1;
            updateQuestions(getString(R.string.question6), getString(R.string.q6_a), getString(R.string.q6_b), getString(R.string.q6_c), getString(R.string.q6_d));
        } else if (currentQuestion == 6 && answerGiven == "B") {
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            showToast(getString(R.string.q6_b_msg));
            currentQuestion += 1;
            updateQuestions(getString(R.string.question7), getString(R.string.q7_a), getString(R.string.q7_b), getString(R.string.q7_c), getString(R.string.q7_d));
        } else if (currentQuestion == 7 && answerGiven == "A") {
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            showToast(getString(R.string.q7_a_msg));
            currentQuestion += 1;
            updateQuestions(getString(R.string.question8), getString(R.string.q8_a), getString(R.string.q8_b), getString(R.string.q8_c), getString(R.string.q8_d));
        } else if (currentQuestion == 8 && answerGiven == "D") {
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            showToast(getString(R.string.q8_d_msg));
            currentQuestion += 1;
            updateQuestions(getString(R.string.question9), getString(R.string.q9_a), getString(R.string.q9_b), getString(R.string.q9_c), getString(R.string.q9_d));
        } else if (currentQuestion == 9 && answerGiven == "C") {
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            showToast(getString(R.string.q9_c_msg));
            currentQuestion += 1;
            updateQuestions(getString(R.string.question10), getString(R.string.q10_a), getString(R.string.q10_b), getString(R.string.q10_c), getString(R.string.q10_d));
        } else if (currentQuestion == 10 && answerGiven == "D") {
            if (answeredWrong) {
                result = result - 1;
                answeredWrong = false;
            }
            showToast(getString(R.string.q10_d_msg));
            currentQuestion += 1;
        } else {
            if (currentQuestion == 1) {
                if (answerGiven == "A") {
                    showToastTwo(getString(R.string.q1_a_msg));
                    answeredWrong = true;
                } else if (answerGiven == "B") {
                    showToastTwo(getString(R.string.q1_b_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q1_c_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 2) {
                if (answerGiven == "A") {
                    showToastTwo(getString(R.string.q2_a_msg));
                    answeredWrong = true;
                } else if (answerGiven == "C") {
                    showToastTwo(getString(R.string.q2_c_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q2_d_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 3) {
                if (answerGiven == "A") {
                    showToastTwo(getString(R.string.q3_a_msg));
                    answeredWrong = true;
                } else if (answerGiven == "B") {
                    showToastTwo(getString(R.string.q3_b_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q3_d_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 4) {
                if (answerGiven == "A") {
                    showToastTwo(getString(R.string.q4_a_msg));
                    answeredWrong = true;
                } else if (answerGiven == "B") {
                    showToastTwo(getString(R.string.q4_b_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q4_d_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 5) {
                if (answerGiven == "B") {
                    showToastTwo(getString(R.string.q5_b_msg));
                    answeredWrong = true;
                } else if (answerGiven == "C") {
                    showToastTwo(getString(R.string.q5_c_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q5_d_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 6) {
                if (answerGiven == "A") {
                    showToastTwo(getString(R.string.q6_a_msg));
                    answeredWrong = true;
                } else if (answerGiven == "C") {
                    showToastTwo(getString(R.string.q6_c_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q6_d_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 7) {
                if (answerGiven == "B") {
                    showToastTwo(getString(R.string.q7_b_msg));
                    answeredWrong = true;
                } else if (answerGiven == "C") {
                    showToastTwo(getString(R.string.q7_c_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q7_d_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 8) {
                if (answerGiven == "A") {
                    showToastTwo(getString(R.string.q8_a_msg));
                    answeredWrong = true;
                } else if (answerGiven == "B") {
                    showToastTwo(getString(R.string.q8_b_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q8_c_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 9) {
                if (answerGiven == "A") {
                    showToastTwo(getString(R.string.q9_a_msg));
                    answeredWrong = true;
                } else if (answerGiven == "B") {
                    showToastTwo(getString(R.string.q9_b_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q9_d_msg));
                    answeredWrong = true;
                }
            } else if (currentQuestion == 10) {
                if (answerGiven == "A") {
                    showToastTwo(getString(R.string.q10_a_msg));
                    answeredWrong = true;
                } else if (answerGiven == "B") {
                    showToastTwo(getString(R.string.q10_b_msg));
                    answeredWrong = true;
                } else {
                    showToastTwo(getString(R.string.q10_c_msg));
                    answeredWrong = true;
                }
            }
        }
        buttonA = false;
        buttonB = false;
        buttonC = false;
        buttonD = false;
    }

    /**
     * Updates the questions.
     */
    public void updateQuestions(String questionToBeAnswered, String answerA, String answerB, String answerC, String answerD) {
        // Update question number
        TextView questionNr = (TextView) findViewById(R.id.questionNumber);
        questionNr.setText(getString(R.string.question_number, currentQuestion));

        // Update question:
        TextView quest = (TextView) findViewById(R.id.question);
        quest.setText(questionToBeAnswered);

        // Update answers:
        TextView ansA = (TextView) findViewById(R.id.answer_a_text);
        ansA.setText(answerA);

        TextView ansB = (TextView) findViewById(R.id.answer_b_text);
        ansB.setText(answerB);

        TextView ansC = (TextView) findViewById(R.id.answer_c_text);
        ansC.setText(answerC);

        TextView ansD = (TextView) findViewById(R.id.answer_d_text);
        ansD.setText(answerD);
    }

    /**
     * Create and show a toast for correct answered questions.
     * After the 10th question has been answered correctly, the show results method will be called.
     */
    public void showToast(String message) {

        // Set the toast duration
        int toastDurationInMilliSeconds = 8000;

        // Inflate toast XML layout
        View layout = getLayoutInflater().inflate(toast,
                (ViewGroup) findViewById(R.id.custom_toast_layout));

        // Fill in the message into the textview
        TextView text = (TextView) layout.findViewById(R.id.custom_toast_message);
        text.setText(message);

        // Construct the toast and set the view
        final Toast toastToShow = Toast.makeText(getApplicationContext(), "some message", Toast.LENGTH_LONG);
        toastToShow.setView(layout);
        toastToShow.setGravity(Gravity.BOTTOM, 0, 0);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toastToShow.show();
            }

            public void onFinish() {
                toastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        toastToShow.show();
        toastCountDown.start();

        if (currentQuestion == 10) {
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    showResults(result); //Delay the method call
                }
            }, 9000);
        }
    }

    /**
     * Create and show a toast for incorrect answered questions.
     */
    public void showToastTwo(String message) {

        // Set the toast duration
        int toastDurationInMilliSeconds = 7000;

        // Inflate toast XML layout
        View layout = getLayoutInflater().inflate(toast_two,
                (ViewGroup) findViewById(R.id.custom_toast_layout_two));

        // Fill in the message into the textview
        TextView text = (TextView) layout.findViewById(R.id.toast_msg);
        text.setText(message);

        // Construct the toast and set the view
        final Toast toastToShow = Toast.makeText(getApplicationContext(), "some message", Toast.LENGTH_LONG);
        toastToShow.setView(layout);
        toastToShow.setGravity(Gravity.BOTTOM, 0, 0);

        // Set the countdown to display the toast
        CountDownTimer toastCountDown;
        toastCountDown = new CountDownTimer(toastDurationInMilliSeconds, 1000 /*Tick duration*/) {
            public void onTick(long millisUntilFinished) {
                toastToShow.show();
            }

            public void onFinish() {
                toastToShow.cancel();
            }
        };

        // Show the toast and starts the countdown
        toastToShow.show();
        toastCountDown.start();
    }

    /**
     * after the 10th question the button 'get results' should appear.
     */
    public void showResults(int results) {
        Button resultsButton = (Button) findViewById(R.id.get_results);
        resultsButton.setVisibility(View.VISIBLE); // Show the 'get results' button
    }

    /**
     * Show the results & display the reset button
     */
    public void setResults(View view) {
        Button resultsButton = (Button) findViewById(R.id.get_results);
        resultsButton.setVisibility(View.INVISIBLE);

        TextView ResultsTextView = (TextView) findViewById(R.id.results_textview);
        ResultsTextView.setText(getString(R.string.show_results, result));

        ResultsTextView.setVisibility(View.VISIBLE); // Shows the results message

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Button resetButton = (Button) findViewById(R.id.reset_button);
                resetButton.setVisibility(View.VISIBLE); //Make Reset Button Visible after some seconds
            }
        }, 4000);

        if (result >= 7){ // Only if 7 or more questions were answered correctly
            new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                @Override
                public void run() {
                    Button bonusButton = (Button) findViewById(R.id.bonus_button);
                    bonusButton.setVisibility(View.VISIBLE); //Make Bonus Button Visible after some seconds
                }
            }, 4000);
        }
    }

    /**
     * Reset it all.
     */
    public void SetBackReset(View view) {
        // Change activity if bonus activity is loaded.
        ViewFlipper vf = (ViewFlipper) findViewById(R.id.viewFlipper);
        if (bonusquestnr > 1){
            vf.showPrevious();
            TextView resultsMsg = (TextView) findViewById(R.id.resultMsg);
            resultsMsg.setVisibility(View.INVISIBLE);
            Button resetButton = (Button) findViewById(R.id.bonus_resetbutton);
            resetButton.setVisibility(View.INVISIBLE);
        }
        // Set Globals back to their start value.
        currentQuestion = 1;
        result = 10;
        bonusquestnr = 0;
        // Temporary remove the 'results' TextView & 'reset' Button
        TextView ResultsTextView = (TextView) findViewById(R.id.results_textview);
        ResultsTextView.setVisibility(View.INVISIBLE);
        Button resetButton = (Button) findViewById(R.id.reset_button);
        resetButton.setVisibility(View.INVISIBLE);
        Button bonusButton = (Button) findViewById(R.id.bonus_button);
        bonusButton.setVisibility(View.INVISIBLE);
        // Update the question to be question #1.
        updateQuestions(getString(R.string.question1), getString(R.string.q1_a), getString(R.string.q1_b), getString(R.string.q1_c), getString(R.string.q1_d));
    }


    /////// *** BONUS *** ///////

    public void bonusButton (View view){
        ViewFlipper vf = (ViewFlipper) findViewById( R.id.viewFlipper);
        bonusquestnr += 1;
        updateBonusQuestions(bonusquestnr);
        vf.showNext();
    }


    /**
     * Updates the Bonus questions.
     */
    public void updateBonusQuestions(int bonusquestnr) {
        // Update question number
        TextView questionNr = (TextView) findViewById(R.id.bonusQuestionNumber);
        questionNr.setText(getString(R.string.question_number, bonusquestnr));

        // Update question:
        TextView quest = (TextView) findViewById(R.id.bonus_question); // Question (String)

        // Find Views
        EditText BQ1 = (EditText) findViewById(R.id.BQ1); // TextInput Q1 & Q5
        Button ETB = (Button) findViewById(R.id.editTextButton); // Button for TextInput (Q1/Q5)
        View BQ2 = (View) findViewById(R.id.checkboxLayout); // Checkboxes for Q2 & Q4
        RadioGroup BQ3 = (RadioGroup) findViewById(R.id.BonusRadioGroup); // RadioButtons for Q3
        Button Check = (Button) findViewById(R.id.CheckBoxButton);

        // Checkbox views
        CheckBox Q4A = (CheckBox) findViewById(R.id.CheckA);
        CheckBox Q4B = (CheckBox) findViewById(R.id.CheckB);
        CheckBox Q4C = (CheckBox) findViewById(R.id.CheckC);
        CheckBox Q4D = (CheckBox) findViewById(R.id.CheckD);

        // Update answers:
        if (bonusquestnr == 1) {
            quest.setText(getString(R.string.bonusQuest1));
            BQ1.setVisibility(View.VISIBLE);
            ETB.setVisibility(View.VISIBLE);
        } else if (bonusquestnr == 2) {
            quest.setText(getString(R.string.bonusQuest2));
            BQ1.setVisibility(View.INVISIBLE);
            ETB.setVisibility(View.INVISIBLE);
            BQ2.setVisibility(View.VISIBLE);
            Check.setVisibility(View.VISIBLE);
        } else if (bonusquestnr == 3) {
            quest.setText(getString(R.string.bonusQuest3));
            BQ2.setVisibility(View.INVISIBLE);
            Check.setVisibility(View.INVISIBLE);
            BQ3.setVisibility(View.VISIBLE);
        } else if (bonusquestnr == 4) {
            quest.setText(getString(R.string.bonusQuest4));
            BQ3.setVisibility(View.INVISIBLE);
            Q4A.setText(getString(R.string.bQ4_a));
            Q4B.setText(getString(R.string.bQ4_b));
            Q4C.setText(getString(R.string.bQ4_c));
            Q4D.setText(getString(R.string.bQ4_d));
            BQ2.setVisibility(View.VISIBLE);
            Check.setVisibility(View.VISIBLE);
        } else if (bonusquestnr == 5) {
            quest.setText(getString(R.string.bonusQuest5));
            BQ2.setVisibility(View.INVISIBLE);
            Check.setVisibility(View.INVISIBLE);
            BQ1.setVisibility(View.VISIBLE);
            ETB.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Find the Radio views declared in main.xml.
     */
    public void BonusRadio (View view){
        RadioButton RB1 = (RadioButton) findViewById(R.id.RadioA);
        RadioButton RB2 = (RadioButton) findViewById(RadioB);
        RadioButton RB3 = (RadioButton) findViewById(R.id.RadioC);

        // Set a listener that will listen for clicks on the radio buttons and perform suitable actions.
        RB1.setOnClickListener(radio_listener);
        RB2.setOnClickListener(radio_listener);
        RB3.setOnClickListener(radio_listener);
    }

    /**
     * Define a OnClickListener for Bonus Question 3 (Radio)
     */
    RadioButton RB1 = (RadioButton) findViewById(R.id.RadioA);
    RadioButton RB2 = (RadioButton) findViewById(RadioB);
    RadioButton RB3 = (RadioButton) findViewById(R.id.RadioC);

    private View.OnClickListener radio_listener = new View.OnClickListener() {
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.RadioA:
                    showToastTwo(getString(R.string.bQ3_a_msg));
                    updateBonusQuestions(bonusquestnr);
                    if (!answeredWrong){
                        resultBonus = resultBonus - 1;
                        answeredWrong = true;
                    }
                    RB1.toggle();
                    break;
                case RadioB: // Correct answer
                    showToast(getString(R.string.bQ3_b_msg));
                    bonusquestnr += 1;
                    updateBonusQuestions(bonusquestnr);
                    answeredWrong = false;
                    RB2.toggle();
                    break;
                case R.id.RadioC:
                    showToastTwo(getString(R.string.bQ3_c_msg));
                    updateBonusQuestions(bonusquestnr);
                    if (!answeredWrong){
                        resultBonus = resultBonus - 1;
                        answeredWrong = true;
                    }
                    RB3.toggle();
                    break;
            }
        }
    };

    /**
     * Validate TextInput for question 1 & 5
     */
    public void ValidateText (View view){
        // Find view and get input
        EditText TextInput = (EditText) findViewById(R.id.BQ1);
        String input = TextInput.getText().toString();
        input.toLowerCase(); // make all lower case

        if (bonusquestnr == 1){
            if (input.contains("sun")){
                showToast(getString(R.string.bQ1_msg_correct));
                TextInput.setText(null);
                bonusquestnr += 1;
            } else{
                showToastTwo(getString(R.string.bQ1_msg_incorrect));
                TextInput.setText(null);
                if (!answeredWrong){
                    resultBonus = resultBonus - 1;
                    answeredWrong = true;
                }
            }
        } else if (bonusquestnr == 5){
            if (input.contains("armstrong")){
                showToast(getString(R.string.bQ5_msg_correct));
                TextInput.setText(null);
                bonusquestnr += 1;
                BonusResults();
            } else{
                showToastTwo(getString(R.string.bQ5_msg_incorrect));
                TextInput.setText(null);
                if (!answeredWrong){
                    resultBonus = resultBonus - 1;
                    answeredWrong = true;
                }
            }
        }
        if (bonusquestnr < 5){
            updateBonusQuestions(bonusquestnr);
        }
        answeredWrong = false;
    }

    /**
     * Validate which checkbox is checked for question 2 & 4
     */
    public void ValidateCheckBox (View view){
        // Get views
        CheckBox boxA = (CheckBox) findViewById(R.id.CheckA);
        CheckBox boxB = (CheckBox) findViewById(R.id.CheckB);
        CheckBox boxC = (CheckBox) findViewById(R.id.CheckC);
        CheckBox boxD = (CheckBox) findViewById(R.id.CheckD);

        // Create Booleans (checkbox checked = true, else false)
        Boolean A = false;
        Boolean B = false;
        Boolean C = false;
        Boolean D = false;

        if (boxA.isChecked()){
            A = true;
        }

        if (boxB.isChecked()){
            B = true;
        }

        if (boxC.isChecked()){
            C = true;
        }

        if (boxD.isChecked()){
            D = true;
        }

        if (bonusquestnr == 2){
            if (A && C){
                showToast(getString(R.string.bQ2_msg_correct));
                boxA.toggle();
                boxC.toggle();
                bonusquestnr += 1;
            } else {
                showToastTwo(getString(R.string.bQ2_msg_incorrect));
                if (!answeredWrong){
                    resultBonus = resultBonus - 1;
                    answeredWrong = true;
                }
            }
        } else if (bonusquestnr == 4){
            if (A && B){
                showToast(getString(R.string.bQ4_msg_correct));
                boxA.toggle();
                boxB.toggle();
                bonusquestnr += 1;
            } else {
                showToastTwo(getString(R.string.bQ4_msg_incorrect));
                if (!answeredWrong){
                    resultBonus = resultBonus - 1;
                    answeredWrong = true;
                }
            }
        }

        updateBonusQuestions(bonusquestnr);
        answeredWrong = false;
    }

    /**
     * Validate which checkbox is checked for question 2 & 4
     */
    public void BonusResults(){
        // Get Views
        Button ETB = (Button) findViewById(R.id.editTextButton);
        TextView resultsMsg = (TextView) findViewById(R.id.resultMsg);
        // Update Views
        ETB.setVisibility(View.INVISIBLE);
        resultsMsg.setText(getString(R.string.bonus_results, resultBonus));
        resultsMsg.setVisibility(View.VISIBLE);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                Button resetButton = (Button) findViewById(R.id.bonus_resetbutton);
                resetButton.setVisibility(View.VISIBLE); //Make Reset Button Visible after some seconds
            }
        }, 4000);
    }
}


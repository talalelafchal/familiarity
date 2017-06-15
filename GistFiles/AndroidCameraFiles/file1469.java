package com.example.android.photoquiz;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class MainActivity extends Activity {
    /**
     * Global Variables
     * totalQuestions - total app questions
     * totalCorrectAnswered = user correct answer
     */
    public int totalQuestions = 5;
    public int totalCorrectAnswered = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
    }

    /**
     * Checks all user answers from app
     */
    private void checkUserAnswers() {
        RadioButton q1 = (RadioButton) findViewById(R.id.film);
        correctAnswerRadio(q1.isChecked());

        RadioButton q2 = (RadioButton) findViewById(R.id.sdcard);
        correctAnswerRadio(q2.isChecked());

        RadioButton q3 = (RadioButton) findViewById(R.id.pixels);
        correctAnswerRadio(q3.isChecked());

        RadioButton q4 = (RadioButton) findViewById(R.id.thirds);
        correctAnswerRadio(q4.isChecked());

        RadioButton q5 = (RadioButton) findViewById(R.id.ppi);
        correctAnswerRadio(q5.isChecked());

    }

    /**
     * @param view app view
     *             onClick function, it shows summary of quiz on screen in toast
     */
    public void submitAnswers(View view) {
        EditText userNameInput = (EditText) findViewById(R.id.user_name);
        String userName = userNameInput.getText().toString();
        checkUserAnswers();
        String summaryMessage;

        if (totalCorrectAnswered == totalQuestions) {
            summaryMessage = "" + userName + " You know all about photography!\n";
            summaryMessage += "Your score is " + totalCorrectAnswered + "/" + totalQuestions + "!";
            showToast(summaryMessage);
            resetScore();
        } else if (totalCorrectAnswered >= 3) {
            summaryMessage = "" + userName + " You still have things to learn!\n";
            summaryMessage += "Your score is " + totalCorrectAnswered + "/" + totalQuestions + "!";
            showToast(summaryMessage);
            resetScore();
        } else if (totalCorrectAnswered < 3) {
            summaryMessage = "" + userName + " You have no idea what hotographi is!\n";
            summaryMessage += "Your score is just " + totalCorrectAnswered + "/" + totalQuestions + " :(";
            showToast(summaryMessage);
            resetScore();
        }
    }

    /**
     * Restart Application
     *
     * @param view app view
     */
    public void resetQuiz(View view) {
        Intent i = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
    }

    /**
     * @param correctAnswer checks answer from a field (RadioButton)
     *                      if true our correct user answers increment
     */
    private void correctAnswerRadio(boolean correctAnswer) {
        if (correctAnswer) {
            totalCorrectAnswered++;
        }
    }

    /**
     * Check multiple choice answers from Checkbox if user didn't select wrong answer and
     * other two are true our correct user answers increment
     *
     * @param firstGoodAnswerCheck  boolean answer if user selected a first good field
     * @param secondGoodAnswerCheck boolean answer if user selected a first good field
     * @param wrongAnswer           boolean answer if user selected a wrong field
     */
    private void correctAnswerCheckBox(boolean firstGoodAnswerCheck, boolean secondGoodAnswerCheck, boolean wrongAnswer) {
        if (wrongAnswer) {
            return;
        } else if (firstGoodAnswerCheck && secondGoodAnswerCheck) {
            totalCorrectAnswered++;
        }
    }

    /**
     * show toast on screen
     *
     * @param toastText CharSequence of message that we want to show on screen
     */
    private void showToast(CharSequence toastText) {
        Toast.makeText(this, toastText, Toast.LENGTH_LONG).show();
    }

    /**
     * reset correct answers after submit score
     */
    private void resetScore() {
        totalCorrectAnswered = 0;
    }
}
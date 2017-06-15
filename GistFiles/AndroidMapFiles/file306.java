package com.example.gpulayerbug;

import android.app.Activity;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class MainActivity extends Activity {

    private ViewGroup container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = (ViewGroup) MainActivity.this.findViewById(R.id.container);

        ((Button) this.findViewById(R.id.show_large_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView largeText = (TextView) MainActivity.this.findViewById(R.id.large_text);
                fadeIn(largeText);
            }
        });

        ((Button) this.findViewById(R.id.show_small_text)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView smallText = (TextView) MainActivity.this.findViewById(R.id.small_text);
                fadeIn(smallText);
            }
        });
    }

    private void fadeIn(View view) {
        Transition transition = new Fade();
        transition.setDuration(1000);
        TransitionManager.beginDelayedTransition(container, transition);

        view.setVisibility(View.VISIBLE);
    }
}

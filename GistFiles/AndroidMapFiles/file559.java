package com.martin.cv3;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by Martin on 04/07/13.
 */
public class TheComputingAndITProject extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.the_computing_and_it_project);

        TextView view = (TextView)findViewById(R.id.projectTextView);
        String formattedText = getString(R.string.the_computing_and_it_project_text);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        Spanned result = Html.fromHtml(formattedText);
        view.setText(result);
    }
}


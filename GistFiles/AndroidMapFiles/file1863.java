package com.martin.cv3;

import android.*;
import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Created by Martin on 30/06/13.
 */
public class AboutMe extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_me);

        TextView view = (TextView)findViewById(R.id.aboutMeTextView);
        String formattedText = getString(R.string.about_me_text);
        view.setMovementMethod(LinkMovementMethod.getInstance());
        Spanned result = Html.fromHtml(formattedText);
        view.setText(result);
    }

}


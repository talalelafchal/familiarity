package com.example.delle4310.wsepinm;

import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ActivityOne extends AppCompatActivity {
    public static int ID = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_one);

        XmlPullParserFactory xmlFactoryObject = null;
        XmlPullParser myParser = null;
        FileInputStream input = null;
        //It is the content useful
        String content = null;

        try {
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myParser = xmlFactoryObject.newPullParser();
            File file = new File(getExternalFilesDir("dir_for_me"), "document.xml");
            if(!file.exists()) {
                Toast.makeText(this, "The content is not available", Toast.LENGTH_LONG).show();
            } else {
                input = new FileInputStream(file);
                myParser.setInput(input, null);
                int event = myParser.getEventType();
                boolean findValue = true;
                //Scan the XML document
                String nameFind = "activity";
                String nameFound = "";
                String attributeFind = "1";
                String attributeFound = "";
                while (event != XmlPullParser.END_DOCUMENT && findValue) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            nameFound = myParser.getName();
                            attributeFound = myParser.getAttributeValue(null, "number");
                            if(attributeFound == null) attributeFound="";
                            if(nameFound.equals(nameFind) && attributeFound.equals(attributeFind)) nameFind = "contentHTML";
                            break;
                        case XmlPullParser.TEXT:
                            //Try to find the version and save it in shared preferences
                            if (nameFound.equals(nameFind)) {
                                content = myParser.getText();
                                findValue = false;
                            }
                            break;
                    }
                    event = myParser.next();
                }
                input.close();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        TextView tv = (TextView)findViewById(R.id.tvOne);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            tv.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv.setText(Html.fromHtml(content));
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());

    }
}

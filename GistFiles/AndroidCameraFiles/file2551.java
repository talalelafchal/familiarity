package com.example.delle4310.wsepinm;

import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class ActivityFour extends AppCompatActivity {
    public static int ID = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_four);

        String content = "The content is not available";
        String name = getIntent().getExtras().getString("name");

        try {
            XmlPullParserFactory xmlFactoryObject = null;
            XmlPullParser myParser = null;
            FileInputStream input = null;

            //It is the content useful
            xmlFactoryObject = XmlPullParserFactory.newInstance();
            myParser = xmlFactoryObject.newPullParser();
            File file = new File(getExternalFilesDir("dir_for_me"), "document.xml");
            if(file.exists()) {
                input = new FileInputStream(file);
                myParser.setInput(input, null);
                int event = myParser.getEventType();
                boolean findValue = true;
                //Scan the XML document
                String nameFind = "activity";
                String nameFound = "";
                String attributeFind = "4";
                String attributeFound = "";
                boolean now = false;
                boolean nextStep = false;
                while (event != XmlPullParser.END_DOCUMENT && findValue) {
                    switch (event) {
                        case XmlPullParser.START_TAG:
                            nameFound = myParser.getName();
                            if(myParser.getAttributeCount()!=0) attributeFound = myParser.getAttributeValue(null, myParser.getAttributeName(0)); //number and name of item
                            else attributeFound = "";
                            if(!now && !nextStep && nameFound.equals(nameFind) && attributeFound.equals(attributeFind)){ nameFind = "item"; attributeFind = name; nextStep = true; break;}
                            if(!now && nextStep && nameFound.equals(nameFind) && attributeFound.equals(attributeFind)){nameFind = "contentHTML"; now = true; nextStep= false; break;}
                            break;
                        case XmlPullParser.END_TAG:
                            nameFound = myParser.getName();
                            if(nextStep && nameFound.equals("activity")) { findValue = false; break; }
                        case XmlPullParser.TEXT:
                            if(now && nameFound.equals(nameFind)) { content = myParser.getText(); nameFound=""; nextStep = true; now = false;}
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

        TextView tv = (TextView)findViewById(R.id.tvFour);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            tv.setText(Html.fromHtml(content, Html.FROM_HTML_MODE_LEGACY));
        } else {
            tv.setText(Html.fromHtml(content));
        }
        tv.setMovementMethod(LinkMovementMethod.getInstance());
    }
}

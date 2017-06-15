package com.example.cameratest;

import android.hardware.Camera;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
{

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.camera_listView);

        //open hardware camera
        Camera camera = Camera.open();
        //create camera parameter from camera object
        Camera.Parameters cameraParameters = camera.getParameters();

        //get supported picture sizes in a List
        List<Camera.Size> cameraPictureSizeList = cameraParameters.getSupportedPictureSizes();

        ArrayList<String> cameraPictureSizeArr = new ArrayList<>();

        for (int i = 0; i < cameraPictureSizeList.size(); i++)
        {
            String str = cameraPictureSizeList.get(i).width+"x"+cameraPictureSizeList.get(i).height;
            cameraPictureSizeArr.add(str);
        }


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, cameraPictureSizeArr);

        listView.setAdapter(arrayAdapter);
    }
}
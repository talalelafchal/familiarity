package com.example.jenny.myapplication;

import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;

public class AndroidApplication extends AppController {

    private ObjectGraph objectGraph;

    @Override public void onCreate() {
        super.onCreate();
        objectGraph = ObjectGraph.create(getModules().toArray());
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(
                new AndroidModule(this),
                new SimpleModule());
    }

    public void inject(Object object) {
        objectGraph.inject(object);
    }
}
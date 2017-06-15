package com.example.jenny.myapplication;

import android.content.Context;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library=true)
public class AndroidModule {
    private AndroidApplication application;

    public AndroidModule(AndroidApplication app) {
        this.application = app;
    }

    @Provides
    @Singleton
    @ForApplication
    public Context provideApplicationContext() {
        return application;
    }
}

package com.example.jenny.myapplication;

import com.example.jenny.myapplication.client.MainActivity;
import com.example.jenny.myapplication.client.PhotoViewActivity;
import com.example.jenny.myapplication.service.FlickrService;
import com.example.jenny.myapplication.service.FlickrServiceImpl;
import com.example.jenny.myapplication.service.ImageService;
import com.example.jenny.myapplication.service.ImageServiceImpl;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(library=true,
    injects = {MainActivity.class,
            PhotoViewActivity.class
    })
public class SimpleModule {

    @Provides
    @Singleton
    public FlickrService provideFlickrService(FlickrServiceImpl service) {
        return service;
    }

    @Provides
    @Singleton
    public ImageService provideImageService(ImageServiceImpl service) {
        return service;
    }

}

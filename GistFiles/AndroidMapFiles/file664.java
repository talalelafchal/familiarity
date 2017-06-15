package com.example.summer.newapp;
public class PskovClass {

    private String namePskovMos;
    private int imagePskovId;


    public static final PskovClass[] pskovObl = {
            new PskovClass(" водохранилище",R.drawable.flat_17),
            new PskovClass(" водохранилище",R.drawable.reka_moscow),
            new PskovClass(" водохранилище", R.drawable.reka_moscow),
            new PskovClass(" водохранилище", R.drawable.ozernickoe),
            new PskovClass(" водохранилище", R.drawable.ozernickoe),
            new PskovClass(" водохранилище", R.drawable.ozernickoe),
            new PskovClass(" водохранилище", R.drawable.ozernickoe),
            new PskovClass(" водохранилище", R.drawable.ozernickoe),

    };

    private PskovClass(String nameOblMos, int imageId) {
        this.namePskovMos = nameOblMos;
        this.imagePskovId = imageId;
    }

    public String getName() {
        return namePskovMos;
    }

    public int getImageResourceId() {

        return imagePskovId;
    }
}



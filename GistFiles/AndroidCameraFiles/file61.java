package com.example.summer.newapp;

/**
 * Created by Summer on 04.12.2016.
 */

class MosOblClass {

    private String nameOblMos;
    private int imageId;
  // private String coordinator;
    private String latitube;
    private String longitube;

   // LatLng mesto;

    static final MosOblClass[] mosObl = {
            new MosOblClass("Рузское водохранилище", "56.051622", "37.310075", R.drawable.flat_19),
            new MosOblClass("Можайское водохранилище","33.051622", "27.310075",R.drawable.flat_12),
           /* new MosOblClass("Озеро Морозово", 56.051622, 37.310075, R.drawable.flat_13),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_10),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_11),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_12),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_13),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_14),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_15),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_17),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_18),
            new MosOblClass("Другое водохранилище", 56.051622, 37.310075, R.drawable.flat_19),
*/
    };


    private MosOblClass(String nameOblMos,String latitube,String longitube, int imageId) {
        this.nameOblMos = nameOblMos;
        this.imageId = imageId;
        this.latitube = latitube;
        this.longitube = longitube;

    }

    public String getName() {
        return nameOblMos;
    }


    public String getLatitube() {
        return latitube;
    }


    public String getLongitube() {return longitube;}


    public int getImageResourceId() {return imageId;}
}

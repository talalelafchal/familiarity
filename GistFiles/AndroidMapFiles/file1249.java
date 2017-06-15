package com.example.summer.newapp;

/**
 * Created by Summer on 04.12.2016.
 */

class MosOblClass {

    private String nameOblMos;
    private int imageId;
  // private String coordinator;
    private String geo;


//массив мест московской области,координаты,изображения
    static final MosOblClass[] mosObl = {
            new MosOblClass("Рузское водохранилище", "geo:56.051622,37.310075", R.drawable.flat_19),
            new MosOblClass("Можайское водохранилище","geo:56.051622,37.310075",R.drawable.flat_12),
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

    //имя ,фото,координаты
         private MosOblClass(String nameOblMos,String geo, int imageId) {
        this.nameOblMos = nameOblMos;
        this.imageId = imageId;
        this.geo = geo;


    }

    public String getName() {
        return nameOblMos;
    }


    public String getGeo() {
        return geo;
    }


   // public String getLongitube() {return longitube;}


    public int getImageResourceId() {return imageId;}
}

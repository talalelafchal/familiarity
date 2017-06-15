package com.example.summer.newapp;

/**
 * Created by Summer on 01.12.2016.
 */

 public class RussianState {
    private String name;
    private int imageResourceId;


    public static final RussianState[] city = {
            new RussianState("Московская область",R.drawable.flat_10),
            new RussianState("Псковская область",R.drawable.flat_11),
            new RussianState("Ярославская область", R.drawable.flat_12),
            new RussianState("Калужская область", R.drawable.flat_13),
            new RussianState("Тверская область", R.drawable.flat_14),
            new RussianState("Скоро будет еще", R.drawable.flat_15),
            new RussianState("Скоро будет еще", R.drawable.flat_11),
            new RussianState("Скоро будет еще", R.drawable.flat_12),
            new RussianState("Скоро будет еще", R.drawable.flat_13),
            new RussianState("Скоро будет еще", R.drawable.flat_14),

    };

    private RussianState(String name, int imageResourceId) {
        this.name = name;
        this.imageResourceId = imageResourceId;
    }

    public String getName() {
        return name;
    }

    public int getImageResourceId() {
        return imageResourceId;
    }
}









package com.example.summer.newapp;

/**
 * Created by Summer on 25.12.2016.
 */

public class Game {


        private String nameGame;
        private int imageGameId;


        public static final Game[] games = {
                new Game("Охота на кабана",R.drawable.krasnyi_kaban),
                new Game("000",R.drawable.krasnyi_kaban),
                new Game("Охота на кабана",R.drawable.krasnyi_kaban),


        };

        private Game(String nameGame, int imageGameId) {
            this.nameGame = nameGame;
            this.imageGameId = imageGameId;

        }

        public String getName() {
            return nameGame;
        }



        public int getImageResourceId() {

            return imageGameId;
        }
    }


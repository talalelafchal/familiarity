package com.example.miguel.hechos_curiosos;

import android.content.Context;
import android.content.res.Resources;

import java.util.Random;

/**
 * Created by Miguel on 03/12/2014.
 */
public class FactBook {
    /**
     * Method to get a random sentence
     * @return String
     */
    public String getRandomFact(Context cont) {
        // Declare our View variables
        // Declare our View variables and assign the Views from the layout file
        String prediction;
        Random randomGenerator = new Random();

        Resources res;
        String[] hechos = cont.getResources().getStringArray(
                R.array.frases);
        int randomNumber = randomGenerator.nextInt(hechos.length);
        prediction = hechos[randomNumber];

        return prediction;
    }
}


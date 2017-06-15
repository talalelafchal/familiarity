package com.example.Posten2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Daniel
 * Date: 10.08.13
 * Time: 01:33
 * To change this template use File | Settings | File Templates.
 */
public class HenteFelt extends Activity {
    String returnString;

    public String avsenderLand(JSONObject object) {

        try {
            JSONArray consignmentSet = object.getJSONArray("consignmentSet");
            JSONObject object1 = consignmentSet.getJSONObject(0);
            JSONArray packageSet = object1.getJSONArray("packageSet");
            JSONObject object2 = packageSet.getJSONObject(0);
            JSONArray eventSet = object2.getJSONArray("eventSet");
            Integer countEvents = eventSet.length();
            countEvents = countEvents - 1;
            JSONObject object3 = eventSet.getJSONObject(countEvents);
            String country = object3.getString("country");

            returnString = " country";

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    public String lesSisteStatus(JSONObject object) {
        String ID = "";

        try {
            JSONArray consignmentSet = object.getJSONArray("consignmentSet");
            JSONObject object1 = consignmentSet.getJSONObject(0);
            JSONArray packageSet = object1.getJSONArray("packageSet");
            JSONObject object2 = packageSet.getJSONObject(0); //object inni packageSet
            JSONArray eventSet = object2.getJSONArray("eventSet");
            JSONObject object3 = eventSet.getJSONObject(0);
            ID = "Siste status er : " + object3.getString("description") + "\n";

            if (ID.contentEquals("Siste status er : Sendingen er utlevert\n")) {
                ID = "Du har hentet pakken !";

            }
        } catch (Exception ex) {
            Toast.makeText(getApplicationContext(), "Vennligst bruk et gyldig sporingsnummer", Toast.LENGTH_SHORT).show();
        }
        return ID;
    }

    public String sentDato(JSONObject object) {

        try {
            JSONArray consignmentSet = object.getJSONArray("consignmentSet");
            JSONObject object1 = consignmentSet.getJSONObject(0);
            JSONArray packageSet = object1.getJSONArray("packageSet");
            JSONObject object2 = packageSet.getJSONObject(0);
            JSONArray eventSet = object2.getJSONArray("eventSet");
            Integer countEvents = eventSet.length();
            countEvents = countEvents - 1;
            JSONObject object3 = eventSet.getJSONObject(countEvents);
            object3.getString("displayDate");
            returnString = "Datoen varen ble sendt er : " + object3.getString("displayDate") + "\n";

        } catch (Exception e) {
            e.printStackTrace();
        }
        return returnString;
    }

    public String antallEventer(JSONObject object) {
        try {
            JSONArray consignmentSet = object.getJSONArray("consignmentSet");
            JSONObject object1 = consignmentSet.getJSONObject(0);
            JSONArray packageSet = object1.getJSONArray("packageSet");
            JSONObject object2 = packageSet.getJSONObject(0);
            JSONArray eventSet = object2.getJSONArray("eventSet");
            Integer countEvents = eventSet.length();
            returnString = "Antall hendelser er : " + countEvents.toString() + "\n";


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnString;

    }

    public ArrayList<String> samleStatuser(JSONObject object) {

        Integer countEvents = 0;
        String buffer = "";
        String dato = "";
        String tid = "";

        ArrayList<String> arrayList = new ArrayList<String>();


        try {
            JSONArray consignmentSet = object.getJSONArray("consignmentSet");
            JSONObject object1 = consignmentSet.getJSONObject(0);
            JSONArray packageSet = object1.getJSONArray("packageSet");
            JSONObject object2 = packageSet.getJSONObject(0);
            JSONArray eventSet = object2.getJSONArray("eventSet");
            countEvents = eventSet.length();

        } catch (Exception e) {
            e.printStackTrace();
        }


        Integer i;
        Integer objekt;

        if (countEvents == 0) {


            i = 0;
            try {

                JSONArray consignmentSet = object.getJSONArray("consignmentSet");
                JSONObject object1 = consignmentSet.getJSONObject(0);
                JSONArray packageSet = object1.getJSONArray("packageSet");
                JSONObject object2 = packageSet.getJSONObject(0);
                JSONArray eventSet = object2.getJSONArray("eventSet");
                JSONObject object3 = eventSet.getJSONObject(i);
                buffer = object3.getString("description");
                dato = object3.getString("displayTime");
                tid = object3.getString("displayDate");
                returnString += dato + "kl : " + tid + " Status : " + buffer;


            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        ArrayList<String> stringArrayList = new ArrayList<String>();
        for (i = countEvents; i > 0; i--) {
            try {
                objekt = i - 1;
                JSONArray consignmentSet = object.getJSONArray("consignmentSet");
                JSONObject object1 = consignmentSet.getJSONObject(0);
                JSONArray packageSet = object1.getJSONArray("packageSet");
                JSONObject object2 = packageSet.getJSONObject(0);
                JSONArray eventSet = object2.getJSONArray("eventSet");
                JSONObject object3 = eventSet.getJSONObject(objekt);
                buffer = object3.getString("description");
                dato = object3.getString("displayTime");
                tid = object3.getString("displayDate");
                String returnArray = "";
                returnArray += dato + "kl : " + tid + " Status : " + buffer + "\n";
                returnString += dato + "kl : " + tid + " Status : " + buffer + "\n";


                //ArrayList<String> stringArrayList = new ArrayList<String>() ;

                stringArrayList.add(returnArray);

                //fillArrayList(returnArray)

            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return stringArrayList;

    }

    public String hentetDatoTid(JSONObject object) {
        try {
            JSONArray consignmentSet = object.getJSONArray("consignmentSet");
            JSONObject object1 = consignmentSet.getJSONObject(0);
            JSONArray packageSet = object1.getJSONArray("packageSet");
            JSONObject object2 = packageSet.getJSONObject(0);
            JSONArray eventSet = object2.getJSONArray("eventSet");
            JSONObject object3 = eventSet.getJSONObject(0);
            String dato = object3.getString("displayDate");
            String tid = object3.getString("displayTime");
            String sted = object3.getString("city");

            returnString = "Klokka : " + tid + " " + "\n" + "Den " + dato + " " + "\n" + "På " + sted;


        } catch (JSONException e) {
            e.printStackTrace();
        }
        return returnString;
    }

    public boolean sjekkSporingsNummer(JSONObject object) {
        Boolean riktigSporing = null;

        try {
            JSONArray consignmentSet = object.getJSONArray("consignmentSet");
            JSONObject object1 = consignmentSet.getJSONObject(0);
            JSONObject object2 = object1.getJSONObject("error");
            String s = object2.getString("message");
            riktigSporing = false;


        } catch (Exception e) {
            e.printStackTrace();
            riktigSporing = true;


        }
        return riktigSporing;


    }



}

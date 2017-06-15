package com.example.samyakandroid.plivosamyak;

import android.os.Bundle;
import APIConnect.APIConnectPlivo;

import android.os.AsyncTask;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnClickListener;
import android.widget.Toast;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends Activity {

    private Button callButton;
    private EditText inputNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        inputNumber = (EditText) findViewById(R.id.userPhone);
        callButton = (Button) findViewById(R.id.makeCall);
        int count =0;

        callButton.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String from = inputNumber.getText().toString();
                SQLiteDatabase mydatabase = openOrCreateDatabase("wdigital", MODE_PRIVATE, null);

                //mydatabase.execSQL("DROP TABLE IF EXISTS transactions");

                mydatabase.execSQL("CREATE TABLE IF NOT EXISTS transactions (" +
                        "id INT PRIMARY KEY NOT NULL, " +
                        "sp INT NOT NULL, " +
                        "ep INT NOT NULL, " +
                        "timestamp TEXT, " +
                        "age INT, " +
                        "gender INT, " +
                        "token TEXT" +
                        ");");
                //mydatabase.execSQL("INSERT INTO transactions VALUES('1','admin', 'admin', 'tstamp', 20, 0, 'trialanderror');");
                //mydatabase.execSQL("INSERT INTO transactions VALUES (2, 1, 2, \"works\", 20, 0, 20");

                mydatabase.execSQL("INSERT INTO transactions (id, sp, ep, token) VALUES (1, 2, 5, 'trialanderror')");

                Cursor resultSet = mydatabase.rawQuery("SELECT * from transactions", null);
                resultSet.moveToFirst();
                Log.v("vikas", "New Data: "+generateDatatoWrite("age=15&g=10&token=1001"));


                Log.v("vikas", resultSet.getString(2));
                checkToken("trialanderror");
                Log.v("vikas", "Generated Token: "+generateToken());



                Toast.makeText(getApplicationContext(), "pressed call button",
                        Toast.LENGTH_SHORT).show();
                new Call().execute(new String[]{from});
            }
        });


    }

    public boolean checkToken(String token){
        /*
        If token found in db return true else return false

        for now, just search for the token in the db and if found return 1
         */
        SQLiteDatabase mydatabase = openOrCreateDatabase("transactions", MODE_PRIVATE, null);

        Cursor resultSet = mydatabase.rawQuery("SELECT * FROM transactions WHERE token='"+token+"'", null);
        resultSet.moveToFirst();

        if(resultSet.getString(5)!=null)
            Log.v("vikas", "Result set: "+resultSet.getString(5));
        else{
            Log.v("vikas", "No result found");
        }
        return true;
    }


    public static final String md5(final String s) {
        final String MD5 = "MD5";
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance(MD5);
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2)
                    h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void makeTable(){
        SQLiteDatabase mydatabase = openOrCreateDatabase("wdigital", MODE_PRIVATE, null);
        mydatabase.execSQL("CREATE TABLE IF NOT EXISTS transactions (" +
                "id INT PRIMARY KEY NOT NULL, " +
                "sp INT NOT NULL, " +
                "ep INT NOT NULL, " +
                "timestamp TEXT, " +
                "age INT, " +
                "gender INT, " +
                "token TEXT" +
                ");");
        mydatabase.close();
    }



    public static final String generateToken(){
        //A simple method to generate token based on some serial
        /*
        for now generete a random number and put that in db
         */
        int minimum=30000, maximum=100000;

        Integer randomNum = minimum + (int)(Math.random()*maximum);

        return md5(randomNum.toString());
    }

    public static final String generateDatatoWrite(String DatafromCard){
        //read contents as it is from card - use Samyak's code here
        //just change one parameter
        String string = DatafromCard;
        String output = "";

        String[] parts = string.split("&");
        for (int j=0; j<parts.length;j++){
            String[] part1 = parts[j].split("=");
            //part1[0], part1[1] - key , value
            if(!part1[0].equals("token")){
                output+=part1[0]+"="+part1[1]+"&";
            }
        }
        output+="token="+generateToken();

        return output;
    }

    class Call extends AsyncTask<String, Void, Boolean> {

        @Override
        protected Boolean doInBackground(String... params) {
            Log.v("vikas", "async here");
            try {
                return APIConnectPlivo.executePostCall(params[0]);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            Toast.makeText(getApplicationContext(),"working",
                    Toast.LENGTH_SHORT).show();
            return true;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
        }
    }
}


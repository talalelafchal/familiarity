package com.example.Posten2;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.bugsense.trace.BugSenseHandler;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class Posten extends Activity {
    /**
     * Called when the activity is first created.
     */

    EditText et_sporingsnummer = null;
    String ID = "";
    ListView lst_packages = null;
    String sporingsnummer = "";
    ProgressDialog pd;
    private List<String> fileList = new ArrayList<String>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BugSenseHandler.initAndStartSession(Posten.this, "dc52dc34");
        setContentView(R.layout.main);

        lst_packages = (ListView) findViewById(R.id.lst_packackeNumbers);
        Button button = (Button) findViewById(R.id.button);

        et_sporingsnummer = (EditText) findViewById(R.id.editText);
        et_sporingsnummer.setText("TESTPACKAGE-AT-PICKUPPOINT");

        pd = ProgressDialog.show(Posten.this, "Laster...", "Laster ned data fra posten", true, false, null);
        pd.dismiss();


        File f = new File("data/data/com.example.Posten2/files");

        ListDir(f);

        //int fileListSize = 0;
        for (int i = 1; i < fileList.size(); i++) {
            String[] input = {fileList.get(i)};

            new GetData().execute(input);

        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String[] input = {String.valueOf(et_sporingsnummer.getText())};
                sporingsnummer = et_sporingsnummer.getText().toString();
                new GetData().execute(input);
                File s = new File("data/data/com.example.Posten2/files");
                ListDir(s);
                //pd.show();



            }
        });

        lst_packages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /* String[] input = {String.valueOf(fileList.get(position))};
                sporingsnummer = String.valueOf(fileList.get(position));
                new GetData().execute(input);
                pd.show();     */
                finnInfo(fileList.get(position));
            }
        });

        lst_packages.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {


                String filePos = fileList.get(position);

                File f = new File("/data/data/com.example.Posten2/files/" + filePos + ".json");
                f.delete();

                File s = new File("data/data/com.example.Posten2/files");

                ListDir(s);
                return true;
            }
        });


    }

    public void writeFile(String sporingsnummer, String json) {

        FileOutputStream oFile;
        String sFileName = sporingsnummer + ".json";
        try {

            oFile = openFileOutput(sFileName, Context.MODE_PRIVATE);
            oFile.write(json.getBytes());
            oFile.close();
        } catch (Exception ex) {
        }
    }

    public void finnInfo(String sporingsnummer) {
        InputStream oFile;
        String sentDato = null;
        String lesSisteStatus = null;
        String antallEventer = null;
        String avsenderLand = null;
        String hentetDatoTid = null;

        ArrayList<String> samleStatuser = new ArrayList<String>();
        try {
            oFile = openFileInput(sporingsnummer + ".json");
            InputStreamReader oReader = new InputStreamReader(oFile);
            BufferedReader oBuffer = new BufferedReader(oReader);
            String sText = "";
            String sEachLine = "";

            while ((sEachLine = oBuffer.readLine()) != null) {
                sText += sEachLine;
            }
            oReader.close();
            JSONObject object = new JSONObject(sText);
            //Sjekk om pakken er registrert eller ikke


            HenteFelt henteFelt = new HenteFelt();
            Boolean riktigSporing = henteFelt.sjekkSporingsNummer(object);
            if (riktigSporing == false) {

                Toast.makeText(getApplicationContext(), "Sporingsnummer er feil", Toast.LENGTH_SHORT).show();
                // TODO finn en måte å informere bruker om at sporingsnummeret er feil, skal bruker få lov til å beholde det ?
            } else if (riktigSporing == true) {
                lesSisteStatus = henteFelt.lesSisteStatus(object);
                antallEventer = henteFelt.antallEventer(object);
                avsenderLand = henteFelt.avsenderLand(object);
                samleStatuser = henteFelt.samleStatuser(object);



            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        et_sporingsnummer.setText("");

        Intent intent = new Intent(Posten.this, DetailView.class);
        intent.putExtra("sporingsnummer", sporingsnummer);
        intent.putExtra("lesSisteStatus", lesSisteStatus);
        intent.putExtra("antallEventer", antallEventer);
        intent.putExtra("avsenderLand", avsenderLand);
        intent.putStringArrayListExtra("array", samleStatuser);
        startActivity(intent);

        File f = new File("data/data/com.example.Posten2/files");
        ListDir(f);
        pd.dismiss();
    }

    void ListDir(File f) {
        File[] files = f.listFiles();
        fileList.clear();
        for (File file : files) {
            String s1 = file.getPath().replace("data/data/com.example.Posten2/files/", "");
            String s2 = s1.substring(0, s1.length() - 5);
            fileList.add(s2);
        }
        ArrayAdapter<String> directoryList
                = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, fileList);

        lst_packages.setAdapter(directoryList);
    }

    void startGetData(String... input) {

        new GetData().execute(input);

    }

    class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... data) {


            String URL = "http://sporing.bring.no/sporing.json?q=" + data[0];
            String jsonReturnText = "";

            try {
                HttpParams httpParameters = new BasicHttpParams();
                int timeoutConnection = 10000; // 10 second timeout for connecting to site
                HttpConnectionParams.setConnectionTimeout(httpParameters, timeoutConnection);
                int timeoutSocket = 30000; // 30 second timeout for obtaining results
                HttpConnectionParams.setSoTimeout(httpParameters, timeoutSocket);
                HttpClient httpclient = new DefaultHttpClient(httpParameters);
                HttpGet httpget = new HttpGet(URL);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity r_entity = response.getEntity();
                jsonReturnText = EntityUtils.toString(r_entity);
            } catch (Exception e) {


            }
            return jsonReturnText;
        }

        protected void onPostExecute(String result) {

            try {
                writeFile(sporingsnummer, result);
                Log.d("Get Data successfully", sporingsnummer);
                Log.d("JSON tekst", result);
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Vennligst sjekk at du har internett og prøv igjen senere.", Toast.LENGTH_LONG).show();
                pd.dismiss();
                Log.d("Get Data Failed", sporingsnummer);
                Log.d("JSON tekst", result);
            }


        }

    }


}




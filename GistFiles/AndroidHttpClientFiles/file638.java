package com.example.QCM;

import android.util.Log;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Sidd8 on 30/01/14.
 */
public class ConnexionBDD
{
    private ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    private String login;
    private String password;

    public ConnexionBDD(String login, String password)
    {
        this.login = login;
        this.password = password;
    }

    //useless pour le moment
    protected boolean setConnection(String url)
    {
        InputStream is = null;
        this.nameValuePairs.add(new BasicNameValuePair("login", this.login));
        this.nameValuePairs.add(new BasicNameValuePair("pass", this.password));
        String result = null;

        Log.i("tagNV",this.login + " " + this.password);
        Log.i("tagURL",url);
        // Envoi de la commande http
        try
        {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();

            is = entity.getContent();

            //conversion de la réponse en chaine de caractère
            try
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(is,"UTF-8"));

                StringBuilder sb  = new StringBuilder();

                String line = null;

                while ((line = reader.readLine()) != null)
                {
                    sb.append(line + "\n");
                }

                is.close();

                result = sb.toString();
                if(result.length() > 0)
                {
                    return false;
                }
            }
            catch(Exception e)
            {
                Log.i("tagconvertstr", "" + e.toString());
            }

            Log.i("set_connection", "ok !");
            return true;
        }
        catch(Exception e)
        {
            Log.e("log_tag", "Error in http connection " + e.toString());
            return false;
        }
    }
}

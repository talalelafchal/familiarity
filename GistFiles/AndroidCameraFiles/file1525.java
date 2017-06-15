package com.example.tudelft.moodpizza.Selfie;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.example.tudelft.dekikkert.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by tudelft on 20/09/2016.
 */
public class SelfieListAdapter extends ArrayAdapter<SelfieItem> {
    private ArrayList<SelfieItem> data;

    public SelfieListAdapter(Context context, ArrayList<SelfieItem> data) {
        super(context, R.layout.selfie_layout, data);
        this.data = data;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.selfie_layout, parent, false);
        ViewGroup.LayoutParams params = rowView.getLayoutParams();
        new DownloadImageTask((ImageView) rowView.findViewById(R.id.selfie_display)).//,
                execute("http://smulders.techmaus.nl/kikkertapp/public/selfies/" + data.get(position).getId() + ".jpg");

        return rowView;
    }

    private void vindIkVo(int id) {
        class vindIkVoTask extends AsyncTask<Integer, Void, Void> {

            HttpURLConnection connection;
            DataOutputStream outputStream = null;
            InputStream inputStream = null;

            @Override
            protected Void doInBackground(Integer... ids) {

                if (ids[0] == null)
                    return null;

                int id = ids[0];

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                stream.write(3);
                InputStream data = new ByteArrayInputStream(stream.toByteArray()); // convert ByteArrayOutputStream to ByteArrayInputStream

                try {
                    URL url = new URL("http://smulders.techmaus.nl/kikkertapp/public/api/selfies");
                    connection = (HttpURLConnection) url.openConnection();

                    connection.setDoInput(true);
                    connection.setDoOutput(true);
                    connection.setUseCaches(false);

                    connection.setRequestMethod("PUT");
                    connection.setRequestProperty("Connection", "Keep-Alive");
                    connection.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");

                    outputStream = new DataOutputStream(connection.getOutputStream());

                    inputStream = connection.getInputStream();
                    String result = convertStreamToString(inputStream);

                    System.out.println("res: "+result);
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

        }
        new vindIkVoTask().execute(id);
    }

    private String convertStreamToString(InputStream is) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    public ArrayList<SelfieItem> getSelfies() {
        ArrayList<SelfieItem> selfieItems = new ArrayList<>();
        String s = "";

        try {
            URL ur = new URL("http://smulders.techmaus.nl/kikkertapp/public/api/selfies");
            HttpURLConnection yc = (HttpURLConnection) ur.openConnection();
            BufferedReader in = new BufferedReader(new InputStreamReader(yc.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {

                s += inputLine;

            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (!s.equals("")) {
            try {
                JSONArray array = new JSONArray(s);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject o = array.getJSONObject(i);
                    SelfieItem item = new SelfieItem(o.getInt("id"), o.getInt("vindikvos"));
                    selfieItems.add(item);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return selfieItems;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        ImageView bmImage;

        public DownloadImageTask(ImageView bmImage) {

            this.bmImage = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            bmImage.setImageBitmap(result);
        }
    }

}


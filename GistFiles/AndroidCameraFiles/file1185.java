package net.theamusementpark.healthycar;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;

public class ReadJSON
{

    protected AssetManager manager;
    protected JSONObject object;
    protected JSONArray array;
    protected InputStream stream;
    protected String jsonData = null;
    protected String[] stringArray;
    final protected String fileName = "cars.json";

    public ReadJSON(Context myContext)
    {
        this.manager = myContext.getAssets();
    }

    private String readFromAsset()
    {
        try
        {
            stream = manager.open(fileName);
            int size = stream.available();

            byte[] buffer = new byte[size];
            stream.read(buffer);
            stream.close();

            jsonData = new String(buffer, "UTF-8");
        }
        catch (IOException ie)
        {
            ie.printStackTrace();
        }

        return jsonData;
    }

    public void loadData()
    {
        try
        {
            object = new JSONObject(readFromAsset());
            array = object.getJSONArray("cars");

            int size = array.length();

            stringArray = new String[array.length()];

            for(int i = 0; i < size; i++)
            {
                JSONObject inside = array.getJSONObject(i);
                stringArray[i] = inside.toString();
            }
        }
        catch (JSONException ex)
        {
            ex.printStackTrace();
        }
    }
}
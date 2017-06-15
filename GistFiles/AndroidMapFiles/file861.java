package masxdeveloper.peternakan.Adapter;

import com.google.android.gms.maps.model.LatLng;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by JonesRandom  on 02/03/2017.
 * https://masx-dev.blogspot.co.id
 */

public class GoogleDirectionRequest {

    public String requestDirectionData(LatLng from, LatLng to) {

        HttpUrl.Builder urlBuider = HttpUrl.parse("http://maps.googleapis.com/maps/api/directions/json?").newBuilder();
        urlBuider.addQueryParameter("origin", from.latitude + "," + from.longitude);
        urlBuider.addQueryParameter("destination", to.latitude + "," + to.longitude);
        urlBuider.addQueryParameter("sensor", "false");
        urlBuider.addQueryParameter("mode", "driving");

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(urlBuider.build().toString())
                .build();

        InputStream is = null;
        try {
            Response response = client.newCall(request).execute();
            is = response.body().byteStream();

            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }

            return stringBuilder.toString();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return "Data";
    }
}

import com.esri.core.geometry.Envelope;
import com.esri.core.geometry.Point;
import com.esri.core.geometry.SpatialReference;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class AGSMapboxLayer extends com.esri.android.map.TiledServiceLayer {

    private int responseCode;
    private String message;

    private String response;

    private final String mapboxAPI = "https://b.tiles.mapbox.com/v4/";
    private String mapID;
    private String mapboxAccessToken;

    private int mLevels = 21;

    public AGSMapboxLayer(String mapID, String mapboxAccessToken, int numLevels) {
        super("");
        this.mapID = mapID;
        this.mapboxAccessToken = mapboxAccessToken;
        this.mLevels = numLevels;

        Envelope envWeb = new Envelope(-20037508.342787, -20037508.342787, 20037508.342787, 20037508.342787);

        Point origin = envWeb.getUpperLeft();

        double[] resolution = new double[mLevels];
        double[] scale = new double[mLevels];
        for (int i = 0; i < mLevels; i++) {
            resolution[i] = 156543.032 / Math.pow(2, i);
            scale[i] = 554678932 / Math.pow(2, i);
        }

        TileInfo ti = new TileInfo(origin, scale, resolution, mLevels, 192, 256, 256);

        this.setTileInfo(ti);
        this.setFullExtent(envWeb);
        this.setDefaultSpatialReference(SpatialReference.create(102100));
        this.setInitialExtent(envWeb);

        this.initLayer();
    }

    @Override
    protected byte[] getTile(int i, int i2, int i3) throws Exception {

        String response = null;

        String url = mapboxAPI + mapID + "/" + i + "/" + i2 + "/"+ i3 +"@2x.png?access_token=" + mapboxAccessToken;

        HttpGet request = new HttpGet(url);

        return executeRequest(request);
    }

    private byte[] executeRequest(HttpUriRequest request)
    {
        HttpClient client = new DefaultHttpClient();

        HttpResponse httpResponse;

        try {
            httpResponse = client.execute(request);
            responseCode = httpResponse.getStatusLine().getStatusCode();
            message = httpResponse.getStatusLine().getReasonPhrase();

            HttpEntity entity = httpResponse.getEntity();

            if (entity != null) {

                InputStream instream = entity.getContent();
                ByteArrayOutputStream buffer = new ByteArrayOutputStream();

                int nRead;
                byte[] data = new byte[16384];

                while ((nRead = instream.read(data, 0, data.length)) != -1) {
                    buffer.write(data, 0, nRead);
                }

                buffer.flush();
                instream.close();
                return buffer.toByteArray();
            }

        } catch (ClientProtocolException e)  {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        } catch (IOException e) {
            client.getConnectionManager().shutdown();
            e.printStackTrace();
        }
        return null;
    }

}

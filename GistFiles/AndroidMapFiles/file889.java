package asia.idoo.trygmap2;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.*;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import static android.graphics.Bitmap.Config.ARGB_8888;

public class MainActivity extends Activity {
    private GoogleMap map;
    private int current = 0;
    private Marker marker;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        map = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
        map.setMyLocationEnabled(true);
    }

    public void btnMyLocationClick(View v) {
        Location location = map.getMyLocation();
        Toast.makeText(this, location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
    }

    public void btnNewMarkerClick(View v) {
        Location location = map.getMyLocation();

        if (marker != null) {
            marker.remove();
        }
        current++;
        String text = String.valueOf(current);
        Bitmap bitmap = makeBitmap(this, text);
        MarkerOptions markerOptions = new MarkerOptions()
                .position(new LatLng(location.getLatitude(), location.getLongitude()))
                .draggable(true)
                .icon(BitmapDescriptorFactory.fromBitmap(bitmap));

        marker = map.addMarker(markerOptions);
    }

    public Bitmap makeBitmap(Context context, String text)
    {
        Resources resources = context.getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = BitmapFactory.decodeResource(resources, R.drawable.marker);
        bitmap = bitmap.copy(ARGB_8888, true);

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.RED); // Text color
        paint.setTextSize(14 * scale); // Text size
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // Text shadow
        Rect bounds = new Rect();
        paint.getTextBounds(text, 0, text.length(), bounds);

        int x = bitmap.getWidth() - bounds.width() - 10; // 10 for padding from right
        int y = bounds.height();
        canvas.drawText(text, x, y, paint);

        return  bitmap;
    }
}

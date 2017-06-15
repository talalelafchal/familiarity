package your.app.package;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.model.Marker;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Callback;

// Note: Picasso setup -> http://square.github.io/picasso/

@SuppressWarnings("all")
class CustomInfoWindow implements InfoWindowAdapter
{
    private final View myContentsView;
    private int placeImgId;
    private Resources resources;
    private Context context;

    CustomInfoWindow(LayoutInflater inflater, int imgId, Resources resources, Context context)
    {
        myContentsView = inflater.inflate(R.layout.business_info_window, null);
        placeImgId = imgId;
        this.resources = resources;
        this.context = context;
    }

    @Override
    public View getInfoContents(Marker marker)
    {
        MyPlace place = (MyPlace) marker.getTag();
        String placeImageUrl = place.getPlaceImageUrl();
        String placeName = place.getPlaceName();
        String placeAddress = place.getPlaceAddress();

        ImageView placeImgView = (ImageView) myContentsView.findViewById(R.id.placeImage);
        TextView placeNameTxtView = (TextView) myContentsView.findViewById(R.id.placeName);
        TextView placeAddressTxtView = (TextView) myContentsView.findViewById(R.id.reviewCount);

        placeNameTxtView.setText(placeName);
        placeAddressTxtView.setText(placeAddress);

        if (placeImageUrl.equals(""))
        {
            Drawable drawable = resources.getDrawableForDensity(placeImgId, DisplayMetrics.DENSITY_LOW, null);
            placeImgView.setImageDrawable(drawable);
        }
        else
        {
            Picasso.with(context).load(placeImageUrl).placeholder(placeImgId).error(placeImgId)
                    .resize(R.dimen.info_window_image_size, R.dimen.info_window_image_size)
                    .onlyScaleDown().centerCrop().into(placeImgView, new MarkerCallback(marker));
        }

        return myContentsView;
    }

    @Override
    public View getInfoWindow(Marker marker)
    {
        return null;
    }
    
    // Callback is an interface from Picasso:
    static class MarkerCallback implements Callback
    {
        Marker marker = null;

        MarkerCallback(Marker marker)
        {
            this.marker = marker;
        }

        @Override
        public void onError() {}

        @Override
        public void onSuccess()
        {
            if (marker == null)
            {
                return;
            }

            if (!marker.isInfoWindowShown())
            {
                return;
            }
            
            // If Info Window is showing, then refresh it's contents:

            marker.hideInfoWindow(); // Calling only showInfoWindow() throws an error
            marker.showInfoWindow();
        }
    }
}

package software.is.com.myapplication;

import android.content.Context;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;


public class DrawerHeaderView extends FrameLayout {
    PrefManager prefManager;
    Uri uri;
    String picture;
    public DrawerHeaderView(Context context) {
        super(context);
        setup();
        prefManager = IcrmApp.getPrefManager();
        picture = prefManager.picture().getOr("");
        Log.e("ddddd",picture);
        ImageView user_image = (ImageView) findViewById(R.id.user_image);
        uri = Uri.parse(picture);
        user_image.setImageURI(uri);

        Picasso.with(context)
                .load(uri)
                .centerCrop()
                .resize(200, 200)
                .transform(new RoundedTransformation(100, 4))
                .into(user_image);
    }

    public DrawerHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setup();
    }

    private void setup() {
        inflate(getContext(), R.layout.navigation_header, this);
    }
}

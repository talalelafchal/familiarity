package yv.recipe.fragments;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import yv.recipe.R;
import yv.recipe.utils.CameraUtils;

public class TabRecipes extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.tab_recipes,container,false);

        ImageView chefIcon = (ImageView) v.findViewById(R.id.analyze_icon);
        final ImageView viewImage = (ImageView) v.findViewById(R.id.image_placeholder);


        chefIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Analyzing photo...", Toast.LENGTH_LONG).show();


//                ImageView analyzeIcon = (ImageView) v.findViewById(R.id.analyze_icon);

               analyzePhoto(viewImage);
            }
        });

        return v;
    }



    public void analyzePhoto(ImageView viewImage) {
        Drawable imgDrawable = viewImage.getDrawable();
        Bitmap bitmap = ((BitmapDrawable)imgDrawable).getBitmap();

//                Bitmap bitmap = BitmapFactory.decodeResource(viewImage.getResources(), R.id.image_placeholder);
        CameraUtils.getInstance().analyzeImage(bitmap, viewImage);
    }

}
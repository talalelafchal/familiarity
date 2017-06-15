package co.malm.android.utils.gms.maps.model;

import android.graphics.Color;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.BitmapDescriptor;

/**
 * Custom {@link com.google.android.gms.maps.model.BitmapDescriptorFactory} Used to create a definition of an image,
 * using HEX color strings as parameter, used for marker icons and ground overlays.
 *
 * @see com.google.android.gms.maps.model.BitmapDescriptorFactory
 * @author marlonlom
 */
public final class CustomBitmapDescriptorFactory extends Object {
    
    /**
     * Using HEX color string, parses the color and return its HUE component.
     * <br/>
     * Note: Used for marker icons and ground overlays.
     *
     * @param colorString hex color as string
     * @return hsv components for color
     */
    private static float[] getHsvFromColor(String colorString) {
        float[] hsv = new float[3];
        int _color = Color.parseColor(colorString);
        Color.colorToHSV(_color, hsv);
        return hsv;
    }

    /**
     * Creates a bitmap descriptor that refers to a colorization of HEX color string.
     * 
     * @param colorString hex color as string
     * @return the BitmapDescriptor that was loaded using colorString or null if failed to obtain.
     */
    public static BitmapDescriptor fromColorString(String colorString) {
        return BitmapDescriptorFactory.defaultMarker(CustomBitmapDescriptorFactory.getHsvFromColor(colorString)[0]);
    }
}
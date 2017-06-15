public class GoogleMapsUtil {

    private final String TAG = GoogleMapsUtil.class.getSimpleName();

    /**
     * Transforma un drawable local en un bitmap compatible con google maps
     * se utiliza para transformar los pins de localizacion del mapa
     *
     * @param id referencia al drawable
     * @param activity referencia de la actividad donde se manda llamar
     * @return Un objecto Bitmap
     */
    public static BitmapDescriptor getBitmapDescriptor(int id, Activity activity) {
        int sdk = android.os.Build.VERSION.SDK_INT;

        Drawable vectorDrawable = activity.getResources().getDrawable(id);

        int h = ScreenUtils.convertDIPToPixels(activity, 24);
        int w = ScreenUtils.convertDIPToPixels(activity, 18);

        assert vectorDrawable != null;
        vectorDrawable.setBounds(0, 0, w, h);
        Bitmap bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bm);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bm);
    }

    /**
     * Hace zoom out o zoom in al mapa de google de acuerdo un array list de marks para que se puedan
     * visualizar todos los pins en la pantalla
     *
     * @param data Arraylist de markers
     * @param activity referencia de la actividad donde se manda llamar
     * @param map Objecto de mapa donde se va a realizar el zoom
     */
    public static void zoomOutWithMark(ArrayList<Marker> data, Activity activity, GoogleMap map) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        if (data.size() >= 2) {
            for (Marker mark : data) {
                builder.include(mark.getPosition());
            }
            LatLngBounds bounds = builder.build();
            int padding = ScreenUtils.convertDIPToPixels(activity, 128);
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

            map.moveCamera(cu);
            map.animateCamera(cu);
        }
    }

}

    
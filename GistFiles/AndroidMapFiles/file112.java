// Compile against nutiteq-3dlib-preview.jar or drop into the HelloMap3D sample

import javax.microedition.khronos.opengles.GL10;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.nutiteq.MapView;
import com.nutiteq.components.Bounds;
import com.nutiteq.components.Components;
import com.nutiteq.components.ImmutableMapPos;
import com.nutiteq.components.MapPos;
import com.nutiteq.components.TileQuadTreeNode;
import com.nutiteq.geometry.VectorElement;
import com.nutiteq.layers.Layer;
import com.nutiteq.log.Log;
import com.nutiteq.projections.Projection;
import com.nutiteq.rasterlayers.RasterLayer;
import com.nutiteq.tasks.NetFetchTileTask;
import com.nutiteq.ui.MapListener;

public class BasicMapActivity extends Activity {

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main);

    Log.enableAll();
    Log.setTag("swissmap");

    // Set up MapView basics
    MapView mapView = (MapView) findViewById(R.id.mapView);
    mapView.setComponents(new Components());
    mapView.getOptions().setKineticPanning(true);
    mapView.getOptions().setDoubleClickZoomIn(true);
    mapView.getConstraints().setRotatable(false);
    mapView.getOptions().setMapListener(new MapClickListener(this));

    // Add a Swiss Grid layer, focused on Zurich
    Layer baseLayer = new SwissGridLayer(42);
    mapView.getLayers().setBaseLayer(baseLayer);

    // Focus on Zurich.. either of these should work
    mapView.setFocusPoint(mapView.getLayers().getBaseLayer().getProjection().fromWgs84(8.55f, 47.366667f));
    mapView.setFocusPoint(683946f, 246796f); // Swiss Grid coordinates for Zurich

    // Zoom in not too far
    mapView.setZoom(18f);

    // Go!
    mapView.startMapping();
  }

  /** Raster layer using the EPSG:21781 projection and Swiss tile server. */
  private static class SwissGridLayer extends RasterLayer {

    private static final int MIN_ZOOM_LEVEL = 14;
    private static final int MAX_ZOOM_LEVEL = 25;

    private static final String URL_BASE = "http://swisstiles.orr.me.uk/21781/";

    /** Number of tiles in the X direction, for zoom levels 14..25. */
    private static final int[] TILES_X = { 3, 4, 8, 19, 38, 94, 188, 375, 750, 938, 1250, 1875 };

    /** Number of tiles in the Y direction, for zoom levels 14..25. */
    private static final int[] TILES_Y = { 2, 3, 5, 13, 25, 65, 125, 250, 500, 625, 834, 1250 };

    protected SwissGridLayer(int id) {
      super(new SwissGrid(), MIN_ZOOM_LEVEL, MAX_ZOOM_LEVEL, id, URL_BASE);
    }

    /**
     * @param zoom Current zoom level.
     * @return The width of the world in tiles, for the given zoom level.
     */
    private static final int getXTileCountForZoom(int zoom) {
      return TILES_X[zoom - MIN_ZOOM_LEVEL];
    }

    /**
     * @param zoom Current zoom level.
     * @return The height of the world in tiles, for the given zoom level.
     */
    private static final int getYTileCountForZoom(int zoom) {
      return TILES_Y[zoom - MIN_ZOOM_LEVEL];
    }

    public void fetchTile(TileQuadTreeNode tile) {
      System.out.println(String.format("fetchTile() X: %d, Y: %d, Zoom: %d", tile.x, tile.y, tile.zoom));

      final int zoom = tile.zoom;
      final double tileCountForZoomLevel = 1 << zoom;
      int x = (int) ((tile.x / tileCountForZoomLevel) * getXTileCountForZoom(zoom));
      int y = (int) ((tile.y / tileCountForZoomLevel) * getYTileCountForZoom(zoom));

      String url = String.format("%s%d/%d/%d.jpeg", URL_BASE, zoom, y, x);
      System.out.println(String.format("SwissGridLayer: Loading tile %s", url));

      this.components.rasterTaskPool.execute(new NetFetchTileTask(tile,
          this.components, this.tileIdOffset, url));
    }

    @Override
    public void flush() {
      // ?
    }

  }

  /** Implements the EPSG:21781 projection: the Swiss Grid. */
  private static class SwissGrid extends Projection {

    /*
     * The "Projection" class only supports certain named PROJ4 projections, therefore we pass
     * in a projection name that works, but then ignore it and manually override the WGS
     * conversion methods.
     */
    private static final String PROJ4_NAME = "merc";

    /* Bounds for the tile set being used, in Swiss Grid metres. */
    private static final Bounds BOUNDS = new Bounds(420000, 350000, 900000, 30000);

    public SwissGrid() {
      super(PROJ4_NAME, BOUNDS);
    }

    @Override
    public ImmutableMapPos fromWgs84(float lon, float lat) {
      // Convert WGS84 to Swiss grid coordinates (metres)
      int north = (int) WGStoCHx(lat, lon);
      int east = (int) WGStoCHy(lat, lon);
      System.out.println(String.format("Input, Lat: %.6f, Lon: %.6f", lat, lon));
      System.out.println(String.format("Output, North: %d, East: %d", north, east));

      return new ImmutableMapPos(east, north);
    }

    @Override
    public MapPos toWgs84(float x, float y) {
      // Currently unused
      return null;
    }

    @Override
    public float[] internalTransformationMatrix(float x, float y, float z) {
      throw new RuntimeException("Unused?");
    }

    // Below are conversion methods provided by swisstopo:
    // http://www.swisstopo.admin.ch/internet/swisstopo/en/home/products/software/products/skripts.html

    // Convert WGS lat/long (° dec) to CH x
    private static double WGStoCHx(double lat, double lng) {
      // Converts degrees dec to sex
      lat = DecToSexAngle(lat);
      lng = DecToSexAngle(lng);

      // Converts degrees to seconds (sex)
      lat = SexAngleToSeconds(lat);
      lng = SexAngleToSeconds(lng);

      // Axiliary values (% Bern)
      double lat_aux = (lat - 169028.66) / 10000;
      double lng_aux = (lng - 26782.5) / 10000;

      // Process X
      double x = ((200147.07 + (308807.95 * lat_aux)
          + (3745.25 * Math.pow(lng_aux, 2)) + (76.63 * Math.pow(lat_aux,
          2))) - (194.56 * Math.pow(lng_aux, 2) * lat_aux))
          + (119.79 * Math.pow(lat_aux, 3));

      return x;
    }

    // Convert WGS lat/long (° dec) to CH y
    private static double WGStoCHy(double lat, double lng) {
      // Converts degrees dec to sex
      lat = DecToSexAngle(lat);
      lng = DecToSexAngle(lng);

      // Converts degrees to seconds (sex)
      lat = SexAngleToSeconds(lat);
      lng = SexAngleToSeconds(lng);

      // Axiliary values (% Bern)
      double lat_aux = (lat - 169028.66) / 10000;
      double lng_aux = (lng - 26782.5) / 10000;

      // Process Y
      double y = (600072.37 + (211455.93 * lng_aux))
          - (10938.51 * lng_aux * lat_aux)
          - (0.36 * lng_aux * Math.pow(lat_aux, 2))
          - (44.54 * Math.pow(lng_aux, 3));

      return y;
    }

    // Convert decimal angle (degrees) to sexagesimal angle (degrees, minutes
    // and seconds dd.mmss,ss)
    public static double DecToSexAngle(double dec) {
      int deg = (int) Math.floor(dec);
      int min = (int) Math.floor((dec - deg) * 60);
      double sec = (((dec - deg) * 60) - min) * 60;

      // Output: dd.mmss(,)ss
      return deg + ((double) min / 100) + (sec / 10000);
    }

    // Convert sexagesimal angle (degrees, minutes and seconds dd.mmss,ss) to
    // seconds
    public static double SexAngleToSeconds(double dms) {
      double deg = 0, min = 0, sec = 0;
      deg = Math.floor(dms);
      min = Math.floor((dms - deg) * 100);
      sec = (((dms - deg) * 100) - min) * 100;

      // Result in degrees sex (dd.mmss)
      return sec + (min * 60) + (deg * 3600);
    }

  }

  /** Simply shows a toast with X and Y projection coordinates clicked. */
  private static class MapClickListener extends MapListener {

    final Activity activity;

    MapClickListener(Activity context) {
      this.activity = context;
    }

    @Override
    public void onMapClicked(final float x, final float y, final boolean longClick) {
      activity.runOnUiThread(new Runnable() {
        public void run() {
          Toast.makeText(activity, "onMapClicked " + x + " " + y, 0).show();
        }
      });
    }

    @Override
    public void onDrawFrameAfter3D(GL10 arg0, float arg1) {
      // If only there was a MapListenerAdapter class, we wouldn't need
      // these empty methods!
    }

    @Override
    public void onDrawFrameBefore3D(GL10 arg0, float arg1) {
      //
    }

    @Override
    public void onLabelClicked(VectorElement arg0, boolean arg1) {
      //
    }

    @Override
    public void onMapMoved() {
      //
    }

    @Override
    public void onSurfaceChanged(GL10 arg0, int arg1, int arg2) {
      //
    }

    @Override
    public void onVectorElementClicked(VectorElement arg0, boolean arg1) {
      //
    }

  }

}

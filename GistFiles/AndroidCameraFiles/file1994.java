

import android.graphics.Color;
import android.location.Location;
import android.os.Handler;
import android.os.SystemClock;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import java.util.LinkedList;
import java.util.List;

public class MapAnimator implements Runnable {

  private static final int ANIMATE_SPEED = 800;
  private static final int ANIMATE_SPEED_TURN = 1000;
  private static final int BEARING_OFFSET = 90;

  private final Interpolator interpolator = new LinearInterpolator();

  private final Handler mHandler = new Handler();
  private int currentIndex = 0;
  private boolean showPolyline = false;
  private long start = SystemClock.uptimeMillis();
  private Marker trackingMarker, mainMarker;
  private GoogleMap googleMap;
  private LinkedList<LatLng> latLngs;
  private PolylineOptions rectOptions = new PolylineOptions();
  private Polyline polyLine;
  private AnimationListener animationListener;

  public MapAnimator(GoogleMap googleMap, LinkedList<LatLng> latLngs, Marker mainMarker) {
    this.googleMap = googleMap;
    this.latLngs = latLngs;
    this.mainMarker = mainMarker;
  }

  private void reset() {
    start = SystemClock.uptimeMillis();
    currentIndex = 0;
  }

  private void stop() {
    mHandler.removeMessages(0);
    mHandler.removeCallbacks(this);
  }

  private void initialize(boolean showPolyLine) {
    reset();
    this.showPolyline = showPolyLine;

    if (showPolyLine) {
      initializePolyLine();
    }

    // We first need to put the camera in the correct position for the first run (we need 2 latLngs for this).....
    LatLng markerPos = latLngs.get(0);
    LatLng secondPos = latLngs.get(1);

    setupCameraPositionForMovement(markerPos, secondPos);
  }

  private void setupCameraPositionForMovement(LatLng markerPos, LatLng secondPos) {

    float bearing = bearingBetweenLatLngs(markerPos, secondPos);

    if (trackingMarker != null) trackingMarker.remove();
    trackingMarker = googleMap.addMarker(
        new MarkerOptions().position(markerPos));

    CameraPosition cameraPosition =
        new CameraPosition.Builder()
            .target(markerPos)
            .bearing(bearing + BEARING_OFFSET)
            .tilt(90)
            .zoom(googleMap.getCameraPosition().zoom >= 16 ? googleMap.getCameraPosition().zoom
                : 16)
            .build();

    googleMap.animateCamera(
        CameraUpdateFactory.newCameraPosition(cameraPosition),
        ANIMATE_SPEED_TURN,
        new GoogleMap.CancelableCallback() {

          @Override
          public void onFinish() {
            System.out.println("finished camera");
            reset();
            Handler handler = new Handler();
            handler.post(MapAnimator.this);
          }

          @Override
          public void onCancel() {
            System.out.println("cancelling camera");
          }
        }
    );
  }

  private Polyline initializePolyLine() {
    rectOptions.add(latLngs.get(0));
    rectOptions.width(8);
    rectOptions.color(Color.RED);
    return polyLine = googleMap.addPolyline(rectOptions);
  }

  /**
   * Add the marker to the polyline.
   */
  private void updatePolyLine(LatLng latLng) {
    List<LatLng> points = polyLine.getPoints();
    points.add(latLng);
    polyLine.setPoints(points);
  }

  public void stopAnimation() {
    stop();
  }

  public void startAnimation(boolean showPolyLine) {
    if (latLngs.size() > 2) {
      mainMarker.setVisible(false);
      mHandler.sendEmptyMessage(0);
      initialize(showPolyLine);
    }
  }

  @Override
  public void run() {
    long elapsed = SystemClock.uptimeMillis() - start;
    double t = interpolator.getInterpolation((float) elapsed / ANIMATE_SPEED);

    LatLng endLatLng = getEndLatLng();
    LatLng beginLatLng = getBeginLatLng();

    double lat = t * endLatLng.latitude + (1 - t) * beginLatLng.latitude;
    double lng = t * endLatLng.longitude + (1 - t) * beginLatLng.longitude;
    LatLng newPosition = new LatLng(lat, lng);

    trackingMarker.setPosition(newPosition);
    mainMarker = trackingMarker;

    if (showPolyline) {
      if (animationListener != null) {
        if (polyLine != null) animationListener.onStart(polyLine);
      }
      updatePolyLine(newPosition);
    }

    // It's not possible to move the marker + center it through a cameraposition update while another camerapostioning was already happening.
    //navigateToPoint(newPosition,tilt,bearing,currentZoom,false);
    //navigateToPoint(newPosition,false);

    if (t < 1) {
      mHandler.postDelayed(this, 16);
    } else {
      System.out.println(
          "Move to next marker.... current = " + currentIndex + " and size = " + latLngs.size()
      );
      // imagine 5 elements -  0|1|2|3|4 currentindex must be smaller than 4
      if (currentIndex < latLngs.size() - 2) {
        currentIndex++;

        start = SystemClock.uptimeMillis();

        LatLng begin = getBeginLatLng();
        LatLng end = getEndLatLng();

        float bearingL = bearingBetweenLatLngs(begin, end);

        CameraPosition cameraPosition =
            new CameraPosition.Builder()
                .target(end) // changed this...
                .bearing(bearingL + BEARING_OFFSET)
                .tilt(90)
                .zoom(googleMap.getCameraPosition().zoom)
                .build();

        googleMap.animateCamera(
            CameraUpdateFactory.newCameraPosition(cameraPosition),
            ANIMATE_SPEED_TURN,
            null
        );

        start = SystemClock.uptimeMillis();
        mHandler.postDelayed(this, 16);
      } else {
        currentIndex++;
        stopAnimation();
      }
    }
  }

  private LatLng getEndLatLng() {
    return latLngs.get(currentIndex + 1);
  }

  private LatLng getBeginLatLng() {
    return latLngs.get(currentIndex);
  }

  private float bearingBetweenLatLngs(LatLng begin, LatLng end) {
    Location beginL = convertLatLngToLocation(begin);
    Location endL = convertLatLngToLocation(end);

    return beginL.bearingTo(endL);
  }

  private Location convertLatLngToLocation(LatLng latLng) {
    Location loc = new Location("");
    loc.setLatitude(latLng.latitude);
    loc.setLongitude(latLng.longitude);
    return loc;
  }

  public boolean isRunning() {
    return mHandler.hasMessages(0);
  }

  public void setOnAnimationEndListener(AnimationListener animationListener) {
    this.animationListener = animationListener;
  }

  public interface AnimationListener {

    void onStart(Polyline polyline);
  }
}
package com.nutiteq.app.listeners;

import javax.microedition.khronos.opengles.GL10;

import org.json.JSONObject;

import android.app.Activity;
import android.view.View;
import android.webkit.WebView;

import com.nutiteq.R;
import com.nutiteq.geometry.VectorElement;
import com.nutiteq.log.Log;
import com.nutiteq.ui.DefaultLabel;
import com.nutiteq.ui.MapListener;

public class LabelOverlayListener extends MapListener {
    private Activity mapActivity;
    private WebView webView;

    public LabelOverlayListener(Activity mapActivity) {
        this.mapActivity = mapActivity;
        initWebView(mapActivity);
    }

    @Override
    public void onMapMoved() {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
    }

    @Override
    public void onDrawFrameBefore3D(GL10 gl, float zoomPow2) {
    }

    @Override
    public void onDrawFrameAfter3D(GL10 gl, float zoomPow2) {
       
    }

    @Override
    public void onMapClicked(float x, float y, boolean longClick) {
       Log.debug("onMapClicked " + x+" "+y+ " "+longClick);
       mapActivity.runOnUiThread(new Runnable() {
           public void run() {
               webView.setVisibility(View.INVISIBLE);
           }
       });
    }

    @Override
    public void onVectorElementClicked(VectorElement vectorElement,
            boolean longClick) {
        if(vectorElement.getLabel() != null){
          Log.debug("onVectorElementClicked " + ((DefaultLabel) vectorElement.getLabel()).getTitle() + " "+longClick);
        }
        // show overlay on object click
        showPopupView(vectorElement);
    }

    @Override
    public void onLabelClicked(VectorElement vectorElement, boolean longClick) {
        Log.debug("onLabelClicked " + ((DefaultLabel) vectorElement.getLabel()).getTitle() + " "+longClick);
        // show overlay on label click also
        showPopupView(vectorElement);
    }

    private void showPopupView(VectorElement vectorElement) {
        webView.setVisibility(View.VISIBLE);
        JSONObject data = (JSONObject) vectorElement.userData;
        StringBuffer html = new StringBuffer();
        if(vectorElement.getLabel() != null){
            html.append("<b>"+((DefaultLabel) vectorElement.getLabel()).getTitle()+"</b>");
        }
        html.append("<br/>");
        html.append("<img src='" + data.optString("thumbnailImg") + "'/><br/>");
        html.append(data.optString("summary") + "<br/>");
        html.append("<a href='http://" + data.optString("wikipediaUrl") + "'>Full article...</a>");
        Log.debug(html.toString());
        webView.loadData(html.toString(), "text/html", "UTF-8");
    }

    public void initWebView(Activity mapActivity2){
        Log.debug("initWebView");

        this.webView = (WebView) mapActivity2.findViewById(R.id.webView1);
        webView.setVisibility(View.INVISIBLE);

    }
}

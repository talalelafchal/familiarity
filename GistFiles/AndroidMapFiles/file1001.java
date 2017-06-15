package com.nclab.CoMonTracker;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import com.nclab.CoMonSimple.DebugLog;
import com.nclab.CoMonSimple.R;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.LinearLayout;

public class SessionGraphActivity extends Activity {
    /** Called when the activity is first created. */
    private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    List<double[]> values = new ArrayList<double[]>();
    private GraphicalView mChartView;
    private TimeSeries time_series;
    
    private TreeMap<Long, Boolean> gpsList;

    // chart container
    private LinearLayout layout;
    
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        String session = getIntent().getStringExtra("session");
        setContentView(R.layout.chart);
        layout = (LinearLayout) findViewById(R.id.chart_area);

        mDataset = new XYMultipleSeriesDataset();
        mRenderer = new XYMultipleSeriesRenderer();
        mRenderer.setAxisTitleTextSize(16);
        mRenderer.setChartTitleTextSize(20);
        mRenderer.setLabelsTextSize(15);
        mRenderer.setLegendTextSize(15);
        mRenderer.setPointSize(3f);

        XYSeriesRenderer r = new XYSeriesRenderer();
        r.setColor(Color.GREEN);
        r.setPointStyle(PointStyle.CIRCLE);
        r.setFillPoints(true);
        
        mRenderer.addSeriesRenderer(r);
        mRenderer.setClickEnabled(true);
        mRenderer.setSelectableBuffer(20);
        mRenderer.setPanEnabled(true);
        mRenderer.setYAxisMin(0);
        mRenderer.setYAxisMax(100);

        time_series = new TimeSeries("Battery");
        fillData(session);
        mDataset.addSeries(time_series);

        mChartView = ChartFactory.getTimeChartView(this, mDataset, mRenderer,
                "H:mm:ss");

        layout.addView(mChartView);
    }

    private void fillData(String sessionNumber) {
        File mf = Environment.getExternalStorageDirectory();
        File batteryFile = new File(mf.getAbsolutePath() + "/CoMonSimple/Sessions/Battery/session_" + sessionNumber + "_bat.txt");
        //File batteryFile = new File(mf.getAbsolutePath() + "/CoMonSimple/batexample.txt");
        
        try {
            FileInputStream fis = new FileInputStream(batteryFile);
            byte[] data = new byte[fis.available()];
            while (fis.read(data) != -1) {;}
            fis.close();
            String strData = new String(data);
            String[] lines = strData.split("\n");
            for (String line: lines) {
                String[] entry = line.trim().split(",");
                time_series.add(new Date(Long.parseLong(entry[0])), Integer.parseInt(entry[3]));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
package tw.soleil.indoorlocation.activity;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import tw.soleil.indoorlocation.InDoorDemo;
import tw.soleil.indoorlocation.R;
import tw.soleil.indoorlocation.object.IndoorObject;
import tw.soleil.indoorlocation.object.ScanRecord;
import tw.soleil.indoorlocation.service.BLEScannerService;
import tw.soleil.indoorlocation.service.BLEService;
import tw.soleil.indoorlocation.util.LocationCalculator;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        this.testData();
    }

    private void testData() {

        LocationCalculator locationCalculator = new LocationCalculator();

        // Add 1st spot
        ScanRecord firstRecord = new ScanRecord();
        firstRecord.setUUID("fake");
        firstRecord.setMajor(1);
        firstRecord.setMinor(1);

        IndoorObject firstSpot = new IndoorObject("A", firstRecord);
        firstSpot.setPosition(new double[]{0, 0});
        firstSpot.setRelativeDistance(3);

        // Add 2nd spot
        ScanRecord secondRecord = new ScanRecord();
        secondRecord.setUUID("fake");
        secondRecord.setMajor(1);
        secondRecord.setMinor(2);

        IndoorObject secondSpot = new IndoorObject("B", secondRecord);
        secondSpot.setPosition(new double[]{0, 4});
        secondSpot.setRelativeDistance(5);

        // Add 3rd spot
        ScanRecord thirdRecord = new ScanRecord();
        thirdRecord.setUUID("fake");
        thirdRecord.setMajor(1);
        thirdRecord.setMinor(3);

        IndoorObject thirdSpot = new IndoorObject("C", thirdRecord);
        thirdSpot.setPosition(new double[]{3, 4});
        thirdSpot.setRelativeDistance(4);

        // Add 4th spot
        ScanRecord forthRecord = new ScanRecord();
        forthRecord.setUUID("fake");
        forthRecord.setMajor(1);
        forthRecord.setMinor(4);

        IndoorObject fourthSpot = new IndoorObject("D", forthRecord);
        fourthSpot.setPosition(new double[]{10, 4});
        fourthSpot.setRelativeDistance(8);

        // Add 4th spot
        ScanRecord fifthRecord = new ScanRecord();
        fifthRecord.setUUID("fake");
        fifthRecord.setMajor(1);
        fifthRecord.setMinor(5);

        IndoorObject fifthSpot = new IndoorObject("E", fifthRecord);
        fifthSpot.setPosition(new double[]{3, 0});
        fifthSpot.setRelativeDistance(0);

        // Make position at 3rd spot
        locationCalculator.getPositions().add(firstSpot);
        locationCalculator.getPositions().add(secondSpot);
        locationCalculator.getPositions().add(thirdSpot);
        locationCalculator.getPositions().add(fourthSpot);
        locationCalculator.getPositions().add(fifthSpot);

        double[] userPosition = locationCalculator.calculateCentroid();
        Log.d(InDoorDemo.TAG, "User Position expect {3, 0} and actual {" + userPosition[0] + ", " + userPosition[1] + "}");
    }
}

package com.example.samsung.gps_coordinate_determiner.presentation.presenter.main;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.support.test.runner.AndroidJUnit4;
import android.test.AndroidTestCase;
import android.test.mock.MockContext;

import com.example.samsung.gps_coordinate_determiner.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Created by
 *
 * @autor Vladimir Bobkov
 * on 29.05.2017.
 */

@SuppressWarnings("deprecation")
public class MainPresenterTest extends AndroidTestCase {
    Context mCxt;

    @RunWith(AndroidJUnit4.class)
    public void onClickBtn() throws Exception {

    }

    @Test
    public void getStringLocation() throws Exception {

    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();

        mCxt = new MockContext();

        setContext(mCxt);

        assertNotNull(mCxt);
    }

    @Test
    public void getStringLocationTest() {

        String assertValue = mCxt.getString(R.string.given_accuracy_is_impossible);

        Location[] locations = {new Location(LocationManager.GPS_PROVIDER),
                new Location(LocationManager.GPS_PROVIDER),
                new Location(LocationManager.GPS_PROVIDER)};

        locations[0].setLatitude(5000);
        locations[0].setLongitude(5000);
        locations[0].setAccuracy(13.5f);

        locations[1].setLatitude(15000);
        locations[1].setLongitude(15000);
        locations[1].setAccuracy(60.5f);

        Map<Location, String> values = new HashMap<>();

        for (Location location :
                locations) {

            if (location == null) {
                assertValue = mCxt.getString(R.string.location_is_not_defined);

            } else if (location.getAccuracy() < 60) {
                assertValue = mCxt.getString(R.string.position_coordinates) + " "
                        + mCxt.getString(R.string.lat) + " = " + location.getLatitude() + ", "
                        + mCxt.getString(R.string.lon) + " = " + location.getLongitude() + ", "
                        + mCxt.getString(R.string.accuracy) + " = " + location.getAccuracy();

            } else if (location.getAccuracy() >= 60) {
                assertValue = mCxt.getString(R.string.given_accuracy_is_impossible);
            }
            values.put(location, assertValue);
        }

        MainPresenter mMainPresenter = new MainPresenter();

        for (Map.Entry<Location, String> value :
                values.entrySet()) {
            Assert.assertEquals(value.getValue(), mMainPresenter.getStringLocation(value.getKey()));
        }

    }

}
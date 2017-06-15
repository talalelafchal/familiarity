package ch.dotpay.ui.settings.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;

import ch.dotpay.R;
import ch.dotpay.databinding.FragmentSettingsSelectAddressBinding;
import ch.dotpay.network.model.my.channel.Business;
import ch.dotpay.network.model.my.office.Office;
import ch.dotpay.utils.PermissionUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by gorodechnyj on 08.04.2016.
 */
public class SelectAddressFragment extends BusinessSettingsFragment<FragmentSettingsSelectAddressBinding>
        implements OnMapReadyCallback,
        GoogleMap.OnMapClickListener,
        ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 101;
    private static final LatLng DEFAULT_LOCATION = new LatLng(55.753942, 37.620725); // Moscow, red square
    private static final int DEFAULT_ZOOM = 13;
    private GoogleMap googleMap;
    private Geocoder geocoder;
    private Office businessOffice;
    private Business businessState;

    public static SelectAddressFragment newInstance() {
        return new SelectAddressFragment();
    }

    public SelectAddressFragment() {
        super(FragmentSettingsSelectAddressBinding.class);
    }

    @Override
    public String getTitle() {
        return getString(R.string.settings_select_address);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        binding.map.onCreate(savedInstanceState);
        geocoder = new Geocoder(getActivity());
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.map.getMapAsync(this);
        businessState = loadBusinessState();
        if (businessState != null
                && businessState.offices != null
                && businessState.offices.size() > 0) {
            businessOffice = businessState.offices.get(0);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.settings, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_ok) {
            // TODO: simplify
            if (businessState != null) {
                if (businessState.offices != null) {
                    if (businessState.offices.size() > 0) {
                        Long officeId = businessState.offices.get(0).id;
                        businessState.offices.remove(0);
                        businessOffice.id = officeId;
                        businessState.offices.add(0, businessOffice);
                    } else {
                        businessState.offices.add(0, businessOffice);
                    }
                } else {
                    businessState.offices = new ArrayList<>();
                    businessState.offices.add(0, businessOffice);
                }
            }
            saveBusinessState(businessState);
            getFragmentManager().popBackStack();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        binding.map.onResume();
    }

    @Override
    public void onPause() {
        binding.map.onPause();
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.map.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        binding.map.onSaveInstanceState(outState);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        binding.map.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setOnMapClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            enableMyLocation();
        } else {
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        }
        if (businessOffice != null) {
            this.googleMap.addMarker(new MarkerOptions()
                    .title(businessOffice.address)
                    .position(new LatLng(businessOffice.latitude, businessOffice.longitude)));
        }
    }

    @Override
    public void onMapClick(LatLng latLng) {
        this.googleMap.clear();
        businessOffice = null;

        try {
            Observable.just(geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1))
                    .subscribeOn(Schedulers.newThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .flatMapIterable(addresses -> addresses)
                    .map(Office::new)
                    .subscribe((office) -> {
                        office.businessId = businessState != null ? businessState.id : null;
                        if (businessOffice != null) {
                            office.id = businessOffice.id;
                        }

                        businessOffice = office;
                        this.googleMap.addMarker(new MarkerOptions()
                                .title(office.address)
                                .position(latLng)).showInfoWindow();
                    }, Throwable::printStackTrace);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission(getBaseActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (this.googleMap != null) {
            // Access to the location has been granted to the app.
            this.googleMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Display the missing permission error dialog when the fragments resume.
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, DEFAULT_ZOOM));
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true)
                .show(getFragmentManager(), "dialog");
    }


}

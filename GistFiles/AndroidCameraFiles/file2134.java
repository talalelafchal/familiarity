package br.com.metasix.cabal.ui.fragment;

import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.metasix.cabal.R;
import br.com.metasix.cabal.adapter.PlacesAdapter;
import br.com.metasix.cabal.app.App;
import br.com.metasix.cabal.callback.Callback;
import br.com.metasix.cabal.callback.CallbackDialogSelect;
import br.com.metasix.cabal.client.PlaceClient;
import br.com.metasix.cabal.dto.NextMeRequestDTO;
import br.com.metasix.cabal.entity.Category;
import br.com.metasix.cabal.entity.Place;
import br.com.metasix.cabal.enums.CardType;
import br.com.metasix.cabal.enums.StatusEnum;
import br.com.metasix.cabal.task.SearchAddressAsyncTask;
import br.com.metasix.cabal.ui.activity.DrawerLayoutMain;
import br.com.metasix.cabal.ui.activity.MapActivity;
import br.com.metasix.cabal.ui.component.CustomInfoWindow;
import br.com.metasix.cabal.ui.component.MyFloatButton;
import br.com.metasix.cabal.ui.dialog.DialogCategory;
import br.com.metasix.cabal.ui.dialog.DialogFilterPlace;
import br.com.metasix.cabal.ui.helper.UIHelperPlaces;
import br.com.metasix.cabal.util.DeviceUtils;
import br.com.metasix.cabal.util.DistanceUtil;
import br.com.metasix.cabal.util.KeyboardUtils;
import br.com.metasix.cabal.util.MapUtils;
import br.com.metasix.cabal.util.TextUtils;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by renan on 5/4/15.
 */
public class PlacesFragment extends Fragment {

    private static final int MAX_RESULT_TEXT = 1;
    public static final int NAME_ITEM = R.string.places_fragment;

    public static final int ICON_ITEM = R.drawable.ic_places_menu;
    private static final int FIRST_ADDRESS = 1;
    private static final int SEARCHING = 1;
    private static final int STOPED = 0;
    private static final int TAB_MAP = 1;
    private static final int TAB_LIST = 2;
    private View view;

    private UIHelperPlaces ui;
    private App app;
    private LatLng lastUpdate;
    private SupportMapFragment mapFragment;
    private Map<Marker, Place> markerPlaceMap;
    private CardType cardType;
    private Category categorySelected;
    private List<Category> categories;
    private List<Place> places;
    private boolean isOpenKmButtons = false;
    private int distanceKm = 1;
    private double DISTANCE_UPDATE = 1000 * ((double) distanceKm);
    private PlacesAdapter adapter;
    private BitmapDescriptor drawable;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_places, container, false);
        init();
        setEvents();
        setHasOptionsMenu(true);
        setValues();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setMap();
    }

    private void init() {
        app = (App) getActivity().getApplication();
        ui = new UIHelperPlaces(view);
        markerPlaceMap = new HashMap<>();
        mapFragment = SupportMapFragment.newInstance();
        getExtras();
        getChildFragmentManager().beginTransaction().add(R.id.fragment_places_map, mapFragment).commit();
    }

    private void setMap() {
        ui.map = mapFragment.getMap();
        ui.map.setMyLocationEnabled(true);
        ui.map.getUiSettings().setMyLocationButtonEnabled(false);
        ui.map.getUiSettings().setZoomControlsEnabled(false);
        ui.map.setOnMyLocationChangeListener(onMyLocationChangeListener());
        ui.map.setOnCameraChangeListener(onCameraChangeListener());

        if (app.myLocation != null){
            searchPlacesByLatLng(app.myLocation);
            updateMapPosition(app.myLocation, MapUtils.ZOOM_DEFAULT);
        }
    }

    private void setEvents() {
        ui.myLocation.setOnClickListener(onMyLocationClickListener());
        ui.editSearch.setOnEditorActionListener(onEditorActionListener());
//        ui.itemCategoryImg.setOnClickListener(onClickItemCategory());
//        ui.itemCategoryTitle.setOnClickListener(onClickItemCategory());
//        ui.itemCategoryClear.setOnClickListener(onClickClearItemCategory());

        ui.kmSelected.setOnClickListener(onClickKmSelected());

        ui.km1.setOnClickListener(onClickKm());
        ui.km2.setOnClickListener(onClickKm());
        ui.km3.setOnClickListener(onClickKm());

        ui.btnTabList.setOnClickListener(onClickBtnTabList());
        ui.btnTabMap.setOnClickListener(onClickBtnTabMap());

        ui.places.setOnItemClickListener(onItemPlaceClick());
        ui.search.setOnQueryTextListener(onQueryTextListenerEvent());
    }

    private AdapterView.OnItemClickListener onItemPlaceClick() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Place placeSelected = (Place)adapterView.getItemAtPosition(i);
                Map.Entry<Marker, Place> item = findMarkerByPlace(markerPlaceMap, placeSelected);
                if (item != null) {
                    selectTab(TAB_MAP);
                    updateMapPosition(item.getKey().getPosition(), MapUtils.ZOOM_DEFAULT);
                    item.getKey().showInfoWindow();

                }
            }
        };
    }

    private View.OnClickListener onClickBtnTabList() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(TAB_LIST);
            }
        };
    }

    private View.OnClickListener onClickBtnTabMap() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(TAB_MAP);
            }
        };
    }

    private void selectTab(int tab){
        switch (tab){
            case TAB_MAP:
                ui.tabMap.setVisibility(View.VISIBLE);
                ui.tabList.setVisibility(View.GONE);

                ui.btnTabList.setBackgroundResource(android.R.color.white);
                ((TextView)ui.btnTabList).setTextColor(getResources().getColor(R.color.strong_gray));

                ui.btnTabMap.setBackgroundResource(R.color.tab_selected);
                ((TextView)ui.btnTabMap).setTextColor(getResources().getColor(android.R.color.white));
                break;
            case TAB_LIST:
                ui.tabList.setVisibility(View.VISIBLE);
                ui.tabMap.setVisibility(View.GONE);

                ui.btnTabMap.setBackgroundResource(android.R.color.white);
                ((TextView)ui.btnTabMap).setTextColor(getResources().getColor(R.color.strong_gray));

                ui.btnTabList.setBackgroundResource(R.color.tab_selected);
                ((TextView)ui.btnTabList).setTextColor(getResources().getColor(android.R.color.white));
                break;
        }
    }

    private void setList(List<Place> places){
        this.places = places;
        this.adapter = new PlacesAdapter(PlacesFragment.this.getActivity(), R.layout.item_filter_places, this.places);
        ui.places.setAdapter(adapter);
    }


    private SearchView.OnQueryTextListener onQueryTextListenerEvent() {
        return new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter != null){
                    adapter.getFilter().filter(newText);
                }
                return false;
            }
        };
    }

    private View.OnClickListener onClickClearItemCategory() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                categorySelected = null;
                searchPlacesByLatLng(app.myLocation);
            }
        };
    }

    private void setValues() {
        setValueKm(R.drawable.ic_btn_7km, 7);
    }

    private View.OnClickListener onClickKm() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseKmButtons();

                switch (view.getId()){
                    case R.id.places_distance_1km:
                        setValueKm(R.drawable.ic_btn_3km, 3);
                        break;
                    case R.id.places_distance_2km:
                        setValueKm(R.drawable.ic_btn_5km, 5);
                        break;
                    case R.id.places_distance_3km:
                        setValueKm(R.drawable.ic_btn_7km, 7);
                        break;
                }
            }
        };
    }

    private void setValueKm(int resourceId, int distanceKm) {
        ((MyFloatButton)ui.kmSelected).setDrawableIcon(getResources().getDrawable(resourceId));
        this.distanceKm = distanceKm;
    }

    private View.OnClickListener onClickKmSelected() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCloseKmButtons();
            }
        };
    }

    private void openCloseKmButtons() {
        if (isOpenKmButtons){
            hideKmButtons();
        } else {
            showKmButtons();
        }

        isOpenKmButtons = !isOpenKmButtons;
    }

    private View.OnClickListener onClickItemCategory() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCategoriesDialog();
            }
        };
    }

    private void showCategoriesDialog(){
        if (categories != null) {
            DialogCategory.showDialog(PlacesFragment.this.getFragmentManager(), categories, callbackCategorySelected());
        }
    }

    private CallbackDialogSelect callbackCategorySelected() {
        return new CallbackDialogSelect() {

            @Override
            public void select(Object o) {
                categorySelected = (Category) o;
                searchPlacesByLatLng(ui.map.getCameraPosition().target);
            }

            @Override
            public void cancel() {

            }
        };
    }

//    private void disableItemCategoryEvents() {
//        ui.itemCategoryImg.setOnClickListener(null);
//        ui.itemCategoryTitle.setOnClickListener(null);
//    }
//
//    private void enableItemCategoryEvents(){
//        ui.itemCategoryImg.setOnClickListener(onClickItemCategory());
//        ui.itemCategoryTitle.setOnClickListener(onClickItemCategory());
//    }

    private void setProgress(int state){
        switch (state){
            case SEARCHING:
                ui.progress.setVisibility(View.VISIBLE);
                break;
            case STOPED:
                ui.progress.setVisibility(View.GONE);
                break;
        }

    }

    private void hideKmButtons(){
        ui.km1.setVisibility(View.GONE);
        ui.km2.setVisibility(View.GONE);
        ui.km3.setVisibility(View.GONE);
    }

    private void showKmButtons(){
        ui.km1.setVisibility(View.VISIBLE);
        ui.km2.setVisibility(View.VISIBLE);
        ui.km3.setVisibility(View.VISIBLE);
    }

    private void getExtras() {
        Bundle extras = getActivity().getIntent().getExtras();

        if (extras != null) {
            cardType = (CardType) extras.getSerializable("product");
        }
    }

    private void checkLocation(StatusEnum status) {
        if (status == StatusEnum.INICIO) {
            statusInicioByText();
        } else if (status == StatusEnum.EXECUTANDO) {
            statusExecutando();
        } else if (status == StatusEnum.EXECUTADO) {
            statusExecutado();
        }
    }

    private void getPlaces(Address address) {
        new PlaceClient(PlacesFragment.this.getActivity()).nextMe(buildDTO(address), String.valueOf(cardType.getId()), buildCallbackService());
    }

    private retrofit.Callback<List<Place>> buildCallbackService() {
        return new retrofit.Callback<List<Place>>() {
            @Override
            public void success(List<Place> places, Response response) {
                addMarkersOnMap(places);
                addCategories(places);
                setProgress(STOPED);
                setList(places);
            }

            @Override
            public void failure(RetrofitError error) {
                if (error.getKind().equals(RetrofitError.Kind.NETWORK)) {
                    Toast.makeText(PlacesFragment.this.getActivity(), "Erro interno, tente novamente", Toast.LENGTH_SHORT).show();
                }
                setProgress(STOPED);
            }
        };
    }

    private void addCategories(List<Place> places) {
//        ui.categoryCard.setVisibility(View.VISIBLE);
        this.categories = Category.getFromPlaces(places);
//        setItemCategory(categorySelected);
    }

//    private void setItemCategory(Category categorySelected) {
//        if (categorySelected != null) {
//            ui.itemCategoryTitle.setText(categorySelected.getTitle());
//            ui.itemCategoryImg.setImageResource(categorySelected.getResource());
//            ui.itemCategoryClear.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
//            enableItemCategoryEvents();
//        } else {
//            ui.itemCategoryTitle.setText(Category.createItemAllCategories().getTitle());
//            ui.itemCategoryImg.setImageResource(Category.createItemAllCategories().getResource());
//            ui.itemCategoryClear.setImageResource(0);
//            disableItemCategoryEvents();
//        }
//    }

    private void addMarkersOnMap(List<Place> places) {
        ui.map.clear();
        this.places = places;
        for (Place place : places) {
            markerPlaceMap.put(addMarker(place), place);
        }
    }

    private Marker addMarker(Place place) {
        ui.map.setInfoWindowAdapter(new CustomInfoWindow(this));

        if (drawable == null){
            drawable = BitmapDescriptorFactory.fromResource(R.drawable.ic_pin_cabal);
        }

        return ui.map.addMarker(
                new MarkerOptions()
                        .icon(drawable)
                        .title(TextUtils.capitalize(place.getNome()))
                        .snippet(new Gson().toJson(place))
                        .position(new LatLng(place.getLatitude(), place.getLongitude())));
    }

    private NextMeRequestDTO buildDTO(Address address) {
        return new NextMeRequestDTO(
                getRadius(),
                MapUtils.getUF(address),
                String.valueOf(address.getLatitude()),
                String.valueOf(address.getLongitude()),
                selectFilter(),
                DeviceUtils.getDeviceKey(PlacesFragment.this.getActivity()));
    }

    private String selectFilter() {
        if (categorySelected == null || categorySelected.getId() == 9999) {
            return null;
        }
        return String.valueOf(categorySelected.getId());
    }

    private String getRadius() {
        return String.valueOf(distanceKm);
    }

    private void getAddressByLatLng(LatLng latLng) {
        new SearchAddressAsyncTask(PlacesFragment.this.getActivity(), latLng, buildCallbackSearchAddress(), FIRST_ADDRESS).execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_menu_filter:
//                if (places != null && !places.isEmpty()) {
//                    DialogFilterPlace.showDialog(PlacesFragment.this.getFragmentManager(), places, callbackFilterPlacesDialog());
                showCategoriesDialog();
                return true;
        }
        return false;
    }

    private CallbackDialogSelect callbackFilterPlacesDialog() {
        return new CallbackDialogSelect() {

            @Override
            public void select(Object o) {
                Place placeSelected = (Place) o;
                Map.Entry<Marker, Place> item = findMarkerByPlace(markerPlaceMap, placeSelected);
                if (item != null) {
                    updateMapPosition(item.getKey().getPosition(), MapUtils.ZOOM_DEFAULT);
                    item.getKey().showInfoWindow();
                }
            }

            @Override
            public void cancel() {

            }
        };
    }

    private Map.Entry<Marker, Place> findMarkerByPlace(Map<Marker, Place> markerPlaceMap, Place place) {
        for (Map.Entry<Marker, Place> item : markerPlaceMap.entrySet()) {
            if (place.equals(item.getValue())){
                return item;
            }

        }
        return null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_places, menu);
    }

    private Callback buildCallbackSearchAddress() {
        return new Callback() {
            @Override
            public void success(Object o) {
                List<Address> list = ((List<Address>) o);

                if (!list.isEmpty()) {
                    getPlaces(list.get(0));
                }
            }

            @Override
            public void failure(Exception e) {
                setProgress(STOPED);
            }
        };
    }

    private void statusInicioByText() {
        if (app.isInternetConnection(getActivity())) {
            SearchAddressAsyncTask searchAddressAsyncTask = new SearchAddressAsyncTask(getActivity(), ui.editSearch.getText().toString(),
                    onSearchAddressCallback(), MAX_RESULT_TEXT);
            searchAddressAsyncTask.execute();
            app.registerTask(searchAddressAsyncTask);
            checkLocation(StatusEnum.EXECUTANDO);
        } else {
            checkLocation(StatusEnum.EXECUTADO);
        }
    }

    private void statusExecutando() {
        ui.progress.setVisibility(View.VISIBLE);
    }

    private void statusExecutado() {
        ui.progress.setVisibility(View.GONE);
    }

    private GoogleMap.OnMyLocationChangeListener onMyLocationChangeListener() {
        return new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                if (app.myLocation == null) {
                    categorySelected = null;
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    searchPlacesByLatLng(latLng);
                    app.myLocation = latLng;
                }
            }
        };
    }

    private void searchPlacesByLatLng(LatLng latLng) {
        setProgress(SEARCHING);
        if (latLng != null) {
            getAddressByLatLng(latLng);
        }
    }

    private GoogleMap.OnCameraChangeListener onCameraChangeListener() {
        return new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                LatLng current = cameraPosition.target;
                if (lastUpdate == null) {
                    lastUpdate = current;
                } else {
                    double distance = DistanceUtil.calcDistance(lastUpdate, current);
                    if (distance > DISTANCE_UPDATE) {
                        lastUpdate = current;
                        categorySelected = null;
                        searchPlacesByLatLng(current);
                    }
                }
            }
        };
    }

    private View.OnClickListener onMyLocationClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (app.myLocation != null) {
                    MapUtils.updateMapPosition(app.myLocation, MapUtils.ZOOM_DEFAULT, ui.map, true);
                    app.myLocation = null;
                }
            }
        };
    }

    private TextView.OnEditorActionListener onEditorActionListener() {
        return new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                switch (actionId) {
                    case EditorInfo.IME_ACTION_SEARCH:
                        checkLocation(StatusEnum.INICIO);
                        KeyboardUtils.hideKeyboard(getActivity(), ui.editSearch);
                        return true;
                }
                return false;
            }
        };
    }

    private Callback onSearchAddressCallback() {
        return new Callback() {
            @Override
            public void success(Object o) {
                List<Address> address = (List<Address>) o;
                if (!address.isEmpty()) {
                    updateMapPosition(new LatLng(address.get(0).getLatitude(),
                            address.get(0).getLongitude()), MapUtils.ZOOM_DEFAULT);
                }
                checkLocation(StatusEnum.EXECUTADO);
            }

            @Override
            public void failure(Exception e) {
                Toast.makeText(PlacesFragment.this.getActivity(), "Endereço não encontrado!", Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void updateMapPosition(LatLng latLng, float zoom) {
        if (latLng != null){
            CameraUpdate center = CameraUpdateFactory.newLatLngZoom(latLng, zoom);
            ui.map.animateCamera(center);
        }
    }
}

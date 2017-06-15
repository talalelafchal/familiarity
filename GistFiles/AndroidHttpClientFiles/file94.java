package ru.entirec.kindneignbour.kindneighbour.view.fragments;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.annimon.stream.Collectors;
import com.annimon.stream.Stream;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import ru.entirec.kindneighbour.domain.entities.User;
import ru.entirec.kindneighbour.domain.entities.Vehicle;
import ru.entirec.kindneignbour.kindneighbour.R;
import ru.entirec.kindneignbour.kindneighbour.image.ImageLoader;
import ru.entirec.kindneignbour.kindneighbour.internal.di.components.DaggerProfilesComponent;
import ru.entirec.kindneignbour.kindneighbour.internal.di.modules.UIModule;
import ru.entirec.kindneignbour.kindneighbour.presenter.Presenter;
import ru.entirec.kindneignbour.kindneighbour.presenter.ProfilePresenter;
import ru.entirec.kindneignbour.kindneighbour.view.ProfileView;
import ru.entirec.kindneignbour.kindneighbour.view.adapters.UniversalAdapter;
import ru.entirec.kindneignbour.kindneighbour.view.adapters.holders.ButtonAddViewHolder;
import ru.entirec.kindneignbour.kindneighbour.view.adapters.holders.OnPhotoClickListener;
import ru.entirec.kindneignbour.kindneighbour.view.adapters.holders.ProfileInfoViewHolder;
import ru.entirec.kindneignbour.kindneighbour.view.adapters.holders.VehicleViewHolder;
import ru.entirec.kindneignbour.kindneighbour.view.adapters.viewmodels.ViewModelWrapper;

/**
 * Created by Arthur Korchagin on 03.02.16
 */

public class ProfileFragment extends BaseFragment implements ProfileView, ButtonAddViewHolder.OnAddClickListener, VehicleViewHolder.OnVehicleChooseListener, OnPhotoClickListener {

    @Bind(R.id.rv_profile_list)
    RecyclerView mRvProfileList;
    @Bind(R.id.l_progress)
    LinearLayout mLProfileProgress;

    @Inject
    ProfilePresenter mProfilePresenter;

    @Inject
    ImageLoader mImageLoader;

    @Inject
    ProfileInfoViewHolder.Builder mProfileHolderBuilder;
    @Inject
    VehicleViewHolder.Builder mVehicleHolderBuilder;
    @Inject
    ButtonAddViewHolder.Builder mButtonAddBuilder;

    private int mVehicleCount = 0;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fmt_profile, container, false);
        ButterKnife.bind(this, fragmentView);
        return fragmentView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit:
                getNavigator().openEditProfileFragment(mProfilePresenter.getUser());
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        mRvProfileList.setLayoutManager(layoutManager);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.initialize();
        mVehicleHolderBuilder.setOnVehicleChooseListener(this);
        mButtonAddBuilder.setOnAddClickListener(this);
    }

    @Override
    protected void initialize() {
        if (mProfilePresenter == null) {
            DaggerProfilesComponent.builder()
                    .activityComponent(getActivityComponent())
                    .uIModule(new UIModule()
                            .photoListener(this))
                    .build()
                    .inject(this);
        }
        mProfilePresenter.setView(this);
    }

    @Override
    public void renderProfile(User user) {

        List<Vehicle> vehicles = user.getVehicles();
        mVehicleCount = vehicles.size();

        List<ViewModelWrapper> list = new ArrayList<ViewModelWrapper>() {
            {
                add(ViewModelWrapper.build(user, mProfileHolderBuilder));
                addAll(Stream.of(vehicles)
                        .map(value -> ViewModelWrapper.build(value, mVehicleHolderBuilder))
                        .collect(Collectors.toList()));
                add(ViewModelWrapper.build(getString(R.string.label_add_vehicle), mButtonAddBuilder));
            }
        };

        mRvProfileList.setAdapter(new UniversalAdapter(list));
    }

    @Override
    public void showLoading() {
        mLProfileProgress.setVisibility(View.VISIBLE);
        mRvProfileList.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        mLProfileProgress.setVisibility(View.GONE);
        mRvProfileList.setVisibility(View.VISIBLE);
    }

    @Override
    public void showError(String message) {
        showToastMessage(message);
    }


    @Override
    protected Presenter getPresenter() {
        return mProfilePresenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @Override
    public void onAddClick() {
        getNavigator().openVehicleFragment(new Vehicle(-1, mVehicleCount <= 0));
    }

    @Override
    public void onChoose(Vehicle vehicle) {
        getNavigator().openVehicleFragment(vehicle);
    }

    @Override
    protected int getTitleRes() {
        return R.string.ttl_profile;
    }

    @Override
    protected int getSubtitleRes() {
        return R.string.label_empty;
    }

    @Override
    public void onPhotoClick(String url, String title) {
        getNavigator().openPhoto(url, title);
    }
}

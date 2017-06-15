package ru.entirec.kindneignbour.kindneighbour.internal.di.components;

import dagger.Component;
import ru.entirec.kindneignbour.kindneighbour.internal.di.PerFragment;
import ru.entirec.kindneignbour.kindneighbour.internal.di.modules.ProfileModule;
import ru.entirec.kindneignbour.kindneighbour.internal.di.modules.StorageModule;
import ru.entirec.kindneignbour.kindneighbour.internal.di.modules.UIModule;
import ru.entirec.kindneignbour.kindneighbour.view.custom.NavigationHeaderView;
import ru.entirec.kindneignbour.kindneighbour.view.fragments.PhotoPickerDialog;
import ru.entirec.kindneignbour.kindneighbour.view.fragments.ProfileEditFragment;
import ru.entirec.kindneignbour.kindneighbour.view.fragments.ProfileFragment;
import ru.entirec.kindneignbour.kindneighbour.view.fragments.TripsPagerFragment;
import ru.entirec.kindneignbour.kindneighbour.view.fragments.UserFragment;
import ru.entirec.kindneignbour.kindneighbour.view.fragments.VehicleFragment;

/**
 * A scope {@link ru.entirec.kindneignbour.kindneighbour.internal.di.PerActivity} component.
 * Injects user specific Fragments.
 */

@PerFragment
@Component(dependencies = ActivityComponent.class, modules = {ProfileModule.class, StorageModule.class, UIModule.class})
public interface ProfilesComponent {

    void inject(NavigationHeaderView navigationHeaderView);

    void inject(ProfileFragment profileFragment);

    void inject(ProfileEditFragment profileFragment);

    void inject(VehicleFragment vehicleFragment);

    void inject(TripsPagerFragment tripsPagerFragment);

    void inject(PhotoPickerDialog photoPickerDialog);

    void inject(UserFragment userFragment);
}

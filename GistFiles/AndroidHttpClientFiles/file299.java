package ru.entirec.kindneignbour.data.repository;

import java.io.File;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.entirec.kindneighbour.domain.entities.User;
import ru.entirec.kindneighbour.domain.entities.Vehicle;
import ru.entirec.kindneighbour.domain.entities.utils.UserMode;
import ru.entirec.kindneighbour.domain.repository.ProfileRepository;
import ru.entirec.kindneighbour.domain.responces.UseCaseResponse;
import ru.entirec.kindneignbour.data.cache.PrivateStorage;
import ru.entirec.kindneignbour.data.cache.UserStorage;
import ru.entirec.kindneignbour.data.repository.datasource.DataSourceFactory;
import ru.entirec.kindneignbour.data.response.Response;
import ru.entirec.kindneignbour.data.response.UserResponse;
import ru.entirec.kindneignbour.data.response.VehicleResponse;
import ru.entirec.kindneignbour.data.utils.RxUtils;
import ru.entirec.kindneignbour.data.utils.TestUtils;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by Arthur Korchagin on 28.01.16
 */

@Singleton
public class ProfileRepositoryImpl implements ProfileRepository {

    private final DataSourceFactory mTripDataSourceFactory;
    private final PrivateStorage mPrivateStorage;
    private final UserStorage mUserStorage;
    public final PublishSubject<UseCaseResponse<User>> mUpdatesSubject = PublishSubject.create();
    private User mCurrentUser;

    @Inject
    public ProfileRepositoryImpl(DataSourceFactory tripDataSourceFactory, PrivateStorage privateStorage, UserStorage userStorage) {
        mTripDataSourceFactory = tripDataSourceFactory;
        mPrivateStorage = privateStorage;
        mUserStorage = userStorage;
    }

    @Override
    public Observable<UseCaseResponse<User>> fetchProfileUpdates() {
        return mUpdatesSubject;
    }

    /**
     * As first fetch user from storage and emit it,
     * next receive user from network,
     * if entity changed emit user again
     **/
    @Override
    public Observable<UseCaseResponse<User>> fetchProfile() {
        return Observable.merge(mUserStorage.fetchUserObservable()
                        .doOnNext(this::setUser)
                        .map(UseCaseResponse::buildSuccessResponse),
                mTripDataSourceFactory.getApiService().fetchUser(getToken())
                        .map(UserResponse::transform)
                        .filter(resp -> mCurrentUser == null || !mCurrentUser.equals(resp.getEntity()))
                        .doOnNext(resp -> putUser(resp.getEntity()))
                        .doOnNext(mUpdatesSubject::onNext)
                        .compose(RxUtils.processErrors()));
    }

    /**
     * As first fetch user from network, save in local storage and emit it,
     * if cannot receive user from network, fetch from local storage
     **/
    @Override
    public Observable<UseCaseResponse<User>> fetchFreshProfile() {
        return
                mTripDataSourceFactory.getApiService().fetchUser(getToken())
                        .map(UserResponse::transform)
                        .doOnNext(resp -> putUser(resp.getEntity()))
                        .doOnNext(mUpdatesSubject::onNext)
                        .onErrorResumeNext(mUserStorage.fetchUserObservable()
                                .map(UseCaseResponse::buildSuccessResponse))
                        .doOnNext(userUseCaseResponse -> TestUtils.addVehicleImages(userUseCaseResponse.getEntity())); // TODO: 09.02.16 Only for test)
    }

    @Override
    public Observable<UseCaseResponse<User>> saveProfile(User user) {
        return mTripDataSourceFactory.getApiService().saveProfile(getToken(), user)
                .map(UserResponse::transform)
                .compose(RxUtils.processErrors());
    }

    @Override
    public Observable<UseCaseResponse<Vehicle>> putVehicle(Vehicle vehicle) {
        return mTripDataSourceFactory.getApiService().putVehicle(getToken(), vehicle)
                .map(VehicleResponse::transform)
                .compose(RxUtils.processErrors());
    }

    @Override
    public Observable<UseCaseResponse<Object>> deleteVehicle(Vehicle vehicle) {
        return mTripDataSourceFactory.getApiService().deleteVehicle(getToken(), vehicle)
                .map(VehicleResponse::transform)
                .compose(RxUtils.processErrors());
    }

    @Override
    public Observable<UseCaseResponse<Object>> uploadAvatar(File avatarFile) {
        return mTripDataSourceFactory.getApiService().uploadAvatar(getToken(), avatarFile)
                .map(Response::transform)
                .compose(RxUtils.processErrors());
    }

    @Override
    public Observable<UseCaseResponse<Object>> uploadVehiclePhoto(int vehicleId, File photo) {
        return mTripDataSourceFactory.getApiService().uploadVehiclePhoto(getToken(), vehicleId, photo)
                .map(Response::transform)
                .compose(RxUtils.processErrors());
    }

    public synchronized String getToken() {
        return mPrivateStorage.fetchToken();
    }

    public synchronized void putUser(User user) {
        mPrivateStorage.putUser(user);
    }

    public synchronized void setUser(User user) {
        this.mCurrentUser = user;
    }
}

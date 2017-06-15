package ru.entirec.kindneignbour.data.network;

import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Converter;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;
import retrofit2.RxJavaCallAdapterFactory;
import ru.entirec.kindneighbour.domain.entities.User;
import ru.entirec.kindneighbour.domain.entities.Vehicle;
import ru.entirec.kindneighbour.domain.entities.request.TripRequest;
import ru.entirec.kindneignbour.data.response.AuthResponse;
import ru.entirec.kindneignbour.data.response.Response;
import ru.entirec.kindneignbour.data.response.TripResponse;
import ru.entirec.kindneignbour.data.response.TripsResponse;
import ru.entirec.kindneignbour.data.response.UserResponse;
import ru.entirec.kindneignbour.data.response.VehicleResponse;
import ru.entirec.kindneignbour.data.utils.DateProcessor;
import ru.entirec.kindneignbour.data.utils.DateUtils;
import ru.entirec.kindneignbour.data.utils.FileUtils;
import rx.Observable;

/**
 * Created by Arthur Korchagin on 22.01.16
 */

@Singleton
public class ApiServiceImpl implements ApiService {

    public static final String BEARER = "Bearer ";
    private static final String WEB_SERVICE_BASE_URL = "http://mysite.com/";

    private static final int OS_TYPE_ANDROID = 0;

    private final RequestService mRequestService;

    public ApiServiceImpl() {

        OkHttpClient httpClient = createHttpClient();

        Retrofit retrofitBuilder = new Retrofit.Builder()
                .baseUrl(WEB_SERVICE_BASE_URL)
                .addConverterFactory(createConverterFactory())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(httpClient)
                .build();

        mRequestService = retrofitBuilder.create(RequestService.class);
    }

    @NonNull
    private Converter.Factory createConverterFactory() {
        return GsonConverterFactory.create(createGson());
    }

    @NonNull
    private Gson createGson() {
        return new GsonBuilder()
                .setDateFormat(DateUtils.PATTERN_SERVER_DATETIME)
                .create();
    }

    private OkHttpClient createHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        return new OkHttpClient.Builder()
                .addInterceptor(createHeadersInterceptor())
                .addInterceptor(interceptor)
                .connectTimeout(1, TimeUnit.MINUTES)
                .readTimeout(1, TimeUnit.MINUTES)
                .build();
    }


    private Interceptor createHeadersInterceptor() {
        return (chain) -> {
            Request.Builder requestBuilder = chain.request().newBuilder();
            return chain.proceed(requestBuilder.build());
        };
    }

    @Override
    public Observable<TripsResponse> fetchTrips(String token, int count, int offset, String role) {
        return mRequestService.fetchTrips(processToken(token), count, offset, role);
    }

    @Override
    public Observable<TripResponse> fetchTrip(String token, int tripId) {
        return mRequestService.fetchTrip(processToken(token), tripId);
    }

    @Override
    public Observable<TripResponse> createTrip(String token, TripRequest tripRequest) {
        return mRequestService.createTrip(processToken(token), tripRequest);
    }

    @Override
    public Observable<TripResponse> editTrip(String token, int tripId, TripRequest tripRequest) {
        return mRequestService.editTrip(processToken(token), tripId, tripRequest);
    }

    @Override
    public Observable<Response> removeTrip(String token, int tripId) {
        return mRequestService.removeTrip(processToken(token), tripId);
    }

    @Override
    public Observable<TripsResponse> searchTrips(String token, TripRequest tripRequest) {
        return mRequestService.searchTrips(processToken(token), tripRequest);
    }

    @Override
    public Observable<AuthResponse> authorize(String phone, String code) {
        return mRequestService.auth(phone, code)
                .doOnNext(authResponse -> DateProcessor.process(authResponse.getUser()));
    }

    @Override
    public Observable<AuthResponse> register(String phone, String name, String date) {
        return mRequestService.register(phone, name, date)
                .doOnNext(authResponse -> DateProcessor.process(authResponse.getUser()));
    }

    @Override
    public Observable<AuthResponse> register(String phone) {
        return mRequestService.register(phone)
                .doOnNext(authResponse -> DateProcessor.process(authResponse.getUser()));
    }

    @Override
    public Observable<UserResponse> fetchUser(String token) {
        return mRequestService.fetchUser(processToken(token))
                .doOnNext(authResponse -> DateProcessor.process(authResponse.getUser()));
    }

    @Override
    public Observable<UserResponse> saveProfile(String token, User user) {
        return mRequestService.saveUser(processToken(token), user)
                .doOnNext(authResponse -> DateProcessor.process(authResponse.getUser()));
    }

    @Override
    public Observable<VehicleResponse> putVehicle(String token, Vehicle vehicle) {
        int id = vehicle.getId();
        if (id > 0) {
            return mRequestService.saveVehicle(processToken(token), id, vehicle);
        } else {
            return mRequestService.createVehicle(processToken(token), vehicle);
        }
    }

    @Override
    public Observable<Response> deleteVehicle(String token, Vehicle vehicle) {
        return mRequestService.deleteVehicle(processToken(token), vehicle.getId());
    }

    @Override
    public Observable<Response> joinAsPassenger(String token, int tripId, int reserved) {
        return mRequestService.joinAsPassenger(processToken(token), tripId, reserved);
    }

    @Override
    public Observable<Response> joinAsDriver(String token, int tripId, int vehicleId) {
        return mRequestService.joinAsDriver(processToken(token), tripId); // TODO: 11.02.16 Add vehicle id
    }

    @Override
    public Observable<Response> uploadAvatar(String token, File avatarFile) {
        RequestBody file = RequestBody.create(MediaType.parse(FileUtils.getMimeTypeOfFile(avatarFile)), avatarFile);
        return mRequestService.uploadAvatar(processToken(token), file);
    }

    @Override
    public Observable<Response> uploadVehiclePhoto(String token, int vehicleId, File photo) {
        RequestBody file = RequestBody.create(MediaType.parse(FileUtils.getMimeTypeOfFile(photo)), photo);
        return mRequestService.uploadVehiclePhoto(processToken(token), vehicleId, file);
    }

    @Override
    public Observable<Response> leave(String token, int tripId) {
        return mRequestService.leave(processToken(token), tripId);
    }

    @Override
    public Observable<Response> approveOrDeclineDriver(String token, int tripId, int userId, boolean approve) {
        return mRequestService.approveOrDeclineDriver(processToken(token), tripId, userId, approve ? 1 : 0);
    }

    @Override
    public Observable<Response> approveOrDeclinePassenger(String token, int tripId, int userId, boolean approve) {
        return mRequestService.approveOrDeclinePassenger(processToken(token), tripId, userId, approve ? 1 : 0);
    }

    @Override
    public Observable<Response> sendGcmToken(String token, String gcmToken) {
        return mRequestService.sendGcmToken(processToken(token), gcmToken, OS_TYPE_ANDROID);
    }

    @NonNull
    private String processToken(String token) {
        return TextUtils.concat(BEARER, token).toString();
    }

}

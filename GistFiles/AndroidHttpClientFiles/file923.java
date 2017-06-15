package ru.entirec.kindneignbour.kindneighbour.internal.di.modules;

import android.app.Activity;
import android.graphics.Bitmap;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ru.entirec.kindneighbour.domain.entities.User;
import ru.entirec.kindneighbour.domain.executor.PostExecutionThread;
import ru.entirec.kindneighbour.domain.executor.ThreadExecutor;
import ru.entirec.kindneighbour.domain.interactor.DeleteVehicleCase;
import ru.entirec.kindneighbour.domain.interactor.GetProfileCase;
import ru.entirec.kindneighbour.domain.interactor.ProfileUpdateCase;
import ru.entirec.kindneighbour.domain.interactor.PutVehicleCase;
import ru.entirec.kindneighbour.domain.interactor.SaveProfileCase;
import ru.entirec.kindneighbour.domain.interactor.UseCase;
import ru.entirec.kindneighbour.domain.repository.ProfileRepository;
import ru.entirec.kindneighbour.domain.requests.SaveProfileRequest;
import ru.entirec.kindneighbour.domain.requests.UseCaseRequest;
import ru.entirec.kindneighbour.domain.requests.VehicleRequest;
import ru.entirec.kindneighbour.domain.responces.UseCaseResponse;
import ru.entirec.kindneignbour.kindneighbour.image.ImageFile;
import ru.entirec.kindneignbour.kindneighbour.internal.di.PerFragment;
import ru.entirec.kindneignbour.kindneighbour.uiinteractor.PhotoUseCase;
import ru.entirec.kindneignbour.kindneighbour.uiinteractor.request.MediaRequest;

/**
 * Created by Arthur Korchagin on 22.01.16
 * <p>
 * Dagger module that provides trips.
 */

@Module
public class ProfileModule {

    @Provides
    @PerFragment
    @Named(PhotoUseCase.CASE_NAME)
    UseCase<MediaRequest, UseCaseResponse<ImageFile>> providePhotoUseCase(Activity activity, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        return new PhotoUseCase(threadExecutor, postExecutionThread, activity);
    }

    @Provides
    @PerFragment
    @Named(GetProfileCase.CASE_NAME)
    UseCase<UseCaseRequest, UseCaseResponse<User>> provideGetProfileCase(ProfileRepository profileRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        return new GetProfileCase(profileRepository, threadExecutor, postExecutionThread);
    }

    @Provides
    @PerFragment
    @Named(ProfileUpdateCase.CASE_NAME)
    UseCase<UseCaseRequest, UseCaseResponse<User>> provideGetProfileUpdatesCase(ProfileRepository profileRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        return new ProfileUpdateCase(profileRepository, threadExecutor, postExecutionThread);
    }

    @Provides
    @PerFragment
    @Named(SaveProfileCase.CASE_NAME)
    UseCase<SaveProfileRequest, UseCaseResponse<Object>> provideSaveProfileCase(ProfileRepository profileRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        return new SaveProfileCase(profileRepository, threadExecutor, postExecutionThread);
    }

    @Provides
    @PerFragment
    @Named(PutVehicleCase.CASE_NAME)
    UseCase<VehicleRequest, UseCaseResponse<Object>> providePutVehicleCase(ProfileRepository profileRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        return new PutVehicleCase(profileRepository, threadExecutor, postExecutionThread);
    }

    @Provides
    @PerFragment
    @Named(DeleteVehicleCase.CASE_NAME)
    UseCase<VehicleRequest, UseCaseResponse<Object>> provideDeleteVehicleCase(ProfileRepository profileRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        return new DeleteVehicleCase(profileRepository, threadExecutor, postExecutionThread);
    }

}
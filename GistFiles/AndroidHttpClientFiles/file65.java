package ru.entirec.kindneignbour.kindneighbour.presenter;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import ru.entirec.kindneighbour.domain.entities.User;
import ru.entirec.kindneighbour.domain.interactor.DefaultSubscriber;
import ru.entirec.kindneighbour.domain.interactor.GetProfileCase;
import ru.entirec.kindneighbour.domain.interactor.UseCase;
import ru.entirec.kindneighbour.domain.requests.UseCaseRequest;
import ru.entirec.kindneighbour.domain.responces.UseCaseResponse;
import ru.entirec.kindneignbour.kindneighbour.internal.di.PerFragment;
import ru.entirec.kindneignbour.kindneighbour.rx.DefaultViewSubscriber;
import ru.entirec.kindneignbour.kindneighbour.view.ProfileView;

/**
 * Created by Arthur Korchagin on 03.02.16
 */

@PerFragment
public class ProfilePresenter implements Presenter {

    /* Use Cases */
    private final UseCase<UseCaseRequest, UseCaseResponse<User>> mGetProfileUseCase;

    /* Main View */
    private ProfileView mProfileView;

    /* Subscribers */
    private DefaultSubscriber<UseCaseResponse<User>> mUseCaseSubscriber;

    private User mUser;

    @Inject
    public ProfilePresenter(@Named(GetProfileCase.CASE_NAME)
                            UseCase<UseCaseRequest, UseCaseResponse<User>> getProfileUseCase) {
        mGetProfileUseCase = getProfileUseCase;
    }

    @Override
    public void start() {
        mProfileView.showLoading();
        mGetProfileUseCase.execute(null, getUseCaseSubscriber());
    }

    @Override
    public void stop() {
        mGetProfileUseCase.unsubscribe();
    }

    public void setView(ProfileView view) {
        this.mProfileView = view;
    }

    public Serializable getUser() {
        return mUser;
    }

    private void onProfileLoaded(UseCaseResponse<User> userUseCaseResponse) {
        mProfileView.hideLoading();
        if (userUseCaseResponse.isStatusOk()) {
            mUser = userUseCaseResponse.getEntity();
            mProfileView.renderProfile(mUser);
        } else {
            mProfileView.showError(userUseCaseResponse.getPrintMessage());
        }
    }

    public DefaultSubscriber<UseCaseResponse<User>> getUseCaseSubscriber() {
        if (mUseCaseSubscriber == null) {
            mUseCaseSubscriber = new DefaultViewSubscriber<>(mProfileView, this::onProfileLoaded);
        }
        return mUseCaseSubscriber;
    }
}

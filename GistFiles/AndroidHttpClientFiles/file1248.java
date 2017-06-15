package ru.entirec.kindneighbour.domain.interactor;

import javax.inject.Inject;

import ru.entirec.kindneighbour.domain.entities.User;
import ru.entirec.kindneighbour.domain.executor.PostExecutionThread;
import ru.entirec.kindneighbour.domain.executor.ThreadExecutor;
import ru.entirec.kindneighbour.domain.repository.ProfileRepository;
import ru.entirec.kindneighbour.domain.requests.UseCaseRequest;
import ru.entirec.kindneighbour.domain.responces.UseCaseResponse;
import rx.Observable;

/**
 * Created by Arthur Korchagin on 27.01.16
 */
public class GetProfileCase extends UseCase<UseCaseRequest, UseCaseResponse<User>> {

    public static final String CASE_NAME = "get_profile_case";

    private final ProfileRepository mProfileRepository;

    @Inject
    public GetProfileCase(ProfileRepository profileRepository, ThreadExecutor threadExecutor, PostExecutionThread postExecutionThread) {
        super(threadExecutor, postExecutionThread);
        mProfileRepository = profileRepository;
    }

    @Override
    protected Observable<UseCaseResponse<User>> buildUseCaseObservable(UseCaseRequest r) {
        return mProfileRepository.fetchProfile();
    }
}

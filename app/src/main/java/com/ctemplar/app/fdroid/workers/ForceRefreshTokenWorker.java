package com.ctemplar.app.fdroid.workers;

import android.content.Context;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.work.RxWorker;
import androidx.work.WorkerParameters;

import io.reactivex.Single;
import com.ctemplar.app.fdroid.CTemplarApp;
import com.ctemplar.app.fdroid.net.request.TokenRefreshRequest;
import com.ctemplar.app.fdroid.repository.UserRepository;
import com.ctemplar.app.fdroid.repository.UserStore;
import timber.log.Timber;

public class ForceRefreshTokenWorker extends RxWorker {
    private final UserRepository userRepository;

    public ForceRefreshTokenWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        userRepository = CTemplarApp.getUserRepository();
    }

    @NonNull
    @Override
    public Single<Result> createWork() {
        UserStore userStore = userRepository.getUserStore();

        boolean keepMeLoggedIn = userStore.getKeepMeLoggedIn();
        String currentUserToken = userStore.getUserToken();
        if (TextUtils.isEmpty(currentUserToken) || !keepMeLoggedIn) {
            Timber.i("Token is null or keep me logged in is disabled");
            return Single.just(Result.success());
        }
        long lastForceRefreshTokenAttemptTime = userStore.getLastForceRefreshTokenAttemptTime();
        long timeDifference = System.currentTimeMillis() - lastForceRefreshTokenAttemptTime;
        if (timeDifference < 4 * 60 * 60 * 1000) {
            Timber.d("4 hours have not passed yet: %s", timeDifference);
            return Single.just(Result.retry());
        }

        return userRepository.getRestService()
                .refreshTokenSingle(new TokenRefreshRequest(currentUserToken))
                .doOnSuccess(response -> {
                    if (response == null) {
                        Timber.e("response is null");
                        return;
                    }
                    userStore.saveUserToken(response.getToken());
                    Timber.i("force token update success");
                })
                .doOnError(Timber::e)
                .doFinally(userStore::updateLastForceRefreshTokenAttemptTime)
                .map(v -> v.getToken() == null ? Result.retry() : Result.success()) ;
    }
}

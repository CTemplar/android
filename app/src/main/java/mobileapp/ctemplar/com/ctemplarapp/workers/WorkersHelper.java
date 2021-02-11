package mobileapp.ctemplar.com.ctemplarapp.workers;

import android.content.Context;

import androidx.work.BackoffPolicy;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public class WorkersHelper {
    private static final String FORCE_REFRESH_TOKEN_WORK_NAME = "force_refresh_token";

    public static void setupForceRefreshTokenWork(Context context) {
        Timber.i("setupForceRefreshTokenWorker");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        PeriodicWorkRequest forceRefreshUserTokenWorkRequest =
                new PeriodicWorkRequest.Builder(ForceRefreshTokenWorker.class,
                        24, TimeUnit.HOURS)
                        .setConstraints(constraints)
                        .setBackoffCriteria(
                                BackoffPolicy.EXPONENTIAL,
                                12, TimeUnit.HOURS
                        )
                        .build();

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                FORCE_REFRESH_TOKEN_WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                forceRefreshUserTokenWorkRequest);
    }

    public static void cancelAllWork(Context context) {
        Timber.i("cancelAllWork");
        WorkManager.getInstance(context).cancelAllWork();
    }
}

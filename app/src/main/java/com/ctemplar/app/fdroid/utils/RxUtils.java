package com.ctemplar.app.fdroid.utils;

import org.jetbrains.annotations.NotNull;

import io.reactivex.Scheduler;
import io.reactivex.Single;
import io.reactivex.SingleObserver;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class RxUtils {
    public interface RunnableWithParam<T> {
        T run() throws Exception;
    }

    public interface OnSuccessCallbackWithParam<T> {
        void onSuccess(T result);
    }

    public interface OnSuccessCallback {
        void onSuccess();
    }

    public interface OnErrorCallback {
        void onError(Throwable e);
    }

    public static <T> void callAsyncWithResult(RunnableWithParam<T> runnable, OnSuccessCallbackWithParam<T> onSuccess, OnErrorCallback onError) {
        callAsyncWithResult(runnable, onSuccess, onError, Schedulers.io());
    }


    public static <T> void callAsyncWithResult(RunnableWithParam<T> runnable, OnSuccessCallbackWithParam<T> onSuccess, OnErrorCallback onError, Scheduler subscribeOn) {
        Single.create((SingleOnSubscribe<T>) emitter -> emitter.onSuccess(runnable.run()))
                .subscribeOn(subscribeOn)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<T>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull T response) {
                        onSuccess.onSuccess(response);
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        onError.onError(e);
                    }
                });
    }

    public static void callAsync(Runnable runnable, OnSuccessCallback onSuccess, OnErrorCallback onError) {
        callAsync(runnable, onSuccess, onError, Schedulers.io());
    }

    public static void callAsync(Runnable runnable, OnSuccessCallback onSuccess, OnErrorCallback onError, Scheduler subscribeOn) {
        Single.create(emitter -> {
            runnable.run();
            emitter.onSuccess(new Object());
        })
                .subscribeOn(subscribeOn)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new SingleObserver<Object>() {
                    @Override
                    public void onSubscribe(@NotNull Disposable d) {

                    }

                    @Override
                    public void onSuccess(@NotNull Object response) {
                        onSuccess.onSuccess();
                    }

                    @Override
                    public void onError(@NotNull Throwable e) {
                        onError.onError(e);
                    }
                });
    }
}

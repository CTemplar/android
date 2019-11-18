package mobileapp.ctemplar.com.ctemplarapp;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;
import mobileapp.ctemplar.com.ctemplarapp.utils.DialogUtils;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getLayoutId();

    private Unbinder mUnbinder;

    protected boolean mRegisterForUserTokenExpiry = true;
    private boolean mExpiredDialogShowing = false;
    private CompositeDisposable mSubscriptions = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());
        mUnbinder = ButterKnife.bind(this);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
    }

    @Override
    protected void onDestroy() {
        mUnbinder.unbind();
        super.onDestroy();
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mRegisterForUserTokenExpiry) {

//            mSubscriptions.add(UserTokenExpired
//                    .asObservable()
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Subscriber<String>() {
//                        @Override
//                        public void onCompleted() {
//                            // never happens
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            // never happens
//                        }
//
//                        @Override
//                        public void onNext(String o) {
//                            // ignore the Object
//                            onUserTokenExpired();
//                        }
//                    })
//            );
        }
    }

    @Override
    protected void onPause() {
        mSubscriptions.dispose();
        mSubscriptions.clear();
        super.onPause();
    }

    protected boolean handleBackPress() {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (!handleBackPress()) {
            finish();
        }
    }

    private void onUserTokenExpired() {
        if (!mExpiredDialogShowing) {
            mExpiredDialogShowing = true;
            // Logout and show dialog
            DialogUtils.showAlertDialog(this, R.string.token_expired_title, R.string.token_expired_message, false, dialog -> {
                mExpiredDialogShowing = false;
                // go to LoginActivity for relogin
                // I.startLoginActivity(BaseActivity.this);
                finish();
            });
        }
    }
}

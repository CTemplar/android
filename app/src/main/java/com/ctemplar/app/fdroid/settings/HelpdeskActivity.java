package com.ctemplar.app.fdroid.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import butterknife.BindView;
import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;
import timber.log.Timber;

public class HelpdeskActivity extends BaseActivity {
    @BindView(R.id.activity_helpdesk_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_helpdesk_web_view)
    WebView webView;

    @BindView(R.id.activity_helpdesk_progress_bar)
    ProgressBar progressBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_helpdesk;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        webView.loadUrl(getString(R.string.url_helpdesk));
        webView.getSettings().setJavaScriptEnabled(false);
        webView.getSettings().setAllowFileAccess(false);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                showProgressBar(false);
            }
        });
    }

    private void showProgressBar(boolean state) {
        if (progressBar == null) {
            Timber.e("progressBar is null");
            return;
        }
        progressBar.setVisibility(state ? View.VISIBLE : View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

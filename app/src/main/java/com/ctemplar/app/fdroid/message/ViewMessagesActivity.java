package com.ctemplar.app.fdroid.message;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;

import com.ctemplar.app.fdroid.BaseActivity;
import com.ctemplar.app.fdroid.R;

public class ViewMessagesActivity extends BaseActivity {

    public static final String PARENT_ID = "parent_id";
    private ViewMessagesFragment fragment;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_messages;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = ViewMessagesFragment.newInstance(getIntent().getExtras());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.activity_view_messages_content_frame, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}

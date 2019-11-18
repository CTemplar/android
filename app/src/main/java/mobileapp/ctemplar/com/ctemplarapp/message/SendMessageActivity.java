package mobileapp.ctemplar.com.ctemplarapp.message;

import android.os.Bundle;
import androidx.fragment.app.FragmentManager;

import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;

public class SendMessageActivity extends BaseActivity {

    public static final String PARENT_ID = "parent_id";
    public static final String MESSAGE_ID = "message_id";
    public static final String ATTACHMENT_LIST = "attachment_list";
    private SendMessageFragment fragment;

    @Override
    protected int getLayoutId() {
        return R.layout.send_message_activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragment = SendMessageFragment.newInstance(getIntent().getExtras());

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.activity_send_message_content_frame, fragment)
                .commit();
    }

    @Override
    public void onBackPressed() {
        if (fragment.onBackPressed()) {
            super.onBackPressed();
        }
    }
}

package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;

public class SendMessageActivity extends BaseActivity {

    private SendMessageActivityViewModel mainModel;

    @BindView(R.id.content_frame)
    FrameLayout mContentFrame;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(mContentFrame.getId(), new SendMessageFragment())
                .commit();

        mainModel = ViewModelProviders.of(this).get(SendMessageActivityViewModel.class);
        mainModel.getMessagesResult()
                .observe(this, new Observer<MessagesResult>() {
                    @Override
                    public void onChanged(@Nullable MessagesResult messagesResult) {
                        if (messagesResult == null) {
                            Toast.makeText(SendMessageActivity.this, "Not sent", Toast.LENGTH_SHORT).show();
                        } else {
                            onBackPressed();
                        }
                    }
                });
    }

}

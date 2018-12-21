package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import butterknife.BindView;
import mobileapp.ctemplar.com.ctemplarapp.BaseActivity;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import timber.log.Timber;

public class ViewMessageActivity extends BaseActivity {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_view_message;
    }

    ViewMessageActivityViewModel mainModel;

    @BindView(R.id.content_frame)
    FrameLayout mContentFrame;

    @BindView(R.id.progress_bar)
    ProgressBar progress;

    @BindView(R.id.progress_background)
    View progressBackground;

    public static final String ARG_ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mainModel = ViewModelProviders.of(this).get(ViewMessageActivityViewModel.class);

        mainModel.getMessageResponse()
                .observe(this, new Observer<MessagesResult>() {
                    @Override
                    public void onChanged(@Nullable MessagesResult messagesResult) {
                        if (messagesResult != null) {
                            progress.setVisibility(View.GONE);
                            progressBackground.setVisibility(View.GONE);
                            mContentFrame.setVisibility(View.VISIBLE);
                        } else {
                            Timber.e("Message doesn't exist");
                            Toast.makeText(getApplicationContext(), "Message doesn't exist", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }
                });

        long id = getIntent().getLongExtra(ARG_ID, -1);
        if (id == -1) {
            return; //ToDo
        }

        ViewMessagesFragment fragment = new ViewMessagesFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_ID, id);
        fragment.setArguments(args);


        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .add(mContentFrame.getId(), fragment)
                .commit();

    }
}

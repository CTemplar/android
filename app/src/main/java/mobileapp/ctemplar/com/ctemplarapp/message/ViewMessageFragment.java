package mobileapp.ctemplar.com.ctemplarapp.message;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import net.kibotu.pgp.Pgp;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import butterknife.BindView;
import butterknife.OnClick;
import mobileapp.ctemplar.com.ctemplarapp.BaseFragment;
import mobileapp.ctemplar.com.ctemplarapp.CTemplarApp;
import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Messages.MessagesResult;
import mobileapp.ctemplar.com.ctemplarapp.repository.entity.MailboxEntity;
import timber.log.Timber;

public class ViewMessageFragment extends BaseFragment {
    private ViewMessageActivityViewModel viewMessageModel;
    private MailboxEntity currentMailbox;
    private MessagesResult currentMessage;

    @BindView(R.id.fragment_view_message_subject_text)
    TextView textViewSubject;

    @BindView(R.id.fragment_view_message_from_name)
    TextView textViewFromName;

    @BindView(R.id.fragment_view_message_from_email)
    TextView textViewFromEmail;

    @BindView(R.id.fragment_view_message_to_name)
    TextView textViewToName;

    @BindView(R.id.fragment_view_message_to_email)
    TextView textViewToEmail;

    @BindView(R.id.fragment_view_message_CC_layout)
    RelativeLayout textViewCCLayout;

    @BindView(R.id.fragment_view_message_CC_name)
    TextView textViewCCName;

    @BindView(R.id.fragment_view_message_CC_email)
    TextView textViewCCEmail;

    @BindView(R.id.fragment_view_message_date)
    TextView textViewMessageDate;

    @BindView(R.id.fragment_view_message_content)
    TextView textViewContent;

    @BindView(R.id.fragment_view_message_subject_star_image)
    ImageView imageViewStar;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_view_message;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        currentMailbox = CTemplarApp.getAppDatabase().mailboxDao().getDefault();

        viewMessageModel = ViewModelProviders.of(getActivity()).get(ViewMessageActivityViewModel.class);
        long id = getArguments().getLong(ViewMessageActivity.ARG_ID, -1);

        viewMessageModel.getMessageResponse().observe(this, new Observer<MessagesResult>() {
            @Override
            public void onChanged(@Nullable MessagesResult messagesResult) {
                handleMessage(messagesResult);
            }
        });

        viewMessageModel.getMessage(id);

        viewMessageModel.getStarredResponse().observe(this, new Observer<MessagesResult>() {
            @Override
            public void onChanged(@Nullable MessagesResult messagesResult) {
                if (messagesResult != null && messagesResult.getId() == currentMessage.getId()) {
                    imageViewStar.setSelected(messagesResult.isStarred());
                    currentMessage = messagesResult;
                }
            }
        });
    }

    private String decodeContent(String encodedString, String password) {
        Pgp.setPrivateKey(currentMailbox.getPrivateKey());
        Pgp.setPublicKey(currentMailbox.getPublicKey());
        String result = "";

        try {
            result = Pgp.decrypt(encodedString, password);
        } catch (Exception e) {
            Timber.e("Pgp decrypt error: %s", e.getMessage());
        }

        return result;
    }

    static String[] addQuotes(String[] receivers) {
        String[] receiversList = new String[receivers.length];
        for (int i = 0; i < receiversList.length; i++) {
            receiversList[i] = "<" + receivers[i] + ">";
        }
        return receiversList;
    }

    void handleMessage(MessagesResult messagesResult) {
        currentMessage = messagesResult;

        textViewSubject.setText(messagesResult.getSubject());
        String sender = '<' + messagesResult.getSender() + '>';
        textViewFromEmail.setText(sender);
        String[] receivers = messagesResult.getReceivers();
        if (receivers != null) {
            receivers = addQuotes(receivers);
            textViewToEmail.setText(TextUtils.join(", ", receivers));
        }
        String[] cc = messagesResult.getCc();
        if (cc != null) {
            cc = addQuotes(cc);
            textViewCCEmail.setText(TextUtils.join(", ", cc));
        } else {
            textViewCCLayout.setVisibility(View.GONE);
        }
        String stringDate = messagesResult.getCreatedAt();
        DateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        DateFormat viewFormat = new SimpleDateFormat("h:mm a',' MMMM d yyyy", Locale.getDefault());
        try {
            Date date = parseFormat.parse(stringDate);
            stringDate = viewFormat.format(date);
            textViewMessageDate.setText(stringDate);
        } catch (ParseException e) {
            Timber.e("DateParse error: %s", e.getMessage());
        }

        imageViewStar.setSelected(messagesResult.isStarred());

        String password =
                CTemplarApp.getInstance().getSharedPreferences("pref_user", Context.MODE_PRIVATE).getString("key_password", null);
        textViewContent.setText(decodeContent(messagesResult.getContent(), password));

        if (!messagesResult.isRead())  {
            viewMessageModel.markMessageAsRead(messagesResult.getId());
        }
    }

    @OnClick(R.id.fragment_view_message_reply)
    public void onClickReply() {

    }

    @OnClick(R.id.fragment_view_message_reply_all)
    public void onClickReplyAll() {

    }

    @OnClick(R.id.fragment_view_message_forward)
    public void onClickForward() {

    }

    @OnClick(R.id.fragment_view_message_back)
    public void onClickBack() {
        getActivity().onBackPressed();
    }

    @OnClick(R.id.fragment_view_message_subject_star_image_layout)
    public void onClickStarred() {
        MessagesResult messagesResult = currentMessage;
        if (messagesResult != null) {
            boolean isStarred = !messagesResult.isStarred();
            viewMessageModel.markMessageIsStarred(messagesResult.getId(), isStarred);
        }
    }
}

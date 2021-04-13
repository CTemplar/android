package mobileapp.ctemplar.com.ctemplarapp.message.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import org.bouncycastle.openpgp.PGPException;

import java.io.IOException;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.repository.provider.MessageProvider;
import mobileapp.ctemplar.com.ctemplarapp.security.PGPManager;
import timber.log.Timber;

public class PasswordEncryptedMessageDialogFragment extends DialogFragment {
    private MessageProvider message;
    private TextView errorTextView;
    private ProgressBar progressBar;
    private EditText passwordEditText;
    private Callback callback;

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.password_encrypted_message_dialog, container, false);
        TextView passwordHintTextView = view.findViewById(R.id.password_hint_text_view);
        passwordHintTextView.setText(getString(R.string.password_hint, message.getEncryptionMessage().getPasswordHint()));
        errorTextView = view.findViewById(R.id.error_text_view);
        errorTextView.setVisibility(View.GONE);
        progressBar = view.findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.GONE);
        passwordEditText = view.findViewById(R.id.password_edit_text);
        ImageView closeDialog = view.findViewById(R.id.close_button_image_view);
        closeDialog.setOnClickListener(v -> dismiss());
        Button decryptButton = view.findViewById(R.id.decrypt_button);
        decryptButton.setOnClickListener(v -> {
            String passwordText = passwordEditText.getText().toString();
            progressBar.setVisibility(View.VISIBLE);
            new Thread(() -> {
                Handler mainThreadHandler = new Handler(Looper.getMainLooper());
                String decryptedMessage;
                try {
                    decryptedMessage = PGPManager.decryptGPGUnsafe(message.getContent(), passwordText);
                } catch (PGPException e) {
                    Timber.i(e);
                    mainThreadHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        showDecryptError(getString(R.string.password_is_incorrect));
                    });
                    return;
                } catch (IOException e) {
                    Timber.w(e);
                    mainThreadHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        showDecryptError("IOException: " + e.getMessage());
                    });
                    return;
                } catch (Exception e) {
                    Timber.e(e);
                    mainThreadHandler.post(() -> {
                        progressBar.setVisibility(View.GONE);
                        showDecryptError("Unexpected error: " + e.getMessage());
                    });
                    return;
                }
                mainThreadHandler.post(() -> {
                    dismiss();
                    if (callback != null) {
                        String decryptedSubject;
                        if (message.getSubject() != null) {
                            decryptedSubject = PGPManager.decryptGPG(message.getSubject(), passwordText);
                        } else {
                            decryptedSubject = null;
                        }
                        message.getEncryptionMessage().setPassword(passwordText);
                        callback.onDecrypted(message, decryptedMessage, decryptedSubject);
                    }
                });
            }).start();
        });
        return view;
    }

    private void showDecryptError(String message) {
        errorTextView.setVisibility(View.VISIBLE);
        errorTextView.setText(message);
    }

    public void setMessage(MessageProvider message) {
        this.message = message;
    }

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        passwordEditText.setText("");
        errorTextView.setVisibility(View.GONE);
        progressBar.setVisibility(View.GONE);
    }

    public void show(FragmentManager fragmentManager, MessageProvider item) {
        setMessage(item);
        if (isAdded()) {
            return;
        }
        show(fragmentManager, "DecryptDialogFragment");
    }

    public interface Callback {
        void onDecrypted(MessageProvider message, String decryptedContent, String decryptedSubject);
    }
}

package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import mobileapp.ctemplar.com.ctemplarapp.R;

public class EncryptMessageDialogFragment extends DialogFragment {

    private String dialogPassword;
    private String dialogHint;

    interface OnSetEncryptMessagePassword {
        void onSet(String password, String passwordHint);
    }

    private OnSetEncryptMessagePassword onSetEncryptMessagePassword;

    public void setEncryptMessagePassword(OnSetEncryptMessagePassword onSetEncryptMessagePassword) {
        this.onSetEncryptMessagePassword = onSetEncryptMessagePassword;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_encrypt_message_dialog, container, false);

        final EditText messagePasswordEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_password_input);
        final EditText messagePasswordConfirmEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_password_confirm_input);
        final EditText messagePasswordHintEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_password_hint_input);

        ImageView closeDialog = view.findViewById(R.id.fragment_encrypt_message_dialog_close);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSetEncryptMessagePassword.onSet(null, null);
                dismiss();
            }
        });

        if (dialogPassword != null && !dialogPassword.isEmpty()) {
            messagePasswordEditText.setText(dialogPassword);
            messagePasswordConfirmEditText.setText(dialogPassword);
        }
        if (dialogHint != null && !dialogHint.isEmpty()) {
            messagePasswordHintEditText.setText(dialogHint);
        }

        Button encryptButton = view.findViewById(R.id.fragment_encrypt_message_dialog_encrypt);
        encryptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String messagePassword = messagePasswordEditText.getText().toString();
                String messagePasswordConfirm = messagePasswordConfirmEditText.getText().toString();
                String messagePasswordHint = messagePasswordHintEditText.getText().toString();

                if (TextUtils.equals(messagePassword, messagePasswordConfirm) &&
                        !messagePassword.isEmpty() &&
                        messagePassword.length() > 7) {

                    dialogPassword = messagePassword;
                    dialogHint = messagePasswordHint;

                    onSetEncryptMessagePassword.onSet(messagePassword, messagePasswordHint);
                    dismiss();

                } else {
                    Toast.makeText(getActivity(), getString(R.string.error_password_not_match_or_small),
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        return view;
    }
}

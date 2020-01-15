package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import timber.log.Timber;

public class EncryptMessageDialogFragment extends DialogFragment {

    interface OnSetEncryptMessagePassword {
        void onSet(String password, String passwordHint, Integer expireHours);
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
        final EditText messagePasswordExpireDaysEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_expire_days_edit_text);
        final EditText messagePasswordExpireHoursEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_expire_hours_edit_text);

        messagePasswordExpireDaysEditText.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, 5), new InputFilter.LengthFilter(1)
        });
        messagePasswordExpireHoursEditText.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, 24), new InputFilter.LengthFilter(2)
        });

        ImageView closeDialog = view.findViewById(R.id.fragment_encrypt_message_dialog_close);
        closeDialog.setOnClickListener(v -> {
            onSetEncryptMessagePassword.onSet(null, null, null);
            dismiss();
        });

        Button encryptButton = view.findViewById(R.id.fragment_encrypt_message_dialog_encrypt);
        encryptButton.setOnClickListener(v -> {
            String messagePassword = messagePasswordEditText.getText().toString();
            String messagePasswordConfirm = messagePasswordConfirmEditText.getText().toString();
            String messagePasswordHint = messagePasswordHintEditText.getText().toString();
            String messagePasswordExpireDays = messagePasswordExpireDaysEditText.getText().toString();
            String messagePasswordExpireHours = messagePasswordExpireHoursEditText.getText().toString();

            int expire = getHours(messagePasswordExpireDays, messagePasswordExpireHours);

            if (!EditTextUtils.isTextLength(messagePassword, 8, 30)) {
                Toast.makeText(getActivity(), getString(R.string.error_password_message), Toast.LENGTH_SHORT).show();
                return;
            }
            if (!TextUtils.equals(messagePassword, messagePasswordConfirm)) {
                Toast.makeText(getActivity(), getString(R.string.error_password_not_match), Toast.LENGTH_SHORT).show();
                return;
            }

            onSetEncryptMessagePassword.onSet(
                    messagePassword,
                    messagePasswordHint,
                    expire
            );
            dismiss();
        });

        return view;
    }

    private int getHours(String daysString, String hoursString) {
        int days = 0;
        int hours = 0;
        try {
            if (!daysString.isEmpty()) {
                days = Integer.valueOf(daysString);
            }
            if (!hoursString.isEmpty()) {
                hours = Integer.valueOf(hoursString);
            }
        } catch (NumberFormatException e) {
            Timber.e(e);
        }

        return 24 * days + hours;
    }
}

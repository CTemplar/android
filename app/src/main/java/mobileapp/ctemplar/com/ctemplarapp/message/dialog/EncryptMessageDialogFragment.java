package mobileapp.ctemplar.com.ctemplarapp.message.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.message.InputFilterMinMax;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import timber.log.Timber;

public class EncryptMessageDialogFragment extends DialogFragment {

    public interface OnSetEncryptMessagePassword {
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

        final TextInputLayout passwordInputLayout = view.findViewById(R.id.fragment_encrypt_message_dialog_password_input_layout);
        final TextInputEditText passwordEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_password_input);
        final TextInputLayout passwordConfirmInputLayout = view.findViewById(R.id.fragment_encrypt_message_dialog_password_confirm_input_layout);
        final TextInputEditText passwordConfirmEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_password_confirm_input);
        final TextInputEditText passwordHintEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_password_hint_input);
        final EditText passwordExpireDaysEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_expire_days_edit_text);
        final EditText passwordExpireHoursEditText = view.findViewById(R.id.fragment_encrypt_message_dialog_expire_hours_edit_text);
        final Button encryptButton = view.findViewById(R.id.fragment_encrypt_message_dialog_encrypt);

        final int minPassword = getResources().getInteger(R.integer.restriction_password_min);
        final int maxPassword = getResources().getInteger(R.integer.restriction_password_max);

        passwordExpireDaysEditText.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, 5), new InputFilter.LengthFilter(1)
        });
        passwordExpireHoursEditText.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, 24), new InputFilter.LengthFilter(2)
        });

        ImageView closeDialog = view.findViewById(R.id.fragment_encrypt_message_dialog_close);
        closeDialog.setOnClickListener(v -> {
            onSetEncryptMessagePassword.onSet(null, null, null);
            dismiss();
        });

        encryptButton.setOnClickListener(v -> {
            String messagePassword = EditTextUtils.getText(passwordEditText);
            String messagePasswordConfirm = EditTextUtils.getText(passwordConfirmEditText);
            String messagePasswordHint = EditTextUtils.getText(passwordHintEditText);
            String messagePasswordExpireDays = EditTextUtils.getText(passwordExpireDaysEditText);
            String messagePasswordExpireHours = EditTextUtils.getText(passwordExpireHoursEditText);

            int expire = getHours(messagePasswordExpireDays, messagePasswordExpireHours);
            if (messagePassword.length() < minPassword) {
                passwordInputLayout.setError(getString(R.string.error_password_small));
                return;
            }
            if (messagePassword.length() > maxPassword) {
                passwordInputLayout.setError(getString(R.string.error_password_big));
                return;
            }
            if(!TextUtils.equals(messagePassword, messagePasswordConfirm)) {
                passwordConfirmInputLayout.setError(getString(R.string.error_password_not_match));
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
                days = Integer.parseInt(daysString);
            }
            if (!hoursString.isEmpty()) {
                hours = Integer.parseInt(hoursString);
            }
        } catch (NumberFormatException e) {
            Timber.e(e);
        }

        return 24 * days + hours;
    }
}

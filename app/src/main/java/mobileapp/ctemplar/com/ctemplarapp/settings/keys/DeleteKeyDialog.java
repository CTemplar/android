package mobileapp.ctemplar.com.ctemplarapp.settings.keys;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentDialogDeleteKeyBinding;
import mobileapp.ctemplar.com.ctemplarapp.utils.EditTextUtils;
import timber.log.Timber;

public class DeleteKeyDialog extends DialogFragment {
    private OnApplyClickListener onApplyClickListener;

    private FragmentDialogDeleteKeyBinding binding;

    interface OnApplyClickListener {
        void onDeleteKeyClick();
    }

    public void setOnApplyClickListener(OnApplyClickListener onApplyClickListener) {
        this.onApplyClickListener = onApplyClickListener;
    }

    @Override
    public void onResume() {
        super.onResume();
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        getDialog().getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        getDialog().getWindow().setStatusBarColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentDialogDeleteKeyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding.closeButtonImageView.setOnClickListener(v -> dismiss());
        binding.confirmationEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                Timber.i("Eq: %s", s);
                binding.confirmationButton.setEnabled(s.toString().equals("CTEMPLAR"));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        binding.confirmationButton.setEnabled(false);
        binding.confirmationButton.setOnClickListener(v -> {
            if (onApplyClickListener != null) {
                onApplyClickListener.onDeleteKeyClick();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogTransparentAnimation);
    }

    public void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.confirmationButton.setEnabled(!loading);
        binding.closeButtonImageView.setEnabled(!loading);
    }

    public String getPassword() {
        return EditTextUtils.getText(binding.passwordEditText);
    }
}

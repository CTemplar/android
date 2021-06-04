package com.ctemplar.app.fdroid.settings.keys;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.databinding.FragmentDialogDeleteKeyBinding;

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
        getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
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
                binding.confirmationButton.setEnabled(s.equals("CTEMPLAR"));
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
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }
}

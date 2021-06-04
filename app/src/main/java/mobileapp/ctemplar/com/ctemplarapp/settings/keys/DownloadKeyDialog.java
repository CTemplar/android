package mobileapp.ctemplar.com.ctemplarapp.settings.keys;


import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.jetbrains.annotations.NotNull;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentDialogDownloadKeyBinding;

public class DownloadKeyDialog extends DialogFragment {
    private OnApplyClickListener onApplyClickListener;

    private FragmentDialogDownloadKeyBinding binding;

    interface OnApplyClickListener {
        void onDownloadPublicKeyClick();

        void onDownloadPrivateKeyClick();
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
        binding = FragmentDialogDownloadKeyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding.closeButtonImageView.setOnClickListener(v -> dismiss());
        binding.downloadPublicKeyButton.setOnClickListener(v -> {
            if (onApplyClickListener != null) {
                onApplyClickListener.onDownloadPublicKeyClick();
            }
        });
        binding.downloadPrivateKeyButton.setOnClickListener(v -> {
            if (onApplyClickListener != null) {
                onApplyClickListener.onDownloadPrivateKeyClick();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }
}

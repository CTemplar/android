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
import mobileapp.ctemplar.com.ctemplarapp.databinding.FragmentDialogMakeAsPrimaryKeyBinding;

public class MakeAsPrimaryKeyDialog extends DialogFragment {
    private OnApplyClickListener onApplyClickListener;

    private FragmentDialogMakeAsPrimaryKeyBinding binding;

    interface OnApplyClickListener {
        void onMakeAsPrimaryKeyClick();
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
        binding = FragmentDialogMakeAsPrimaryKeyBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull @NotNull View view, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        binding.closeButtonImageView.setOnClickListener(v -> dismiss());
        binding.makeAsPrimaryButton.setOnClickListener(v -> {
            if (onApplyClickListener != null) {
                onApplyClickListener.onMakeAsPrimaryKeyClick();
            }
        });
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getActivity(), R.style.DialogAnimation);
    }

    public void setLoading(boolean loading) {
        binding.progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        binding.makeAsPrimaryButton.setEnabled(!loading);
        binding.closeButtonImageView.setEnabled(!loading);
    }

}

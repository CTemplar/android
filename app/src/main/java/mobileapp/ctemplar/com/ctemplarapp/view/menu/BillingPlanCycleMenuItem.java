package mobileapp.ctemplar.com.ctemplarapp.view.menu;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.databinding.SwitchWithTitleMenuItemBinding;

public class BillingPlanCycleMenuItem extends FrameLayout {
    private SwitchWithTitleMenuItemBinding binding;
    private OnPlanCycleChangeListener listener;

    public BillingPlanCycleMenuItem(@NonNull Context context) {
        super(context);
        init();
    }

    public BillingPlanCycleMenuItem(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public BillingPlanCycleMenuItem(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater inflater = LayoutInflater.from(getContext());
        binding = SwitchWithTitleMenuItemBinding.inflate(inflater, this, false);
        addView(binding.getRoot());
        binding.switchCompat.setOnCheckedChangeListener((buttonView, isChecked) -> {
            binding.titleTextView.setText(getContext().getString(isChecked ? R.string.yearly : R.string.monthly));
            if (listener != null) {
                listener.onPlanCycleChange(isChecked);
            }
        });
    }

    public void setIsYearly(boolean yearly) {
        binding.switchCompat.setChecked(yearly);
    }

    public void setOnChangeListener(OnPlanCycleChangeListener listener) {
        this.listener = listener;
    }

    public interface OnPlanCycleChangeListener {
        void onPlanCycleChange(boolean isYearly);
    }
}

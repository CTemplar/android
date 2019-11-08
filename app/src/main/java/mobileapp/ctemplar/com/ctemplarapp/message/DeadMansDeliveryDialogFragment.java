package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.InputFilter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import mobileapp.ctemplar.com.ctemplarapp.R;
import timber.log.Timber;

public class DeadMansDeliveryDialogFragment extends DialogFragment {

    private OnScheduleDeadMansDelivery onScheduleDeadMansDelivery;

    interface OnScheduleDeadMansDelivery {
        void onSchedule(Long timeInHours);
    }

    public void setOnScheduleDeadMansDelivery(OnScheduleDeadMansDelivery onScheduleDeadMansDelivery) {
        this.onScheduleDeadMansDelivery = onScheduleDeadMansDelivery;
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
        final View view = inflater.inflate(R.layout.fragment_dead_mans_dialog, container, false);

        final EditText daysEditText = view.findViewById(R.id.fragment_dead_mans_dialog_days_edit_text);
        final EditText hoursEditText = view.findViewById(R.id.fragment_dead_mans_dialog_hours_edit_text);

        daysEditText.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, 9999), new InputFilter.LengthFilter(4)
        });
        hoursEditText.setFilters(new InputFilter[] {
                new InputFilterMinMax(0, 9999), new InputFilter.LengthFilter(4)
        });

        ImageView closeDialog = view.findViewById(R.id.fragment_dead_mans_dialog_close);
        closeDialog.setOnClickListener(v -> {
            onScheduleDeadMansDelivery.onSchedule(null);
            dismiss();
        });

        Button scheduleButton = view.findViewById(R.id.fragment_dead_mans_dialog_schedule);
        scheduleButton.setOnClickListener(v -> {
            String daysString = daysEditText.getText().toString();
            String hoursString = hoursEditText.getText().toString();
            long timer = getHours(daysString, hoursString);

            onScheduleDeadMansDelivery.onSchedule(timer);
            dismiss();
        });

        return view;
    }

    private long getHours(String daysString, String hoursString) {
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

package com.ctemplar.app.fdroid.message;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;

import com.ctemplar.app.fdroid.R;
import com.ctemplar.app.fdroid.utils.DateUtils;

public class DelayedDeliveryDialogFragment extends DialogFragment {
    private Calendar calendar = Calendar.getInstance();

    interface OnScheduleDelayedDelivery {
        void onSchedule(Date date);
    }

    private OnScheduleDelayedDelivery onScheduleDelayedDelivery;

    public void setOnScheduleDelayedDelivery(OnScheduleDelayedDelivery onScheduleDelayedDelivery) {
        this.onScheduleDelayedDelivery = onScheduleDelayedDelivery;
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
        final View view = inflater.inflate(R.layout.fragment_delayed_message_dialog, container, false);

        final ImageView closeDialog = view.findViewById(R.id.fragment_delayed_message_dialog_close);
        closeDialog.setOnClickListener(v -> {
            onScheduleDelayedDelivery.onSchedule(null);
            dismiss();
        });

        final Button scheduleButton = view.findViewById(R.id.fragment_delayed_message_dialog_schedule);
        scheduleButton.setOnClickListener(v -> {
            if (validate()) {
                onScheduleDelayedDelivery.onSchedule(calendar.getTime());
                dismiss();
            }
        });

        final TextView dateTextView = view.findViewById(R.id.fragment_delayed_message_dialog_input_date);
        final TextView timeTextView = view.findViewById(R.id.fragment_delayed_message_dialog_input_time);

        dateTextView.setText(DateUtils.dateFormat(calendar.getTimeInMillis()));
        timeTextView.setText(DateUtils.timeFormat(calendar.getTimeInMillis()));

        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        final int currentHoursOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);

        dateTextView.setOnClickListener(v -> {
            final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), 0, (view12, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                dateTextView.setText(DateUtils.dateFormat(calendar.getTimeInMillis()));
                validate();
            }, currentYear, currentMonth, currentDayOfMonth);
            datePickerDialog.show();
        });

        timeTextView.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), (view1, hourOfDay, minute) -> {
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                timeTextView.setText(DateUtils.timeFormat(calendar.getTimeInMillis()));
                validate();
            }, currentHoursOfDay, currentMinute, false);
            timePickerDialog.show();
        });

        return view;
    }

    private boolean validate() {
        Calendar nowCalendar = Calendar.getInstance();
        if (calendar.getTimeInMillis() < nowCalendar.getTimeInMillis()) {
            calendar = nowCalendar;
            Toast.makeText(getActivity(), R.string.txt_selected_datetime_is_past, Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}

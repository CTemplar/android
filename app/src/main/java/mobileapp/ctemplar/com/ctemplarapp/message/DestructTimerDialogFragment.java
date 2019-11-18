package mobileapp.ctemplar.com.ctemplarapp.message;

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

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

public class DestructTimerDialogFragment extends DialogFragment {

    private Calendar calendar = Calendar.getInstance(AppUtils.getTimeZone());

    interface OnScheduleDestructTimerDelivery {
        void onSchedule(Long timeInMilliseconds);
    }

    private OnScheduleDestructTimerDelivery onScheduleDestructTimerDelivery;

    public void setOnScheduleDestructTimerDelivery(OnScheduleDestructTimerDelivery onScheduleDestructTimerDelivery) {
        this.onScheduleDestructTimerDelivery = onScheduleDestructTimerDelivery;
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
        final View view = inflater.inflate(R.layout.fragment_destruct_message_dialog, container, false);

        ImageView closeDialog = view.findViewById(R.id.fragment_destruct_message_dialog_close);
        closeDialog.setOnClickListener(v -> {
        onScheduleDestructTimerDelivery.onSchedule(null);
        dismiss();
        });

        Button scheduleButton = view.findViewById(R.id.fragment_destruct_message_dialog_schedule);
        scheduleButton.setOnClickListener(v -> {
            if (validate()) {
                Calendar timezoneCalendar = Calendar.getInstance();
                timezoneCalendar.setTimeInMillis(
                        calendar.getTimeInMillis() - AppUtils.timezoneOffsetInMillis()
                );
                onScheduleDestructTimerDelivery.onSchedule(timezoneCalendar.getTimeInMillis());
                dismiss();
            }
        });

        final TextView dateTextView = view.findViewById(R.id.fragment_destruct_message_dialog_input_date);
        final TextView timeTextView = view.findViewById(R.id.fragment_destruct_message_dialog_input_time);

        dateTextView.setText(AppUtils.dateFormat(calendar.getTimeInMillis()));
        timeTextView.setText(AppUtils.timeFormat(calendar.getTimeInMillis()));

        final int currentYear = calendar.get(Calendar.YEAR);
        final int currentMonth = calendar.get(Calendar.MONTH);
        final int currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        final int currentHoursOfDay = calendar.get(Calendar.HOUR_OF_DAY);
        final int currentMinute = calendar.get(Calendar.MINUTE);

        dateTextView.setOnClickListener(v -> {
            final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), 0, (view12, year, month, dayOfMonth) -> {
                calendar.set(year, month, dayOfMonth);
                dateTextView.setText(AppUtils.dateFormat(calendar.getTimeInMillis()));
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
                timeTextView.setText(AppUtils.timeFormat(calendar.getTimeInMillis()));
                validate();
            }, currentHoursOfDay, currentMinute, false);
            timePickerDialog.show();
        });

        return view;
    }

    private boolean validate() {
        Calendar nowCalendar = Calendar.getInstance(AppUtils.getTimeZone());
        if (calendar.getTimeInMillis() < nowCalendar.getTimeInMillis()) {
            calendar = nowCalendar;
            Toast.makeText(getActivity(), getString(R.string.txt_selected_datetime_is_past), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}

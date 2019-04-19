package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

import static mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils.timezoneOffsetInMillis;

public class DestructTimerDialogFragment extends DialogFragment {

    private Calendar calendar = Calendar.getInstance();

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
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            onScheduleDestructTimerDelivery.onSchedule(null);
            dismiss();
            }
        });

        Button scheduleButton = view.findViewById(R.id.fragment_destruct_message_dialog_schedule);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    int offsetInHours = -timezoneOffsetInMillis() / 1000 / 60 / 60;
                    calendar.add(Calendar.HOUR_OF_DAY, offsetInHours);
                    onScheduleDestructTimerDelivery.onSchedule(calendar.getTimeInMillis());
                    dismiss();
                }
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

        dateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), 0, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        calendar.set(year, month, dayOfMonth);
                        if (validate()) {
                            dateTextView.setText(AppUtils.dateFormat(calendar.getTimeInMillis()));
                        }
                    }
                }, currentYear, currentMonth, currentDayOfMonth);
                datePickerDialog.show();
            }
        });

        timeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        int year = calendar.get(Calendar.YEAR);
                        int month = calendar.get(Calendar.MONTH);
                        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

                        calendar.set(year, month, dayOfMonth, hourOfDay, minute);
                        if (validate()) {
                            timeTextView.setText(AppUtils.timeFormat(calendar.getTimeInMillis()));
                        }
                    }
                }, currentHoursOfDay, currentMinute, false);
                timePickerDialog.show();
            }
        });

        return view;
    }

    private boolean validate() {
        Calendar nowCalendar = Calendar.getInstance();
        if (calendar.getTimeInMillis() < nowCalendar.getTimeInMillis()) {
            calendar = nowCalendar;
            Toast.makeText(getActivity(), getString(R.string.txt_selected_datetime_is_past), Toast.LENGTH_SHORT).show();
            return false;
        } else {
            return true;
        }
    }
}

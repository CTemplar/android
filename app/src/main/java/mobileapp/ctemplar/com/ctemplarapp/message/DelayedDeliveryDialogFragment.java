package mobileapp.ctemplar.com.ctemplarapp.message;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

import mobileapp.ctemplar.com.ctemplarapp.R;
import mobileapp.ctemplar.com.ctemplarapp.folders.AddFolderActivity;
import mobileapp.ctemplar.com.ctemplarapp.folders.ManageFoldersActivity;
import mobileapp.ctemplar.com.ctemplarapp.main.FilterDialogFragment;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResponse;
import mobileapp.ctemplar.com.ctemplarapp.net.response.Folders.FoldersResult;
import mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils;

import static mobileapp.ctemplar.com.ctemplarapp.utils.AppUtils.offsetFromCalendar;

public class DelayedDeliveryDialogFragment extends DialogFragment {

    private Calendar calendar = Calendar.getInstance();

    interface OnScheduleDelayedDelivery {
        void onSchedule(Long timeInMilliseconds);
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

        ImageView closeDialog = view.findViewById(R.id.fragment_delayed_message_dialog_close);
        closeDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScheduleDelayedDelivery.onSchedule(null);
                dismiss();
            }
        });

        Button scheduleButton = view.findViewById(R.id.fragment_delayed_message_dialog_schedule);
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    calendar.add(Calendar.HOUR_OF_DAY, -offsetFromCalendar(calendar));
                    onScheduleDelayedDelivery.onSchedule(calendar.getTimeInMillis());
                    dismiss();
                }
            }
        });

        final TextView dateTextView = view.findViewById(R.id.fragment_delayed_message_dialog_input_date);
        final TextView timeTextView = view.findViewById(R.id.fragment_delayed_message_dialog_input_time);

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

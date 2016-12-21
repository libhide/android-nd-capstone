package com.ratik.todone.ui;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;

/**
 * Created by Ratik on 17/12/16.
 */

public class FormDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimeSetListener timeSetListener;
    private OnTimeSetCancel timeSetCancel;

    // Make sure the parent activity implements
    // OnTimeSetListener
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.timeSetListener = (OnTimeSetListener) context;
            this.timeSetCancel = (OnTimeSetCancel) context;
        } catch (final ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must implement OnTimeSetListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        timeSetCancel.onTimeSetCancel();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        // Do something with the time chosen by the user
        timeSetListener.onTimeSet(hourOfDay, minute);
    }
}

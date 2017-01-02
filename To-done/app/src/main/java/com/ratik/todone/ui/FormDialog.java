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

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.Calendar;

/**
 * Created by Ratik on 17/12/16.
 */

public class FormDialog extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

    private OnTimeSetListener timeSetListener;
    private OnTimeSetCancel timeSetCancel;

    private FirebaseAnalytics firebaseAnalytics;

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
        // Obtain the FirebaseAnalytics instance.
        firebaseAnalytics = FirebaseAnalytics.getInstance(getActivity());

        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        // Analytics
        Bundle bundle = new Bundle();
        bundle.putBoolean("time_set", false);
        firebaseAnalytics.logEvent("create_todo", bundle);

        timeSetCancel.onTimeSetCancel();
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minute) {
        // Analytics
        Bundle bundle = new Bundle();
        bundle.putBoolean("time_set", true);
        firebaseAnalytics.logEvent("create_todo", bundle);

        // Do something with the time chosen by the user
        Calendar c = Calendar.getInstance();
        if (hourOfDay < c.get(Calendar.HOUR_OF_DAY)) {
            // day's changing
            int currentDay = c.get(Calendar.DATE);
            c.set(Calendar.DATE, currentDay + 1);
        }

        c.set(Calendar.HOUR_OF_DAY, hourOfDay);
        c.set(Calendar.MINUTE, minute);
        timeSetListener.onTimeSet(c);
    }
}

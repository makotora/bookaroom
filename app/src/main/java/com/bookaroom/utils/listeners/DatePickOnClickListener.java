package com.bookaroom.utils.listeners;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;
import java.util.TimeZone;

public class DatePickOnClickListener implements View.OnClickListener {
    private Context context;
    private DatePickerDialog.OnDateSetListener onDateSetListener;

    public DatePickOnClickListener(Context context, DatePickerDialog.OnDateSetListener onDateSetListener)
    {
        this.context = context;
        this.onDateSetListener = onDateSetListener;
    }

    @Override
    public void onClick(View v) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());

        DatePickerDialog dialog = new DatePickerDialog(context, this.onDateSetListener,
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }
}
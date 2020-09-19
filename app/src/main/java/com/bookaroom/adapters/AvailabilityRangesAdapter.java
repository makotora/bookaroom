package com.bookaroom.adapters;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;

import com.bookaroom.R;
import com.bookaroom.adapters.data.AvailabilityRange;
import com.bookaroom.utils.listeners.DatePickOnClickListener;
import com.bookaroom.utils.Utils;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AvailabilityRangesAdapter extends ArrayAdapter<AvailabilityRange> implements ListAdapter {

    private static List<AvailabilityRange> availabilityRanges;
    private int resource;

    public AvailabilityRangesAdapter(Context context, int resource, List<AvailabilityRange> availabilityRanges) {
        super(context, resource, availabilityRanges);
        this.resource = resource;
        this.availabilityRanges = availabilityRanges;
    }

    @Override
    public int getCount() {
        return availabilityRanges.size();
    }

    @Override
    public AvailabilityRange getItem(int pos) {
        return availabilityRanges.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(this.resource, null);
        }

        AvailabilityRange availabilityRange = getItem(position);

        EditText edtFrom = (EditText)view.findViewById(R.id.edtFrom);
        EditText edtTo = (EditText)view.findViewById(R.id.edtTo);

        setupFromToDatePickers(availabilityRange, edtFrom, edtTo);

        edtFrom.setText(getDateString(availabilityRange.getFrom()));
        edtTo.setText(getDateString(availabilityRange.getTo()));

        setupSelfRemoveButton(view, position);

        return view;
    }

    private void setupFromToDatePickers(AvailabilityRange availabilityRange, EditText edtFrom, EditText edtTo) {
        edtFrom.setOnClickListener(new DatePickOnClickListener(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                availabilityRange.setFrom(getDate(day, month + 1, year));
                notifyDataSetChanged();
            }
        }));

        edtTo.setOnClickListener(new DatePickOnClickListener(getContext(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                availabilityRange.setTo(getDate(day, month + 1, year));
                notifyDataSetChanged();
            }
        }));
    }

    private void setupSelfRemoveButton(View view, int position) {
        Button removeSelfButton = (Button)view.findViewById(R.id.btnRemoveAvailabilityDates);
        removeSelfButton.setTag(position);

        removeSelfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                availabilityRanges.remove(pos);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public void add(AvailabilityRange availabilityRange) {
        availabilityRanges.add(availabilityRange);
    }

    @Override
    public void remove(AvailabilityRange availabilityRange) {
        availabilityRanges.remove(availabilityRange);
    }


    private Date getDate(int day, int month, int year) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, day);
        cal.set(Calendar.MONTH, month - 1);
        cal.set(Calendar.YEAR, year);

        return cal.getTime();
    }

    private String getDateString(Date date) {
        if (date == null) return "";

        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        return padDateDigit(String.valueOf(cal.get(Calendar.DAY_OF_MONTH))) + "/" + padDateDigit(String.valueOf(cal.get(Calendar.MONTH) + 1)) + "/" + String.valueOf(cal.get(Calendar.YEAR));
    }

    private String padDateDigit(String digitString) {
        return Utils.padStringLeft(digitString, '0', 2);
    }

}

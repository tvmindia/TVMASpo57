package com.tech.thrithvam.spoffice;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EnquiryInput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_input);
        //Spinner
        ArrayList<String> statisticsDuration = new ArrayList<String>();
        statisticsDuration.add("Mr.");
        statisticsDuration.add("Ms.");
        statisticsDuration.add("Mrs.");
        statisticsDuration.add("Miss.");
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_black, statisticsDuration);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        Spinner contactTitleSpinner =(Spinner)findViewById(R.id.contact_title);
        contactTitleSpinner.setAdapter(dataAdapter);
    }
    public void getDates(View view){
        final TextView requiredDate=(TextView)view;
        final Calendar today = Calendar.getInstance();
        final Calendar selectedDate=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDate.set(Calendar.YEAR, year);
                selectedDate.set(Calendar.MONTH, monthOfYear);
                selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //Setting display text-------
                SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                requiredDate.setText(formatted.format(selectedDate.getTime()));
            }
        };
        new DatePickerDialog(EnquiryInput.this, dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
    }
}

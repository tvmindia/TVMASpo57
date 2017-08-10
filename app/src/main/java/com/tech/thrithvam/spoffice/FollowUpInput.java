package com.tech.thrithvam.spoffice;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class FollowUpInput extends AppCompatActivity {
    Calendar selectedDateTime =Calendar.getInstance();
    Calendar today = Calendar.getInstance();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up_input);
    }
    public void getDates(View view){
        final TextView selectedDate=(TextView)view;

        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                selectedDateTime.set(Calendar.YEAR, year);
                selectedDateTime.set(Calendar.MONTH, monthOfYear);
                selectedDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                //Setting display text-------
                SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                selectedDate.setText(formatted.format(selectedDateTime.getTime()));
            }
        };
        new DatePickerDialog(FollowUpInput.this, dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void getTime(View view){
        final TextView selectedTime=(TextView)view;
        //Select time-------------------------------
        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                selectedDateTime.set(Calendar.HOUR_OF_DAY,hourOfDay);
                selectedDateTime.set(Calendar.MINUTE,minute);
                //Setting display text-------
                SimpleDateFormat formatted = new SimpleDateFormat("hh:mm a", Locale.US);
                selectedTime.setText(formatted.format(selectedDateTime.getTime()));
                /*SimpleDateFormat formattedForServer = new SimpleDateFormat("dd-MMM-yyyy HH:mm", Locale.US);
                dateTimeGlobal=formattedForServer.format(eventDateTime.getTime());*/
            }
        };
        TimePickerDialog timePickerDialog=new TimePickerDialog(FollowUpInput.this,timeSetListener,today.get(Calendar.HOUR_OF_DAY), today.get(Calendar.MINUTE),false);
        timePickerDialog.setTitle(R.string.select_time);
        /*timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                getResources().getString(R.string.no_time),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        eventDateTime.set(Calendar.HOUR_OF_DAY,0);
                        eventDateTime.set(Calendar.MINUTE,0);
                        //Setting display text-------
                        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                        dateTime.setText(formatted.format(eventDateTime.getTime()));
                        SimpleDateFormat formattedForServer = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
                        dateTimeGlobal=formattedForServer.format(eventDateTime.getTime());
                    }
                });*/
        timePickerDialog.show();
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(FollowUpInput.this,Enquiries.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
       // super.onBackPressed();
    }
}

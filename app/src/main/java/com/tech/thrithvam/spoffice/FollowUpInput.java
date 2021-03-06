package com.tech.thrithvam.spoffice;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

import com.unstoppable.submitbuttonview.SubmitButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class FollowUpInput extends AppCompatActivity {
    Calendar selectedDateTime =Calendar.getInstance();
    Calendar today = Calendar.getInstance();
    ArrayList<View> inputFields=new ArrayList<>();
    String enquiryID;
    String followUpID;
    SharedPreferences sharedpreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up_input);
        //Status spinner
        ArrayList<String> followUpStatusList = new ArrayList<String>();
        followUpStatusList.add(getResources().getString(R.string.open));
        followUpStatusList.add(getResources().getString(R.string.closed));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, followUpStatusList);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner_black);
        Spinner followUpStatusSpinner =(Spinner)findViewById(R.id.status_spinner);
        followUpStatusSpinner.setAdapter(dataAdapter);

        //Input Fields
        inputFields.add(findViewById(R.id.select_date));//0
        inputFields.add(findViewById(R.id.select_time));//1
        inputFields.add(findViewById(R.id.description));//2
        inputFields.add(findViewById(R.id.status_spinner));//3
        sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        //If follow editing context
        if(getIntent().hasExtra(Common.FOLLOWUPID)){
            followUpID=getIntent().getExtras().getString(Common.FOLLOWUPID);
            getSupportActionBar().setTitle("FollowUp Edit");
            ((TextView)findViewById(R.id.select_date)).setText(getIntent().getExtras().getString(Common.FOLLOWUP_date));
            ((TextView)findViewById(R.id.select_time)).setText(getIntent().getExtras().getString(Common.FOLLOWUP_time));
            ((EditText)findViewById(R.id.description)).setText(getIntent().getExtras().getString(Common.FOLLOWUP_description));
            followUpStatusSpinner.setSelection(dataAdapter.getPosition(getIntent().getExtras().getString(Common.FOLLOWUP_status)));

            ((TextView)findViewById(R.id.enquiry_no)).setText(sharedpreferences.getString(Common.ENQUIRYNO,""));
        }
        else {//New followup context
            //Enquiry info
            ((TextView)findViewById(R.id.enquiry_no)).setText(getIntent().getExtras().getString(Common.ENQUIRYNO));
            enquiryID=getIntent().getExtras().getString(Common.ENQUIRYID);
        }
        //Saving follow up
        final SubmitButton saveButton=(SubmitButton) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validations
                if(((TextView)inputFields.get(0)).getText().toString().equals(getResources().getString(R.string.select_date))){//if date not selected
                    Common.toastMessage(FollowUpInput.this,R.string.give_valid);
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(200);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(3);
                    inputFields.get(0).startAnimation(anim);
                    return;
                }
                if(((TextView)inputFields.get(1)).getText().toString().equals(getResources().getString(R.string.select_time))){//if time not selected
                    Common.toastMessage(FollowUpInput.this,R.string.give_valid);
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(200);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(3);
                    inputFields.get(1).startAnimation(anim);
                    return;
                }
                for(int i=2;i<inputFields.size();i++){
                    if(i==3) break;//values that are not to be validated
                    if(((EditText)inputFields.get(i)).getText().toString().length()==0){//if values are not entered
                        ((EditText)inputFields.get(i)).setError(getResources().getString(R.string.give_valid));
                        return;
                    }
                }
                //Disabling views
                for (int i=0;i<inputFields.size();i++){
                    inputFields.get(i).setEnabled(false);
                }
                saveButton.setClickable(false);

                String userName=sharedpreferences.getString(Common.userName,"<error_in_getting_username_from_mobile");
                //Threading------------------------------------------------------------------------------------------------------
                final Common common=new Common();
                String webService="API/FollowUp/InsertUpdateFollowUp";
                String postData;
                if(followUpID!=null) {//edit followup
                    postData = "{\"ID\":\"" + followUpID
                            + "\",\"FollowUpDate\":\"" + ((TextView) findViewById(R.id.select_date)).getText().toString()
                            + "\",\"FollowUpTime\":\"" + ((TextView) findViewById(R.id.select_time)).getText().toString()
                            + "\",\"Status\":\"" + ((Spinner) findViewById(R.id.status_spinner)).getSelectedItem().toString()
                            + "\",\"Subject\":\"" + ((EditText) findViewById(R.id.description)).getText().toString()
                            + "\",\"commonObj\":{\"UpdatedBy\":\"" + userName + "\"}"
                            + "}";
                }
                else {// new followup
                    postData = "{\"EnquiryID\":\"" + enquiryID
                            + "\",\"FollowUpDate\":\"" + ((TextView) findViewById(R.id.select_date)).getText().toString()
                            + "\",\"FollowUpTime\":\"" + ((TextView) findViewById(R.id.select_time)).getText().toString()
                            + "\",\"Status\":\"" + ((Spinner) findViewById(R.id.status_spinner)).getSelectedItem().toString()
                            + "\",\"ReminderType\":\"" + "MNT"//Mobile notification
                            + "\",\"Subject\":\"" + ((EditText) findViewById(R.id.description)).getText().toString()
                            + "\",\"commonObj\":{\"CreatedBy\":\"" + userName + "\"}"
                            + "}";
                }
                String[] dataColumns={};
                Runnable postThread=new Runnable() {
                    @Override
                    public void run() {
                        saveButton.doResult(true);
                        //Save success
                        if(followUpID!=null) {//edit
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(FollowUpInput.this, FollowUp.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.putExtra(Common.ENQUIRYID,sharedpreferences.getString(Common.ENQUIRYID,""));
                                    intent.putExtra(Common.ENQUIRYNO,sharedpreferences.getString(Common.ENQUIRYNO,""));
                                    startActivity(intent);
                                }
                            }, 1500);
                        }
                        else {//new insert
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    if(getIntent().getExtras().getString(Common.FROM).equals("followup")){//came from follow up
                                            Intent intent=new Intent(FollowUpInput.this,FollowUp.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra(Common.ENQUIRYID,getIntent().getExtras().getString(Common.ENQUIRYID));
                                            intent.putExtra(Common.ENQUIRYNO,getIntent().getExtras().getString(Common.ENQUIRYNO));
                                            startActivity(intent);
                                    }
                                    else {//Came from new enquiry input
                                        Intent intent=new Intent(FollowUpInput.this,Enquiries.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                    }
                                }
                            }, 1500);
                        }
                    }
                };
                Runnable postThreadFailed=new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0;i<inputFields.size();i++){
                            inputFields.get(i).setEnabled(true);
                        }
                        Common.toastMessage(FollowUpInput.this,common.msg);
                        Common.toastMessage(FollowUpInput.this, R.string.failed_try_again);
                        saveButton.doResult(false);
                        //Change button after a while
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                saveButton.reset();
                                saveButton.setClickable(true);
                            }
                        }, 1500);
                    }};

                common.AsynchronousThread(FollowUpInput.this,
                        webService,
                        postData,
                        null,
                        dataColumns,
                        postThread,
                        postThreadFailed);
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            }
        });
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
        if(getIntent().getExtras().getString(Common.FROM).equals("followup")){
            if(followUpID!=null) {//edit mode
                super.onBackPressed();
            }
            else {
                Intent intent=new Intent(FollowUpInput.this,FollowUp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.putExtra(Common.ENQUIRYID,getIntent().getExtras().getString(Common.ENQUIRYID));
                intent.putExtra(Common.ENQUIRYNO,getIntent().getExtras().getString(Common.ENQUIRYNO));
                startActivity(intent);
            }
        }
        else {
            Intent intent=new Intent(FollowUpInput.this,Enquiries.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            Intent intent=new Intent(this,HomeScreen.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

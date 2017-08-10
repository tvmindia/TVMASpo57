package com.tech.thrithvam.spoffice;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.dd.CircularProgressButton;

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

        //Save enquiry
        final CircularProgressButton saveButton=(CircularProgressButton)findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                usernameInput.clearFocus();
//                passwordInput.clearFocus();
//                usernameInput.setEnabled(false);
//                passwordInput.setEnabled(false);
                saveButton.setClickable(false);
                //Loading
                saveButton.setIndeterminateProgressMode(true);
                saveButton.setProgress(50);

                //Threading------------------------------------------------------------------------------------------------------
                final Common common=new Common();
                String webService="/API/Login/HomeLogin";
                String postData =  "{\"LoginName\":\"" + "albert" + "\",\"Password\":\""+ "albert@123" + "\"}";
//                AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
                String[] dataColumns={};
                Runnable postThread=new Runnable() {
                    @Override
                    public void run() {
                        saveButton.setProgress(100);
                        //Save success
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(EnquiryInput.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                                        .setMessage(R.string.add_followup_q)
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent=new Intent(EnquiryInput.this,FollowUpInput.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra(Common.ENQUIRYID,"");//enquiry id here
                                                startActivity(intent);
                                            }
                                        }).setNegativeButton(R.string.no, null).show();
                            }
                        }, 1500);
                    }
                };
                Runnable postThreadFailed=new Runnable() {
                    @Override
                    public void run() {
                        Common.toastMessage(EnquiryInput.this,common.msg);
                        Common.toastMessage(EnquiryInput.this, R.string.failed_try_again);

                        saveButton.setProgress(-1);
                        //Setting button to refresh when password/email change
//                        usernameInput.setEnabled(true);
//                        passwordInput.setEnabled(true);
                        saveButton.setClickable(true);
                 /*       usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                loginButton.setProgress(0);
                            }
                        });
                        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                loginButton.setProgress(0);
                            }
                        });*/

                    }};

                common.AsynchronousThread(EnquiryInput.this,
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

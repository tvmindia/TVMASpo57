package com.tech.thrithvam.spoffice;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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

import com.dd.CircularProgressButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EnquiryInput extends AppCompatActivity {
    ArrayList<View> inputFields=new ArrayList<>();
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
        dataAdapter.setDropDownViewResource(R.layout.item_spinner_black);
        Spinner contactTitleSpinner =(Spinner)findViewById(R.id.contact_title);
        contactTitleSpinner.setAdapter(dataAdapter);

        //Input Fields
        inputFields.add(findViewById(R.id.date));//0
        inputFields.add(findViewById(R.id.contact_title));//1
        inputFields.add(findViewById(R.id.contact_name));//2
        inputFields.add(findViewById(R.id.client_name));//3
        inputFields.add(findViewById(R.id.mobile));//4
        inputFields.add(findViewById(R.id.email));//5
        inputFields.add(findViewById(R.id.notes));//6

        //Save enquiry
        final CircularProgressButton saveButton=(CircularProgressButton)findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Validations
                if(((TextView)inputFields.get(0)).getText().toString().equals(getResources().getString(R.string.select_date))){//if date not selected
                    Common.toastMessage(EnquiryInput.this,R.string.give_valid);
                    Animation anim = new AlphaAnimation(0.0f, 1.0f);
                    anim.setDuration(200);
                    anim.setStartOffset(20);
                    anim.setRepeatMode(Animation.REVERSE);
                    anim.setRepeatCount(3);
                    inputFields.get(0).startAnimation(anim);
                    return;
                }
                for(int i=2;i<inputFields.size();i++){
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
                //Loading animation
                saveButton.setIndeterminateProgressMode(true);
                saveButton.setProgress(50);
                SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
                String userName=sharedpreferences.getString("UserName","<error_in_getting_username_from_mobile");
                //Threading------------------------------------------------------------------------------------------------------
                final Common common=new Common();
                String webService="/API/Enquiry/InsertUpdateEnquiry";
                String postData =  "{\"EnquiryDate\":\""+((TextView)findViewById(R.id.date)).getText().toString()
                        +"\",\"ContactTitle\":\""+((Spinner)findViewById(R.id.contact_title)).getSelectedItem().toString()
                        +"\",\"ContactName\":\""+((EditText)findViewById(R.id.contact_name)).getText().toString()
                        +"\",\"CompanyName\":\""+((EditText)findViewById(R.id.client_name)).getText().toString()
                        +"\",\"Mobile\":\""+((EditText)findViewById(R.id.mobile)).getText().toString()
                        +"\",\"Email\":\""+((EditText)findViewById(R.id.email)).getText().toString()
                        +"\",\"GeneralNotes\":\""+((EditText)findViewById(R.id.notes)).getText().toString()
                        +"\",\"commonObj\":{\"CreatedBy\":\""+userName+"\"}"
                        +"}";
                String[] dataColumns={};
                Runnable postThread=new Runnable() {
                    @Override
                    public void run() {
                        saveButton.setProgress(100);
                        //Save success
                        final String enquiryID,enquiryNo;
                        try {
                            JSONObject jsonObject=new JSONObject(common.json);
                            enquiryID= jsonObject.getString("ID");
                            enquiryNo= jsonObject.getString("EnquiryNo");

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                new AlertDialog.Builder(EnquiryInput.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                                        .setMessage(getResources().getString(R.string.add_followup_q,enquiryNo))
                                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent=new Intent(EnquiryInput.this,FollowUpInput.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                intent.putExtra(Common.ENQUIRYID,enquiryID);//enquiry id here
                                                intent.putExtra(Common.ENQUIRYNO,enquiryNo);//enquiry no here
                                                startActivity(intent);
                                            }
                                        }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    Intent intent=new Intent(EnquiryInput.this,Enquiries.class);
                                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                    startActivity(intent);
                                                     }
                                                    })
                                        .setCancelable(false).show();
                            }
                        }, 1500);
                        } catch (JSONException e) {
                            Common.toastMessage(EnquiryInput.this,e.toString());
                            Common.toastMessage(EnquiryInput.this, R.string.failed_try_again);
                        }
                    }
                };
                Runnable postThreadFailed=new Runnable() {
                    @Override
                    public void run() {
                        for (int i=0;i<inputFields.size();i++){
                            inputFields.get(i).setEnabled(true);
                        }
                        Common.toastMessage(EnquiryInput.this,common.msg);
                        Common.toastMessage(EnquiryInput.this, R.string.failed_try_again);
                        saveButton.setProgress(-1);
                        //Change button after a while
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                saveButton.setProgress(0);
                                saveButton.setClickable(true);
                            }
                        }, 1500);
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

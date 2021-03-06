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

import com.unstoppable.submitbuttonview.SubmitButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class EnquiryInput extends AppCompatActivity {
    ArrayList<View> inputFields=new ArrayList<>();
    String enquiryID;
    Spinner employeeSpinner;
    ArrayList<String[]> employeeList=new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_input);
        getEmployees();

        //Spinner
        ArrayList<String> statisticsDuration = new ArrayList<String>();
        statisticsDuration.add("Mr.");
        statisticsDuration.add("Ms.");
        statisticsDuration.add("Mrs.");
        statisticsDuration.add("Miss.");
        ArrayAdapter<String> dataAdapterSpinner = new ArrayAdapter<String>(this, R.layout.item_spinner_black, statisticsDuration);
        dataAdapterSpinner.setDropDownViewResource(R.layout.item_spinner_black);
        Spinner contactTitleSpinner =(Spinner)findViewById(R.id.contact_title);
        contactTitleSpinner.setAdapter(dataAdapterSpinner);

        //Status spinner
        ArrayList<String> enquiryStatusList = new ArrayList<String>();
        enquiryStatusList.add(getResources().getString(R.string.open));
        enquiryStatusList.add(getResources().getString(R.string.converted));
        enquiryStatusList.add(getResources().getString(R.string.not_converted));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner, enquiryStatusList);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner_black);
        final Spinner enquiryStatusSpinner =(Spinner)findViewById(R.id.status_spinner);
        enquiryStatusSpinner.setAdapter(dataAdapter);

        //Input Fields
        inputFields.add(findViewById(R.id.date));//0
        inputFields.add(findViewById(R.id.contact_title));//1
        inputFields.add(findViewById(R.id.contact_name));//2
        inputFields.add(findViewById(R.id.client_name));//3
        inputFields.add(findViewById(R.id.mobile));//4
        inputFields.add(findViewById(R.id.email));//5
        inputFields.add(findViewById(R.id.notes));//6

        //If enquiry editing context
        if(getIntent().hasExtra(Common.ENQUIRYID)){
            enquiryID=getIntent().getExtras().getString(Common.ENQUIRYID);
            getSupportActionBar().setTitle("Edit: "+getIntent().getExtras().getString(Common.ENQUIRYNO));
            ((TextView)findViewById(R.id.date)).setText(getIntent().getExtras().getString(Common.ENQUIRY_date));
            contactTitleSpinner.setSelection(dataAdapterSpinner.getPosition(getIntent().getExtras().getString(Common.ENQUIRY_contactTitle)));
            ((EditText)findViewById(R.id.contact_name)).setText(getIntent().getExtras().getString(Common.ENQUIRY_contactName));
            ((EditText)findViewById(R.id.client_name)).setText(getIntent().getExtras().getString(Common.ENQUIRY_clientName));
            ((EditText)findViewById(R.id.mobile)).setText(getIntent().getExtras().getString(Common.ENQUIRY_mobile));
            ((EditText)findViewById(R.id.email)).setText(getIntent().getExtras().getString(Common.ENQUIRY_email));
            ((EditText)findViewById(R.id.notes)).setText(getIntent().getExtras().getString(Common.ENQUIRY_notes));
            int empPos=0;
            for(int i=0;i<employeeList.size();i++){
                if(employeeList.get(i)[0].equals(getIntent().getExtras().getString(Common.ENQUIRY_enquiryOwnerID))){
                    empPos=i;
                }
            }
            enquiryStatusSpinner.setSelection(empPos);
            String status="";
            if(getIntent().getExtras().getString(Common.ENQUIRY_status).equals("OE")){
                status=getResources().getString(R.string.open);
            }
            else if(getIntent().getExtras().getString(Common.ENQUIRY_status).equals("CE")){
                status=getResources().getString(R.string.converted);
            }
            else if(getIntent().getExtras().getString(Common.ENQUIRY_status).equals("NCE")){
                status=getResources().getString(R.string.not_converted);
            }
            if(dataAdapter.getPosition(status)>=0) {
                enquiryStatusSpinner.setSelection(dataAdapter.getPosition(status));
            }
        }

        //Save enquiry
        final SubmitButton saveButton=(SubmitButton) findViewById(R.id.save_button);
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
                SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
                String userName=sharedpreferences.getString(Common.userName,"<error_in_getting_username_from_mobile>");

                //Enquiry status
                String status="OE";
                if(enquiryStatusSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.open))){
                    status = "OE";
                }
                else if(enquiryStatusSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.converted))){
                    status="CE";
                }
                else if(enquiryStatusSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.not_converted))){
                    status="NCE";
                }

                //Threading------------------------------------------------------------------------------------------------------
                final Common common=new Common();
                String webService="/API/Enquiry/InsertUpdateEnquiry";
                String postData;
                if(enquiryID!=null){//update
                    postData="{\"ID\":\"" + enquiryID
                            +"\",\"EnquiryDate\":\""+((TextView)findViewById(R.id.date)).getText().toString()
                            +"\",\"ContactTitle\":\""+((Spinner)findViewById(R.id.contact_title)).getSelectedItem().toString()
                            +"\",\"ContactName\":\""+((EditText)findViewById(R.id.contact_name)).getText().toString()
                            +"\",\"CompanyName\":\""+((EditText)findViewById(R.id.client_name)).getText().toString()
                            +"\",\"Mobile\":\""+((EditText)findViewById(R.id.mobile)).getText().toString()
                            +"\",\"Email\":\""+((EditText)findViewById(R.id.email)).getText().toString()
                            +"\",\"GeneralNotes\":\""+((EditText)findViewById(R.id.notes)).getText().toString()
                            +"\",\"EnquiryStatus\":\""+status
                            +"\",\"EnquiryOwnerID\":\""+employeeList.get(employeeSpinner.getSelectedItemPosition())[0]
                            +"\",\"userObj\":{\"UserName\":\""+userName+"\"}"
                            +"}";
                }
                else {//insert
                    postData="{\"EnquiryDate\":\""+((TextView)findViewById(R.id.date)).getText().toString()
                            +"\",\"ContactTitle\":\""+((Spinner)findViewById(R.id.contact_title)).getSelectedItem().toString()
                            +"\",\"ContactName\":\""+((EditText)findViewById(R.id.contact_name)).getText().toString()
                            +"\",\"CompanyName\":\""+((EditText)findViewById(R.id.client_name)).getText().toString()
                            +"\",\"Mobile\":\""+((EditText)findViewById(R.id.mobile)).getText().toString()
                            +"\",\"Email\":\""+((EditText)findViewById(R.id.email)).getText().toString()
                            +"\",\"GeneralNotes\":\""+((EditText)findViewById(R.id.notes)).getText().toString()
                            +"\",\"EnquiryStatus\":\""+status
                            +"\",\"EnquiryOwnerID\":\""+employeeList.get(employeeSpinner.getSelectedItemPosition())[0]
                            +"\",\"userObj\":{\"UserName\":\""+userName+"\"}"
                            +"}";
                }
                String[] dataColumns={};
                Runnable postThread=new Runnable() {
                    @Override
                    public void run() {
                        saveButton.doResult(true);
                        //Save success
                        if(enquiryID!=null){//updated enquiry
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent(EnquiryInput.this, Enquiries.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                            | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                            | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    startActivity(intent);
                                    finish();
                                }
                                }, 1500);
                        }
                        else {//new enquiry inserted
                            final String enquiryID, enquiryNo;
                            try {
                                JSONObject jsonObject = new JSONObject(common.json);
                                enquiryID = jsonObject.getString("ID");
                                enquiryNo = jsonObject.getString("EnquiryNo");

                                final Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        new AlertDialog.Builder(EnquiryInput.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                                                .setMessage(getResources().getString(R.string.add_followup_q, enquiryNo))
                                                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                                    @Override
                                                    public void onClick(DialogInterface dialog, int which) {
                                                        Intent intent = new Intent(EnquiryInput.this, FollowUpInput.class);
                                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                        intent.putExtra(Common.ENQUIRYID, enquiryID);
                                                        intent.putExtra(Common.ENQUIRYNO, enquiryNo);
                                                        intent.putExtra(Common.FROM,"enquiries");
                                                        startActivity(intent);
                                                    }
                                                }).setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                Intent intent = new Intent(EnquiryInput.this, Enquiries.class);
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
                                Common.toastMessage(EnquiryInput.this, e.toString());
                                Common.toastMessage(EnquiryInput.this, R.string.failed_try_again);
                            }
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
    void getEmployees(){
        (findViewById(R.id.screen)).setVisibility(View.GONE);
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/Employee/GetEmployeeListForMobile";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String postData = "";
        String[] dataColumns = {"ID",//0
                "Code",//1
                "Name" //2
        };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                //Spinner
                ArrayList<String> employeeNames = new ArrayList<String>();
                for (int i=0;i<common.dataArrayList.size();i++){
                    String[] data=new String[3];
                    data[0]=common.dataArrayList.get(i)[0];
                    data[1]=common.dataArrayList.get(i)[1];
                    data[2]=common.dataArrayList.get(i)[2];
                    employeeList.add(data);
                }
                for(int i=0;i<employeeList.size();i++){
                    employeeNames.add(employeeList.get(i)[1]+"-"+employeeList.get(i)[2]);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(EnquiryInput.this, R.layout.item_spinner_black, employeeNames);
                dataAdapter.setDropDownViewResource(R.layout.item_spinner);
                employeeSpinner =(Spinner)findViewById(R.id.lead_owner);
                employeeSpinner.setAdapter(dataAdapter);
                (findViewById(R.id.screen)).setVisibility(View.VISIBLE);
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(EnquiryInput.this, R.string.failed_server);
                finish();
            }
        };

        common.AsynchronousThread(EnquiryInput.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
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

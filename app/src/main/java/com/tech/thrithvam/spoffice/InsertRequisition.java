package com.tech.thrithvam.spoffice;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.dd.CircularProgressButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class InsertRequisition extends AppCompatActivity {
    TextView requiredDate;
    ArrayList<View> detailItemViews=new ArrayList<>();
    LinearLayout detailItemListView;
    LayoutInflater inflater;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_requisition);
        requiredDate=(TextView)findViewById(R.id.date_value);
        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        requiredDate.setText(formatted.format(Calendar.getInstance().getTime()));
        detailItemListView=(LinearLayout)findViewById(R.id.requisition_detail_list);
        //first detail item
        inflater=getLayoutInflater();
        View firstItemView=inflater.inflate(R.layout.item_requisition_detail_input,null);
        detailItemViews.add(firstItemView);
        detailItemListView.addView(firstItemView);
    }
    public void getDates(View view){
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
        new DatePickerDialog(InsertRequisition.this, dateSetListener, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH)).show();
    }
    public void addMoreDetail(View view)
    {
        View detailItemView=inflater.inflate(R.layout.item_requisition_detail_input,null);
        detailItemViews.add(detailItemView);
        detailItemListView.addView(detailItemView);
        view.setVisibility(View.GONE);
        ((ScrollView)findViewById(R.id.scrollView)).fullScroll(View.FOCUS_DOWN);
    }
    public void submitRequisition(View view){
        //Validations
        EditText title=(EditText)findViewById(R.id.title_value);
        if(title.getText().toString().equals("")){
            title.setError(getResources().getString(R.string.give_valid));
            return;
        }
        EditText companyName=(EditText)findViewById(R.id.company_name_value);
        if(companyName.getText().toString().equals("")){
            companyName.setError(getResources().getString(R.string.give_valid));
            return;
        }
        TextView date=(TextView)findViewById(R.id.date_value);
        for(int i=0;i<detailItemViews.size();i++){
            //only requested quantity is mandatory in site, so
            EditText reqQty=(EditText) detailItemViews.get(i).findViewById(R.id.req_qty);
            if(reqQty.getText().toString().equals("")){
                reqQty.setError(getResources().getString(R.string.give_valid));
                return;
            }
        }
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
        //.setClickable(false);
        final CircularProgressButton saveButton=(CircularProgressButton)findViewById(R.id.approve_button);
        //Loading animation
        saveButton.setIndeterminateProgressMode(true);
        saveButton.setProgress(50);
        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        String userName=sharedpreferences.getString(Common.userName,"<error_in_getting_username_from_mobile>");


        //Threading------------------------------------------------------------------------------------------------------
        final Common common=new Common();
        String webService="/API/Requisition/InsertUpdateRequisition";
        String[] dataColumns={};
        String detailItemsJSON="[";
        for(int i=0;i<detailItemViews.size();i++){
            detailItemsJSON+="{\"MaterialID\":\"00000000-0000-0000-0000-000000000000\"" +
                    ",\"Description\":\"Projector\"" +
                    ",\"ExtendedDescription\":\""+((TextView)detailItemViews.get(i).findViewById(R.id.ext_des)).getText().toString()
                    +"\",\"CurrStock\":\""+((TextView)detailItemViews.get(i).findViewById(R.id.curr_qty)).getText().toString()
                    +"\",\"AppxRate\":\"500.00\""
                    +",\"RequestedQty\":\""+((TextView)detailItemViews.get(i).findViewById(R.id.req_qty)).getText().toString()
                    +"\"},";
        }
        detailItemsJSON=detailItemsJSON.substring(0, detailItemsJSON.lastIndexOf(","));
        detailItemsJSON+="]";
        String postData;
        postData="{\"Title\":\""+title.getText().toString()
                +"\",\"ReqDateFormatted\":\""+date.getText().toString()
                +"\",\"ReqForCompany\":\""+companyName.getText().toString()
                +"\",\"ReqStatus\":\"Open\"" +
                ",\"RequisitionDetailList\":" +
                 detailItemsJSON
                +",\"userObj\":{\"UserName\":\""+userName+"\"}}";
        Runnable postThread=new Runnable() {
            @Override
            public void run() {
                saveButton.setProgress(100);
                String reqNo="";
                try {
                    JSONObject result=new JSONObject(common.json);
                    reqNo=result.getString("ReqNo");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //Save success
                new AlertDialog.Builder(InsertRequisition.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                        .setMessage(getResources().getString(R.string.req_inserted,reqNo))
                        .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(InsertRequisition.this,RequisitionList.class);
                                intent.putExtra(Common.REQUISITIONTYPE,"pending");
                                startActivity(intent);
                            }
                        }).setCancelable(false).show();
                }
        };
        Runnable postThreadFailed=new Runnable() {
            @Override
            public void run() {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

                Common.toastMessage(InsertRequisition.this,common.msg);
                Common.toastMessage(InsertRequisition.this, R.string.failed_try_again);
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

        common.AsynchronousThread(InsertRequisition.this,
                webService,
                postData,
                null,
                dataColumns,
                postThread,
                postThreadFailed);
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
            Intent intent=new Intent(this,HomeScreenNormalUser.class);
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

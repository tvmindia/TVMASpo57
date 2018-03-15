package com.tech.thrithvam.spoffice;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.CircularProgressButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class RequisitionDetails extends AppCompatActivity {
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    CircularProgressButton approveButton;
    View headerDetails;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisition_details);
        getApprovalDetails();
        setTitle("Requisition: "+getIntent().getExtras().getString(Common.REQNO));
        LinearLayout headerView=(LinearLayout)findViewById(R.id.header);
        LayoutInflater inflater=getLayoutInflater();
        headerDetails=inflater.inflate(R.layout.item_requisition_header,null);
        ((TextView)headerDetails.findViewById(R.id.requisition_no)).setText(
                getIntent().getExtras().getString(Common.REQNO).equals("null")?"-":
                        getIntent().getExtras().getString(Common.REQNO));
        ((TextView)headerDetails.findViewById(R.id.date)).setText(
                getIntent().getExtras().getString(Common.REQDATE).equals("null")?"-":
                        getIntent().getExtras().getString(Common.REQDATE));
        ((TextView)headerDetails.findViewById(R.id.status)).setText(
                getIntent().getExtras().getString(Common.REQSTATUS).equals("null")?"-":
                        getIntent().getExtras().getString(Common.REQSTATUS));
       /* ((TextView)headerDetails.findViewById(R.id.value)).setText(
                getIntent().getExtras().getString(Common.REQ).equals("null")?"-":
                        getResources().getString(R.string.rupees,getIntent().getExtras().getString(Common.AMOUNT)));*/
        ((TextView)headerDetails.findViewById(R.id.company_name)).setText(
                getIntent().getExtras().getString(Common.REQCCOMP).equals("null")?"-":
                        getIntent().getExtras().getString(Common.REQCCOMP));

        headerView.addView(headerDetails);
        //getting user name
        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        userName=sharedpreferences.getString(Common.userName,"");
    }
    void getApprovalDetails(){
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "/API/Requisition/GetRequisitionDetailByID";
        String postData = "{\"ID\":\""+getIntent().getExtras().getString(Common.REQID)+"\",\"userObj\":{\"UserName\":\""+userName+"\"}}";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {};
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                ArrayList<String[]> dataArrayList=new ArrayList<>();
                Double total=0.0;
                try {
                    JSONObject records=new JSONObject(common.json);
                    if(records.length()==0){
                        Common.toastMessage(RequisitionDetails.this,"No details Available");
                        findViewById(R.id.no_items).setVisibility(View.VISIBLE);
                        return;
                    }

                    JSONArray jsonObject1= records.getJSONArray("RequisitionDetailList");
                    for (int i = 0; i < jsonObject1.length(); i++) {
                        JSONObject jsonObject2 = jsonObject1.getJSONObject(i);
                        String[] data = new String[5];
                        data[0] = jsonObject2.getString("ID");
                        data[1] = jsonObject2.getString("Description");
                        data[2] = jsonObject2.getString("CurrStock");
                        data[3] = jsonObject2.getString("RequestedQty");
                        data[4] = jsonObject2.getString("AppxRate");
                        dataArrayList.add(data);
                        total+=Double.parseDouble(data[4]);
                    }
                } catch (JSONException e) {
                    Toast.makeText(RequisitionDetails.this, "Some error occurred\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                CustomAdapter adapter=new CustomAdapter(RequisitionDetails.this,dataArrayList,Common.REQUISITIONDETAILLIST);
                ListView requisitionDetailList=(ListView)findViewById(R.id.requisition_detail_list);
                requisitionDetailList.setAdapter(adapter);
                requisitionDetailList.setVisibility(View.VISIBLE);

                TextView totalTextView=(TextView)headerDetails.findViewById(R.id.total);
                totalTextView.setText(getResources().getString(R.string.total_label,String.format(Locale.US,"%.2f",total)));
                totalTextView.setVisibility(View.VISIBLE);
                (headerDetails.findViewById(R.id.approve_button)).setVisibility(View.VISIBLE);
                //approve button
                approveButton = (CircularProgressButton) headerDetails.findViewById(R.id.approve_button);
                if(getIntent().getExtras().getString(Common.REQUISITIONTYPE).equals("pending")) {
                    approveButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new AlertDialog.Builder(RequisitionDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                                    .setMessage(getResources().getString(R.string.approve_q))
                                    .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            approve();
                                        }
                                    }).setNegativeButton(R.string.cancel, null)
                                    .setCancelable(true).show();
                        }
                    });
                }
                else {
                    approveButton.setVisibility(View.GONE);
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(RequisitionDetails.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(RequisitionDetails.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        asyncTasks.add(common.asyncTask);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    void approve(){
        approveButton.setClickable(false);
        //Loading
        approveButton.setIndeterminateProgressMode(true);
        approveButton.setProgress(50);
        //Threading------------------------------------------------------------------------------------------------------
        final Common common=new Common();
        String webService="/API/Requisition/ApproveRequisition";
        String postData = "{\"ID\":\""+getIntent().getExtras().getString(Common.REQID)+"\",\"userObj\":{\"UserName\":\""+userName+"\"}}";
        String[] dataColumns={};
        Runnable postThread=new Runnable() {
            @Override
            public void run() {
                approveButton.setProgress(100);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(RequisitionDetails.this, HomeScreenNormalUser.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }, 2000);
            }
        };
        Runnable postThreadFailed=new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(RequisitionDetails.this,common.msg);
                Common.toastMessage(RequisitionDetails.this, R.string.failed_try_again);
                approveButton.setProgress(-1);
            }};
        common.AsynchronousThread(RequisitionDetails.this,
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
    @Override
    public void onBackPressed() {
        for(int i=0;i<asyncTasks.size();i++){
            asyncTasks.get(i).cancel(true);
        }
        super.onBackPressed();
    }
}

package com.tech.thrithvam.spoffice;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

public class SupplierOrderDetails extends AppCompatActivity {
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    CircularProgressButton approveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_order_details);
        TextView supplierName = (TextView) findViewById(R.id.supplier_name);
        TextView purchaseOrderNo = (TextView) findViewById(R.id.po_no);
        TextView date = (TextView) findViewById(R.id.date);
        TextView amount = (TextView) findViewById(R.id.amount);
        TextView status=(TextView) findViewById(R.id.status);

        supplierName.setText(getIntent().getExtras().getString(Common.SUPPLIERNAME));
        purchaseOrderNo.setText(getIntent().getExtras().getString(Common.PONUMBER));
        date.setText(getIntent().getExtras().getString(Common.PODATE));
        amount.setText((getIntent().getExtras().getString(Common.TOTALAMOUNT).equals("null")?"-":getResources().getString(R.string.rupees,String.format(Locale.US,"%.2f",Double.parseDouble(getIntent().getExtras().getString(Common.TOTALAMOUNT))))));
        status.setText((getIntent().getExtras().getString(Common.POSTATUS).equals("null")?"-":getResources().getString(R.string.status_colon,getIntent().getExtras().getString(Common.POSTATUS))));
        approveButton=(CircularProgressButton)findViewById(R.id.approve_button);
        approveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                approve();
            }
        });
}
    /*void getRequisitionDetails(){
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
                //Double total=0.0;
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
                        String[] data = new String[6];
                        data[0] = jsonObject2.getString("ID");
                        data[1] = jsonObject2.getString("Description");
                        data[2] = jsonObject2.getString("CurrStock");
                        data[3] = jsonObject2.getString("RequestedQty");
                        data[4] = jsonObject2.getString("AppxRate");
                        data[5] = jsonObject2.getString("ExtendedDescription");
                        //total+=(Integer.parseInt(data[3]) * Double.parseDouble(data[4]));
                        //data[6] = Double.toString(Integer.parseInt(data[3]) * Double.parseDouble(data[4]));
                        dataArrayList.add(data);
                    }
                } catch (JSONException e) {
                    Toast.makeText(RequisitionDetails.this, "Some error occurred\n"+ e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                CustomAdapter adapter=new CustomAdapter(RequisitionDetails.this,dataArrayList,Common.REQUISITIONDETAILLIST);
                ListView requisitionDetailList=(ListView)findViewById(R.id.requisition_detail_list);
                requisitionDetailList.setAdapter(adapter);
                requisitionDetailList.setVisibility(View.VISIBLE);

               /* TextView totalTextView=(TextView)headerDetails.findViewById(R.id.total);
                totalTextView.setText(getResources().getString(R.string.total_label,String.format(Locale.US,"%.2f",total)));
                totalTextView.setVisibility(View.VISIBLE);*/
               /* (headerDetails.findViewById(R.id.approve_button)).setVisibility(View.VISIBLE);
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
                    (findViewById(R.id.delete)).setVisibility(View.GONE);
                    (findViewById(R.id.edit)).setVisibility(View.GONE);
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
    }*/
    void approve(){
        approveButton.setClickable(false);
        //Loading
        approveButton.setIndeterminateProgressMode(true);
        approveButton.setProgress(50);
        //Threading------------------------------------------------------------------------------------------------------
        final Common common=new Common();
        String webService="API/Supplier/ApproveSupplierOrder";
        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        String userName=sharedpreferences.getString(Common.userName,"");
        String postData = "{\"ID\":\""+getIntent().getExtras().getString(Common.SUPPLIERORDERID)+"\",\"userObj\":{\"UserName\":\""+userName+"\"}}";
        String[] dataColumns={};
        Runnable postThread=new Runnable() {
            @Override
            public void run() {
                approveButton.setProgress(100);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
                        String RoleCSV=sharedpreferences.getString(Common.roleCSV,"");
                        Intent intent;
                            intent= new Intent(SupplierOrderDetails.this, HomeScreen.class);
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
                Common.toastMessage(SupplierOrderDetails.this, R.string.failed_try_again);
                Common.toastMessage(SupplierOrderDetails.this,common.msg);
                approveButton.setProgress(-1);
            }};
        common.AsynchronousThread(SupplierOrderDetails.this,
                webService,
                postData,
                null,
                dataColumns,
                postThread,
                postThreadFailed);
    }
    /*public void deleteRequisition(View view){
        new AlertDialog.Builder(RequisitionDetails.this).setIcon(android.R.drawable.ic_dialog_alert)//.setTitle(R.string.exit)
                .setMessage(getResources().getString(R.string.delete_q))
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Threading------------------------------------------------------------------------------------------------------
                        final Common common=new Common();
                        String webService="API/Requisition/DeleteRequisitionByID";
                        String postData = "{\"ID\":\""+getIntent().getExtras().getString(Common.REQID)+"\",\"userObj\":{\"UserName\":\""+userName+"\"}}";
                        final ProgressDialog progressDialog=new ProgressDialog(RequisitionDetails.this);
                        progressDialog.setMessage(getResources().getString(R.string.please_wait));
                        progressDialog.show();
                        String[] dataColumns={};
                        Runnable postThread=new Runnable() {
                            @Override
                            public void run() {
                                progressDialog.cancel();
                                SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
                                String RoleCSV=sharedpreferences.getString(Common.roleCSV,"");
                                Intent intent;
                                if(RoleCSV.contains("CEO")){
                                    intent= new Intent(RequisitionDetails.this, HomeScreen.class);
                                }
                                else {
                                    intent= new Intent(RequisitionDetails.this, HomeScreenNormalUser.class);
                                }
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                                        | Intent.FLAG_ACTIVITY_CLEAR_TOP
                                        | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }
                        };
                        Runnable postThreadFailed=new Runnable() {
                            @Override
                            public void run() {
                                Common.toastMessage(RequisitionDetails.this,common.msg);
                                Common.toastMessage(RequisitionDetails.this, R.string.failed_try_again);
                                progressDialog.cancel();
                            }};
                        common.AsynchronousThread(RequisitionDetails.this,
                                webService,
                                postData,
                                null,
                                dataColumns,
                                postThread,
                                postThreadFailed);
                    }
                }).setNegativeButton(R.string.cancel, null)
                .setCancelable(true).show();
    }*/
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_home) {
            Intent intent;
                intent= new Intent(SupplierOrderDetails.this, HomeScreen.class);
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

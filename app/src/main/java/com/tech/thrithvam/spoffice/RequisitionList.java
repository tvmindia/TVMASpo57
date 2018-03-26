package com.tech.thrithvam.spoffice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class RequisitionList extends AppCompatActivity {
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    CustomAdapter adapter;
    ListView requisitionList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_requisition_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        requisitionList =(ListView)findViewById(R.id.requisition_list);
        getRequisitionsList();
    }
    void getRequisitionsList(){
        (findViewById(R.id.no_items)).setVisibility(View.GONE);
        requisitionList.setVisibility(View.GONE);
        //getting user name
        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        String userName=sharedpreferences.getString(Common.userName,"");
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String postData = "";
        String webService = "/API/Requisition/GetUserRequisitionList";
        switch (getIntent().getExtras().getString(Common.REQUISITIONTYPE)){// for different screens
            case "pending":
                postData = "{\"userObj\":{\"UserName\":\""+userName+"\",\"RoleObj\":{\"AppID\":\"a8503173-99f9-45cc-be2a-81340bd13e26\"}},\"ReqAdvSearchObj\":{\"ReqStatus\":\"ALL\"}}";
                break;
            case "approved":
                postData = "{\"userObj\":{\"UserName\":\""+userName+"\",\"RoleObj\":{\"AppID\":\"a8503173-99f9-45cc-be2a-81340bd13e26\"}},\"ReqAdvSearchObj\":{\"FinalApproved\":\"True\"}}";
                break;
            case "closed":
                postData = "{\"userObj\":{\"UserName\":\""+userName+"\",\"RoleObj\":{\"AppID\":\"a8503173-99f9-45cc-be2a-81340bd13e26\"}},\"ReqAdvSearchObj\":{\"ReqStatus\":\"Closed\"}}";
                break;
            default:
        }
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {"ID",//0
                "ReqNo",//1
                "ReqDateFormatted",//2
                "ReqStatus",//3
                "CompanyObj",//4
                "Value",//5
                "Title"//6
        };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                if(common.dataArrayList.size()==0){
                    (findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                    return;
                }
                adapter=new CustomAdapter(RequisitionList.this,common.dataArrayList,Common.REQUISITIONSLIST);
                requisitionList.setAdapter(adapter);
                requisitionList.setVisibility(View.VISIBLE);
                requisitionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String[] clickedItem=(String[]) parent.getItemAtPosition(position);//Done so for click on searchable list
                        Intent intent=new Intent(RequisitionList.this,RequisitionDetails.class);
                        intent.putExtra(Common.REQID,clickedItem[0]);
                        intent.putExtra(Common.REQNO,clickedItem[1]);
                        intent.putExtra(Common.REQDATE,clickedItem[2]);
                        intent.putExtra(Common.REQSTATUS,clickedItem[3]);
                        intent.putExtra(Common.REQTITLE,clickedItem[6]);
                        String companyName="null";
                        try {
                            companyName=(new JSONObject(clickedItem[4])).getString("Name");
                        } catch (JSONException e) {
                        }
                        intent.putExtra(Common.REQCCOMP,companyName);
                        intent.putExtra(Common.REQUISITIONTYPE,getIntent().getExtras().getString(Common.REQUISITIONTYPE));//To show approve button if pending in details screen
                        startActivity(intent);
                    }
                });
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(RequisitionList.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(RequisitionList.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        asyncTasks.add(common.asyncTask);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    SearchView searchView;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        //Searching-------------------
        searchView=(SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                if(adapter!=null){//for searching
                    adapter.getFilter(Arrays.asList(1,2,3,4)).filter(searchView.getQuery().toString().trim());
                }
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
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

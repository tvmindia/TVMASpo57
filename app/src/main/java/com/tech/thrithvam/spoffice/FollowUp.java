package com.tech.thrithvam.spoffice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class FollowUp extends AppCompatActivity {
    String enquiryID;
    CustomAdapter adapter;
    ListView quotationsList;
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    FloatingActionButton fab;
    Boolean atLeastOneOpen=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        enquiryID=getIntent().getExtras().getString(Common.ENQUIRYID);
        getSupportActionBar().setTitle("FollowUp: "+getIntent().getExtras().getString(Common.ENQUIRYNO));
        //Storing for session
        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putString(Common.ENQUIRYID, getIntent().getExtras().getString(Common.ENQUIRYID));
        editor.putString(Common.ENQUIRYNO, getIntent().getExtras().getString(Common.ENQUIRYNO));
        editor.apply();

        quotationsList=(ListView)findViewById(R.id.follow_up_list);
        getFollowUps();
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(atLeastOneOpen){
                    Common.toastMessage(FollowUp.this,R.string.close_all_followups);
                    return;
                }
                Intent intent=new Intent(FollowUp.this,FollowUpInput.class);
                intent.putExtra(Common.ENQUIRYID,enquiryID);
                intent.putExtra(Common.ENQUIRYNO,getIntent().getExtras().getString(Common.ENQUIRYNO));
                intent.putExtra(Common.FROM,"followup");
                startActivity(intent);
            }
        });
    }
    void getFollowUps(){
        (findViewById(R.id.no_items)).setVisibility(View.GONE);

        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/FollowUp/GetFollowUpDetailsForMobile";
        String postData = "{\"EnquiryID\":\""+enquiryID+"\"}";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {"ID",//0
                "FollowUpDate",//1
                "FollowUpTime",//2
                "Subject",//3
                "Status",//4
        };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                if(common.dataArrayList.size()==0){
                    (findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                    atLeastOneOpen=false;
                    return;
                }
                adapter=new CustomAdapter(FollowUp.this,common.dataArrayList,Common.FOLLOWUPLIST);
                quotationsList.setAdapter(adapter);
                quotationsList.setVisibility(View.VISIBLE);
                for(int i=0;i<common.dataArrayList.size();i++){
                    if(common.dataArrayList.get(i)[4].equals("Open")){
                        atLeastOneOpen=true;
                        break;
                    }
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(FollowUp.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(FollowUp.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        asyncTasks.add(common.asyncTask);
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
    @Override
    public void onBackPressed() {
        for(int i=0;i<asyncTasks.size();i++){
            asyncTasks.get(i).cancel(true);
        }
        if(isTaskRoot()){//to avoid closing the app, re routing to enquiries
            Intent intent=new Intent(this,Enquiries.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
        else {
            super.onBackPressed();
        }
    }
}

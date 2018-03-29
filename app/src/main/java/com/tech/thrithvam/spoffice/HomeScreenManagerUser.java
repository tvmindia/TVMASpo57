package com.tech.thrithvam.spoffice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeScreenManagerUser extends AppCompatActivity {
    Spinner statisticsType;
    SharedPreferences sharedpreferences;
    String userName;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen_manager_user);

        sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        userName=sharedpreferences.getString(Common.userName,"");
        if(userName.equals("")){//not logged in
            Intent intent = new Intent(HomeScreenManagerUser.this, Login.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_CLEAR_TOP
                    | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return;
        }
        ((TextView)findViewById(R.id.welcome)).setText(getResources().getString(R.string.welcome,userName));

       //Spinner
        ArrayList<String> statisticsDuration = new ArrayList<String>();
        statisticsDuration.add(getResources().getString(R.string.days90));
        statisticsDuration.add(getResources().getString(R.string.days180));
        statisticsDuration.add(getResources().getString(R.string.days365));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_small, statisticsDuration);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        statisticsType =(Spinner)findViewById(R.id.statistics_duration);
        statisticsType.setAdapter(dataAdapter);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            statisticsType.getBackground().setColorFilter(getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        else {
            statisticsType.getBackground().setColorFilter(getResources().getColor(R.color.colorAccent), PorterDuff.Mode.SRC_ATOP);
        }
        statisticsType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //REquisitions statistics
                getRequisitionStatistics();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        //add enquiry
        (findViewById(R.id.add_enquiry)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeScreenManagerUser.this,InsertRequisition.class));
            }
        });


    }
    void getRequisitionStatistics(){
        (findViewById(R.id.statistics_linear)).setVisibility(View.GONE);
        int duration=0;
        if(statisticsType.getSelectedItem().toString().equals(getResources().getString(R.string.days90))){
            duration = 90;
        }
        else if(statisticsType.getSelectedItem().toString().equals(getResources().getString(R.string.days180))){
            duration=180;
        }
        else if(statisticsType.getSelectedItem().toString().equals(getResources().getString(R.string.days365))){
            duration=365;
        }
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/Requisition/GetRequisitionCount";
        String postData = "{\"duration\":\""+duration+"\",\"UserName\":\""+userName+"\"}}";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {};
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                (findViewById(R.id.statistics_linear)).setVisibility(View.VISIBLE);
//                Common.toastMessage(HomeScreenNormalUser.this,common.json);
                try {
                    JSONObject jsonObject=new JSONObject(common.json);
                    int allCount,pendingManagerCount,pendingFinalCount,closeCount;
                    allCount=jsonObject.optInt("AllCount");
                    pendingManagerCount=jsonObject.optInt("PendingManagerCount");
                    pendingFinalCount=jsonObject.optInt("PendingFinalCount");
                    closeCount=jsonObject.optInt("CloseCount");

                    if(allCount<0) allCount =0;
                    if(pendingManagerCount<0) pendingManagerCount =0;
                    if(pendingFinalCount<0) pendingFinalCount =0;
                    if(closeCount<0) closeCount =0;

                    int pending,approved;
                    pending = pendingManagerCount + pendingFinalCount ;
                    approved = allCount - pendingManagerCount - pendingFinalCount ;


                    ((TextView)findViewById(R.id.pending_requisitions)).setText(Integer.toString(pending));
                    ((TextView)findViewById(R.id.approved_requisitions)).setText(Integer.toString(approved));
                    ((TextView)findViewById(R.id.closed_requisitions)).setText(Integer.toString(closeCount));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(HomeScreenManagerUser.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(HomeScreenManagerUser.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    public void pendingRequisitionsClick(View view){
        Intent intent=new Intent(HomeScreenManagerUser.this,RequisitionList.class);
        intent.putExtra(Common.REQUISITIONTYPE,"pending");
        startActivity(intent);
    }
    public void approvedRequisitionsClick(View view){
        Intent intent=new Intent(HomeScreenManagerUser.this,RequisitionList.class);
        intent.putExtra(Common.REQUISITIONTYPE,"approved");
        startActivity(intent);
    }
    public void closedRequisitionsClick(View view){
        Intent intent=new Intent(HomeScreenManagerUser.this,RequisitionList.class);
        intent.putExtra(Common.REQUISITIONTYPE,"closed");
        startActivity(intent);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_logout, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.menu_logout) {
            //Storing for session
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putString(Common.userName, "");
            editor.apply();
            Intent intent = new Intent(HomeScreenManagerUser.this, Login.class);
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

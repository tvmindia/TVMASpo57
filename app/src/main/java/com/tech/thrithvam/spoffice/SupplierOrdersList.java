package com.tech.thrithvam.spoffice;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;
import java.util.Arrays;

public class SupplierOrdersList extends AppCompatActivity {
    ArrayList<AsyncTask> asyncTasks=new ArrayList<>();
    CustomAdapter adapter;
    Spinner listDurationSpinner;
    ListView supplierOrdersList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_orders_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        supplierOrdersList =(ListView)findViewById(R.id.supplier_orders_list);
        //Spinner
        ArrayList<String> statisticsDuration = new ArrayList<String>();
        statisticsDuration.add(getResources().getString(R.string.days90));
        statisticsDuration.add(getResources().getString(R.string.days180));
        statisticsDuration.add(getResources().getString(R.string.days365));
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, R.layout.item_spinner_small_white, statisticsDuration);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        listDurationSpinner =(Spinner)findViewById(R.id.list_duration);
        listDurationSpinner.setAdapter(dataAdapter);
        listDurationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getSupplierOrders();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    void getSupplierOrders(){
        (findViewById(R.id.no_items)).setVisibility(View.GONE);
        supplierOrdersList.setVisibility(View.GONE);
        int duration=0;
        if(listDurationSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.days90))){
            duration = 90;
        }
        else if(listDurationSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.days180))){
            duration=180;
        }
        else if(listDurationSpinner.getSelectedItem().toString().equals(getResources().getString(R.string.days365))){
            duration=365;
        }
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/Supplier/GetAllSupplierPODetail";
        String postData = "{\"duration\":\""+duration+"\"}";
        AVLoadingIndicatorView loadingIndicator = (AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
        String[] dataColumns = {"ID",//0
                "SupplierName",//1
                "PONo",//2
                "PODate",//3
                "Amount",//4
                "POStatus"//5
        };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                if(common.dataArrayList.size()==0){
                    (findViewById(R.id.no_items)).setVisibility(View.VISIBLE);
                    return;
                }
                adapter=new CustomAdapter(SupplierOrdersList.this,common.dataArrayList,Common.CUSTOMERORDERSLIST);
                supplierOrdersList.setAdapter(adapter);
                supplierOrdersList.setVisibility(View.VISIBLE);
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(SupplierOrdersList.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(SupplierOrdersList.this,
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
                listDurationSpinner.setVisibility(View.GONE);
                if(adapter!=null){//for searching
                    adapter.getFilter(Arrays.asList(1,2,3,5)).filter(searchView.getQuery().toString().trim());
                }
                return false;
            }
        });
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                listDurationSpinner.setVisibility(View.VISIBLE);
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

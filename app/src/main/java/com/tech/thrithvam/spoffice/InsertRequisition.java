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
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import com.dd.CircularProgressButton;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
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
    Spinner companySpinner;
    ArrayList<String[]> companyList=new ArrayList<>();
    ArrayList<String[]> materialList=new ArrayList<>();
    ArrayList<String> materialCodes = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_insert_requisition);
        inflater=getLayoutInflater();
        (findViewById(R.id.scrollView)).setVisibility(View.INVISIBLE);
        getCompanies();
        requiredDate=(TextView)findViewById(R.id.date_value);
        SimpleDateFormat formatted = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        requiredDate.setText(formatted.format(Calendar.getInstance().getTime()));
        detailItemListView=(LinearLayout)findViewById(R.id.requisition_detail_list);
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
    public void addMoreDetail(View view){
        final View detailItemView=inflater.inflate(R.layout.item_requisition_detail_input,null);
        detailItemViews.add(detailItemView);
        detailItemListView.addView(detailItemView);
        view.setVisibility(View.GONE);
        ((ScrollView)findViewById(R.id.scrollView)).fullScroll(View.FOCUS_DOWN);
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(InsertRequisition.this, R.layout.item_spinner_black, materialCodes);
        dataAdapter.setDropDownViewResource(R.layout.item_spinner);
        final Spinner materialSpinner =(Spinner)detailItemView.findViewById(R.id.material_code_value);
        materialSpinner.setAdapter(dataAdapter);
        materialSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView)detailItemView.findViewById(R.id.description)).setText(materialList.get(materialSpinner.getSelectedItemPosition())[2]);
                amountCalculation(detailItemView);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //calculating quantity
        final EditText reqQty=((EditText)detailItemView.findViewById(R.id.req_qty));
        reqQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                amountCalculation(detailItemView);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //cancelling item
        ImageView closeIcon=(ImageView)detailItemView.findViewById(R.id.close);
        closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(detailItemViews.size()==1){
                    Common.toastMessage(InsertRequisition.this,"At least one item needed");
                    return;
                }
                else {
                    detailItemViews.remove(detailItemView);
                    detailItemListView.removeView(detailItemView);
                    //adding 'add_detail' button to last detail view
                    (detailItemViews.get(detailItemViews.size() - 1)).findViewById(R.id.add_detail).setVisibility(View.VISIBLE);
                }
            }
        });
    }
    void amountCalculation(View detailItemView){
        final EditText reqQty=((EditText)detailItemView.findViewById(R.id.req_qty));
        final TextView amountCalc=((TextView)detailItemView.findViewById(R.id.amount_calculation));
        final TextView amount=((TextView)detailItemView.findViewById(R.id.amount));
        final Spinner materialSpinner =(Spinner)detailItemView.findViewById(R.id.material_code_value);
        String amountCalculation;
        amountCalculation=(reqQty.getText().toString().equals("")?"0":reqQty.getText().toString())
                +" X "
                +getResources().getString(R.string.rupees,String.format(Locale.US,"%.2f",Double.parseDouble(materialList.get(materialSpinner.getSelectedItemPosition())[3])));
        amountCalc.setText(amountCalculation);

        Double amountValue=Double.parseDouble(reqQty.getText().toString().equals("")?"0.0":reqQty.getText().toString())
                * Double.parseDouble(materialList.get(materialSpinner.getSelectedItemPosition())[3]);
        amount.setText(getResources().getString(R.string.rupees,
                String.format(Locale.US,"%.2f",amountValue)));
    }
    public void submitRequisition(View view){
        //Validations
        EditText title=(EditText)findViewById(R.id.title_value);
        if(title.getText().toString().equals("")){
            title.setError(getResources().getString(R.string.give_valid));
            return;
        }
        /*EditText companyName=(EditText)findViewById(R.id.company_name_value);
        if(companyName.getText().toString().equals("")){
            companyName.setError(getResources().getString(R.string.give_valid));
            return;
        }*/
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
            Spinner materialSelection=(Spinner)detailItemViews.get(i).findViewById(R.id.material_code_value);
            detailItemsJSON+="{\"MaterialID\":\""+materialList.get(materialSelection.getSelectedItemPosition())[0]+"\"" +
                    //",\"Description\":\""+materialList.get(materialSelection.getSelectedItemPosition())[2]+"\"" +
                    ",\"ExtendedDescription\":\""+((TextView)detailItemViews.get(i).findViewById(R.id.ext_des)).getText().toString()
                    +"\",\"CurrStock\":\""+((TextView)detailItemViews.get(i).findViewById(R.id.curr_qty)).getText().toString()
//                    +"\",\"AppxRate\":\""+materialList.get(materialSelection.getSelectedItemPosition())[3]+"\""
                    +"\",\"RequestedQty\":\""+((TextView)detailItemViews.get(i).findViewById(R.id.req_qty)).getText().toString()
                    +"\"},";
        }
        detailItemsJSON=detailItemsJSON.substring(0, detailItemsJSON.lastIndexOf(","));
        detailItemsJSON+="]";
        String postData;
        postData="{\"Title\":\""+title.getText().toString()
                +(getIntent().hasExtra(Common.REQID)?"\",\"ID\":\""+getIntent().getExtras().getString(Common.REQID):"")//if update
                +"\",\"ReqDateFormatted\":\""+date.getText().toString()
                +"\",\"ReqForCompany\":\""+companyList.get(companySpinner.getSelectedItemPosition())[0]//Company code
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
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent=new Intent(InsertRequisition.this,RequisitionList.class);
                                intent.putExtra(Common.REQUISITIONTYPE,"pending");
                                startActivity(intent);
                                InsertRequisition.this.finish();
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
    void getCompanies(){
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/Company/GetAllCompanies";
        String postData = "";
        String[] dataColumns = {"Code",//0
        "Name"//1
        };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                //Spinner
                ArrayList<String> companyNames = new ArrayList<String>();
                for (int i=0;i<common.dataArrayList.size();i++){
                    String[] data=new String[2];
                    data[0]=common.dataArrayList.get(i)[0];
                    data[1]=common.dataArrayList.get(i)[1];
                    companyList.add(data);
                }
                for(int i=0;i<companyList.size();i++){
                    companyNames.add(companyList.get(i)[1]);
                }
                ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(InsertRequisition.this, R.layout.item_spinner_black, companyNames);
                dataAdapter.setDropDownViewResource(R.layout.item_spinner);
                companySpinner =(Spinner)findViewById(R.id.company_name_value);
                companySpinner.setAdapter(dataAdapter);

                //getting materials available from server
                getMaterials();
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(InsertRequisition.this, R.string.failed_server);
                finish();
            }
        };

        common.AsynchronousThread(InsertRequisition.this,
                webService,
                postData,
                null,
                dataColumns,
                postThread,
                postThreadFailed);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    void getMaterials(){
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        String webService = "API/RawMaterial/GetAllRawMaterials";
        String postData = "";
        String[] dataColumns = {"ID",//0
                "MaterialCode",//1
                "Description",//2
                "ApproximateRate"//3
        };
        Runnable postThread = new Runnable() {
            @Override
            public void run() {
                //Spinner
                for (int i=0;i<common.dataArrayList.size();i++){
                    String[] data=new String[4];
                    data[0]=common.dataArrayList.get(i)[0];
                    data[1]=common.dataArrayList.get(i)[1];
                    data[2]=common.dataArrayList.get(i)[2];
                    data[3]=common.dataArrayList.get(i)[3];
                    materialList.add(data);
                }
                for(int i=0;i<materialList.size();i++){
                    materialCodes.add(materialList.get(i)[1]);
                }
                if(getIntent().hasExtra(Common.REQID)){
                    getRequisitionDetails();
                }
                else {
                    addMoreDetail(new View(InsertRequisition.this));//this view is dummy
                    (findViewById(R.id.scrollView)).setVisibility(View.VISIBLE);
                    (findViewById(R.id.loading_indicator)).setVisibility(View.GONE);
                }
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(InsertRequisition.this, R.string.failed_server);
                finish();
            }
        };

        common.AsynchronousThread(InsertRequisition.this,
                webService,
                postData,
                null,
                dataColumns,
                postThread,
                postThreadFailed);
        //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    }
    void getRequisitionDetails(){
        //Threading------------------------------------------------------------------------------------------------------
        final Common common = new Common();
        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        String userName=sharedpreferences.getString(Common.userName,"");
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
                        Common.toastMessage(InsertRequisition.this,"No details Available");
                        finish();
                        return;
                    }
                    JSONArray jsonObject1= records.getJSONArray("RequisitionDetailList");
                    for (int i = 0; i < jsonObject1.length(); i++) {
                        JSONObject jsonObject2 = jsonObject1.getJSONObject(i);
                        String[] data = new String[7];
                        data[0] = jsonObject2.getString("ID");
                        data[1] = jsonObject2.getString("Description");
                        data[2] = jsonObject2.getString("CurrStock");
                        data[3] = jsonObject2.getString("RequestedQty");
                        data[4] = jsonObject2.getString("AppxRate");
                        data[5] = jsonObject2.getString("ExtendedDescription");
                        total+=(Integer.parseInt(data[3]) * Double.parseDouble(data[4]));
                        data[6] = Double.toString(Integer.parseInt(data[3]) * Double.parseDouble(data[4]));
                        dataArrayList.add(data);
                    }
                    //Requisition details
                    ((TextView)findViewById(R.id.requisition_no_value)).setText(records.optString("ReqNo"));
                    ((EditText)findViewById(R.id.title_value)).setText(records.optString("Title"));
                    ((TextView)findViewById(R.id.date_value)).setText(records.optString("ReqDateFormatted"));
                    //finding company index
                    int index=0;
                    for(int j=0;j<companyList.size();j++){
                        if(companyList.get(j)[0].equals(records.optString("ReqForCompany")))
                            index=j;
                    }
                    companySpinner.setSelection(index);
                } catch (JSONException e) {
                    Common.toastMessage(InsertRequisition.this, "Some error occurred\n"+ e.getMessage());
                }
                for(int i=0;i<dataArrayList.size();i++){
                    addMoreDetail(new View(InsertRequisition.this));
                    View view=detailItemViews.get(i);
                    /*if(!dataArrayList.get(i)[1].equals("null"))
                        ((TextView)view.findViewById(R.id.description)).setText(dataArrayList.get(i)[1]);*/
                    if(!dataArrayList.get(i)[2].equals("null"))
                        ((EditText)view.findViewById(R.id.curr_qty)).setText(dataArrayList.get(i)[2]);
                    if(!dataArrayList.get(i)[3].equals("null"))
                        ((EditText)view.findViewById(R.id.req_qty)).setText(dataArrayList.get(i)[3]);
                    if(!dataArrayList.get(i)[5].equals("null"))
                        ((EditText)view.findViewById(R.id.ext_des)).setText(dataArrayList.get(i)[5]);
                    //finding material index
                    int index=0;
                    for(int j=0;j<materialList.size();j++){
                        if(materialList.get(j)[2].equals(dataArrayList.get(i)[1]))//TODO: change to match material id
                            index=j;
                    }
                    if(!dataArrayList.get(i)[0].equals("null"))
                        ((Spinner)view.findViewById(R.id.material_code_value)).setSelection(index);
                }
                 (findViewById(R.id.scrollView)).setVisibility(View.VISIBLE);
/*                TextView totalTextView=(TextView)headerDetails.findViewById(R.id.total);
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
                }*/
            }
        };
        Runnable postThreadFailed = new Runnable() {
            @Override
            public void run() {
                Common.toastMessage(InsertRequisition.this, R.string.failed_server);
            }
        };

        common.AsynchronousThread(InsertRequisition.this,
                webService,
                postData,
                loadingIndicator,
                dataColumns,
                postThread,
                postThreadFailed);
//        asyncTasks.add(common.asyncTask);
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

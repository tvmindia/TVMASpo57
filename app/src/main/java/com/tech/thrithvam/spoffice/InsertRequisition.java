package com.tech.thrithvam.spoffice;

import android.app.DatePickerDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
}

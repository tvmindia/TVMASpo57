package com.tech.thrithvam.spoffice;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class HomeScreen extends AppCompatActivity {
    Spinner statisticsType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        String userName=sharedpreferences.getString("UserName","");
        ((TextView)findViewById(R.id.welcome)).setText(getResources().getString(R.string.welcome,userName));

        //chart Spinner
        List<String> statisticsDuration = new ArrayList<String>();
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
               // getChartData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}

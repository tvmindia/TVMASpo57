package com.tech.thrithvam.spoffice;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class FollowUpInput extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow_up_input);
    }
    @Override
    public void onBackPressed() {
        Intent intent=new Intent(FollowUpInput.this,Enquiries.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
       // super.onBackPressed();
    }
}

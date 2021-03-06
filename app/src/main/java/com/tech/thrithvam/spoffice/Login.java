package com.tech.thrithvam.spoffice;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.messaging.FirebaseMessaging;
import com.unstoppable.submitbuttonview.SubmitButton;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {
    SubmitButton loginButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseMessaging.getInstance().subscribeToTopic("common");
        final SharedPreferences sharedpreferences = getSharedPreferences(Common.preferenceName, Context.MODE_PRIVATE);
        if(!sharedpreferences.getString(Common.userName,"").equals("")){//If already login
            enrouteUser(sharedpreferences.getString(Common.roleCSV,""),false);
            return;
        }

        loginButton=(SubmitButton)findViewById(R.id.login_button);
        final EditText usernameInput=(EditText)findViewById(R.id.input_username);
        final EditText passwordInput=(EditText)findViewById(R.id.input_password);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usernameInput.clearFocus();
                passwordInput.clearFocus();
                if(usernameInput.getText().toString().equals("")||passwordInput.getText().toString().equals("")) {loginButton.reset();return;}
                usernameInput.setEnabled(false);
                passwordInput.setEnabled(false);
                loginButton.setClickable(false);

                //Threading------------------------------------------------------------------------------------------------------
                final Common common=new Common();
                String webService="/API/Login/HomeLogin";
                String postData =  "{\"LoginName\":\"" + usernameInput.getText().toString() + "\",\"Password\":\""+ passwordInput.getText().toString()+ "\"}";
//                AVLoadingIndicatorView loadingIndicator =(AVLoadingIndicatorView) findViewById(R.id.loading_indicator);
                String[] dataColumns={};
                Runnable postThread=new Runnable() {
                    @Override
                    public void run() {
                        loginButton.doResult(true);
                        JSONObject jsonObject= null;
                        try {
                            jsonObject = new JSONObject(common.json);
                            String userName=jsonObject.optString("UserName");
                            String RoleCSV=jsonObject.optString("RoleCSV");
                            //Common.toastMessage(Login.this,RoleCSV);
                            //Login success
                            // Storing for session
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(Common.userName, userName);
                            editor.putString(Common.roleCSV, RoleCSV);
                            editor.apply();
                            enrouteUser(RoleCSV,true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Common.toastMessage(Login.this,e.getMessage());
                        }
                    }
                };
                Runnable postThreadFailed=new Runnable() {
                    @Override
                    public void run() {
                        Common.toastMessage(Login.this,common.msg);
                        Common.toastMessage(Login.this, R.string.failed_try_again);
                        loginButton.doResult(false);
                        //Setting button to refresh when password/email change
                        usernameInput.setEnabled(true);
                        passwordInput.setEnabled(true);
                        loginButton.setClickable(true);
                        usernameInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                loginButton.reset();
                            }
                        });
                        passwordInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                            @Override
                            public void onFocusChange(View v, boolean hasFocus) {
                                loginButton.reset();
                            }
                        });

                    }};

                common.AsynchronousThread(Login.this,
                        webService,
                        postData,
                        null,
                        dataColumns,
                        postThread,
                        postThreadFailed);
                //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
            }
        });
    }
    void enrouteUser(String RoleCSV,boolean isLogin){
        final Handler handler = new Handler();
        final Intent intent;
        if(RoleCSV.contains("Manager")) {
            intent = new Intent(Login.this, HomeScreenManagerUser.class);
        }
        else if (RoleCSV.contains("CEO")) {
            intent = new Intent(Login.this, HomeScreen.class);
        }
        else if(RoleCSV.contains("Approver")) {
            intent = new Intent(Login.this, HomeScreen.class);
        }
        else if(RoleCSV.contains("Reception")) {
            intent = new Intent(Login.this, HomeScreen.class);
        }
        else {
            intent = new Intent(Login.this, HomeScreenNormalUser.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        if(isLogin)//Checking whether user manual logging in or already logged in before
        {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startActivity(intent);
                    finish();
                }
            }, 2000);
        }
        else { //no delay is needed
            startActivity(intent);finish();
        }
    }
}

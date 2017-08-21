package com.tech.thrithvam.spoffice;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Common {

    //Constants-----------------------
    static String preferenceName="SPO";
    static final int SALES=123;
    //Custom adapter constants
    static final String QUOTATIONLIST="quotations",
                        ENQUIRYLIST="enquiries",
                        FOLLOWUPLIST="followups",
                        PROFORMALIST="proformainvoices"
                    ;
    //Intent constants
    static final String ENQUIRYID="enquiryID",
                        ENQUIRYNO="enquiryNo";

    //To load image from a url------------------------------------------------------
    static void LoadImage(Context context,ImageView imageView, String imageURL, int failImage){
       /* try {
            if (!imageURL.equals("null")) {
                Glide.with(context)
                        .load(imageURL)//adapterContext.getResources().getString(R.string.url) +imageURL.substring(imageURL.indexOf("img")))
                        .fitCenter()
                        .thumbnail(0.1f)
                        .error(failImage)
                        .into(imageView);
            } else {
                Glide.with(context)
                        .load(failImage)
                        .fitCenter()
                        .into(imageView);
            }
        }
        catch (Exception e){//to avoid exception when user press back before loading an image(target activity not found exception))
        }*/
    }

    //Toast customizations-----------------------------------------------------------
    static void toastMessage(Context context,String message){//To receive string
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        displayToast(toast,context);
    }
    static void toastMessage(Context context, @StringRes int stringID){//To receive string as resource
        Toast toast = Toast.makeText(context, stringID, Toast.LENGTH_LONG);
        displayToast(toast,context);
    }
    static private void displayToast(Toast toast,Context context){
        View toastView = toast.getView();
        TextView toastMessage = (TextView) toastView.findViewById(android.R.id.message);
        toastMessage.setTextSize(15);
        toastMessage.setShadowLayer(0,0,0,0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toastMessage.setTextColor(context.getResources().getColor(R.color.colorAccent,null));
        }
        else {
            toastMessage.setTextColor(context.getResources().getColor(R.color.colorAccent));
        }
        toastMessage.setCompoundDrawablesWithIntrinsicBounds(R.mipmap.ic_launcher, 0, 0, 0);
        toastMessage.setGravity(Gravity.CENTER);
        toastMessage.setCompoundDrawablePadding(7);
        toastMessage.setPadding(5,5,5,5);
        toastView.setPadding(10,10,10,10);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            toastView.setBackground(ContextCompat.getDrawable(context, R.drawable.boarder_accent));
        }
        else {
            toastView.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.boarder_accent));
        }
        toast.show();
    }


    //Threading: to load data from a server----------------------------------------
    ArrayList<String[]> dataArrayList=new ArrayList<>();
    String json;
    AsyncTask asyncTask;
    String msg;
    void AsynchronousThread(final Context context,
                            final String webService,
                            final String postData,
                            final AVLoadingIndicatorView loadingIndicator,
                            final String[] dataColumns,
                            final Runnable postThread,
                            final Runnable postFailThread){

        class GetDataFromServer extends AsyncTask<Void , Void, Void> {
            private int status;
            private StringBuilder sb;
            private String strJson;
            private boolean pass=false;
            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                dataArrayList.clear();
                json="";
                if(loadingIndicator!=null) loadingIndicator.setVisibility(View.VISIBLE);
                //----------encrypting ---------------------------
                // usernameString=cryptography.Encrypt(usernameString);
            }
            @Override
            protected Void doInBackground(Void... arg0) {
                String url =context.getResources().getString(R.string.url) + webService;
                HttpURLConnection c = null;
                try {
                    URL u = new URL(url);
                    c = (HttpURLConnection) u.openConnection();
                    c.setRequestMethod("POST");
                    c.setRequestProperty("Content-type", "application/json");
                    c.setRequestProperty("Content-length", Integer.toString(postData.length()));
                 //   String basicAuth = "Basic " + Base64.encodeToString("partyec@tvm-2017:".getBytes(), Base64.NO_WRAP);
                 //   c.setRequestProperty ("Authorization", basicAuth);
                    c.setDoInput(true);
                    c.setDoOutput(true);
                    c.setUseCaches(false);
                    c.setConnectTimeout(10000);
                    c.setReadTimeout(10000);
                    DataOutputStream wr = new DataOutputStream(c.getOutputStream());
                    wr.writeBytes(postData);
                    wr.flush();
                    wr.close();
                    status = c.getResponseCode();
                    switch (status) {
                        case 200:
                        case 201: BufferedReader br = new BufferedReader(new InputStreamReader(c.getInputStream()));
                            sb = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                sb.append(line).append("\n");
                            }
                            br.close();
                            int a=sb.indexOf("{");
                            int b=sb.lastIndexOf("}");
                            strJson=sb.substring(a, b + 1);
                            //   strJson=cryptography.Decrypt(strJson);
                            strJson=/*"{\"JSON\":[" +*/ strJson.replace("\\\"","\"").replace("\\\\","\\") /*+ "]}"*/;
                    }
                } catch (Exception ex) {
                    Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                    msg=ex.getMessage();
                } finally {
                    if (c != null) {
                        try {
                            c.disconnect();
                        } catch (Exception ex) {
                            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);
                            msg=ex.getMessage();
                        }
                    }
                }
                if(strJson!=null)
                {try {
                    JSONObject jsonRootObject = new JSONObject(strJson);
                    pass = jsonRootObject.optBoolean("Result");
                    if(pass){
                        JSONArray jsonArray = jsonRootObject.optJSONArray("Records");
                        if(jsonArray!=null && dataColumns.length!=0) {  //json which can be load into an adapter/array list
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String[] data = new String[dataColumns.length];
                                for (int j = 0; j < dataColumns.length; j++) {
                                    data[j] = jsonObject.optString(dataColumns[j]);
                                }
                                dataArrayList.add(data);
                            }
                        }
                        else {//Take coming json as it is
                            json=jsonRootObject.optString("Records");
                        }
                    }
                    else {
                        msg=jsonRootObject.optString("Message");
                        json=strJson;
                    }
                } catch (Exception ex) {
                    msg=ex.getMessage();
                }}
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                super.onPostExecute(result);
                if(loadingIndicator!=null) loadingIndicator.setVisibility(View.GONE);
                if(!pass) {
                    if(postFailThread==null){
                    /*    Intent noItemsIntent=new Intent(context,NothingToDisplay.class);
                        noItemsIntent.putExtra("msg",msg);
                        noItemsIntent.putExtra("activityHead","PartyEC");
                        context.startActivity(noItemsIntent);
                        ((Activity)context).finish();*/
                    Common.toastMessage(context,msg);
                    }
                    else {
                        postFailThread.run();
                    }
                }
                else {
                    postThread.run();
                }
            }
        }
        asyncTask=new GetDataFromServer().execute();
    }
}

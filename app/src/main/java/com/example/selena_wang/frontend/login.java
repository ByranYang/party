package com.example.selena_wang.frontend;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.AsyncTask;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Intent;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class login extends Activity {
    public static final String base_url = "http://caravanparty.herokuapp.com/";

    String SUCCESS = "SUCCESS";
    String ERR_BAD_CREDENTIALS = "ERR_BAD_CREDENTIALS";
    String ERR_USERNAME_EXISTS = "ERR_USERNAME_EXISTS";
    String ERR_INVALID_USERNAME = "ERR_INVALID_USERNAME";
    String ERR_INVALID_PASSWORD = "ERR_INVALID_PASSWORD";
    String MAX_USERNAME_LENGTH = "MAX_USERNAME_LENGTH";
    String MAX_PASSWORD_LENGTH = "MAX_PASSWORD_LENGTH";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickLogin(View view){
        new MyAsyncTask().execute("login");
    }


    public void onClickCreate(View view){
        String username = ((EditText) findViewById(R.id.inputUser)).getText().toString();
        String password = ((EditText) findViewById(R.id.inputPass)).getText().toString();
        new MyAsyncTask().execute("register");
    }

    public void createAlertDialog(String ERR){
        String message = "";
        if(ERR=="ERR_BAD_CREDENTIALS"){
            message = "Invalid username and password combination. Please try again.";
        }else if(ERR=="ERR_INVALID_PASSWORD"){
            message = "The password should be at most 128 characters long. Please try again.";
        }else if(ERR=="ERR_INVALID_USERNAME"){
            message = "The user name should be non-empty and at most 128 characters long. Please try again.";
        }else if(ERR=="ERR_USERNAME_EXISTS"){
            message = "This username already exists. Please try again.";
        }else{
            System.out.println("I dont know unknown error?");
            return;
        }
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(login.this);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setNegativeButton("Okay",new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int id) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double>{
        private String username;
        private String password;

        @Override
        protected Double doInBackground(String... params) {
            postData(params[0]);
            return null;
        }

        private void postData(String parameter) {
            HttpClient httpclient = new DefaultHttpClient();
            String extend_url = "";
            if(parameter.equals("register")){ extend_url = "register/";}
            if(parameter.equals("login")){extend_url="login/";}
            HttpPost httppost = new HttpPost(base_url + extend_url);
            username = ((EditText) findViewById(R.id.inputUser)).getText().toString();
            password = ((EditText) findViewById(R.id.inputPass)).getText().toString();
            List data= new ArrayList();
            data.add(new BasicNameValuePair("username", username));
            data.add(new BasicNameValuePair("password",password));
            try{
                httppost.setEntity(new UrlEncodedFormEntity(data));
            }catch(UnsupportedEncodingException e){
                e.printStackTrace();
            }
            try{
                HttpResponse response = httpclient.execute(httppost);
                try {
                    JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    try_log_add(json);

                }catch(JSONException e){
                    e.printStackTrace();
                }

            }catch (IOException e ) {
                e.printStackTrace();
            }
        }

        private void try_log_add(JSONObject json){
            final JSONObject json2 = json;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String ERR = "ERR";
                    String name = "user";
                    String user_id = "user_id";
                    ArrayList<String> friends = new ArrayList<String>();
                    try {
                        ERR = json2.getString("reply_code");
                        name = json2.getString("name");
                        user_id = json2.getString("user_id");
                        JSONArray json_friends = json2.getJSONArray("friend_ids");
                        if (json_friends!=null){
                            for(int i = 0; i<json_friends.length(); i++){
                                friends.add(json_friends.get(i).toString());
                            }
                        }

                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                    if(ERR.equals("SUCCESS")){
                        Intent intent = new Intent(login.this, homepage.class);
                        intent.putExtra("username", name);
                        intent.putExtra("user_id",user_id);
                        intent.putExtra("password", password);
                        intent.putExtra("friend_ids",friends);
                        startActivity(intent);
                    }else{
                        createAlertDialog(ERR);
                    }
                }
            });

        }
    }

}

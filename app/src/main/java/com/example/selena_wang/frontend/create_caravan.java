package com.example.selena_wang.frontend;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class create_caravan extends Activity {

    private static boolean active = false;
    private home_list_adapter create_adapter;

    public static boolean getActive(){
        return active;
    }

    public ListView friendList;

    // Initialize variables
    String SUCCESS = "SUCCESS";
    String ERR_USER_ALREADY_HOSTING = "ERR_USER_ALREADY_HOSTING";
    String ERR_USER_DOESNT_EXIST = "ERR_USER_DOESNT_EXIST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_caravan_layout);

        friendList = (ListView) findViewById(R.id.addfriend_list);
        createListView();

    }

    private ArrayList<String> getIDs(){
        return create_adapter.get_ids();
    }

    private void createListView(){
        new MyAsyncTask().execute("list");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        ActionBar bar = getActionBar();
        bar.setHomeButtonEnabled(true);
        bar.setDisplayShowHomeEnabled(true);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if(id==android.R.id.home){
            Intent intent = new Intent(this,homepage.class);
            if(homepage.getActive()){
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(intent);
        }
        else if (id == R.id.friend_icon) {
            Intent intent = new Intent(this,friends.class);
            if(friends.getActive()){
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(intent);
        }else if (id == R.id.caravan_icon){
            Intent intent = new Intent(this,caravan_map.class);
            if(caravan_map.getActive()){
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(intent);
        }else if(id == R.id.create_icon){
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickCreate(View view){
        new MyAsyncTask().execute("create");
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        private String host_id = homepage.get_user_id();
        private Boolean setAdapter = false;

        ArrayList<HashMap<String,String>> list_file;

        @Override
        protected Double doInBackground(String... params) {
            if(params[0].equals("list")){
                createList(params[0]);
            }
            if(params[0].equals("create")) {
                createCaravan(params[0]);
            }
            return null;
        }

        private void createList(String parameter){
            String friend_url = "users/" + homepage.get_user_id() + "/friends";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet_friend = new HttpGet(homepage.url + friend_url);
            try{
                HttpResponse response_friend = httpclient.execute(httpGet_friend);
                try{
                    JSONObject json_friend = new JSONObject(EntityUtils.toString(response_friend.getEntity()));
                    create_listView(json_friend);
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }


        }

        private void getUser(String user_id){
            String friend_url = "users/" + user_id;
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet_friend = new HttpGet(homepage.url + friend_url);
            try{
                HttpResponse response_friend = httpclient.execute(httpGet_friend);
                try{
                    JSONObject json_friend = new JSONObject(EntityUtils.toString(response_friend.getEntity()));
                    String name = json_friend.getString("name");
                    String id = json_friend.getString("user_id");
                    HashMap<String,String>to_add = new HashMap<String,String>();
                    to_add.put("username",name);
                    to_add.put("user_id",id);
                    to_add.put("time","time");
                    to_add.put("type","friend");
                    list_file.add(to_add);

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        private void create_listView(JSONObject friends){
            JSONArray friend_array = new JSONArray();
            try{
                friend_array = friends.getJSONArray("friends");
            }catch(JSONException e){
                e.printStackTrace();
            }

            list_file = new ArrayList<HashMap<String,String>>();
            for(int i = 0; i<friend_array.length(); i++){
                try {
                    getUser(Integer.toString(friend_array.getInt(i)));
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }
            setAdapter = true;
        }

        private void createCaravan(String parameter) {
            HttpClient httpclient = new DefaultHttpClient();
            String extend_url = "caravans/create/" + host_id;
            HttpPost httppost = new HttpPost(homepage.url + extend_url);

            try {
                HttpResponse response = httpclient.execute(httppost);
                try {
                    String json_string = EntityUtils.toString(response.getEntity());
                    System.out.println(json_string);
                    JSONObject json = new JSONObject(json_string);
                    addCaravan(json);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private void addCaravan(JSONObject json){
            final JSONObject json2 = json;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String ERR = "ERR";
                    String caravan_id = "id";
                    try {
                        ERR = json2.getString("reply_code");
                        caravan_id = json2.getString("id");
                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                    ArrayList<String> ids = getIDs();
                    for(int i = 0; i<ids.size();i++){
                        inviteUser(Integer.parseInt(ids.get(i)));
                    }

                    System.out.println(ERR);
                    if(ERR.equals("SUCCESS")){
                        homepage.set_caravanId(caravan_id);
                        Intent intent = new Intent(create_caravan.this, caravan_map.class);
                        intent.putExtra("user_id",host_id);
                        startActivity(intent);
                    }else{

                    }
                }
            });

        }

        private void inviteUser(int id){
            HttpClient httpclient = new DefaultHttpClient();
            String extend_url = "caravans/" + homepage.get_caravanId() + "/invite/" + id;
            HttpPost httppost = new HttpPost(homepage.url + extend_url);

            try {
                HttpResponse response = httpclient.execute(httppost);
                try {
                    String json_string = EntityUtils.toString(response.getEntity());
                    System.out.println(json_string);
                    JSONObject json = new JSONObject(json_string);

                    String ERR = "ERR";
                    String caravan_id = "id";
                    try {
                        ERR = json.getString("reply_code");
                        caravan_id = json.getString("id");
                    }catch(JSONException e){
                        e.printStackTrace();
                    }

                    System.out.println(ERR);
                    if(ERR.equals("SUCCESS")){
                    }else{

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(Double result){
            if (setAdapter){
                create_adapter= new home_list_adapter(list_file,create_caravan.this);
                friendList.setAdapter(create_adapter);
            }
        }
    }

}

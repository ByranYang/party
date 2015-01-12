package com.example.selena_wang.frontend;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


public class findfriends extends Fragment {

    private String current_search_id;
    private  String current_search_name;
    private Button search_button;
    private Button add_button;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.findfriends_layout, container, false);
        search_button = (Button)view.findViewById(R.id.searchButton);
        add_button = (Button)view.findViewById(R.id.addButton);
        search_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new MyAsyncTask().execute("search");
            }
        });
        add_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                new MyAsyncTask().execute("add");
            }
        });
        return view;
    }

    private void setAddUser(){
        TextView user_text = (TextView)getView().findViewById(R.id.friend_info);
        user_text.setText("The id " + current_search_id + " is associated with " + current_search_name + ".");
        add_button.setVisibility(View.VISIBLE);
    }

    private void removeAddUser(){
        TextView user_text = ((TextView) getView().findViewById(R.id.friend_info));
        user_text.setText("");
        add_button.setVisibility(View.INVISIBLE);
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {
        private String id;
        private String name;
        private boolean searched = false;
        private boolean added = false;

        @Override
        protected Double doInBackground(String... params) {
            if(params[0].equals("search")){
                searchUser(params[0]);
            }else if(params[0].equals("add")){
                addUser(params[0]);
            }
            return null;
        }

        private void searchUser(String parameter){
            HttpClient httpclient = new DefaultHttpClient();
            id = ((EditText)getView().findViewById(R.id.inputFriend)).getText().toString();
            String friend_url = "users/" + id;
            HttpGet httpGet_friend = new HttpGet(homepage.url + friend_url);
            List data= new ArrayList();
            data.add(id);
            try{
                HttpResponse response_user = httpclient.execute(httpGet_friend);
                try{
                    JSONObject json_user = new JSONObject(EntityUtils.toString(response_user.getEntity()));
                    id = json_user.getString("user_id");
                    name = json_user.getString("name");
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
            searched = true;
        }

        // list structure = [caravan/friend, username/caravan_id, caravan destination, caravan members]
        private void addUser(String parameter){
            HttpClient httpclient = new DefaultHttpClient();
            String to_add_url = "users/" + homepage.get_user_id() + "/friends/add/" + current_search_id;
            HttpPost httpPost_friend = new HttpPost(homepage.url + to_add_url);
            List data= new ArrayList();
            try{
                httpPost_friend.setEntity(new UrlEncodedFormEntity(data));

            }catch (UnsupportedEncodingException e){
                e.printStackTrace();
            }
            try{
                HttpResponse response_user = httpclient.execute(httpPost_friend);
                try{
                    JSONObject json_user = new JSONObject(EntityUtils.toString(response_user.getEntity()));
                    if(json_user.getString("reply_code").equals("SUCCESS")) {
                        added = true;
                    }

                }catch(JSONException e){
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }

        }

        protected void onPostExecute(Double result){
            if (searched){
                current_search_id = id;
                current_search_name = name;
                setAddUser();
            }
            if (added){
                removeAddUser();
            }

        }

    }
}

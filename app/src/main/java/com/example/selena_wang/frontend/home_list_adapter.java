package com.example.selena_wang.frontend;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.TextView;

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

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by selena_wang on 10/23/14.
 */
public class home_list_adapter extends BaseAdapter implements ListAdapter {

    // friend list structure = [time, "friend", username, user_id]
    //friend_request structure = [time, "friend_request", username]
    //caravan list structure = [time, "caravan", caravan_id, caravan destination, caravan members]
    private ArrayList<String[]> list = new ArrayList<String[]>();
    private Context context;


    private LayoutInflater mInflater;

    public home_list_adapter(ArrayList<String[]> list, Context context) {
        this.list = list;
        this.context = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return 0;
        //just return 0 if your list items do not have an Id variable.
    }

    private static final int friend_request = 0;
    private static final int caravan = 1;
    private static final int friend = 2;
    private static final int ERROR = 3;
    public int getItemViewType(int position){
        String temp = list.get(position)[1];
        if(temp=="friend_request"){
            return friend_request;
        }else if(temp=="caravan") {
            return caravan;
        }else if(temp=="friend"){
            return friend;
        }else{
            return ERROR;
        }
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        int type = getItemViewType(position);
        if (holder == null) {
            holder = new ViewHolder();
            switch(type) {
                case friend_request:
                    convertView = mInflater.inflate(R.layout.friend_request_item, null);

                    //Handle buttons and add onClickListeners
                    Button deleteBtn = (Button)convertView.findViewById(R.id.delete_btn);
                    Button addBtn = (Button)convertView.findViewById(R.id.add_btn);


                    holder.text1 = (TextView)convertView.findViewById(R.id.list_item_string);
                    holder.add = addBtn;
                    holder.delete= deleteBtn;
                    holder.text1.setText(list.get(position)[2] + " wants to be your friend!");

                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new MyAsyncTask().execute("deny", Integer.toString(position));
                        }
                    });

                    addBtn.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            new MyAsyncTask().execute("friend_request", Integer.toString(position));
                        }
                    });
                    break;
                case caravan:
                    convertView = mInflater.inflate(R.layout.past_caravan_item, null);
                    holder.text1 = (TextView)convertView.findViewById(R.id.list_item_destination);
                    holder.text1.setText(list.get(position)[2]);
                    holder.add = (Button)convertView.findViewById(R.id.accept_caravan_btn);
                    holder.delete = (Button)convertView.findViewById(R.id.deny_caravan_btn);
                    holder.add.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            new MyAsyncTask().execute("accept_caravan", Integer.toString(position));
                        }
                    });

                    holder.delete.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            new MyAsyncTask().execute("deny_caravan", Integer.toString(position));
                        }
                    });
                    break;
                case friend:
                    convertView = mInflater.inflate(R.layout.friend_item,null);
                    holder.text1 = (TextView)convertView.findViewById(R.id.friend_item_string);
                    holder.text2 = (TextView) convertView.findViewById(R.id.friend_item_id);
                    holder.delete = (Button) convertView.findViewById(R.id.delete_friend);
                    holder.text1.setText(list.get(position)[2]);
                    holder.text2.setText(list.get(position)[3] + " is your friend's id");
                    holder.delete.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v) {
                            new MyAsyncTask().execute("delete",Integer.toString(position));
                        }
                    });
                case ERROR:
                    Logger logger = Logger.getAnonymousLogger();
                    Exception e = new NullPointerException("Not Friend, Request, or Caravan");
                    logger.log(Level.WARNING, "an exception was thrown", e);
            }
            convertView.setTag(holder);
        }
        return convertView;
    }

    public static class ViewHolder{

        public TextView text1;
        public TextView text2;
        public Button add;
        public Button delete;

    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

        String ERR = "ERROR";
        int SUCCESS = 1;
        int position = -1;
        Boolean added = false;
        Boolean denied = false;
        Boolean deleted = false;
        Boolean caravan_added = false;
        Boolean caravan_denied = false;

        @Override
        protected Double doInBackground(String... params) {
            if (params[0].equals("friend_request")) {
                friendRequest(params[0], Integer.valueOf(params[1]));
            }
            if(params[0].equals("deny")){
                denyRequest(params[0],Integer.valueOf(params[1]));
            }
            if(params[0].equals("delete")){
                deleteFriend(params[0],Integer.valueOf(params[1]));
            }
            if(params[0].equals("accept_caravan")){
                addCaravan(params[0],Integer.valueOf(params[1]));
            }
            if(params[0].equals("deny_caravan")){
                denyCaravan(params[0],Integer.valueOf(params[1]));
            }
            return null;
        }

        private void friendRequest(String parameter, int position1) {
            position = position1;
            HttpClient httpclient = new DefaultHttpClient();
            String extend_url = "users/" + homepage.get_user_id() + "/friends/accept/" + list.get(position)[2];
            HttpPost httppost = new HttpPost(homepage.url + extend_url);
            try {
                HttpResponse response = httpclient.execute(httppost);
                try {
                    JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    ERR = json.getString("reply_code");
                    if(ERR.equals("SUCCESS")){
                        added = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void denyRequest(String parameter, int position1) {
            position = position1;
            HttpClient httpclient = new DefaultHttpClient();
            String extend_url = "users/" + homepage.get_user_id() + "/friends/delete/" + list.get(position)[2];
            HttpPost httppost = new HttpPost(homepage.url + extend_url);
            try {
                HttpResponse response = httpclient.execute(httppost);
                try {
                    JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    ERR = json.getString("reply_code");
                    if(ERR.equals("SUCCESS")){
                        denied = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void addCaravan(String parameter, int position1) {
            position = position1;
            HttpClient httpclient = new DefaultHttpClient();
            String extend_url = "caravans/" + list.get(position)[2]+ "/accept/" + homepage.get_user_id();
            HttpPost httppost = new HttpPost(homepage.url + extend_url);
            try {
                HttpResponse response = httpclient.execute(httppost);
                try {
                    JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    ERR = json.getString("reply_code");
                    if(ERR.equals("SUCCESS")){
                        caravan_added = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void denyCaravan(String parameter, int position1) {
            position = position1;
            HttpClient httpclient = new DefaultHttpClient();
            String extend_url = "caravans/" + list.get(position)[2] + "/deny/" + homepage.get_user_id();
            HttpPost httppost = new HttpPost(homepage.url + extend_url);
            try {
                HttpResponse response = httpclient.execute(httppost);
                try {
                    JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    ERR = json.getString("reply_code");
                    if(ERR.equals("SUCCESS")){
                        caravan_denied = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void deleteFriend(String parameter, int position1) {
            position = position1;
            HttpClient httpclient = new DefaultHttpClient();
            String extend_url = "users/" + homepage.get_user_id() + "/friends/delete/" + list.get(position)[3];
            HttpPost httppost = new HttpPost(homepage.url + extend_url);
            try {
                HttpResponse response = httpclient.execute(httppost);
                try {
                    JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
                    ERR = json.getString("reply_code");
                    if(ERR.equals("SUCCESS")){
                        deleted = true;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        protected void onPostExecute(Double result){
            if (added){
                list.remove(position);
                notifyDataSetChanged();
            }
            if(denied){
                list.remove(position);
                notifyDataSetChanged();
            }
            if(deleted){
                list.remove(position);
                notifyDataSetChanged();
            }
            if(caravan_added){
                list.remove(position);
                homepage.set_caravanId(list.get(position)[2]);
                notifyDataSetChanged();
            }
            if(caravan_denied){
                list.remove(position);
                notifyDataSetChanged();
            }
        }

    }
}
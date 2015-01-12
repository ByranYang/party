package com.example.selena_wang.frontend;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class caravan_map extends FragmentActivity {

    private double myLatitude = 37.865265;
    private double myLongitude = -122.309411;
    private Location myLocation;
    public HashMap<String,LatLng> caravanLocations;
    private ArrayList<Marker> markers = new ArrayList<Marker>();
    private HashMap<String, Marker> marking = new HashMap<String, Marker>();
    private Boolean isHost = false;

    private ArrayList<LatLng> points_toDest = null;
    private int current_point_index = 0;

    private double destinationLat = 37.865265;
    private double destinationLong = -122.309411;
    private static boolean active = false;

    public static boolean getActive(){
        return active;
    }

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.caravan_map_layout);

        caravanLocations = new HashMap<String, LatLng>();
        // Initializing array List

        setUpMapIfNeeded();
        checkCaravan();

    }

    private void caravanTimer(){
        final Handler handler = new Handler();
        Timer timer = new Timer();
        TimerTask getCaravanInformation = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        new MyAsyncTask().execute("caravanInfo");
                    }
                });
            }
        };
//        TimerTask setLocation = new TimerTask() {
//            @Override
//            public void run() {
//                handler.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        new MyAsyncTask().execute("location");
//                    }
//                });
//            }
//        };
        timer.schedule(getCaravanInformation,0,8000);
//        timer.schedule(setLocation,0,4000);
    }

    private void checkCaravan(){new MyAsyncTask().execute("caravan");}

    public void onClickEnd(View view){
        if(isHost) {
            new MyAsyncTask().execute("end");
            Intent intent = new Intent(this, homepage.class);
            if (homepage.getActive()) {
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(intent);
        }
    }

//    @Override
//    protected void onResume() {
//        super.onResume();
//        setUpMap();
//    }

    @Override
    protected void onRestart(){
        super.onRestart();
        active = true;
    }

    @Override
    public void onStop() {
        super.onStop();
        active = false;
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
        }else if(id == R.id.create_icon){
            Intent intent = new Intent(this,create_caravan.class);
            if(create_caravan.getActive()){
                intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            }
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            if(mMap==null){
                throw new NullPointerException("mMap is null, meaning Google Play services APK is not installed");
            }
            mMap.setMyLocationEnabled(true);
            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                @Override
                public void onMapLoaded() {
                    setUpMap();
                }
            });
        }
    }

    private void setUpMap() {
        myLocation = mMap.getMyLocation();
        if (myLocation!=null) {
            myLatitude = myLocation.getLatitude();
            myLongitude = myLocation.getLongitude();
        }
        Marker dest_marker = mMap.addMarker(new MarkerOptions().position(new LatLng(destinationLat,destinationLong))
                .title("Destination").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
        markers.add(dest_marker);
        Iterator iterator = caravanLocations.entrySet().iterator();
        while (iterator.hasNext()){
            Map.Entry pair = (Map.Entry) iterator.next();
            Marker current = mMap.addMarker(new MarkerOptions().position((LatLng) pair.getValue())
                    .title((String) pair.getKey()).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
            marking.put(pair.getKey().toString(),current);
            markers.add(current);
        }

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for(Marker marker:markers){
            builder.include(marker.getPosition());
        }
        builder.include(new LatLng(myLatitude,myLongitude));
        LatLngBounds bounds = builder.build();
        CameraUpdate update = CameraUpdateFactory.newLatLngBounds(bounds,50);
        mMap.animateCamera(update);

        LatLng origin_latlng = new LatLng(myLatitude, myLongitude);
        LatLng dest_latlng = new LatLng(destinationLat,destinationLong);
        String url = getDirectionsUrl(origin_latlng, dest_latlng);

        DownloadTask downloadTask = new DownloadTask();

        downloadTask.execute(url);
    }

    private String getDirectionsUrl(LatLng origin,LatLng dest){

        String str_origin = "origin="+origin.latitude+","+origin.longitude;
        String str_dest = "destination="+dest.latitude+","+dest.longitude;

        String sensor = "sensor=false";


        String parameters = str_origin+"&"+str_dest+"&"+sensor;
        String output = "json";

        return "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
    }

    private String downloadUrl(String strUrl) throws IOException{
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            urlConnection = (HttpURLConnection) url.openConnection();

            // Connecting to url
            urlConnection.connect();

            // Reading data from url
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb  = new StringBuffer();

            String line = "";
            while( ( line = br.readLine())  != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception while downloading url", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }

    private class MyAsyncTask extends AsyncTask<String, Integer, Double> {

        @Override
        protected Double doInBackground(String... params) {
            if(params[0].equals("caravan")){
                getCaravan(params[0]);
            }
            if(params[0].equals("caravanInfo")){
                addCaravanInfo();
            }
            if(params[0].equals("end")){
                endCaravan(params[0]);
            }
//            if(params[0].equals("location")){
//                updateLocation(params[0]);
//            }
            return null;
        }

//        private boolean locationchanged = false;
//        private double new_lat;
//        private double new_long;
//        private void updateLocation(String parameter){
//
//            if(homepage.get_username().contains("test")){
//                new_lat = points_toDest.get(current_point_index).latitude;
//                new_long = points_toDest.get(current_point_index).longitude;
//            }else {
//                Location new_Location = mMap.getMyLocation();
//                new_lat = new_Location.getLatitude();
//                new_long = new_Location.getLongitude();
//            }
//            if(new_lat!=myLatitude || new_long!=myLongitude) {
//                locationchanged = true;
//                myLatitude = new_lat;
//                myLongitude = new_long;
//
//                String locations_url = "users/" + homepage.get_user_id() + "/location/";
//                HttpClient httpclient = new DefaultHttpClient();
//                HttpPost httpPost_location = new HttpPost(homepage.url + locations_url);
//
//                List data = new ArrayList();
//                data.add(new BasicNameValuePair("latitude", Double.toString(new_lat)));
//                data.add(new BasicNameValuePair("longitude", Double.toString(new_long)));
//                try {
//                    httpPost_location.setEntity(new UrlEncodedFormEntity(data));
//                } catch (UnsupportedEncodingException e) {
//                    e.printStackTrace();
//                }
//                try {
//                    HttpResponse response_caravans = httpclient.execute(httpPost_location);
//                    try {
//                        JSONObject json_caravan = new JSONObject(EntityUtils.toString(response_caravans.getEntity()));
//                        if (json_caravan.getString("reply_code") == homepage.SUCCESS) {
//                            locationchanged = true;
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }

        private Boolean caravanEnded = false;
        private void endCaravan(String parameter){
            String caravans_url = "caravans/" + homepage.get_caravanId() + "/end/" + homepage.get_user_id();
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost  httpPost_endCaravan = new HttpPost(homepage.url+caravans_url);
            try{
                HttpResponse response_caravans = httpclient.execute(httpPost_endCaravan);
                try{
                    JSONObject json_caravan = new JSONObject(EntityUtils.toString(response_caravans.getEntity()));
                    if (json_caravan.getString("reply_code")== homepage.SUCCESS){
                        caravanEnded = true;
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        private Boolean checkHost = false;
        private Boolean activeCaravan = false;
        private void getCaravan(String parameter){
            String caravans_url = "users/" + homepage.get_user_id() + "/caravans/active";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet  httpGet_caravans = new HttpGet(homepage.url+caravans_url);
            try{
                HttpResponse response_caravans = httpclient.execute(httpGet_caravans);
                try{
                    JSONObject json_caravan = new JSONObject(EntityUtils.toString(response_caravans.getEntity()));
                    String reply_code = json_caravan.getString("reply_code");
                    if (reply_code.equals("SUCCESS")){
//                        if(json_caravan.getString("host_id").equals(homepage.get_user_id()) && isHost == false){
//                            checkHost = true;
//                        }
                        JSONArray active_caravans = json_caravan.getJSONArray("caravan_ids");
                        for (int i = 0; i < active_caravans.length(); i++){
                            homepage.set_caravanId(active_caravans.getString(i));
                            activeCaravan = true;
                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        private JSONArray caravans_array;
        private HashMap<String, LatLng> list_file;
        private boolean addToList = false;
        private double destLat = 0;
        private double destLong = 0;

        private void addCaravanInfo(){
            String caravans_url = "caravans/" + "36";
            HttpClient httpclient = new DefaultHttpClient();
            HttpGet  httpGet_caravans = new HttpGet(homepage.url+caravans_url);
            try{
                HttpResponse response_caravans = httpclient.execute(httpGet_caravans);
                try{
                    JSONObject json_caravan = new JSONObject(EntityUtils.toString(response_caravans.getEntity()));
                    if (json_caravan.getString("reply_code").equals( homepage.SUCCESS)){

                        JSONObject destinationObject = json_caravan.getJSONObject("destination");
                        destLat = destinationObject.getDouble("latitude");
                        destLong = destinationObject.getDouble("longitude");

                        JSONObject participants = json_caravan.getJSONObject("participants");
                        list_file = new HashMap<String,LatLng>();
                        for (int i = 0; i < participants.names().length(); i++){
                            String user = participants.names().getString(i);
                            JSONObject user_object = participants.getJSONObject(user);
                            double Lat = user_object.getDouble("latitude");
                            double Long = user_object.getDouble("longitude");
                            list_file.put(user, new LatLng(Lat,Long));
                            addToList= true;
                        }
                    }
                }catch(JSONException e){
                    e.printStackTrace();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }

        protected void onPostExecute(Double result){
            if (addToList){
                Iterator iterator = list_file.entrySet().iterator();
                while (iterator.hasNext()){
                    Map.Entry pair = (Map.Entry)iterator.next();
                    if(caravanLocations.containsKey(pair.getKey())){
                        marking.get(pair.getKey()).remove();
                    }
                    caravanLocations.put((String)pair.getKey(),(LatLng)pair.getValue());
                }
                if(destLat!=0 && destLong!=0){
                    destinationLat = destLat;
                    destinationLong = destLong;
                }
                setUpMap();
            }

            if(caravanEnded){
                homepage.set_caravanId("");
            }

            if(activeCaravan){
                new MyAsyncTask().execute("caravanInfo");
                caravanTimer();
            }

            if(checkHost){
                isHost = true;
            }

//            if(locationchanged){
//                myLatitude = new_lat;
//                myLongitude = new_long;
//            }



        }



    }











    // Fetches data from url passed
    private class DownloadTask extends AsyncTask<String, Void, String>{

        // Downloading data in non-ui thread
        @Override
        protected String doInBackground(String... url) {

            // For storing data from web service
            String data = "";

            try{
                // Fetching the data from web service
                data = downloadUrl(url[0]);
            }catch(Exception e){
                Log.d("Background Task",e.toString());
            }
            return data;
        }

        // Executes in UI thread, after the execution of
        // doInBackground()
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }


    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>>> {

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adds all the points in the route to lineOptions
                lineOptions.addAll(points);
                lineOptions.width(10);
                lineOptions.color(Color.CYAN);
            }

            points_toDest = points;
            current_point_index = points.size();
            // Draws route to destination
            mMap.addPolyline(lineOptions);
        }
    }

}

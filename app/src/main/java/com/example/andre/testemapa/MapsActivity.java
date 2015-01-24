package com.example.andre.testemapa;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.content.Context.*;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    double longitude=0.0;
    double latitude=0.0;
    double mph=0.0;
    String currTime="";
    LocationManager lm;
    Location location;
    public TextView txtCTime;
    java.sql.Timestamp currentTimestamp;
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_maps);

        // setTheme(R.style.Theme_Holo_Light);
        setContentView(R.layout.fragment_blank);
        setUpMapIfNeeded();
        txtCTime=(TextView)findViewById(R.id.textLatLong);

        addLocationListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    private void addLocationListener()
    {
        lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        if(lm==null) {
            txtCTime.setText("Location Manager null");
            return;
        }
        Criteria c = new Criteria();
        c.setAccuracy(Criteria.ACCURACY_FINE);

        final String PROVIDER = lm.getBestProvider(c, true);

        this.myLocationListener = new MyLocationListener();
        this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0.0F, this.myLocationListener);
        //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0.0F, myLocationListener);
        txtCTime.setText("Esperando GPS");
    }

    TreadEnviaServidor tEnvia;
    public void updateLocation(Location location)
    {
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        mph = convertSpeed(location.getSpeed());
        currentTimestamp = new java.sql.Timestamp(Calendar.getInstance().getTime().getTime());
        currTime = String.format("%1$TD %1$TT", currentTimestamp);
        txtCTime.setText(currTime+" - lat: "+latitude+"\nlng: "+longitude+"\nkph: "+mph);

        tEnvia = new TreadEnviaServidor();
        tEnvia.latitude = latitude;
        tEnvia.longitude = longitude;
        tEnvia.mph = mph;
        tEnvia.currTime = currTime;

        tEnvia.execute("");

    }


    private double convertSpeed(float speed)
    {
        double mph =(int)(3.6D*speed);
        return mph;
    }


    MyLocationListener myLocationListener;
    class MyLocationListener implements LocationListener {
        @Override
        public void onLocationChanged(Location location) {
            updateLocation(location);
        }
        @Override
        public void onProviderDisabled(String provider) {}
        @Override
        public void onProviderEnabled(String provider) {}
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }
    ////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
    ////////////////////////////////////////////////
    public void ClicouMemoriza(View v)
    {
        // txtCTime=(TextView)findViewById(R.id.textLatLong);
        // txtCTime.setText(new Date().toString());
        // txtCTime.setText("csa de penha");

        if(latitude!=0.0)
        {
            // SendPostToServer();
            mMap.clear();
            mMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).title("Local"));

        }
        return;
    }
    ///////////////////////////////////////////////
    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }
    ///////////////////////////////////////////////
    // Pega valores de um site
    public static JSONObject getJSONfromURL(String url) {
        InputStream is = null;
        String result = "";
        JSONObject jArray = null;

        // Download JSON data from URL
        try {
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity entity = response.getEntity();
            is = entity.getContent();

        } catch (Exception e) {
            Log.e("log_tag", "Error in http connection " + e.toString());
        }

        // Convert response to string
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "iso-8859-1"), 8);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            result = sb.toString();
        } catch (Exception e) {
            Log.e("log_tag", "Error converting result " + e.toString());
        }

        try {

            jArray = new JSONObject(result);
        } catch (JSONException e) {
            Log.e("log_tag", "Error parsing data " + e.toString());
        }

        return jArray;
    }
    ///////////////////////////////////////////////
    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p/>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p/>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    /**
     * This is where we can add markers or lines, add listeners or move the camera. In this case, we
     * just add a marker near Africa.
     * <p/>
     * This should only be called once and when we are sure that {@link #mMap} is not null.
     */
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Local"));
    }


}




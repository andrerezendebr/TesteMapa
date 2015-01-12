package com.example.andre.testemapa;

import android.content.Context;
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

import java.util.Date;

import static android.content.Context.*;

public class MapsActivity extends FragmentActivity {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.

    double longitude;
    double latitude;
    LocationManager lm;
    Location location;
    public TextView txtCTime;
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
        this.lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0L, 0.0F, this.myLocationListener);
        //lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0L, 0.0F, myLocationListener);
        txtCTime.setText("Esperando GPS");
    }

    public void updateLocation(Location location)
    {
        double latitude, longitude;

        latitude = location.getLatitude();
        longitude = location.getLongitude();
        int mph = convertSpeed(location.getSpeed());
        txtCTime.setText("lat: "+latitude+" lng: "+longitude+" mph: "+mph);
    }


    private int convertSpeed(float speed) {
        int mph =(int)(2.236936D * speed);
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
    // lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 10, locationListener);



    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }


    ////////////////////////////////////////////////
    public void ClicouMemoriza(View v)
    {
        txtCTime=(TextView)findViewById(R.id.textLatLong);
        txtCTime.setText(new Date().toString());
        Log.d("Debug","Teste");
        return;
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

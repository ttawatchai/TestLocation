package com.example.n007.testlocation;
import android.content.Context;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.MessageFormat;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    TextView Tvlati, Tvlong;
    GoogleApiClient googleApiClient;
    private LocationManager mLocationManager = null;
    private boolean gpsSupported = false;
    private Handler mHandler;
    private int lastStatus;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tvlati = (TextView) findViewById(R.id.lati);
        Tvlong = (TextView) findViewById(R.id.longti);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();


    }
    @Override
    public void onStart() {
        super.onStart();

        googleApiClient.connect();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (googleApiClient != null && googleApiClient.isConnected()) {
            googleApiClient.disconnect();
        }
    }
    @Override
    public void onConnected(Bundle bundle) {
        LocationAvailability locationAvailability = LocationServices.FusedLocationApi.getLocationAvailability(googleApiClient);
        if(locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(5000);
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
        } else {

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        double latitude = location.getLatitude();
        double longitude = location.getLongitude();

        Tvlati.setText(String.valueOf(latitude));
        Tvlong.setText(String.valueOf(longitude));

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


//    @Override
//    public void onGpsStatusChanged(int event) {
//
//    }
//    private void initGps() {
//        try {
//            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//
//            // Check GPS enabled
//            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//
//
//                gpsSupported = true;
//
//                mLocationManager.requestLocationUpdates("gps", 0, 0.0f, this);
//                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50000,, this);
//                mLocationManager.addGpsStatusListener(this);
//
//                mHandler = new Handler();
//                mLastFixUpdater = new LastFixUpdater();
//                mHandler.post(mLastFixUpdater);
//
//            } else {
//                writeLog(LogType.Error, AppConstants.gps, "initGps", AppConstants.gpsDisabled, null);
//                showGPSDisabledAlertToUser();
//            }
//
//        } catch (Exception e) {
//            writeLog(LogType.Error, AppConstants.gps, "initGps", AppConstants.gpsNotSupport, e.getMessage());
//            showToast(MessageFormat.format("{0} -> {1}", AppConstants.gpsNotSupport, e.getMessage()));
//        }
//    }
}


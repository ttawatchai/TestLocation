package com.example.n007.testlocation;
import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener,GpsStatus.Listener
{

    TextView Tvlati, Tvlong, Tvlati1, Tvlong1;
    GoogleApiClient googleApiClient;
    private LocationManager mLocationManager = null;
   private Double lattitude = 13.6972 * 1E6;
    private Double longitude = 100.5150 * 1E6;
    private String mLatitude = "0";
    private String mLongitude = "0";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tvlati = (TextView) findViewById(R.id.lati);
        Tvlong = (TextView) findViewById(R.id.longti);
        Tvlati1 = (TextView) findViewById(R.id.lati2);
        Tvlong1 = (TextView) findViewById(R.id.longti2);
        initGps();
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
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

        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(10000);
            LocationListener locationListener = new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {
                    double latitude = location.getLatitude();
                    double longitude = location.getLongitude();

                    Tvlati.setText(String.valueOf(latitude));
                    Tvlong.setText(String.valueOf(longitude));
                }
            };
            LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, locationListener);

        } else {

        }
    }
    @Override
    public void onConnectionSuspended(int i) {

    }
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
    @Override
    public void onLocationChanged(Location loc) {
        try {
            if (loc == null) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, (android.location.LocationListener) this);
            } else {


                updateLocation(loc);

                   }
        } catch (Exception e) {

                e.printStackTrace();
        }
    }

    private void updateLocation(Location loc) {
        try {
            mLatitude = String.valueOf(loc.getLatitude());
            mLongitude = String.valueOf(loc.getLongitude());
            Tvlati1.setText(mLatitude);
            Tvlong1.setText(mLongitude);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void initGps() {
        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);


            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {


                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mLocationManager.requestLocationUpdates("gps", 0, 0.0f, locationListener);
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
                mLocationManager.addGpsStatusListener(this);

            } else {

                showGPSDisabledAlertToUser();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        // GPS is disabled in your device. Would you like to enable it?
        alertDialogBuilder.setMessage("ขณะนี้ GPS ของเครื่องปิดอยู่ คุณต้องการจะเปิดหรือไม่?");
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton(
                // "Goto Settings Page To Enable GPS",
                "ต้องการ", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(
                                android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                    }
                });

        alertDialogBuilder.setNegativeButton("ไม่ต้องการ", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });

        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
        googleApiClient.connect();
    }
    private final android.location.LocationListener locationListener = new android.location.LocationListener() {
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        public void onProviderDisabled(String provider) {
            updateWithNewLocation(null);
            Log.e("GPS", "provider disabled " + provider);
        }
        public void onProviderEnabled(String provider) {
            Log.e("GPS", "provider enabled " + provider);
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.e("GPS", "status changed to " + provider + " [" + status + "]");
        }
    };

    private void updateWithNewLocation(Location location) {
        try {
            if (location == null) {
                lattitude = 0.00; // 13.6972 * 1E6;
                longitude = 0.00; // 100.5150 * 1E6;
               } else {
                lattitude = location.getLatitude();
                longitude = location.getLongitude();
                    }

            Tvlati1.setText(String.valueOf(lattitude));
            Tvlong1.setText(String.valueOf(longitude));
            // TEST BY TIP PC

        } catch (Exception e) {
            // TEST BY TIP PC
            e.printStackTrace();

        }
    }

    @Override
    public void onGpsStatusChanged(int event) {

    }
}



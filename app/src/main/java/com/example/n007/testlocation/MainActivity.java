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
import android.location.LocationProvider;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.MessageFormat;
import java.util.logging.Logger;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener, GpsStatus.Listener {

    TextView Tvlati, Tvlong, Tvlati1, Tvlong1;
    GoogleApiClient googleApiClient;
    // Gps
    private String pdaversion = "";
    private String imei;// = null;
    private final int MIN_DISTANCE = 10; // Meter
    private final int MIN_TIME = 30000; // Millisecond
    private PowerManager mPowerManager;
    private PowerManager.WakeLock mWakeLock;
    private Logger logger;
    private Double lattitude = 13.6972 * 1E6;
    private Double longitude = 100.5150 * 1E6;
    private long mClockSkew;
    private LastFixUpdater mLastFixUpdater;
    private long mLastUpdateTime = -1;
    private int aTimes = 0; // Loop times
    private int mac = 3; // Limit counter of sending tracking loop
    private int lastStatus; // Last GPS status
    private int minimumFixedSatellite = 1;
    private Handler mHandler;
    private int gprsLossConnectionCount = 0;
    private boolean gprsLossConnectionCountEnable = true;
    private LocationManager mLocationManager = null;
    private boolean gpsSupported = false;
    private SendingCountDownTimer counter = new SendingCountDownTimer(15000, 1000, this);
    private boolean isOpenTracking = false;

    private FrameLayout logo_wrapper;
    private LinearLayout layout_tab_two;
    private LinearLayout titleview;

    private TextView latitudeEditText;

    private TextView longitudeEditText;
    private TextView gpsInfoTextView;
    private TextView version;
    private EditText errorEditText;
    private CheckBox gpsEnabledCheckBox;
    private String speed = "";
    private String direction = "";
    private String webServiceResponseMessage = "";
    private String status = "cl";
    private String docno;
    private String currentJobId;
    private int currentStatus;
    private String wifi;

    private GpsStatus mGpsStatus = null;
    private String mLatitude = "0";
    private String mLongitude = "0";
    private String mAccuracy;
    private String mAltitude;
    private String mBearing; // direction
    private String mSpeed;
    private String mTime;
    private String mDeviceTime;
    private String mTtff;
    private String mTslf;
    private int mSatInSky;
    private int mSatInFix;
    private String mState;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Tvlati = (TextView) findViewById(R.id.lati);
        Tvlong = (TextView) findViewById(R.id.longti);
        Tvlati1 = (TextView) findViewById(R.id.lati2);
        Tvlong1 = (TextView) findViewById(R.id.longti2);
        googleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        initGps();


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


    private void initGps() {
        try {
            mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            // Check GPS enabled
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                gpsSupported = true;
                mLocationManager.requestLocationUpdates("gps", 0, 0.0f, (android.location.LocationListener) this);
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
                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MIN_DISTANCE, (android.location.LocationListener) this);
                mLocationManager.addGpsStatusListener(this);

                mHandler = new Handler();
                mLastFixUpdater = new LastFixUpdater();
                mHandler.post(mLastFixUpdater);
            } else {

                showGPSDisabledAlertToUser();
            }

        } catch (Exception e) {


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
    }

    private class LastFixUpdater implements Runnable {
        @Override
        public void run() {
            updateLastUpdateTime();
            mHandler.postDelayed(this, 1000);
        }
    }

    private void updateLastUpdateTime() {
        try {
            if (mLastUpdateTime >= 0) {
                long t = Math.round((System.currentTimeMillis() - mLastUpdateTime - mClockSkew) / 1000);
                long sec = t % 60;
                long min = (t / 60);
                mTslf = String.format("%d:%02d", min, sec);
            }

            mDeviceTime = String.valueOf(System.currentTimeMillis());
        } catch (Exception e) {

        }
    }


    @Override
    public void onGpsStatusChanged(int state) {
        try {
            lastStatus = state;
            switch (lastStatus) {
                case LocationProvider.TEMPORARILY_UNAVAILABLE:
//                    writeLog(LogType.Error, AppConstants.gps, "onGpsStatusChanged", AppConstants.gpsTemporarilyUnavailable,
//                            null);
//                    showToast(AppConstants.gpsTemporarilyUnavailable);
                    break;
                case LocationProvider.OUT_OF_SERVICE:
//                    writeLog(LogType.Error, AppConstants.gps, "onGpsStatusChanged", AppConstants.gpsOutOfService, null);
//                    showToast(AppConstants.gpsOutOfService);
                    break;
                case LocationProvider.AVAILABLE:
//                    writeLog(LogType.Information, AppConstants.gps, "onGpsStatusChanged", AppConstants.gpsAvailable, null);
//                    showToast(AppConstants.gpsAvailable);
                    break;
            }

            setGpsStatus();
        } catch (Exception e) {

        }
    }

    protected void setGpsStatus() {
        try {
            mSatInSky = 0;
            mSatInFix = 0;

            String message;
            if (mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mGpsStatus = mLocationManager.getGpsStatus(mGpsStatus);
                Iterable<GpsSatellite> sats = mGpsStatus.getSatellites();
                for (GpsSatellite s : sats) {
                    mSatInSky += 1;
                    if (s.usedInFix()) {
                        mSatInFix += 1;
                    }
                }

                mState = mSatInFix >= minimumFixedSatellite ? AppConstants.gpsStateLock : AppConstants.gpsStateOn;
                message = MessageFormat.format("in sky {0}, in fix {1}, state {2}", mSatInSky, mSatInFix, mState);

                mTtff = String.valueOf(mGpsStatus.getTimeToFirstFix());
                Tvlati1.setText(message);
                Tvlati1.postInvalidate();
            } else {
                mState = AppConstants.gpsStateOff;
                message = MessageFormat.format("in sky {0}, in fix {1}, state {2}", mSatInSky, mSatInFix, mState);

                Tvlong1.setText(message);
                Tvlong1.postInvalidate();
            }
        } catch (Exception e) {

        }
    }

    public class SendingCountDownTimer extends CountDownTimer {
        private Context context;

        /**
         * @param millisInFuture
         * @param countDownInterval
         * @param context
         */
        public SendingCountDownTimer(long millisInFuture, long countDownInterval, Context context) {
            super(millisInFuture, countDownInterval);
            this.context = context;
        }

        @Override
        public void onTick(long millisUntilFinished) {

        }

        @Override
        public void onFinish() {
            try {
                if (MainActivity.this.isFinishing()) {
                    this.start();
                }
                if (aTimes < mac) {
                    aTimes++;

                } else {
                    aTimes = 0;
                    String sentLatitude;
                    String sentLongitude;
                    if (lastStatus != LocationProvider.OUT_OF_SERVICE) {
                        sentLatitude = latitudeEditText.getText().toString();
                        sentLongitude = longitudeEditText.getText().toString();
                    } else {
                        sentLatitude = "0";
                        sentLongitude = "0";
                    }
                    if (gprsLossConnectionCountEnable) {


                    }
                }
            } catch (Exception e) {
                gprsLossConnectionCount += 1;


            }
        }
    }
}


package com.example.mxu24.wiperdata;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.openxc.NoValueException;
import com.openxc.VehicleManager;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.UnrecognizedMeasurementTypeException;
import com.openxc.measurements.VehicleSpeed;
import com.openxc.measurements.WindshieldWiperStatus;

public class DataCollectService extends Service implements  WiperDataSource.DataListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener{

    public static final String BROADCAST_ACTION = "com.example.mxu24.wiperdata";
    private final static String TAG = "WiperDataService";
    private Location mLocation;
    private VehicleManager mVehicleManager;
    //private LocationManager mLocationManager;
    private WiperDataSource mWiperDataSource;
    private GoogleApiClient mGoogleApiClient;
    Handler myHandler;

    Handler queryVehicleHandler;
    Runnable queryVehicleRunnable;

    private final IBinder mBinder = new LocalBinder();
    Intent intent;
    @Override
    public void onLocationChanged(Location location) {
        this.mLocation = location;
    }


    public class LocalBinder extends Binder {
        DataCollectService getService(){
            return DataCollectService.this;
        }
    }

    public DataCollectService() {
    }

    @Override
    public void onCreate(){
        //one time setup procedure
        super.onCreate();
        //mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        buildGoogleApiClient();
        if(mVehicleManager == null){
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
        //HandlerThread thread = new HandlerThread()
        myHandler = new Handler();
        intent = new Intent(BROADCAST_ACTION);
        queryVehicleHandler = new Handler();
        queryVehicleRunnable = new Runnable() {
            @Override
            public void run() {
                //TODO:query vehicle data
                if (mVehicleManager != null) {
                    try {
                        WindshieldWiperStatus wiperStatus = (WindshieldWiperStatus) mVehicleManager.get(WindshieldWiperStatus.class);
                        VehicleSpeed vehicleSpeed = (VehicleSpeed) mVehicleManager.get(VehicleSpeed.class);
                        mWiperDataSource.updateData(vehicleSpeed, wiperStatus);

                    } catch (NoValueException e) {
                        Log.w(TAG, "The vehicle may not have made the measurement yet");
                    } catch (UnrecognizedMeasurementTypeException e) {
                        Log.w(TAG, "The measurement type was not recognized");
                    }
                    queryVehicleHandler.postDelayed(queryVehicleRunnable, 1000);
                }
            }

        };
        queryVehicleHandler.postDelayed(queryVehicleRunnable, 1000);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        return super.onStartCommand(intent, flags, startId);
    }
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onDestroy(){
        mVehicleManager = null;
        super.onDestroy();
    }



    /**
     * Get Vehicle Message
     */

    WindshieldWiperStatus.Listener mWiperListener = new WindshieldWiperStatus.Listener() {
        public void receive(Measurement measurement) {

           final WindshieldWiperStatus wiperStatus = (WindshieldWiperStatus) measurement;
           Log.i(TAG,"Service get wiper data");
           /*this.runOnUiThread(new Runnable() {
                public void run() {
                    //TODO:update UI or data
                    Log.i(TAG, "got wiper data update");
                    if(wiperDataSource!=null&&locationManager!=null)
                        wiperDataSource.updateWiperStatus(wiperStatus);
                }
            });*/
            //Handler myHandler = new Handler();
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mWiperDataSource!=null&&mGoogleApiClient!=null)
                        mWiperDataSource.updateWiperStatus(wiperStatus);
                }
            });
            //if(mWiperDataSource!=null&&mGoogleApiClient!=null)
                //mWiperDataSource.updateWiperStatus(wiperStatus);
        }
    };

    VehicleSpeed.Listener mSpeedListener = new VehicleSpeed.Listener(){

        @Override
        public void receive(Measurement measurement) {
            final VehicleSpeed vehicleSpeed = (VehicleSpeed) measurement;
            Log.i(TAG,"Service get speed data");
            /*MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //TODO:update UI or data
                    Log.i(TAG,"got vehicle speed data update");
                    if(wiperDataSource!=null&&locationManager!=null)
                        wiperDataSource.updateVehicleSpeed(vehicleSpeed);
                }
            });*/
            //Handler myHandler = new Handler();
            myHandler.post(new Runnable() {
                @Override
                public void run() {
                    if(mWiperDataSource!=null&&mGoogleApiClient!=null)
                        mWiperDataSource.updateVehicleSpeed(vehicleSpeed);
                }
            });
            //if(mWiperDataSource!=null&&mGoogleApiClient!=null)
                //mWiperDataSource.updateVehicleSpeed(vehicleSpeed);
        }

    };

    private ServiceConnection mConnection = new ServiceConnection() {
        // Called when the connection with the VehicleManager service is established
        public void onServiceConnected(ComponentName className, IBinder service) {
            Log.i("openxc", "Bound to VehicleManager");
            // When the VehicleManager starts up, we store a reference to it
            // here in "mVehicleManager" so we can call functions on it
            // elsewhere in our code.
            mVehicleManager = ((VehicleManager.VehicleBinder) service)
                    .getService();

            // We want to receive updates whenever the EngineSpeed changes. We
            // have an EngineSpeed.ListeVehicleMessageBufferner (see above, mSpeedListener) and here
            // we request that the VehicleManager call its receive() method
            // whenever the EngineSpeed changes
            //mVehicleManager.addListener(WindshieldWiperStatus.class, mWiperListener);
            //mVehicleManager.addListener(VehicleSpeed.class, mSpeedListener);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w("openxc", "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;
        }
    };


    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        mWiperDataSource = new WiperDataSource(this, mGoogleApiClient);
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    @Override
    public void receive(WiperData data) {
        //TODO:send through Internet to Database
        Log.i(TAG,"Receive Wiper Data");
        //wiperTextView.setText(String.valueOf(data.getWiperStatus()));
        //speedTextView.setText(String.valueOf(data.getVehicleSpeed()));
        String latitude = String.valueOf(data.getVehicleLocation().getLatitude());
        String longitude = String.valueOf(data.getVehicleLocation().getLongitude());
        //positionTextView.setText("Latitude:"+latitude+" Altitude:"+altitude);
        //timeTextView.setText(data.getTimeStamp().toString());

        intent.putExtra("wiperstatus", data.getWiperStatus().toString());
        intent.putExtra("vehiclespeed",data.getVehicleSpeed().toString());
        intent.putExtra("timestamp",data.getTimeStamp().toString());
        intent.putExtra("location",data.getVehicleLocation().toString());
        sendBroadcast(intent);
    }
}

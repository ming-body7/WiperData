package com.example.mxu24.wiperdata;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.openxc.VehicleManager;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.VehicleSpeed;
import com.openxc.measurements.WindshieldWiperStatus;

public class DataCollectService extends Service implements  WiperDataSource.DataListener{

    private final static String TAG = "WiperDataService";
    private Location mLocation;
    private VehicleManager mVehicleManager;
    private LocationManager mLocationManager;
    private WiperDataSource mWiperDataSource;

    private final IBinder mBinder = new LocalBinder();



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
        mLocationManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        mWiperDataSource = new WiperDataSource(this);

        if(mVehicleManager == null){
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }

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

    LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            //call the location update
            updateLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void updateLocation(Location location) {
        this.mLocation = location;
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
            if(mWiperDataSource!=null&&mLocationManager!=null)
                mWiperDataSource.updateWiperStatus(wiperStatus);
        }
    };

    VehicleSpeed.Listener mSpeedListener = new VehicleSpeed.Listener(){

        @Override
        public void receive(Measurement measurement) {
            final VehicleSpeed vehicleSpeed = (VehicleSpeed) measurement;
            Log.i(TAG,"Service get wiper data");
            /*MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //TODO:update UI or data
                    Log.i(TAG,"got vehicle speed data update");
                    if(wiperDataSource!=null&&locationManager!=null)
                        wiperDataSource.updateVehicleSpeed(vehicleSpeed);
                }
            });*/

            if(mWiperDataSource!=null&&mLocationManager!=null)
                mWiperDataSource.updateVehicleSpeed(vehicleSpeed);
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
            mVehicleManager.addListener(WindshieldWiperStatus.class, mWiperListener);
            mVehicleManager.addListener(VehicleSpeed.class, mSpeedListener);
        }

        // Called when the connection with the service disconnects unexpectedly
        public void onServiceDisconnected(ComponentName className) {
            Log.w("openxc", "VehicleManager Service  disconnected unexpectedly");
            mVehicleManager = null;
        }
    };

    @Override
    public void receive(WiperData data) {

    }
}

package com.example.mxu24.wiperdata;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.openxc.VehicleManager;
import com.openxc.measurements.Measurement;
import com.openxc.measurements.VehicleSpeed;
import com.openxc.measurements.WindshieldWiperStatus;


public class MainActivity extends ActionBarActivity implements WiperDataSource.DataListener{

    private final static String TAG = "WiperData";
    private Location location;
    private VehicleManager mVehicleManager;
    public LocationManager locationManager;// = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    private WiperDataSource wiperDataSource;

    private TextView wiperTextView;
    private TextView speedTextView;
    private TextView positionTextView;
    private TextView timeTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        wiperDataSource = new WiperDataSource(this, this);

        wiperTextView = (TextView)findViewById(R.id.wiperTextView);
        speedTextView = (TextView)findViewById(R.id.speedTextView);
        positionTextView = (TextView)findViewById(R.id.positionTextView);
        timeTextView = (TextView)findViewById(R.id.timeTextView);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onResume(){
        super.onResume();
        //LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(mVehicleManager == null) {
            Intent intent = new Intent(this, VehicleManager.class);
            bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        }
    }

    @Override public void onPause(){
        mVehicleManager = null;
        super.onPause();
    }
    @Override public void onStop(){
        mVehicleManager = null;
        super.onStop();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
        this.location = location;
    }


    /**
     * Get Vehicle Message
     */

    WindshieldWiperStatus.Listener mWiperListener = new WindshieldWiperStatus.Listener() {
        public void receive(Measurement measurement) {
            // When we receive a new EngineSpeed value from the car, we want to
            // update the UI to display the new value. First we cast the generic
            // Measurement back to the type we know it to be, an EngineSpeed.
            final WindshieldWiperStatus wiperStatus = (WindshieldWiperStatus) measurement;
            // In order to modify the UI, we have to make sure the code is
            // running on the "UI thread" - Google around for this, it's an
            // important concept in Android.
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //TODO:update UI or data
                    Log.i(TAG,"got wiper data update");
                    if(wiperDataSource!=null&&locationManager!=null)
                    wiperDataSource.updateWiperStatus(wiperStatus);
                }
            });
        }
    };

    VehicleSpeed.Listener mSpeedListener = new VehicleSpeed.Listener(){

        @Override
        public void receive(Measurement measurement) {
            final VehicleSpeed vehicleSpeed = (VehicleSpeed) measurement;
            MainActivity.this.runOnUiThread(new Runnable() {
                public void run() {
                    //TODO:update UI or data
                    Log.i(TAG,"got vehicle speed data update");
                    if(wiperDataSource!=null&&locationManager!=null)
                    wiperDataSource.updateVehicleSpeed(vehicleSpeed);
                }
            });
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


    /**
     *
     * Get Wiper Data Set Update
     */
    @Override
    public void receive(WiperData data) {
        //TODO:send through Internet to Database
        Log.i(TAG,"Receive Wiper Data");
        wiperTextView.setText(String.valueOf(data.getWiperStatus()));
        speedTextView.setText(String.valueOf(data.getVehicleSpeed()));
        String latitude = String.valueOf(data.getVehicleLocation().getLatitude());
        String altitude = String.valueOf(data.getVehicleLocation().getAltitude());
        positionTextView.setText("Latitude:"+latitude+" Altitude:"+altitude);
        timeTextView.setText(data.getTimeStamp().toString());
    }
}

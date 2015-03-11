package com.example.mxu24.wiperdata;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity{

    private final static String TAG = "WiperData";
    private Location location;
    //private VehicleManager mVehicleManager;
    public LocationManager locationManager;// = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
    private WiperDataSource wiperDataSource;

    private TextView wiperTextView;
    private TextView speedTextView;
    private TextView positionTextView;
    private TextView timeTextView;

    DataCollectService mService;
    boolean mBound = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        //wiperDataSource = new WiperDataSource(this, this);

        wiperTextView = (TextView)findViewById(R.id.wiperTextView);
        speedTextView = (TextView)findViewById(R.id.speedTextView);
        positionTextView = (TextView)findViewById(R.id.positionTextView);
        timeTextView = (TextView)findViewById(R.id.timeTextView);

        Intent intent = new Intent(this, DataCollectService.class);
        startService(intent);
        //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateData(intent);
        }
    };

    private void updateData(Intent intent) {

        wiperTextView.setText(intent.getStringExtra("wiperstatus"));
        speedTextView.setText(intent.getStringExtra("vehiclespeed"));
        timeTextView.setText(intent.getStringExtra("timestamp"));
        positionTextView.setText(intent.getStringExtra("location"));
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
        //if(mVehicleManager == null) {
            //Intent intent = new Intent(this, VehicleManager.class);
            //bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
        //}
        registerReceiver(broadcastReceiver, new IntentFilter(DataCollectService.BROADCAST_ACTION));

    }

    @Override public void onPause(){
        //mVehicleManager = null;
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }
    @Override public void onStop(){
        //mVehicleManager = null;
        super.onStop();
        if(mBound){
            //unbindService(mConnection);
        }


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



    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            DataCollectService.LocalBinder binder = (DataCollectService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };


}

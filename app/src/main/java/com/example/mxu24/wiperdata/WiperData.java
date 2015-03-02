package com.example.mxu24.wiperdata;

/**
 * Created by MXU24 on 2/27/2015.
 */

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

import java.sql.Timestamp;

/**
 * wiper status, GPS information, timestamp, vehicle speed,
 */
public class WiperData {

    private Boolean wiperStatus;
    private Location vehicleLocation;
    private Timestamp timeStamp;
    private Double vehicleSpeed;
    private Context context;

    public WiperData(Context context, Boolean wiperStatus, Double vehicleSpeed){
        this.wiperStatus = wiperStatus;
        this.vehicleSpeed = vehicleSpeed;
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        this.context = context;
        //LocationManager locationManager = (LocationManager)
                //getSystemService(Context.LOCATION_SERVICE);
        MainActivity activity = (MainActivity)context;
        String locationProvider = LocationManager.GPS_PROVIDER;
        this.vehicleLocation = activity.locationManager.getLastKnownLocation(locationProvider);
    }
    public WiperData(Context context, Boolean wiperStatus){
        this.wiperStatus = wiperStatus;
        this.vehicleSpeed = 0.0;
        this.context = context;
        //this.timeStamp = new Timestamp(System.currentTimeMillis());
        //LocationManager locationManager = (LocationManager)
        //getSystemService(Context.LOCATION_SERVICE);
        updateLocationAndTimestamp();
    }
    public WiperData(Context context, Double vehicleSpeed){
        this.wiperStatus = false;
        this.vehicleSpeed = vehicleSpeed;
        this.context = context;
        //this.timeStamp = new Timestamp(System.currentTimeMillis());
        updateLocationAndTimestamp();
    }

    public void updateVehicleSpeedStatus(Double vehicleSpeedData) {
        this.vehicleSpeed = vehicleSpeedData;
        updateLocationAndTimestamp();

    }

    public void updateWiperStatus(Boolean wiperStatusData) {
        this.wiperStatus = wiperStatusData;
        updateLocationAndTimestamp();
    }

    private void updateLocationAndTimestamp(){
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        //LocationManager locationManager = (LocationManager)
        //getSystemService(Context.LOCATION_SERVICE);
        MainActivity activity = (MainActivity)context;
        //String locationProvider = LocationManager.GPS_PROVIDER;
        String locationProvider = LocationManager.NETWORK_PROVIDER;
        this.vehicleLocation = activity.locationManager.getLastKnownLocation(locationProvider);
    }

    public Boolean getWiperStatus() {
        return wiperStatus;
    }

    public Location getVehicleLocation() {
        return vehicleLocation;
    }

    public Timestamp getTimeStamp() {
        return timeStamp;
    }

    public Double getVehicleSpeed() {
        return vehicleSpeed;
    }
}

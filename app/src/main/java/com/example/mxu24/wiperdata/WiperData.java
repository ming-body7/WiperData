package com.example.mxu24.wiperdata;

/**
 * Created by MXU24 on 2/27/2015.
 */

import android.content.Context;
import android.location.Location;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

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
    private GoogleApiClient mGoogleApiClient;

    public WiperData(Boolean wiperStatus, Double vehicleSpeed, GoogleApiClient mGoogleApiClient){
        this.wiperStatus = wiperStatus;
        this.vehicleSpeed = vehicleSpeed;
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        this.mGoogleApiClient = mGoogleApiClient;
        updateLocationAndTimestamp(mGoogleApiClient);
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
        //updateLocationAndTimestamp();
        updateLocationAndTimestamp(mGoogleApiClient);
    }
    public WiperData(Boolean wiperStatus, GoogleApiClient mGoogleApiClient){
        this.wiperStatus = wiperStatus;
        this.vehicleSpeed = 0.0;
        this.mGoogleApiClient = mGoogleApiClient;
        //this.timeStamp = new Timestamp(System.currentTimeMillis());
        updateLocationAndTimestamp(mGoogleApiClient);
    }
    public WiperData(Double vehicleSpeed, GoogleApiClient mGoogleApiClient){
        this.wiperStatus = false;
        this.vehicleSpeed = vehicleSpeed;
        this.mGoogleApiClient = mGoogleApiClient;
        //this.timeStamp = new Timestamp(System.currentTimeMillis());
        updateLocationAndTimestamp(mGoogleApiClient);
    }



    public void updateVehicleSpeedStatus(Double vehicleSpeedData) {
        this.vehicleSpeed = vehicleSpeedData;
        updateLocationAndTimestamp(mGoogleApiClient);

    }

    public void updateWiperStatus(Boolean wiperStatusData) {
        this.wiperStatus = wiperStatusData;
        updateLocationAndTimestamp(mGoogleApiClient);
    }

    private void updateLocationAndTimestamp(){
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        //LocationManager locationManager = (LocationManager)
        //getSystemService(Context.LOCATION_SERVICE);
        //MainActivity activity = (MainActivity)context;
        //String locationProvider = LocationManager.GPS_PROVIDER;
        //String locationProvider = LocationManager.NETWORK_PROVIDER;
        //this.vehicleLocation = activity.locationManager.getLastKnownLocation(locationProvider);
        this.vehicleLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
    }
    private void updateLocationAndTimestamp(GoogleApiClient mGoogleApiClient) {
        this.timeStamp = new Timestamp(System.currentTimeMillis());
        this.vehicleLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
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

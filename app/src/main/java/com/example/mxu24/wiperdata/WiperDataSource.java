package com.example.mxu24.wiperdata;

/**
 * Created by MXU24 on 2/27/2015.
 */

import android.content.Context;

import com.openxc.measurements.VehicleSpeed;
import com.openxc.measurements.WindshieldWiperStatus;

/**
 * The data source is used to get the wiper data from vehicle and format it
 * Tht output should be a WiperData
 *
 */
public class WiperDataSource {

    private Context main;
    private DataListener mainDataListener;
    private WiperData wiperData;

    public interface DataListener{
        public void receive(WiperData data);
    }

    public WiperDataSource(DataListener listener, Context context){
        this.main = context;
        this.mainDataListener = listener;

    }
    public WiperDataSource(DataListener listener){
        //this.main = context;
        this.mainDataListener = listener;

    }

    public void updateVehicleSpeed(VehicleSpeed vehicleSpeed){
        Double vehicleSpeedData = vehicleSpeed.toVehicleMessage().asSimpleMessage().getValueAsNumber().doubleValue();
        if(wiperData != null){
            wiperData.updateVehicleSpeedStatus(vehicleSpeedData);
        }else{
            wiperData = new WiperData(main, vehicleSpeedData);
        }
        notifyDataChange();
    }
    public void updateWiperStatus(WindshieldWiperStatus wiperStatus){
        Boolean wiperStatusData = wiperStatus.toVehicleMessage().asSimpleMessage().getValueAsBoolean();
        if(wiperData != null){
            wiperData.updateWiperStatus(wiperStatusData);
        }else{
            wiperData = new WiperData(main, wiperStatusData);
        }
        notifyDataChange();
    }

    private void notifyDataChange(){
        mainDataListener.receive(wiperData);
    }

}

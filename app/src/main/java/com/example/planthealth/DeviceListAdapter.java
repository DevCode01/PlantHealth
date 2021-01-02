package com.example.planthealth;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class DeviceListAdapter extends ArrayAdapter<BluetoothDevice> {
    public DeviceListAdapter(Context context, ArrayList<BluetoothDevice> users) {
        super(context, 0, users);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        BluetoothDevice device = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.custom_device_list, parent, false);
        }
        // Lookup view for data population
        TextView name = (TextView) convertView.findViewById(R.id.deviceName);
        TextView macAddress = (TextView) convertView.findViewById(R.id.macAddress);
        // Populate the data into the template view using the data object


            name.setText(device.getName());
            macAddress.setText(device.getAddress());

            if(name.getText() == "") {
                name.setText("Null");
            }


        // Return the completed view to render on screen
        return convertView;
    }
}
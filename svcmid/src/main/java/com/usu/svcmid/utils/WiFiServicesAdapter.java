package com.usu.svcmid.utils;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.usu.svcmid.R;
import com.usu.svcmid.wfd.WiFiDiscoveryManager;
import com.usu.svcmid.wfd.WiFiP2pService;

import java.util.List;

/**
 * Created by lee on 9/12/17.
 */
public class WiFiServicesAdapter extends ArrayAdapter<WiFiP2pService> {

    private Context context;
    private List<WifiP2pDevice> items;
    private WiFiDiscoveryManager mManager;

    /**
     * @param context
     * @param mManager
     */
    public WiFiServicesAdapter(Context context, WiFiDiscoveryManager mManager) {
        super(context, R.layout.row_devices);

        this.context = context;
        this.mManager = mManager;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_devices, null);
        }
        WiFiP2pService service = this.getItem(position);
        if (service != null) {
            TextView top = (TextView) v.findViewById(R.id.device_name);
            TextView bottom = (TextView) v.findViewById(R.id.device_details);
            if (top != null) {
                top.setText(service.name);
            }
            if (bottom != null) {
                bottom.setText(getDeviceStatus(service.device.status));
            }
        }
        v.setOnClickListener(new DeviceClickListener(service));
        return v;
    }

    /**
     * this class hold one Device object to establish connection
     * to the device described by this object
     */
    private class DeviceClickListener implements View.OnClickListener {
        WiFiP2pService service;

        public DeviceClickListener(WiFiP2pService service) {
            this.service = service;
        }

        @Override
        public void onClick(View v) {
            switch (service.device.status){
                case WifiP2pDevice.INVITED: {
                    //mWifiBroadcaster.cancelConnection(device.deviceName, null);
                    break;
                }
                case WifiP2pDevice.CONNECTED: {
                    // just disconnect, no confirmation
                    mManager.disconnectService(service);
                    break;
                }
                case WifiP2pDevice.AVAILABLE: {
                    // connect to the selected service
                    mManager.connectToService(service);
                    break;
                }
                case WifiP2pDevice.UNAVAILABLE: {
                    // nothing todo here
                    break;
                }
                case WifiP2pDevice.FAILED: {
                    // attempt connecting to the service again
                    mManager.connectToService(service);
                    break;
                }
            }
        }
    }

    /**
     * return device status in String rather than number
     * @param deviceStatus
     * @return
     */
    private static String getDeviceStatus(int deviceStatus) {
        switch (deviceStatus) {
            case WifiP2pDevice.AVAILABLE:
                return "Available";
            case WifiP2pDevice.INVITED:
                return "Invited";
            case WifiP2pDevice.CONNECTED:
                return "Connected";
            case WifiP2pDevice.FAILED:
                return "Failed";
            case WifiP2pDevice.UNAVAILABLE:
                return "Unavailable";
            default:
                return "Unknown";
        }
    }
}
package com.usu.connection.wifi;

/**
 * Created by minhld on 01/28/2016
 */

import android.content.Context;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.usu.connection.R;
import com.usu.connection.utils.DevUtils;

import java.util.List;

/**
 * Array adapter for ListFragment that maintains WifiP2pDevice list.
 */
public class WiFiListAdapter extends ArrayAdapter<ScanResult> {
    private Context context;
    private List<ScanResult> items;
    private WiFiManager wifiManager;

    /**
     * @param context
     * @param textViewResourceId
     */
    public WiFiListAdapter(Context context, int textViewResourceId, WiFiManager wifiBroader) {
        super(context, textViewResourceId);
        this.context = context;
        this.wifiManager = wifiBroader;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(
                    Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.row_wifi, null);
        }
        ScanResult result = this.getItem(position);
        if (result != null) {
            TextView top = (TextView) v.findViewById(R.id.device_name);
            TextView bottom = (TextView) v.findViewById(R.id.device_details);
            if (top != null) {
                top.setText(result.SSID);
            }
            if (bottom != null) {
                bottom.setText(result.capabilities);
            }
        }
        v.setOnClickListener(new DeviceClickListener(result));
        return v;
    }

    /**
     * this class hold one Device object to establish connection
     * to the device described by this object
     */
    private class DeviceClickListener implements View.OnClickListener {
        ScanResult result;

        public DeviceClickListener(ScanResult result) {
            this.result = result;
        }

        @Override
        public void onClick(View v) {
            // connect
            DevUtils.showInputDialog(WiFiListAdapter.this.context, new DevUtils.InputDialogListener() {
                @Override
                public void inputDone(String resultStr) {
                    wifiManager.connectWifiNetwork(DeviceClickListener.this.result.SSID);
                }
            }, WiFiManager.PASSWORD);

        }
    }
}
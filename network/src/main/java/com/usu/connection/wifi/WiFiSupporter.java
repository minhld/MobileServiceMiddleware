package com.usu.connection.wifi;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.os.Handler;

import com.usu.connection.R;

import java.util.List;

/**
 * This class utilizes the pub-sub library to provide full functionality of
 * publish-subscribe to the client application
 *
 * Created by minhld on 8/6/2016.
 */
public class WiFiSupporter {
    Activity context;
    WiFiManager wiFiManager;
    WiFiListAdapter wifiListAdapter;

    public WiFiSupporter(Activity context, final Handler mainHandler) {
        this.context = context;

        wiFiManager = new WiFiManager(context, mainHandler);
        wiFiManager.setmWifiScanListener(new WiFiManager.WiFiScanListener() {
            @Override
            public void listReceived(List<ScanResult> mScanResults) {
                wifiListAdapter.clear();
                wifiListAdapter.addAll(mScanResults);
                wifiListAdapter.notifyDataSetChanged();
            }
        });

        // WiFi network list
        wifiListAdapter = new WiFiListAdapter(context, R.layout.row_wifi, wiFiManager);

    }

    /**
     * return the peer list adapter (for UI usage)
     * @return
     */
    public WiFiListAdapter getWifiListAdapter() {
        return wifiListAdapter;
    }

    /**
     * request permission for an activity
     * @param c
     */
    public void requestPermission(Activity c) {
        wiFiManager.requestPermission(c);
    }

    /**
     * scan the list of wifi routers
     */
    public void getWifiConnections() {
        wiFiManager.getWifiConnections();
    }

}

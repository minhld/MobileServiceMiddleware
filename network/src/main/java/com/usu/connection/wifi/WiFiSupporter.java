package com.usu.connection.wifi;

import android.app.Activity;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import com.usu.connection.R;
import com.usu.connection.utils.DevUtils;
import com.usu.tinyservice.network.NetUtils;

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

    public WiFiSupporter(Activity context) {
        this.context = context;

        wiFiManager = new WiFiManager(context);
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
     * get WiFi information
     *
     * @param c
     */
    public void getWifiInfo(Activity c) {
        WifiInfo wifiInfo = wiFiManager.checkConnectedToDesiredWifi(c);
        NetUtils.raiseEvent(DevUtils.MESSAGE_WIFI_DETECTED, wifiInfo);
    }

    public String getRouterWifiInfo(Activity c) {
        WifiManager manager = (WifiManager) c.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo info = manager.getDhcpInfo();
        return DevUtils.getIPString(info.gateway);
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

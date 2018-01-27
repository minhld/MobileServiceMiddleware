package com.usu.connection.wifi;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.support.v4.app.ActivityCompat;
import android.text.format.Formatter;

import com.usu.connection.utils.DevUtils;
import com.usu.tinyservice.network.NetUtils;

import java.util.List;

/**
 * Created by minhld on 7/26/2016.
 */
public class WiFiManager {
    public static final String SERVER_IP = "144.39.162.153"; //"144.39.248.223"; //"144.39.215.135"; //"144.39.254.119";// "192.168.49.1";
    public static final String PASSWORD = "DIWNZzzv"; // "qVK5TkO9"; // "NbNCLPiX"; // "EWat5Nhr";

    android.net.wifi.WifiManager mWifiManager;
    WiFiScanListener scanListener;

    String deviceName;

    public void setmWifiScanListener(WiFiScanListener scanListener) {
        this.scanListener = scanListener;
    }

    public WiFiManager(Activity c) {
        mWifiManager = (android.net.wifi.WifiManager) c.getApplicationContext().
                                    getSystemService(Context.WIFI_SERVICE);
        IntentFilter filters = new IntentFilter(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filters.addAction(android.net.wifi.WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        c.registerReceiver(mWifiScanReceiver, filters);
    }

    /**
     * receive a wifi network list
     */
    private BroadcastReceiver mWifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context c, Intent intent) {
            if (intent.getAction().equals(android.net.wifi.WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                List<ScanResult> mScanResults = mWifiManager.getScanResults();
                if (scanListener != null) {
                    // only keep wifi direct network
                    for (int i = mScanResults.size() - 1; i >= 0; i--) {
                        if (!mScanResults.get(i).SSID.toLowerCase().contains("direct")) {
                            mScanResults.remove(i);
                        }
                    }
                    scanListener.listReceived(mScanResults);
                }
            } else if (intent.getAction().equals(android.net.wifi.WifiManager.SUPPLICANT_STATE_CHANGED_ACTION)) {
                SupplicantState state = intent.getParcelableExtra(android.net.wifi.WifiManager.EXTRA_NEW_STATE);
                if (SupplicantState.isValidState(state) && state == SupplicantState.COMPLETED) {
                    // when a wifi connection is established
                    // boolean connected = checkConnectedToDesiredWifi(c);
                    checkConnectedToDesiredWifi(c);
                }
            }
        }
    };

    public void requestPermission(Activity activity) {
        ActivityCompat.requestPermissions(activity,
                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION }, 0);
    }

    public void getWifiConnections() {
//        ActivityCompat.requestPermissions(activity,
//                new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
//                0);
        mWifiManager.startScan();
    }

    public boolean connectWifiNetwork(String SSID) {
//        writeLog("attempt connecting to " + wifiNetwork.SSID);
//        WifiConfiguration wifiConfiguration = new WifiConfiguration();
//        wifiConfiguration.SSID = "\"" + wifiNetwork.SSID + "\"";
//        wifiConfiguration.preSharedKey = "\"" + password + "\"";
////        wifiConfiguration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
//        int netId = mWifiManager.addNetwork(wifiConfiguration);
//
//        List<WifiConfiguration> list = mWifiManager.getConfiguredNetworks();
//        for( WifiConfiguration i : list ) {
//            if(i.SSID != null && i.SSID.equals("\"" + wifiNetwork.SSID + "\"") && i.networkId == netId) {
//                mWifiManager.disconnect();
//                mWifiManager.reconnect();
////                mWifiManager.saveConfiguration();
//                mWifiManager.enableNetwork(i.networkId, true);
//                break;
//            }
//        }
//
//        writeLog("wait for establishment...");

        WifiConfiguration configuration = createOpenWifiConfiguration(SSID);
        int networkId = mWifiManager.addNetwork(configuration);
        writeLog("networkId assigned while adding network is " + networkId);
        return enableNetwork(SSID, networkId);
    }

    private WifiConfiguration createOpenWifiConfiguration(String SSID) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.SSID = String.format("\"%s\"", SSID);
        configuration.preSharedKey = "\"" + PASSWORD + "\"";
        configuration.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        assignHighestPriority(configuration);
        return configuration;
    }

    /**
     * to tell OS to give preference to this network
     *
     * @param config
     */
    private void assignHighestPriority(WifiConfiguration config) {
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (config.priority <= existingConfig.priority) {
                    config.priority = existingConfig.priority + 1;
                }
            }
        }
    }

    private boolean enableNetwork(String SSID, int networkId) {
        if (networkId == -1) {
            networkId = getExistingNetworkId(SSID);

            if (networkId == -1) {
                writeLog("Couldn't add network with SSID: " + SSID);
                return false;
            }
        }
        return mWifiManager.enableNetwork(networkId, true);
    }

    private int getExistingNetworkId(String SSID) {
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration existingConfig : configuredNetworks) {
                if (SSID != null && SSID.equals("\"" + existingConfig.SSID + "\"")) {
                    return existingConfig.networkId;
                }
            }
        }
        return -1;
    }

    /**
     * detect you are connected to a specific network.
     *
     * @param c
     */
    public WifiInfo checkConnectedToDesiredWifi(Context c) {
        android.net.wifi.WifiManager wifiManager = (android.net.wifi.WifiManager)
                                    c.getSystemService(Context.WIFI_SERVICE);

        WifiInfo wifi = wifiManager.getConnectionInfo();
        if (wifi != null) {
            writeLog("connected to: " + wifi.getSSID() + "; " +
                    "bssid: " + wifi.getBSSID() + "; " +
                    "IP: " + DevUtils.getIPString(wifi.getIpAddress()) + "; " +
                    "freq: " + wifi.getFrequency() + "MHz; " +
                    "speed: " + wifi.getLinkSpeed() + "MBps; ");
        }
        return wifi;
    }

    /**
     *
     */
    public interface WiFiScanListener {
        void listReceived(List<ScanResult> mScanResults);
    }

    public void writeLog(final String msg){
        NetUtils.print(msg);
    }

}

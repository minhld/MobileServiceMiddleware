package com.usu.connection.wfd;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Handler;

import com.usu.connection.utils.WiFiServicesAdapter;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 9/12/17.
 */

public class Supporter {
    Activity context;
    WiFiDiscoveryManager wfdManager;
    IntentFilter mIntentFilter;
    WiFiServicesAdapter serviceAdapter;

    // hold the virtual service list obtained from the local and remote devices
    Map<String, WiFiP2pService> serviceList = new HashMap<>();
    boolean isGroupOwner;
    boolean isGroupFormed;

    public Supporter(Activity context, final Handler mainHandler) {
        this.context = context;

        // configure the wifi-direct manager
        wfdManager = new WiFiDiscoveryManager(this.context);
        wfdManager.setWFDHandler(mainHandler);
        wfdManager.setWiFiDiscoveryListener(new WiFiDiscoveryManager.WiFiDiscoveryListener() {
            @Override
            public void serviceFound(WiFiP2pService service) {
                // avoid duplicated services
                if (!serviceList.containsKey(service.name)) {
                    // add to the managed list
                    serviceList.put(service.name, service);

                    // add to the adapter to be available on the list
                    serviceAdapter.add(service);
                    serviceAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void serviceRecordFound(String serviceName, Map<String, String> record) {
                // find the service key and update its record
                WiFiP2pService service = serviceList.get(serviceName);
                service.record = record;
                // add the update the service info
                serviceList.put(serviceName, service);
            }

            @Override
            public void p2pEstablished(WifiP2pInfo p2pInfo) {
                isGroupOwner = p2pInfo.isGroupOwner;
                isGroupFormed = p2pInfo.groupFormed;
            }
        });

        mIntentFilter = wfdManager.getSingleIntentFilter();
        serviceAdapter = new WiFiServicesAdapter(this.context, wfdManager);
    }

    public WiFiServicesAdapter getServiceAdapter() {
        return this.serviceAdapter;
    }

    /**
     * start registration, including discovery
     */
    public void startRegistration() {
        wfdManager.startRegistration();
        startDiscovery();
    }

    /**
     * start the discovery only
     */
    public void startDiscovery() {
        // remove the service lists before discovery and adding the new ones
        serviceAdapter.clear();
        serviceList.clear();

        // prepare & start discovery
        wfdManager.prepareDiscovery();
        wfdManager.startDiscovery();
    }

    /**
     * this should be added at the end of onPause on main activity
     */
    public void addOnPause() {
        if (wfdManager != null && mIntentFilter != null) {
            this.context.unregisterReceiver(wfdManager.getBroadCastReceiver());
        }
    }

    /**
     * this should be added at the end of onResume on main activity
     */
    public void addOnResume() {
        if (wfdManager != null && mIntentFilter != null) {
            this.context.registerReceiver(wfdManager.getBroadCastReceiver(), mIntentFilter);
        }
    }
}


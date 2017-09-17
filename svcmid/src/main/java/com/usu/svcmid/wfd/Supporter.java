package com.usu.svcmid.wfd;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Handler;

import com.usu.svcmid.utils.WiFiServicesAdapter;

/**
 * Created by lee on 9/12/17.
 */

public class Supporter {
    Activity context;
    WiFiDiscoveryManager wfdManager;
    IntentFilter mIntentFilter;
    WiFiServicesAdapter serviceAdapter;

    public Supporter(Activity context, final Handler mainHandler) {
        this.context = context;

        wfdManager = new WiFiDiscoveryManager(this.context);
        wfdManager.setWFDHandler(mainHandler);
        wfdManager.setWiFiDiscoveryListener(new WiFiDiscoveryManager.WiFiDiscoveryListener() {
            @Override
            public void serverFound(WiFiP2pService service) {
                serviceAdapter.add(service);
                serviceAdapter.notifyDataSetChanged();
            }

            @Override
            public void wfdEstablished(WifiP2pInfo p2pInfo) {

            }
        });

        // mIntentFilter = wfdManager.getSingleIntentFilter();
        serviceAdapter = new WiFiServicesAdapter(this.context, wfdManager);
    }

    public WiFiServicesAdapter getServiceAdapter() {
        return this.serviceAdapter;
    }

    public void startRegister() {
        wfdManager.startRegistration();
        wfdManager.startDiscovery();
    }

    public void startDiscovery() {
        serviceAdapter.clear();
        wfdManager.startDiscovery();
    }
}


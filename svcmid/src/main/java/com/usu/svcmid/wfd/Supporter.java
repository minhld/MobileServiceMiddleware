package com.usu.svcmid.wfd;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Handler;

import com.usu.svcmid.utils.WiFiServicesAdapter;

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

    public Supporter(Activity context, final Handler mainHandler) {
        this.context = context;

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
        // remove the service lists before discovery and adding the new ones
        serviceAdapter.clear();
        serviceList.clear();
        wfdManager.startDiscovery();
    }
}


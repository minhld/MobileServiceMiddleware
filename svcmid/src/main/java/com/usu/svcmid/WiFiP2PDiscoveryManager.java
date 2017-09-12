package com.usu.svcmid;

import android.content.BroadcastReceiver;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by minhld on 9/9/2017.
 */

public class WiFiP2PDiscoveryManager {
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE = "_svc";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver = null;
    private WifiP2pDnsSdServiceRequest mServiceRequest;

    public void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE, SERVICE_REG_TYPE, record);
        mManager.addLocalService(mChannel, service, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                appendStatus("Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                appendStatus("Failed to add a service");
            }
        });

        discoverService();

    }

    private void discoverService() {
        /*
         * Register listeners for DNS-SD services. These are callbacks invoked
         * by the system when a service is actually discovered.
         */

        mManager.setDnsSdResponseListeners(mChannel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                        WifiP2pDevice srcDevice) {
                        // A service has been discovered. Is this our app?
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            // update the UI and add the item the discovered device.
                            WiFiDirectServicesList fragment = (WiFiDirectServicesList)
                                                getFragmentManager().findFragmentByTag("services");
                            if (fragment != null) {
                                WiFiDirectServicesList.WiFiDevicesAdapter adapter =
                                        ((WiFiDirectServicesList.WiFiDevicesAdapter)
                                                fragment.getListAdapter());
                                WiFiP2pService service = new WiFiP2pService();
                                service.device = srcDevice;
                                service.instanceName = instanceName;
                                service.serviceRegistrationType = registrationType;
                                adapter.add(service);
                                adapter.notifyDataSetChanged();
                                Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
                            }
                        }

                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {
                    // A new TXT record is available. Pick up the advertised
                    // buddy name.
                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        Log.d(TAG,
                                device.deviceName + " is "
                                        + record.get(TXTRECORD_PROP_AVAILABLE));
                    }
                });

        // After attaching listeners, create a service request and initiate
        // discovery.
        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, mServiceRequest,
                new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        appendStatus("Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        appendStatus("Failed adding service discovery request");
                    }
                });
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                appendStatus("Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                appendStatus("Service discovery failed");

            }
        });
    }
}

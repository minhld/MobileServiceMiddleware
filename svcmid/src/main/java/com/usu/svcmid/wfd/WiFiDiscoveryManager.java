package com.usu.svcmid.wfd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import utils.Utils;

/**
 * Created by minhld on 9/9/2017.
 */

public class WiFiDiscoveryManager {
    public static final String TXTRECORD_PROP_AVAILABLE = "available";
    public static final String SERVICE_INSTANCE_1 = "_svc_1";
    public static final String SERVICE_INSTANCE_2 = "_svc_2";
    public static final String SERVICE_REG_TYPE = "_presence._tcp";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private BroadcastReceiver mReceiver = null;
    private WifiP2pDnsSdServiceRequest mServiceRequest;

    private Activity context;
    private Handler mHandler;
    private WiFiDiscoveryListener wifiListener;

    public void setWFDHandler(Handler sHandler) {
        this.mHandler = sHandler;
    }

    public WiFiDiscoveryManager(Activity context) {
        this.context = context;
        mManager = (WifiP2pManager) context.getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(context, context.getMainLooper(), null);
    }

    public void startRegistration() {
        Map<String, String> record = new HashMap<String, String>();
        record.put(TXTRECORD_PROP_AVAILABLE, "visible");

        WifiP2pDnsSdServiceInfo service1 = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE_1, SERVICE_REG_TYPE, record);
        mManager.addLocalService(mChannel, service1, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                writeLog("Added Local Service " + SERVICE_INSTANCE_1);
            }

            @Override
            public void onFailure(int error) {
                writeLog("Failed to add a service");
            }
        });

        WifiP2pDnsSdServiceInfo service2 = WifiP2pDnsSdServiceInfo.newInstance(
                SERVICE_INSTANCE_2, SERVICE_REG_TYPE, record);
        mManager.addLocalService(mChannel, service2, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                writeLog("Added Local Service " + SERVICE_INSTANCE_2);
            }

            @Override
            public void onFailure(int error) {
                writeLog("Failed to add a service");
            }
        });

        // startDiscovery();
    }

    public void startDiscovery() {
        // Register listeners for DNS-SD services. These are callbacks invoked
        // by the system when a service is actually discovered.
        mManager.setDnsSdResponseListeners(mChannel,
            new WifiP2pManager.DnsSdServiceResponseListener() {
                @Override
                public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                    WifiP2pDevice srcDevice) {
                    // A service has been discovered.
                    if (instanceName.contains("_svc")) {
                        // update the UI and add the item the discovered device.
                        WiFiP2pService service = new WiFiP2pService();
                        service.device = srcDevice;
                        service.name = instanceName;
                        service.type = registrationType;
                        wifiListener.serverFound(service);

//                        // update the UI and add the item the discovered device.
//                        WiFiDirectServicesList fragment = (WiFiDirectServicesList)
//                                            getFragmentManager().findFragmentByTag("services");
//                        if (fragment != null) {
//                            WiFiServicesAdapter adapter = ((WiFiDirectServicesList.WiFiServicesAdapter) fragment.getListAdapter());
//                            WiFiP2pService service = new WiFiP2pService();
//                            service.device = srcDevice;
//                            service.name = instanceName;
//                            service.type = registrationType;
//                            adapter.add(service);
//                            adapter.notifyDataSetChanged();
//                            Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
//                        }
                    }

                }
            }, new WifiP2pManager.DnsSdTxtRecordListener() {
                // A new TXT record is available. Pick up the advertised
                // buddy name.
                @Override
                public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record,
                                                        WifiP2pDevice device) {
                    writeLog(device.deviceName + " is " + record.get(TXTRECORD_PROP_AVAILABLE));
                }
            }
        );

        // After attaching listeners, create a service request and initiate
        // discovery.
        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, mServiceRequest, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                writeLog("Added service discovery request");
            }

            @Override
            public void onFailure(int arg0) {
                writeLog("Failed adding service discovery request");
            }
        });

        // start the service discovery
        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                writeLog("Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                writeLog("Service discovery failed");

            }
        });
    }

    public void setWiFiDiscoveryListener(WiFiDiscoveryListener mListener){
        this.wifiListener = mListener;
    }

    /**
     * this class is to throw events from WFD Manager back to the UI thread
     */
    public interface WiFiDiscoveryListener {
        /**
         * this is called when the device list changes
         * @param service
         */
        public void serverFound(WiFiP2pService service);

        /**
         * called when wifi direct connection is established
         * @param p2pInfo
         */
        public void wfdEstablished(WifiP2pInfo p2pInfo);
    }

    public void writeLog(final String msg){
        mHandler.obtainMessage(Utils.MESSAGE_INFO, msg).sendToTarget();
    }

}

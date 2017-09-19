package com.usu.svcmid.wfd;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Handler;

import java.util.HashMap;
import java.util.Map;

import com.usu.svcmid.utils.Constants;
import com.usu.svcmid.utils.Utils;

/**
 * Created by minhld on 9/9/2017.
 */
public class WiFiDiscoveryManager {

    public static final String SERVICE_INSTANCE_1 = "svc_1";
    public static final String SERVICE_INSTANCE_2 = "svc_2";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    IntentFilter mIntentFilter;

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

        // setup broadcast listener
        mReceiver = new WiFiP2pBroadcastListener();
    }

    /**
     * Register services to the local device, so that the other devices could
     * discover over WiFi-Direct. This function associates with <b>startDiscovery()</b>
     * so that the registered services could be seen from outside immediately
     */
    public void startRegistration() {
        // clear all local services before adding the new
        mManager.clearLocalServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Map<String, String> record;
                String[] services = new String[] { SERVICE_INSTANCE_1, SERVICE_INSTANCE_2 };

                for (int i = 0; i < services.length; i++) {
                    // prepare record for service #i
                    record = new HashMap<String, String>();
                    record.put(Constants.TXTRECORD_PROP_AVAILABLE, "visible");
                    record.put(Constants.TXTRECORD_PROP_DEVICENAME, Utils.getDeviceName());

                    final String svcName = Utils.getFullServiceName(services[i]);
                    WifiP2pDnsSdServiceInfo service1 = WifiP2pDnsSdServiceInfo.newInstance(
                            svcName, Constants.SERVICE_REG_TYPE, record);
                    mManager.addLocalService(mChannel, service1, new WifiP2pManager.ActionListener() {
                        @Override
                        public void onSuccess() {
                            writeLog("Added local service [" + svcName + "]");
                        }

                        @Override
                        public void onFailure(int error) {
                            writeLog("Failed to add a service [" + svcName + "]");
                        }
                    });
                }
            }

            @Override
            public void onFailure(int reason) {
                writeLog("Clear local services failed. No new services added");
            }
        });


    }

    public void prepareDiscovery() {
        mManager.setDnsSdResponseListeners(mChannel,
            new WifiP2pManager.DnsSdServiceResponseListener() {
                @Override
                public void onDnsSdServiceAvailable(String instanceName, String registrationType,
                                                    WifiP2pDevice srcDevice) {
                    // do all the things you need to do with detected service
                    if (instanceName.contains("_svc")) {
                        // update the UI and add the item the discovered device.
                        WiFiP2pService service = new WiFiP2pService();
                        service.device = srcDevice;
                        service.name = instanceName;
                        service.type = registrationType;
                        wifiListener.serviceFound(service);
                    }
                }
            }, new WifiP2pManager.DnsSdTxtRecordListener() {
                @Override
                public void onDnsSdTxtRecordAvailable(String fullDomainName, Map<String, String> record,
                                                    WifiP2pDevice device) {
                    // do all the things you need to do with detailed information about detected service
                    writeLog(record.get(Constants.TXTRECORD_PROP_DEVICENAME) + " is " +
                            record.get(Constants.TXTRECORD_PROP_AVAILABLE));
                }
            }
        );
        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();
    }

    public void startDiscovery() {
        mManager.removeServiceRequest(mChannel, mServiceRequest,
            new WifiP2pManager.ActionListener() {

                @Override
                public void onSuccess() {
                    mManager.addServiceRequest(mChannel, mServiceRequest,
                        new WifiP2pManager.ActionListener() {

                            @Override
                            public void onSuccess() {
                                mManager.discoverServices(mChannel,
                                    new WifiP2pManager.ActionListener() {

                                        @Override
                                        public void onSuccess() {
                                            //service discovery started
                                            writeLog("Service discovery initiated");
                                        }

                                        @Override
                                        public void onFailure(int error) {
                                            // react to failure of starting service discovery
                                            writeLog("Service discovery failed");
                                        }
                                    }
                                );
                            }

                            @Override
                            public void onFailure(int error) {
                                // react to failure of adding service request
                            }
                        }
                    );
                }

                @Override
                public void onFailure(int reason) {
                    // react to failure of removing service request
                }
            }
        );
    }

//    /**
//     * This function starts discovery services locate on the local device or
//     * on the others.
//     */
//    public void startDiscovery1() {
//
//        // remove service request before adding a new one
//        if (mServiceRequest != null) {
//            mManager.removeServiceRequest(mChannel, mServiceRequest, new WifiP2pManager.ActionListener() {
//                @Override
//                public void onSuccess() {
//                    writeLog("Removed [last session] service discovery request");
//                }
//
//                @Override
//                public void onFailure(int reason) {
//                    writeLog("Failed removing [last session] service discovery request");
//                }
//            });
//        }
//
//        // Register listeners for DNS-SD services. These are callbacks invoked
//        // by the system when a service is actually discovered.
//        mManager.setDnsSdResponseListeners(mChannel,
//            new WifiP2pManager.DnsSdServiceResponseListener() {
//                @Override
//                public void onDnsSdServiceAvailable(String instanceName, String registrationType,
//                                                    WifiP2pDevice srcDevice) {
//                    // A service has been discovered.
//                    if (instanceName.contains("_svc")) {
//                        // update the UI and add the item the discovered device.
//                        WiFiP2pService service = new WiFiP2pService();
//                        service.device = srcDevice;
//                        service.name = instanceName;
//                        service.type = registrationType;
//                        wifiListener.serviceFound(service);
//
////                        // update the UI and add the item the discovered device.
////                        WiFiDirectServicesList fragment = (WiFiDirectServicesList)
////                                            getFragmentManager().findFragmentByTag("services");
////                        if (fragment != null) {
////                            WiFiServicesAdapter adapter = ((WiFiDirectServicesList.WiFiServicesAdapter) fragment.getListAdapter());
////                            WiFiP2pService service = new WiFiP2pService();
////                            service.device = srcDevice;
////                            service.name = instanceName;
////                            service.type = registrationType;
////                            adapter.add(service);
////                            adapter.notifyDataSetChanged();
////                            Log.d(TAG, "onBonjourServiceAvailable " + instanceName);
////                        }
//                    }
//
//                }
//            }, new WifiP2pManager.DnsSdTxtRecordListener() {
//                // A new TXT record is available. Pick up the advertised
//                // buddy name.
//                @Override
//                public void onDnsSdTxtRecordAvailable(String fullDomainName,
//                                        Map<String, String> record, WifiP2pDevice device) {
//                    writeLog(record.get(Constants.TXTRECORD_PROP_DEVICENAME) + " is " +
//                                    record.get(Constants.TXTRECORD_PROP_AVAILABLE));
//                }
//            }
//        );
//
//        // After attaching listeners, create a service request and initiate
//        // discovery.
//        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();
//        mManager.addServiceRequest(mChannel, mServiceRequest, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                writeLog("Added service discovery request");
//            }
//
//            @Override
//            public void onFailure(int arg0) {
//                writeLog("Failed adding service discovery request");
//            }
//        });
//
//        // start the service discovery
//        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
//            @Override
//            public void onSuccess() {
//                writeLog("Service discovery initiated");
//            }
//
//            @Override
//            public void onFailure(int arg0) {
//                writeLog("Service discovery failed");
//
//            }
//        });
//    }

    public void connectToService(final WiFiP2pService service) {
        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = service.device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        // remove the previous service request if it's still available
        if (mServiceRequest != null) {
            mManager.removeServiceRequest(mChannel, mServiceRequest, null);
        }

        // connect to the service
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                writeLog("Connected to service [" + service.name + "]");
            }

            @Override
            public void onFailure(int errorCode) {
                writeLog("Failed connecting to service [" + service.name + "]");
            }
        });
    }

    public void disconnectService(WiFiP2pService service) {

    }

    public void setWiFiDiscoveryListener(WiFiDiscoveryListener mListener){
        this.wifiListener = mListener;
    }

    public BroadcastReceiver getBroadCastReceiver() {
        return this.mReceiver;
    }

    /**
     *
     */
    private class WiFiP2pBroadcastListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                // Check to see if Wi-Fi is enabled and notify appropriate activity
                int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                    // Wifi P2P is enabled
                    writeLog("wifi p2p is enabled");
                } else {
                    // Wi-Fi P2P is not enabled
                    writeLog("wifi p2p is disabled");
                }
            } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
                // mManager.request
            } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {

                    }
                });
            } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
                writeLog("device's wifi state changed");
            }
        }
    }

    public IntentFilter getSingleIntentFilter() {
        if (mIntentFilter == null) {
            mIntentFilter = new IntentFilter();
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
            mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        }

        return mIntentFilter;
    }

    /**
     * this class is to throw events from WFD Manager back to the UI thread
     */
    public interface WiFiDiscoveryListener {
        /**
         * called when a service has been found
         * @param service
         */
        public void serviceFound(WiFiP2pService service);

        /**
         * called when the record of a server has been found
         * @param serviceName
         * @param record
         */
        public void serviceRecordFound(String serviceName, Map<String, String> record);

        /**
         * called when wifi direct connection is established
         * @param info
         */
        public void p2pEstablished(WifiP2pInfo info);
    }

    public void writeLog(final String msg){
        mHandler.obtainMessage(Utils.MESSAGE_INFO, msg).sendToTarget();
    }

}

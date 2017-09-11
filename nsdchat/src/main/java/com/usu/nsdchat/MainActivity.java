package com.usu.nsdchat;

import android.Manifest;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceRequest;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    public static final int WRITE_PERM_REQ_CODE = 19;
    public static final String SERVICE_INSTANCE = "nsdchat";
    public static final String SERVICE_TYPE = "_nsdchat._tcp";

    private TextView mStatusView;

    WifiP2pManager mManager;
    WifiP2pManager.Channel mChannel;
    WifiP2pDnsSdServiceRequest mServiceRequest = null;

    private boolean isConnectP2pCalled = false;
    private String peerIP = null;
    private int peerPort = -1;
    private boolean isConnectionInfoSent = false;
    private boolean isWDConnected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mStatusView = (TextView) findViewById(R.id.status);

        initialize();
    }

    private void initialize() {
//
//        appController = (AppController) getApplicationContext();
//
//        progressBarLocalDash = findViewById(R.id.progressBarLocalDash);
//
//        appController.startConnectionListener();
//
//        setToolBarTitle(0);

        mManager = (WifiP2pManager) getSystemService(WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

//        startRegistrationAndDiscovery(ConnectionUtils.getPort(LocalDashWiFiP2PSD.this));
        checkWritePermission();
    }

    public void clickRegister(View v) {
        startRegistrationAndDiscovery(ConnectionUtils.getPort(MainActivity.this));
    }

    public void clickDiscover(View v) {

    }

    public void clickConnect(View v) {

    }

    private void checkWritePermission() {
        boolean isGranted = Utils.checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, this);
        if (!isGranted) {
            Utils.requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, WRITE_PERM_REQ_CODE, this);
        } else {
            startRegistrationAndDiscovery(ConnectionUtils.getPort(MainActivity.this));
        }
    }

    private void startRegistrationAndDiscovery(int port) {

        String player = Utils.getString(MainActivity.this, TransferConstants.KEY_USER_NAME);

        Map<String, String> record = new HashMap<String, String>();
        record.put(TransferConstants.KEY_BUDDY_NAME, player == null ? Build.MANUFACTURER : player);
        record.put(TransferConstants.KEY_PORT_NUMBER, String.valueOf(port));
        record.put(TransferConstants.KEY_DEVICE_STATUS, "available");
        record.put(TransferConstants.KEY_WIFI_IP, Utils.getWiFiIPAddress(MainActivity.this));

        WifiP2pDnsSdServiceInfo service = WifiP2pDnsSdServiceInfo.newInstance(SERVICE_INSTANCE, SERVICE_TYPE, record);
        mManager.addLocalService(mChannel, service, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mStatusView.setText("[Success] Added Local Service");
            }

            @Override
            public void onFailure(int error) {
                mStatusView.setText("[Error-Registration]: Failed to add a service");
            }
        });

        discoverService();
    }

    private void discoverService() {
        // Register listeners for DNS-SD services. These are callbacks invoked
        // by the system when a service is actually discovered.
        mManager.setDnsSdResponseListeners(mChannel,
                new WifiP2pManager.DnsSdServiceResponseListener() {
                    @Override
                    public void onDnsSdServiceAvailable(String instanceName, String registrationType, WifiP2pDevice srcDevice) {
                        mStatusView.setText(instanceName + "####" + registrationType);
                        // A service has been discovered. Is this our app?
                        if (instanceName.equalsIgnoreCase(SERVICE_INSTANCE)) {
                            // yes it is
                            WiFiP2pServiceHolder serviceHolder = new WiFiP2pServiceHolder();
                            serviceHolder.device = srcDevice;
                            serviceHolder.registrationType = registrationType;
                            serviceHolder.instanceName = instanceName;
                            connectP2p(serviceHolder);
                        } else {
                            //no it isn't
                        }
                    }
                }, new WifiP2pManager.DnsSdTxtRecordListener() {

                    @Override
                    public void onDnsSdTxtRecordAvailable(
                            String fullDomainName, Map<String, String> record,
                            WifiP2pDevice device) {
                        boolean isGroupOwner = device.isGroupOwner();
                        peerPort = Integer.parseInt(record.get(TransferConstants.KEY_PORT_NUMBER).toString());
                        mStatusView.setText(Build.MANUFACTURER + ". peer port received: " + peerPort);
                        if (peerIP != null && peerPort > 0 && !isConnectionInfoSent) {
                            String player = record.get(TransferConstants.KEY_BUDDY_NAME).toString();

//                            DataSender.sendCurrentDeviceData(LocalDashWiFiP2PSD.this,
//                                    peerIP, peerPort, true);
//                            isWDConnected = true;
//                            isConnectionInfoSent = true;
                        }
                    }
                });

        // After attaching listeners, create a service request and initiate
        // discovery.
        mServiceRequest = WifiP2pDnsSdServiceRequest.newInstance();
        mManager.addServiceRequest(mChannel, mServiceRequest,
                new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        mStatusView.setText("Added service discovery request");
                    }

                    @Override
                    public void onFailure(int arg0) {
                        mStatusView.setText("Error-Discovery-Request: Failed adding service discovery request");
                    }
                });

        mManager.discoverServices(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mStatusView.setText("Service discovery initiated");
            }

            @Override
            public void onFailure(int arg0) {
                mStatusView.setText("Service discovery failed: " + arg0);
            }
        });
    }

    private void connectP2p(WiFiP2pServiceHolder serviceHolder) {
        if (!isConnectP2pCalled) {
            isConnectP2pCalled = true;
            WifiP2pConfig config = new WifiP2pConfig();
            config.deviceAddress = serviceHolder.device.deviceAddress;
            config.wps.setup = WpsInfo.PBC;
            if (mServiceRequest != null)
                mManager.removeServiceRequest(mChannel, mServiceRequest,
                        new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                            }

                            @Override
                            public void onFailure(int arg0) {
                            }
                        });

            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                @Override
                public void onSuccess() {
                    //("Connecting to service");
                }

                @Override
                public void onFailure(int errorCode) {
                    //("Failed connecting to service");
                }
            });
        }
    }

    private class WiFiP2pServiceHolder {
        WifiP2pDevice device;
        String instanceName;
        String registrationType;
    }

}

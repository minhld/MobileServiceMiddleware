package com.usu.mobileservice.g2gapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.usu.connection.utils.DevUtils;
import com.usu.connection.wfd.WFDSupporter;
import com.usu.connection.wifi.WiFiSupporter;
import com.usu.mobileservice.g2gapp.ServiceAClient;
import com.usu.mobileservice.g2gapp.ServiceAWorker;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.createGroupBtn)
    Button createGroupBtn;

    @BindView(R.id.discoverBtn)
    Button discoverBtn;

    @BindView(R.id.getDirectInfoBtn)
    Button getDirectInfoBtn;

    @BindView(R.id.sendWifiDirectBtn)
    Button sendWifiDirectBtn;

    @BindView(R.id.searchWiFiBtn)
    Button searchWiFiBtn;

    @BindView(R.id.getWiFiInfoBtn)
    Button getWiFiInfoBtn;

    @BindView(R.id.sendWifiDataBtn)
    Button sendWiFiDataBtn;

    @BindView(R.id.deviceList)
    ListView deviceList;

    @BindView(R.id.wifiList)
    ListView wifiList;

    @BindView(R.id.infoText)
    TextView infoText;

    WFDSupporter wfdSupporter;
    WiFiSupporter wfSupport;

    ServiceAClient client;

    Handler mainUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
//                case Utils.MESSAGE_READ_SERVER: {
//                    String strMsg = msg.obj.toString();
//                    UITools.writeLog(MainActivity.this, infoText, strMsg);
//                    break;
//                }
//                case Utils.MESSAGE_READ_CLIENT: {
//                    String strMsg = msg.obj.toString();
//                    UITools.writeLog(MainActivity.this, infoText, strMsg);
//                    break;
//                }
//                case Utils.MAIN_JOB_DONE: {
//
//                    break;
//                }
                case DevUtils.MESSAGE_INFO: {
                    String strMsg = (String) msg.obj;
                    UITools.writeLog(MainActivity.this, infoText, strMsg);
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        infoText.setMovementMethod(new ScrollingMovementMethod());

        // WiFi-Direct
        wfdSupporter = new WFDSupporter(this, mainUiHandler);
        deviceList.setAdapter(wfdSupporter.getDeviceListAdapter());

        // Original WiFi Interface
        wfSupport = new WiFiSupporter(this, mainUiHandler);
        wifiList.setAdapter(wfSupport.getWifiListAdapter());


//        // ------ Prepare for WiFi Direct ------
//        wifiBroader = new WifiBroader(this, infoText);
//        wifiBroader.setSocketHandler(mainUiHandler);
//        wifiBroader.setBroadCastListener(new WifiBroader.BroadCastListener() {
//            @Override
//            public void peerDeviceListUpdated(Collection<WifiP2pDevice> deviceList) {
//                deviceListAdapter.clear();
//                deviceListAdapter.addAll(deviceList);
//                deviceListAdapter.notifyDataSetChanged();
//            }
//
//            @Override
//            public void socketUpdated(Utils.SocketType socketType, boolean connected) {
//
//            }
//        });
//        mIntentFilter = wifiBroader.getSingleIntentFilter();
//
//        // device list
//        deviceListAdapter = new WifiPeerListAdapter(this, R.layout.row_devices, wifiBroader);
//        deviceList.setAdapter(deviceListAdapter);

//        // ------ Prepared for Original WiFi ------
//        orgWifiBroader = new WiFiManager(this, infoText);
//        orgWifiBroader.setSocketHandler(mainUiHandler);
//        orgWifiBroader.setmWifiScanListener(new WiFiManager.WiFiScanListener() {
//            @Override
//            public void listReceived(List<ScanResult> mScanResults) {
//                networkListAdapter.clear();
//                networkListAdapter.addAll(mScanResults);
//                networkListAdapter.notifyDataSetChanged();
//            }
//        });
//
//        // WiFi network list
//        networkListAdapter = new WiFiListAdapter(this, R.layout.row_wifi, orgWifiBroader);
//        wifiList.setAdapter(networkListAdapter);

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfdSupporter.createGroup();
            }
        });

        discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfdSupporter.discoverPeers();
            }
        });

        getDirectInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfdSupporter.requestGroupInfo();
            }
        });

        sendWifiDirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initClient();
                client.greeting("send " + client.client.getName());
            }
        });

        searchWiFiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    wfSupport.requestPermission(MainActivity.this);
                } else {
                    // search for Wifi network list
                    // orgWifiBroader.getWifiConnections();
                    wfSupport.getWifiConnections();
                }
            }
        });

        getWiFiInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        sendWiFiDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // connect to one of the Wifi networks
                // wifiBroader.requestGroupInfo();
                // orgWifiBroader.writeString("sent a WiFi ACK :)");
            }
        });

        // grant permission - for Android 6.0 and higher
        DevUtils.grandWritePermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // orgWifiBroader.getWifiConnections();
    }

    @Override
    protected void onPause() {
        super.onPause();
        wfdSupporter.runOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        wfdSupporter.runOnResume();
    }

    void initBroker() {
        new Broker();
    }

    void initClient() {
        client = new ServiceAClient(new ReceiveListener() {
            @Override
            public void dataReceived(String idChain, String funcName, byte[] data) {
                ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
                if (resp.functionName.equals(NetUtils.BROKER_INFO)) {
                    // a denied message from the Broker
                    String msg = (String) resp.outParam.values[0];
                    UITools.writeLog(MainActivity.this, infoText, "[Client] Error " + msg);
                } else if (resp.functionName.equals("greeting")) {
                    java.lang.String[] msgs = (java.lang.String[]) resp.outParam.values;
                    UITools.writeLog(MainActivity.this, infoText, "[Client] Received: " + msgs[0]);
                } else if (resp.functionName.equals("getFileList2")) {
                    java.lang.String[] files = (java.lang.String[]) resp.outParam.values;
                    UITools.writeLog(MainActivity.this, infoText, "[Client] Received: ");
                    for (int i = 0; i < files.length; i++) {
                        UITools.writeLog(MainActivity.this, infoText, "\t File: " + files[i]);
                    }
                }
            }
        });
    }

    void initWorker() {
        new ServiceAWorker();
    }
}

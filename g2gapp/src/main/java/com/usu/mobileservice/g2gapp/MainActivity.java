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

import butterknife.BindView;
import butterknife.ButterKnife;
import g2glib.UITools;
import support.Utils;

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

//    WifiBroader wifiBroader;
//    WiFiManager orgWifiBroader;
//    IntentFilter mIntentFilter;

//    WifiPeerListAdapter deviceListAdapter;
//    WiFiListAdapter networkListAdapter;

    Handler mainUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utils.MESSAGE_READ_SERVER: {
                    String strMsg = msg.obj.toString();
                    UITools.writeLog(MainActivity.this, infoText, strMsg);
                    break;
                }
                case Utils.MESSAGE_READ_CLIENT: {
                    String strMsg = msg.obj.toString();
                    UITools.writeLog(MainActivity.this, infoText, strMsg);
                    break;
                }
                case Utils.MAIN_JOB_DONE: {

                    break;
                }
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
                // wifiBroader.writeString("sent a ACK :)");
                // send data
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

}

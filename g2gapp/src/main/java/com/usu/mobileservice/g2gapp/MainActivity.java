package com.usu.mobileservice.g2gapp;

import android.Manifest;
import android.app.Activity;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Collection;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import g2glib.UITools;
import g2glib.WifiBroader;
import g2glib.WifiConnector;
import g2glib.WifiNetworkListAdapter;
import g2glib.WifiPeerListAdapter;
import support.Utils;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

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

    WifiBroader wifiBroader;
    WifiConnector orgWifiBroader;
    IntentFilter mIntentFilter;

    WifiPeerListAdapter deviceListAdapter;
    WifiNetworkListAdapter networkListAdapter;

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
                case Utils.MAIN_INFO: {
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

        // ------ Prepare for WiFi Direct ------
        wifiBroader = new WifiBroader(this, infoText);
        wifiBroader.setSocketHandler(mainUiHandler);
        wifiBroader.setBroadCastListener(new WifiBroader.BroadCastListener() {
            @Override
            public void peerDeviceListUpdated(Collection<WifiP2pDevice> deviceList) {
                deviceListAdapter.clear();
                deviceListAdapter.addAll(deviceList);
                deviceListAdapter.notifyDataSetChanged();
            }

            @Override
            public void socketUpdated(Utils.SocketType socketType, boolean connected) {

            }
        });
        mIntentFilter = wifiBroader.getSingleIntentFilter();

        // device list
        deviceListAdapter = new WifiPeerListAdapter(this, R.layout.row_devices, wifiBroader);
        deviceList.setAdapter(deviceListAdapter);

        // ------ Prepared for Original WiFi ------
        orgWifiBroader = new WifiConnector(this, infoText);
        orgWifiBroader.setSocketHandler(mainUiHandler);
        orgWifiBroader.setmWifiScanListener(new WifiConnector.WiFiScanListener() {
            @Override
            public void listReceived(List<ScanResult> mScanResults) {
                networkListAdapter.clear();
                networkListAdapter.addAll(mScanResults);
                networkListAdapter.notifyDataSetChanged();
            }
        });

        // WiFi network list
        networkListAdapter = new WifiNetworkListAdapter(this, R.layout.row_wifi, orgWifiBroader);
        wifiList.setAdapter(networkListAdapter);

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiBroader.createGroup();
            }
        });

        discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiBroader.discoverPeers();
            }
        });

        getDirectInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiBroader.requestGroupInfo();
            }
        });

        sendWifiDirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wifiBroader.writeString("sent a ACK :)");
            }
        });

        searchWiFiBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    orgWifiBroader.requestPermission(MainActivity.this);
                } else {
                    // search for Wifi network list
                    orgWifiBroader.getWifiConnections();
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
                orgWifiBroader.writeString("sent a WiFi ACK :)");
            }
        });

        grandWritePermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        orgWifiBroader.getWifiConnections();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(wifiBroader);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(wifiBroader, mIntentFilter);
    }

    public static void grandWritePermission(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }
    }

}

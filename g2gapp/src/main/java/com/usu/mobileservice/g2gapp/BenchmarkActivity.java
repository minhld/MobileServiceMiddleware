package com.usu.mobileservice.g2gapp;

import android.net.wifi.WifiInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.usu.connection.utils.DevUtils;
import com.usu.connection.wfd.WFDSupporter;
import com.usu.connection.wifi.WiFiSupporter;
import com.usu.tinyservice.messages.binary.ResponseMessage;
import com.usu.tinyservice.network.Bridge;
import com.usu.tinyservice.network.Broker;
import com.usu.tinyservice.network.NetUtils;
import com.usu.tinyservice.network.ReceiveListener;

import com.usu.mobileservice.g2gapp.ServiceBClient;
import com.usu.mobileservice.g2gapp.ServiceBWorker;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BenchmarkActivity extends AppCompatActivity {

    // ------ FIRST BUTTON ROW ------
    @BindView(R.id.createGroupBtn)
    Button createGroupBtn;

    @BindView(R.id.discoverBtn)
    Button discoverBtn;

    @BindView(R.id.getDirectInfoBtn)
    Button getDirectInfoBtn;

    @BindView(R.id.startBrokerBtn)
    Button startBrokerBtn;

    @BindView(R.id.startWorkerBtn)
    Button startWorkerBtn;

    // ------ SECOND BUTTON ROW ------

    @BindView(R.id.startClientBtn)
    Button startClientBtn;

    @BindView(R.id.sendWifiDirectBtn)
    Button sendWifiDirectBtn;

    @BindView(R.id.startWFDBridgeBtn)
    Button startWFDBridgeBtn;

    @BindView(R.id.startWiFiBridgeBtn)
    Button startWiFiBridgeBtn;

    // ------ THIRD BUTTON ROW ------
//    @BindView(R.id.searchWiFiBtn)
//    Button searchWiFiBtn;

    @BindView(R.id.getWiFiInfoBtn)
    Button getWiFiInfoBtn;

    @BindView(R.id.startWiFiBrokerBtn)
    Button startWiFiBrokerBtn;

    @BindView(R.id.startWiFiWorkerBtn)
    Button startWiFiWorkerBtn;

    @BindView(R.id.startWiFiClientBtn)
    Button startWiFiClientBtn;

    @BindView(R.id.sendWifiDataBtn)
    Button sendWiFiDataBtn;

    // ------ FORTH TEXT ROW ------
    @BindView(R.id.ipText)
    EditText ipText;

    @BindView(R.id.workerPortText)
    EditText workerPortText;

    @BindView(R.id.clientPortText)
    EditText clientPortText;

    // ------ FIFTH LIST ROW ------

    @BindView(R.id.packageSizeText)
    EditText packageSizeText;

//    @BindView(R.id.workerPortText)
//    EditText workerPortText;
//
//    @BindView(R.id.clientPortText)
//    EditText clientPortText;

    // ------ SIXTH LIST ROW ------

    @BindView(R.id.deviceList)
    ListView deviceList;

    @BindView(R.id.wifiList)
    ListView wifiList;

    @BindView(R.id.infoText)
    TextView infoText;

    WFDSupporter wfdSupporter;
    WiFiSupporter wfSupport;

    ServiceBClient client, wifiClient;

    String brokerIp, wifiBrokerIp;

    Handler mainUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DevUtils.MESSAGE_GO_CONNECTED: {
                    WifiP2pInfo p2pInfo = (WifiP2pInfo) msg.obj;
                    brokerIp = p2pInfo.groupOwnerAddress.getHostAddress();
                    UITools.printLog(BenchmarkActivity.this, infoText, "Server " + brokerIp);
                    break;
                }
                case DevUtils.MESSAGE_CLIENT_CONNECTED: {
                    WifiP2pInfo p2pInfo = (WifiP2pInfo) msg.obj;
                    // initWorker(p2pInfo.groupOwnerAddress.getHostAddress());
                    brokerIp = p2pInfo.groupOwnerAddress.getHostAddress();
                    UITools.printLog(BenchmarkActivity.this, infoText, brokerIp);
                    break;
                }
                case DevUtils.MESSAGE_WIFI_DETECTED: {
                    WifiInfo wifiInfo = (WifiInfo) msg.obj;
                    wifiBrokerIp = DevUtils.getIPString(wifiInfo.getIpAddress());
                    ipText.setText(wifiBrokerIp);
                    break;
                }
                case DevUtils.MESSAGE_INFO: {
                    UITools.printLog(BenchmarkActivity.this, infoText, msg.obj);
                    // DevUtils.printLog(MainActivity.this, infoText, msg.obj);
                    break;
                }
            }
        }
    };

    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_benchmark);

        ButterKnife.bind(this);
        infoText.setMovementMethod(new ScrollingMovementMethod());

        // WiFi-Direct
        wfdSupporter = new WFDSupporter(this);
        deviceList.setAdapter(wfdSupporter.getDeviceListAdapter());

        // Original WiFi Interface
        wfSupport = new WiFiSupporter(this);
        wifiList.setAdapter(wfSupport.getWifiListAdapter());

        // set up the main handler
        NetUtils.setMainHandler(mainUiHandler);

        // ------ FIRST BUTTON ROW ------
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

        startBrokerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Broker(brokerIp);
            }
        });

        startWorkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWorker(brokerIp);
            }
        });

        // ------ SECOND BUTTON ROW ------

        startClientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initClient(brokerIp);
            }
        });

        sendWifiDirectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int packageSize = Integer.parseInt(packageSizeText.getText().toString());
                byte[] data = new byte[packageSize * 1024];
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) (Math.random() * 127);
                }

                // start the clock to measure time
                startTime = System.currentTimeMillis();

                client.sendData(new String(data));
            }
        });

        startWFDBridgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routerIp = wfSupport.getRouterWifiInfo(BenchmarkActivity.this);

                UITools.showInputDialog2(BenchmarkActivity.this, new UITools.InputDialogListener() {
                    @Override
                    public void inputDone(String resultStr) { }

                    @Override
                    public void inputDone(String localBrokerIp, int localWorkerPort,
                                          String remoteBrokerIp, int remoteClientPort) {
                        // string
                        new Bridge(localBrokerIp, localWorkerPort, remoteBrokerIp, remoteClientPort);
                    }
                }, routerIp);
            }
        });

        startWiFiBridgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routerIp = wfSupport.getRouterWifiInfo(BenchmarkActivity.this);

                UITools.showInputDialog2(BenchmarkActivity.this, new UITools.InputDialogListener() {
                    @Override
                    public void inputDone(String resultStr) { }
                    @Override
                    public void inputDone(String localBrokerIp, int localWorkerPort,
                                          String remoteBrokerIp, int remoteClientPort) {
                        // string
                        new Bridge(localBrokerIp, localWorkerPort, remoteBrokerIp, remoteClientPort);
                    }
                }, routerIp);
            }
        });

        // ------ THIRD BUTTON ROW ------
//        searchWiFiBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
//                        checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    wfSupport.requestPermission(MainActivity.this);
//                } else {
//                    // search for Wifi network list
//                    wfSupport.getWifiConnections();
//                }
//            }
//        });

        getWiFiInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfSupport.getWifiInfo(BenchmarkActivity.this);
            }
        });

        startWiFiBrokerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ip = ipText.getText().toString();
                int clientPort = Integer.parseInt(clientPortText.getText().toString());
                int workerPort = Integer.parseInt(workerPortText.getText().toString());

                new Broker(ip, clientPort, workerPort);
            }
        });

        startWiFiWorkerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routerIp = wfSupport.getRouterWifiInfo(BenchmarkActivity.this);

                UITools.showInputDialog(BenchmarkActivity.this, new UITools.InputDialogListener() {
                    @Override
                    public void inputDone(String resultStr) {
                        // string
                        String brokerIp = resultStr;
                        new ServiceBWorker(brokerIp);
                    }

                    @Override
                    public void inputDone(String localBrokerIp, int localWorkerPort,
                                          String remoteBrokerIp, int remoteClientPort) {
                        // empty
                    }
                }, routerIp);
            }
        });

        startWiFiClientBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWiFiClient(wifiBrokerIp);
            }
        });

        sendWiFiDataBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int packageSize = Integer.parseInt(packageSizeText.getText().toString());
                byte[] data = new byte[packageSize * 1024];
                for (int i = 0; i < data.length; i++) {
                    data[i] = (byte) (Math.random() * 127);
                }

                // start the clock to measure time
                startTime = System.currentTimeMillis();

                wifiClient.sendData(new String(data));
            }
        });

        // grant permission - for Android 6.0 and higher
        DevUtils.grandWritePermission(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // search for Wifi network list
        wfSupport.getWifiConnections();
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

    void initClient(String brokerIp) {
        client = new ServiceBClient(brokerIp, new ReceiveListener() {
            @Override
            public void dataReceived(String idChain, String funcName, byte[] data) {
                ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
                if (resp.functionName.equals(NetUtils.BROKER_INFO)) {
                    // a denied message from the Broker
                    String msg = (String) resp.outParam.values[0];
                    UITools.printLog(BenchmarkActivity.this, infoText, " Error " + msg);
                } else if (resp.functionName.equals("sendData")) {
                    String[] msgs = (String[]) resp.outParam.values;
                    long totalTime = System.currentTimeMillis() - startTime;
                    UITools.printLog(BenchmarkActivity.this, infoText, msgs[0] + " " + msgs[1] + " in " + totalTime + "ms");
                } else if (resp.functionName.equals("getFolderList")) {
                    String[] files = (String[]) resp.outParam.values;
                    UITools.printLog(BenchmarkActivity.this, infoText, "[Client-" + client.client.clientId + "] Received: ");
                    for (int i = 0; i < files.length; i++) {
                        UITools.printLog(BenchmarkActivity.this, infoText, "\t File: " + files[i]);
                    }
                }
            }
        });
    }

    void initWiFiClient(String brokerIp) {
        wifiClient = new ServiceBClient(brokerIp, new ReceiveListener() {
            @Override
            public void dataReceived(String idChain, String funcName, byte[] data) {
                ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
                if (resp.functionName.equals(NetUtils.BROKER_INFO)) {
                    // a denied message from the Broker
                    String msg = (String) resp.outParam.values[0];
                    UITools.printLog(BenchmarkActivity.this, infoText, " Error " + msg);
                } else if (resp.functionName.equals("sendData")) {
                    String[] msgs = (String[]) resp.outParam.values;
                    long totalTime = System.currentTimeMillis() - startTime;
                    UITools.printLog(BenchmarkActivity.this, infoText, msgs[0] + " " + msgs[1] + " in " + totalTime + "ms");
                } else if (resp.functionName.equals("getFolderList")) {
                    String[] files = (String[]) resp.outParam.values;
                    UITools.printLog(BenchmarkActivity.this, infoText, "[Client-" + wifiClient.client.clientId + "] Received: ");
                    for (int i = 0; i < files.length; i++) {
                        UITools.printLog(BenchmarkActivity.this, infoText, "\t File: " + files[i]);
                    }
                }
            }
        });
    }

    void initWorker(String brokerIp) {
        new ServiceBWorker(brokerIp);
    }
}

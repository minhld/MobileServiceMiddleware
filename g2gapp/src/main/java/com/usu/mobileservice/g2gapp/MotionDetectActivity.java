package com.usu.mobileservice.g2gapp;

import android.graphics.Bitmap;
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
import android.widget.ImageView;
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

import com.usu.mobileservice.g2gapp.ServiceCClient;
import com.usu.mobileservice.g2gapp.ServiceCWorker;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import java.nio.ByteBuffer;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MotionDetectActivity extends AppCompatActivity {

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

    @BindView(R.id.viewerImg)
    org.opencv.android.JavaCameraView viewerImg;

    @BindView(R.id.infoText)
    TextView infoText;

    WFDSupporter wfdSupporter;
    WiFiSupporter wfSupport;

    ServiceCClient client, wifiClient;

    String brokerIp, wifiBrokerIp;

    Handler mainUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case DevUtils.MESSAGE_GO_CONNECTED: {
                    WifiP2pInfo p2pInfo = (WifiP2pInfo) msg.obj;
                    brokerIp = p2pInfo.groupOwnerAddress.getHostAddress();
                    UITools.printLog(MotionDetectActivity.this, infoText, "Server " + brokerIp);
                    break;
                }
                case DevUtils.MESSAGE_CLIENT_CONNECTED: {
                    WifiP2pInfo p2pInfo = (WifiP2pInfo) msg.obj;
                    // initWorker(p2pInfo.groupOwnerAddress.getHostAddress());
                    brokerIp = p2pInfo.groupOwnerAddress.getHostAddress();
                    UITools.printLog(MotionDetectActivity.this, infoText, brokerIp);
                    break;
                }
                case DevUtils.MESSAGE_WIFI_DETECTED: {
                    WifiInfo wifiInfo = (WifiInfo) msg.obj;
                    wifiBrokerIp = DevUtils.getIPString(wifiInfo.getIpAddress());
                    ipText.setText(wifiBrokerIp);
                    break;
                }
                case DevUtils.MESSAGE_INFO: {
                    UITools.printLog(MotionDetectActivity.this, infoText, msg.obj);
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
        setContentView(R.layout.activity_motion);

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

                // client.resolveImage(null, null);
                statusReady = true;
            }
        });

        startWFDBridgeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String routerIp = wfSupport.getRouterWifiInfo(MotionDetectActivity.this);

                UITools.showInputDialog2(MotionDetectActivity.this, new UITools.InputDialogListener() {
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
                String routerIp = wfSupport.getRouterWifiInfo(MotionDetectActivity.this);

                UITools.showInputDialog2(MotionDetectActivity.this, new UITools.InputDialogListener() {
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

        getWiFiInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                wfSupport.getWifiInfo(MotionDetectActivity.this);
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
                String routerIp = wfSupport.getRouterWifiInfo(MotionDetectActivity.this);

                UITools.showInputDialog(MotionDetectActivity.this, new UITools.InputDialogListener() {
                    @Override
                    public void inputDone(String resultStr) {
                        // string
                        String brokerIp = resultStr;
                        new ServiceCWorker(brokerIp);
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

                // wifiClient.resolveImage(null, null);
            }
        });

        // grant permission - for Android 6.0 and higher
        DevUtils.grandWritePermission(this);

        // set listener for Image Viewer
        viewerImg.setCvCameraViewListener(new CvCameraViewListener2X());
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
        if (viewerImg != null) {
            viewerImg.disableView();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        wfdSupporter.runOnResume();
        if (!OpenCVLoader.initDebug()) {
            UITools.printLog(MotionDetectActivity.this, infoText, "OpenCV library not found.");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_2_0, this, mLoaderCallback);
        } else {
            UITools.printLog(MotionDetectActivity.this, infoText, "OpenCV library found!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (viewerImg != null) {
            viewerImg.disableView();
        }
    }

    boolean statusReady = false;

    class CvCameraViewListener2X implements CameraBridgeViewBase.CvCameraViewListener2 {
        @Override
        public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
            Mat orgMat = inputFrame.rgba();
            if (client != null && statusReady) {
                byte[] bytes = new byte[(int) (orgMat.total() * orgMat.elemSize())];
                orgMat.get(0, 0, bytes);
                client.resolveImage(bytes);
                statusReady = false;
            }
            return orgMat;
        }

        @Override
        public void onCameraViewStarted(int width, int height) {

        }

        @Override
        public void onCameraViewStopped() {

        }
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    // Log.i(TAG, "OpenCV loaded successfully");
                    viewerImg.enableView();
                    break;
                } default: {
                    super.onManagerConnected(status);
                    break;
                }
            }
        }
    };

    void initClient(String brokerIp) {
        client = new ServiceCClient(brokerIp, new ReceiveListener() {
            @Override
            public void dataReceived(String idChain, String funcName, byte[] data) {
                ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
                if (resp.functionName.equals(NetUtils.BROKER_INFO)) {
                    // a denied message from the Broker
                    String msg = (String) resp.outParam.values[0];
                    UITools.printLog(MotionDetectActivity.this, infoText, " Error " + msg);
                } else if (resp.functionName.equals("resolveImage")) {
                    Integer[] vals = (Integer[]) resp.outParam.values;
                    long totalTime = System.currentTimeMillis() - startTime;
                    UITools.printLog(MotionDetectActivity.this, infoText, "[" + vals[0] + ", " + vals[1] + ", " + vals[2] + "] in " + totalTime + "ms");
                    statusReady = true;
                }
            }
        });
    }

    void initWiFiClient(String brokerIp) {
        wifiClient = new ServiceCClient(brokerIp, new ReceiveListener() {
            @Override
            public void dataReceived(String idChain, String funcName, byte[] data) {
                ResponseMessage resp = (ResponseMessage) NetUtils.deserialize(data);
                if (resp.functionName.equals(NetUtils.BROKER_INFO)) {
                    // a denied message from the Broker
                    String msg = (String) resp.outParam.values[0];
                    UITools.printLog(MotionDetectActivity.this, infoText, " Error " + msg);
                } else if (resp.functionName.equals("resolveImage")) {
                    String[] msgs = (String[]) resp.outParam.values;
                    long totalTime = System.currentTimeMillis() - startTime;
                    UITools.printLog(MotionDetectActivity.this, infoText, msgs[0] + " " + msgs[1] + " in " + totalTime + "ms");
                    statusReady = true;
                }
            }
        });
    }

    void initWorker(String brokerIp) {
        new ServiceCWorker(brokerIp);
    }
}

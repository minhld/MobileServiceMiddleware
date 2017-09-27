package com.usu.mobileserviceapp;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.usu.mobileservice.jobex.DataParser;
import com.usu.mobileservice.jobex.JobPackage;
import com.usu.mobileservice.jobimpls.WordDataParserImpl;
import com.usu.mobileservice.pbsbjob.MidWorker;
import com.usu.mobileservice.pubsublib.Broker;
import com.usu.mobileservice.pubsublib.Client;
import com.usu.mobileservice.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.createGroupBtn)
    Button createGroupBtn;

    @BindView(R.id.discoverBtn)
    Button discoverBtn;

    @BindView(R.id.pubBtn)
    Button pubBtn;

    @BindView(R.id.subBtn)
    Button subBtn;

    @BindView(R.id.workerBtn)
    Button workerBtn;

    @BindView(R.id.deviceList)
    ListView deviceList;

    @BindView(R.id.infoText)
    TextView infoText;

    @BindView(R.id.imageView)
    ImageView viewer;

    Handler mainUiHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Utils.MESSAGE_INFO: {
                    String strMsg = (String) msg.obj;
                    UITools.writeLog(MainActivity.this, infoText, strMsg);
                    break;
                }
            }
        }
    };

    MidSupporter midSupporter;

    long startTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        infoText.setMovementMethod(new ScrollingMovementMethod());

        midSupporter = new MidSupporter(this, mainUiHandler);

        deviceList.setAdapter(midSupporter.getDeviceListAdapter());

        // --- BUTTON EVENT HANDLERS ---

        createGroupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midSupporter.createGroup();
            }
        });

        discoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                midSupporter.discoverPeers();
            }
        });

        pubBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initSystem();
            }
        });

        subBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initClient();
            }
        });

        workerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initWorker();
            }
        });

        // grant read/write permission for Android 6.x
        Utils.grandWritePermission(this);
    }

    /**
     * init the basic infrastructure including broker and workers
     */
    private void initSystem() {
        String brokerIp = "*";
        // start server (broker)
        new Broker(this, brokerIp);
        UITools.writeLog(MainActivity.this, infoText, "server started");
    }


    /**
     * start worker
     */
    private void initWorker() {
        // start workers - this workers will be move to another part - not along with the broker

        String brokerIp = UITools.GO_IP;
        new MidWorker(this, brokerIp) {
            @Override
            public void workerStarted(String workerId) {
                UITools.writeLog(MainActivity.this, infoText, "worker [" + workerId + "] started.");
            }

            @Override
            public void receivedTask(String clientId, int dataSize) {
                UITools.writeLog(MainActivity.this, infoText, "worker [" + this.workerId + "] received " + dataSize + " bytes from client [" + workerId + "].");
            }

            @Override
            public void workerFinished(String workerId, TaskDone taskDone) {
                UITools.writeLog(MainActivity.this, infoText, "worker [" + this.workerId + "] finished job. time: " + taskDone.durration);
            }

        };

    }

    /**
     * init client - on client devices
     */
    private void initClient() {
        Client client = new Client(Utils.BROKER_SPECIFIC_IP) {
            @Override
            public void clientStarted(String clientId) {
                // print out
                UITools.writeLog(MainActivity.this, infoText, "client [" + clientId + "] started");
            }

            @Override
            public void send() {
                // dispatch jobs to clients
                String dataPath = Utils.getDownloadPath() + "/mars.jpg";
                String jobPath = Utils.getDownloadPath() + "/job.jar";

                startTime = System.currentTimeMillis();

                try {
                    JobSupporter.initDataParser(MainActivity.this, jobPath);
                    byte[] jobData = JobSupporter.getData(dataPath);

                    JobPackage job = new JobPackage(0, this.clientId, jobData, jobPath);
                    byte[] jobPkg = job.toByteArray();

                    // print out
                    UITools.writeLog(MainActivity.this, infoText, "client sent: " + jobPkg.length);

                    this.sendMessage(jobPkg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void resolveResult(byte[] result) {
                final long durr = System.currentTimeMillis() - startTime;

                UITools.writeLog(MainActivity.this, infoText, "client received result: " + result.length + " bytes");
                DataParser parser = new WordDataParserImpl();
                try {
                    final String resultStr = (String) parser.parseBytesToObject(result);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            UITools.writeLog(MainActivity.this, infoText, resultStr);
                            UITools.writeLog(MainActivity.this, infoText, "total time: " + durr + "ms");
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

    }

    @Override
    protected void onPause() {
        super.onPause();
        midSupporter.actOnPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        midSupporter.actOnResume();
    }
}
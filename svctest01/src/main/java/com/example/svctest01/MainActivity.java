package com.example.svctest01;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.usu.svcmid.wfd.WiFiDiscoveryManager;

import utils.Utils;

public class MainActivity extends AppCompatActivity {

    TextView infoText;

    WiFiDiscoveryManager wifiManager;
    Handler mHandler = new Handler() {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        infoText = (TextView) findViewById(R.id.status);

        wifiManager = new WiFiDiscoveryManager(this);
        wifiManager.setWFDHandler(mHandler);
    }

    public void clickRegister(View v) {
        wifiManager.startRegistration();
    }

    public void clickDiscover(View v) {
        wifiManager.discoverService();
    }

    public void clickConnect(View v) {

    }

    public void clickSend(View v) {

    }
}

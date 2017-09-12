package com.example.svctest01;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.usu.svcmid.wfd.WiFiDiscoveryManager;

public class MainActivity extends AppCompatActivity {

    WiFiDiscoveryManager wifiManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        wifiManager = new WiFiDiscoveryManager(this);
    }

    public void clickRegister() {
        wifiManager.startRegistration();
    }

    public void clickDiscover() {

    }

    public void clickConnect() {

    }

    public void clickSend() {

    }
}

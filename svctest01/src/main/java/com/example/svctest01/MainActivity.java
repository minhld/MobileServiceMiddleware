package com.example.svctest01;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.usu.svcmid.wfd.Supporter;

import com.usu.svcmid.utils.Utils;

public class MainActivity extends AppCompatActivity {
    ListView deviceList;
    TextView infoText;
    EditText chatInput;

    Supporter supporter;
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

        supporter = new Supporter(this, mHandler);

        deviceList = (ListView) findViewById(R.id.deviceList);
        deviceList.setAdapter(supporter.getServiceAdapter());
        infoText = (TextView) findViewById(R.id.status);
        infoText.setMovementMethod(new ScrollingMovementMethod());

        chatInput = (EditText) findViewById(R.id.chatInput);
    }

    public void clickRegister(View v) {
        supporter.startRegister();
    }

    public void clickDiscover(View v) {
        supporter.startDiscovery();
    }

    public void clickConnect(View v) {

    }

    public void clickSend(View v) {

    }
}

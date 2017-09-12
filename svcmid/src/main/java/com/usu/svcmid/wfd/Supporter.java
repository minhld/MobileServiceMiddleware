package com.usu.svcmid.wfd;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Handler;

import com.usu.svcmid.R;

import java.util.Collection;

import utils.WiFiDevicesAdapter;

/**
 * Created by lee on 9/12/17.
 */

public class Supporter {
    Activity context;
    WiFiDiscoveryManager wfdManager;
    IntentFilter mIntentFilter;
    WiFiDevicesAdapter deviceListAdapter;

    public Supporter(Activity context, final Handler mainHandler) {
        this.context = context;

        wfdManager = new WiFiDiscoveryManager(this.context);
        wfdManager.setWFDHandler(mainHandler);
        wfdManager.setBroadCastListener(new WiFiDiscoveryManager.BroadCastListener() {
            @Override
            public void peerDeviceListUpdated(Collection<WifiP2pDevice> deviceList) {
                deviceListAdapter.clear();
                deviceListAdapter.addAll(deviceList);
                deviceListAdapter.notifyDataSetChanged();
            }

            @Override
            public void wfdEstablished(WifiP2pInfo p2pInfo) {
                if (p2pInfo.groupOwnerAddress == null) {
                    return;
                }

                String brokerIp = p2pInfo.groupOwnerAddress.getHostAddress();
                if (p2pInfo.groupFormed && p2pInfo.isGroupOwner) {
//                    if (mBroker != null) {
//                        mainHandler.obtainMessage(Utils.MESSAGE_INFO, "broker reused").sendToTarget();
//                        return;
//                    }
//
//                    // the group owner will also become a broker
//                    mBroker = new Broker(brokerIp);
                } else if (p2pInfo.groupFormed) {
//                    // let user select to be either publisher or subscriber
//                    new Publisher(brokerIp, mainHandler).start();
                }
            }
        });
        mIntentFilter = wfdManager.getSingleIntentFilter();
        deviceListAdapter = new WiFiDevicesAdapter(this.context, R.layout.row_devices, wfdManager);

    }

}


package com.usu.connection.wifi;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.os.Handler;

import com.usu.connection.R;
import com.usu.connection.wfd.WFDListAdapter;
import com.usu.connection.wfd.WFDManager;

import java.util.Collection;

/**
 * This class utilizes the pub-sub library to provide full functionality of
 * publish-subscribe to the client application
 *
 * Created by minhld on 8/6/2016.
 */
public class WiFiSupporter {
    Activity context;
    WFDManager wfdManager;
    IntentFilter mIntentFilter;
    WFDListAdapter deviceListAdapter;

    public WiFiSupporter(Activity context, final Handler mainHandler) {
        this.context = context;

        wfdManager = new WFDManager(this.context);
        wfdManager.setWFDListener(mainHandler);
        wfdManager.setBroadCastListener(new WFDManager.BroadCastListener() {
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
                    // when device becomes an Owner
                    // a Broker should be placed on the Owner
                } else if (p2pInfo.groupFormed) {
                    // when device becomes a Client
                    // a Client or Worker should be placed on the Client
                }
            }
        });
        mIntentFilter = wfdManager.getSingleIntentFilter();
        deviceListAdapter = new WFDListAdapter(this.context, R.layout.row_devices, wfdManager);
    }

    /**
     * discover the peers in the WiFi peer-to-peer mobile network
     */
    public void discoverPeers() {
        // start discovering
        wfdManager.discoverPeers();
        mIntentFilter = wfdManager.getSingleIntentFilter();
    }

    /**
     * create itself to be a group owner to handle a private group
     */
    public void createGroup() {
        wfdManager.createGroup();
    }

    /**
     *
     */
    public void requestGroupInfo() {
        wfdManager.requestGroupInfo();
    }

    /**
     * return the peer list adapter (for UI usage)
     * @return
     */
    public WFDListAdapter getDeviceListAdapter() {
        return deviceListAdapter;
    }

    /**
     * this should be added at the end of onPause on main activity
     */
    public void runOnPause() {
        if (wfdManager != null && mIntentFilter != null) {
            this.context.unregisterReceiver(wfdManager);
        }
    }

    /**
     * this should be added at the end of onResume on main activity
     */
    public void runOnResume() {
        if (wfdManager != null && mIntentFilter != null) {
            this.context.registerReceiver(wfdManager, mIntentFilter);
        }
    }
}

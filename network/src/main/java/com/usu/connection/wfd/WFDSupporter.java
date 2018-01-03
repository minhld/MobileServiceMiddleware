package com.usu.connection.wfd;

import android.app.Activity;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;

import com.usu.connection.R;
import com.usu.connection.utils.DevUtils;
import com.usu.tinyservice.network.NetUtils;

import java.util.Collection;

/**
 * This class utilizes the pub-sub library to provide full functionality of
 * publish-subscribe to the client application
 *
 * Created by minhld on 8/6/2016.
 */
public class WFDSupporter {
    Activity context;
    WFDManager wfdManager;
    IntentFilter mIntentFilter;
    WFDListAdapter deviceListAdapter;

    public WFDSupporter(Activity context) {
        this.context = context;

        wfdManager = new WFDManager(this.context);
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

                // String brokerIp = p2pInfo.groupOwnerAddress.getHostAddress();

                if (p2pInfo.groupFormed && p2pInfo.isGroupOwner) {
                    // When the device becomes an GO
                    // - a Broker should be placed on the GO
                    // - this Broker will be set on WiFi-Direct interface. it will hold
                    //   the default IP 192.168.49.1

                    // raise an event back to the main UI thread
                    NetUtils.raiseEvent(DevUtils.MESSAGE_GO_CONNECTED, p2pInfo);
                    // new Broker("*");
                    // new Broker(brokerIp);
                } else if (p2pInfo.groupFormed) {
                    // When the device becomes a Client
                    // - a Worker should be placed on the Client
                    // - the Worker will hold an IP and connect to the Broker which is
                    //   located on the GO (holding IP 192.168.49.1)
                    NetUtils.raiseEvent(DevUtils.MESSAGE_CLIENT_CONNECTED, p2pInfo);
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

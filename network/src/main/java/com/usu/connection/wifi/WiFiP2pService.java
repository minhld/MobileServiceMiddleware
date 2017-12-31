
package com.usu.connection.wifi;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.Map;

/**
 * A structure to hold service information.
 * @author minhld
 */
public class WiFiP2pService {
    public WifiP2pDevice device;
    public Map<String, String> record;
    public String name = "";
    public String type = "";

    public WiFiP2pService() {}

    public WiFiP2pService(WifiP2pDevice device, Map<String, String> record, String name, String type) {
        this.device = device;
        this.record = record;
        this.name = name;
        this.type = type;
    }
}

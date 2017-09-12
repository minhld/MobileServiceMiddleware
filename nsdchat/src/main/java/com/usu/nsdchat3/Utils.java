package com.usu.nsdchat3;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.nio.ByteOrder;
import java.util.Enumeration;

/**
 * Created by minhld on 9/11/2017.
 */

public class Utils {
    public static void saveInt(Context cxt, String key, int value) {
        SharedPreferences.Editor prefsEditor = cxt.getSharedPreferences("kkd", Context.MODE_PRIVATE).edit();
        prefsEditor.putInt(key, value);
        prefsEditor.commit();
    }

    public static int getInt(Context cxt, String key) {
        SharedPreferences prefs = cxt.getSharedPreferences("kkd", Context.MODE_PRIVATE);
        int val = prefs.getInt(key, -1);
        return val;
    }

    public static String getString(Context cxt, String key) {
        SharedPreferences prefs = cxt.getSharedPreferences("kkd", Context.MODE_PRIVATE);
        String val = prefs.getString(key, null);
        return val;
    }

    public static void clearKey(Context cxt, String key) {
        SharedPreferences.Editor prefsEditor = cxt.getSharedPreferences("kkd", Context.MODE_PRIVATE).edit();
        prefsEditor.remove(key);
        prefsEditor.commit();
    }

    public static String getMyIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        return inetAddress.getHostAddress();
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("Utils", ex.toString());
        }
        return null;
    }

    public static String getWiFiIPAddress(Context context) {
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = getDottedDecimalIP(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    public static String getDottedDecimalIP(int ipAddr) {

        if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
            ipAddr = Integer.reverseBytes(ipAddr);
        }

        byte[] ipByteArray = BigInteger.valueOf(ipAddr).toByteArray();

        //convert to dotted decimal notation:
        String ipAddrStr = getDottedDecimalIP(ipByteArray);
        return ipAddrStr;
    }

    public static String getDottedDecimalIP(byte[] ipAddr) {
        //convert to dotted decimal notation:
        String ipAddrStr = "";
        for (int i = 0; i < ipAddr.length; i++) {
            if (i > 0) {
                ipAddrStr += ".";
            }
            ipAddrStr += ipAddr[i] & 0xFF;
        }
        return ipAddrStr;
    }

    public static boolean checkPermission(String strPermission, Context _c) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = ContextCompat.checkSelfPermission(_c, strPermission);
            if (result == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    public static void requestPermission(String strPermission, int perCode, Activity activity) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(activity, strPermission)) {
            Toast.makeText(activity, "GPS permission allows us to access location data." +
                            " Please allow in App Settings for additional functionality.",
                    Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(activity, new String[]{strPermission}, perCode);
        }
    }

}

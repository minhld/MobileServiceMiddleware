package com.usu.connection.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by lee on 9/12/17.
 */
public class DevUtils {
    public static final int MESSAGE_INFO = 0x500 + 6;

    // these constants are for PERMISSION GRANT
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static class XDevice {
        public String address;
        public String name;

        // public XDevice () {}

        public XDevice (String address, String name) {
            this.address = address;
            this.name = name;
        }
    }

    /**
     * list of connected client devices that currently connect to current server<br>
     * this list will be used as iterating devices for sending, checking, etc...
     */
    public static Map<String, XDevice> connectedDevices = new HashMap<>();

    public static String getFullServiceName(String svcName) {
        return getDeviceName() + "_" + svcName;
    }

    /**
     * get the device's name
     * @return
     */
    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER.toLowerCase().replaceAll(" ", "_");
        String model = Build.MODEL.toLowerCase().replaceAll(" ", "_");
        if (model.startsWith(manufacturer)) {
            return model;
        } else {
            return manufacturer + "_" + model;
        }
    }

    /**
     * grant permission, to support Android 6.0 and higher versions
     *
     * @param activity
     */
    public static void grandWritePermission(Activity activity) {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1) {
            int permission = ActivityCompat.checkSelfPermission(activity,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);

            if (permission != PackageManager.PERMISSION_GRANTED) {
                // We don't have permission so prompt the user
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE, REQUEST_EXTERNAL_STORAGE);
            }
        }
    }

    /**
     * appends a test value (key + value) to a text file in the Download folder
     *
     * @param fileName
     * @param test
     * @param values
     */
    public static void appendTestInfo(String fileName, String test, long... values) {
        return;

        /*
        try {
            File downloadFolder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            String testResultPath = downloadFolder.getAbsolutePath() + "/" + fileName + ".txt";
            FileWriter writer = new FileWriter(testResultPath, true);
            if (values.length == 0) {
                writer.write(test + " " + new Date().getTime() + "\n");
            } else {
                writer.write(test);
                for (int i = 0; i < values.length; i++) {
                    writer.write(" " + values[i]);
                }
                writer.write("\n");
            }

            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        */
    }
}

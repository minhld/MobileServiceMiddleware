package com.usu.svcmid.utils;

import android.os.Build;

/**
 * Created by lee on 9/12/17.
 */
public class Utils {
    public static final int MESSAGE_INFO = 0x500 + 6;

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
}

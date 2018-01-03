package com.usu.mobileservice.g2gapp;

import android.app.Activity;
import android.text.SpannableStringBuilder;
import android.widget.TextView;


/**
 * Created by minhld on 01/28/2016.
 */
public class UITools {

    public static void printLog(Activity c, final TextView log, final String msg) {
        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append(msg + "\r\n");
            }
        });
    }

    public static void printLog(Activity c, final TextView log, final Object msg) {
        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append((SpannableStringBuilder) msg);
            }
        });
    }
}

package com.usu.mobileservice.g2gapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableStringBuilder;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by minhld on 01/28/2016.
 */
public class UITools {
    public static void printLog(Activity c, final TextView log, final Object msg) {
        c.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.append((SpannableStringBuilder) msg);
            }
        });
    }
}

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
import android.widget.Toast;


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

    public static void printMsg(Context c, String msg) {
        Toast.makeText(c, msg, Toast.LENGTH_LONG);
    }

    /**
     * open a dialog to prompt text
     *
     * @param c
     * @param listener
     */
    public static void showInputDialog2(Context c, final InputDialogListener listener, String... defs) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(c);
        View promptView = layoutInflater.inflate(R.layout.input_dialog_2, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setView(promptView);

        final EditText localBrokerIpText = (EditText) promptView.findViewById(R.id.localBrokerIpText);
        final EditText remoteBrokerIpText = (EditText) promptView.findViewById(R.id.remoteBrokerIpText);
        if (defs.length > 0) {
            localBrokerIpText.setText(defs[0]);
            remoteBrokerIpText.setText(defs[0]);
        }

        final EditText localWorkerPortText = (EditText) promptView.findViewById(R.id.localWorkerPortText);
        final EditText remoteClientPortText = (EditText) promptView.findViewById(R.id.remoteClientPortText);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.inputDone(localBrokerIpText.getText().toString(),
                                        Integer.parseInt(localWorkerPortText.getText().toString()),
                                        remoteBrokerIpText.getText().toString(),
                                        Integer.parseInt(remoteClientPortText.getText().toString()));
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    /**
     * open a dialog to prompt text
     *
     * @param c
     * @param listener
     */
    public static void showInputDialog(Context c, final InputDialogListener listener, String... defs) {
        // get prompts.xml view
        LayoutInflater layoutInflater = LayoutInflater.from(c);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
        alertDialogBuilder.setView(promptView);

        final EditText editText = (EditText) promptView.findViewById(R.id.edittext);
        if (defs.length > 0) editText.setText(defs[0]);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        listener.inputDone(editText.getText().toString());
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    public interface InputDialogListener {
        void inputDone(String resultStr);
        void inputDone(String localBrokerIpText, int localWorkerPort, String remoteBrokerIp, int remoteClientPort);
    }
}

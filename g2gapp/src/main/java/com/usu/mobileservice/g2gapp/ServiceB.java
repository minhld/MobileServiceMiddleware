package com.usu.mobileservice.g2gapp;

import com.usu.servicemiddleware.annotations.CommModel;
import com.usu.servicemiddleware.annotations.MobileService;
import com.usu.servicemiddleware.annotations.ServiceMethod;
import com.usu.servicemiddleware.annotations.SyncMode;
import com.usu.servicemiddleware.annotations.TransmitType;

import java.io.File;

/**
 * Created by lee on 9/23/17.
 */
@MobileService(
        version = "1.2",
        commModel = CommModel.ClientServer,
        transmitType = TransmitType.Binary)
public class ServiceB {

    @ServiceMethod(syncMode = SyncMode.Async)
    public String[] sendData(String msg) {
        return new String[] { "receive message: ", Integer.toString(msg.length()) };
    }

    @ServiceMethod(
            syncMode = SyncMode.Async,
            suffix = "2")
    public String[] getFolderList(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles();
        String[] res = new String[files.length];
        for (int i = 0; i < files.length; i++) {
            res[i] = files[i].getAbsolutePath();
        }
        return res;
    }
}
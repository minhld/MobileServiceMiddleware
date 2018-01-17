package com.usu.mobileservice.g2gapp;

import com.usu.servicemiddleware.annotations.CommModel;
import com.usu.servicemiddleware.annotations.MobileService;
import com.usu.servicemiddleware.annotations.ServiceMethod;
import com.usu.servicemiddleware.annotations.SyncMode;
import com.usu.servicemiddleware.annotations.TransmitType;

import org.opencv.core.Mat;

import java.io.File;

/**
 * Created by lee on 9/23/17.
 */
@MobileService(
        version = "1.2",
        commModel = CommModel.ClientServer,
        transmitType = TransmitType.Binary)
public class ServiceC {

    @ServiceMethod(syncMode = SyncMode.Async)
    public Integer[] resolveImage(byte[] img1, byte[] img2) {
        Mat orgMat = new Mat();
        return null;
    }
}
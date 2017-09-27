package com.usu.mobileserviceapp;

import android.content.Context;

import com.usu.mobileservice.jobex.DataParser;
import com.usu.mobileservice.jobimpls.WordDataParserImpl;

/**
 * Created by minhld on 10/17/2016.
 */

public class JobSupporter {
    static DataParser dataParser;

    public static void initDataParser(Context c, String jarPath) throws Exception {
        dataParser = new WordDataParserImpl();
    }

    public static byte[] getData(String filePath) throws Exception {
        Object dataObj = dataParser.loadObject(filePath);
        return dataParser.parseObjectToBytes(dataObj);
    }
}

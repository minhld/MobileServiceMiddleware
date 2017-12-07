package com.usu.observer;

//
//import org.apache.http.HttpResponse;
//import org.apache.http.NameValuePair;
//import org.apache.http.client.HttpClient;
//import org.apache.http.client.entity.UrlEncodedFormEntity;
//import org.apache.http.client.methods.HttpPost;
//import org.apache.http.impl.client.DefaultHttpClient;
//import org.apache.http.message.BasicNameValuePair;

import android.provider.MediaStore;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by minhld on 12/7/2017.
 */

public class EventDb {
    static String urlPattern = "http://129.123.7.41:8080";
    static SimpleDateFormat sdf = new SimpleDateFormat("MM-dd-yyyy hh:mm:ss a");

    public static List<Event> getEvents(int top, int offset) {
        List<Event> events = new ArrayList<>();

        try {
            OkHttpClient client = new OkHttpClient.Builder().build();
            MediaType jsonType = MediaType.parse("application/json; charset=utf-8");
            Request request = new Request.Builder().url(urlPattern + "/minhlab/receiveEvents")
                                        .post(RequestBody.create(jsonType, "{ \"top\" : 10 }")).build();
            Response response = client.newCall(request).execute();
            String res = response.body().string();

            JSONArray array = new JSONArray(res);
            JSONObject item, itemInfo;
            Event e;
            for (int i = 0; i < array.length(); i++) {
                item = (JSONObject) array.get(i);
                e = new Event();
                e.time = sdf.format(new Date(item.getLong("time")));
                e.type = item.getString("type");
                itemInfo = new JSONObject(item.getString("info"));
                e.info = urlPattern + "/imgs/" + getFileName(itemInfo.getString("file"));
                events.add(e);
            }

            response.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return events;
    }

    static String getFileName(String file) {
        int lastIndex = file.lastIndexOf("/");
        return file.substring(lastIndex + 1);
    }
}

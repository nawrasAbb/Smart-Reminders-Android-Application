package com.example.smartremindersapp2;

import android.app.Notification;
import android.content.Context;
import android.location.Location;
import android.util.Log;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.*;
import cz.msebera.android.httpclient.client.HttpClient;

//class that responsible to get to the user the desired location according to
// his choice
public class GooglePlacesClient {
    public void getResponseThread(final String url, String lat1 , String lang1, String title,
                                  String content,String key, Context context,String type) {
        new Thread(new Runnable() {
            public void run() {
                String cadHTTP = getResponse(url);
                do1(cadHTTP,lat1, lang1,  title,
                        content, key,  context,type);
            }
        }).start();
    }

    public static String do1(String cadHTTP, String lat1, String lang1, String title,
                             String content, String key, Context context,String type) {
        String Name;
        String lat;
        String lng;
        String address;
        String category=type;
        int end_indx_place;
        Name = getparameters(cadHTTP, "\"name\" : \"", "\"");
        lat = getparameters(cadHTTP, "lat\" : ", ",");
        lng = getparameters(cadHTTP, "lng\" : ", "\n");
        address = getparameters(cadHTTP, " \"vicinity\" : \"", "\"");
        end_indx_place = cadHTTP.indexOf(" \"vicinity\" : \"") + 15;
        cadHTTP = cadHTTP.substring(end_indx_place);
        float[] distance = new float[1];
        Location.distanceBetween(Double.parseDouble( lat ), Double.parseDouble( lng), Double.parseDouble( lat1), Double.parseDouble( lang1), distance);
        if (distance[0] < 2000) {
            NotificationHelper notificationHelper = new NotificationHelper(context);
            Notification nb = notificationHelper.getChannelNotification(key
                    , HomePage.class, title, content,cadHTTP,Name,address,category,lat1,lang1);
            notificationHelper.getManager().notify(key.hashCode(), nb);
        }
        return cadHTTP;
    }


    private static String getparameters(String response, String searchString, String end){
        int firstIndex=response.indexOf(searchString)+searchString.length();
        String mySubstring=response.substring(firstIndex);
        int lastIndex=firstIndex+mySubstring.indexOf(end);
        return (response.substring(firstIndex,lastIndex));
    }


    public String getResponse(String url) {
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet del = new HttpGet(url);
        del.setHeader("content-type", "application/json");
        String respStr;
        try {
            HttpResponse resp = httpClient.execute(del);
            respStr = EntityUtils.toString(resp.getEntity());
        } catch(Exception ex) {
            Log.e("RestService","Error!", ex);
            respStr = "";
        }
        Log.e("getResponse",respStr);
        return respStr;
    }
}




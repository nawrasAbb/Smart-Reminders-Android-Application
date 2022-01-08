package com.example.smartremindersapp2;

import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpGet;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.util.EntityUtils;

public class ReminderReceiver extends BroadcastReceiver  {

    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }


    // if this is a date notification only then we save the notification in the alarm manager
    // else if this a location reminder then we only update the state of the reminder to true,
    // because the location in the background handle only reminders with true state
    @Override
    public void onReceive(Context context, Intent intent){
        String key=intent.getStringExtra("key");
        if (intent.getStringExtra("title").equals("Date Reminder")){
            NotificationHelper notificationHelper = new NotificationHelper(context);
            Notification nb = notificationHelper.getChannelNotification(key
                    , HomePage.class, intent.getStringExtra("title"), intent.getStringExtra("content"),"","","","","","");
            notificationHelper.getManager().notify(0, nb);
        }else{
            //change date status to true
            HashMap map = new HashMap();
            map.put("dateState",true);
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(intent.getStringExtra("userName")).child("reminder_list").child(intent.getStringExtra("key"));
            ref.updateChildren(map);
        }
    }
}
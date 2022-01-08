package com.example.smartremindersapp2;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import java.util.Calendar;
import java.util.Date;

public class NotifierLocationRemind extends Service {
    private static final String CHANNEL_ID = "MyNotificationChannelID2";

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    // we need class that extend from service in order to keep the application work
    // in background and be able to send notification
    // by using setExact we save in the manager the date to notify
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar calendar=Calendar.getInstance();
        Date date = calendar.getTime();
        String ContentTitle;
        String ContentText;
        String key=intent.getStringExtra("key");
        Integer pendingKey=intent.getIntExtra("Pending_key",0);
        Intent notificationIntent = new Intent(this, HomePage.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, pendingKey , notificationIntent, 0);
        ContentTitle=intent.getStringExtra("title");
        ContentText=intent.getStringExtra("content");
        date = new Date(intent.getExtras().getLong("date", -1));
        if(intent.getStringExtra("type").equals("Time")){
            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .addAction(R.drawable.ic_cancel, "remove", pendingIntent)
                    .setContentTitle(ContentTitle)
                    .setContentText(ContentText)
                    .setSmallIcon(R.drawable.ic_launcher_foreground)
                    .setContentIntent(pendingIntent)
                    .build();
            notification.flags = Notification.FLAG_AUTO_CANCEL;
            startForeground(0, notification);
        }

        Intent intent2 = new Intent(this, ReminderReceiver.class);
        intent2.putExtra("key",key);
        intent2.putExtra("userName",intent.getStringExtra("userName"));
        intent2.putExtra("title",ContentTitle);
        intent2.putExtra("content",ContentText);
        intent2.putExtra("locationType",intent.getStringExtra("locationType"));
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(getApplicationContext()
                , pendingKey, intent2, 0);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, date.getTime(), pendingIntent2);
        return super.onStartCommand(intent, flags, startId);
    }
}
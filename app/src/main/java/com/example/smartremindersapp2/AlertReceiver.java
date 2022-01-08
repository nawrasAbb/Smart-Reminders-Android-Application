package com.example.smartremindersapp2;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.os.CountDownTimer;
import android.os.IBinder;

public class AlertReceiver extends BroadcastReceiver {
    private static Ringtone ringtone;
    private static boolean stopring;
    @Override
    public IBinder peekService(Context myContext, Intent service) {
        return super.peekService(myContext, service);
    }
    public static Ringtone getRingtone() {
        return ringtone;
    }

    //update the current alarm that rings
    // and play a ring until the status of the current reminder change to false
    @Override
    public void onReceive(Context context, Intent intent){
        SharedPreferences sharedPreferences=context
                .getSharedPreferences("U",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        String key=intent.getStringExtra("key");
        String title=intent.getStringExtra("title");
        editor.putString("Current Ring Key",key);
        editor.commit();
        stopring=true;
        NotificationHelper notificationHelper = new NotificationHelper(context);
        Notification nb = notificationHelper.getChannelNotification(intent.getStringExtra("key")
                ,all_alarms.class,"Alarm!",title,"","alarm","","","","");
        notificationHelper.getManager().notify(key.hashCode(), nb);
        ringtone = RingtoneManager.getRingtone(context, RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE));
        new CountDownTimer(30*1000, 1000){
            @Override
            public void onTick(long millisUntilFinished){
                if(sharedPreferences.getBoolean("ring "+key,false)) {
                    ringtone.play();
                }
                else{
                    ringtone.stop();
                    cancel();
                }
            }
            @Override
            public void onFinish() { ringtone.stop(); }
        }.start();
    }
}
package com.example.smartremindersapp2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class all_alarms extends AppCompatActivity {
    private ImageView addAlarmImage;
    private ArrayList<alarm_view> alarms_list = new ArrayList<>();
    private static RecyclerView mRecyclerView;
    private static ExampleAdapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    private DrawerLayout drawerLayout;
    private String userName;
    private AuxiliaryFunctions myAuxiliaryFunctions;
    private SharedPreferences msharedPreferences;
    private SharedPreferences.Editor editor;
    private int hour;
    private int minutes;
    private static TextView instruction;




    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_alarms);
        userName=getSharedPreferences("U",MODE_PRIVATE).
                getString("username",null);
        msharedPreferences=getSharedPreferences("U",MODE_PRIVATE);
        editor=msharedPreferences.edit();
        mRecyclerView = findViewById(R.id.recycleView);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);
        myAuxiliaryFunctions=AuxiliaryFunctions.getInstance();
        drawerLayout=findViewById(R.id.drawer_layout);
        instruction = findViewById(R.id.instructions_A);
        addAlarmImage = findViewById(R.id.imageView);

        // the try will succeed if the intent started by notification click
        // and it will be one of 2 option according to reminder's kind and
        // the clicked button
        try{

            // if dismiss button clicked, cancel the notification and
            // update the reminder's switch to unchecked
            if(getIntent().getStringExtra("type").equals("Dismiss")){
                String key=getIntent().getStringExtra("key");
                NotificationManager manager=(NotificationManager) getApplicationContext()
                        .getSystemService(NOTIFICATION_SERVICE);
                manager.cancelAll();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("reminder_list").child(key);
                ref.removeValue();
            }

            // if snooze button clicked, cancel the notification and
            // started new notification to ring after 1 minute,
            // and keep the switch check
            else if(getIntent().getStringExtra("type").equals("SNOOZE")) {
                editor.putBoolean("ring "+msharedPreferences.getString("Current Ring Key",null),false);
                editor.commit();
                String key = getIntent().getStringExtra("key");
                SaveInDatabase.UpdateAlarmData(userName,key,"checked",true);

                NotificationManager manager=(NotificationManager) getApplicationContext()
                        .getSystemService(NOTIFICATION_SERVICE);
                manager.cancelAll();
                Calendar date = Calendar.getInstance();
                long timeInSecs = date.getTimeInMillis();
                Date afterAdding1Mins = new Date(timeInSecs + ( 60 * 1000));
                Intent ServiceIntent=new Intent(this,NotifierAlarm.class);
                stopService(ServiceIntent);
                ServiceIntent.putExtra("date",afterAdding1Mins.getTime());
                ServiceIntent.putExtra("Pending_key",key.hashCode()+1);
                ServiceIntent.putExtra("title", getIntent().getStringExtra("title"));
                ServiceIntent.putExtra("key",key);
                this.startService(ServiceIntent);
            }

        }
        catch(Exception ex){
            System.out.println("in catch");
        }

        // click on the add alarm button
        addAlarmImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                myAuxiliaryFunctions.openNewPage(getApplicationContext(),alarm_clock.class);
            }
        });

        // AlertReceiver.getRingtone() will not be null if we have arrive
        // to all_alarms page from click on a notification
        if(AlertReceiver.getRingtone()!=null) {
            if (AlertReceiver.getRingtone().isPlaying()){
                DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("Alarms").child(msharedPreferences.getString("Current Ring Key",null));
                NotifierAlarm.setKillTimer();

                // update this alarm to ring=false in order to stop the ringing
                editor.putBoolean("ring "+msharedPreferences.getString("Current Ring Key",null),false);
                editor.commit();
                ref.child("days_date").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        // if its not a repeated alarm then turn off the switch
                        if(snapshot.getValue()==null) {
                            if(!getIntent().getStringExtra("type").equals("SNOOZE"))
                                SaveInDatabase.UpdateAlarmData(userName,msharedPreferences.getString("Current Ring Key",null),
                                        "checked",false);
                        }
                        // else, its a repeated alarm then set the alarm to this day next week
                        else{
                            DatabaseReference ref3=FirebaseDatabase.getInstance().getReference().child("Users")
                                    .child(userName).child("Alarms").
                                            child(msharedPreferences.getString("Current Ring Key",null));
                            ref3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    alarm_view alarm=snapshot.getValue(alarm_view.class);
                                    hour=alarm.getHour();
                                    minutes=alarm.getMinutes();
                                    setRepeatedAlarm(alarm.getDays_date().size()+1,msharedPreferences.getString("Current Ring Key",null),true);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {}
                            });
                        }
                        alarms_list = get_all_the_alarms_from_firebase(userName);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
            }
        }
        else
            alarms_list = get_all_the_alarms_from_firebase(userName);
    }


    //if there is at least one alarm then the instruction must be invisible
    // else visible
    public static void setInstruction(int v) {
        if(v==0)
            instruction.setVisibility(View.VISIBLE);
        else
            instruction.setVisibility(View.INVISIBLE);
    }





    public void ClickMenu(View view){ myAuxiliaryFunctions.openDrawer(drawerLayout);}
    public void ClickHome(View view){ myAuxiliaryFunctions.openNewPage(getApplicationContext(),HomePage.class);}
    public void ClickDashboard(View view){ myAuxiliaryFunctions.closeDrawer(drawerLayout);}
    public void ClickAboutUs(View view){ myAuxiliaryFunctions.openNewPage(getApplicationContext(),AboutUs.class);}
    public void ClickLogout(View view){ myAuxiliaryFunctions.logout(this,userName);}




    // function that get all the alarms from the database in order to display
    //then in the all_alarms page
    public ArrayList<alarm_view> get_all_the_alarms_from_firebase(String userName){
        ArrayList<alarm_view> alarms = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("Alarms");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    alarm_view alarm = ds.getValue(alarm_view.class);
                    alarm.setSwitch((Boolean) ds.child("checked").getValue());
                    alarms.add(alarm);
                }
                c(alarms, userName);
                setInstruction(alarms.size());
            }

            @Override
            public void onCancelled(DatabaseError error) {}
        });
        return alarms;
    }




    public void c(ArrayList<alarm_view> alarms, String userName) {
        mAdapter = new ExampleAdapter(this, alarms, userName);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ExampleAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {}
            @Override
            public void onDeleteClick(int position) {}
        });
    }

    // cancel the notificaion from the alarm manager
    void cancelAlarm(alarm_view alarm,Context context) {
        //subtract one from the number of running processes
        SharedPreferences sharedPreferences=context.getSharedPreferences("U",context.MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();

        //one alarm to delete from AlarmManager
        if(alarm.getDate()!=null){
            AlarmManager alarmManager
                    = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlertReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getKey().hashCode(), intent, 0);
            alarmManager.cancel(pendingIntent);
        }
        else{
            List<String> days=alarm.getDays_date();
            int i=0;
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
            Intent intent = new Intent(context, AlertReceiver.class);
            for (String day:days){
                PendingIntent pendingIntent = PendingIntent.getBroadcast(context, alarm.getKey().hashCode()+i, intent, 0);
                alarmManager.cancel(pendingIntent);
                i++;
            }
        }
    }

    // we call to this function by the on clicking  of the switch.
    // if this alarm setted to specific date then we call to
    // startNotifierAlarmIntent function to send the notification to the alarm
    // manager. else if this alarm setted to repeated every specific days
    // then we call to startNotifierAlarmIntent function for every selected day
    @RequiresApi(api = Build.VERSION_CODES.M)
     void startAlarm(alarm_view alarm,Context context){
        //add one alarm to the AlarmManager
        if(alarm.getDate()!=null)
            startNotifierAlarmIntent(context,alarm.getDate().getTime()
                    ,alarm.getKey(),false);
        else{
            int i=0;
            String key=alarm.getKey();
            List<String> daysList=alarm.getDays_date();
            if (daysList.contains("0"))  setRepeatedAlarm(i++,Calendar.SUNDAY,key,alarm,context);
            if (daysList.contains("1"))  setRepeatedAlarm(i++,Calendar.MONDAY,key,alarm,context);
            if (daysList.contains("2"))  setRepeatedAlarm(i++,Calendar.TUESDAY,key,alarm,context);
            if (daysList.contains("3"))  setRepeatedAlarm(i++,Calendar.WEDNESDAY,key,alarm,context);
            if (daysList.contains("4")) setRepeatedAlarm(i++,Calendar.THURSDAY,key,alarm,context);
            if (daysList.contains("5"))  setRepeatedAlarm(i++,Calendar.FRIDAY,key,alarm,context);
            if (daysList.contains("6"))  setRepeatedAlarm(i++,Calendar.SATURDAY,key,alarm,context);
        }
    }


    public void setRepeatedAlarm(int i,int day,String key,alarm_view alarm,Context context){
        Calendar NotificationDate=Calendar.getInstance();
        NotificationDate.set(Calendar.HOUR_OF_DAY, alarm.getHour());
        NotificationDate.set(Calendar.MINUTE, alarm.getMinutes());
        NotificationDate.set(Calendar.SECOND, 0);
        NotificationDate.set(Calendar.MILLISECOND, 0);
        NotificationDate.set(Calendar.DAY_OF_WEEK, day);
        Date date=NotificationDate.getTime();
        startNotifierAlarmIntent(context,date.getTime(),key,true);
    }

    // send a notification intent to the alarm manager by call to
    // NotifierAlarm intent
    public void startNotifierAlarmIntent(Context context,long date,
                                         String key,boolean repeat){
        Intent ServiceIntent=new Intent(context,NotifierAlarm.class);
        context.stopService(ServiceIntent);
        ServiceIntent.putExtra("date",date);
        ServiceIntent.putExtra("Pending_key",key.hashCode());
        ServiceIntent.putExtra("key",key);
        ServiceIntent.putExtra("Repeating",repeat);
        context.startService(ServiceIntent);
    }

    // send notification to alarm manager
    public void setRepeatedAlarm(int i,String key,boolean repeated) {
        Intent intent=new Intent(this,NotifierAlarm.class);
        stopService(intent);
        Calendar c = Calendar.getInstance();
        int offset = Calendar.SATURDAY;
        c.add(Calendar.DATE, offset);
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minutes);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        Date date=c.getTime();
        intent.putExtra("Pending_key",key.hashCode() + i);
        intent.putExtra("date",date.getTime());
        intent.putExtra("key",key);
        startService(intent);
    }
}
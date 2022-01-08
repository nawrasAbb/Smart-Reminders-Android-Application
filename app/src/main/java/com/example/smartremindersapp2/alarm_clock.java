package com.example.smartremindersapp2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import  androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class alarm_clock extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {
    private CheckBox SundayBox;
    private CheckBox MondayBox;
    private CheckBox TuesdayBox;
    private CheckBox WednesdayBox;
    private CheckBox ThursdayBox;
    private CheckBox FridayBox;
    private CheckBox SaturdayBox;
    private EditText AlarmName;
    private EditText AlarmDate;
    private ImageView CalendarButton;
    private Button scheduleButton;
    private Button CancelButton;
    private int hour;
    private int minutes;
    private String userName;
    private DatabaseReference ref;
    private TimePicker alarmTimePicker;
    private String daysList="Every ";
    private Calendar NotificationDate;
    private Date date;

    //initialize the fields in the alarm clock page and get the current time
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void InitializeData(){
        SundayBox=findViewById(R.id.Sunday_checkBox);
        MondayBox=findViewById(R.id.Monday_CheckBox);
        TuesdayBox=findViewById(R.id.Tuesday_Check_Box);
        WednesdayBox=findViewById(R.id.Wednesday_Check_box);
        ThursdayBox=findViewById(R.id.Thursday_Check_box);
        FridayBox=findViewById(R.id.Friday_Check_Box);
        SaturdayBox=findViewById(R.id.Saturday_Check_Box);
        AlarmName=findViewById(R.id.alarm_name);
        AlarmDate=findViewById(R.id.editTextDate);
        CalendarButton=findViewById(R.id.calendar_button);
        alarmTimePicker=findViewById(R.id.alarmTimePicker);
        CancelButton = findViewById(R.id.cancel_alarm);
        scheduleButton = findViewById(R.id.schedule_button);
        //initial a few variables
        userName=getSharedPreferences("U",MODE_PRIVATE).getString("username",null);
        NotificationDate=Calendar.getInstance();
        hour=alarmTimePicker.getHour();
        minutes=alarmTimePicker.getMinute();
        updateHourAndMinutesInCalendar();
        date=NotificationDate.getTime();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.FOREGROUND_SERVICE}, PackageManager.PERMISSION_GRANTED);
        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECEIVE_BOOT_COMPLETED}, PackageManager.PERMISSION_GRANTED);
        setContentView(R.layout.activity_alarm_clock);
        InitializeData();

        //check if this is an alarm to edit, if yes must initial the alarm_clock page
        if(getIntent().getStringExtra("Key")!=null){
            StartAlarmClockActivity(getIntent().getStringExtra("Key"));
        }

        //back to all_alarms page
        CancelButton.setOnClickListener(new View.OnClickListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // the update of the alarm picker
        alarmTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                String text;
                //update the new chooses hour and minutes
                hour=alarmTimePicker.getHour();
                minutes=alarmTimePicker.getMinute();
                updateHourAndMinutesInCalendar();
                if(date!=null) {
                    date.setHours(hour);
                    date.setMinutes(minutes);
                }
                //get the date of today and save in text
                text="Today-"+getDateAsString(NotificationDate.getTime());
                //if the chooses time is before the current time then get the date of tomorrow
                Calendar cal=Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, minutes);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);

                // if the selected time has elapsed then set the alarm for
                // tomorrow
                if (cal.before(Calendar.getInstance())){
                    cal.add(Calendar.DATE,1);
                    text="Tomorrow-"+getDateAsString(cal.getTime());
                    NotificationDate=(Calendar) cal.clone();
                }
                //set text if relevant
                if ((AlarmDate.getText().toString().isEmpty()) || (AlarmDate.getText().toString()
                        .split("-")[0].equals("Tomorrow")) || (AlarmDate.getText().toString()
                        .split("-")[0].equals("Today"))){
                    AlarmDate.setText(text);
                    date= NotificationDate.getTime();
                }
            }
        });



        SundayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){UpdateTextViewDays();}
        });
        MondayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){UpdateTextViewDays();}
        });
        TuesdayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){UpdateTextViewDays();}
        });
        WednesdayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){UpdateTextViewDays();}
        });
        ThursdayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){UpdateTextViewDays();}
        });
        FridayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){UpdateTextViewDays();}
        });
        SaturdayBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked){UpdateTextViewDays();}
        });

        // if the user select specific day by the calendat then
        // we save the chased date and unchecked all the boxes
        // and save the date on the textview
        CalendarButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Calendar newCalender = Calendar.getInstance();
                DatePickerDialog dialog = new DatePickerDialog(alarm_clock.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, final int year, final int month, final int dayOfMonth) {
                        //save the date in the calendar
                        NotificationDate = Calendar.getInstance();
                        updateHourAndMinutesInCalendar();
                        NotificationDate.set(Calendar.MONTH, month);
                        NotificationDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        date=NotificationDate.getTime();
                        UnCheckTheCheckBoxes();
                        //get the date and set in the alarm date text view
                        AlarmDate.setText(getDateAsString(NotificationDate.getTime()));
                    }
                }, newCalender.get(Calendar.YEAR), newCalender.get(Calendar.MONTH), newCalender.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.show();
            }
        });


        // save alarm button
        scheduleButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v){

                // if this is the state of edit alarm, then we remove the old one
                // and cancel the notification belongs to this alarm
                // and save the new one and send the new notication to the
                // alarm manager
                if(getIntent().getStringExtra("Key")!=null){
                    String old_key=getIntent().getStringExtra("Key");
                    //push the new alarm
                    DatabaseReference ref1= FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("Alarms");
                    DatabaseReference keyRef =ref1.push();
                    String new_key=keyRef.getKey();
                    List<String> checkedBoxes=check_boxes(SundayBox,MondayBox,TuesdayBox,WednesdayBox,ThursdayBox,FridayBox,SaturdayBox);
                    alarm_view new_alarm=new alarm_view(new_key,AlarmName.getText().toString(),hour
                            ,minutes,checkedBoxes,true,date);
                    ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("Alarms").child(old_key);
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            alarm_view old_alarm=snapshot.getValue(alarm_view.class);
                            if((boolean)(snapshot.child("checked").getValue())==true){
                                all_alarms AC=new all_alarms();
                                AC.cancelAlarm(old_alarm,getApplicationContext());
                            }
                            DatabaseReference ref1= FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("Alarms").child(old_key);
                            ref1.removeValue();
                            keyRef.setValue(new_alarm);
                            boolean status;
                            if(AlarmDate.getText().toString().split(" ")[0].equals("Every"))status=false;
                            else status=true;
                            startAlarm(new_key,status,AlarmName.getText().toString());
                            AuxiliaryFunctions.getInstance().openNewPage(getApplicationContext(),all_alarms.class);
                            HomePage.getmRecyclerView().setHasFixedSize(true);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {}
                    });
                }
                else{
                    List<String> checkedBoxes=check_boxes(SundayBox,MondayBox,TuesdayBox,WednesdayBox,ThursdayBox,FridayBox,SaturdayBox);
                    ref= FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("Alarms");
                    DatabaseReference keyRef =ref.push();
                    String key=keyRef.getKey();
                    alarm_view alarm=new alarm_view(key,AlarmName.getText().toString(),hour
                            ,minutes,checkedBoxes,true,date);
                    keyRef.setValue(alarm);
                    AuxiliaryFunctions.getInstance().openNewPage(getApplicationContext(),all_alarms.class);
                    boolean status;
                    if(AlarmDate.getText().toString().split(" ")[0].equals("Every"))status=false;
                    else status=true;
                    startAlarm(key,status,AlarmName.getText().toString());
                }
            }
        });
    }


    // return all the check boxes to be unchecked
    private void UnCheckTheCheckBoxes(){
        SundayBox.setChecked(false);
        MondayBox.setChecked(false);
        TuesdayBox.setChecked(false);
        WednesdayBox.setChecked(false);
        ThursdayBox.setChecked(false);
        FridayBox.setChecked(false);
        SaturdayBox.setChecked(false);
    }

    //update the time to the new time setted on the alarm picker
    private void updateHourAndMinutesInCalendar() {
        NotificationDate=Calendar.getInstance();
        NotificationDate.set(Calendar.HOUR_OF_DAY, hour);
        NotificationDate.set(Calendar.MINUTE, minutes);
        NotificationDate.set(Calendar.SECOND, 0);
        NotificationDate.set(Calendar.MILLISECOND, 0);
    }

    // if we want to edit the larm, this function inialize the page with the
    // old data
    public void StartAlarmClockActivity(String key){
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("Alarms").child(key);
        ref.addListenerForSingleValueEvent(new ValueEventListener(){
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                alarm_view currentAlarm = snapshot.getValue(alarm_view.class);
                HashMap map=new HashMap();
                map.put("status","1");
                AlarmName.setText(currentAlarm.getTitle());
                alarmTimePicker.setHour(currentAlarm.getHour());
                alarmTimePicker.setMinute(currentAlarm.getMinutes());
                if (currentAlarm.getDays_date()==null)
                    AlarmDate.setText(getDateAsString(currentAlarm.getDate()));
                else{
                    String DaysList="Every ";
                    for(String day:currentAlarm.getDays_date()) {
                        if (day.equals("0")){
                            DaysList=DaysList+"Sun, ";
                            SundayBox.setChecked(true);
                        }
                        if (day.equals("1")){
                            DaysList=DaysList+"Mon, ";
                            MondayBox.setChecked(true);
                        }
                        if (day.equals("2")){
                            DaysList=DaysList+"Tue, ";
                            TuesdayBox.setChecked(true);
                        }
                        if (day.equals("3")){
                            DaysList=DaysList+"Wed, ";
                            WednesdayBox.setChecked(true);
                        }
                        if (day.equals("4")){
                            DaysList=DaysList+"Thur, ";
                            ThursdayBox.setChecked(true);
                        }
                        if (day.equals("5")){
                            DaysList=DaysList+"Fri, ";
                            FridayBox.setChecked(true);
                        }
                        if (day.equals("6")){
                            DaysList=DaysList+"Sat, ";
                            SaturdayBox.setChecked(true);
                        }
                    }
                    AlarmDate.setText(DaysList);
                }
                ref.updateChildren(map);
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    // with all check/uncheck of day/box we call this function to update the
    // selected days text view
    @RequiresApi(api = Build.VERSION_CODES.M)
    private void UpdateTextViewDays(){
        date=null;
        daysList="Every ";
        if(SundayBox.isChecked()) daysList=daysList+"Sun, ";
        if(MondayBox.isChecked()) daysList=daysList+"Mon, ";
        if(TuesdayBox.isChecked()) daysList=daysList+"Tue, ";
        if(WednesdayBox.isChecked()) daysList=daysList+"Wed, ";
        if(ThursdayBox.isChecked()) daysList=daysList+"Thur, ";
        if(FridayBox.isChecked()) daysList=daysList+"Fri, ";
        if(SaturdayBox.isChecked()) daysList=daysList+"Sat, ";

        if(daysList.equals("Every ")){
            String text;
            //update the new chooses hour and minutes
            hour=alarmTimePicker.getHour();
            minutes=alarmTimePicker.getMinute();
            updateHourAndMinutesInCalendar();

            //get the date of today and save in text
            text="Today-"+getDateAsString(NotificationDate.getTime());

            //if the chooses time is before the current time then get the date of tomorrow
            Calendar cal=Calendar.getInstance();
            cal.set(Calendar.HOUR_OF_DAY, hour);
            cal.set(Calendar.MINUTE, minutes);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);
            if (cal.before(Calendar.getInstance())){
                cal.add(Calendar.DATE,1); //or add
                text="Tomorrow-"+getDateAsString(cal.getTime());
            }
            date= NotificationDate.getTime();
            AlarmDate.setText(text);
        }
        else AlarmDate.setText(daysList.substring(0,daysList.length()-2));
    }



    //when the user save the alarm we call to this function in order
    // to send the notification to the alarmManager
    @RequiresApi(api = Build.VERSION_CODES.M)
    public void startAlarm(String key,boolean status,String title) {
        int i = 0;
        Intent ServiceIntent=new Intent(this,NotifierAlarm.class);
        // the situation that we choose specific date
        if (status){
            stopService(ServiceIntent);
            ServiceIntent.putExtra("date",date.getTime());
            ServiceIntent.putExtra("Pending_key",key.hashCode());
            ServiceIntent.putExtra("title",title);
            ServiceIntent.putExtra("key",key);
            startService(ServiceIntent);
        }
        // the situation that the user choose repeated alarm every
        // specific day, and for every selected day we save a notification
        else {
            if (daysList.contains("Sun"))  setRepeatedAlarm(i++,Calendar.SUNDAY,key,title);
            if (daysList.contains("Mon"))  setRepeatedAlarm(i++,Calendar.MONDAY,key,title);
            if (daysList.contains("Tue"))  setRepeatedAlarm(i++,Calendar.TUESDAY,key,title);
            if (daysList.contains("Wed"))  setRepeatedAlarm(i++,Calendar.WEDNESDAY,key,title);
            if (daysList.contains("Thur")) setRepeatedAlarm(i++,Calendar.THURSDAY,key,title);
            if (daysList.contains("Fri"))  setRepeatedAlarm(i++,Calendar.FRIDAY,key,title);
            if (daysList.contains("Sat"))  setRepeatedAlarm(i++,Calendar.SATURDAY,key,title);
        }
    }


    // we call to this function for every selected checkbox/day
    //
    public void setRepeatedAlarm(int i,int day,String key,String title) {
        Intent intent=new Intent(this,NotifierAlarm.class);
        stopService(intent);
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY,hour);
        c.set(Calendar.MINUTE,minutes);
        c.set(Calendar.SECOND,0);
        c.set(Calendar.MILLISECOND,0);
        int today=c.get(Calendar.DAY_OF_WEEK);
        Date NextDate;

        // if the selected day box is the same as today
        if(day==today){

            // if the time has elapsed then set the notification to the same
            //day next week by adding offset 6 to the calendar
            if (c.before(Calendar.getInstance())){
                int offset = Calendar.SATURDAY;
                c.add(Calendar.DATE, offset);
                NextDate = c.getTime();
            }
            // else it notify today at the chased time
            else{
                c.set(Calendar.DAY_OF_WEEK, day);
                NextDate = c.getTime();
            }
        }

        // if the selected day at this week not yet passed then it notify at
        // the chased time as usual
        else if (day > today) {
            c.set(Calendar.DAY_OF_WEEK, day);
            NextDate = c.getTime();
        }
        //else we set the notification to notify at date of this day next week
        else {
            int offset = Calendar.SATURDAY - today + day;
            c.add(Calendar.DATE, offset);
            NextDate = c.getTime();
        }
        date=c.getTime();
        intent.putExtra("Pending_key",key.hashCode() + i);
        intent.putExtra("date",date.getTime());
        intent.putExtra("title",title);
        intent.putExtra("key",key);
        startService(intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {}


    // get Date object and return it as string
    public String getDateAsString(Date date){
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
        String[] splitDate = formattedDate.split(",");
        String date2=splitDate[0]+","+splitDate[1];
        return date2;
    }

    //check which days was selected
    public static List<String> check_boxes(CheckBox sundayBox, CheckBox mondayBox, CheckBox tuesdayBox, CheckBox wednesdayBox, CheckBox thursdayBox, CheckBox fridayBox, CheckBox saturdayBox) {
        List<String> checkedBoxes=new ArrayList<>();
        if (sundayBox.isChecked()){ checkedBoxes.add("0");}
        if (mondayBox.isChecked()){ checkedBoxes.add("1");}
        if (tuesdayBox.isChecked()){ checkedBoxes.add("2");}
        if (wednesdayBox.isChecked()){ checkedBoxes.add("3");}
        if (thursdayBox.isChecked()){ checkedBoxes.add("4");}
        if (fridayBox.isChecked()){ checkedBoxes.add("5");}
        if (saturdayBox.isChecked()){ checkedBoxes.add("6");}
        return checkedBoxes;
    }
}
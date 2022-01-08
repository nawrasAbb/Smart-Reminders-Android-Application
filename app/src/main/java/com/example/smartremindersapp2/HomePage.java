package com.example.smartremindersapp2;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class HomePage extends AppCompatActivity implements AdapterView.OnItemSelectedListener, SharedPreferences.OnSharedPreferenceChangeListener {
    private addReminder a;
    private MyBackgroundService mService;
    private static String userName;
    private DrawerLayout drawerLayout;
    private TextView Date1, hello_txt;
    private static TextView instruction;
    private FloatingActionButton add_button, pen_button, locate_button;
    private Boolean clicked = false;
    private boolean mBound = false;
    private Spinner RemindersKindSpinner;
    public ArrayList<Reminder> reminders_list_firebase = new ArrayList<>();
    private static RecyclerView mRecyclerView;
    private ReminderAdapter mAdapter;
    private static RecyclerView.LayoutManager mLayoutManager;
    private static HomePage instance;
    public static addReminder addRdialog;
    private ConstraintLayout background_layout;
    private ImageView menu_btn,setting_btn;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;
    private Spinner spinner;

    public Spinner getRemindersKindSpinner() {
        return RemindersKindSpinner;
    }
    public static RecyclerView getmRecyclerView() {
        return mRecyclerView;
    }


    // initialize the homepage fields and data
    private void InitializeData(){
        instance = this;
        drawerLayout = findViewById(R.id.drawer_layout);
        add_button = findViewById(R.id.btn_add);
        pen_button = findViewById(R.id.btn_pen);
        locate_button = findViewById(R.id.btn_locateReminder);
        spinner = findViewById(R.id.DateSpinner);
        RemindersKindSpinner = findViewById((R.id.KindSpinner));
        instruction = findViewById(R.id.instructions);
        hello_txt = findViewById(R.id.Hello);
        pen_button = findViewById(R.id.btn_pen);
        Date1 = findViewById(R.id.textView);
        background_layout=findViewById(R.id.background_layout);
        menu_btn=findViewById(R.id.MenuButton);
        setting_btn=findViewById(R.id.setting_btn);
        userName = getSharedPreferences("U", MODE_PRIVATE).getString("username", null);
        Places.initialize(getApplicationContext(), "AIzaSyCfsrOq62GRNdUvZeMBhimX4RFX9cpm4uU");

        //display all the reminders in the recycle view
        get_all_reminders_by_kind("all");
        mRecyclerView = findViewById(R.id.recycleViewR);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(this);

        //initialize the spinner and set it's position to "all" state
        ArrayAdapter KindsAdapter=ArrayAdapter.createFromResource(
                this,R.array.kinds,R.layout.color_spinner_layout
        );
        KindsAdapter.setDropDownViewResource(R.layout.spinner_dropdown_layout);
        RemindersKindSpinner.setAdapter(KindsAdapter);
        RemindersKindSpinner.setOnItemSelectedListener(this);
        spinner.setOnItemSelectedListener(this);
        int spinnerPosition = KindsAdapter.getPosition("all");
        RemindersKindSpinner.setSelection(spinnerPosition);

        // update the date text view
        Date currentDate = Calendar.getInstance().getTime();
        String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(currentDate);
        int index = formattedDate.indexOf(",");
        Date1.setText(formattedDate.substring(index + 1));
    }


    // update the image in the background according to the current time
    // and initialize the hello text view according to the time and the username
    // and set the current date in the appropriate field
    private void updateHomePageBackgroundImage(){
        String time = "Hello ";
        int btn_color =R.color.white,texts_color=R.color.white;
        int backgroungImage=0;
        Calendar calendar=Calendar.getInstance();
        if(calendar.getTime().getHours()>=6 &&
                calendar.getTime().getHours()<12) {
            backgroungImage= R.drawable.morning_image;
            time = "Good Morning, ";
        }
        if(calendar.getTime().getHours()>=12 &&
                calendar.getTime().getHours()<17) {
            btn_color=R.color.black;
            texts_color=R.color.white;
            backgroungImage= R.drawable.image3;
            time = "Good Afternoon, ";
        }
        if(calendar.getTime().getHours()>=17 &&
                calendar.getTime().getHours()<20) {
            btn_color=R.color.black;
            texts_color=R.color.white;
            backgroungImage= R.drawable.image4;
            time = "Good Evening, ";
        }
        if(calendar.getTime().getHours()>=20 ||
                calendar.getTime().getHours()<6) {
            backgroungImage= R.drawable.image5;
            time = "Good Night, ";
        }
        Date1.setTextColor(ContextCompat.getColor(getInstance(), texts_color));
        setting_btn.setColorFilter(ContextCompat.getColor(getInstance(), btn_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        menu_btn.setColorFilter(ContextCompat.getColor(getInstance(), btn_color), android.graphics.PorterDuff.Mode.MULTIPLY);
        hello_txt.setTextColor(ContextCompat.getColor(getInstance(), texts_color));
        background_layout.setBackground(ContextCompat.getDrawable(getInstance(), backgroungImage));
        hello_txt.setText(time+ userName+"!");
    }




    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_page);
        InitializeData();



        // the try will succeed if the intent started by notification click
        // and it will be one of 4 option according to reminder's kind and
        // the clicked button
        try{
            // if dismiss button clicked, cancel the notification and
            // update the reminder's state to false
            // that's mean that handling of this reminder has been discontinued
            if(getIntent().getStringExtra("type").equals("Dismiss")) {
                String key=getIntent().getStringExtra("key");
                NotificationManager manager=(NotificationManager) getApplicationContext()
                        .getSystemService(NOTIFICATION_SERVICE);
                manager.cancelAll();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("reminder_list").child(key);
                HashMap map=new HashMap();
                map.put("state",false);
                ref.updateChildren(map);
            }
            // if delete button clicked, cancel the notification and
            //  remove it from the database
            else if(getIntent().getStringExtra("type").equals("DELETE")) {
                String key=getIntent().getStringExtra("key");
                addReminder add_remind =new addReminder();
                NotificationManager manager=(NotificationManager) getApplicationContext()
                        .getSystemService(NOTIFICATION_SERVICE);
                manager.cancelAll();HashMap map=new HashMap();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("reminder_list").child(key);
                ref.removeValue();
            }

            // if snooze button clicked, cancel the notification and
            // started new notification to ring after 1 minute,
            // and keep the state true
            else if(getIntent().getStringExtra("type").equals("SNOOZE")) {
                String key = getIntent().getStringExtra("key");
                NotificationManager manager=(NotificationManager) getApplicationContext()
                        .getSystemService(NOTIFICATION_SERVICE);
                manager.cancelAll();
                addReminder add_remind = new addReminder();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("reminder_list");
                Calendar date = Calendar.getInstance();
                long timeInSecs = date.getTimeInMillis();
                Date afterAdding10Mins = new Date(timeInSecs + (10 * 60 * 100));
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Reminder oldView = new Reminder();
                        oldView = snapshot.getValue(Reminder.class);
                        Reminder new_reminder = new Reminder();
                        new_reminder.setRemindDate(afterAdding10Mins);
                        new_reminder.setDateState(false);
                        new_reminder.setKey(key);
                        new_reminder.setLAT(oldView.getLAT());
                        new_reminder.setLNG(oldView.getLNG());
                        new_reminder.setState(true);
                        new_reminder.setLocationAsString(oldView.getLocationAsString());
                        new_reminder.setTitle(oldView.getTitle());
                        new_reminder.setDescription(oldView.getDescription());
                        add_remind.sendToAlarmManager(new_reminder, false);

                        HashMap map2 = new HashMap();
                        map2.put("remindDate", afterAdding10Mins);
                        map2.put("state", true);
                        map2.put("dateState", false);
                        ref.child(key).updateChildren(map2);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
            // if choose another button clicked, cancel the notification and
            // looking for the next appropriate place to offer
            else if(getIntent().getStringExtra("type").equals("Another")) {
                String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + "32.7614296" + "," + "35.0195184" + "&" +
                       "rankby=distance" + "&" + "type="+"some type" +"&"+ "key=" + "AIzaSyDMU9eVBmHymFvymjsCO3pUCBwwGMTqV5w";
                String title=getIntent().getStringExtra("title");
                String content=getIntent().getStringExtra("content");
                String category=getIntent().getStringExtra("category");
                String lat1=getIntent().getStringExtra("lat1");
                String lang1=getIntent().getStringExtra("lang1");
                NotificationManager manager=(NotificationManager) getApplicationContext()
                        .getSystemService(NOTIFICATION_SERVICE);
                manager.cancelAll();
                GooglePlacesClient.do1(getIntent().getStringExtra("cadHTTP")
                        ,lat1,lang1,
                        title,content
                        ,getIntent().getStringExtra("key"),getInstance(),category);
            }
        }catch (Exception i){
            System.out.println("im in catch");
        }

        //update the background
        updateHomePageBackgroundImage();

        PreferenceManager.getDefaultSharedPreferences(HomePage.this).registerOnSharedPreferenceChangeListener(this);
        Dexter.withActivity(HomePage.this).withPermissions(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                PreferenceManager.getDefaultSharedPreferences(HomePage.this).registerOnSharedPreferenceChangeListener(HomePage.this);
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
            }
        }).check();

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("my_channel_01", "hello", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Channel for reminders");
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }


        add_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                setVisibility(clicked);
                if (!clicked) clicked = true;
                else clicked = false;
            }
        });

        // update the reminders in the recycle view according to the chased
        // option in the spinner
        RemindersKindSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view,
                                       int position, long id){
                Object item = adapterView.getItemAtPosition(position);
                String stringItem = item.toString();
                get_all_reminders_by_kind(stringItem);
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });


        pen_button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                addTodoList a=new addTodoList(userName);
                a.openDialog(false,null,0);
            }
        });
        locate_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                a=new addReminder(userName);
                a.openDialog(false,null,0);
                addRdialog=a;
            }
        });
    }


    @Override
    protected void onStop() {
        if(mBound) {
            unbindService(mServiceConnection);
            mBound=false;
        }
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(this);
        super.onStop();
    }

    @Override
    protected void onStart() {
        super.onStart();
        PreferenceManager.getDefaultSharedPreferences(HomePage.this).registerOnSharedPreferenceChangeListener(this);
    }

    // get the current lat and lang and update in the database
    public void backgroundLocation(Location location) {
        double lat_current_d = location.getLatitude();
        double lang_current_d = location.getLongitude();
        String lat_current = String.valueOf(lat_current_d);
        String lang_current = String.valueOf(lang_current_d);
        DatabaseReference ref11 = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userName);
        HashMap hashmap = new HashMap();
        hashmap.put("lat", lat_current_d);
        hashmap.put("lang", lang_current_d);
        ref11.updateChildren(hashmap);
        ArrayList<Reminder> reminders_locationList = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userName).child("reminder_list");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Reminder reminder = ds.getValue(Reminder.class);
                    if (reminder.isDateState() && reminder.isState()) {
                        reminders_locationList.add(reminder);
                    }
                }

                for (Reminder reminder : reminders_locationList) {
                    String message = reminder.getTitle();
                    String type = reminder.getLocationAsString();
                    String description = reminder.getDescription();
                    String key = reminder.getKey();
                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(userName).child("reminder_list").child(key);
                    HashMap map = new HashMap();
                    map.put("state",false);

                    // if the user choose the person option' then we all the time
                    // check if the user is near the desired user,
                    // if yes we send a notification
                    if (type.equals("Person")) {
                        String name1=reminder.getPerson().getName();
                        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child(name1);
                        ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                System.out.println(name1);
                                double lat_reminder = snapshot.child("lat").getValue(Double.class);
                                double lang_reminder =snapshot.child("lang").getValue(Double.class);
                                float[] distance = new float[1];
                                Location.distanceBetween(lat_reminder, lang_reminder, lat_current_d, lang_current_d, distance);
                                if (distance[0] < 500) {
                                    ref.updateChildren(map);
                                    NotificationHelper notificationHelper = new NotificationHelper(HomePage.this);
                                    Notification nb = notificationHelper.getChannelNotification(key
                                            , HomePage.class, message+" : You are close to "+ name1, description, "","","","","","");
                                    notificationHelper.getManager().notify(key.hashCode(), nb);
                                }
                            }
                            @Override
                            public void onCancelled(DatabaseError error) {}
                        });
                    }

                    // if this is a "other" notification then
                    // send notification when the distance less than 500 meters
                    else if (type.equals("Other")) {
                        double lat_reminder = reminder.getLAT();
                        double lang_reminder = reminder.getLNG();
                        float[] distance = new float[1];
                        Location.distanceBetween(lat_reminder, lang_reminder, lat_current_d, lang_current_d, distance);

                        if (distance[0] < 500) {
                            ref.updateChildren(map);
                            NotificationHelper notificationHelper = new NotificationHelper(HomePage.this);
                            Notification nb = notificationHelper.getChannelNotification(key
                                    , HomePage.class, message+" : you are close to wanted location", description, "","","","","","");
                            notificationHelper.getManager().notify(key.hashCode(), nb);
                        }
                    }
                    // if this is a specific category then we notify when we
                    // become far from the nearest destination less than
                    // 2000 meters
                    else {
                        ref.updateChildren(map);
                        type=type.toLowerCase();
                        String url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?" + "location=" + lat_current + "," + lang_current + "&" +
                                "rankby=distance" + "&" + "type=" + type + "&" + "key=" + "AIzaSyDMU9eVBmHymFvymjsCO3pUCBwwGMTqV5w";

                        new GooglePlacesClient().getResponseThread(url,
                                lat_current, lang_current, message,
                                description, key, HomePage.this,type);
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });


    }

    // display the relevant reminders according to the chased kind in
    // the spinner
    public void get_all_reminders_by_kind(String ReminderType) {
        ArrayList<Reminder> reminders_locationList = new ArrayList<>();
        ArrayList<Reminder> reminders_views_list = new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users")
                .child(userName).child("reminder_list");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Reminder reminder = ds.getValue(Reminder.class);
                    if(ReminderType.equals("all")){
                        reminders_locationList.add(reminder);
                    }
                    else {
                        if (reminder.getMyType().contains(ReminderType))
                            reminders_locationList.add(reminder);
                    }
                    Dexter.withActivity(HomePage.this).withPermissions(Arrays.asList(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_BACKGROUND_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)).withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {

                            bindService(new Intent(HomePage.this, MyBackgroundService.class), mServiceConnection, Context.BIND_AUTO_CREATE);
                        }
                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {}
                    }).check();
                }

                for (Reminder reminder : reminders_locationList) {
                    String message = reminder.getTitle();
                    String type = reminder.getMyType();
                    String description = reminder.getDescription();
                    String key = reminder.getKey();
                    Date date=reminder.getRemindDate();
                    String location=reminder.getLocationAsString();
                    Reminder remindView = new Reminder(key,message, description,type,date,location,reminder.getLocation(),reminder.getAudios(),reminder.getPerson());
                    if (type.contains("Location")) {
                        remindView.setLAT(reminder.getLAT());
                        remindView.setLNG(reminder.getLNG());
                        remindView.setLocationAsString(reminder.getLocationAsString());
                    }
                    else{
                        remindView.setRemindDate(reminder.getRemindDate());
                    }

                    reminders_views_list.add(remindView);
                }
                c(reminders_views_list);
                setInstruction(reminders_views_list.size());
            }
            @Override
            public void onCancelled(DatabaseError error) {}
        });
    }

    private final ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            MyBackgroundService.LocalBinder binder = (MyBackgroundService.LocalBinder) iBinder;
            mService = binder.getService();
            mService.requestLocationUpdates();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            mBound = false;
        }
    };

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {}


    public void c(ArrayList<Reminder> reminderss) {
        mAdapter = new ReminderAdapter(getApplicationContext(), reminderss, userName);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setOnItemClickListener(new ReminderAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) { }
            @Override
            public void onDeleteClick(int position) {
                removeItem(position);
            }
        });
    }

    // remove from the recycle voew
    public void removeItem(int position) {
        reminders_list_firebase.remove(position);
        mAdapter.notifyItemRemoved(position);
    }

    //show or hide instructions according to the number of the reminders
    // the chosed kind in the spinner
    public static void setInstruction(int v) {
        if(v==0)
            instruction.setVisibility(View.VISIBLE);
        else
            instruction.setVisibility(View.INVISIBLE);
    }

    // show or hide the pen and location buttons
    private void setVisibility(Boolean clicked) {
        if (!clicked) {
            pen_button.setVisibility(View.VISIBLE);
            locate_button.setVisibility(View.VISIBLE);
        } else {
            pen_button.setVisibility(View.INVISIBLE);
            locate_button.setVisibility(View.INVISIBLE);
        }
    }


    public void ClickMenu(View view) {
        AuxiliaryFunctions.openDrawer(drawerLayout);
    }
    public void ClickHome(View view) {}
    public void ClickDashboard(View view) {
        AuxiliaryFunctions.redirectActivity(this, all_alarms.class, userName);}
    public void ClickAboutUs(View view) {
        AuxiliaryFunctions.redirectActivity(this, AboutUs.class, userName);
    }
    public void ClickLogout(View view) {
        AuxiliaryFunctions.logout(this,userName);
    }
    @Override
    protected void onPause() {
        super.onPause();
        AuxiliaryFunctions.closeDrawer(drawerLayout);
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String text = parent.getItemAtPosition(position).toString();
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {}
    public static HomePage getInstance() {
        return instance;
    }

    // permissions function
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((requestCode==100)&&(resultCode==RESULT_OK))
        {
            Place place = Autocomplete.getPlaceFromIntent(data);
            sharedPreferences=getSharedPreferences("U",MODE_PRIVATE);
            editor=sharedPreferences.edit();
            editor.putFloat("lat", (float) place.getLatLng().latitude);
            editor.putFloat("lng", (float) place.getLatLng().longitude);
            editor.putString("location",  place.getName());
            editor.commit();
            addReminder.wantedLocation=place;
            a.LocationTextView.setVisibility(View.VISIBLE);
            a.LocationTextView.setText(place.getAddress());
            a.address=place.getAddress();
            a.lat=place.getLatLng().latitude;
            a.lang=place.getLatLng().longitude;
        }
        else if (resultCode== AutocompleteActivity.RESULT_ERROR) {
            Status status =Autocomplete.getStatusFromIntent(data);
            Toast.makeText(getApplicationContext(),status.getStatusMessage(),Toast.LENGTH_SHORT).show();;
        }
    }
}

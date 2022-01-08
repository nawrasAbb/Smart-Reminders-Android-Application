package com.example.smartremindersapp2;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import static android.content.Context.NOTIFICATION_SERVICE;


// class that handle the views of the alarms in the RecycleView
public class ExampleAdapter extends RecyclerView.Adapter<ExampleAdapter.alarmViewHolder> {
    private static ArrayList<alarm_view> mAlram_view_list;
    private String username;
    private Context mcontext;
    private OnItemClickListener mlistener;


    public interface OnItemClickListener {
        void onItemClick(int position);

        void onDeleteClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    // class that refers to each alarm view
    public class alarmViewHolder extends RecyclerView.ViewHolder {
        public TextView mhour;
        public TextView mminutes;
        public Switch mSwitch;
        public TextView mdays_date;
        public TextView alarmName;
        public ImageView DeleteB;

        public alarmViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mhour = itemView.findViewById(R.id.ReminderTitle);
            mminutes = itemView.findViewById(R.id.minutes);
            mdays_date = itemView.findViewById(R.id.date_days);
            mSwitch = itemView.findViewById(R.id.Switch);
            DeleteB = itemView.findViewById(R.id.DeleteButton);
            alarmName = itemView.findViewById(R.id.alarm_title);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });
            DeleteB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onDeleteClick(position);
                        }
                    }
                }
            });
        }
    }


    public ExampleAdapter(Context context, ArrayList<alarm_view> alarm_view_list, String username) {
        mAlram_view_list = alarm_view_list;
        this.username = username;
        mcontext = context;
    }

    @Override
    public alarmViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vi = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_alarm_view, parent, false);
        alarmViewHolder evh = new alarmViewHolder(vi, mlistener);
        return evh;
    }

    public String getDateAsString(alarm_view alarm) {
        List<String> days = alarm.getDays_date();
        if (days == null) {
            days = new ArrayList<>();
        }
        Date date;
        String text = "";
        if (days.isEmpty()) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(alarm.getDate());
            if (cal.before(Calendar.getInstance())) {
                cal.add(Calendar.DATE, 1);
                date = cal.getTime();
                String key = alarm.getKey();
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(username)
                        .child("Alarms").child(key);
                HashMap map = new HashMap();
                map.put("date", date);
                ref.updateChildren(map);
            } else
                date = alarm.getDate();
            String formattedDate = DateFormat.getDateInstance(DateFormat.FULL).format(date);
            String[] splitDate = formattedDate.split(",");
            String NextDate = splitDate[0] + "," + splitDate[1];
            text = NextDate;
        } else {
            if (days.contains("0")) text = text + "<u>" + "<b> " + "S" + "</b>" + "</u>" + "  ";
            else text = text + " S  ";
            if (days.contains("1")) text = text + "<u>" + "<b>" + "M" + "</b>" + "</u>" + "  ";
            else text = text + " M  ";
            if (days.contains("2")) text = text + "<u>" + "<b>" + "T" + "</b>" + "</u>" + "  ";
            else text = text + " T  ";
            if (days.contains("3")) text = text + "<u>" + "<b>" + "W" + "</b>" + "</u>" + "  ";
            else text = text + " W  ";
            if (days.contains("4")) text = text + "<u>" + "<b>" + "T" + "</b>" + "</u>" + "  ";
            else text = text + " T  ";
            if (days.contains("5")) text = text + "<u>" + "<b>" + "F" + "</b>" + "</u>" + "  ";
            else text = text + " F  ";
            if (days.contains("6")) text = text + "<u>" + "<b>" + "S" + "</b>" + "</u>" + "  ";
            else text = text + " S  ";
        }
        return text;
    }


    @Override
    public void onBindViewHolder(alarmViewHolder holder, int position) {
        int[][] states = new int[][]{
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_checked},
        };

        int[] thumbColors = new int[]{
                Color.LTGRAY,
                Color.GREEN,
        };
        @SuppressLint("ResourceAsColor")
        int[] trackColors = new int[]{
                Color.LTGRAY,
                Color.GREEN,
        };
        alarm_view currentAlarm = mAlram_view_list.get(position);
        holder.mdays_date.setText(Html.fromHtml(getDateAsString(currentAlarm)));
        holder.alarmName.setText(currentAlarm.getTitle());
        if (Integer.toString(currentAlarm.getHour()).length() == 1)
            holder.mhour.setText("0" + Integer.toString(currentAlarm.getHour()));
        else
            holder.mhour.setText(Integer.toString(currentAlarm.getHour()));

        if (Integer.toString(currentAlarm.getMinutes()).length() == 1)
            holder.mminutes.setText("0" + Integer.toString(currentAlarm.getMinutes()));
        else
            holder.mminutes.setText(Integer.toString(currentAlarm.getMinutes()));

        if (currentAlarm.isChecked() == true)
            holder.mSwitch.setChecked(true);

        else
            holder.mSwitch.setChecked(false);

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("Alarms").child(currentAlarm.getKey());
        DatabaseReference ref1 = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("Alarms").child(currentAlarm.getKey()).child("checked");

        // if we remove the alarm then if the switch is on then cancel the
        // notification from the alarm manager
        // and remove the alarm from the database
        holder.DeleteB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((boolean) (dataSnapshot.getValue()) == true) {
                            all_alarms AC = new all_alarms();
                            AC.cancelAlarm(currentAlarm, mcontext);
                        }
                        NotificationManager manager = (NotificationManager) mcontext.getApplicationContext()
                                .getSystemService(NOTIFICATION_SERVICE);
                        manager.cancel(currentAlarm.getKey().hashCode());
                        ref.removeValue();
                        mAlram_view_list.remove(position);
                        if (mAlram_view_list.isEmpty())
                            all_alarms.setInstruction(0);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });

        //when the user change the switch then cancel the notification if
        //the switch has been checked and start a new notification if he switch
        //has been unchecked
        holder.mSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = mcontext.getSharedPreferences("U", mcontext.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                ref1.addListenerForSingleValueEvent(new ValueEventListener() {
                    @RequiresApi(api = Build.VERSION_CODES.M)
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if ((boolean) (dataSnapshot.getValue()) == true) {
                            currentAlarm.setSwitch(false);
                            ref1.setValue(false);
                            all_alarms AC = new all_alarms();
                            DrawableCompat.setTintList(DrawableCompat.wrap(holder.mSwitch.getThumbDrawable()), new ColorStateList(states, thumbColors));
                            DrawableCompat.setTintList(DrawableCompat.wrap(holder.mSwitch.getTrackDrawable()), new ColorStateList(states, trackColors));
                            AC.cancelAlarm(currentAlarm, mcontext);
                        } else {
                            all_alarms AC = new all_alarms();
                            AC.startAlarm(currentAlarm, mcontext);
                            currentAlarm.setSwitch(true);
                            ref1.setValue(true);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
            }
        });
    }


    @Override
    public int getItemCount() {
        return mAlram_view_list.size();
    }
}
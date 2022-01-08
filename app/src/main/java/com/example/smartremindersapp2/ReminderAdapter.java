package com.example.smartremindersapp2;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import static android.content.Context.*;

//class that handle the views of the reminders in the recycleview in the homepage
public class ReminderAdapter extends RecyclerView.Adapter<ReminderAdapter.ramindViewHolder>{
    private static ArrayList<Reminder> mRemind_view_list;
    private String username;
    private Context mcontext;
    private OnItemClickListener mlistener;

    public interface OnItemClickListener {
        void onItemClick(int position);
        void onDeleteClick(int position);
    }

    public static ArrayList<Reminder> getmRemind_view_list() {
        return mRemind_view_list;
    }

    public static void setmRemind_view_list(ArrayList<Reminder> mRemind_view_list) {
        ReminderAdapter.mRemind_view_list = mRemind_view_list;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mlistener = listener;
    }

    // class that refers to each reminder
    public class ramindViewHolder extends RecyclerView.ViewHolder {
        public TextView mtitle;
        public ImageView DeleteB;
        public TextView date,location;

        public ramindViewHolder(View itemView, final OnItemClickListener listener) {
            super(itemView);
            mtitle = itemView.findViewById(R.id.ReminderTitle);
            date = itemView.findViewById(R.id.time_RV);
            location = itemView.findViewById(R.id.location_RV);
            DeleteB = itemView.findViewById(R.id.DeleteButton);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null)  {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                            Reminder currentReminder=mRemind_view_list.get(position);

                            if(currentReminder.getMyType().equals("Todo List")) {
                                addTodoList a=new addTodoList(username);
                                a.openDialog(true,currentReminder,position);
                            }
                            else{
                                addReminder a=new addReminder(username);
                                a.openDialog(true,currentReminder,position);
                            }
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


    public ReminderAdapter(Context context, ArrayList<Reminder> alarm_view_list, String username) {
        mRemind_view_list = alarm_view_list;
        this.username = username;
        mcontext = context;
    }

    @Override
    public ramindViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View vi = LayoutInflater.from(parent.getContext()).inflate(R.layout.reminder_item2, parent, false);
        ramindViewHolder evh = new ramindViewHolder(vi, mlistener);
        return evh;
    }


    @Override
    public void onBindViewHolder(ramindViewHolder holder, int position) {
        Reminder currentReminer = mRemind_view_list.get(position);
        holder.mtitle.setText((currentReminer.getTitle()));
        if(currentReminer.getRemindDate()!=null) {
            String hour,minutes;
            Date date2=currentReminer.getRemindDate();
            if (Integer.toString(date2.getHours()).length() == 1)
                hour = "0" + Integer.toString(date2.getHours());
            else
                hour = Integer.toString(date2.getHours());

            if (Integer.toString(date2.getMinutes()).length() == 1)
                minutes = "0" + Integer.toString(date2.getMinutes());
            else
                minutes = Integer.toString(date2.getMinutes());
            String formattedDate = new SimpleDateFormat("dd MM yyyy").format(date2);
            formattedDate = formattedDate + " - " + hour + ":" + minutes;
            holder.date.setText(formattedDate);
        }

        //if its a location reminder then set the text in the location textview according to the
        // category. (person, specific location, category)
        if(currentReminer.getLocationAsString()!=null){
            if(currentReminer.getLocationAsString().equals("Person"))
                holder.location.setText(currentReminer.getPerson().getEmail());
            else if(currentReminer.getLocationAsString().equals("Other"))
                holder.location.setText(currentReminer.getLocation());
            else
                holder.location.setText(currentReminer.getLocationAsString());
        }
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(username).child("reminder_list").child(currentReminer.getKey());

        //delete the reminder and cancel notification
        holder.DeleteB.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {
                if(currentReminer.getRemindDate()!=null){
                    addReminder cancelR=new addReminder();
                    cancelR.cancelNotification(currentReminer.getKey().hashCode(),mcontext);
                }
                ref.removeValue();
                mRemind_view_list.remove(position);
                if(mRemind_view_list.isEmpty())
                    HomePage.setInstruction(0);
                notifyDataSetChanged();
                NotificationManager manager=(NotificationManager) mcontext.getApplicationContext()
                        .getSystemService(NOTIFICATION_SERVICE);
                manager.cancel(currentReminer.getKey().hashCode());
            }
        });
    }


    @Override
    public int getItemCount() {
        return mRemind_view_list.size();
    }
}












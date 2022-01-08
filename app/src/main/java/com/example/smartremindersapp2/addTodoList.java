package com.example.smartremindersapp2;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class addTodoList extends AppCompatActivity {
    private String UserName;
    private Dialog todoList_dialog;
    private EditText AddDescriptionEditText;
    private TextView title;
    private Button add_btn;
    private ImageButton cancel_btn;
    private FloatingActionButton mRecordBtn;
    private Chronometer simpleChronometer;
    private List<String> all_audios_list;
    private MediaPlayer mediaPlayer= new MediaPlayer();
    private SeekBar seekBar2;
    private boolean play=false;
    private Handler handler=new Handler();
    private MediaRecorder mediaRecorder;
    private String PathName="";
    private StorageReference mStorage= FirebaseStorage.getInstance().getReference();
    private DatabaseReference keyRef;
    final int REQUEST_PERMISSION_CODE=1000;


    public addTodoList(String userName) { UserName=userName; }

    public void openDialog(boolean edit,Reminder oldReminder,int position) {
        all_audios_list=new ArrayList<>();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Users").child(UserName).child("reminder_list");
        keyRef =ref.push();
        todoList_dialog=new Dialog(HomePage.getInstance());
        todoList_dialog.setContentView(R.layout.todo_list);
        simpleChronometer = todoList_dialog.findViewById(R.id.simpleChronometer2);
        mRecordBtn = todoList_dialog.findViewById(R.id.recordBtn2);
        add_btn = todoList_dialog.findViewById(R.id.addButton);
        cancel_btn = todoList_dialog.findViewById(R.id.cancel_Btn);
        title = todoList_dialog.findViewById(R.id.Title);
        AddDescriptionEditText=todoList_dialog.findViewById(R.id.descriptionTextView);

        if(edit){
            all_audios_list=oldReminder.getAudios();
            title.setText(oldReminder.getTitle());
            AddDescriptionEditText.setText(oldReminder.getDescription());
            if(oldReminder.getAudios()!=null){
                for(int i=0;i<all_audios_list.size();i++){
                    LinearLayout audios=todoList_dialog.findViewById(R.id.kinear_all_audios);
                    LayoutInflater inflater=LayoutInflater.from(todoList_dialog.getContext());
                    View view= inflater.inflate(R.layout.one_audio,audios,false);
                    ImageView playAudio=view.findViewById(R.id.playAudio);
                    ImageView deleteAudio=view.findViewById(R.id.delete_audio);
                    SeekBar seekBar=view.findViewById(R.id.seekBar);
                    view.setTag(all_audios_list.get(i));
                    audios.addView(view);
                    seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                            if(fromUser) mediaPlayer.seekTo(progress);
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {}
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {/*mediaPlayer.seekTo(seekBar.getProgress());*/}
                    });
                    deleteAudio.setOnClickListener(new View.OnClickListener(){
                        @Override
                        public void onClick(View v){
                            LinearLayout audios=todoList_dialog.findViewById(R.id.kinear_all_audios);
                            audios.removeView((LinearLayout)v.getParent());
                            all_audios_list.remove((String)((View)v.getParent()).getTag());
                        }
                    });
                    playAudio.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v){
                            seekBar2=seekBar;
                            if (play)
                                play = false;
                            else
                                play = true;
                            if (play) {
                                mediaPlayer = new MediaPlayer();
                                try {
                                    mediaPlayer.setDataSource((String)((View)v.getParent()).getTag());
                                    mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                        @Override
                                        public void onPrepared(MediaPlayer mp) {
                                            mp.start();
                                        }
                                    });
                                    mediaPlayer.prepare();

                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                seekBar.setMax(mediaPlayer.getDuration());
                                UpdateSeekBar updateSeekBar = new UpdateSeekBar();
                                handler.post(updateSeekBar);
                            } else {
                                if (mediaPlayer != null) {
                                    mediaPlayer.pause();
                                }
                            }
                            mediaPlayer.seekTo(seekBar.getProgress());
                        }
                    });
                }
            }
        }

        // long click on the recording button
        mRecordBtn.setOnTouchListener(new View.OnTouchListener() {
            @RequiresApi(api = Build.VERSION_CODES.P)
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                //start recording
                if(event.getAction() == MotionEvent.ACTION_DOWN){
                    if (checkPermissionFromDevice()) {
                        PathName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" +
                                UUID.randomUUID().toString() + "audio_record.3gp";
                        setupMediaRecorder();
                        try {
                            mediaRecorder.prepare();
                            mediaRecorder.start();
                            simpleChronometer.start();
                        }catch (IOException e) {
                            e.printStackTrace();
                        }
                        Toast.makeText(HomePage.getInstance(), "Recording ...", Toast.LENGTH_SHORT).show();
                    } else
                        requestPermission();
                }
                //stop recording
                else if(event.getAction() == MotionEvent.ACTION_UP){
                    simpleChronometer.stop();
                    simpleChronometer.setBase(SystemClock.elapsedRealtime());
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder=null;
                    addToScrollView();
                }
                return false;
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                todoList_dialog.cancel();
            }
        });

        // initialize the reminder object by set all the fielda and save in the database
        // and update the recycleview in the homepage
        add_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit){
                    ReminderAdapter.getmRemind_view_list().remove(position);
                    DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users").
                            child(UserName).child("reminder_list").child(oldReminder.getKey());
                    ref.removeValue();
                }
                Reminder reminder=new Reminder();
                reminder.setKey(keyRef.getKey());
                reminder.setMyType("Todo List");
                reminder.setState(true);
                reminder.setAudios(all_audios_list);
                if (all_audios_list != null)
                    uploadAudios();
                if(title.getText().toString().isEmpty())
                    reminder.setTitle("Todo list");
                else
                    reminder.setTitle(title.getText().toString().trim());
                reminder.setDescription(AddDescriptionEditText.getText().toString());
                keyRef.setValue(reminder);
                ArrayAdapter<CharSequence> KindsAdapter = ArrayAdapter.createFromResource(HomePage.getInstance(), R.array.kinds, android.R.layout.simple_spinner_item);
                KindsAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                int spinnerPosition = KindsAdapter.getPosition("all");
                HomePage.getInstance().getRemindersKindSpinner().setSelection(spinnerPosition);
                HomePage.getInstance().get_all_reminders_by_kind("all");
                HomePage.getInstance().getmRecyclerView().setHasFixedSize(true);
                todoList_dialog.cancel();
            }
        });

        todoList_dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        todoList_dialog.show();
    }

    // each audio saved in specific url
    private void uploadAudios(){
        for(int i=0;i<all_audios_list.size();i++) {
            final ProgressDialog pd = new ProgressDialog(HomePage.getInstance());
            pd.setMessage("Uploading Audio ...");
            pd.show();
            StorageReference filePath = mStorage.child("Users").child(UserName).child("Audio")
                    .child(keyRef.getKey())
                    .child(System.currentTimeMillis() + "_" + "new_audio.3gp");
            Uri uri = Uri.fromFile(new File(all_audios_list.get(i)));
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    pd.dismiss();
                }
            });
        }
    }

    //add the audio to the list and declare his items and add it to the scrollview
    private void addToScrollView(){
        all_audios_list.add(PathName);
        LinearLayout audios=todoList_dialog.findViewById(R.id.kinear_all_audios);
        LayoutInflater inflater=LayoutInflater.from(todoList_dialog.getContext());
        View view= inflater.inflate(R.layout.one_audio,audios,false);
        ImageView playAudio=view.findViewById(R.id.playAudio);
        ImageView deleteAudio=view.findViewById(R.id.delete_audio);
        SeekBar seekBar=view.findViewById(R.id.seekBar);
        view.setTag(PathName);
        audios.addView(view);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar2, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);
                }
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {/*mediaPlayer.seekTo(seekBar.getProgress());*/}
        });

        // remove audio causes to remove it from the list and from the scrollview
        deleteAudio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                LinearLayout audios=todoList_dialog.findViewById(R.id.kinear_all_audios);
                audios.removeView((LinearLayout)v.getParent());
                all_audios_list.remove((String)((View)v.getParent()).getTag());
            }
        });

        // button clicked in order to play/pause the recorded audio
        playAudio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                seekBar2=seekBar;
                if (play)
                    play = false;
                else
                    play = true;
                if (play) {
                    mediaPlayer = new MediaPlayer();
                    try {
                        mediaPlayer.setDataSource((String)((View)v.getParent()).getTag());
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                mp.start();
                            }
                        });
                        mediaPlayer.prepare();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    seekBar.setMax(mediaPlayer.getDuration());
                    UpdateSeekBar updateSeekBar = new UpdateSeekBar();
                    handler.post(updateSeekBar);
                } else {
                    if(mediaPlayer != null) {
                        mediaPlayer.pause();
                    }
                }
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
    }

    // update the position of the seekbar as it progresses
    public class UpdateSeekBar implements Runnable{
        @Override
        public void run() {
            seekBar2.setProgress(mediaPlayer.getCurrentPosition());
            handler.postDelayed(this,100);
            if(mediaPlayer.getCurrentPosition()==mediaPlayer.getDuration()) {
                seekBar2.setProgress(0);
                play=true;
            }
        }
    }

    // request permission to allow record if this has not been approved until now
    private void requestPermission() {
        ActivityCompat.requestPermissions(HomePage.getInstance(),new String[]{
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.RECORD_AUDIO
        },REQUEST_PERMISSION_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case REQUEST_PERMISSION_CODE:
            {
                if(grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED)
                    Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            break;
        }
    }

    //check if the user get the permission to recoding audio
    private boolean checkPermissionFromDevice() {
        int write_external_storage_result= ContextCompat.checkSelfPermission
                (HomePage.getInstance(), Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int record_audio_result=ContextCompat.checkSelfPermission(HomePage.getInstance(),
                Manifest.permission.RECORD_AUDIO);
        return write_external_storage_result== PackageManager.PERMISSION_GRANTED
                && record_audio_result==PackageManager.PERMISSION_GRANTED;
    }

    // initialize the media recorder and its attributes
    private void setupMediaRecorder() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setOutputFile(PathName);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

    }

}



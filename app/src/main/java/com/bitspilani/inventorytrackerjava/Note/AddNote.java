package com.bitspilani.inventorytrackerjava.Note;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.bitspilani.inventorytrackerjava.R;
import com.bitspilani.inventorytrackerjava.ReminderBroadcast;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import java.text.ParseException;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AddNote extends AppCompatActivity {
    FirebaseFirestore fstore;
    EditText noteContent, noteTitle;
    ProgressBar progressBarSave;
    FirebaseUser user;
    Intent data;
    Button button_date, button_time;
    String timeTonotify;
    String dateString = "";
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        createNotificationChannel();
        button_date = findViewById(R.id.button_date);
        button_time = findViewById(R.id.button_time);
        button_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setDate();
            }
        });

        button_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setTime();
            }
        });


        data = getIntent();

        fstore = FirebaseFirestore.getInstance();
        noteContent = findViewById(R.id.addNoteContent);
        noteTitle = findViewById(R.id.addNoteTitle);

        progressBarSave = findViewById(R.id.progressBar);

        user = FirebaseAuth.getInstance().getCurrentUser();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String nTitle = noteTitle.getText().toString();
                String ncontent = noteContent.getText().toString();

                if (nTitle.isEmpty() || ncontent.isEmpty()) {
                    Toast.makeText(AddNote.this, "Cannot save notes with Empty Fields", Toast.LENGTH_SHORT).show();
                    return;

                }else{
                    progressBarSave.setVisibility(view.VISIBLE);

                    //save note
                    DocumentReference docref = fstore.collection("notes").document(user.getUid())
                            .collection("myNotes").document();
                    Map<String, Object> note = new HashMap<>();
                    note.put("title", nTitle);
                    note.put("content", ncontent);

                    docref.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(AddNote.this, "Note Added", Toast.LENGTH_SHORT).show();
                            onBackPressed();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(AddNote.this, "Try again", Toast.LENGTH_SHORT).show();
                            progressBarSave.setVisibility(view.VISIBLE);
                        }
                    });
                   giveNotification(view,nTitle,ncontent);

                }


            }
        });
//        To add a back button in the ad note activity
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void giveNotification(View v,String title,String content) {
        try {
            Toast.makeText(AddNote.this, "Reminder Set", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AddNote.this, ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(AddNote.this,0,intent,0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long timeAtButtonClick = System.currentTimeMillis();
            try {
                Date date = sdf.parse(dateString);  //This line of code can blow up.
                long delay = date.getTime()-timeAtButtonClick;
                alarmManager.set(AlarmManager.RTC_WAKEUP,timeAtButtonClick+delay,pendingIntent);
            }catch (Exception e){
                if(e!=null){
                    Date date = sdf2.parse(dateString);
                    Log.e("dateString parse error",e.getMessage());
                    long delay = date.getTime()-timeAtButtonClick;
                    alarmManager.set(AlarmManager.RTC_WAKEUP,timeAtButtonClick+delay,pendingIntent);
                }
            }

        }catch (Exception e){
            Log.e("DateTime Error",e.getMessage());

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.close_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.close) {
            Toast.makeText(this, "Note not saved", Toast.LENGTH_SHORT).show();
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }





//TODO: Change the "LemuBit" occurances in the source code!





    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "LemubitReminderChannel";
            String description = "Channel for Lemubit Reminder";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("Lemubit", name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }

    private void setTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                timeTonotify = i + ":" + i1;
                button_time.setText(FormatTime(i, i1));
            }
        }, hour, minute, true);
        timePickerDialog.show();

    }

    private void setDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {


            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                String date = day + "-" + (month + 1) + "-" + year;
                button_date.setText(date);
                dateString += " " + date ;
            }
        }, year, month, day);
        datePickerDialog.show();
    }

    public String FormatTime(int hour, int minute) {

        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }


        if (hour < 10) {
            time = "0" + hour + ":" + formattedMinute + ":"  + "00";
        }else{
            time = hour +":" + formattedMinute + ":"+ "00";
        }

        dateString += " " + time;
        return time;
    }
}
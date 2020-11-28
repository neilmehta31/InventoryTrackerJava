package com.bitspilani.inventorytrackerjava.Note;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import com.bitspilani.inventorytrackerjava.R;
import com.bitspilani.inventorytrackerjava.ReminderBroadcast;
import com.bitspilani.inventorytrackerjava.dashboard;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class EditNote extends AppCompatActivity {
    Intent data;
    EditText editNoteTitle, editNoteContent;
    FirebaseFirestore fStore;
    ProgressBar spinner;
    FirebaseUser user;

    Button button_date, button_time, button_cancel;
    String timeTonotify;
    String dateString = "";
    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
    SimpleDateFormat sdf2 = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        fStore = fStore.getInstance();

        data = getIntent();

        editNoteTitle = findViewById(R.id.editNoteTitle);
        editNoteContent = findViewById(R.id.editNoteContent);
        spinner = findViewById(R.id.spinner);

        user = FirebaseAuth.getInstance().getCurrentUser();

        String noteTitle = data.getStringExtra("title");
        String noteContent = data.getStringExtra("content");

        editNoteContent.setText(noteContent);
        editNoteTitle.setText(noteTitle);

        button_date = findViewById(R.id.button_date);
        button_time = findViewById(R.id.button_time);
        button_cancel = findViewById(R.id.button_cancel);

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
        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditNote.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(EditNote.this,0,intent,0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                Intent i = new Intent(EditNote.this,dashboard.class);
                Toast.makeText(EditNote.this, "Reminder deleted", Toast.LENGTH_SHORT).show();
                startActivity(i);

                finish();
            }
        });


        FloatingActionButton fab = findViewById(R.id.saveEditedNote);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nTitle = editNoteTitle.getText().toString();
                String ncontent = editNoteContent.getText().toString();

                if (nTitle.isEmpty() || ncontent.isEmpty()) {
                    Toast.makeText(EditNote.this, "Cannot save notes with Empty Fields", Toast.LENGTH_SHORT).show();
                    return;

                }

                spinner.setVisibility(view.VISIBLE);

                //save note
                DocumentReference docref = fStore.collection("notes")
                        .document(user.getUid())
                        .collection("myNotes")
                        .document(data.getStringExtra("noteId"));
                Map<String, Object> note = new HashMap<>();
                note.put("title", nTitle);
                note.put("content", ncontent);

                docref.update(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditNote.this, "Note Updated", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), dashboard.class));
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditNote.this, "Try again", Toast.LENGTH_SHORT).show();
                        spinner.setVisibility(view.VISIBLE);
                    }
                });
                giveNotification(view,nTitle,ncontent);
            }
        });
    }

    private void giveNotification(View v, String title, String content) {
        try {
            Toast.makeText(EditNote.this, "Reminder Set", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(EditNote.this, ReminderBroadcast.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(EditNote.this, 0, intent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
            long timeAtButtonClick = System.currentTimeMillis();
            try {
                Date date = sdf.parse(dateString);  //This line of code can blow up.
                long delay = date.getTime() - timeAtButtonClick;
                alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + delay, pendingIntent);
            } catch (Exception e) {
                if (e != null) {
                    Date date = sdf2.parse(dateString);
                    Log.e("dateString parse error", e.getMessage());
                    long delay = date.getTime() - timeAtButtonClick;
                    alarmManager.set(AlarmManager.RTC_WAKEUP, timeAtButtonClick + delay, pendingIntent);
                }
            }

        } catch (Exception e) {
            Log.e("DateTime Error", e.getMessage());

        }


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                dateString += " " + date;
            }
        }, year, month, day);
        datePickerDialog.show();
    }



    public String FormatTime ( int hour, int minute){
        String time;
        time = "";
        String formattedMinute;

        if (minute / 10 == 0) {
            formattedMinute = "0" + minute;
        } else {
            formattedMinute = "" + minute;
        }


        if (hour < 12) {
            time = "0" + hour + ":" + formattedMinute + ":" + "00";
        } else {
            time = hour + ":" + formattedMinute + ":" + "00";
        }

        dateString += " " + time;
        return time;

    }
}





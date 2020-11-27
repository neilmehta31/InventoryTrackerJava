package com.bitspilani.inventorytrackerjava.Note;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
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

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

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
                Toast.makeText(AddNote.this, "Reminder", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(AddNote.this, ReminderBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(AddNote.this,0,intent,0);
                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                long timeAtButtonClick = System.currentTimeMillis();
                long tenSecondsInMillis = 1000*10;
                alarmManager.set(AlarmManager.RTC_WAKEUP,timeAtButtonClick+tenSecondsInMillis,pendingIntent);
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

                }

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
            }
        });
//        To add a back button in the ad note activity
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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

    }}
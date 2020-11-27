package com.bitspilani.inventorytrackerjava;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class ReminderBroadcast  extends BroadcastReceiver  {

    @Override
    public void onReceive(Context context, Intent intent) {


            NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context,"Lemubit")
                    .setSmallIcon(R.drawable.ic_menu_camera)
                    .setContentTitle("You have a TODO Scheduled")
                    .setContentText("Checkout your Scheduled TODO Task right now!!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(200,mBuilder.build());




        //TODO:Change the app Icon and also change the notification Icon.
//        TOdo: Also change the Lemubit occurances in the code.



    }
}

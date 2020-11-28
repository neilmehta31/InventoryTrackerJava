package com.bitspilani.inventorytrackerjava;

import android.app.PendingIntent;
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
                    .setSmallIcon(R.drawable.images2)
                    .setContentTitle("You have a TODO Scheduled")
                    .setContentText("Checkout your Scheduled TODO Task right now!!")
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        PendingIntent conPendingIntent = PendingIntent.getActivity(context,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(conPendingIntent);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(context);
            notificationManagerCompat.notify(200,mBuilder.build());




//        TOdo: Also change the Lemubit occurances in the code.



    }
}

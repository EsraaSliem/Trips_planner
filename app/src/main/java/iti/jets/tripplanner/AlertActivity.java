package iti.jets.tripplanner;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import iti.jets.tripplanner.adapters.UpComingTripAdapter;
import iti.jets.tripplanner.interfaces.AlertAdapterCommunicator;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.Utilities;

public class AlertActivity extends AppCompatActivity {
    FragmentTransaction fragmentTransaction;
    NotificationManager mNotificationManager;
    private NotificationManager notifManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intenntComingFromReciever = getIntent();

        int alertMessage = intenntComingFromReciever.getIntExtra(Utilities.ALERT_MESSAGE, -1);
        if (alertMessage != -1) {
            if (alertMessage == 3)
                mNotificationManager.cancelAll();
            Trip trip = intenntComingFromReciever.getParcelableExtra(Utilities.TRIP_OBJECT);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("You Have Trip, \"" + trip.getTripName() + "\" Now!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "go",
                    (dialog, id) -> {
//                            dialog.cancel();
//                            Intent intent1 = new Intent(getApplicationContext(), NavigatinDrawerActivity.class);
//                            startActivity(intent1);
                            AlertAdapterCommunicator communicator = new UpComingTripAdapter(AlertActivity.this);

                        communicator.callOpenMap(trip);
                    });

            builder1.setNegativeButton(
                    "Snooze",
                    (dialog, id) -> {
                        dialog.cancel();

                       // sendNotification(trip);
                            createNotification(trip, getApplicationContext());

                    });
            builder1.setNeutralButton("cancel", (dialogInterface, i) -> {
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);


            });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
    }

    public void sendNotification(Trip trip) {

        //Get an instance of NotificationManager//

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this, "channelId")
                        .setSmallIcon(R.drawable.notify_logo)
                        .setContentTitle("You have Trip")
                        .setContentText(trip.getTripName() + ", today at " + trip.getTripTime());

        //  mNotificationManager.notify().
        Intent resultIntent = new Intent(this, AlertActivity.class);
        resultIntent.putExtra(Utilities.ALERT_MESSAGE, 3);
        resultIntent.putExtra(Utilities.TRIP_OBJECT, trip);
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntentWithParentStack(resultIntent);
// Get the PendingIntent containing the entire back stack
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        mNotificationManager.notify(001, mBuilder.build());
    }

    public void createNotification(Trip trip, Context context) {
        final int NOTIFY_ID = 0; // ID of notification
        String id = "id"; //context.getString(R.string.default_notification_channel_id); // default_channel_id
        String title = "Ykm;k"; //context.getString(R.string.default_notification_channel_title); // Default Channel
        Intent intent;
        TaskStackBuilder stackBuilder;
        PendingIntent pendingIntent;
        NotificationCompat.Builder builder;
        if (notifManager == null) {
            notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = notifManager.getNotificationChannel(id);
            if (mChannel == null) {
                mChannel = new NotificationChannel(id, title, importance);
                mChannel.enableVibration(true);
                mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
                notifManager.createNotificationChannel(mChannel);
            }
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(this, AlertActivity.class);
            intent.putExtra(Utilities.ALERT_MESSAGE, 3);
            intent.putExtra(Utilities.TRIP_OBJECT, trip);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            pendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
            // pendingIntent = PendingIntent.getActivity(0,PendingIntent.FLAG_IMMUTABLE);
            builder.setContentTitle("you have trip now")                            // required
                    .setSmallIcon(R.drawable.notify_logo)   // required
                    .setContentText(trip.getTripName() + ", today at " + trip.getTripTime()) // required
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
            builder.setOngoing(true);
            builder.setContentIntent(pendingIntent);
        } else {
            builder = new NotificationCompat.Builder(context, id);
            intent = new Intent(this, AlertActivity.class);
            intent.putExtra(Utilities.ALERT_MESSAGE, 3);
            intent.putExtra(Utilities.TRIP_OBJECT, trip);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntentWithParentStack(intent);
            pendingIntent =
                    stackBuilder.getPendingIntent(0, PendingIntent.FLAG_IMMUTABLE);
            //  pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            builder.setContentTitle("you have trip now")                            // required
                    .setSmallIcon(R.drawable.notify_logo)   // required
                    .setContentText(trip.getTripName() + ", today at " + trip.getTripTime()) // required
                    .setAutoCancel(false)
                    .setContentIntent(pendingIntent)
                    .setTicker("you have trip now")
                    .setVibrate(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400})
                    .setPriority(Notification.PRIORITY_HIGH);
            builder.setOngoing(true);
            builder.setContentIntent(pendingIntent);
        }
        Notification notification = builder.build();
        notifManager.notify(NOTIFY_ID, notification);
    }

}

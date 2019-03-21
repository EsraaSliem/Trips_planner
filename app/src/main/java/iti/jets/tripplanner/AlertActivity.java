package iti.jets.tripplanner;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        mNotificationManager =

                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intentComingFromReceiver = getIntent();
        int alertMessage = intentComingFromReceiver.getIntExtra(Utilities.ALERT_MESSAGE, -1);
        if (alertMessage != -1) {
            if (alertMessage == 3)
                mNotificationManager.cancelAll();
            Trip trip = intentComingFromReceiver.getParcelableExtra(Utilities.TRIP_OBJECT);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
//            builder1.setMessage("You Have Trip, \"" + trip.getTripName() + "\" Now!");
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

                        sendNotification(trip);
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
                new NotificationCompat.Builder(this)
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
                stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setOngoing(true);

        mNotificationManager.notify(001, mBuilder.build());
    }

}

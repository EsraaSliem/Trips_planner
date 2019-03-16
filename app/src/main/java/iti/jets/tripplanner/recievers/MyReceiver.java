package iti.jets.tripplanner.recievers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Vibrator;
import android.widget.Toast;

import iti.jets.tripplanner.AlertActivity;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.Utilities;

public class MyReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Don't panik but your time is up!!!!.",
                Toast.LENGTH_LONG).show();
        Trip trip = intent.getParcelableExtra(Utilities.TRIP_OBJECT);
        //***************
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        MediaPlayer mp = MediaPlayer.create(context, notification);
        mp.start();
        //**************
        // Vibrate the mobile phone
        Intent intent1 = new Intent(context, AlertActivity.class);
        intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent1.putExtra(Utilities.ALERT_MESSAGE, 1);
        intent1.putExtra(Utilities.TRIP_OBJECT, trip);
        context.startActivity(intent1);
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        vibrator.vibrate(2000);
    }
}

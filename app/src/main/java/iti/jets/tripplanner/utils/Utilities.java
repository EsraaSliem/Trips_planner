package iti.jets.tripplanner.utils;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.recievers.AlarmReceiver;

import static android.content.Context.ALARM_SERVICE;

public class Utilities {
    public static final String ALERT_MESSAGE = "alert message";
    public static final String TRIP_OBJECT = "trip";
    public static final String TRIP_ID = "tripId";
    public static final int TRIP_REMINDER = 0;
    public static final int RETURN_REMINDER = 1;



    public static String getCurrentTime() {
        Date date = new Date();
        String strDateFormat = "hh:mm a";
        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);
        String formattedDate = dateFormat.format(date);
        return formattedDate;
    }

    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static Date convertStringToDateFormat(String date, String time) {
        Date newDate = null;
        DateFormat format = new SimpleDateFormat("dd/MM/yyyy hh:mm a");
        try {
            newDate = format.parse(date + " " + time);
            long d = newDate.getTime();
            long dd = System.currentTimeMillis();
            return newDate;
        } catch (ParseException e) {
            e.printStackTrace();
            return newDate;
        }


    }

    public static long convertDateToMilliSecond(Date date) {
        return date.getTime();
    }

    public static boolean isEditTextEmpty(EditText editText) {
        return editText.getText().toString().matches("([\\s])*");
    }

    //Start Timer To broadCast Receiver
    public static void startAlert(Trip trip, Context context, int flag) {
        int pendingIntentId = (int) Utilities.convertDateToMilliSecond(
                Utilities.convertStringToDateFormat(Utilities.getCurrentDate(), Utilities.getCurrentTime()));
        Date date = null;
        if (flag == TRIP_REMINDER) {
            date = Utilities.convertStringToDateFormat(trip.getTripDate(), trip.getTripTime());
        } else if (flag == RETURN_REMINDER) {
            pendingIntentId += 1;
            date = Utilities.convertStringToDateFormat(trip.getReturnDate(), trip.getReturnTime());
        }
        long millis = date.getTime();
        Intent intent = new Intent(context, AlarmReceiver.class);
        intent.putExtra(Utilities.TRIP_OBJECT, trip);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), pendingIntentId, intent, PendingIntent.FLAG_IMMUTABLE);
        trip.setPendingIntentId(pendingIntentId);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);//getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, millis/*System.currentTimeMillis() + (i * 1000)*/, pendingIntent);
        ComponentName receiver = new ComponentName(context.getApplicationContext(), AlarmReceiver.class);
        PackageManager pm = context.getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }

    public static void alertMessage(Context context, Trip trip, String string, FireBaseData fireBaseData) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(string + " Trip");
        alert.setMessage("Are you sure you want to " + string.toLowerCase() + "?");
        if (string.toLowerCase().contains("delete")) {
            alert.setIcon(R.drawable.ic_delete_forever_black_24dp);
        } else if (string.toLowerCase().contains("cancel")) {
            alert.setIcon(R.drawable.ic_cancel_black_24dp);
        } else {
            //else if we want add something in future
        }

        alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
            // continue wif delete
            //FireBaseData fireBaseData = new FireBaseData(context);
            if (string.toLowerCase().contains("delete")) {
                fireBaseData.deleteTrip(trip);
                cancelAlarm(context, trip);
            } else if (string.toLowerCase().contains("cancel")) {
                fireBaseData.cancelTrip(trip, Trip.STATUS_CANCELLED);
                cancelAlarm(context, trip);
            } else {
                Toast.makeText(context, "String Must be (delete or cancel)", Toast.LENGTH_SHORT).show();
            }

        });
        alert.setNegativeButton(android.R.string.no, (dialog, which) -> {
            // close dialog
            dialog.cancel();
        });
        alert.show();
    }

    // cancel Alarm
    public static void cancelAlarm(Context context, Trip trip) {
        AlarmManager mAlarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent cancelServiceIntent = new Intent(context.getApplicationContext(), AlarmReceiver.class);
        PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                trip.getPendingIntentId(), // integer constant used to identify the service
                cancelServiceIntent,
                PendingIntent.FLAG_IMMUTABLE //no FLAG needed for a service cancel
        );
        cancelServicePendingIntent.cancel();
        mAlarmManager.cancel(cancelServicePendingIntent);

        ComponentName receiver = new ComponentName(context.getApplicationContext(), AlarmReceiver.class);
        PackageManager pm = context.getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

}

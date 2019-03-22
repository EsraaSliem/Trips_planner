package iti.jets.tripplanner.utils;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.recievers.MyReceiver;

import static android.content.Context.ALARM_SERVICE;

public class Utilities {
    public static final String ALERT_MESSAGE = "alert message";
    public static final String TRIP_OBJECT = "trip";
    public static final String TRIP_ID = "tripId";

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
            Log.i("Date         : ", "" + newDate.getTime());
            Log.i("Current Date : ", "" + System.currentTimeMillis());
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

    //Start Timer To broadCast Reciever
    public static void startAlert(Trip trip, Context context) {
        Date date = Utilities.convertStringToDateFormat(trip.getTripDate(), trip.getTripTime());
        Toast.makeText(context, "your trip Starts At " + date, Toast.LENGTH_LONG).show();
        Toast.makeText(context, "your trip trip date  " + trip.getTripDate() + trip.getTripTime(), Toast.LENGTH_LONG).show();


        long millis = date.getTime();
        Intent intent = new Intent(context, MyReceiver.class);
        intent.putExtra(Utilities.TRIP_OBJECT, trip);
        int pindingIntentId = (int) Utilities.convertDateToMilliSecond(Utilities.convertStringToDateFormat(Utilities.getCurrentDate(), Utilities.getCurrentTime()));
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(), pindingIntentId, intent, PendingIntent.FLAG_IMMUTABLE);
        trip.setPindingIntentId(pindingIntentId);


        AlarmManager alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);//getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, millis/*System.currentTimeMillis() + (i * 1000)*/, pendingIntent);
        Toast.makeText(context, "current " + System.currentTimeMillis() + " seconds",
                Toast.LENGTH_LONG).show();
        Toast.makeText(context, "Date " + millis + " seconds",
                Toast.LENGTH_LONG).show();
        ComponentName receiver = new ComponentName(context.getApplicationContext(), MyReceiver.class);
        PackageManager pm = context.getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

    }


}

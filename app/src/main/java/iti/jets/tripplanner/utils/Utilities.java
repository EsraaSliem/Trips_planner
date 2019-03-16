package iti.jets.tripplanner.utils;

import android.util.Log;
import android.widget.EditText;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Utilities {
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

    public static Date convertStringToDateFormate(String date, String time) {
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

}

package iti.jets.tripplanner.pojos;

import java.util.Date;

import iti.jets.tripplanner.utils.Utilities;

public class Trip {
    public static final int STATUS_UP_COMING = 1;
    public static final int STATUS_DONE = 0;
    public static final int STATUS_CANCELLED = -1;
    public static final int TYPE_ONE_DIRECTION = 1;
    public static final int TYPE_ROUND = 2;

    private String tripId;
    private String tripName;
    private String tripDate;
    private String tripTime;
    private String startPoint;
    private String endPoint;
    private int tripType;
    private int tripStatues;


    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }

    public String getTripName() {
        return tripName;
    }

    public void setTripName(String tripName) {
        this.tripName = tripName;
    }

    public String getTripDate() {
        return tripDate;
    }

    public boolean setTripDate(String tripDate) {
        Date currentDate = Utilities.convertStringToDateFormate(Utilities.getCurrentDate(), Utilities.getCurrentTime());

        Long date1 = Utilities.convertDateToMilliSecond(currentDate);
        Date inputDate;
        if (getTripTime() != null) {
            inputDate = Utilities.convertStringToDateFormate(tripDate, getTripTime());
        } else {
            inputDate = Utilities.convertStringToDateFormate(tripDate, "00:00 AM");
        }
        Long date2 = Utilities.convertDateToMilliSecond(inputDate);

        if (date1 < date2) {
            this.tripDate = tripDate;
            return true;
        } else {
            return false;
        }

    }

    public String getTripTime() {
        return tripTime;
    }

    public void setTripTime(String tripTime) {
        this.tripTime = tripTime;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public int getTripType() {
        return tripType;
    }

    public void setTripType(int tripType) {
        this.tripType = tripType;
    }

    public int getTripStatues() {
        return tripStatues;
    }

    public void setTripStatues(int tripStatues) {
        this.tripStatues = tripStatues;
    }
}

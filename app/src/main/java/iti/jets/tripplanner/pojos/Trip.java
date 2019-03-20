package iti.jets.tripplanner.pojos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import iti.jets.tripplanner.utils.Utilities;

public class Trip implements Parcelable {
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


    public static final Creator<Trip> CREATOR = new Creator<Trip>() {
        @Override
        public Trip createFromParcel(Parcel in) {
            return new Trip(in);
        }

        @Override
        public Trip[] newArray(int size) {
            return new Trip[size];
        }
    };

    public Trip(Parcel in) {
        tripId = in.readString();
        tripName = in.readString();
        tripDate = in.readString();
        tripTime = in.readString();
        startPoint = in.readString();
        endPoint = in.readString();
        tripType = in.readInt();
        tripStatues = in.readInt();
    }

    public Trip() {

    }

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
        Date currentDate = Utilities.convertStringToDateFormat(Utilities.getCurrentDate(), Utilities.getCurrentTime());

        Long date1 = Utilities.convertDateToMilliSecond(currentDate);
        Date inputDate;
        if (getTripTime() != null) {
            inputDate = Utilities.convertStringToDateFormat(tripDate, getTripTime());
        } else {
            inputDate = Utilities.convertStringToDateFormat(tripDate, "00:00 AM");
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(tripId);
        parcel.writeString(tripName);
        parcel.writeString(tripDate);
        parcel.writeString(tripTime);
        parcel.writeString(startPoint);
        parcel.writeString(endPoint);
        parcel.writeInt(tripType);
        parcel.writeInt(tripStatues);
    }
}

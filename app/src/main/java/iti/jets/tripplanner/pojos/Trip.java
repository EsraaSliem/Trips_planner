package iti.jets.tripplanner.pojos;

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

    public void setTripDate(String tripDate) {
        this.tripDate = tripDate;
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

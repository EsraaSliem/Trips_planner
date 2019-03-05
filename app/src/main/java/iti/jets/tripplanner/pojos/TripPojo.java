package iti.jets.tripplanner.pojos;

import iti.jets.tripplanner.utils.Utilities;

public class TripPojo {
    int tripId;
    String tripName;
    String tripDate;
    String tripTime;
    String startPoint;
    String endPoint;
    int tripType;
    int tripStatues;

    public TripPojo() {
        this.tripDate = Utilities.getCurrentDate();
        this.tripTime = Utilities.getCurrentTime();
        this.tripType = 1;
        this.tripStatues = 1;
    }

    public TripPojo(int tripId, String tripName, String tripDate, String tripTime, String startPoint, String endPoint, int tripType, int tripStatues) throws Exception {
        this.tripId = tripId;
        this.tripName = tripName;
        this.tripDate = tripDate;
        this.tripTime = tripTime;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        if (tripType == 1) {
            this.tripType = tripType;
        } else if (tripType == 2) {
            this.tripType = tripType;
        } else {
            throw new Exception("invalid input you must enter 1 or 2 ya ahraf");
        }
        this.tripStatues = tripStatues;
    }

    public TripPojo(int tripId, String tripName, String startPoint, String endPoint) {
        this.tripId = tripId;
        this.tripName = tripName;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.tripDate = Utilities.getCurrentDate();
        this.tripTime = Utilities.getCurrentTime();
        this.tripType = 1;
        this.tripStatues = 1;

    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
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

    public void setTripType(int tripType) throws Exception {
        if (tripType == 1) {
            this.tripType = tripType;
        } else if (tripType == 2) {
            this.tripType = tripType;
        } else {
            throw new Exception("invalid input you must enter 1 or 2 ya ahraf");
        }


    }

    public int getTripStatues() {
        return tripStatues;
    }

    public void setTripStatues(int tripStatues) throws Exception {
        if (tripStatues == 1) {
            this.tripStatues = tripStatues;
        } else if (tripStatues == -1) {
            this.tripStatues = tripStatues;
        } else if (tripStatues == 0) {
            this.tripStatues = tripStatues;
        } else {
            throw new Exception("invalid input you must enter -1 or 0 or 1  ya ahraf");
        }


    }
}

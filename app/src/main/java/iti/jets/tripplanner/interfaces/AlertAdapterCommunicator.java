package iti.jets.tripplanner.interfaces;

import android.content.Context;

import iti.jets.tripplanner.pojos.Trip;

public interface AlertAdapterCommunicator {
    void callOpenMap(Trip trip, Context context);
}

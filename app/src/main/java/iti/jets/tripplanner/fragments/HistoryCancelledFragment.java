package iti.jets.tripplanner.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.HistoryTripAdapter;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.FireBaseData;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryCancelledFragment extends Fragment {

    RecyclerView tripRecyclerView;
    Context context;
    DatabaseReference mRefDatabase;
    HistoryTripAdapter adapter;
    private List<Trip> trips;

    public HistoryCancelledFragment() {
        mRefDatabase = FireBaseData.mDatabase.getReference();
        trips = new ArrayList<>();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history_cancelled, container, false);
        context = getActivity();
        tripRecyclerView = view.findViewById(R.id.historyCancelledTrip_recyclerView);
        adapter = new HistoryTripAdapter(context, trips);
        tripRecyclerView.setAdapter(adapter);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        tripRecyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mRefDatabase.child("Trips").orderByKey().equalTo(FireBaseData.mAuth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if (iterator.hasNext()) {
                    DataSnapshot next = iterator.next();
                    trips.clear();
                    for (DataSnapshot dataSnapshot1 : next.getChildren()) {
                        Trip trip = dataSnapshot1.getValue(Trip.class);
                        if (trip != null && trip.getTripStatues() == Trip.STATUS_CANCELLED) {
                            trips.add(trip);
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error", databaseError.toString());
            }
        });
    }
}

package iti.jets.tripplanner.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

import iti.jets.tripplanner.NavigatinDrawerActivity;
import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.UpComingTripAdapter;
import iti.jets.tripplanner.pojos.Trip;


public class UpcomingTripFragment extends Fragment {


    RecyclerView tripRecyclerView;
    Context context;
    NavigatinDrawerActivity navigatinDrawerActivity;
    ArrayList<Trip> trips;
    UpComingTripAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_upcoming_trip, container, false);
        tripRecyclerView = view.findViewById(R.id.recyclerView_upcomingTrip);
        trips = new ArrayList<>();
        adapter = new UpComingTripAdapter(context, trips);
        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tripRecyclerView.setItemAnimator(new DefaultItemAnimator());
        tripRecyclerView.setAdapter(adapter);

        view.findViewById(R.id.fab).setOnClickListener(v -> openAddTripFragment());
        return view;
    }

    private void openAddTripFragment() {
        AddTripFragment addTripFragment = new AddTripFragment();
        FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
        fragmentTransaction
                .replace(R.id.mainContainerView, addTripFragment, "AddTripFragment")
                .addToBackStack("AddTripFragment")
                .commit();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        navigatinDrawerActivity = (NavigatinDrawerActivity) context;
    }


    @Override
    public void onStart() {
        super.onStart();

        Query query = navigatinDrawerActivity.getDatabaseReference()
                .child("Trips")
                .orderByKey()
                .equalTo(navigatinDrawerActivity.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.i("TAG", "onDataChange: getTrip");

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if (iterator.hasNext()) {
                    DataSnapshot next = iterator.next();
                    trips.clear();
                    for (DataSnapshot dataSnapshot1 : next.getChildren()) {
                        Trip trip = dataSnapshot1.getValue(Trip.class);
                        if (trip != null && trip.getTripStatues() == Trip.STATUS_UP_COMING) {
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

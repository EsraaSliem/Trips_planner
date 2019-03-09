package iti.jets.tripplanner.fragments;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.ObjectInputValidation;
import java.util.ArrayList;
import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.TripAdapter;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.FireBaseData;


public class UpcomingTripFragment extends Fragment {

    ImageButton addNoteTrip;
    RecyclerView tripRecyclerView;
    Button editBtn, removeBtn;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_trip, container, false);
        context = getActivity();
        tripRecyclerView = view.findViewById(R.id.recyclerView_upcomingTrip);

        tripRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        tripRecyclerView.setItemAnimator(new DefaultItemAnimator());
        addNoteTrip = view.findViewById(R.id.imgBtn_addNote_upcomingTripCard);
        editBtn = view.findViewById(R.id.editTripBtn_upcomingTripCard);
        removeBtn = view.findViewById(R.id.deleteTripBtn_upcomingTripCard);

        FireBaseData fireBaseData = new FireBaseData(context);
        List<Trip> trips = fireBaseData.getUpComingTrip();
        TripAdapter adapter = new TripAdapter(context, trips);

        tripRecyclerView.setAdapter(adapter);
        return view;
    }


}

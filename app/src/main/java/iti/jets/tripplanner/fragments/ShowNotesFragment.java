package iti.jets.tripplanner.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.NoteAdapter;
import iti.jets.tripplanner.interfaces.ObjectCarrier;
import iti.jets.tripplanner.pojos.Note;
import iti.jets.tripplanner.pojos.Trip;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowNotesFragment extends Fragment implements ObjectCarrier {

    Trip trip;
    private RecyclerView recyclerView;
    private Context context;
    FirebaseDatabase mDatabase;
    DatabaseReference mRefDatabase;
    List<Note> notes;
    NoteAdapter adapter;

    public ShowNotesFragment() {
        mDatabase = FirebaseDatabase.getInstance();
        mRefDatabase = mDatabase.getReference();
        notes = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_notes, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);
        context = getActivity();

        adapter = new NoteAdapter(context, notes);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = mRefDatabase.child("Notes").child(trip.getTripId());
        mRefDatabase.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    note.setTripId(trip.getTripId());
                    notes.add(note);
                    adapter.notifyDataSetChanged();
                }
                NoteAdapter adapter = new NoteAdapter(context, notes);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    @Override
    public void sendTripId(Trip trip) {
        this.trip = new Trip();
        this.trip.setTripId(trip.getTripId());
    }
}

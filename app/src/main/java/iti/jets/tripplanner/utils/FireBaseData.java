package iti.jets.tripplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import iti.jets.tripplanner.NavigatinDrawerActivity;
import iti.jets.tripplanner.adapters.HistoryTripAdapter;
import iti.jets.tripplanner.adapters.NoteAdapter;
import iti.jets.tripplanner.adapters.UpComingTripAdapter;
import iti.jets.tripplanner.pojos.Note;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.pojos.User;


public class FireBaseData {
    private String uid;
    private List<Trip> trips;
    private List<Note> notes;
    //Firebase Auth and DataBase
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRefDatabase;
    private Context context;

    //Firebase Connect
    public FireBaseData(Context context) {
        this.context = context;
        if (mDatabase == null) {
//            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = FirebaseDatabase.getInstance();
            mRefDatabase = mDatabase.getReference();
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
        }

        if (mCurrentUser != null) {
            mRefDatabase = mDatabase.getReference();
            uid = mCurrentUser.getUid();
            Toast.makeText(context, "IF " + uid, Toast.LENGTH_SHORT).show();
        }
    }

    public String getUserId() {
        return uid;
    }

    public void setUserId(String uid) {
        this.uid = uid;
    }

    public void writeNewUser(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setUserId(mCurrentUser.getUid());
                    mRefDatabase.child("Users").child(user.getUserId()).setValue(user);
                }
            }
        });
    }

    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent main_intent = new Intent(context, NavigatinDrawerActivity.class);
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(main_intent);
                } else {
                    Toast.makeText(context, "email or password is invalid", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean verifyUser(final String email, final String password) {
        Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
        return task.isSuccessful();
    }

    public void addTrip(Trip trip) {
        User user = new User();
        mRefDatabase = mDatabase.getReference("Trips");
        String key = mRefDatabase.push().getKey();

        trip.setTripId(key);

        trip.setTripName(trip.getTripName());
        trip.setTripDate(trip.getTripDate());
        trip.setTripTime(trip.getTripTime());
        trip.setStartPoint(trip.getStartPoint());
        trip.setEndPoint(trip.getEndPoint());
        try {
            trip.setTripType(trip.getTripType());
            trip.setTripStatues(trip.getTripStatues());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "UID " + uid, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "User UID " + user.getUserId(), Toast.LENGTH_SHORT).show();
        mRefDatabase.child(uid).child(key).setValue(trip);
        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
    }

    public void addNote(Note note, Trip trip) {
        mRefDatabase = mRefDatabase.child("Notes").child(trip.getTripId());
        String key = mRefDatabase.push().getKey();
        Toast.makeText(context, "Trip Id " + trip.getTripId(), Toast.LENGTH_SHORT).show();
        note.setNoteId(key);
        note.setNoteName(note.getNoteName());
        note.setNoteDescription(note.getNoteDescription());
        mRefDatabase.child(key).setValue(note);
    }

    public void getNotes(final RecyclerView recyclerView, Trip trip) {
        notes = new ArrayList<>();
        Query query = mRefDatabase.child("Notes").child("-L_gF1jKEA-Sq52ZZbnM");
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    Log.e("Note ID", note.getNoteId() + " " + note.getNoteName());
                    notes.add(note);
                }
                NoteAdapter adapter = new NoteAdapter(context, notes);
                recyclerView.setAdapter(adapter);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }


    public void getTrips(final RecyclerView recyclerView, final int status) {
        trips = new ArrayList<>();
        Query query = mRefDatabase.child("Trips").orderByKey().equalTo(mAuth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if (iterator.hasNext()) {
                    DataSnapshot next = iterator.next();
                    Iterator<DataSnapshot> iterator1 = next.getChildren().iterator();
                    while (iterator1.hasNext()) {
                        Trip trip = iterator1.next().getValue(Trip.class);
                        if (trip.getTripStatues() == status) {
                            trips.add(trip);
                        }
                    }
                }
                if (status == Trip.STATUS_UP_COMING) {
                    UpComingTripAdapter adapter = new UpComingTripAdapter(context, trips);
                    recyclerView.setAdapter(adapter);
                } else {
                    HistoryTripAdapter adapter = new HistoryTripAdapter(context, trips);
                    recyclerView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("fffff", databaseError.toString());
            }
        });
    }
}

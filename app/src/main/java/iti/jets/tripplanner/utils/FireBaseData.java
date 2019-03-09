package iti.jets.tripplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.pojos.User;


public class FireBaseData {
    String uid;
    //Firebase Auth and DataBase
    private FirebaseUser mCurrentUser;
    private FirebaseDatabase mDatabase;
    private FirebaseAuth mAuth;
    private DatabaseReference mRefDatabase;
    private Context context;
    List<Trip> trips;

    //Firebase Connect
    public FireBaseData(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance();
        mRefDatabase = mDatabase.getReference();
        mAuth = FirebaseAuth.getInstance();
        mCurrentUser = mAuth.getCurrentUser();

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

    public void addTrip(Trip t) {
        User user = new User();
        Trip trip = new Trip();

        mRefDatabase = mDatabase.getReference("Trips");
        String key = mRefDatabase.push().getKey();

        trip.setTripId(key);

        trip.setTripName(t.getTripName());
        trip.setTripDate(t.getTripDate());
        trip.setTripTime(t.getTripTime());
        trip.setStartPoint(t.getStartPoint());
        trip.setEndPoint(t.getEndPoint());
        try {
            trip.setTripType(t.getTripType());
            trip.setTripStatues(t.getTripStatues());
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(context, "UID " + uid, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "User UID " + user.getUserId(), Toast.LENGTH_SHORT).show();
        mRefDatabase.child(uid).child(key).setValue(trip);
        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
    }

    public List<Trip> getUpComingTrip(){
        trips =new ArrayList<>();
        Query query=mRefDatabase.child("Trips").orderByKey().equalTo(mAuth.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.e("fffffffffffff",dataSnapshot.getChildren().iterator().next().getValue().toString());
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if(iterator.hasNext()){
                    DataSnapshot next = iterator.next();
                    Iterator<DataSnapshot> iterator1 = next.getChildren().iterator();
                    while (iterator1.hasNext()){
                        Trip trip = iterator1.next().getValue(Trip.class);
                        Log.e("fffffffffffff1111",trip.getTripName());
                        trips.add(trip);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return trips;
    }
}

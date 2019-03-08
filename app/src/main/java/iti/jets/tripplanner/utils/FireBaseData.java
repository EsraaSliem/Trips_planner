package iti.jets.tripplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import iti.jets.tripplanner.NavigatinDrawerActivity;
import iti.jets.tripplanner.pojos.TripPojo;
import iti.jets.tripplanner.pojos.User;


public class FireBaseData {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Context context;

    public FireBaseData(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference();
            //.child("Users").child(mAuth.getCurrentUser().getUid())
        }
    }

    public void writeNewUser(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    user.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    mDatabase.child("Users").child(user.getUserId()).setValue(user);
                }
            }
        });
    }

    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    context.startActivity(new Intent(context, NavigatinDrawerActivity.class));
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

    public void addTrip(String tripName, String tripDate, String tripTime, String startPoint, String endPoint, int tripType, int tripStatues) {
        User user = new User();
        TripPojo trip = new TripPojo();
        trip.setTripName(tripName);
        trip.setTripDate(tripDate);
        trip.setTripTime(tripTime);
        trip.setStartPoint(startPoint);
        trip.setEndPoint(endPoint);
        try {
            trip.setTripType(tripType);
            trip.setTripStatues(tripStatues);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mDatabase.child("Trips").child(user.getUserId()).push().setValue(trip);
    }
}

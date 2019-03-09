package iti.jets.tripplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

    public void addTrip(String tripName, String tripDate, String tripTime, String startPoint, String endPoint, int tripType, int tripStatues) {
        User user = new User();
        Trip trip = new Trip();

        mRefDatabase = mDatabase.getReference("Trips");
        String key = mRefDatabase.push().getKey();

        trip.setTripId(key);

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
        Toast.makeText(context, "UID " + uid, Toast.LENGTH_SHORT).show();
        Toast.makeText(context, "User UID " + user.getUserId(), Toast.LENGTH_SHORT).show();
        mRefDatabase.child(uid).child(key).setValue(trip);
        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
    }
}

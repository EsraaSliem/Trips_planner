package iti.jets.tripplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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

import iti.jets.tripplanner.AuthenticationActivity;
import iti.jets.tripplanner.NavigatinDrawerActivity;
import iti.jets.tripplanner.pojos.Note;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.pojos.User;


public class FireBaseData {
    public static FirebaseDatabase mDatabase;
    public static FirebaseAuth mAuth;
    //Firebase Auth and DataBase
    static FirebaseUser mCurrentUser;
    static DatabaseReference mRefDatabase;
    private String uid;
    private Context context;

    //Firebase Connect
    public FireBaseData(Context context) {
        this.context = context;
        if (mDatabase == null) {
            //   FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = FirebaseDatabase.getInstance();
            mRefDatabase = mDatabase.getReference();
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
        }

        if (mCurrentUser != null) {
            mRefDatabase = mDatabase.getReference();
            uid = mCurrentUser.getUid();
            setUserId(uid);
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
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uId = current_user.getUid();
                    //Firebase Database
                    mRefDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
//                    user.setUserId(uId);
//                    mRefDatabase.setValue(user);
//                    Constatnts.user = user;

                    Intent main_intent = new Intent(context, AuthenticationActivity.class);
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(main_intent);

                }
            }
        });
    }

    public void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent main_intent = new Intent(context, NavigatinDrawerActivity.class);
                main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                SharedPreferences.Editor prefEditor= context.getSharedPreferences("AppPrefrences", Context.MODE_PRIVATE).edit();
                prefEditor.putBoolean("logined",true);
                prefEditor.apply();
                context.startActivity(main_intent);
            } else {
                Toast.makeText(context, "email or password is invalid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public boolean verifyUser(final String email, final String password) {
        Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
        return task.isSuccessful();
    }

    public String addTrip(Trip trip) {
        //User user = new User();
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
        mRefDatabase.child(mAuth.getUid()).child(key).setValue(trip);
        Toast.makeText(context, "Done", Toast.LENGTH_SHORT).show();
        return key;
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

    public void updateTrip(final Trip trip) {
        mRefDatabase = mDatabase.getReference("Trips").child(uid).child(trip.getTripId());
        mRefDatabase.child("tripDate").setValue(trip.getTripDate());
        mRefDatabase.child("tripName").setValue(trip.getTripName());
        mRefDatabase.child("tripTime").setValue(trip.getTripTime());
        mRefDatabase.child("tripType").setValue(trip.getTripType());
        mRefDatabase.child("startPoint").setValue(trip.getStartPoint());
        mRefDatabase.child("endPoint").setValue(trip.getEndPoint());
        Toast.makeText(context, "Update Trip Is Done", Toast.LENGTH_SHORT).show();
    }

    public void cancelTrip(final Trip trip, int status) {
        mRefDatabase = mDatabase.getReference("Trips").child(uid);
        Query applesQuery = mRefDatabase.child(trip.getTripId());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRefDatabase = mRefDatabase.child(trip.getTripId());
                mRefDatabase.child("tripStatues").setValue(status);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateNote(final Note note) {
        mRefDatabase = mDatabase.getReference("Notes").child(note.getTripId());
        Query applesQuery = mRefDatabase.child(note.getNoteId());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRefDatabase = mRefDatabase.child(note.getNoteId());
                mRefDatabase.child("noteDescription").setValue(note.getNoteDescription());
                mRefDatabase.child("noteName").setValue(note.getNoteName()).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        //progress dismiss if success
                        Toast.makeText(context, "Update Done", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(context, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(context, "Note Updated " + note.getNoteId(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteNote(Note note) {
        mRefDatabase = mDatabase.getReference("Notes").child(note.getTripId());
        Query applesQuery = mRefDatabase.child(note.getNoteId()).orderByChild("noteName");

        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                    appleSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public void deleteTrip(Trip trip) {
        mRefDatabase = mDatabase.getReference("Trips").child(getUserId());
        mRefDatabase.child(trip.getTripId()).getRef().removeValue();
    }

    public void changeNoteStatus(Note note) {
        mRefDatabase = mDatabase.getReference("Notes").child(note.getTripId()).child(note.getNoteId());
        if (note.isNoteStatus())
            mRefDatabase.child("noteStatus").setValue(note.isNoteStatus());
        else
            mRefDatabase.child("noteStatus").setValue(!note.isNoteStatus());
        Toast.makeText(context, "Note Updated " + note.isNoteStatus(), Toast.LENGTH_SHORT).show();
    }

    public void updateUser(final User user) {
        mRefDatabase = mDatabase.getReference("Users").child(mAuth.getUid());

        mRefDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mRefDatabase.child("email").setValue(user.getEmail());
                mRefDatabase.child("fName").setValue(user.getfName());
                mRefDatabase.child("lName").setValue(user.getlName());
                mRefDatabase.child("image").setValue(user.getImage());
                mRefDatabase.child("password").setValue(user.getPassword());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

    }
}

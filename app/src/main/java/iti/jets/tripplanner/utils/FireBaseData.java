package iti.jets.tripplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
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
import iti.jets.tripplanner.fragments.ProfileFragment;
import iti.jets.tripplanner.pojos.Note;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.pojos.User;


public class FireBaseData {
    //Firebase Auth and DataBase
    static FirebaseUser mCurrentUser;
    public static FirebaseDatabase mDatabase;
    static DatabaseReference mRefDatabase;
    public static FirebaseAuth mAuth;
    private String uid;
    private List<Trip> trips;
    private List<Note> notes;
    private Context context;

    //Firebase Connect
    public FireBaseData(Context context) {
        this.context = context;
        if (mDatabase == null) {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            mDatabase = FirebaseDatabase.getInstance();
            mRefDatabase = mDatabase.getReference();
            mAuth = FirebaseAuth.getInstance();
            mCurrentUser = mAuth.getCurrentUser();
        }

        if (mCurrentUser != null) {
            mRefDatabase = mDatabase.getReference();
            uid = mCurrentUser.getUid();
            setUserId(uid);
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
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uId = current_user.getUid();
                    //Firebase Database
                    mRefDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);
//                    user.setUserId(mAuth.getUid());
                    user.setUserId(uId);
//                    mRefDatabase.child("Users").child(user.getUserId()).setValue(user);
                    mRefDatabase.setValue(user);

                    Intent main_intent = new Intent(context, NavigatinDrawerActivity.class);
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
        mRefDatabase.child(uid).child(key).setValue(trip);
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

    public void getNotes(final RecyclerView recyclerView, String tripId) {
        notes = new ArrayList<>();
        Query query = mRefDatabase.child("Notes").child(tripId);
        mRefDatabase.keepSynced(true);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    note.setTripId(tripId);
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
                Log.i("TAG", "onDataChange: getTrip");

                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();

                if (iterator.hasNext()) {
                    DataSnapshot next = iterator.next();
                    trips.clear();
                    for (DataSnapshot dataSnapshot1 : next.getChildren()) {
                        Trip trip = dataSnapshot1.getValue(Trip.class);
                        if (trip != null && trip.getTripStatues() == status) {
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

    public  void updateUser(final User user)
    {
        mRefDatabase = mDatabase.getReference("Users").child(uid);

        mRefDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                updateUserEmail(user.getEmail(),user.getPassword());
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
    public void getUser(final ProfileFragment fragment ) {

        Query query = mRefDatabase.child("Users").orderByKey().equalTo(mAuth.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Toast.makeText(context, "ss", Toast.LENGTH_SHORT).show();

                User user=dataSnapshot.getChildren().iterator().next().getValue(User.class);
                fragment.getUser(user);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    }
    public void updateUserEmail(String email ,String pass)
    {



// Get auth credentials from the user for re-authentication. The example below shows
// email and password credentials but there are multiple possible providers,
// such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(email, pass);

     // Prompt the user to re-provide their sign-in credentials
        mCurrentUser.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Log.d(TAG, "User re-authenticated.");
                        mCurrentUser.updateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                          //  Log.d(TAG, "User email address updated.");
                                            Toast.makeText(context, "sucess", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                    }
                });

    }
}


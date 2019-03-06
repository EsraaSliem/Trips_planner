package iti.jets.tripplanner.utils;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import iti.jets.tripplanner.pojos.UserPojo;

public class FireBaseData {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private Context context;

    public FireBaseData(Context context) {
        this.context = context;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
    }

    public void writeNewUser(final String fName, final String lName, final String email, final String password, final String image) {
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
                    UserPojo user = new UserPojo();
                    user.setUserId(FirebaseAuth.getInstance().getCurrentUser().getUid());
                    user.setfName(fName);
                    user.setlName(lName);
                    user.setEmail(email);
                    user.setPassword(password);
                    user.setImage(image);
                    mDatabase.child("Users").child(user.getUserId()).setValue(user);
                }
            }
        });


    }

    public boolean verifyUser(final String email, final String password) {
        Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
        return task.isSuccessful();

    }
}

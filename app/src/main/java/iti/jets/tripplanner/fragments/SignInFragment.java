package iti.jets.tripplanner.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import iti.jets.tripplanner.AuthenticationActivity;
import iti.jets.tripplanner.NavigatinDrawerActivity;
import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.User;
import iti.jets.tripplanner.utils.FireBaseData;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    public static final int RC_SIGN_IN = 11100;
    private static final String EMAIL = "email";
    Button btnLogin;
    EditText edtEmail, edtPassword;
    DatabaseReference mDatabase;
    FireBaseData fireBaseData;
    private FirebaseAuth mAuth;
    Context context;
    GoogleSignInClient mGoogleSignInClient;
//    CallbackManager callbackManager;
//    LoginButton btnFacebook;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        context = getActivity();

        btnLogin = view.findViewById(R.id.signIn_btnSingUp);
        edtEmail = view.findViewById(R.id.signIn_edtEmail);
        edtPassword = view.findViewById(R.id.signUp_edtPassword);
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        edtPassword = view.findViewById(R.id.signIn_edtPassword);
        fireBaseData = new FireBaseData(getActivity());

        btnLogin.setOnClickListener(v -> {
            String email = edtEmail.getText().toString();
            String password = edtPassword.getText().toString();
            fireBaseData.loginUser(email, password);
        });

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("816442432158-45hahcuoo4c1blqucgg1au2jv56c2scs.apps.googleusercontent.com")
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        view.findViewById(R.id.signIn_btnGoogle).setOnClickListener(v -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        });
        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            sendToStart();
        }
    }

    private void sendToStart() {
        Intent startIntent = new Intent(getActivity(), NavigatinDrawerActivity.class);
        startActivity(startIntent);
        getActivity().finish();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == SignInFragment.RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success");
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        User user = new User();
                        user.setfName(firebaseUser.getDisplayName());
                        user.setImage(firebaseUser.getPhotoUrl().getPath());
                        user.setEmail(firebaseUser.getEmail());
                        user.setPassword(firebaseUser.getProviderId());
                        writeNewUser(user);
                        Intent main_intent = new Intent(context, NavigatinDrawerActivity.class);
                        main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        context.startActivity(main_intent);
                        //updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.getException());
//                            Snackbar.make(findViewById(R.id.main_layout), "Authentication Failed.", Snackbar.LENGTH_SHORT).show();
                        Toast.makeText(getActivity(), "Authentication Failed.", Toast.LENGTH_SHORT).show();
                        //updateUI(null);
                    }

                    // ...
                });
    }

    private void loginUser(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Intent main_intent = new Intent(context, NavigatinDrawerActivity.class);
                main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(main_intent);
            } else {
                Toast.makeText(context, "Email or Password is invalid", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void writeNewUser(final User user) {
        mAuth.createUserWithEmailAndPassword(user.getEmail(), user.getPassword()).addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uId = current_user.getUid();
                    //Firebase Database
                    mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(uId);

                    Intent main_intent = new Intent(context, AuthenticationActivity.class);
                    main_intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    context.startActivity(main_intent);

                }
            }
        });
    }


    public boolean verifyUser(final String email, final String password) {
        Task<AuthResult> task = mAuth.signInWithEmailAndPassword(email, password);
        return task.isSuccessful();
    }

}

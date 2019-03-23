package iti.jets.tripplanner.fragments;


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

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;

import iti.jets.tripplanner.NavigatinDrawerActivity;
import iti.jets.tripplanner.R;
import iti.jets.tripplanner.utils.FireBaseData;

import static android.support.constraint.Constraints.TAG;

/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends Fragment {

    private static final int RC_SIGN_IN = 100;
    private static final String EMAIL = "email";
    Button btnLogin;
    EditText edtEmail, edtPassword;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    FireBaseData fireBaseData;
    Context context;
    GoogleSignInClient mGoogleSignInClient;
    CallbackManager callbackManager;
    LoginButton btnFacebook;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        context = getActivity();

        btnLogin = view.findViewById(R.id.signIn_btnSingUp);
        edtEmail = view.findViewById(R.id.signIn_edtEmail);
        edtPassword = view.findViewById(R.id.signUp_edtPassword);
        //fireBaseData = new FireBaseData(getContext());
        mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());
        }
        edtPassword = view.findViewById(R.id.signIn_edtPassword);
        fireBaseData = new FireBaseData(getActivity());

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edtEmail.getText().toString();
                String password = edtPassword.getText().toString();
                fireBaseData.loginUser(email, password);
            }
        });

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
        // Set the dimensions of the sign-in button.
        SignInButton signInButton = view.findViewById(R.id.signIn_btnGoogle);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signInIntent = mGoogleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });
//        FacebookSdk.sdkInitialize(context);
//        AppEventsLogger.activateApp(context);
        callbackManager = CallbackManager.Factory.create();
        btnFacebook = view.findViewById(R.id.signIn_btnFacebook);
        btnFacebook.setReadPermissions(Arrays.asList(EMAIL));
        // If you are using in a fragment, call loginButton.setFragment(this);

        // Callback registration
        btnFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(context, "onSuccess", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
                Toast.makeText(context, "onCancel", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException exception) {
                Toast.makeText(context, "onError", Toast.LENGTH_SHORT).show();
            }
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
//        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
//            setResult(Resource.forSuccess(createIdpResponse(account)));
            Toast.makeText(context, "" + account.getEmail() + "\n" + account.getServerAuthCode() + "\n" + account.getServerAuthCode() + "\n" + account.getIdToken(), Toast.LENGTH_SHORT).show();
//            Log.i("account",account.getServerAuthCode());
            Log.i("account", account.getDisplayName());
//            Log.i("account",account.getIdToken());
            context.startActivity(new Intent(context, NavigatinDrawerActivity.class));


        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
            e.printStackTrace();
            Toast.makeText(context, "this acount does not exist", Toast.LENGTH_SHORT).show();
        }
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
}

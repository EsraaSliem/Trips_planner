package iti.jets.tripplanner;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;
import iti.jets.tripplanner.adapters.UpComingTripAdapter;
import iti.jets.tripplanner.fragments.AddTripFragment;
import iti.jets.tripplanner.fragments.HistoryFragment;
import iti.jets.tripplanner.fragments.ProfileFragment;
import iti.jets.tripplanner.fragments.ShowNotesFragment;
import iti.jets.tripplanner.fragments.UpcomingTripFragment;
import iti.jets.tripplanner.interfaces.UserInt;
import iti.jets.tripplanner.pojos.User;
import iti.jets.tripplanner.utils.Constatnts;
import iti.jets.tripplanner.utils.FireBaseData;
import iti.jets.tripplanner.utils.TripHeadService;

import static iti.jets.tripplanner.utils.Constatnts.user;
import static iti.jets.tripplanner.utils.FireBaseData.mAuth;


public class NavigatinDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentTransaction fragmentTransaction;
    Context context;
    FirebaseStorage storage;
    StorageReference storageReference;
    //declare fragments
    UpcomingTripFragment upcomingTripFragment;
    ProfileFragment profileFragment;
    HistoryFragment historyFragment;
    FragmentManager fragmentManager;
    CircleImageView profileImg;
    TextView nameTxt, emailTxt;
    private DatabaseReference databaseReference;
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigatin_drawer);

        //get Firebase
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        databaseReference = mDatabase.getReference();
        context = this;

        getUserDetails();
        fragmentManager = getSupportFragmentManager();

        Toolbar toolbar = findViewById(R.id.toolbar);
        context = this;
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (upcomingTripFragment == null) {
            upcomingTripFragment = new UpcomingTripFragment();
        }
        addFragment(upcomingTripFragment, "UpcomingTripFragment");
        navigationView.getMenu().getItem(0).setChecked(true);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigatin_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(context, AuthenticationActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.

        switch (item.getItemId()) {
//            case R.id.nav_profile:
//
//                break;
            case R.id.nav_upComing:
                if (upcomingTripFragment == null)
                    upcomingTripFragment = new UpcomingTripFragment();
                addFragment(upcomingTripFragment, "UpcomingTripFragment");
                break;
            case R.id.nav_history:
                if (historyFragment == null)
                    historyFragment = new HistoryFragment();
                addFragment(historyFragment, "HistoryFragment");
                break;
            case R.id.nav_setting:

                break;
            case R.id.nav_about:

                break;
            case R.id.nav_logout:
                logout();
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void addFragment(Fragment fragment, String fragmentTag) {

        Fragment fragmentByTag = fragmentManager.findFragmentByTag(fragmentTag);
        if (fragmentByTag != null) {
//            fragmentTransaction = fragmentManager.beginTransaction();
//            fragmentTransaction.remove(fragmentByTag).commit();
            fragmentManager.popBackStackImmediate(fragmentTag, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContainerView, fragment, fragmentTag)
                .addToBackStack(fragmentTag)
                .commit();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == UpComingTripAdapter.CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == RESULT_OK) {
                context.startService(new Intent(context, TripHeadService.class));
            } else {
                Toast.makeText(this, "Draw over other app permission not available. Closing the application.", Toast.LENGTH_SHORT).show();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void getUserImage() {
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        storageReference.child("images/" + Constatnts.uri).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Uri downloadUri = uri;
                String generatedFilePath = downloadUri.toString(); /// The string(file link) that you need
                storageReference.getDownloadUrl().toString();
                Constatnts.uri = generatedFilePath;
                Picasso.with(NavigatinDrawerActivity.this)
                        .load(Constatnts.uri)
                        .into(profileImg);
                Toast.makeText(context, "uri : " + generatedFilePath, Toast.LENGTH_LONG).show();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });
    }

    private void getUserDetails() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Query query = databaseReference.child("Users").orderByKey().equalTo(currentUser.getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                User user = dataSnapshot.getChildren().iterator().next().getValue(User.class);

                Toast.makeText(context, "ss" , Toast.LENGTH_SHORT).show();
                Constatnts.user = user;
                nameTxt.setText(Constatnts.user.getfName());
                emailTxt.setText(Constatnts.user.getEmail());
                getUserImage();
                progressDialog.dismiss();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public DatabaseReference getDatabaseReference() {
        return databaseReference;
    }

    public String getUserId() {
        return currentUser.getUid();
    }
}

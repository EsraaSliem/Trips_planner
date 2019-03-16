package iti.jets.tripplanner;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import iti.jets.tripplanner.adapters.UpComingTripAdapter;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.Utilities;

public class AlertActivity extends AppCompatActivity {
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main);
        Intent intenntComingFromReciever = getIntent();
        int alertMessage = intenntComingFromReciever.getIntExtra(Utilities.ALERT_MESSAGE, -1);
        if (alertMessage != -1) {
            final Trip trip = intenntComingFromReciever.getParcelableExtra(Utilities.TRIP_OBJECT);
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("You Have Trip, \"" + trip.getTripName() + "\" Now!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "go",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
//                            dialog.cancel();
//                            Intent intent1 = new Intent(getApplicationContext(), NavigatinDrawerActivity.class);
//                            startActivity(intent1);
                            AlertAdapterCommunicator communicator = new UpComingTripAdapter(AlertActivity.this);

                            communicator.callOpenMap(trip);
                        }
                    });

            builder1.setNegativeButton(
                    "Dismiss",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                            android.os.Process.killProcess(android.os.Process.myPid());
                            System.exit(1);
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        }
        //setContentView(R.layout.activity_main);
//        fragmentTransaction = getSupportFragmentManager().beginTransaction().addToBackStack("One");
//        fragmentTransaction.add(R.id.viewContainerFragment, new AddTripFragment(), "Frag_One_tag");
//        fragmentTransaction.commit();
    }

}

package iti.jets.tripplanner;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
//        fragmentTransaction = getSupportFragmentManager().beginTransaction().addToBackStack("One");
//        fragmentTransaction.add(R.id.viewContainerFragment, new AddTripFragment(), "Frag_One_tag");
//        fragmentTransaction.commit();
    }
}

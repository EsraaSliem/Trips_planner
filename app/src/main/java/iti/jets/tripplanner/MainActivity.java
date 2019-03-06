package iti.jets.tripplanner;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import iti.jets.tripplanner.adapters.NoteAdapter;
import iti.jets.tripplanner.fragments.AddTripFragment;
import iti.jets.tripplanner.pojos.NotePojo;

public class MainActivity extends AppCompatActivity {
    FragmentTransaction fragmentTransaction;
    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<NotePojo> noteList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);

        noteList = new ArrayList<>();
        adapter = new NoteAdapter(this, noteList);
        NotePojo a = new NotePojo("True Romance", "True Romance");
        noteList.add(a);
        a = new NotePojo("True Romance", "True Romance");
        noteList.add(a);
        a = new NotePojo("True Romance", "True Romance");
        noteList.add(a);
        a = new NotePojo("True Romance", "True Romance");
        noteList.add(a);
        a = new NotePojo("True Romance", "True Romance");
        noteList.add(a);
        a = new NotePojo("True Romance", "True Romance");
        noteList.add(a);


//        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(dis, 2);
//        recyclerView.setLayoutManager(mLayoutManager);
//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(10), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        fragmentTransaction = getSupportFragmentManager().beginTransaction().addToBackStack("One");
        fragmentTransaction.add(R.id.viewContainerFragment, new AddTripFragment(), "Frag_One_tag");
        fragmentTransaction.commit();
    }
}

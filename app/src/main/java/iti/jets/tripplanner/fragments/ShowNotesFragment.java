package iti.jets.tripplanner.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.NoteAdapter;
import iti.jets.tripplanner.pojos.Note;

/**
 * A simple {@link Fragment} subclass.
 */
public class ShowNotesFragment extends Fragment {

    private RecyclerView recyclerView;
    private NoteAdapter adapter;
    private List<Note> noteList;

    public ShowNotesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_show_notes, container, false);
        recyclerView = view.findViewById(R.id.recycler_view);

        noteList = new ArrayList<>();
        adapter = new NoteAdapter(getContext(), noteList);
        Note a = new Note("True Romance", "True Romance");
        noteList.add(a);
        a = new Note("True Romance", "True Romance");
        noteList.add(a);
        a = new Note("True Romance", "True Romance");
        noteList.add(a);
        a = new Note("True Romance", "True Romance");
        noteList.add(a);
        a = new Note("True Romance", "True Romance");
        noteList.add(a);
        a = new Note("True Romance", "True Romance");
        noteList.add(a);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        Toast.makeText(getContext(), "view", Toast.LENGTH_SHORT).show();
        return view;
    }

}

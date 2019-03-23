package iti.jets.tripplanner.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import iti.jets.tripplanner.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddNoteFragment extends Fragment {

    String noteDescription, noteName;
    private Button addNoteFragment_btnAddNoteFragment;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_note, container, false);
        addNoteFragment_btnAddNoteFragment = view.findViewById(R.id.addNoteFragment_btnAddNoteFragment);
        addNoteFragment_btnAddNoteFragment.setOnClickListener(view1 -> {

            AlertDialog.Builder mBuilder = new AlertDialog.Builder(getActivity());
            View mView = getActivity().getLayoutInflater().inflate(R.layout.add_note_layout, null);
            mBuilder.setTitle("Add Note To Trip");
            final EditText addNoteName = mView.findViewById(R.id.addNote_edtAddNoteName);
            final EditText addNoteDescription = mView.findViewById(R.id.addNote_edtAddNoteDescription);

            mBuilder.setPositiveButton("OK", (dialogInterface, i) -> {
                noteName = addNoteName.getText().toString();
                noteDescription = addNoteDescription.getText().toString();
                dialogInterface.dismiss();
            });
            mBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> dialogInterface.dismiss());
            mBuilder.setView(mView);
            AlertDialog dialog = mBuilder.create();
            dialog.show();
        });
        return view;
    }

}

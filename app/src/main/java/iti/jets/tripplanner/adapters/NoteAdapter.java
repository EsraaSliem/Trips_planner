package iti.jets.tripplanner.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.Note;
import iti.jets.tripplanner.utils.FireBaseData;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    // private Note note;
    LayoutInflater inflater;
    private Context context;
    private List<Note> noteList;
    private View alertLayout;
    private String noteDescription, noteName;

    public NoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Note note = noteList.get(position);
        holder.noteCheck.setText(note.getNoteName());
        holder.noteDesc.setText(note.getNoteDescription());
        if (note.isNoteStatus()) {
            holder.noteCheck.setChecked(true);
        }

        holder.editNoteCard_menu.setOnClickListener(view -> {
            //creating a popup menu
            PopupMenu popup = new PopupMenu(context, holder.editNoteCard_menu);
            //inflating menu from xml resource
            popup.inflate(R.menu.edit_note);
            //adding click listener
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.update_note_menu:
                        //handle menu1 click
                        alertNote(context, position);
                        break;
                    case R.id.delete_note_menu:
                        //handle menu1 click
                        FireBaseData fireBaseDat = new FireBaseData(context);
                        fireBaseDat.deleteNote(note);
                        break;
                }
                return false;
            });
            //displaying the popup
            popup.show();
        });

        holder.linearLayout.setOnLongClickListener(v -> {
            alertNote(context, position);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    private void alertNote(Context mContext, final int position) {
//        Toast.makeText(mContext, "Note ID pos out " + noteList.get(position).getNoteId(), Toast.LENGTH_SHORT).show();

        AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
        alertLayout = inflater.inflate(R.layout.add_note_layout, null);
        alert.setTitle("Add Note To Trip");
        final EditText addNoteName = alertLayout.findViewById(R.id.addNote_edtAddNoteName);
        final EditText addNoteDescription = alertLayout.findViewById(R.id.addNote_edtAddNoteDescription);
        alert.setPositiveButton("Ok", (dialog, i) -> {
            noteList.get(position);
            final FireBaseData fireBaseData = new FireBaseData(mContext);
            final Note note = new Note();
            noteName = addNoteName.getText().toString();
            noteDescription = addNoteDescription.getText().toString();
            note.setNoteName(noteName);
            note.setNoteDescription(noteDescription);
            note.setNoteId(noteList.get(position).getNoteId());
            note.setTripId(noteList.get(position).getTripId());
            fireBaseData.updateNote(note);
            dialog.dismiss();
        });

        alert.setNegativeButton("Cancel", (dialog, i) -> dialog.dismiss());

        alert.setView(alertLayout);
        AlertDialog dialog = alert.create();
        dialog.show();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView noteDesc;
        public CheckBox noteCheck;
        public ImageButton editNoteCard_menu;

        public LinearLayout parentLayout, linearLayout;

        public MyViewHolder(View view) {
            super(view);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            noteCheck = view.findViewById(R.id.name);
            noteDesc = view.findViewById(R.id.desc);
            editNoteCard_menu = view.findViewById(R.id.editNoteCard_menu);
            linearLayout = view.findViewById(R.id.linearLayout);
            parentLayout = view.findViewById(R.id.addNoteLayout);
        }
    }


}

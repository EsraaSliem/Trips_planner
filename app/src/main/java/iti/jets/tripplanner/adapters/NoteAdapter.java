package iti.jets.tripplanner.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.Note;

public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.MyViewHolder> {

    private Context mContext;
    private List<Note> noteList;

    public NoteAdapter(Context mContext, List<Note> noteList) {
        this.mContext = mContext;
        this.noteList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.note_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Note note = noteList.get(position);
        holder.noteName.setText(note.getNoteName());
        holder.noteDesc.setText(note.getNoteDescription());
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView noteName, noteDesc;
        public LinearLayout parentLayout;

        public MyViewHolder(View view) {
            super(view);
            noteName = view.findViewById(R.id.name);
            noteDesc = view.findViewById(R.id.desc);
            parentLayout = view.findViewById(R.id.addNoteLayout);
        }
    }
}

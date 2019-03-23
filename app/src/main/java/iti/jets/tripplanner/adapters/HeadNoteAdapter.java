package iti.jets.tripplanner.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.Note;
import iti.jets.tripplanner.utils.FireBaseData;

public class HeadNoteAdapter extends RecyclerView.Adapter<HeadNoteAdapter.MyViewHolder> {

    // private Note note;
    LayoutInflater inflater;
    private Context context;
    private List<Note> noteList;

    public HeadNoteAdapter(Context context, List<Note> noteList) {
        this.context = context;
        this.noteList = noteList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.head_note_item, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        Note note = noteList.get(position);
        holder.noteName.setText(note.getNoteName());
        holder.noteDesc.setText(note.getNoteDescription());
        holder.noteName.setOnCheckedChangeListener((buttonView, isChecked) -> {
            FireBaseData fireBaseData = new FireBaseData(context);
            if (isChecked) {
                note.isNoteStatus();
                fireBaseData.changeNoteStatus(note);
            } else {
                fireBaseData.changeNoteStatus(note);
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView noteDesc;
        public CheckBox noteName;

        public MyViewHolder(View view) {
            super(view);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            noteName = view.findViewById(R.id.headNoteItem_checkBox);
            noteDesc = view.findViewById(R.id.headNoteItem_Description);
        }
    }
}

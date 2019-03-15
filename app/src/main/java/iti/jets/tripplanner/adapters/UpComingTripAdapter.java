package iti.jets.tripplanner.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.Note;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.FireBaseData;

public class UpComingTripAdapter extends RecyclerView.Adapter<UpComingTripAdapter.MyViewHolder> {
    LayoutInflater inflater;
    View view;
    private Context context;
    private List<Trip> tripList;
    private View alertLayout;
    private String noteDescription, noteName;
    Trip trip;


    public UpComingTripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
        this.tripList = tripList;
    }

    @Override
    public UpComingTripAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.upcoming_trip_card, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UpComingTripAdapter.MyViewHolder holder, int position) {
        trip = tripList.get(position);
        holder.titleTxt.setText(trip.getTripName());
        holder.startPointTxt.setText(trip.getStartPoint());
        holder.endPointTxt.setText(trip.getEndPoint());
        holder.durationTxt.setText(trip.getTripDate());
        holder.timeTxt.setText(trip.getTripTime());
        holder.btnMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopup(v);
            }
        });
        holder.addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLayout = inflater.inflate(R.layout.add_note_layout, null);
                final AlertDialog.Builder alert = new AlertDialog.Builder(context);
                alert.setTitle("Add Note To Trip");
                final EditText addNoteName = alertLayout.findViewById(R.id.addNote_edtAddNoteName);
                final EditText addNoteDescription = alertLayout.findViewById(R.id.addNote_edtAddNoteDescription);
                alert.setView(alertLayout);
                alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        final FireBaseData fireBaseData = new FireBaseData(context);
                        final Note note = new Note();
//                        String key = trip.getTripId();
                        trip.setTripId(trip.getTripId());
                        noteName = addNoteName.getText().toString();
                        noteDescription = addNoteDescription.getText().toString();
                        note.setNoteName(noteName);
                        note.setNoteDescription(noteDescription);
                        fireBaseData.addNote(note, trip);
                        dialog.dismiss();
                    }
                });

                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (view != null) {
                            ViewGroup parent = (ViewGroup) view.getParent();
                            if (parent != null) {
                                parent.removeAllViews();
                                dialog.cancel();
                                dialog.dismiss();
                            }
                        }
                    }
                });
                AlertDialog dialog = alert.create();
                dialog.show();
            }
        });


    }

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.upcoming_card_menu, popup.getMenu());
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.upComingMenu_start:
                        String uri = "http://maps.google.com/maps?saddr=" + trip.getStartPoint() + "&daddr=" + trip.getEndPoint();
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                        context.startActivity(intent);
                        return true;
                    default:
                        return false;
                }
            }
        });
        popup.show();
    }


    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTxt, startPointTxt, endPointTxt, timeTxt, durationTxt;
        public ImageButton addNoteBtn, btnMenu;

        public MyViewHolder(View view) {
            super(view);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            titleTxt = view.findViewById(R.id.tripNameTxt_upcomingTrip_card);
            startPointTxt = view.findViewById(R.id.txtStartPoint_tripCardView);
            endPointTxt = view.findViewById(R.id.txtEndPoint_tripCardView);
            timeTxt = view.findViewById(R.id.txtTime_tripCardView);
            durationTxt = view.findViewById(R.id.txtDuration_tripCardView);
            addNoteBtn = view.findViewById(R.id.imgBtn_addNote_upcomingTripCard);
            btnMenu = view.findViewById(R.id.upcomingTripCard_menu);

        }
    }
}

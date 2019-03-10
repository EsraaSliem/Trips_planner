package iti.jets.tripplanner.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.Trip;

public class HistoryTripAdapter extends RecyclerView.Adapter<HistoryTripAdapter.MyViewHolder> {
    LayoutInflater inflater;
    View view;
    private Context mContext;
    private List<Trip> tripList;
    private View alertLayout;

    public HistoryTripAdapter(Context mContext, List<Trip> tripList) {
        this.mContext = mContext;
        this.tripList = tripList;
    }

    @Override
    public HistoryTripAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int i) {
        view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.history_trip_card_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(HistoryTripAdapter.MyViewHolder holder, int position) {
        Trip trip = tripList.get(position);
        holder.titleTxt.setText(trip.getTripName());
        holder.startPointTxt.setText(trip.getStartPoint());
        holder.endPointTxt.setText(trip.getEndPoint());
        holder.durationTxt.setText(trip.getTripDate());
        holder.timeTxt.setText(trip.getTripTime());
        holder.addNoteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertLayout = inflater.inflate(R.layout.add_note_layout, null);
                final AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                alert.setTitle("Details");
                alert.setView(alertLayout);
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


    @Override
    public int getItemCount() {
        return tripList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        public TextView titleTxt, startPointTxt, endPointTxt, timeTxt, durationTxt;
        public ImageButton addNoteBtn;
        public MyViewHolder(View view) {
            super(view);
            inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            titleTxt = view.findViewById(R.id.history_trip_tripNameTxt);
            startPointTxt = view.findViewById(R.id.history_trip_txtStartPoint);
            endPointTxt = view.findViewById(R.id.history_trip_txtEndPoint);
            timeTxt = view.findViewById(R.id.history_trip_txtTime);
            durationTxt = view.findViewById(R.id.history_trip_txtDuration);
            addNoteBtn = view.findViewById(R.id.history_trip_imgBtn_addNote);
            view.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v,
                                        ContextMenu.ContextMenuInfo menuInfo) {
            menu.add(Menu.NONE, R.id.action_settings,
                    Menu.NONE, R.string.action_settings);
            menu.add(Menu.NONE, R.id.action_settings,
                    Menu.NONE, R.string.action_settings);

        }

    }
}

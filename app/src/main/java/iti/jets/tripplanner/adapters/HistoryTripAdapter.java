package iti.jets.tripplanner.adapters;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;

import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.fragments.EditTripFragment;
import iti.jets.tripplanner.fragments.ShowNotesFragment;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.FireBaseData;

public class HistoryTripAdapter extends RecyclerView.Adapter<HistoryTripAdapter.MyViewHolder> {
    LayoutInflater inflater;
    View view;
    private Context context;
    private List<Trip> tripList;

    public HistoryTripAdapter(Context context, List<Trip> tripList) {
        this.context = context;
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
        holder.txtTitle.setText(trip.getTripName());
        holder.txtStartPoint.setText(trip.getStartPoint());
        holder.txtEntPoint.setText(trip.getEndPoint());
        holder.txtDate.setText(trip.getTripDate());
        holder.timeTxt.setText(trip.getTripTime());
        holder.btnRenewTrip.setOnClickListener(v -> renewTrip(trip));
        holder.btnShowNotes.setOnClickListener(v -> openShowNotesFragment(trip));
        holder.btnDeleteTrip.setOnClickListener(v -> deleteTrip(trip.getTripId()));
    }

    @Override
    public int getItemCount() {
        return tripList.size();
    }

    private void openShowNotesFragment(Trip trip) {
        ShowNotesFragment showNotesFragment = new ShowNotesFragment();
        showNotesFragment.sendTripId(trip);
        FragmentTransaction fragmentTransaction = ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.mainContainerView, showNotesFragment, null)
                .addToBackStack(null)
                .commit();

    }

    private void deleteTrip(String tripId) {
        Toast.makeText(context, "delete", Toast.LENGTH_SHORT).show();
        DatabaseReference databaseReference = FireBaseData.mDatabase.getReference("Trips").child(tripId);
        databaseReference.child(tripId).getRef().removeValue();
        notifyDataSetChanged();
    }

    private void renewTrip(Trip trip) {
        EditTripFragment editTripFragment = new EditTripFragment();
        editTripFragment.sendTripId(trip);
        FragmentManager supportFragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = supportFragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.mainContainerView, editTripFragment, "edit fragment")
                .addToBackStack("edit fragment")
                .commit();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        public TextView txtTitle, txtStartPoint, txtEntPoint, timeTxt, txtDate;
        public Button btnShowNotes, btnRenewTrip;
        public ImageButton btnDeleteTrip;

        public MyViewHolder(View view) {
            super(view);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            txtTitle = view.findViewById(R.id.historyTripItem_txtTripName);
            txtStartPoint = view.findViewById(R.id.historyTripItem_txtStartPoint);
            txtEntPoint = view.findViewById(R.id.historyTripItem_tripEndTxt);
            timeTxt = view.findViewById(R.id.historyTripItem_txtTime);
            txtDate = view.findViewById(R.id.historyTripItem_txtDate);
            btnShowNotes = view.findViewById(R.id.historyTripItem_btnShowNotes);
            btnRenewTrip = view.findViewById(R.id.historyTripItem_btnRenew);
            btnDeleteTrip = view.findViewById(R.id.historyTripItem_imageButtonDelete);
        }
    }
}

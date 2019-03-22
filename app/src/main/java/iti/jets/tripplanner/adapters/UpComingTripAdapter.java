package iti.jets.tripplanner.adapters;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.Date;
import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.fragments.EditTripFragment;
import iti.jets.tripplanner.fragments.ShowNotesFragment;
import iti.jets.tripplanner.interfaces.AlertAdapterCommunicator;
import iti.jets.tripplanner.pojos.Note;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.recievers.MyReceiver;
import iti.jets.tripplanner.utils.FireBaseData;
import iti.jets.tripplanner.utils.TripHeadService;
import iti.jets.tripplanner.utils.Utilities;

public class UpComingTripAdapter extends RecyclerView.Adapter<UpComingTripAdapter.MyViewHolder> implements AlertAdapterCommunicator {
    public static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 100;
    LayoutInflater inflater;
    View view;
    Trip trip;
    FragmentManager manager;
    FragmentTransaction transaction;
    private Context context;
    private List<Trip> tripList;
    private View alertLayout;
    private String noteDescription, noteName;
    TripHeadService mService;
    boolean mBound = false;

    public UpComingTripAdapter(Context context) {
        this.context = context;
    }

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

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            TripHeadService binder = (TripHeadService) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
//            mBound = false;
        }
    };

    private void addTrip() {
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

    public void showPopup(View v) {
        PopupMenu popup = new PopupMenu(context, v);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.upcoming_card_menu, popup.getMenu());
        // This activity implements OnMenuItemClickListener
        popup.setOnMenuItemClickListener(item -> {
            FireBaseData fireBaseData;
            switch (item.getItemId()) {
                case R.id.upComingMenu_edit:
                    EditTripFragment editTripFragment = new EditTripFragment();
                    editTripFragment.sendTripId(trip);
                    manager = ((AppCompatActivity) context).getSupportFragmentManager();
                    transaction = manager.beginTransaction();
                    transaction.replace(R.id.mainContainerView, editTripFragment, "edit fragment");
                    transaction.addToBackStack("edit fragment");
                    transaction.commit();
                    return true;
                case R.id.upComingMenu_cancel:
                    fireBaseData = new FireBaseData(context);
                    //cancel Alarm
                    cancelAlarm(trip);
                    fireBaseData.cancelTrip(trip, Trip.STATUS_CANCELLED);
                    return true;
                case R.id.upComingMenu_remove:
                    deleteTrip(trip);
                    //cancel Alarm
                    cancelAlarm(trip);
                    return true;
                case R.id.upComingMenu_showNotes:
                    ShowNotesFragment showNotesFragment = new ShowNotesFragment();
                    showNotesFragment.sendTripId(trip);
                    manager = ((AppCompatActivity) context).getSupportFragmentManager();
                    transaction = manager.beginTransaction();
                    transaction.replace(R.id.mainContainerView, showNotesFragment, null);
                    transaction.addToBackStack(null);
                    transaction.commit();
                    return true;
                default:
                    return false;
            }
        });
        popup.show();
    }


    @Override
    public int getItemCount() {
        return tripList.size();
    }

    @Override
    public void onBindViewHolder(UpComingTripAdapter.MyViewHolder holder, int position) {
        trip = tripList.get(position);
        //validate up comming trip to set Alarm For it
        if (trip != null) {
            if (isTripComming(trip)) {
                Utilities.startAlert(trip, context);
            }
            holder.txtTitle.setText(trip.getTripName());
            holder.txtStartPoint.setText(trip.getStartPoint());
            holder.txtEndPoint.setText(trip.getEndPoint());
            holder.txtDate.setText(trip.getTripDate());
            holder.txtTime.setText(trip.getTripTime());
            holder.btnMenu.setOnClickListener(v -> showPopup(v));
            holder.btnAddNote.setOnClickListener(v -> addTrip());
            holder.btnStartTrip.setOnClickListener(v -> {
//            FireBaseData fireBaseData = new FireBaseData(context);
//            fireBaseData.cancelTrip(trip, Trip.STATUS_DONE);
                openMap();
            });

        }

    }

    private void deleteTrip(Trip trip) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle("Delete Trip");
        alert.setMessage("Are you sure you want to delete?");
        alert.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // continue wif delete
                FireBaseData fireBaseData = new FireBaseData(context);
                fireBaseData.deleteTrip(trip);

            }
        });
        alert.setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                // close dialog
                dialog.cancel();
            }
        });
        alert.show();
    }

    @Override
    public void callOpenMap(Trip trip1) {
        String uri = "http://maps.google.com/maps?saddr=" + trip1.getStartPoint() + "&daddr=" + trip1.getEndPoint();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        context.startActivity(intent);
    }

    // cancel Alarm
    public void cancelAlarm(Trip trip) {
        AlarmManager mAlarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        Intent cancelServiceIntent = new Intent(context.getApplicationContext(), MyReceiver.class);
        PendingIntent cancelServicePendingIntent = PendingIntent.getBroadcast(
                context.getApplicationContext(),
                trip.getPindingIntentId(), // integer constant used to identify the service
                cancelServiceIntent,
                PendingIntent.FLAG_IMMUTABLE //no FLAG needed for a service cancel
        );
        cancelServicePendingIntent.cancel();
        mAlarmManager.cancel(cancelServicePendingIntent);

        ComponentName receiver = new ComponentName(context.getApplicationContext(), MyReceiver.class);
        PackageManager pm = context.getApplicationContext().getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView txtTitle, txtStartPoint, txtEndPoint, txtTime, txtDate;
        public ImageButton btnMenu;
        Button btnAddNote, btnStartTrip;

        public MyViewHolder(View view) {
            super(view);
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            txtTitle = view.findViewById(R.id.tripNameTxt_upcomingTrip_card);
            txtStartPoint = view.findViewById(R.id.txtStartPoint_tripCardView);
            txtEndPoint = view.findViewById(R.id.txtEndPoint_tripCardView);
            txtTime = view.findViewById(R.id.txtTime_tripCardView);
            txtDate = view.findViewById(R.id.txtDuration_tripCardView);
            btnAddNote = view.findViewById(R.id.upcomingTripCard_btnAddNote);
            btnMenu = view.findViewById(R.id.upcomingTripCard_menu);
            btnStartTrip = view.findViewById(R.id.upcomingTripCard_btnStart);
        }
    }

    private void openMap() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(context)) {

            //If the draw over permission is not available open the settings screen
            //to grant the permission.
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + context.getPackageName()));
            ((AppCompatActivity) context).startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
        } else {
            Intent intent = new Intent(context, TripHeadService.class);
            intent.putExtra(Utilities.TRIP_ID, trip.getTripId());

            context.bindService(intent, connection, Context.BIND_AUTO_CREATE);
            String uri = "http://maps.google.com/maps?saddr=" + trip.getStartPoint() + "&daddr=" + trip.getEndPoint();
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uri)));
        }

    }

    public boolean isTripComming(Trip trip) {


        Date currentDate = Utilities.convertStringToDateFormat(Utilities.getCurrentDate(), Utilities.getCurrentTime());
        Date tripDate = Utilities.convertStringToDateFormat(trip.getTripDate(), trip.getTripTime());
        Long date1 = Utilities.convertDateToMilliSecond(currentDate);
        if (tripDate != null) {
            Long date2 = Utilities.convertDateToMilliSecond(tripDate);
            return date1 < date2;
        } else return false;

    }
}

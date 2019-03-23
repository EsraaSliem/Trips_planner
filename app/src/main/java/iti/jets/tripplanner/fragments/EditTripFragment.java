package iti.jets.tripplanner.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;

import java.util.Calendar;
import java.util.Date;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.interfaces.ObjectCarrier;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.FireBaseData;
import iti.jets.tripplanner.utils.Utilities;

public class EditTripFragment extends Fragment implements ObjectCarrier {


    private static View view;
    Trip trip;
    private Context context;
    private String mAM_PM;
    private EditText edtTripName;
    private EditText edtTripDate;
    private EditText edtTripTime;
    private Spinner spnTripType;
    private Button btnUpdateTrip;
    private ImageButton btnTripDate, btnTripTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String tripDate;
    private String tripTime;
    private String startPoint;
    private String endPoint;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_edit_trip, container, false);
        }
        context = getActivity();

        edtTripName = view.findViewById(R.id.editTripFragment_edtTripName);
        edtTripDate = view.findViewById(R.id.editTripFragment_edtTripDate);
        edtTripTime = view.findViewById(R.id.editTripFragment_edtTripTime);
        spnTripType = view.findViewById(R.id.editTripFragment_spnTripType);

        edtTripName.setText(trip.getTripName());
        edtTripDate.setText(trip.getTripDate());
        //edtTripTime.setText(trip.getTripTime());

        btnTripDate = view.findViewById(R.id.editTripFragment_btnTripDate);
        btnTripTime = view.findViewById(R.id.editTripFragment_btnTripTime);
        btnUpdateTrip = view.findViewById(R.id.editTripFragment_btnUpdateTrip);

        ArrayAdapter<? extends String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.addTripFragment_spnTripType));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnTripType.setAdapter(adapter);
        if (trip.getTripType() == Trip.TYPE_ONE_DIRECTION) {
            spnTripType.setSelection(0);
        } else {
            spnTripType.setSelection(1);
        }

        //Trip_Date
        btnTripDate.setOnClickListener(v -> {
            // Get Current Time
            final Calendar c = Calendar.getInstance();

            mDay = c.get(Calendar.DAY_OF_MONTH);
            mMonth = c.get(Calendar.MONTH);
            mYear = c.get(Calendar.YEAR);
            // Launch Time Picker Dialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view1, year, monthOfYear, dayOfMonth) -> {
                        edtTripDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                        tripDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                    }, mYear, mMonth, mDay);
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis());
            datePickerDialog.show();
        });

        //Trip_Time
        btnTripTime.setOnClickListener(v -> {
            // Get Current Time
            final Calendar c = Calendar.getInstance();
            mHour = c.get(Calendar.HOUR_OF_DAY);
            mMinute = c.get(Calendar.MINUTE);
            // Launch Time Picker Dialog
            TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                    (view12, hourOfDay, minute) -> {
                        if (hourOfDay > 12) {
                            mAM_PM = "PM";
                            hourOfDay = hourOfDay - 12;
                        } else {
                            mAM_PM = "AM";
                        }
                        edtTripTime.setText(hourOfDay + ":" + minute + " " + mAM_PM);
                        tripTime = hourOfDay + ":" + minute + " " + mAM_PM;
                    }, mHour, mMinute, false);
            timePickerDialog.show();
        });

        PlaceAutocompleteFragment autocompleteStartPoint = (PlaceAutocompleteFragment)
                ((AppCompatActivity) context).getFragmentManager().findFragmentById(R.id.editTripFragment_startPoint);
        autocompleteStartPoint.setText(trip.getStartPoint());
        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("EG")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteStartPoint.setFilter(filter);
        autocompleteStartPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                startPoint = place.getName().toString();
//                if (startPoint.isEmpty()) {
//                    startPoint = trip.getEndPoint();
//                }
                Log.i("jh", place.getName().toString());
            }

            @Override
            public void onError(Status status) {
                Log.e("error", status.toString());
            }
        });
        PlaceAutocompleteFragment autocompleteEndPoint = (PlaceAutocompleteFragment)
                ((AppCompatActivity) context).getFragmentManager().findFragmentById(R.id.editTripFragment_entPoint);
        autocompleteEndPoint.setText(trip.getEndPoint());
        autocompleteEndPoint.setFilter(filter);
        autocompleteEndPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                endPoint = place.getName().toString();
//                if (endPoint.isEmpty()) {
//                    endPoint = trip.getEndPoint();
//                }
            }

            @Override
            public void onError(Status status) {
                Log.e("error", status.toString());
            }
        });
        if (startPoint == null || endPoint == null) {
            startPoint = trip.getStartPoint();
            endPoint = trip.getEndPoint();
        }

        btnUpdateTrip.setOnClickListener(v -> {
            updateTrip();
            getActivity().getSupportFragmentManager().popBackStack("edit fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        });
        return view;
    }


    private void updateTrip() {
        if (Utilities.isEditTextEmpty(edtTripName)) {
            edtTripName.setError("required Field");
        } else if (Utilities.isEditTextEmpty(edtTripDate)) {
            edtTripDate.setError("required Field");
        } else if (Utilities.isEditTextEmpty(edtTripTime)) {
            edtTripTime.setError("required Field");
        } else if (startPoint == null) {
            Toast.makeText(context, "you must enter start point", Toast.LENGTH_LONG).show();
        } else if (endPoint == null) {
            Toast.makeText(context, "you must enter end point", Toast.LENGTH_LONG).show();
        } else {
            if (!isValidDateAndTime(edtTripDate.getText().toString(), edtTripTime.getText().toString())) {
                Toast.makeText(context, "you must enter valid date and time", Toast.LENGTH_LONG).show();
            } else {
                FireBaseData fireBaseData = new FireBaseData(context);
                //trip = new Trip();
                trip.setTripName(edtTripName.getText().toString());
                trip.setTripTime(tripTime);
                trip.setTripDate(tripDate);
                trip.setTripStatues(Trip.STATUS_UP_COMING);
                trip.setStartPoint(startPoint);
                trip.setEndPoint(endPoint);
                if (spnTripType.getSelectedItem().toString().equalsIgnoreCase("one direction")) {
                    trip.setTripType(Trip.TYPE_ONE_DIRECTION);
                    Log.i("TAG", "updateTrip: " + spnTripType.getSelectedItemPosition());
                } else {
                    trip.setTripType(Trip.TYPE_ROUND_DIRECTION);
                }
                fireBaseData.updateTrip(trip);

                //Start Listening for BroadCast Receiver
                //tripDateDateObject = Utilities.convertStringToDateFormat(tripDate, tripTime);
                //startAlert(tripDateDateObject, trip);
                Utilities.startAlert(trip, getContext(), Utilities.TRIP_REMINDER);
            }
        }
    }


    private boolean isValidDateAndTime(String date, String time) {
        Date currentDate = Utilities.convertStringToDateFormat(Utilities.getCurrentDate(), Utilities.getCurrentTime());
        Long date1 = Utilities.convertDateToMilliSecond(currentDate);
        Date inputDate = Utilities.convertStringToDateFormat(date, time);

        Long date2 = Utilities.convertDateToMilliSecond(inputDate);

        return date1 < date2;
    }

    public void sendTripId(Trip trip) {
        this.trip = new Trip();
        this.trip.setTripId(trip.getTripId());
        this.trip = trip;
    }
}

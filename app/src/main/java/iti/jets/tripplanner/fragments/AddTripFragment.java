package iti.jets.tripplanner.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.FireBaseData;
import iti.jets.tripplanner.utils.Utilities;


public class AddTripFragment extends Fragment {

    private static View view;
    private Context context;
    private String mAM_PM;
    private EditText edtTripName;
    private EditText edtTripDate;
    private EditText edtTripTime;
    private Spinner spnTripType;
    private Button btnAddTrip;
    private ImageButton btnTripDate, btnTripTime;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private String tripDate;
    private String tripTime;
    private String startPoint;
    private String endPoint;
    //longitude and latitude
    private double startPointLongitude;
    private double startPointLatitude;
    private double endPointLongitude;
    private double endPointLatitude;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        }

        context = getActivity();

        edtTripName = view.findViewById(R.id.addTripFragment_edtTripName);
        edtTripDate = view.findViewById(R.id.addTripFragment_edtTripDate);
        edtTripTime = view.findViewById(R.id.addTripFragment_edtTripTime);
        spnTripType = view.findViewById(R.id.addTripFragment_spnTripType);
        btnTripTime = view.findViewById(R.id.addTripFragment_btnTripTime);
        btnTripDate = view.findViewById(R.id.addTripFragment_btnTripDate);
        btnAddTrip = view.findViewById(R.id.addTripFragment_btnAddTrip);

        ArrayAdapter<? extends String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.addTripFragment_spnTripType));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTripType.setAdapter(adapter);


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

        btnAddTrip.setOnClickListener(v -> addTrip());

        PlaceAutocompleteFragment autocompleteStartPoint = (PlaceAutocompleteFragment)
                ((AppCompatActivity) context).getFragmentManager().findFragmentById(R.id.addTripFragment_startPoint);

        AutocompleteFilter filter = new AutocompleteFilter.Builder()
                .setCountry("EG")
                .setTypeFilter(AutocompleteFilter.TYPE_FILTER_CITIES)
                .build();
        autocompleteStartPoint.setFilter(filter);
        autocompleteStartPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {


            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                startPoint = place.getName().toString();
                startPointLongitude = place.getLatLng().longitude;
                startPointLatitude = place.getLatLng().latitude;
                Log.i("jh", place.getName().toString());
            }

            @Override
            public void onError(Status status) {
                Log.e("error", status.toString());
            }
        });

        PlaceAutocompleteFragment autocompleteEndPoint = (PlaceAutocompleteFragment)
                ((AppCompatActivity) context).getFragmentManager().findFragmentById(R.id.addTripFragment_entPoint);
        autocompleteEndPoint.setFilter(filter);
        autocompleteEndPoint.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place place) {
                endPoint = place.getName().toString();
                endPointLongitude = place.getLatLng().longitude;
                endPointLatitude = place.getLatLng().latitude;
            }

            @Override
            public void onError(Status status) {
                Log.e("error", status.toString());
            }
        });
        return view;
    }


    private void addTrip() {
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
                final FireBaseData fireBaseData = new FireBaseData(context);
                Trip trip = new Trip();
                trip.setTripName(edtTripName.getText().toString());
                trip.setTripTime(tripTime);
                trip.setTripDate(tripDate);
                trip.setTripStatues(Trip.STATUS_UP_COMING);
                trip.setStartPoint(startPoint);
                trip.setStartPointlongitude(startPointLongitude);
                trip.setStartPointlatitude(startPointLatitude);
                trip.setEndPointlongitude(endPointLongitude);
                trip.setEndPointlatitude(endPointLatitude);
                trip.setEndPoint(endPoint);
                if (spnTripType.getSelectedItem().toString().equalsIgnoreCase("one direction")) {
                    trip.setTripType(Trip.TYPE_ONE_DIRECTION);
                } else {
                    trip.setTripType(Trip.TYPE_ROUND_DIRECTION);
                }
                fireBaseData.addTrip(trip);
                FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.mainContainerView, new UpcomingTripFragment());
                fragmentTransaction.addToBackStack("NoteTrip");
                fragmentTransaction.commit();
                //Start Listning for BroadCast Reciever
                Utilities.startAlert(trip, getContext());
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
}

package iti.jets.tripplanner.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import iti.jets.tripplanner.R;

public class AddTripFragment extends Fragment {

    private String mAM_PM;
    private EditText addTripFragment_edtTripName;
    private EditText addTripFragment_edtTripStartPoint;
    private EditText addTripFragment_edtTripEndPoint;
    private EditText addTripFragment_edtTripDate;
    private EditText addTripFragment_edtTripTime;
    private Spinner addTripFragment_spnTripType;
    private Button addTripFragment_btnAddTrip;
    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        addTripFragment_edtTripName = view.findViewById(R.id.addTripFragment_edtTripName);
        addTripFragment_edtTripStartPoint = view.findViewById(R.id.addTripFragment_edtTripStartPoint);
        addTripFragment_edtTripEndPoint = view.findViewById(R.id.addTripFragment_edtTripEndPoint);
        addTripFragment_edtTripDate = view.findViewById(R.id.addTripFragment_edtTripDate);
        addTripFragment_edtTripTime = view.findViewById(R.id.addTripFragment_edtTripTime);
        addTripFragment_spnTripType = view.findViewById(R.id.addTripFragment_spnTripType);
        addTripFragment_btnAddTrip = view.findViewById(R.id.addTripFragment_btnAddTrip);

        ArrayAdapter<? extends String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.addTripFragment_spnTripType));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addTripFragment_spnTripType.setAdapter(adapter);
        Toast.makeText(getActivity(), addTripFragment_spnTripType.getSelectedItem().toString(), Toast.LENGTH_SHORT).show();


        addTripFragment_edtTripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mMonth = c.get(Calendar.MONTH);
                mYear = c.get(Calendar.YEAR);
                Toast.makeText(getContext(), "Date", Toast.LENGTH_SHORT).show();
                // Launch Time Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                addTripFragment_edtTripDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

        addTripFragment_edtTripTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
                Toast.makeText(getContext(), "Clicked", Toast.LENGTH_SHORT).show();
                // Launch Time Picker Dialog
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                if (hourOfDay > 12) {
                                    mAM_PM = "PM";
                                    hourOfDay = hourOfDay - 12;
                                } else {
                                    mAM_PM = "AM";
                                }
                                addTripFragment_edtTripTime.setText(hourOfDay + ":" + minute + " " + mAM_PM);
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }

        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}

package iti.jets.tripplanner.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.GooglePlacesAutocompleteAdapter;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.recievers.MyReceiver;
import iti.jets.tripplanner.utils.FireBaseData;
import iti.jets.tripplanner.utils.Utilities;

import static android.content.Context.ALARM_SERVICE;


public class AddTripFragment extends Fragment {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD12pWp4Qe-ta3WXsV5WT9cv8pKEHnaR9w";

    Context context;
    private AutoCompleteTextView edtTripStartPoint;
    private String mAM_PM;
    private AutoCompleteTextView edtTripEndPoint;
    private EditText edtTripName;
    private EditText edtTripDate;
    private EditText edtTripTime;
    private Spinner spnTripType;
    private Button btnAddTrip;
    private Button btnTripDate;
    private int mYear, mMonth, mDay, mHour, mMinute;
    private Button btnTripTime;
    private String tripDate;
    private String tripTime;
    private String startPoint;
    private String endPoint;
    private Date tripDateDateObject;

    public static ArrayList autocomplete(String input) {
        ArrayList resultList = null;

        HttpURLConnection conn = null;
        StringBuilder jsonResults = new StringBuilder();
        try {
            StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
            sb.append("?key=" + API_KEY);
            sb.append("&components=country:egy");
            sb.append("&input=" + URLEncoder.encode(input, "utf8"));

            URL url = new URL(sb.toString());
            conn = (HttpURLConnection) url.openConnection();
            InputStreamReader in = new InputStreamReader(conn.getInputStream());

            // Load the results into a StringBuilder
            int read;
            char[] buff = new char[1024];
            while ((read = in.read(buff)) != -1) {
                jsonResults.append(buff, 0, read);
            }
        } catch (MalformedURLException e) {
            //  Log.e(LOG_TAG, "Error processing Places API URL", e);
            return resultList;
        } catch (IOException e) {
            //Log.e(LOG_TAG, "Error connecting to Places API", e);
            return resultList;
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        try {
            // Create a JSON object hierarchy from the results
            JSONObject jsonObj = new JSONObject(jsonResults.toString());
            JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

            // Extract the Place descriptions from the results
            resultList = new ArrayList(predsJsonArray.length());
            for (int i = 0; i < predsJsonArray.length(); i++) {
                resultList.add(predsJsonArray.getJSONObject(i).getString("description"));
            }
        } catch (JSONException e) {
            // Log.e(LOG_TAG, "Cannot process JSON results", e);
        }
        return resultList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_trip, container, false);
        context = getActivity();

        edtTripName = view.findViewById(R.id.addTripFragment_edtTripName);
        edtTripDate = view.findViewById(R.id.addTripFragment_edtTripDate);
        btnTripDate = view.findViewById(R.id.addTripFragment_btnTripDate);
        edtTripTime = view.findViewById(R.id.addTripFragment_edtTripTime);
        btnTripTime = view.findViewById(R.id.addTripFragment_btnTripTime);
        spnTripType = view.findViewById(R.id.addTripFragment_spnTripType);
        btnAddTrip = view.findViewById(R.id.addTripFragment_btnAddTrip);
        edtTripStartPoint = view.findViewById(R.id.addTripFragment_edtTripStartPoint);
        edtTripEndPoint = view.findViewById(R.id.addTripFragment_edtTripEndPoint);

        ArrayAdapter<? extends String> adapter = new ArrayAdapter<>(context,
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.addTripFragment_spnTripType));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnTripType.setAdapter(adapter);


        //Trip_Date
        btnTripDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mDay = c.get(Calendar.DAY_OF_MONTH);
                mMonth = c.get(Calendar.MONTH);
                mYear = c.get(Calendar.YEAR);
                // Launch Time Picker Dialog
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {
                                edtTripDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                tripDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

        //Trip_Time
        btnTripTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get Current Time
                final Calendar c = Calendar.getInstance();
                mHour = c.get(Calendar.HOUR_OF_DAY);
                mMinute = c.get(Calendar.MINUTE);
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
                                edtTripTime.setText(hourOfDay + ":" + minute + " " + mAM_PM);
                                tripTime = hourOfDay + ":" + minute + " " + mAM_PM;
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }

        });


        edtTripStartPoint.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), R.layout.list_item));
        edtTripStartPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPoint = parent.getItemAtPosition(position).toString();
            }
        });
        edtTripEndPoint.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), R.layout.list_item));
        edtTripEndPoint.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                endPoint = parent.getItemAtPosition(position).toString();
            }
        });

        btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final FireBaseData fireBaseData = new FireBaseData(context);
                String tripName = edtTripName.getText().toString();
                Trip trip = new Trip();
                trip.setTripName(tripName);
                trip.setTripTime(tripTime);
                boolean isValidDate = trip.setTripDate(tripDate);
                if (isValidDate) {
                    Toast.makeText(getContext(), "Valide Date",
                            Toast.LENGTH_LONG).show();

                } else {
                    Toast.makeText(getContext(), "Invalid Date and you must enter Valid Date ",
                            Toast.LENGTH_LONG).show();
                    edtTripDate.setText("");
                }


                trip.setTripStatues(Trip.STATUS_UP_COMING);
                trip.setStartPoint(startPoint);
                trip.setEndPoint(endPoint);
                if (!isTripFieldsEmpty()) {
                    if (spnTripType.getSelectedItem().toString().equalsIgnoreCase("one direction")) {
                        trip.setTripType(Trip.TYPE_ONE_DIRECTION);
                    } else {
                        trip.setTripType(Trip.TYPE_ROUND);
                    }
                    //addTrip
                    fireBaseData.addTrip(trip);
                    FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.mainContainerView, new UpcomingTripFragment());
                    fragmentTransaction.addToBackStack("NoteTrip");
                    fragmentTransaction.commit();
                    //Start Listning for BroadCast Reciever
                    tripDateDateObject = Utilities.convertStringToDateFormate(tripDate, tripTime);
                    startAlert(tripDateDateObject);
                } else {
                    Toast.makeText(getContext(), "You must Enter All Fields", Toast.LENGTH_LONG).show();
                }


            }
        });

        return view;
    }

    //Start Timer To broadCast Reciever
    public void startAlert(Date date) {
        Toast.makeText(getContext(), "your trip Starts At " + date, Toast.LENGTH_LONG).show();
        long millis = date.getTime();
        int i = 0;// Integer.parseInt(text.getText().toString());
        Intent intent = new Intent(getActivity(), MyReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                getActivity().getApplicationContext(), 234324243, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getActivity().getSystemService(ALARM_SERVICE);//getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, millis/*System.currentTimeMillis() + (i * 1000)*/, pendingIntent);
        Toast.makeText(getContext(), "current " + System.currentTimeMillis() + " seconds",
                Toast.LENGTH_LONG).show();
        Toast.makeText(getContext(), "Date " + millis + " seconds",
                Toast.LENGTH_LONG).show();

    }

    public boolean isTripFieldsEmpty() {
        if (Utilities.isEditTextEmpty(edtTripDate) ||
                Utilities.isEditTextEmpty(edtTripEndPoint) ||
                Utilities.isEditTextEmpty(edtTripTime) ||
                Utilities.isEditTextEmpty(edtTripName) ||
                Utilities.isEditTextEmpty(edtTripStartPoint))
            return true;
        else return false;
    }
}

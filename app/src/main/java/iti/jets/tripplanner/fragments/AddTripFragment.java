package iti.jets.tripplanner.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import android.widget.Filter;
import android.widget.Filterable;
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

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.utils.FireBaseData;

public class AddTripFragment extends Fragment implements AdapterView.OnItemClickListener {

    private static final String LOG_TAG = "Google Places Autocomplete";
    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
    private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyD12pWp4Qe-ta3WXsV5WT9cv8pKEHnaR9w";
    String tripName;
    String tripDate;
    String tripTime;
    int tripType;
    private String mAM_PM;
    private EditText addTripFragment_edtTripName;
    private EditText addTripFragment_edtTripDate;
    private EditText addTripFragment_edtTripTime;
    private Spinner addTripFragment_spnTripType;
    private Button addTripFragment_btnAddTrip;
    private int mYear, mMonth, mDay, mHour, mMinute;

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
                System.out.println(predsJsonArray.getJSONObject(i).getString("description"));
                System.out.println("============================================================");
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
        addTripFragment_edtTripName = view.findViewById(R.id.addTripFragment_edtTripName);
        //addTripFragment_edtTripStartPoint = view.findViewById(R.id.addTripFragment_edtTripStartPoint);
        //addTripFragment_edtTripEndPoint = view.findViewById(R.id.addTripFragment_edtTripEndPoint);
        addTripFragment_edtTripDate = view.findViewById(R.id.addTripFragment_edtTripDate);
        addTripFragment_edtTripTime = view.findViewById(R.id.addTripFragment_edtTripTime);
        addTripFragment_spnTripType = view.findViewById(R.id.addTripFragment_spnTripType);
        addTripFragment_btnAddTrip = view.findViewById(R.id.addTripFragment_btnAddTrip);

        ArrayAdapter<? extends String> adapter = new ArrayAdapter<>(getActivity(),
                android.R.layout.simple_spinner_item,
                getResources().getStringArray(R.array.addTripFragment_spnTripType));

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        addTripFragment_spnTripType.setAdapter(adapter);
        //Trip_Type
        if (addTripFragment_spnTripType.getSelectedItem().toString().equalsIgnoreCase("one dirction")) {
            tripType = 1;
        } else {
            tripType = 2;
        }

        //Trip_Date
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
                                tripDate = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }

        });

        final FireBaseData fireBaseData = new FireBaseData(getActivity());

        //Trip_Time
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
                                tripTime = hourOfDay + ":" + minute + " " + mAM_PM;
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
            }

        });

        AutoCompleteTextView addTripFragment_edtTripStartPoint = view.findViewById(R.id.addTripFragment_edtTripStartPoint);
        AutoCompleteTextView addTripFragment_edtTripEndPoint = view.findViewById(R.id.addTripFragment_edtTripEndPoint);

        addTripFragment_edtTripStartPoint.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), R.layout.list_item));
        addTripFragment_edtTripStartPoint.setOnItemClickListener(this);
        addTripFragment_edtTripEndPoint.setAdapter(new GooglePlacesAutocompleteAdapter(getContext(), R.layout.list_item));
        addTripFragment_edtTripEndPoint.setOnItemClickListener(this);

        addTripFragment_btnAddTrip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fireBaseData.addTrip(tripName, tripDate, tripTime, "", "", tripType, 1);
                Fragment fragment = new AddNoteFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.viewContainerFragment, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        String str = (String) adapterView.getItemAtPosition(position);
        Toast.makeText(getContext(), str, Toast.LENGTH_SHORT).show();
    }

    class GooglePlacesAutocompleteAdapter extends ArrayAdapter implements Filterable {
        private ArrayList resultList;

        public GooglePlacesAutocompleteAdapter(Context context, int textViewResourceId) {
            super(context, textViewResourceId);
        }

        @Override
        public int getCount() {
            return resultList.size();
        }

        @Override
        public String getItem(int index) {
            return (String) resultList.get(index);
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {
                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults filterResults = new FilterResults();
                    if (constraint != null) {
                        // Retrieve the autocomplete results.
                        resultList = autocomplete(constraint.toString());

                        // Assign the data to the FilterResults
                        filterResults.values = resultList;

                        filterResults.count = resultList.size();
                    }
                    return filterResults;
                }

                @Override
                protected void publishResults(CharSequence constraint, FilterResults results) {
                    if (results != null && results.count > 0) {
                        notifyDataSetChanged();
                    } else {
                        notifyDataSetInvalidated();
                    }
                }
            };
            return filter;
        }
    }
}

package iti.jets.tripplanner.fragments;


import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import iti.jets.tripplanner.NavigatinDrawerActivity;
import iti.jets.tripplanner.R;
import iti.jets.tripplanner.pojos.Trip;
import iti.jets.tripplanner.utils.VolleyConnection;

/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryMapFragment extends Fragment implements OnMapReadyCallback {

    //should replaced with  array of history trips
    Context context;
    SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private ProgressDialog mProgressDialog;
    private int mRequests;
    private RequestQueue mRequestQueue;
    private List<Trip> trips;
    NavigatinDrawerActivity navigatinDrawerActivity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_map, container, false);

        trips = new ArrayList<>();
        mRequestQueue = VolleyConnection.getRequestQueue(context);
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null) {
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment);
        }
        mapFragment.getMapAsync(this);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mapFragment.getMapAsync(HistoryMapFragment.this);
            }
        }, 0);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        navigatinDrawerActivity = (NavigatinDrawerActivity) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        Query query = navigatinDrawerActivity.getDatabaseReference().child("Trips").orderByKey()
                .equalTo(navigatinDrawerActivity.getUserId());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> iterator = dataSnapshot.getChildren().iterator();
                if (iterator.hasNext()) {
                    DataSnapshot next = iterator.next();
                    for (DataSnapshot dataSnapshot1 : next.getChildren()) {
                        Trip trip = dataSnapshot1.getValue(Trip.class);
                        if (trip != null && trip.getTripStatues() == Trip.STATUS_DONE) {
                            Log.i("TAG", "onDataChange: in done");
                            trips.add(trip);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error", databaseError.toString());
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        for (final Trip trip : trips) {
            mRequestQueue.add(
                    new JsonObjectRequest(
                            "https://maps.googleapis.com/maps/api/directions/json?origin="

                                    + trip.getStartPointlatitude() + "," + trip.getStartPointlongitude()
                                    + "&destination="
                                    + trip.getEndPointlatitude() + "," + trip.getEndPointlongitude()
                                    + getString(R.string.google_key_display), null,
                            new Response.Listener<JSONObject>() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    int colors[] = {Color.RED, Color.BLACK, Color.MAGENTA, Color.DKGRAY, Color.GRAY, Color.MAGENTA, Color.YELLOW, Color.BLUE};
                                    Random rand = new Random();
                                    int randomColor = colors[rand.nextInt(colors.length)];
                                    drawPath(response, mMap, randomColor);
                                    LatLng latLng = new LatLng(trip.getStartPointlatitude(), trip.getStartPointlongitude());
                                    MarkerOptions markerOptions = new MarkerOptions();
                                    markerOptions.title("Start point");
                                    markerOptions.position(latLng);
                                    LatLng latLngEnd = new LatLng(trip.getEndPointlatitude(), trip.getEndPointlongitude());
                                    MarkerOptions markerOptionsEnd = new MarkerOptions();
                                    markerOptionsEnd.title("End point");
                                    markerOptionsEnd.position(latLngEnd);
                                    mMap.addMarker(markerOptions);
                                    mMap.addMarker(markerOptionsEnd);
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Log.e("VolleyError", error.getCause() + "");
                                }
                            }
                    )
            );
            mRequests++;
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(trip.getStartPointlatitude(), trip.getStartPointlongitude()), 10f));
        }

        mRequestQueue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                mRequests--;
                if (mRequests == 0) {
                    if (mProgressDialog != null)
                        mProgressDialog.dismiss();
                }
            }
        });

    }


    public void drawPath(JSONObject result, GoogleMap Map, int color) {

        try {
            JSONArray routeArray = result.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Map.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(10)
                    .color(color)
                    .geodesic(true)
            );
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }
        return poly;
    }
}

package iti.jets.tripplanner.utils;

import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class VolleyConnection {

    private static RequestQueue mRequestQueue;

    private VolleyConnection() {
    }

    public static RequestQueue getRequestQueue(Context context) {
        if (mRequestQueue == null) {
            synchronized (VolleyConnection.class) {

                if (mRequestQueue == null)
                    mRequestQueue = Volley.newRequestQueue(context);
            }
        }

        return mRequestQueue;
    }
}



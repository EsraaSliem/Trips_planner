package iti.jets.tripplanner.utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import iti.jets.tripplanner.R;
import iti.jets.tripplanner.adapters.HeadNoteAdapter;
import iti.jets.tripplanner.pojos.Note;

public class TripHeadService extends Service {
    WindowManager.LayoutParams params;
    private WindowManager mWindowManager;
    private View mTripHeadView;
    boolean isExpand = false;
    String tripId;
    RecyclerView notesRecyclerView;
    CardView notesCardView;
    List<Note> notes;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int i = super.onStartCommand(intent, flags, startId);
        tripId = intent.getStringExtra(Utilities.TRIP_ID);
        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mRefDatabase = mDatabase.getReference();

        notes = new ArrayList<>();
        HeadNoteAdapter adapter = new HeadNoteAdapter(getApplicationContext(), notes);
        notesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        notesRecyclerView.setAdapter(adapter);

        Query query = mRefDatabase.child("Notes").child(tripId);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Note note = snapshot.getValue(Note.class);
                    note.setTripId(tripId);
                    notes.add(note);
                    adapter.notifyDataSetChanged();
                    adapter.notifyItemInserted(notes.size());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        mTripHeadView.findViewById(R.id.tripHead_btnClose).setOnClickListener(v -> {
            stopService(intent);
            mWindowManager.removeView(mTripHeadView);
            mTripHeadView = null;
        });


//        Drag and move floating view using user's touch action.
        mTripHeadView.findViewById(R.id.tripHead_img).setOnTouchListener(new View.OnTouchListener() {
            long time_start = 0, time_end = 0;
            private int initialX;
            private int initialY;
            private float initialTouchX;
            private float initialTouchY;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        //remember the initial position.
                        initialX = params.x;
                        initialY = params.y;
                        //get the touch location
                        initialTouchX = event.getRawX();
                        initialTouchY = event.getRawY();
                        time_start = System.currentTimeMillis();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (int) (event.getRawX() - initialTouchX - 60);
                        params.y = initialY + (int) (event.getRawY() - initialTouchY - 60);
                        //Update the layout with new X & Y coordinate
                        break;
                    case MotionEvent.ACTION_UP:
                        DisplayMetrics metrics = new DisplayMetrics();
                        mWindowManager.getDefaultDisplay().getMetrics(metrics);
                        int xInches = metrics.widthPixels;
                        if (params.x > xInches / 2) {
                            params.x = xInches - mTripHeadView.getWidth() - 10;
                        } else {
                            params.x = 0;
                        }
                        time_end = System.currentTimeMillis();
                        if (time_end - time_start < 300) {
                            if (!isExpand) {
                                if (notes.size() > 0) {
                                    notesCardView.setVisibility(View.VISIBLE);
                                } else {
                                    Toast.makeText(getApplicationContext(), "no notes added", Toast.LENGTH_SHORT).show();
                                }
                                isExpand = !isExpand;
                            } else {
                                notesCardView.setVisibility(View.GONE);
                                isExpand = !isExpand;
                            }
                        }
                        break;
                    default:
                        return false;
                }
                mWindowManager.updateViewLayout(mTripHeadView, params);
                return true;
            }
        });

        return i;

    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTripHeadView = LayoutInflater.from(this).inflate(R.layout.trip_head, null);
        notesRecyclerView = mTripHeadView.findViewById(R.id.tripHead_recyclerView);
        notesCardView = mTripHeadView.findViewById(R.id.tripHead_cardView);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        } else {
            params = new WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                            | WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                            | WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH
                            | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    PixelFormat.TRANSLUCENT);
        }
        params.gravity = Gravity.START | Gravity.TOP;
        params.x = 0;
        params.y = 100;
        mWindowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        mWindowManager.addView(mTripHeadView, params);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTripHeadView != null) mWindowManager.removeView(mTripHeadView);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
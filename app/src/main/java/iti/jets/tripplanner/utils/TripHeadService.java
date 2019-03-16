package iti.jets.tripplanner.utils;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import iti.jets.tripplanner.R;

public class TripHeadService extends Service {
    WindowManager.LayoutParams params;
    private WindowManager mWindowManager;
    private View mTripHeadView;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    @Override
    public void onCreate() {
        super.onCreate();
        //Inflate the chat head layout we created
        mTripHeadView = LayoutInflater.from(this).inflate(R.layout.trip_head, null);
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

//        Drag and move floating view using user's touch action.
        mTripHeadView.findViewById(R.id.tripHead_img).setOnTouchListener(new View.OnTouchListener() {
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
                            params.x = xInches - v.getWidth();
                        } else {
                            params.x = 0;
                        }
                        break;
                    default:
                        return false;
                }
                mWindowManager.updateViewLayout(mTripHeadView, params);
                return true;
            }
        });
        mTripHeadView.findViewById(R.id.tripHead_btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripHeadService.this.stopSelf();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTripHeadView != null) mWindowManager.removeView(mTripHeadView);
    }
}
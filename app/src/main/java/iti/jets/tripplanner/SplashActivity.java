package iti.jets.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.FirebaseDatabase;

import iti.jets.tripplanner.adapters.SlideAdapter;

public class SplashActivity extends AppCompatActivity {

    SharedPreferences appPrefrences;
    Boolean logined = false;
    private ViewPager mSliderViewPager;
    private LinearLayout maDotsLayout;
    private SlideAdapter slideAdapter;
    private TextView[] mDots;
    private Button bSlider;
    private int mcurrent;
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            mcurrent = position;
            if (position == mDots.length - 1) {

                bSlider.setText("Get start");
            } else {

                bSlider.setText("SKIP");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appPrefrences = SplashActivity.this.getSharedPreferences("AppPrefrences", Context.MODE_PRIVATE);
        logined = appPrefrences.getBoolean("logined", false);

        mSliderViewPager = findViewById(R.id.splshActivity_viewPagerSlider);
        maDotsLayout = findViewById(R.id.splshActivity_dots);
        bSlider = findViewById(R.id.splashActivity_btnSplash);


        bSlider.setOnClickListener(v -> {
            Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            startActivity(intent);
            finish();
        });
        slideAdapter = new SlideAdapter(this);
        mSliderViewPager.setAdapter(slideAdapter);
        addDotsIndicator(0);
        mSliderViewPager.addOnPageChangeListener(viewListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (logined) {
            Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
            startActivity(intent);
            finish();
        } else {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        }
    }


    public void addDotsIndicator(int position) {
        mDots = new TextView[3];
        maDotsLayout.removeAllViews();
        for (int i = 0; i < mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorGray));
            maDotsLayout.addView(mDots[i]);

        }
        if (mDots.length > 0) {
            mDots[position].setTextColor(getResources().getColor(R.color.colorFocus));

        }
    }

}

package iti.jets.tripplanner;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import iti.jets.tripplanner.adapters.SlideAdapter;

public class SplashActivity extends AppCompatActivity {

    private ViewPager mSliderViewPager;
    private LinearLayout maDotsLayout;
    private SlideAdapter slideAdapter;
    private TextView[] mDots;
    private Button bSlider;
    private int mcurrent;
    SharedPreferences appPrefrences;
    Boolean logined=false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        appPrefrences= SplashActivity.this.getSharedPreferences("AppPrefrences", Context.MODE_PRIVATE);
        logined=appPrefrences.getBoolean("logined",false);

        mSliderViewPager =(ViewPager) findViewById(R.id.splshActivity_viewPagerSlider);
        maDotsLayout=(LinearLayout)findViewById(R.id.splshActivity_dots);
        bSlider=(Button)findViewById(R.id.splashActivity_btnSplash);



        bSlider.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SplashActivity.this ,AuthenticationActivity.class);
                startActivity(intent);
            }
        });
        slideAdapter=new SlideAdapter(this);
        mSliderViewPager.setAdapter(slideAdapter);
        addDotsIndicator(0);
        mSliderViewPager.addOnPageChangeListener(viewListener);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(logined){
            Intent intent = new Intent(SplashActivity.this, AuthenticationActivity.class);
            startActivity(intent);
        }
    }

    public void addDotsIndicator(int position){
        mDots=new TextView[3];
        maDotsLayout.removeAllViews();
        for (int i=0;i<mDots.length;i++){
            mDots[i]=new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setTextColor(getResources().getColor(R.color.colorGray));
            maDotsLayout.addView(mDots[i]);

        }
        if(mDots.length>0){
            mDots[position].setTextColor(getResources().getColor(R.color.colorFocus));

        }
    }
    ViewPager.OnPageChangeListener viewListener=new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

            mcurrent=position;
            if(position==mDots.length-1){

                bSlider.setText("Get start");
            }
            else {

                bSlider.setText("SKIP");
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

}

package iti.jets.tripplanner.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SlideAdapter extends PagerAdapter {
    Context context;
    LayoutInflater layoutInflater;
    public SlideAdapter(Context context){
        this.context=context;
    }
    public int [] slide_image={
            iti.jets.tripplanner.R.drawable.splash1,
            iti.jets.tripplanner.R.drawable.splash2,
            iti.jets.tripplanner.R.drawable.splash3,


    };
    public String [] slide_desc={
            "Never miss your trip",
            "find the way for your trip",
            "write your notes on your trip"
    };
    @Override
    public int getCount() {
        return slide_image.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view ==(RelativeLayout)object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater=(LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view=layoutInflater.inflate(iti.jets.tripplanner.R.layout.slide,container,false);
        ImageView slideImageView=(ImageView) view.findViewById(iti.jets.tripplanner.R.id.slideImage);
        TextView textView=(TextView) view.findViewById(iti.jets.tripplanner.R.id.slideText);
        slideImageView.setImageResource(slide_image[position]);
        textView.setText(slide_desc[position]);

        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout)object);
    }
}

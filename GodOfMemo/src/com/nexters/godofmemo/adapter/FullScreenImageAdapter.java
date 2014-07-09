package com.nexters.godofmemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nexters.godofmemo.R;

public class FullScreenImageAdapter extends PagerAdapter {
	private int[] mImages;
	
	
	private Activity activity;
    private LayoutInflater inflater;
 
    // constructor
    public FullScreenImageAdapter(Activity tempActivity) {
        this.activity = tempActivity;
        
        mImages = new int[] {
                R.drawable.tutorial1,
                R.drawable.tutorial2,
                R.drawable.tutorial3,
                R.drawable.tutorial4,
                R.drawable.tutorial5,
            };
    }
 
    @Override
    public int getCount() {
        return mImages.length;
    }
 
    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }
     
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        ImageView imgDisplay;
        Button btnClose;
  
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image, container,
                false);
  
        imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
        btnClose = (Button) viewLayout.findViewById(R.id.btnClose);
        
        imgDisplay.setImageResource(mImages[position]);
        
         
        // close button click event
        btnClose.setOnClickListener(new View.OnClickListener() {            
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
  
        ((ViewPager) container).addView(viewLayout);
  
        return viewLayout;
    }
     
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);
  
    }

}

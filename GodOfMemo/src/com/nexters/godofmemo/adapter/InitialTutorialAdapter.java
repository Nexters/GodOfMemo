package com.nexters.godofmemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nexters.godofmemo.MainActivity;
import com.nexters.godofmemo.R;
import com.nexters.godofmemo.SplashActivity;

public class InitialTutorialAdapter extends PagerAdapter {
	private int[] mImages;

	private Activity activity;
	private LayoutInflater inflater;

	// constructor
	public InitialTutorialAdapter(Activity tempActivity) {
		this.activity = tempActivity;

		mImages = new int[] { R.drawable.howtouse_1, R.drawable.howtouse_2,
				R.drawable.howtouse_3, R.drawable.splash_seen };
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
		Log.i("memo", String.valueOf(position));
		Log.i("memo", String.valueOf(mImages.length));
		// start main activity on last page.
		if (position == mImages.length - 1) {
			Intent i = new Intent(activity, MainActivity.class);
			activity.startActivity(i);

			// close this activity
			activity.finish();

		}

		ImageView imgDisplay;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_initial_tutorial,
				container, false);

		imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);

		// imgDisplay.setImageResource(mImages[position]);
		Bitmap bm = BitmapFactory.decodeResource(this.activity.getResources(),
				mImages[position]);
		imgDisplay.setImageBitmap(bm);

		((ViewPager) container).addView(viewLayout, 0);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}

}

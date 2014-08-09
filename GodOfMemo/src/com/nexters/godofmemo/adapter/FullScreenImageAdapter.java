package com.nexters.godofmemo.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nexters.godofmemo.BackupActivity;
import com.nexters.godofmemo.R;

public class FullScreenImageAdapter extends PagerAdapter {
	private int[] mImages;

	private Activity activity;
	private LayoutInflater inflater;

	// constructor
	public FullScreenImageAdapter(Activity tempActivity) {
		this.activity = tempActivity;

		mImages = new int[] { R.drawable.tutorial1, R.drawable.tutorial2,
				R.drawable.tutorial3, R.drawable.tutorial4,
				R.drawable.tutorial5, };
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
		RelativeLayout layout;
		Button btnSkip;
		// tutorial5
		Button btnFacebook;
		Button btnDone;

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image,
				container, false);
		
		layout = (RelativeLayout) viewLayout.findViewById(R.layout.layout_fullscreen_image);
		imgDisplay = (ImageView) viewLayout.findViewById(R.id.imgDisplay);
		btnSkip = (Button) viewLayout.findViewById(R.id.btnSkip);
		btnDone = (Button) viewLayout.findViewById(R.id.btnDone);
		btnFacebook = (Button) viewLayout.findViewById(R.id.btnFacebook);
		

		//View viewLayout5 = inflater.inflate(R.layout.layout_tutorial5, container, false);
		
		// Tutorial5
		//btnFacebook = (Button) viewLayout5.findViewById(R.id.btnFacebook);
		//btnDone = (Button) viewLayout5.findViewById(R.id.btnDone);


		//imgDisplay.setImageResource(mImages[position]);
		BitmapFactory.Options option = new BitmapFactory.Options();

		option.inSampleSize = 2;

		option.inPurgeable = true;

		option.inDither = true;
		
		Bitmap bm = BitmapFactory.decodeResource(this.activity.getResources(), mImages[position], option);
		imgDisplay.setImageBitmap(bm);
		
		//layout.removeAllViews();
		//layout.addView(View.inflate(activity.getBaseContext(), R.layout.layout_tutorial5, null));

		// skip button click event
			btnSkip.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					// TODO skip 으로 바꾸기
					//activity.finish();
					
					Intent i = new Intent(activity, BackupActivity.class);
					activity.startActivity(i);
				}
			});
			
			btnDone.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					activity.finish();
				}
			});
			
			btnFacebook.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Uri webpage = Uri
							.parse("http://www.facebook.com/subidubam");
					Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
					activity.startActivity(webIntent);
				}
			});
			
			/**
		{
			layout.removeAllViews();
			layout.addView(View.inflate(activity.getBaseContext(), R.layout.layout_tutorial5, null));

			// Have a Facebook link and done button

			// facebook button click event
			btnFacebook.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					Uri webpage = Uri
							.parse("http://www.facebook.com/subidubam");
					Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
					activity.startActivity(webIntent);

				}
			});

			// Done button click event
			btnDone.setOnClickListener(new View.OnClickListener() {

				@Override
				public void onClick(View v) {
					activity.finish();
				}
			});
		} **/

		((ViewPager) container).addView(viewLayout, 0);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}

}

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
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.nexters.godofmemo.BackupActivity;
import com.nexters.godofmemo.CreditActivity;
import com.nexters.godofmemo.R;

public class FullScreenImageAdapter extends PagerAdapter implements
		OnClickListener {
	private int[] mImages;

	private Activity activity;
	private LayoutInflater inflater;

	// ui
	private ImageView imgDisplay;
	private ImageView btnDone;
	private ImageView btnCredit;
	private ImageView btnFacebook;
	private ImageView btnBackup;

	// constructor
	public FullScreenImageAdapter(Activity tempActivity) {
		this.activity = tempActivity;

		mImages = new int[] { R.drawable.id_1, R.drawable.id_2,
				R.drawable.id_3, R.drawable.id_4 };
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

		inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View viewLayout = inflater.inflate(R.layout.layout_fullscreen_image,
				container, false);

		imgDisplay = (ImageView) viewLayout.findViewById(R.id.img_display);
		btnDone = (ImageView) viewLayout.findViewById(R.id.btn_done);
		btnCredit = (ImageView) viewLayout.findViewById(R.id.btn_credit);
		btnFacebook = (ImageView) viewLayout.findViewById(R.id.btn_facebook);
		btnBackup = (ImageView) viewLayout.findViewById(R.id.btn_backup);

		// set background image.
		BitmapFactory.Options option = new BitmapFactory.Options();
		option.inSampleSize = 1;
		option.inPurgeable = true;
		option.inDither = true;

		Bitmap bm = BitmapFactory.decodeResource(this.activity.getResources(),
				mImages[position], option);
		imgDisplay.setImageBitmap(bm);

		// button event.
		btnDone.setOnClickListener(this);
		btnCredit.setOnClickListener(this);
		btnFacebook.setOnClickListener(this);
		btnBackup.setOnClickListener(this);

		// add view to viewpager.
		((ViewPager) container).addView(viewLayout, 0);

		return viewLayout;
	}

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		((ViewPager) container).removeView((RelativeLayout) object);

	}

	/**
	 * click event.
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_done:
			// done!
			activity.finish();
			break;
		case R.id.btn_credit:
			// credit
			Intent ci = new Intent(activity, CreditActivity.class);
			activity.startActivity(ci);
			break;
		case R.id.btn_facebook:
			// facebook
			Uri webpage = Uri.parse("http://www.facebook.com/mindpaper");
			Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
			activity.startActivity(webIntent);
			break;
		case R.id.btn_backup:
			// backup
			Intent bi = new Intent(activity, BackupActivity.class);
			activity.startActivity(bi);
			break;

		}

	}
}

package com.nexters.mindpaper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nexters.mindpaper.adapter.FullScreenImageAdapter;

@SuppressLint("NewApi")
public class TutorialActivity extends FragmentActivity {
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	//private ArrayList<String> images;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getActionBar().hide();
	    
		setContentView(R.layout.activity_tutorial);

		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		adapter = new FullScreenImageAdapter(TutorialActivity.this);

		viewPager.setAdapter(adapter);
	}

}
package com.nexters.mindpaper;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nexters.mindpaper.adapter.InitialTutorialAdapter;

@SuppressLint("NewApi")
public class InitialTutorialActivity extends FragmentActivity {
	private InitialTutorialAdapter adapter;
	private ViewPager viewPager;
	//private ArrayList<String> images;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    getActionBar().hide();
	    
		setContentView(R.layout.activity_tutorial);

		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		adapter = new InitialTutorialAdapter(InitialTutorialActivity.this);

		viewPager.setAdapter(adapter);
	}

}
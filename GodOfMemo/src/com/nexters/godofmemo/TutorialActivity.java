package com.nexters.godofmemo;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.nexters.godofmemo.adapter.FullScreenImageAdapter;
import com.nexters.godofmemo.util.Font;

@SuppressLint("NewApi")
public class TutorialActivity extends FragmentActivity {
	private FullScreenImageAdapter adapter;
	private ViewPager viewPager;
	//private ArrayList<String> images;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		// 폰트 초기화
		Font.setTf(Typeface.createFromAsset(getAssets(), "fonts/nanum.ttf"));
		
		//커스톰 액션바 구현.
		getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getActionBar().setCustomView(R.layout.actionbar_tutorial);
		
		viewPager = (ViewPager) findViewById(R.id.pager);
		
		adapter = new FullScreenImageAdapter(TutorialActivity.this);

		viewPager.setAdapter(adapter);
	}

}
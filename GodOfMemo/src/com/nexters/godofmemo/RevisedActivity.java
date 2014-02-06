package com.nexters.godofmemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class RevisedActivity extends Activity {
	TextView memoContentView;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_revised);
		
		memoContentView = (TextView) findViewById(R.id.memoContentView);
		memoContentView.setText("success");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.revised, menu);
		return true;
	}

}

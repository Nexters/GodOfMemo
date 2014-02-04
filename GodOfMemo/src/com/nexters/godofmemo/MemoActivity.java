package com.nexters.godofmemo;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.Toast;

public class MemoActivity extends ActionBarActivity {

	EditText short_et;
	EditText detailed_et;

	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_memo);

		short_et = (EditText) findViewById(R.id.short_text);
		detailed_et = (EditText) findViewById(R.id.detailed_text);
		
		
		// focus on/off
		detailed_et.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
			    if(hasFocus){
			        detailed_et.setAlpha(1.0f);
			        detailed_et.setText("");
			    }else {
			        detailed_et.setAlpha(0.3f);
			        detailed_et.setText(R.string.detailed_message);
			    }
			   }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.memo, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_write_finish:
			makeText();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@SuppressLint("NewApi")
	public void makeText() {
		Intent intent = getIntent();
		
		String short_txt = short_et.getText().toString();
		intent.putExtra("short_txt", short_txt);
		
		if(detailed_et.getAlpha() == 1.0f){
			String detailed_txt = detailed_et.getText().toString();
			intent.putExtra("detailed_txt", detailed_txt);
		}
		
		setResult(RESULT_OK, intent);
		finish();
	}

}

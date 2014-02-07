package com.nexters.godofmemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;


public class MemoActivity extends ActionBarActivity {

	EditText short_et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_memo);

		short_et = (EditText) findViewById(R.id.short_text);
		
		Intent intent = getIntent();
		String memoContent = intent.getStringExtra("selectedMemoContent");
		if(memoContent!=null){
			short_et.setText(memoContent);
		}
		
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
	
	public void makeText() {
		Intent intent = getIntent();
		
		String short_txt = short_et.getText().toString();
		intent.putExtra("short_txt", short_txt);
		
		setResult(RESULT_OK, intent);
		finish();
	}

}

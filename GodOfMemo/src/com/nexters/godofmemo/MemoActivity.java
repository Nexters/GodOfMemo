package com.nexters.godofmemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;

public class MemoActivity extends ActionBarActivity {

	EditText et;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		setContentView(R.layout.activity_memo);

		et = (EditText) findViewById(R.id.input_text);
		findViewById(R.id.okBtn).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				makeText();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	public void makeText() {
		Intent intent = getIntent();
		String txt = et.getText().toString();
		intent.putExtra("txt", txt);
		setResult(RESULT_OK, intent);
		finish();
	}

}

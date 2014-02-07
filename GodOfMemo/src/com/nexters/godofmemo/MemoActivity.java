package com.nexters.godofmemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;


public class MemoActivity extends ActionBarActivity implements OnClickListener{

	EditText short_et;
	Intent intent; 
	
	private final int NONE = 0;
	private final int CREATE = 1;
	private final int UPDATE = 2;
	private int write_mode = NONE;
	
	private final int BACK = 3;
	
	private String memoContent;
	private String memoId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.actionbar_memo_edit);
		
		setContentView(R.layout.activity_memo);

		short_et = (EditText) findViewById(R.id.short_text);
		
		intent = getIntent();
		memoContent = intent.getStringExtra("selectedMemoContent");
		memoId = intent.getStringExtra("selectedMemoId");

		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_finish).setOnClickListener(this);
		findViewById(R.id.trash_can).setOnClickListener(this);
		
		if(memoContent==null){
			write_mode = CREATE;
		}else{
			write_mode = UPDATE;
			short_et.setText(memoContent);
		}
	}
	
	public void makeText() {
		String short_txt = short_et.getText().toString();
		intent.putExtra("short_txt", short_txt);
		setResult(RESULT_OK, intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_finish:
			makeText();
			intent.putExtra("selectedMemoId", memoId);
			finish();
	        break;
		case R.id.btn_back:
			intent.putExtra("checkBack", BACK);
			finish();
			break;
		}
	}

}

package com.nexters.godofmemo;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;


public class MemoActivity extends ActionBarActivity implements OnClickListener{

	EditText short_et;
	Intent intent; 
	
	private final int BACK = 3;
	
	private String memoContent;
	private String memoId;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//커스텀 액션바 
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.actionbar_memo_edit);
		
		setContentView(R.layout.activity_memo);

		short_et = (EditText) findViewById(R.id.short_text);
		
		intent = getIntent();
		memoContent = intent.getStringExtra("selectedMemoContent");
		memoId = intent.getStringExtra("selectedMemoId");
		//System.out.println("memoId: "+ memoId);
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_finish).setOnClickListener(this);
		ImageView trash_can =  (ImageView)findViewById(R.id.trash_can);
		trash_can.setOnClickListener(this);
		
		if(memoContent==null){
			trash_can.setVisibility(View.GONE);
		}else{
			
			short_et.setText(memoContent);
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_finish:
			makeText();
	        break;
		case R.id.btn_back:
			moveToBack();
			break;
		case R.id.trash_can:
			deleteMemo();
			break;
		}
	}
	
	private void makeText() {
		String short_txt = short_et.getText().toString();
		intent.putExtra("short_txt", short_txt);
		// if this case is when you tab create button, memoId's value is null.
		// and maybe you don't use it.
		intent.putExtra("selectedMemoId", memoId);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void deleteMemo(){
		intent.putExtra("selectedMemoId", memoId);
		intent.putExtra("delete", true);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void moveToBack(){
		intent.putExtra("checkBack", BACK);
		setResult(RESULT_OK, intent);
		finish();
	}
}

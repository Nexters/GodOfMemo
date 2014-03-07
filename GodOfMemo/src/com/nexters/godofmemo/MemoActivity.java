package com.nexters.godofmemo;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.nexters.godofmemo.dao.MemoDAO;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.util.Util;


public class MemoActivity extends ActionBarActivity implements OnClickListener{

	EditText short_et;
	Intent intent; 
	
	private final int BACK = 3;
	
	private String memoContent;
	private String memoId;
	private int memoColor;
	
	//memo background
	private View memoBg;
	private View background;

	//
	int dWidth = 0;
	int dHeight = 0;
	//
	private boolean isLong = false;
	//
	private int shortMemoSize = 25;
	private int longMemoSize = 45;
	//
	private int textDivisionSize = 20;
	//
	private TextView memoTimeTextView;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		
		//커스텀 액션바 
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.actionbar_memo_edit);
		
		setContentView(R.layout.activity_memo);
		
		//memo size
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		dWidth = metrics.widthPixels;
		dHeight= metrics.heightPixels;
		
		//width
		dWidth = dWidth * 8 / 10;
		

		//memo backgroudn control
		memoBg = findViewById(R.id.memo_img_background);
		setMemoBgSize(dHeight*shortMemoSize/100);
		
		short_et = (EditText) findViewById(R.id.short_text);
		//텍스트 길이에 따른 배경 변화 로직 추가.
		onTextChanged();
		
		//메모 색깔 버튼 선택 이벤트
		findViewById(R.id.memo_color_select_red).setOnClickListener(this);
		findViewById(R.id.memo_color_select_blue).setOnClickListener(this);
		findViewById(R.id.memo_color_select_yellow).setOnClickListener(this);
		
		//memo time
		memoTimeTextView = (TextView) findViewById(R.id.memo_time);
		//background touchevent
		background = findViewById(R.id.memo_activiy_background);
		background.setOnClickListener(this);
		
		intent = getIntent();
		memoContent = intent.getStringExtra("selectedMemoContent");
		memoColor = intent.getIntExtra("selectedMemoColor",Memo.MEMO_COLOR_BLUE);
		memoId = intent.getStringExtra("selectedMemoId");
	
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_finish).setOnClickListener(this);
		ImageView trash_can =  (ImageView)findViewById(R.id.trash_can);
		trash_can.setOnClickListener(this);
		
		//입력, 수정모드에 따라 삭제버튼을 보이거나 숨긴다.
		if(memoId==null){
			//입력모드
			trash_can.setVisibility(View.GONE);
			String memoDate = Util.getDate();
			String memoTime = Util.getTime();
			memoTimeTextView.setText(memoDate +" "+ memoTime);
		}else{
			//수정모드
			short_et.setText(memoContent);
			MemoDAO dao = new MemoDAO(getApplicationContext());
			Memo memo = dao.getMemoInfo(memoId);
			memoTimeTextView.setText(memo.getMemoDate() +" "+ memo.getMemoTime());
		}
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_finish:
			createMemo();
	        break;
		case R.id.btn_back:
			moveToBack();
			break;
		case R.id.trash_can:
			deleteMemo();
			break;
		case R.id.memo_color_select_red:
			findViewById(R.id.memo_img_background).setBackgroundResource(R.drawable.memo_red);
			findViewById(R.id.memo_color_selection_area).setBackgroundResource(R.drawable.writememo_colorselect_red);
			memoColor = Memo.MEMO_COLOR_RED;
			break;

		case R.id.memo_color_select_blue:
			findViewById(R.id.memo_img_background).setBackgroundResource(R.drawable.memo_blue);
			findViewById(R.id.memo_color_selection_area).setBackgroundResource(R.drawable.writememo_colorselect_blue);
			memoColor = Memo.MEMO_COLOR_BLUE;
			break;

		case R.id.memo_color_select_yellow:
			findViewById(R.id.memo_img_background).setBackgroundResource(R.drawable.memo_yellow);
			findViewById(R.id.memo_color_selection_area).setBackgroundResource(R.drawable.writememo_colorselect_yellow);
			memoColor = Memo.MEMO_COLOR_YELLOW;
			break;
		case R.id.memo_activiy_background:
			InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
		    inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
			break;
		}
	}
	
	private void createMemo() {
		String short_txt = short_et.getText().toString();
		intent.putExtra("short_txt", short_txt);
		// if this case is when you tab create button, memoId's value is null.
		// and maybe you don't use it.
		intent.putExtra("selectedMemoId", memoId);
		intent.putExtra("selectedMemoColor", memoColor);
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
	
	private void setMemoBgSize(int height){
		RelativeLayout.LayoutParams lp =  new RelativeLayout.LayoutParams(dWidth,
				height);
		lp.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
		lp.addRule(RelativeLayout.BELOW, R.id.memo_color_selection_area);
		lp.topMargin = dWidth * 5 / 100;
		memoBg.setLayoutParams(lp);
		
	}
	
	private void onTextChanged(){
		short_et.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				int textLength = s.length();

				if (textLength > textDivisionSize) {
					// 긴거
					if (!isLong) {
						setMemoBgSize(dHeight*longMemoSize/100);
						isLong = true;
					}
				} else {
					// 짧은거
					if (isLong) {
						setMemoBgSize(dHeight*shortMemoSize/100);
						isLong = false;
					}

				}
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}
		});
	}
	
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//입력, 수정모드에 따라 삭제버튼을 보이거나 숨긴다.
		if(memoId==null){
			((TextView)findViewById(R.id.memoBoardTitle)).setText("메모 입력");
		}else{
			((TextView)findViewById(R.id.memoBoardTitle)).setText("메모 수정");
		}
		/*
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/telegrafico.ttf");
		((TextView)findViewById(R.id.memoBoardTitle)).setTypeface(tf);*/
	}
	
}

package com.nexters.godofmemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nexters.godofmemo.dao.MemoDAO;
import com.nexters.godofmemo.data.ColorDB;
import com.nexters.godofmemo.object.Color;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.util.Util;
import com.nexters.godofmemo.view.ColorSelectionView;

public class MemoActivity extends ActionBarActivity implements OnClickListener {

	private EditText memoContentET;
	private EditText memoTitleET;
	Intent intent;

	// memo color picker
	LinearLayout memoColorPicker;
	LinearLayout bgColorPicker;
	List<ColorSelectionView> memoColorSelectionViewList = new ArrayList<ColorSelectionView>();
	List<ColorSelectionView> bgColorSelectionViewList = new ArrayList<ColorSelectionView>();
	

	private final int BACK = 3;

	private String memoTitle;
	private String memoContent;
	private String memoId;
	private int memoColor;

	// color
	private int r;
	private int g;
	private int b;

	// memo background
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

		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_memo_edit);

		setContentView(R.layout.activity_memo);

		// memo size
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		dWidth = metrics.widthPixels;
		dHeight = metrics.heightPixels;

		// width
		dWidth = dWidth * 8 / 10;

		// memo backgroudn control

		memoTitleET = (EditText) findViewById(R.id.memo_title);
		memoContentET = (EditText) findViewById(R.id.memo_content);

		// memo color picker
		memoColorPicker = (LinearLayout) findViewById(R.id.memo_color_picker);
		bgColorPicker = (LinearLayout) findViewById(R.id.bg_color_picker);
		initColorPicker();

		// 텍스트 길이에 따른 배경 변화 로직 추가.
		onTextChanged();

		// memo time
		memoTimeTextView = (TextView) findViewById(R.id.memo_time);
		
		// background touchevent
		background = findViewById(R.id.memo_activiy_background);
		background.setOnClickListener(this);

		intent = getIntent();
		memoTitle = intent.getStringExtra("selectedMemoTitle");
		memoContent = intent.getStringExtra("selectedMemoContent");
		memoColor = intent.getIntExtra("selectedMemoColor",
				Memo.MEMO_COLOR_BLUE);
		memoId = intent.getStringExtra("selectedMemoId");

		// color
		r = intent.getIntExtra("selectedMemoR", 100);
		g = intent.getIntExtra("selectedMemoG", 100);
		b = intent.getIntExtra("selectedMemoB", 100);

		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_finish).setOnClickListener(this);

		// finish

		// 입력, 수정모드에 따라 삭제버튼을 보이거나 숨긴다.
		if (memoId == null) {
			// 입력모드
			findViewById(R.id.btn_del).setVisibility(View.GONE);
			String memoDate = Util.getDate();
			String memoTime = Util.getTime();
			memoTimeTextView.setText(memoDate + " " + memoTime);
		} else {
			// 수정모드
			memoTitleET.setText(memoTitle);
			memoContentET.setText(memoContent);
			MemoDAO dao = new MemoDAO(getApplicationContext());
			Memo memo = dao.getMemoInfo(memoId);
			memoTimeTextView.setText(memo.getMemoDate() + " "
					+ memo.getMemoTime());
		}
	}

	/**
	 * 메모 색 선택 모듈 초기화
	 */
	private void initColorPicker() {
		for (Color c : ColorDB.getInstance().getColorList()) {
			//memo
			addColorSelectionView(c, "memo", memoColorSelectionViewList, memoColorPicker); 
			//bg
			addColorSelectionView(c, "bg", bgColorSelectionViewList, bgColorPicker);
		}

		// 그리기

	}

	/**
	 * memo, bg에 따라 색상 선택 뷰를 넣는다.
	 * @param c
	 * @param type
	 * @param list
	 * @param layout
	 */
	private void addColorSelectionView(Color c, String type,
			List<ColorSelectionView> list, LinearLayout layout) {
		ColorSelectionView v = new ColorSelectionView(
				getApplicationContext(), c, type);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		lp.weight = 1;
		lp.setMargins(10, 10, 10, 10);
		v.setLayoutParams(lp);

		// onclick listener
		v.setOnClickListener(this);
		
		//add to list
		list.add(v);

		layout.addView(v);
		
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
		case R.id.btn_del:
			deleteMemo();
			break;
		case R.id.memo_activiy_background:
			InputMethodManager inputMethodManager = (InputMethodManager) this
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), 0);
			break;
		}

		// color picker 선택 시
		if (v instanceof ColorSelectionView) {
			if (((ColorSelectionView) v).getType() == 0) {
				// memo
				
				//set non selection state on every view
				for(ColorSelectionView csv :memoColorSelectionViewList){
					if(csv.isSelected()){
						csv.setSelected(false);
						csv.invalidate();
					}
				}
				
				//set current selected view
				v.setSelected(true);
				v.invalidate();
				
				//change memo color
				memoTitleET.setBackgroundColor(((ColorSelectionView) v).getColorI());
				memoContentET.setBackgroundColor(((ColorSelectionView) v).getColorI());
				memoTimeTextView.setBackgroundColor(((ColorSelectionView) v).getColorI());
				
			} else if (((ColorSelectionView) v).getType() == 1) {
				// bg
				// change background
				background.setBackgroundColor(((ColorSelectionView) v).getColorBG());
				
			}
		}
	}

	private void createMemo() {
		String memoTitle = memoTitleET.getText().toString();
		String memoContent = memoContentET.getText().toString();
		intent.putExtra("memoTitle", memoTitle);
		intent.putExtra("memoContent", memoContent);
		// if this case is when you tab create button, memoId's value is null.
		// and maybe you don't use it.
		intent.putExtra("selectedMemoId", memoId);
		intent.putExtra("selectedMemoColor", memoColor);

		// color
		intent.putExtra("selectedMemoR", r);
		intent.putExtra("selectedMemoG", g);
		intent.putExtra("selectedMemoB", b);

		setResult(RESULT_OK, intent);
		finish();
	}

	private void deleteMemo() {
		intent.putExtra("selectedMemoId", memoId);
		intent.putExtra("delete", true);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void moveToBack() {
		intent.putExtra("checkBack", BACK);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void onTextChanged() {
		memoContentET.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				int textLength = s.length();
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
		// 입력, 수정모드에 따라 삭제버튼을 보이거나 숨긴다.
		if (memoId == null) {
			// ((TextView)findViewById(R.id.memoBoardTitle)).setText("메모 입력");
		} else {
			// ((TextView)findViewById(R.id.memoBoardTitle)).setText("메모 수정");
		}
		/*
		 * Typeface tf = Typeface.createFromAsset(getAssets(),
		 * "fonts/telegrafico.ttf");
		 * ((TextView)findViewById(R.id.memoBoardTitle)).setTypeface(tf);
		 */
	}

}

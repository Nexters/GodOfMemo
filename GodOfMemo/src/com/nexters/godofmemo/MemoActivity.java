package com.nexters.godofmemo;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
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
import com.nexters.godofmemo.util.Constants;
import com.nexters.godofmemo.util.Util;
import com.nexters.godofmemo.view.ColorSelectionView;

public class MemoActivity extends ActionBarActivity implements OnClickListener {

	// layout objects
	private EditText memoContentET;
	private EditText memoTitleET;
	private TextView memoTimeTextView;
	private View background; // 배경.

	// memo color picker
	LinearLayout memoColorPicker;
	LinearLayout bgColorPicker;
	List<ColorSelectionView> memoColorSelectionViewList = new ArrayList<ColorSelectionView>();
	List<ColorSelectionView> bgColorSelectionViewList = new ArrayList<ColorSelectionView>();

	// 수정할 메모!!
	private Memo memo;

	// color
	private int r;
	private int g;
	private int b;

	// 설정 저장소
	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 저장소 초기화
		pref = getSharedPreferences("memo", Context.MODE_PRIVATE);

		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_memo_edit);

		setContentView(R.layout.activity_memo);

		// memo size
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int dWidth = metrics.widthPixels;
		dWidth = dWidth * 8 / 10;

		// memo backgroudn control
		memoTitleET = (EditText) findViewById(R.id.memo_title);
		memoContentET = (EditText) findViewById(R.id.memo_content);
		memoTimeTextView = (TextView) findViewById(R.id.memo_time);

		// memo color picker
		memoColorPicker = (LinearLayout) findViewById(R.id.memo_color_picker);
		bgColorPicker = (LinearLayout) findViewById(R.id.bg_color_picker);
		initColorPicker();

		// background touchevent
		background = findViewById(R.id.memo_activiy_background);
		background.setOnClickListener(this);
		initBackground();

		// 이벤트설정.
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_finish).setOnClickListener(this);

		// layout 크기 설정.
		setLayoutSize();

		// 입력, 수정모드
		memo = getIntent().getParcelableExtra("memo");
		if (memo != null) {
			initMemoInfo(memo); // 기존메모 수정.
		} else {
			initNewMemo(); // 신규메모
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_finish:
			saveMemo(); // 메모생성!!
			break;
		case R.id.btn_back:
			moveToBack(); // 뒤로가기
			break;
		case R.id.btn_del:
			deleteMemo(); // 메모 삭제.
			break;
		case R.id.memo_activiy_background: // 배경선택시 키보드 내리기.
			InputMethodManager inputMethodManager = (InputMethodManager) this
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), 0);
			break;
		}

		// color picker 선택 시
		if (v instanceof ColorSelectionView) {
			onClickColorPicker(v);
		}
	}

	/**
	 * 색상선택 선택시.
	 *
	 * @param v
	 */
	private void onClickColorPicker(View v) {
		if (((ColorSelectionView) v).getType() == 0) {
			// memo

			// set non selection state on every view
			for (ColorSelectionView csv : memoColorSelectionViewList) {
				if (csv.isSelected()) {
					csv.setSelected(false);
					csv.invalidate();
				}
			}

			// set current selected view
			v.setSelected(true);
			v.invalidate();

			// 메모색 설정.
			setMemoColor(((ColorSelectionView) v).getColor());

		} else if (((ColorSelectionView) v).getType() == 1) {
			// bg
			// change background
			background
					.setBackgroundColor(((ColorSelectionView) v).getColorBG());

			// save bg color
			pref.edit()
					.putInt("bg_color_r",
							((ColorSelectionView) v).getColor().getR())
					.commit();
			pref.edit()
					.putInt("bg_color_g",
							((ColorSelectionView) v).getColor().getG())
					.commit();
			pref.edit()
					.putInt("bg_color_b",
							((ColorSelectionView) v).getColor().getB())
					.commit();

		}

	}

	/**
	 * 메모색상설정.
	 *
	 * @param color
	 */
	private void setMemoColor(Color color) {
		// color
		int c = android.graphics.Color.argb(255, color.getR(), color.getG(),
				color.getB());

		// change memo color
		memoTitleET.setBackgroundColor(c);
		memoContentET.setBackgroundColor(c);
		memoTimeTextView.setBackgroundColor(c);

		// save memo color
		r = color.getR();
		g = color.getG();
		b = color.getB();

	}

	/**
	 * 신규입력일 때 레이아웃을 초기화한다.
	 */
	private void initNewMemo() {
		findViewById(R.id.btn_del).setVisibility(View.GONE);
		String memoDate = Util.getDate();
		String memoTime = Util.getTime();
		memoTimeTextView.setText(memoDate + " " + memoTime);

		// 메모지색깔 초기화
		Color color = new Color("8cd39c", 140, 211, 156);
		setMemoColor(color);
	}

	/**
	 * 수정모드일때 메모정보를 불러오고 화면에 설정한다.
	 *
	 * @param memo
	 */
	private void initMemoInfo(Memo memo) {

		String memoTitle = memo.getMemoTitle();
		String memoContent = memo.getMemoContent();

		// color
		r = (int) (memo.getRed() * 255.f);
		g = (int) (memo.getGreen() * 255.f);
		b = (int) (memo.getBlue() * 255.f);

		// 색상 설정
		setMemoColor(new Color("", r, g, b));

		// 수정모드
		memoTitleET.setText(memoTitle);
		memoContentET.setText(memoContent);

		memoTimeTextView.setText(memo.getMemoDate() + " " + memo.getMemoTime());

	}

	/**
	 * 어따쓰는겨???
	 */
	public void setLayoutSize() {

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		int height = metrics.heightPixels;

		LinearLayout layoutBody = (LinearLayout) findViewById(R.id.memo_layout_body);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				height / 4, LinearLayout.LayoutParams.MATCH_PARENT,
				Gravity.CENTER_HORIZONTAL);
		lp.weight = 1;
		layoutBody.setOrientation(LinearLayout.VERTICAL);
		layoutBody.setLayoutParams(lp);

	}

	/**
	 * 배경설정!
	 */
	private void initBackground() {

		int r = pref.getInt("bg_color_r", 255);
		int g = pref.getInt("bg_color_g", 255);
		int b = pref.getInt("bg_color_b", 255);

		int c = android.graphics.Color.argb(128, r, g, b);
		background.setBackgroundColor(c);

	}

	/**
	 * 메모 색 선택 모듈 초기화
	 */
	private void initColorPicker() {
		for (Color c : ColorDB.getInstance().getColorList()) {
			// memo
			addColorSelectionView(c, "memo", memoColorSelectionViewList,
					memoColorPicker);
			// bg
			addColorSelectionView(c, "bg", bgColorSelectionViewList,
					bgColorPicker);
		}

		// 그리기?

	}

	/**
	 * memo, bg에 따라 색상 선택 뷰를 넣는다.
	 *
	 * @param c
	 * @param type
	 * @param list
	 * @param layout
	 */
	private void addColorSelectionView(Color c, String type,
			List<ColorSelectionView> list, LinearLayout layout) {
		ColorSelectionView v = new ColorSelectionView(getApplicationContext(),
				c, type);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.MATCH_PARENT,
				LinearLayout.LayoutParams.MATCH_PARENT);
		lp.gravity = Gravity.CENTER_HORIZONTAL;
		lp.weight = 1;
		lp.setMargins(10, 10, 10, 10);
		v.setLayoutParams(lp);

		// onclick listener
		v.setOnClickListener(this);

		// add to list
		list.add(v);

		layout.addView(v);

	}

	/**
	 * 메모저장 or 수정.!!
	 */
	private void saveMemo() {
		// 메모작성내용
		String memoTitle = memoTitleET.getText().toString();
		String memoContent = memoContentET.getText().toString();
		boolean isNew = false;

		// 넘겨줄 메모객체 생성.
		if (memo == null) {
			memo = new Memo();
			isNew = true;
		}
		memo.setMemoTitle(memoTitle);
		memo.setMemoContent(memoContent);
		memo.setMemoDate(Util.getDate());
		memo.setMemoTime(Util.getTime());
		memo.setRed(r / 255f);
		memo.setGreen(g / 255f);
		memo.setBlue(b / 255f);

		MemoDAO memoDao = new MemoDAO(getApplicationContext());

		// 신규 or 수정.
		if (isNew) {
			// 새로작성한 메모 저장!
			long memoIdL = memoDao.insertMemo(memo);
			String memoId = String.valueOf(memoIdL);
			memo.setMemoId(memoId);
		} else {
			// 메모 수정.
			memoDao.updateMemo(memo);
		}

		// 소포에 담기.
		Intent intent = new Intent();
		intent.putExtra("memo", memo);

		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 메모 ㅅ ㅏㄱ제!!
	 */
	private void deleteMemo() {
		MemoDAO memoDao = new MemoDAO(getApplicationContext());
		memoDao.delMemo(memo);

		// 소포에 담기.
		Intent intent = new Intent();
		intent.putExtra("delete", true);
		intent.putExtra("memo", memo);

		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 뒤로~!
	 */
	private void moveToBack() {
		Intent intent = new Intent();
		intent.putExtra("checkBack", Constants.BACK);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

}

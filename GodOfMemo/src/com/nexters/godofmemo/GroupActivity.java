package com.nexters.godofmemo;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nexters.godofmemo.data.ColorDB;
import com.nexters.godofmemo.object.Color;
import com.nexters.godofmemo.util.Util;
import com.nexters.godofmemo.view.ColorSelectionView;

@SuppressLint("NewApi")
public class GroupActivity extends ActionBarActivity implements
		OnClickListener, OnSeekBarChangeListener {
	Intent intent;

	private SeekBar bar;
	private TextView seekBarAction;
	private int width, height;
	private int changedGroupSize;
	private int initGroupSize;
	private View group; // ImageView는 안된다
	private View groupImgArea;
	private View groupSelectionLayout;
	private View background;
	private EditText groupTitleInput;

	private String groupId;
	private String groupTitle;
	private int groupColor;
	private float groupSize;

	private final int BACK = 3;

	// 그룹 영역 크기
	int dHeight = 0;
	// 그룹 중심 위치
	int centerPosition = 50;
	// 그룹 최소 크기(%)
	int minGroupSize = 30;
	// 그룹 최대 크기(%)
	int maxGroupSize = 80;

	// memo color picker
	LinearLayout groupColorPicker;
	List<ColorSelectionView> groupColorSelectionViewList = new ArrayList<ColorSelectionView>();

	// color
	private int r;
	private int g;
	private int b;

	// 설정 저장소
	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the layout
		setContentView(R.layout.activity_group);

		// 저장소 초기화
		pref = getSharedPreferences("memo", Context.MODE_PRIVATE);

		// init color picker
		groupColorPicker = (LinearLayout) findViewById(R.id.group_color_picker);
		initColorPicker();

		// init background
		background = findViewById(R.id.group_activiy_background);
		background.setOnClickListener(this);
		initBackground();

		// 유틸 초기화
		Util.init(getApplicationContext());

		group = findViewById(R.id.group_img);
		groupTitleInput = (EditText) findViewById(R.id.group_name_text);
		groupImgArea = findViewById(R.id.group_img_area);

		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_group);

		// 그룹 이미지 배경 레이아웃 위치설정?!
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		dHeight = metrics.heightPixels;
		dHeight = dHeight * centerPosition / 100;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, dHeight);
		// lp.addRule(RelativeLayout.ABOVE, R.id.seekBar);
		groupImgArea.setLayoutParams(lp);

		// 레이아웃에서 그룹 크기 초기값
		initGroupSize = dHeight * minGroupSize / 100;
		changedGroupSize = initGroupSize;
		// 그룹이미지를 위치한다
		Util.setPosition(group, initGroupSize, initGroupSize, 50,
				centerPosition / 2);
		// 그룹제목 위치
		Util.setPosition(groupTitleInput,initGroupSize, initGroupSize,  50, centerPosition / 2);

		groupTitleInput.bringToFront();

		bar = (SeekBar) findViewById(R.id.seekBar); // make seekbar object
		bar.setOnSeekBarChangeListener(this); // set seekbar listener.
		// since we are using this class as the listener the class is "this"
		// make text label for seekBarAction value
		findViewById(R.id.grp_btn_back).setOnClickListener(this);
		findViewById(R.id.grp_btn_del).setOnClickListener(this);
		ImageView btn_del = (ImageView) findViewById(R.id.grp_btn_del);
		btn_del.setOnClickListener(this);

		// get Intent
		intent = getIntent();
		// When you update the group's status.
		// TODO Handling tab event that user select Group!
		groupId = intent.getStringExtra("selectedGroupId");
		groupTitle = intent.getStringExtra("selectedGroupTitle");
		//groupColor = intent.getIntExtra("selectedGroupColor",Group.GROUP_COLOR_BLUE);
		groupSize = intent.getFloatExtra("selectedGroupSize", initGroupSize);

		r = intent.getIntExtra("selectedGroupColorR", 128);
		g = intent.getIntExtra("selectedGroupColorG", 128);
		b = intent.getIntExtra("selectedGroupColorB", 128);

		// Setting values likes size or color of the group;
		if (groupId == null) {
			btn_del.setVisibility(View.INVISIBLE);
		} else {
			float dSize = adjustGroupSize(groupSize);
			// 그룹 크기 조절
			Util.setPosition(group, (int) dSize, (int) dSize, 50,
					centerPosition / 2);
			// set progress of the seekbar
			int progress = adjustProgress((int) dSize);
			bar.setProgress(progress);
			// set groupTitle
			groupTitleInput.setText(groupTitle);

		}

		//set click event
		findViewById(R.id.btn_finish).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finish:
			createGroup();
			break;
		case R.id.grp_btn_back:
			moveToBack();
			break;
		case R.id.grp_btn_del:
			deleteGroup();
			break;
		case R.id.group_activiy_background:
			InputMethodManager inputMethodManager = (InputMethodManager) this
					.getSystemService(Activity.INPUT_METHOD_SERVICE);
			inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus()
					.getWindowToken(), 0);
			break;
		}

		// color picker 선택 시
		if (v instanceof ColorSelectionView) {
			if (((ColorSelectionView) v).getType() == 2) {
				// memo

				// set non selection state on every view
				for (ColorSelectionView csv : groupColorSelectionViewList) {
					if (csv.isSelected()) {
						csv.setSelected(false);
						csv.invalidate();
					}
				}

				// set current selected view
				v.setSelected(true);
				v.invalidate();

				// change memo color
				// group.setBackgroundColor(((ColorSelectionView)
				// v).getColorI());
				int center = ((ColorSelectionView) v).getColorI();
				int outline = ((ColorSelectionView) v).getColorBG();
				Drawable drawable = new BitmapDrawable(getResources(),
						makeRadGrad(center, outline));
				//group.setBackground(drawable);

				// save memo color
				r = ((ColorSelectionView) v).getColor().getR();
				g = ((ColorSelectionView) v).getColor().getG();
				b = ((ColorSelectionView) v).getColor().getB();

			}
		}
	}

	private void initColorPicker() {
		for (Color c : ColorDB.getInstance().getColorList()) {
			// group
			addColorSelectionView(c, "group", groupColorSelectionViewList,
					groupColorPicker);
		}

	}

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

	private void initBackground() {

		int r = pref.getInt("bg_color_r", 255);
		int g = pref.getInt("bg_color_g", 255);
		int b = pref.getInt("bg_color_b", 255);

		int c = android.graphics.Color.argb(128, r, g, b);
		background.setBackgroundColor(c);

	}

	private void createGroup() {
		// case group create
		// because group's title and size is common element
		// key of Extra write group- as prefix.
		String inputGroupTitle = "";
		if (groupTitleInput == null) {
			System.out.println("Didn't get text");
		} else {
			inputGroupTitle = groupTitleInput.getText().toString();
		}
		intent.putExtra("groupTitle", inputGroupTitle);
		intent.putExtra("groupSize",
				changeGroupSizeSuitableMain(changedGroupSize));

		// case group update
		intent.putExtra("selectedGroupId", groupId);
		intent.putExtra("selectedGroupColor", groupColor);

		// color
		intent.putExtra("selectedGroupColorR", r);
		intent.putExtra("selectedGroupColorG", g);
		intent.putExtra("selectedGroupColorB", b);

		setResult(RESULT_OK, intent);
		finish();
	}

	private void deleteGroup() {
		intent.putExtra("selectedGroupId", groupId);
		intent.putExtra("delete", true);
		setResult(RESULT_OK, intent);
		finish();
	}

	private void moveToBack() {
		intent.putExtra("checkBack", BACK);
		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * group size로 넘어오는 값을 MainActivity에 맞게 계산.
	 *
	 * @param groupSize2
	 * @return
	 */
	private float changeGroupSizeSuitableMain(float groupSize) {
		float max = dHeight * centerPosition / 100f * maxGroupSize / 100f;
		float result = ((groupSize * 1.5f) / max);
		// System.out.println("changeGroupSizeSuitableMain : "+result);
		return result;
	}

	/**
	 * Adjust group size to Group Activity layout max group size : 2 max result
	 * : 448 <= as adjusting size to group layout
	 *
	 * @param groupSize
	 * @return
	 */
	private float adjustGroupSize(float groupSize) {
		// 1.125
		// System.out.println("before adjust group size :"+groupSize);
		float max = dHeight * centerPosition / 100f * maxGroupSize / 100f;
		float result = ((groupSize * max) / 1.5f);
		// System.out.println("adjustGroupSize :"+result);
		return result;
	}

	/**
	 * in onProgressChanged method, there is expression how to calculate group
	 * size. this method is calculating progress value using above expression.
	 * max progress : 100 max group size : 80 min group size : 30
	 *
	 * @param size
	 * @return
	 */
	private int adjustProgress(int size) {
		// System.out.println("before size adjust progress "+size);
		int result = (((size * 100 / dHeight) - minGroupSize) * 100)
				/ (maxGroupSize - minGroupSize);
		// System.out.println("after adjust progress "+result);
		return result;
	}

	/**
	 * This callback method change Group size fit to progress value max group
	 * size : 80 min group size : 30
	 */
	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		// TODO int progress 받아와서 그룹이미지 크기 조정
		// dHeight * (50 * progress /100 +20) /100
		// progress / 100 : 진행사항.
		// dHeight * (30) /100
		changedGroupSize = dHeight
				* (((maxGroupSize - minGroupSize) * progress / 100) + minGroupSize)
				/ 100;
		// 조정 가능한 최대 크기 = 최대에서 최소 뺀거.
		System.out.println("onProgressChanged:" + changedGroupSize);
		if (changedGroupSize > initGroupSize) {
			Util.setPosition(group, changedGroupSize, changedGroupSize, 50,
					centerPosition / 2);
		}

		groupTitleInput.bringToFront();
	}

	// 지금 필요없음
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// 입력, 수정모드에 따라 삭제버튼을 보이거나 숨긴다.
		if (groupId == null) {
			// ((TextView)findViewById(R.id.memoBoardTitle)).setText("그룹 생성");
		} else {
			// ((TextView)findViewById(R.id.memoBoardTitle)).setText("그룹 수정");
		}
		/*
		 * Typeface tf = Typeface.createFromAsset(getAssets(),
		 * "fonts/telegrafico.ttf");
		 * ((TextView)findViewById(R.id.memoBoardTitle)).setTypeface(tf);
		 */
	}

	private Bitmap makeRadGrad(int centerC, int outC) {

		RadialGradient gradient = new RadialGradient(200, 200, 200, centerC,
				outC, android.graphics.Shader.TileMode.CLAMP);
		Paint p = new Paint();
		p.setDither(true);
		p.setShader(gradient);

		Bitmap bitmap = Bitmap.createBitmap(400, 400, Config.ARGB_8888);
		Canvas c = new Canvas(bitmap);
		c.drawCircle(200, 200, 200, p);

		return bitmap;
	}
}

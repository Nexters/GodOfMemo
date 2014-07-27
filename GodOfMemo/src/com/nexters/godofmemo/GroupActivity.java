package com.nexters.godofmemo;

import static com.nexters.godofmemo.util.Constants.BACK;
import static com.nexters.godofmemo.util.Constants.centerPosition;
import static com.nexters.godofmemo.util.Constants.maxGroupSize;
import static com.nexters.godofmemo.util.Constants.minGroupSize;

import java.util.ArrayList;
import java.util.List;

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
import android.util.Log;
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

import com.nexters.godofmemo.dao.GroupDAO;
import com.nexters.godofmemo.data.ColorDB;
import com.nexters.godofmemo.object.Color;
import com.nexters.godofmemo.object.Group;
import com.nexters.godofmemo.util.Util;
import com.nexters.godofmemo.view.ColorSelectionView;

public class GroupActivity extends ActionBarActivity implements
		OnClickListener, OnSeekBarChangeListener {

	// layouts.
	private SeekBar bar;
	private ImageView groupImg; // ImageView는 안된다
	private View groupImgArea;
	private View background;
	private EditText groupTitleInput;
	private ImageView btn_del;

	// memo color picker
	private LinearLayout groupColorPicker;
	private final List<ColorSelectionView> groupColorSelectionViewList = new ArrayList<ColorSelectionView>();

	// 수정시 갖고있을 그룹정보.
	private Group group;

	// 그룹 크기변경을 위한 변수.
	private int changedGroupSize;
	private int initGroupSize;
	private int dHeight;

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

		// 유틸 초기화
		Util.init(getApplicationContext());

		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_group);

		// Load the layout
		setContentView(R.layout.activity_group);

		groupImg = (ImageView) findViewById(R.id.group_img);
		groupTitleInput = (EditText) findViewById(R.id.group_name_text);
		groupImgArea = findViewById(R.id.group_img_area);
		btn_del = (ImageView) findViewById(R.id.grp_btn_delete);

		// init color picker
		groupColorPicker = (LinearLayout) findViewById(R.id.group_color_picker);
		initColorPicker();

		// init background
		background = findViewById(R.id.group_activiy_background);
		background.setOnClickListener(this);
		initBackground();

		// 크기조절막대.
		bar = (SeekBar) findViewById(R.id.seekBar); // make seekbar object
		bar.setOnSeekBarChangeListener(this); // set seekbar listener.

		// 그룹 이미지 배경 레이아웃 위치설정?!
		initGroupImgArea();

		// since we are using this class as the listener the class is "this"
		// make text label for seekBarAction value
		// 이벤트설정
		findViewById(R.id.grp_btn_back).setOnClickListener(this);
		findViewById(R.id.grp_btn_delete).setOnClickListener(this);
		findViewById(R.id.grp_btn_done).setOnClickListener(this);
		//btn_del.setOnClickListener(this);

		groupTitleInput.bringToFront();

		// get Intent
		group = getIntent().getParcelableExtra("group");

		// When you update the group's status.
		// TODO Handling tab event that user select Group!

		// 신규입력인가?!
		if (group == null) {
			initNewGroup();
		} else {
			// 수정일 때.
			initGroupInfo();
		}

		// set click event
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.grp_btn_done:
			// 그룹 생성!!
			createGroup();
			break;
		case R.id.grp_btn_back:
			// 뒤로가기
			moveToBack();
			break;
		case R.id.grp_btn_delete:
			// 그룹 삭제
			deleteGroup();
			break;
		case R.id.group_activiy_background:
			// 배경 클릭 시 키보드 숨김.
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
	 * 수정모드 시 기존 그룹정보 설정.
	 */
	private void initGroupInfo() {

		// 그룹 크기 조절
		float dSize = adjustGroupSize(group.getRadius());
		Util.setPosition(groupImg, (int) dSize, (int) dSize, 50,
				centerPosition / 2);

		// set progress of the seekbar
		int progress = adjustProgress((int) dSize);
		bar.setProgress(progress);

		// set groupTitle
		groupTitleInput.setText(group.getGroupTitle());

		//이미지
		r = (int) (group.getRed() * 255f);
		g = (int) (group.getGreen() * 255f);
		b = (int) (group.getBlue() * 255f);

		int center = android.graphics.Color.argb(255, r, g, b);
		int outline = android.graphics.Color.argb(128, r, g, b);

		setGroupImgColor(center, outline);

	}

	/**
	 * 그룹 신규 생성시 화면조정.
	 */
	private void initNewGroup() {
		btn_del.setVisibility(View.INVISIBLE);

		// 그룹 크기 조절
		// TODO 임시값.
		int dSize = 300;
		Util.setPosition(groupImg, dSize, dSize, 50, centerPosition / 2);
		// set progress of the seekbar
		int progress = adjustProgress(dSize);
		bar.setProgress(progress);

		//초기색상값.
		r = 140;
		g = 211;
		b = 156;

		//초기 그룹배경 지정!
		int center = android.graphics.Color.argb(255, r, g, b);
		int outline = android.graphics.Color.argb(128, r, g, b);
		setGroupImgColor(center, outline);

	}

	/**
	 * 그룹영역 위치와 사이즈를 조정한다.
	 */
	private void initGroupImgArea() {
		// 그룹이미지영역 조정.
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		dHeight = metrics.heightPixels;
		dHeight = dHeight * centerPosition / 100;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, dHeight);
		groupImgArea.setLayoutParams(lp);

		// 레이아웃에서 그룹 크기 초기값
		initGroupSize = dHeight * minGroupSize / 100;
		changedGroupSize = initGroupSize;
		// 그룹이미지를 위치한다
		Util.setPosition(groupImg, initGroupSize, initGroupSize, 50,
				centerPosition / 2);
		// 그룹제목 위치
		Util.setPosition(groupTitleInput, initGroupSize, initGroupSize, 50,
				centerPosition / 2);

	}

	/**
	 * 색상선택!!!
	 *
	 * @param v
	 */
	private void onClickColorPicker(View v) {

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
			setGroupImgColor(center, outline);

			// save memo color
			r = ((ColorSelectionView) v).getColor().getR();
			g = ((ColorSelectionView) v).getColor().getG();
			b = ((ColorSelectionView) v).getColor().getB();

		}

	}

	/**
	 * 그룹형상을 그린다.
	 *
	 * @param center
	 * @param outline
	 */
	@SuppressWarnings("deprecation")
	private void setGroupImgColor(int center, int outline) {
		Drawable drawable = new BitmapDrawable(getResources(), makeRadGrad(
				center, outline));
		groupImg.setBackgroundDrawable(drawable);

	}

	/**
	 * 색상선택 초기화.
	 */
	private void initColorPicker() {
		for (Color c : ColorDB.getInstance().getColorList()) {
			// group
			addColorSelectionView(c, "group", groupColorSelectionViewList,
					groupColorPicker);
		}

	}

	/**
	 * 색상선택 뷰 하나씩 넣기.
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
	 * 배경 초기화.
	 */
	private void initBackground() {

		int r = pref.getInt("bg_color_r", 255);
		int g = pref.getInt("bg_color_g", 255);
		int b = pref.getInt("bg_color_b", 255);

		int c = android.graphics.Color.argb(128, r, g, b);
		background.setBackgroundColor(c);

	}

	/**
	 * 그룹 저장. 신규or수정.
	 */
	private void createGroup() {
		boolean isNew = false;

		// 그룹에 이것저것 넣기.
		if (group == null) {
			// 신규니까.
			group = new Group();
			isNew = true;
		}

		// 그룹제목.
		String inputGroupTitle = groupTitleInput.getText().toString();

		group.setGroupTitle(inputGroupTitle);
		group.setGroupDate(Util.getDate());
		group.setGroupTime(Util.getTime());
		group.setRadius(changeGroupSizeSuitableMain(changedGroupSize)); // TODO 여기 무슨값넣지?
		group.setRed(r / 255f);
		group.setGreen(g / 255f);
		group.setBlue(b / 255f);

		GroupDAO groupDao = new GroupDAO(getApplicationContext());
		if (isNew) {
			// 신규일때.
			// 새로 생성한 메모에 아이디를 설정.
			long groupIdL = groupDao.insertGroup(group);
			String groupId = String.valueOf(groupIdL);
			group.setGroupId(groupId);
		} else {
			// 수정일때
			// 수정된 그룹 정보를 갱신한다.
			groupDao.updateGroup(group);
		}

		// 소포에 담기
		Intent intent = new Intent();
		intent.putExtra("group", group);

		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 그룹 삭제..
	 */
	private void deleteGroup() {
		// 그룹 삭제.
		GroupDAO groupDao = new GroupDAO(getApplicationContext());
		groupDao.delGroup(group);

		// 소포에 담기
		Intent intent = new Intent();
		intent.putExtra("group", group);
		intent.putExtra("delete", true);

		setResult(RESULT_OK, intent);
		finish();
	}

	/**
	 * 뒤로가기.
	 */
	private void moveToBack() {
		Intent intent = new Intent();
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
		float result = ((groupSize * 0.5f) / max);
		Log.i("debug","changeGroupSizeSuitableMain : "+result);
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
		float result = ((groupSize * max) * 1.5f);
		Log.i("debug", "adjustGroupSize :"+result);
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
		Log.i("debug","after adjust progress "+result);
		return result;
	}

	/**
	 * This callback method change Group size fit to progress value max group
	 * size : 80 min group size : 30
	 */
	@Override
	public void onProgressChanged(SeekBar sb, int progress, boolean arg2) {
		// TODO int progress 받아와서 그룹이미지 크기 조정
		// dHeight * (50 * progress /100 +20) /100
		// progress / 100 : 진행사항.
		// dHeight * (30) /100
		changedGroupSize = dHeight
				* (((maxGroupSize - minGroupSize) * progress / 100) + minGroupSize)
				/ 100;
		// 조정 가능한 최대 크기 = 최대에서 최소 뺀거.
		Log.i("debug","onProgressChanged:" + changedGroupSize);
		if (changedGroupSize > initGroupSize) {
			Util.setPosition(groupImg, changedGroupSize*3, changedGroupSize*3, 50,
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
	}

	/**
	 * 그라데이션 효과 만들기.
	 *
	 * @param centerC
	 * @param outC
	 * @return
	 */
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

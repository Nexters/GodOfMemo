package com.nexters.godofmemo;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nexters.godofmemo.object.Group;
import com.nexters.godofmemo.util.Util;

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
	
	//그룹 영역 크기
	int dHeight = 0;
	//그룹 중심 위치
	int centerPosition = 50;
	//그룹 최소 크기(%)
	int minGroupSize = 30;
	//그룹 최대 크기(%)
	int maxGroupSize = 80;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the layout
		setContentView(R.layout.activity_group);

		//유틸 초기화
		Util.init(getApplicationContext());

		group = findViewById(R.id.group_img);
		groupTitleInput = (EditText) findViewById(R.id.group_name_text);
		groupImgArea = findViewById(R.id.group_img_area);
		groupSelectionLayout = findViewById(R.id.group_color_selection_area);
		
		//메모 색깔 버튼 선택 이벤트
		findViewById(R.id.group_color_select_red).setOnClickListener(this);
		findViewById(R.id.group_color_select_blue).setOnClickListener(this);
		findViewById(R.id.group_color_select_yellow).setOnClickListener(this);
		
		
		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_group);


		//그룹 이미지 배경 레이아웃 위치설정?!
		DisplayMetrics metrics = getResources().getDisplayMetrics();
		dHeight = metrics.heightPixels;
		dHeight = dHeight * centerPosition / 100;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				dHeight);
		lp.addRule(RelativeLayout.ABOVE, R.id.seekBar);
		groupImgArea.setLayoutParams(lp);
		
		// 레이아웃에서 그룹 크기 초기값
		initGroupSize = dHeight * minGroupSize / 100;
		changedGroupSize = initGroupSize;
		// 그룹이미지를 위치한다
		Util.setPosition(group, initGroupSize, initGroupSize, 50, centerPosition/2);
		// 그룹제목 위치
		Util.setPosition(groupTitleInput, 50, centerPosition/2);
		
		groupTitleInput.bringToFront();
		
		// To handling click event on background 
		background = findViewById(R.id.group_activiy_background);
		background.setOnClickListener(this);
				
		bar = (SeekBar) findViewById(R.id.seekBar); // make seekbar object
		bar.setOnSeekBarChangeListener(this); // set seekbar listener.
		// since we are using this class as the listener the class is "this"
		// make text label for seekBarAction value
		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_finish).setOnClickListener(this);
		ImageView trash_can = (ImageView) findViewById(R.id.trash_can);
		trash_can.setOnClickListener(this);
		
		// get Intent
		intent = getIntent();
		// When you update the group's status.
		//TODO Handling tab event that user select Group!
		groupId = intent.getStringExtra("selectedGroupId");
		groupTitle = intent.getStringExtra("selectedGroupTitle");
		groupColor = intent.getIntExtra("selectedGroupColor", Group.GROUP_COLOR_BLUE);
		groupSize  = intent.getFloatExtra("selectedGroupSize", initGroupSize);
		
		// Setting values likes size or color of the group;
		if(groupId==null){
			trash_can.setVisibility(View.INVISIBLE);
		}else{
			float dSize = adjustGroupSize(groupSize);
			//그룹 크기 조절
			Util.setPosition(group, (int)dSize, (int)dSize, 50, centerPosition/2);
			// set progress of the seekbar 
			int progress = adjustProgress((int)dSize);
			bar.setProgress(progress);
			// set groupTitle
			groupTitleInput.setText(groupTitle);
			// set groupColor
			switch (groupColor) {
			case Group.GROUP_COLOR_BLUE:
				group.setBackgroundResource(R.drawable.circle_blue);
				groupSelectionLayout.setBackgroundResource(R.drawable.group_colorselect_blue);
				break;
			case Group.GROUP_COLOR_RED:
				group.setBackgroundResource(R.drawable.circle_red);
				groupSelectionLayout.setBackgroundResource(R.drawable.group_colorselect_red);
				break;
			case Group.GROUP_COLOR_YELLOW:
				group.setBackgroundResource(R.drawable.circle_yellow);
				groupSelectionLayout.setBackgroundResource(R.drawable.group_colorselect_yellow);
				break;
			default:
				break;
			}
			
		}
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finish:
			createGroup();
			break;
		case R.id.btn_back:
			moveToBack();
			break;
		case R.id.trash_can:
			deleteGroup();
			break;
		case R.id.group_color_select_red:
			findViewById(R.id.group_img).setBackgroundResource(R.drawable.circle_red);
			findViewById(R.id.group_color_selection_area).setBackgroundResource(R.drawable.group_colorselect_red);
			//groupColor = intent.getIntExtra("selectedGroupColor", Group.GROUP_COLOR_RED);
			groupColor = Group.GROUP_COLOR_RED;
			break;

		case R.id.group_color_select_blue:
			findViewById(R.id.group_img).setBackgroundResource(R.drawable.circle_blue);
			findViewById(R.id.group_color_selection_area).setBackgroundResource(R.drawable.group_colorselect_blue);
			//groupColor = intent.getIntExtra("selectedGroupColor", Group.GROUP_COLOR_BLUE);
			groupColor = Group.GROUP_COLOR_BLUE;
			break;

		case R.id.group_color_select_yellow:
			findViewById(R.id.group_img).setBackgroundResource(R.drawable.circle_yellow);
			findViewById(R.id.group_color_selection_area).setBackgroundResource(R.drawable.group_colorselect_yellow);
			//groupColor = intent.getIntExtra("selectedGroupColor", Group.GROUP_COLOR_YELLOW);
			groupColor = Group.GROUP_COLOR_YELLOW;
			break;
		case R.id.group_activiy_background:
			 InputMethodManager inputMethodManager = (InputMethodManager)  this.getSystemService(Activity.INPUT_METHOD_SERVICE);
		        inputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
			break;
		}
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
		intent.putExtra("groupSize", changeGroupSizeSuitableMain(changedGroupSize));
		
		//case group update
		intent.putExtra("selectedGroupId", groupId);
		intent.putExtra("selectedGroupColor", groupColor);
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
	 * @param groupSize2
	 * @return
	 */
	private float changeGroupSizeSuitableMain(float groupSize ) {
		float max = dHeight * centerPosition / 100f * maxGroupSize / 100f;
		float result = ((groupSize * 1.5f) / max);
		//System.out.println("changeGroupSizeSuitableMain : "+result);
		return result;
	}
	
	/**
	 * Adjust group size to Group Activity layout
	 * max group size : 2
	 * max result : 448 <= as adjusting size to group layout
	 * @param groupSize
	 * @return
	 */
	private float adjustGroupSize(float groupSize) {
		// 1.125
		//System.out.println("before adjust group size :"+groupSize);
		float max = dHeight * centerPosition / 100f * maxGroupSize / 100f;
		float result = ((groupSize * max) /1.5f);
		//System.out.println("adjustGroupSize :"+result);
		return result;
	}
	/**
	 * in onProgressChanged method, there is expression how to calculate group size. 
	 * this method is calculating progress value using above expression.
	 * max progress : 100
	 * max group size : 80
	 * min group size : 30
	 * @param size
	 * @return
	 */
	private int adjustProgress(int size){
		//System.out.println("before size adjust progress "+size);
		int result = (((size* 100/dHeight)-minGroupSize)*100)/(maxGroupSize - minGroupSize);
		//System.out.println("after adjust progress "+result);
		return result;
	}
	/**
	 * This callback method change Group size fit to progress value
	 * max group size : 80
	 * min group size : 30 
	 */
	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		// TODO int progress 받아와서 그룹이미지 크기 조정
		// dHeight *  (50 * progress /100 +20)  /100
		// progress / 100 : 진행사항. 
		// dHeight *  (30) /100
		changedGroupSize = dHeight*(((maxGroupSize - minGroupSize) * progress / 100) + minGroupSize)/100;
		//조정 가능한 최대 크기 = 최대에서 최소 뺀거.
		System.out.println("onProgressChanged:" + changedGroupSize);
		if(changedGroupSize > initGroupSize){
			Util.setPosition(group, changedGroupSize, changedGroupSize, 50, centerPosition/2);
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
		//입력, 수정모드에 따라 삭제버튼을 보이거나 숨긴다.
		if(groupId==null){
			((TextView)findViewById(R.id.groupBoardTitle)).setText("그룹 생성");
		}else{
			((TextView)findViewById(R.id.groupBoardTitle)).setText("그룹 수정");
		}
		/*
		Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/telegrafico.ttf");
		((TextView)findViewById(R.id.memoBoardTitle)).setTypeface(tf);*/
	}
}

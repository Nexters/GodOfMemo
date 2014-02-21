package com.nexters.godofmemo;


import com.nexters.godofmemo.object.Group;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.nexters.godofmemo.util.Util;

@SuppressLint("NewApi")
public class GroupActivity extends ActionBarActivity implements
		OnClickListener, OnSeekBarChangeListener {
	Intent intent;

	private SeekBar bar;
	private TextView seekBarAction;
	private int width, height;
	private View group; // ImageView는 안된다
	private View groupImgArea;
	private EditText groupTitleInput;

	private String groupId;
	private String groupTitle;
	private int groupColor;
	
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
		

		
		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_group);


		//그룹 이미지 배경 레이아웃 위치설정?!

		DisplayMetrics metrics = getResources().getDisplayMetrics();
		dHeight = metrics.heightPixels;
		dHeight = dHeight * centerPosition / 100;
		RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
				dHeight);
		groupImgArea.setLayoutParams(lp);
		
		//그룹 크기 초기값
		int initSize = dHeight * minGroupSize / 100;
		// 그룹이미지를 위치한다
		Util.setPosition(group, initSize, initSize, 50, centerPosition/2);
		// 그룹제목 위치
		Util.setPosition(groupTitleInput, 50, centerPosition/2);
		
		groupTitleInput.bringToFront();

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
		groupColor = intent.getIntExtra("selectedGroupColor", Group.DEFAULT_GROUP_COLOR);
			
		if(groupTitle==null){
			trash_can.setVisibility(View.GONE);
		}else{
			groupTitleInput.setText(groupTitle);
			//TODO set groupColor
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
		}
	}

	private void createGroup() {
		// case group create
		String groupTitleInputText = "";
		if (groupTitleInput == null) {
			System.out.println("Didn't get text");
		} else {
			groupTitleInputText = groupTitleInput.getText().toString();
		}
		intent.putExtra("newGroupTitle", groupTitleInputText);
		
		String updateGroupTitle = groupTitleInputText;
				//case status update
		intent.putExtra("selectedGroupId", groupId);
		//TODO You must write code selecting color. 
		intent.putExtra("selectedGroupColor", groupColor);
		intent.putExtra("selectedGroupTitle", updateGroupTitle);
		// TODO You must write code selecting color.
		
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

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		// TODO int progress 받아와서 그룹이미지 크기 조정
		int p = dHeight*(((maxGroupSize - minGroupSize) * progress / 100) + 20)/100;
		//조정 가능한 최대 크기 = 최대에서 최소 뺀거.
		
		Util.setPosition(group, p, p, 50, centerPosition/2);
		
		groupTitleInput.bringToFront();
		
	}

	// 지금 필요없음
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}
}

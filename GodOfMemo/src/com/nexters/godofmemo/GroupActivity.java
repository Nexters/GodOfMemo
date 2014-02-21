package com.nexters.godofmemo;

import com.nexters.godofmemo.object.Group;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

public class GroupActivity extends ActionBarActivity implements
		OnClickListener, OnSeekBarChangeListener {
	Intent intent;
	EditText input_group_et;
	
	private SeekBar bar;
	private TextView seekBarAction;
	
	private String groupId;
	private String groupTitle;
	private int groupColor;
	
	private final int BACK = 3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the layout
		setContentView(R.layout.activity_group);
		
		// get Intent
		intent = getIntent();
		// When you update the group's status.
		//TODO Handling tab event that user select Group!
		groupId = intent.getStringExtra("selectedGroupId");
		groupTitle = intent.getStringExtra("selectedTitle");
		groupColor = intent.getIntExtra("selectedColor", Group.DEFAULT_GROUP_COLOR);
	
		input_group_et = (EditText) findViewById(R.id.group_name_text);
		
		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_group);

		bar = (SeekBar) findViewById(R.id.seekBar); // make seekbar object
		bar.setOnSeekBarChangeListener(this); // set seekbar listener.
		// since we are using this class as the listener the class is "this"
		
		// make text label for seekBarAction value
		seekBarAction = (TextView) findViewById(R.id.seekBarAction);

		findViewById(R.id.btn_back).setOnClickListener(this);
		findViewById(R.id.btn_finish).setOnClickListener(this);
		ImageView trash_can = (ImageView) findViewById(R.id.trash_can);
		trash_can.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_finish:
			createGroup();
			finish();
			break;
		case R.id.btn_back:
			moveToBack();
			finish();
			break;
		case R.id.trash_can:
			deleteGroup();
			finish();
			break;
		}
	}
	
	private void createGroup(){
		//case status update
		intent.putExtra("selectedGroupId", groupId);
		intent.putExtra("selectedGroupTitle", groupTitle);
		intent.putExtra("selectedGroupColor", groupColor);
		// case group create
		String input_group_title_text = "";
		if(input_group_et == null){
			System.out.println("Didn't get text");
		}else{
			input_group_title_text = input_group_et.getText().toString();
		}

		intent.putExtra("newGroupTitle", input_group_title_text);
		//TODO You must write code selecting color. 
		
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void deleteGroup(){
		intent.putExtra("selectedGroupId", groupId);
		intent.putExtra("delete", true);
		setResult(RESULT_OK, intent);
		finish();
	}
	
	private void moveToBack(){
		intent.putExtra("checkBack", BACK);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		seekBarAction.setText(progress + "% 움직였습니다.");
		//TODO int progress 받아와서 그룹이미지 크기 조정
	}
	
	//지금 필요없음 
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}
}

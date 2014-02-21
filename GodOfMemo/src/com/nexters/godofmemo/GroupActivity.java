package com.nexters.godofmemo;

import android.annotation.SuppressLint;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("NewApi")
public class GroupActivity extends ActionBarActivity implements
		OnClickListener, OnSeekBarChangeListener {

	private SeekBar bar;
	private TextView seekBarAction;
	private int width, height;
	RelativeLayout group; //ImageView는 안된다 
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_group);

		// Load the layout
		setContentView(R.layout.activity_group);
		
		//화면 넓이와 높이 구하기 
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		width = size.x;
		height = size.y;
		
		
		//그룹이미지를 위치한다 
		group = (RelativeLayout) findViewById(R.id.group_img);
		//group.setX(width/10);
		//group.setY(height/10);
		
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(width/10, height/10);
		layoutParams.addRule(RelativeLayout.BELOW, R.id.seekBar);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.seekBarAction);
		//layoutParams.addRule(RelativeLayout.m, anchor)
		//layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, R.id.seekBarAction);
		group.setLayoutParams(layoutParams);
		
		
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
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_finish:
			// intent.putExtra("selectedMemoId", memoId);
			createToast("완료하셨습니다~");
			finish();
			break;
		case R.id.btn_back:
			// intent.putExtra("checkBack", BACK);
			// setResult(RESULT_OK, intent);
			createToast("뒤로가기 하셨습니다~");
			finish();
			break;
		case R.id.trash_can:
			// intent.putExtra("selectedMemoId", memoId);
			// intent.putExtra("delete", true);
			// setResult(RESULT_OK, intent);
			createToast("휴지통 눌렀어요~");
			finish();
			break;
		}
	}

	/**
	 * 토스트를 굽는다.
	 * 
	 * @param text
	 */
	private void createToast(String text) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}

	@Override
	public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
		seekBarAction.setText(progress + "% 움직였습니다." + width);
		// TODO int progress 받아와서 그룹이미지 크기 조정
		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
				100 + (progress * 10), 100 + (progress * 10)); //parameter(x, y)
		layoutParams.addRule(RelativeLayout.BELOW, R.id.seekBar);
		layoutParams.addRule(RelativeLayout.CENTER_HORIZONTAL, R.id.seekBarAction);
		group.setLayoutParams(layoutParams);
	}

	// 지금 필요없음
	@Override
	public void onStartTrackingTouch(SeekBar arg0) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar arg0) {
	}
}

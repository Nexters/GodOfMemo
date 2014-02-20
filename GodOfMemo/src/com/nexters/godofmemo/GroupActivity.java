package com.nexters.godofmemo;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class GroupActivity extends ActionBarActivity implements
		OnClickListener, OnSeekBarChangeListener {

	private SeekBar bar;
	private TextView seekBarAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 커스텀 액션바
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_group);

		// Load the layout
		setContentView(R.layout.activity_group);
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

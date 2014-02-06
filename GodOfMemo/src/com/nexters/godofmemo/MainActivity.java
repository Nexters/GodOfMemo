package com.nexters.godofmemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.nexters.godofmemo.dao.MemoDAO;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.util.Constants;
import com.nexters.godofmemo.view.MemoGLView;

public class MainActivity extends ActionBarActivity implements OnClickListener{
	/**
	 * Hold a reference to our GLSurfaceView
	 */
	private MemoGLView glSurfaceView;
	private boolean rendererSet = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	
		//getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM); 
		getSupportActionBar().setCustomView(R.layout.actionbar_memoboard);
		
		findViewById(R.id.action_write).setOnClickListener(this);

		
		//액션바 높이를 저장한다.
		Constants.actionbarHeight = getActionBarHeight();

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		final ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();

		/*
		 * final boolean supportsEs2 = configurationInfo.reqGlEsVersion >=
		 * 0x20000;
		 */

		// Even though the latest emulator supports OpenGL ES 2.0,
		// it has a bug where it doesn't set the reqGlEsVersion so
		// the above check doesn't work. The below will detect if the
		// app is running on an emulator, and assume that it supports
		// OpenGL ES 2.0.
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT
						.startsWith("generic")
						|| Build.FINGERPRINT.startsWith("unknown")
						|| Build.MODEL.contains("google_sdk")
						|| Build.MODEL.contains("Emulator") || Build.MODEL
							.contains("Android SDK built for x86")));

		if (supportsEs2) {
			glSurfaceView = new MemoGLView(this);
			rendererSet = true;
		} else {
			/*
			 * This is where you could create an OpenGL ES 1.x compatible
			 * renderer if you wanted to support both ES 1 and ES 2. Since we're
			 * not doing anything, the app will crash if the device doesn't
			 * support OpenGL ES 2.0. If we publish on the market, we should
			 * also add the following to AndroidManifest.xml:
			 * 
			 * <uses-feature android:glEsVersion="0x00020000"
			 * android:required="true" />
			 * 
			 * This hides our app from those devices which don't support OpenGL
			 * ES 2.0.
			 */
			Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
					Toast.LENGTH_LONG).show();
			return;
		}

		setContentView(glSurfaceView);

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (rendererSet) {
			glSurfaceView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (rendererSet) {
			glSurfaceView.onResume();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu items for use in the action bar
		//MenuInflater inflater = getMenuInflater();
		//inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		System.out.println("onActivityResult");

		//비정상종료면?
		if(resultCode != Activity.RESULT_OK) return;
		
		String txt = data.getStringExtra("short_txt");
		
		//TODO 새 메모 체크하기 
		//메모를 저장한다.

		Memo newMemo = new Memo(getApplicationContext(), txt, glSurfaceView);
		//지금 시간을 구한다
		long curr = System.currentTimeMillis();
		//setter
		newMemo.setProdTime(curr);
		
		MemoDAO memoDao = new MemoDAO(getApplicationContext());
		long memoIdL = memoDao.insertMemo(newMemo);
		String memoId = String.valueOf(memoIdL);
		newMemo.setMemoId(memoId);
		
		//TODO 새 메모가 생겼을때 토스트
		String text = "새 메모!";
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
		
		//화면에 그릴 목록에 추가
		glSurfaceView.mr.memoList.add(newMemo);
	}

	private int getActionBarHeight() {
	    int actionBarHeight = getSupportActionBar().getHeight();
	    if (actionBarHeight != 0)
	        return actionBarHeight;
	    final TypedValue tv = new TypedValue();
	    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
	        if (getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
	            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
	    } else if (getTheme().resolveAttribute(R.attr.actionBarSize, tv, true))
	        actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
	    return actionBarHeight;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.action_write:
			Intent intent = new Intent(this, MemoActivity.class);
			startActivityForResult(intent, 0);
			return;
		}
		
	}
}

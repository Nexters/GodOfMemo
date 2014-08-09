package com.nexters.godofmemo;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;

public class SplashActivity extends ActionBarActivity {

	private static final int AUTO_HIDE_DELAY_MILLIS = 1500;

	// 시작관련 변수
	private boolean isFirst = false; // 처음 실행 여부

	// 설정 저장소
	SharedPreferences pref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash);

		// 저장소 초기화
		pref = getApplicationContext().getSharedPreferences("memo",
				Context.MODE_PRIVATE);

		// 처음 실행 여부 체크
		isFirst = pref.getBoolean("isFirst", true);
		isFirst = true;
		if (isFirst) {
			pref.edit().putBoolean("isFirst", false).commit();

			// start normal activity
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					// start first tutorial activity

					Intent i = new Intent(SplashActivity.this,
							InitialTutorialActivity.class);
					startActivity(i);

					// close this activity
					finish();
				}
			}, AUTO_HIDE_DELAY_MILLIS);

		} else {

			// start normal activity
			new Handler().postDelayed(new Runnable() {

				@Override
				public void run() {
					Intent i = new Intent(SplashActivity.this,
							MainActivity.class);
					startActivity(i);

					// close this activity
					finish();
				}
			}, AUTO_HIDE_DELAY_MILLIS);
		}
	}
}

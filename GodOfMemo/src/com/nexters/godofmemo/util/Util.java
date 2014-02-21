package com.nexters.godofmemo.util;

import java.text.DateFormat;
import java.util.Calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

public class Util {

	private static Context context;
	public static int width = 0;
	public static int height = 0;

	/**
	 * 초기화
	 * 
	 * @param context
	 */
	public static void init(Context context) {
		Util.context = context;
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		width = metrics.widthPixels;
		height = metrics.heightPixels;
	}

	
	/**
	 *  FrameLayout에서 View의 위치를 지정한다. (기본)
	 */
	public static void setPosition(View view, int x, int y) {
		setPosition(view, view.getWidth(), view.getHeight(), x, y);
	}

	/**
	 * FrameLayout에서 View의 위치를 지정한다. (이미지ID로)
	 * 
	 * @param view
	 *            위치를 조정할 View
	 * @param id
	 *            위치를 조정할 View에 사용한 이미지 ID
	 * @param x
	 *            x 위치(%)
	 * @param y
	 *            y 위치(%)
	 */
	public static void setPosition(View view, int id, int x, int y) {
		//TODO 널포인터 생기네...
		Drawable dr = context.getResources().getDrawable(id);
		int width = dr.getIntrinsicWidth();
		int height = dr.getIntrinsicHeight();
		setPosition(view, width, height, x, y);
	}

	/**
	 * FrameLayout에서 View의 위치를 지정한다. (이미지 너비,높이)
	 * 
	 * @param view
	 *            위치를 조정할 View
	 * @param width
	 *            view 너비
	 * @param height
	 *            view 높이
	 * @param x
	 *            x 위치(%)
	 * @param y
	 *            y 위치(%)
	 */
	public static void setPosition(View view, int width, int height, int x,
			int y) {

		int px = Util.width * x / 100 - (width/2);
		int py = Util.height * y / 100 - (height/2);
		
		if(width == 0) width = LayoutParams.WRAP_CONTENT;
		if(height == 0) height = LayoutParams.WRAP_CONTENT;
		
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(width,
				height);
		lp.leftMargin = px;
		lp.topMargin = py;
		lp.gravity = Gravity.LEFT | Gravity.TOP;
		view.setLayoutParams(lp);
	}
	/**
	 * 현재 날짜를 가져온다. YYYY-MM-DD.
	 * 
	 * @return
	 */
	public static String getDate() {
		String date = DateFormat.getDateInstance().format(
				Calendar.getInstance().getTime());
		return date;
	}

	/**
	 * 현재 시간을 가져온다. HH:mm:ss.
	 * 
	 * @return
	 */
	public static String getTime() {
		String time = DateFormat.getTimeInstance().format(
				Calendar.getInstance().getTime());
		return time;
	}
}

package com.nexters.godofmemo.util;

import java.text.DateFormat;
import java.util.Calendar;

public class Util {
	
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

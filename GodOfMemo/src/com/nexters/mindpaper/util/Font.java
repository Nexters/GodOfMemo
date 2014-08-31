package com.nexters.mindpaper.util;

import android.graphics.Typeface;

public class Font {
	private static Typeface tf;

	public static Typeface getTf() {
		if (tf == null) {
			return Typeface.create("Helvetica", Typeface.BOLD);
		} else {
			return tf;
		}
	}

	public static void setTf(Typeface tf) {
		Font.tf = tf;
	}

}

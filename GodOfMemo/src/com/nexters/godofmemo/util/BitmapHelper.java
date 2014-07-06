package com.nexters.godofmemo.util;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import com.nexters.godofmemo.object.Memo;

public class BitmapHelper {
	
	private static Map<Integer, Bitmap> bitmapCache = new HashMap<Integer, Bitmap>();
	
	//메모 한줄 최대 길이
	private static final int maxLength = 15;	//TODO 최대길이는 임시값.
	//메모 최대 줄 개수
	

	/**
	 * 메모지!!!
	 * @param gContext
	 * @param gResId
	 * @return
	 */
	public static Bitmap drawBitmap(Context gContext, int gResId) {
		Resources resources = gContext.getResources();
		float scale = resources.getDisplayMetrics().density;

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		Bitmap bitmap;
		if(bitmapCache.containsKey(gResId)){
			bitmap = bitmapCache.get(gResId);
		}else{
			// Read in the resource
			bitmap = BitmapFactory
					.decodeResource(resources, gResId, options);
			bitmapCache.put(gResId, bitmap);
		}
		

		return bitmap;
	}
	
	/**
	 * 텍스트를 특정 길이마다 행변환시킨다.
	 * @param text
	 * @return
	 */
	public static String getDividedText(String text){
		StringBuffer sb = new StringBuffer();
		int cnt = 0;
		for(int i=0; i<text.length(); i++){
			char c = text.charAt(i);
			
			if(cnt == maxLength){
				cnt = 1;
				sb.append('\n');
			}else if(c == '\n'){
				cnt = 0;
			}else{
				cnt++;
			}
			
			sb.append(c);
		}
		String dividedText = sb.toString();
		return dividedText;
	}
}

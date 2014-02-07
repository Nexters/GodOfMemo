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

public class BitmapHelper {
	
	private static Map<Integer, Bitmap> bitmapCache = new HashMap<Integer, Bitmap>();

	/**
	 * 이미지에 텍스트를 쓰는 함수
	 * 
	 * @param gContext
	 * @param gResId
	 * @param gText
	 * @return
	 */
	public static Bitmap drawTextToBitmap(Context gContext, int gResId, String gText) {
		Resources resources = gContext.getResources();
		float scale = resources.getDisplayMetrics().density;

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		// Read in the resource
		Bitmap bitmap = BitmapFactory
				.decodeResource(resources, gResId, options);

		
		if(true){
			return bitmap;
		}
		
		android.graphics.Bitmap.Config bitmapConfig = bitmap.getConfig();
		// set default bitmap config if none
		if (bitmapConfig == null) {
			bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
		}
		// resource bitmaps are imutable,
		// so we need to convert it to mutable one
		bitmap = bitmap.copy(bitmapConfig, true);

		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.rgb(61, 61, 61));
		// text size in pixels
		int textSize = (int) (12 * scale);
		paint.setTextSize(textSize);
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
		


		//텍스트를 자른다.
		int maxLength = 5;
		if(gText.length()>maxLength){
			gText = gText.substring(0, maxLength);
			gText += "...";
		}

		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;
		
		// TODO 텍스트를 메모 위 어느 위치에 그릴것인지 정해야 한다.
		canvas.drawText(gText, x, y, paint);

		return bitmap;
	}
	
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
	 * 텍스트만 그리는 함수
	 * @param gContext
	 * @param gResId
	 * @param gText
	 * @return
	 */
	public static Bitmap drawTextToBitmap(String gText) {

		// Read in the resource
		Bitmap bitmap = Bitmap.createBitmap(400, 300, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.rgb(61, 61, 61));
		// text size in pixels
		int textSize = (int) (32);
		paint.setTextSize(textSize);
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
		


		//텍스트를 자른다.
		int maxLength = 5;
		if(gText.length()>maxLength){
			gText = gText.substring(0, maxLength);
			gText += "...";
		}

		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;
		
		// TODO 텍스트를 메모 위 어느 위치에 그릴것인지 정해야 한다.
		canvas.drawText(gText, x, y, paint);

		return bitmap;
	}
}

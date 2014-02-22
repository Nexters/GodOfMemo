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
	private static final int maxLength = 10;	//TODO 최대길이는 임시값.
	//메모 최대 줄 개수
	private static final int maxLine= 3;	//TODO 최대 줄 개수는 임시값.

	/**
	 * 이미지에 텍스트를 쓰는 함수
	 * 
	 * @param context
	 * @param gResId
	 * @param gText
	 * @return
	 */
	public static Bitmap drawTextToBitmap(Context context, int gResId, String gText) {
		Resources resources = context.getResources();
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

		// 폰트설정
		Typeface tf = Typeface.createFromAsset(context.getAssets(), "fonts/NanumBarunGothicBold.ttf");
		paint.setTypeface(tf);
		
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
		
		int width = 512;
		int height = 512;

		// Read in the resource
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// Canvas
		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.rgb(61, 61, 61));
		// text size in pixels
		int textSize = (int) (32);
		paint.setTextSize(textSize);
		// text shadow
		//paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
		
		//##########
		//텍스트 여러줄 처리
		//##########
		String dividedText = getDividedText(gText);
		
		//텍스트를 줄바꿈 단위로 쪼갠다.
		String[] dividedTextArray = dividedText.split("\n");

		// draw text to the Canvas center
		// TODO memo에 적합한 로직.
		int x = (int) (width * Memo.ratioW / 2);
		int y = (int) (height * Memo.ratioH / 2);

		int loopCnt = 0;
		int textOffsetY = 0;
		int margin = 3;
		int offset = (textSize + margin)/1;
		
		//몇번 포문을 수행할지 결정
		if(dividedTextArray.length < maxLine){
			loopCnt = dividedTextArray.length;
		}else{
			loopCnt = maxLine;
		}
		
		//시작 높이 위치 정하기
		textOffsetY = y - (offset/2)*(loopCnt-1) + margin;
		
		//폰트 설정
		paint.setTypeface(Font.getTf());
		
		//여러줄 출력하기
		for(int i=0; i<loopCnt; i++){
			String text = dividedTextArray[i];
			int px = x - (text.length() * textSize)/2;
			int py = textOffsetY + (i*offset);
			canvas.drawText(text, px, py, paint);
		}
		
		return bitmap;
	}
	
	/**
	 * 텍스트를 특정 길이마다 행변환시킨다.
	 * @param text
	 * @return
	 */
	private static String getDividedText(String text){
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

package com.nexters.mindpaper.object.helper;

import static com.nexters.mindpaper.util.Constants.FLOATS_PER_VERTEX;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.FloatMath;

import com.nexters.mindpaper.data.VertexArray;
import com.nexters.mindpaper.object.Group;
import com.nexters.mindpaper.util.BitmapHelper;
import com.nexters.mindpaper.util.Font;
import com.nexters.mindpaper.util.TextureHelper;
import com.nexters.mindpaper.view.MemoGLView;

public class GroupHelper {

	@SuppressWarnings("unused")
	private int texture;
	@SuppressWarnings("unused")
	private int textTexture;

	@SuppressLint("FloatMath")
	public static VertexArray getGroupVertices(int numPoints, float x, float y, float red, float green,
			float blue, float radius) {

		int size = sizeOfCircleInVertices(numPoints);

		float[] VERTEX_DATA = new float[size * FLOATS_PER_VERTEX];
		// Order of coordinates: X, Y, texture's X, texture's Y
		// 일단은 texture 배제.

		int ai = 128;
		float a = ai / 255.0f;
		// rgb 253, 245, 229
		// rgb 140, 211, 156
		int offset = 0;

		// Center point of fan
		VERTEX_DATA[offset++] = x;
		VERTEX_DATA[offset++] = y;
		VERTEX_DATA[offset++] = red; // r
		VERTEX_DATA[offset++] = green; // g
		VERTEX_DATA[offset++] = blue; // b
		VERTEX_DATA[offset++] = a; // a

		// Fan around center point. <= is used because we want to generate
		// the point at the starting angle twice to complete the fan.
		for (int i = 0; i <= numPoints; i++) {

			// radian 계산하기. 360도에 대한 각도의 비율을 통해서 radian 을 구할 수 있다. l = r * 2PI *
			// (angle/360) = r * 2PI * (i/numPoints)
			float angleInRadians = ((float) i / (float) numPoints)
					* ((float) Math.PI * 2f);
			// center = (gx,gy) 이후에 center class 생성.
			VERTEX_DATA[offset++] = x + radius * FloatMath.cos(angleInRadians);
			VERTEX_DATA[offset++] = y + radius * FloatMath.sin(angleInRadians);
			VERTEX_DATA[offset++] = red; // r
			VERTEX_DATA[offset++] = green; // g
			VERTEX_DATA[offset++] = blue; // b
			VERTEX_DATA[offset++] = a; // a
		}

		return new VertexArray(VERTEX_DATA);
	}

	public static VertexArray getTextVertices(float x, float y, float width,
			float height) {
		// System.out.println("setTextVertices");
		float[] VERTEX_DATA_TEXT = new float[24];

		// 중심.
		int s = 0;
		VERTEX_DATA_TEXT[0] = x; // x
		VERTEX_DATA_TEXT[1] = y; // y
		VERTEX_DATA_TEXT[2] = 0.5f;// S
		VERTEX_DATA_TEXT[3] = 0.5f; // T

		// 왼쪽 아래
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 0f; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 1f; // z

		// 오른쪽 아래
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 1f; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 1f; // z

		// 오른쪽 위에
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 1f; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 0f; // z

		// 왼쪽 위에
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 0f; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 0f; // z

		// 왼쪽 아래
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 0f; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 1f; // z

		return new VertexArray(VERTEX_DATA_TEXT);
	}

	// Return size of a circle built out of a triangle fan
	public static int sizeOfCircleInVertices(int numPoints) {
		return 1 + (numPoints + 1);
	}

	public VertexArray setColorVertices(float x, float y, float red,
			float green, float blue, int width, int height) {

		int ai = 128;
		float a = ai / 255.0f;
		// rgb 253, 245, 229
		// rgb 140, 211, 156

		float[] VERTEX_DATA_COLOR = new float[36];

		// Order of coordinates: X, Y, R, G, B

		// point 1
		int s = 0;
		VERTEX_DATA_COLOR[s * 6 + 0] = x; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 2
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 3
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x + width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 4
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x + width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y + height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 5
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y + height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 6
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		return new VertexArray(VERTEX_DATA_COLOR);
	}

	/**
	 * 텍스트만 그리는 함수
	 *
	 * @param gContext
	 * @param gResId
	 * @param gText
	 * @param ratioW
	 * @param ratioH
	 * @param maxLine
	 * @return
	 */
	public static Bitmap drawTextToBitmap(String gText) {

		int width = 384;
		int height = 384;

		// Read in the resource
		Bitmap bitmap = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		// Canvas
		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.rgb(61, 61, 61));
		// text size in pixels
		int textSize = (64);
		paint.setTextSize(textSize);
		// text shadow
		// paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

		// ##########
		// 텍스트 여러줄 처리
		// ##########

		// 텍스트를 줄바꿈 단위로 쪼갠다.
		List<String> dividedTextList = new ArrayList<String>();
		dividedTextList = getParts(gText, 6);
		

		// draw text to the Canvas center
		// TODO group에 적합하도록.
		int x = width / 2;
		int y = height / 2;

		int loopCnt = 0;
		int textoffsetYY = 0;
		int marginY = 4;
		int marginX = 10;
		int offsetY = (textSize + marginY) / 1;

		// 몇번 포문을 수행할지 결정
		int maxLine = 5;
		if (dividedTextList.size() < maxLine) {
			loopCnt = dividedTextList.size();
		} else {
			loopCnt = maxLine;
		}

		// 시작 높이 위치 정하기
		textoffsetYY = y - (offsetY / 2) * (loopCnt - 1) + marginY;

		// 폰트 설정
		paint.setTypeface(Font.getTf());

		// 여러줄 출력하기
		for (int i = 0; i < loopCnt; i++) {
			String text = dividedTextList.get(i);
			int px = x - (text.length() * textSize) / 2 + marginX;
			int py = textoffsetYY + (i * offsetY);
			canvas.drawText(text, px, py, paint);
		}

		return bitmap;
	}

	//
	//

	// 텍스쳐 설정
	public void setTexture(int textureSource, Context context,
			Bitmap textBitmap, int textBitmapId) {
		if (textureSource != 0) {
			// 텍스쳐를 불러보고
			this.texture = TextureHelper.loadTexture(context, textureSource);
		} else if (textBitmap != null) {
			// 비트맵이 있으면 비트맵 텍스쳐를 입힌다.
			this.texture = TextureHelper.loadBitmpTexture(textBitmap,
					textBitmapId);
			this.textTexture = TextureHelper.loadTextBitmpTexture(this);
		}
	}



	/**
	 * 초기 위치를 설정한다.
	 * @param memoGLView
	 * @param memo
	 */
	public static void setInitPosition(MemoGLView memoGLView, Group group) {
		// 새로 생성될 위치.
		float tempX = (memoGLView.mr.width) / 2; // 폰의 보여지는 width 값
		float tempY = (memoGLView.mr.height) / 2;

		float nx = memoGLView.getNormalizedX(tempX);
		float ny = memoGLView.getNormalizedY(tempY);

		group.setX(nx);
		group.setY(ny);

		group.setVertices();
	}
	
    public static List<String> getParts(String string, int partitionSize) {
        List<String> parts = new ArrayList<String>();
        int len = string.length();
        for (int i=0; i<len; i+=partitionSize)
        {
            parts.add(string.substring(i, Math.min(len, i + partitionSize)));
        }
        return parts;
    }
}

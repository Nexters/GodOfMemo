package com.nexters.godofmemo.object.helper;

import static com.nexters.godofmemo.util.Constants.MEMO_MAX_LINE;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.util.BitmapHelper;
import com.nexters.godofmemo.util.Font;
import com.nexters.godofmemo.view.MemoGLView;

public class MemoHelper {

	/**
	 * 위치와 크기를 지정한다. 텍스쳐와 사용할때.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public VertexArray setVertices(float x, float y, int width, int height) {
		// System.out.println("setVertices");

		float[] VERTEX_DATA = new float[24];

		// 중심.
		int s = 0;
		VERTEX_DATA[0] = x; // x
		VERTEX_DATA[1] = y; // y
		VERTEX_DATA[2] = 0.5f; // S
		VERTEX_DATA[3] = 0.5f; // T

		// 왼쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		// 오른쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 1f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		// 오른쪽 위에
		s++;
		VERTEX_DATA[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 1f; // z
		VERTEX_DATA[s * 4 + 3] = 0f; // z

		// 왼쪽 위에
		s++;
		VERTEX_DATA[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 0f; // z

		// 왼쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		return new VertexArray(VERTEX_DATA);
	}

	/**
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 */
	public static VertexArray getTextVertices(float x, float y, float width,
			float height) {
		// System.out.println("setTextVertices");
		float[] VERTEX_DATA_TEXT = new float[24];

		// 중심.
		int s = 0;
		VERTEX_DATA_TEXT[0] = x; // x
		VERTEX_DATA_TEXT[1] = y; // y
		VERTEX_DATA_TEXT[2] = 0.5f; // S
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

	/**
	 * 
	 * @param x
	 * @param y
	 * @param red
	 * @param green
	 * @param blue
	 * @param width
	 * @param height
	 * @return
	 */
	public static VertexArray getColorVertices(float x, float y, float red,
			float green, float blue, float width, float height) {

		float[] VERTEX_DATA_COLOR = new float[36]; // 6(점개수)*6(2위치+4색상)

		// Order of coordinates: X, Y, R, G, B, A

		int ai = 128;
		float alpha = ai / 255.0f;

		// point 1
		int s = 0;
		VERTEX_DATA_COLOR[s * 6 + 0] = x; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = alpha; // a

		// point 2
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = alpha; // a

		// point 3
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x + width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = alpha; // a

		// point 4
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x + width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y + height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = alpha; // a

		// point 5
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y + height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = alpha; // a

		// point 6
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = blue; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = alpha; // a

		return new VertexArray(VERTEX_DATA_COLOR);
	}

	/**
	 * 텍스트만 그리는 함수
	 * 
	 * @param ratioW
	 * @param ratioH
	 * @param gContext
	 * @param gResId
	 * @param gText
	 * @param string
	 * @return
	 */
	public static Bitmap drawTextToBitmap(String title, String content) {
		// TODO 제목과 내용 둘다 제대로 뿌려줘야한다.
		int size = 384;

		// Read in the resource
		Bitmap bitmap = Bitmap.createBitmap(size, size,
				Bitmap.Config.ARGB_8888);
		// Canvas
		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.rgb(61, 61, 61));
		// text shadow
		// paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

		// ##########
		// 텍스트 여러줄 처리
		// ##########
		String dividedText = BitmapHelper.getDividedText(content);

		// 텍스트를 줄바꿈 단위로 쪼갠다.
		String[] dividedTextArray = dividedText.split("\n");

		int textSize = size/16;
		int loopCnt = 0;
		int textOffsetY = 0;
		int margin = size/16;
		int leading = size/16/3;
		int offset = (textSize + leading) / 1;
		int charPerLine = 8;

		// 몇번 포문을 수행할지 결정
		if (dividedTextArray.length < MEMO_MAX_LINE) {
			loopCnt = dividedTextArray.length;
		} else {
			loopCnt = MEMO_MAX_LINE + 1;
			String[] tempArray = new String[dividedTextArray.length + 1];
			System.arraycopy(dividedTextArray, 0, tempArray, 0,
					dividedTextArray.length);
			tempArray[MEMO_MAX_LINE] = "...";
			dividedTextArray = tempArray;
		}

		// 시작 높이 위치 정하기
		// textOffsetY = y - (offset / 2) * (loopCnt - 1) + margin;

		// 폰트 설정
		paint.setTypeface(Font.getTf());

		// 타이틀을 먼저 그린다..
		// 타이틀 길이
		String title1 = title;
		String title2 = "";
		if (title.length() > charPerLine) {
			title1 = title.substring(0, charPerLine);

			// 두줄일 때 처리한다.
			if (title.length() > charPerLine * 2) {
				title2 = title.substring(charPerLine, charPerLine * 2);
			} else {
				title2 = title.substring(charPerLine, title.length());
			}
		}

		int titleSize = textSize*2;
		paint.setTextSize(titleSize);
		textOffsetY = titleSize + margin;
		canvas.drawText(title1, margin, textOffsetY, paint);
		// 텍스트가 2줄일 때 출력한다.
		if (!"".equals(title2)) {
			textOffsetY += titleSize + margin / 2;
			canvas.drawText(title2, margin, textOffsetY, paint);
		}
		// 하단여백.
		textOffsetY += margin * 2 / 3;

		// 여러줄 출력하기
		paint.setTextSize(textSize);
		for (int i = 0; i < loopCnt; i++) {
			String text = dividedTextArray[i];
			int px = margin;
			int py = textOffsetY + ((i + 1) * offset);
			canvas.drawText(text, px, py, paint);
		}

		return bitmap;
	}

	/**
	 * 초기 위치를 설정한다.
	 * 
	 * @param memoGLView
	 * @param memo
	 */
	public static void setInitPosition(MemoGLView memoGLView, Memo memo) {
		// 새로 생성될 위치.
		float tempX = (memoGLView.mr.width) / 2; // 폰의 보여지는 width 값
		float tempY = (memoGLView.mr.height) / 2;

		float nx = memoGLView.getNormalizedX(tempX);
		float ny = memoGLView.getNormalizedY(tempY);

		memo.setX(nx);
		memo.setY(ny);

		memo.setVertices();
	}
}

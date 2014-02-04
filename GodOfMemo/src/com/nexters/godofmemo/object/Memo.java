package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.BYTES_PER_FLOAT;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.nexters.godofmemo.R;
import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.TextureHelper;

public class Memo {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	private static float[] VERTEX_DATA;
	private VertexArray vertexArray;
	
	//위치, 크기정보
	public float px;
	public float py;
	public float pWidth;
	public float pHeight;
	
	//텍스쳐 정보
	public int texture;
	
	//텍스쳐 원본
	public int textureSource;
	
	//텍스트를 입력한 비트맵
	public Bitmap textBitmap;
	
	//텍스쳐 설정에 필요한 변수
	private Context context;
	
	/**
	 * 위치와 크기를 지정한다
	 */
	public void setVertices() {

		VERTEX_DATA = new float[24];

		// top left
		int s = 0;
		VERTEX_DATA[0] = px; // x
		VERTEX_DATA[1] = py; // y
		VERTEX_DATA[2] = 0.5f; // S
		VERTEX_DATA[3] = 0.5f; // T

		// 왼쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = px - pWidth / 2; // x
		VERTEX_DATA[s * 4 + 1] = py - pHeight / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		// 오른쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = px + pWidth / 2; // x
		VERTEX_DATA[s * 4 + 1] = py - pHeight / 2; // y
		VERTEX_DATA[s * 4 + 2] = 1f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		// 오른쪽 위에 
		s++;
		VERTEX_DATA[s * 4 + 0] = px + pWidth / 2; // x
		VERTEX_DATA[s * 4 + 1] = py + pHeight / 2; // y
		VERTEX_DATA[s * 4 + 2] = 1f; // z
		VERTEX_DATA[s * 4 + 3] = 0f; // z

		// 왼쪽 위에
		s++;
		VERTEX_DATA[s * 4 + 0] = px - pWidth / 2; // x
		VERTEX_DATA[s * 4 + 1] = py + pHeight / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 0f; // z

		// 왼쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = px - pWidth / 2; // x
		VERTEX_DATA[s * 4 + 1] = py - pHeight / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		vertexArray = new VertexArray(VERTEX_DATA);
	}

	// 텍스쳐 설정
	public void setTexture() {
		if (textureSource != 0) {
			// 텍스쳐를 불러보고
			this.texture = TextureHelper.loadTexture(context, textureSource);
		} else if (textBitmap != null) {
			// 비트맵이 있으면 비트맵 텍스쳐를 입힌다.
			this.texture = TextureHelper.loadBitmpTexture(textBitmap);
		}
	}

	/**
	 * 저장된 메모를 그린다
	 * @param context
	 * 컨텍스트
	 * @param px
	 * 
	 * @param py
	 * @param pWidth
	 * @param pHeight
	 * @param text
	 */
	public Memo(Context context, float px, float py, float pWidth, float pHeight, String text) {
		this.context = context;
		this.px = px;
		this.py = py;
		this.pWidth = pWidth;
		this.pHeight = pHeight;
		this.textBitmap = drawTextToBitmap(context, R.drawable.whitememo, text);
		
		setVertices();
	}
	
	//신규입력시
	public Memo(Context context, String text) {
		this.context = context;
		this.px = 0;
		this.py = 0;
		this.pWidth = 0.5f;
		this.pHeight = 0.5f;
		this.textBitmap = drawTextToBitmap(context, R.drawable.whitememo, text);
		
		setVertices();
		
	}

	public void bindData(TextureShaderProgram textureProgram) {
		vertexArray.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoordinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
	}

	public void draw() {
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
	
	/**
	 * 이미지에 텍스트를 쓰는 함수
	 * 
	 * @param gContext
	 * @param gResId
	 * @param gText
	 * @return
	 */
	public Bitmap drawTextToBitmap(Context gContext, int gResId, String gText) {
		Resources resources = gContext.getResources();
		float scale = resources.getDisplayMetrics().density;

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		// Read in the resource
		Bitmap bitmap = BitmapFactory.decodeResource(resources, gResId,
				options);

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
		paint.setTextSize((int) (32 * scale));
		// text shadow
		paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);

		// draw text to the Canvas center
		Rect bounds = new Rect();
		paint.getTextBounds(gText, 0, gText.length(), bounds);
		int x = (bitmap.getWidth() - bounds.width()) / 2;
		int y = (bitmap.getHeight() + bounds.height()) / 2;

		//TODO 텍스트를 메모 위 어느 위치에 그릴것인지 정해야 한다.
		canvas.drawText(gText, x, y, paint);

		return bitmap;
	}
}
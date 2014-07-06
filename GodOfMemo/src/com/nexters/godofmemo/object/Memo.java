package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.GL_POINTS;
import static android.opengl.GLES20.GL_TRIANGLES;
import static android.opengl.GLES20.GL_UNSIGNED_INT;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.BYTES_PER_FLOAT;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.nexters.godofmemo.R;
import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.object.helper.MemoHelper;
import com.nexters.godofmemo.programs.ColorShaderProgram;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.BitmapHelper;
import com.nexters.godofmemo.util.Font;
import com.nexters.godofmemo.util.TextureHelper;
import com.nexters.godofmemo.view.MemoGLView;

public class Memo {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	private static float[] VERTEX_DATA;
	private VertexArray vertexArray;

	// 텍스트 입력을 위한 정보
	private static float[] VERTEX_DATA_TEXT;
	private VertexArray vertexArrayText;

	// TODO 최대 줄 개수는 임시값.
	private static final int maxLine = 7;

	// 기본정보
	private String memoId;
	private String memoTitle;
	private String memoContent;
	private String memoDate;
	private String memoTime;
	private int memoColor;
	private String groupId;

	// 위치, 크기정보
	private float x;
	private float y;
	private float width;
	private float height;

	// 색깔 동적으로.
	private float red;
	private float green;
	private float blue;

	// 생성시
	private long prodTime = 0;

	// 글씨 텍스처
	public int textTexture;
	// 텍스트를 입력한 비트맵
	public Bitmap textBitmap;
	// 비트맵 아이디
	public int textBitmapId;

	// 텍스쳐 설정에 필요한 변수
	private Context context;
	private VertexArray vertexArrayColor;

	// 텍스트가 들어갈 상자의 비율
	public static float ratioW = 10f / 10f;
	public static float ratioH = 10f / 10f;

	public static final float ratioMarginTop = 150f / 512f;
	public static final float ratioMarginBottom = 160f / 512f;
	public static final float ratioMarginLeft = 15f / 512f;

	/**
	 * 위치와 크기를 지정한다
	 */
	public void setVertices() {

		// 글자저장을 위한 저장...
		vertexArrayText = MemoHelper.getTextVertices(x, y, width, height,
				ratioW, ratioH);

		// color vertex
		vertexArrayColor = MemoHelper.getColorVertices(x, y, red, green, blue,
				width, height);
	}

	// 색깔을 설정한다.
	public void setColor(int ri, int gi, int bi) {
		// rgb 253, 245, 229
		// rgb 140, 211, 156
		red = ri / 255.0f;
		green = gi / 255.0f;
		blue = bi / 255.0f;
	}

	/**
	 * 생성자
	 * 
	 * @param context
	 */
	public Memo(Context context) {
		this.context = context;
	}

	// 신규입력시
	public Memo(Context context, MemoGLView memoGLView, String title,
			String text) {
		this.context = context;
		// 제목과 내용 채우고
		setMemoTitle(title);
		setMemoContent(text);

		// 위치와 크기
		//setWidth(0.8f);
		//setHeight(0.8f);

		float tempX = (memoGLView.mr.width) / 2; // 폰의 보여지는 width 값
		float tempY = (memoGLView.mr.height) / 2;

		float nx = memoGLView.getNormalizedX(tempX);
		float ny = memoGLView.getNormalizedY(tempY);

		setX(nx);
		setY(ny);

		setVertices();

	}

	// 텍스쳐 설정
	public void setTexture() {
		// text texture
		this.textTexture = TextureHelper.loadTextBitmpTexture(this);
	}

	private static final int COLOR_COMPONENT_COUNT = 3;
	private static final int COLOR_STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	public void drawMemo(ColorShaderProgram colorProgram) {

		vertexArrayColor.setVertexAttribPointer(0,
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, COLOR_STRIDE);

		vertexArrayColor.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				colorProgram.getColorAttributeLocation(),
				COLOR_COMPONENT_COUNT, COLOR_STRIDE);

		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}

	public void drawText(TextureShaderProgram textureProgram) {
		// 텍스트에 대한 처리...
		vertexArrayText.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArrayText.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoordinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);

		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}

	// ##############
	// Getter, Setter
	// ##############

	public String getMemoId() {
		if (memoId == null) {
			return "";
		} else {
			return memoId;
		}
	}

	public void setMemoId(String memoId) {
		this.memoId = memoId;
	}

	public String getMemoTitle() {
		return memoTitle;
	}

	public void setMemoTitle(String memoTitle) {
		this.memoTitle = memoTitle;
	}

	public String getMemoContent() {
		return memoContent;
	}

	public void setMemoContent(String memoContent) {
		if (memoContent == null) {
			memoContent = "test";
		}
		this.memoContent = memoContent;
	}

	public String getMemoDate() {
		return memoDate;
	}

	public void setMemoDate(String memoDate) {
		this.memoDate = memoDate;
	}

	public String getMemoTime() {
		return memoTime;
	}

	public void setMemoTime(String memoTime) {
		this.memoTime = memoTime;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public long getProdTime() {

		return prodTime;
	}

	public void setProdTime(long prodTime) {
		this.prodTime = prodTime;
	}

	public int getMemoColor() {
		return memoColor;
	}

	// 메모 색깔...
	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Memo) {
			Memo t = (Memo) o;
			if (this.getMemoId().equals(t.getMemoId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return super.equals(o);
		}
	}

	public static Bitmap drawTextToBitmap(String memoTitle, String memoContent) {

		return MemoHelper.drawTextToBitmap(memoTitle, memoContent, ratioW,
				ratioH, maxLine);
	}
}
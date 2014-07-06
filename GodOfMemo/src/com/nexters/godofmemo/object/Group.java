package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.BYTES_PER_FLOAT;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import com.nexters.godofmemo.R;
import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.object.helper.GroupHelper;
import com.nexters.godofmemo.programs.ColorShaderProgram;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.BitmapHelper;
import com.nexters.godofmemo.view.MemoGLView;

public class Group {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	private static float[] VERTEX_DATA;
	private VertexArray vertexArray;

	// 텍스트 입력을 위한 정보
	private static float[] VERTEX_DATA_TEXT;
	private VertexArray vertexArrayText;

	// Circle 형태로 그릴 때 필요한 변수들.
	private static final int FLOATS_PER_VERTEX = 6;
	private int offset = 0;
	private float radius;
	private int numPoints;

	// 기본정보
	// 그룹의 기본 정보
	private String groupId;
	private int groupColor; // 일단은 case를 나누는 용도. 단계를 나눌 필요가 없다면 삭제.
	private String groupTitle;
	private String groupSymbolId;
	private HashMap<String, Memo> groupMemoList; // memoId를 key로 Memo를 value로
													// 정리.
	// Symbol 에 대한 논의도 필요.
	private String groupDate;
	private String groupTime;

	// 색상정보
	private float red;
	private float green;
	private float blue;

	// 크기 기본값
	public static final float GROUP_DEFAULT_SIZE = 0.8f;

	// 위치, 크기정보
	private float x;
	private float y;
	private float width;
	private float height;

	// TODO 최대 줄 개수는 임시값.
	private static final int maxLine = 3;

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
	public static float ratioW = 8f / 10f;
	public static float ratioH = 6f / 10f;

	/**
	 * 위치와 크기를 지정한다
	 */
	@SuppressLint("FloatMath")
	public void setVertices() {

		VERTEX_DATA = GroupHelper.getGroupVertices(numPoints,
				FLOATS_PER_VERTEX, x, y, red, green, blue, radius);
		vertexArray = new VertexArray(VERTEX_DATA);

		// 글자저장을 위한 저장...
		setTextVertices();
	}

	private void setTextVertices() {

		VERTEX_DATA_TEXT = GroupHelper.getTextVertices(x, y, width, height,
				ratioW, ratioH);

		vertexArrayText = new VertexArray(VERTEX_DATA_TEXT);
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
	public Group(Context context) {
		this.context = context;
		this.radius = 2f;
		this.numPoints = 365;
	}

	// 신규입력시
	public Group(Context context, String text, int colorMarker,
			float groupSize, MemoGLView memoGLView) {
		this.context = context;
		// circle size.
		this.radius = 2f;
		this.numPoints = 365;

		// input title and color
		setGroupTitle(text);

		// TODO Need a selecting logic that finds appropriate color.

		float tempX = (memoGLView.mr.width) / 2; // 폰의 보여지는 width 값
		float tempY = (memoGLView.mr.height) / 2;

		float nx = memoGLView.getNormalizedX(tempX);
		float ny = memoGLView.getNormalizedY(tempY);

		setX(nx);
		setY(ny);

		setVertices();

	}

	public void drawTitle(TextureShaderProgram textureProgram) {
		// 텍스트에 대한 처리...
		vertexArrayText.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArrayText.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoordinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);

		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}

	private static final int COLOR_COMPONENT_COUNT = 4;
	private static final int COLOR_STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	public void drawGroup(ColorShaderProgram colorProgram) {
		vertexArray.setVertexAttribPointer(0,
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, COLOR_STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				colorProgram.getColorAttributeLocation(),
				COLOR_COMPONENT_COUNT, COLOR_STRIDE);

		final int startVertex = offset / FLOATS_PER_VERTEX;
		final int numVertices = GroupHelper.sizeOfCircleInVertices(numPoints);

		glDrawArrays(GL_TRIANGLE_FAN, 0, numVertices);

		// glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}

	// ##############
	// Getter, Setter
	// ##############

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getGroupColor() {
		return groupColor;
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		if (groupTitle == null) {
			groupTitle = "test";
		}
		this.groupTitle = groupTitle;
	}

	public String getGroupSymbolId() {
		return groupSymbolId;
	}

	public void setGroupSymbolId(String groupSymbolId) {
		this.groupSymbolId = groupSymbolId;
	}

	public HashMap<String, Memo> getGroupMemoList() {
		return groupMemoList;
	}

	public void setGroupMemoList(HashMap<String, Memo> groupMemoList) {
		this.groupMemoList = groupMemoList;
	}

	public String getGroupDate() {
		return groupDate;
	}

	public void setGroupDate(String groupDate) {
		this.groupDate = groupDate;
	}

	public String getGroupTime() {
		return groupTime;
	}

	public void setGroupTime(String groupTime) {
		this.groupTime = groupTime;
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

	public static Bitmap drawTextToBitmap(String groupTitle) {

		return null;
	}

	// ////////////////////////
	// //////////////////////////
	// //////////////////////

}
package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.COLOR_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.COLOR_STRIDE;
import static com.nexters.godofmemo.util.Constants.POSITION_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.STRIDE;
import static com.nexters.godofmemo.util.Constants.TEXTURE_COORDINATES_COMPONENT_COUNT;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;

import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.object.helper.GroupHelper;
import com.nexters.godofmemo.programs.ColorShaderProgram;
import com.nexters.godofmemo.programs.TextureShaderProgram;

public class Group extends MovableObject {

	// 그룹배경 위치정보 저장.
	private VertexArray vertexArray;
	// 그룹제목을 그리기 위한 위치정보 저장.
	private VertexArray vertexArrayText;

	// Circle 형태로 그릴 때 필요한 변수들.
	private final int numPoints = 70;

	// 글씨 텍스처
	public int textTexture; // 렌더러에서 참조.

	// ##########################
	// *********************
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

	// 크기 기본값
	private final float radius = 2f;

	// *********************
	// ##########################

	/**
	 * 생성자
	 *
	 * @param context
	 */
	public Group() {
	}

	/**
	 * 위치와 크기를 지정한다
	 */
	@Override
	@SuppressLint("FloatMath")
	public void setVertices() {

		vertexArray = GroupHelper.getGroupVertices(numPoints, x, y, red, green,
				blue, radius);

		// 글자저장을 위한 저장...
		vertexArrayText = GroupHelper.getTextVertices(x, y, width, height);
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

	public void drawGroup(ColorShaderProgram colorProgram) {

		vertexArray.setVertexAttribPointer(0,
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, COLOR_STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				colorProgram.getColorAttributeLocation(),
				COLOR_COMPONENT_COUNT, COLOR_STRIDE);

		// final int startVertex = offset / FLOATS_PER_VERTEX;
		final int numVertices = GroupHelper.sizeOfCircleInVertices(numPoints);

		glDrawArrays(GL_TRIANGLE_FAN, 0, numVertices);

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

	public static Bitmap drawTextToBitmap(String groupTitle) {

		return null;
	}

	// ////////////////////////
	// //////////////////////////
	// //////////////////////

}
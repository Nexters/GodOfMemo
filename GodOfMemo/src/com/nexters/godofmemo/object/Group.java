package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.COLOR_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.COLOR_STRIDE;
import static com.nexters.godofmemo.util.Constants.POSITION_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.STRIDE;
import static com.nexters.godofmemo.util.Constants.TEXTURE_COORDINATES_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.numPoints;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.os.Parcel;
import android.os.Parcelable;

import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.object.helper.GroupHelper;
import com.nexters.godofmemo.programs.ColorShaderProgram;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.TextureHelper;

public class Group extends MovableObject implements Parcelable {

	// 그룹배경 위치정보 저장.
	private VertexArray vertexArray;
	// 그룹제목을 그리기 위한 위치정보 저장.
	private VertexArray vertexArrayText;

	// 글씨 텍스처
	public int textTexture; // 렌더러에서 참조.

	// ##########################
	// *********************
	// 기본정보
	// 그룹의 기본 정보
	private String groupId;
	private String groupTitle;
	private HashMap<String, Memo> groupMemoList; // memoId를 key로 Memo를 value로
													// 정리.
	private String groupDate;
	private String groupTime;

	private float radius = 2f;

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
	 * Parcelable로 만들어질 때.
	 *
	 * @param src
	 */
	public Group(Parcel src) {
		readFromParcel(src);
	}

	/**
	 * 위치와 크기를 지정한다
	 */
	@Override
	@SuppressLint("FloatMath")
	public void setVertices() {

		vertexArray = GroupHelper.getGroupVertices(numPoints, x, y, red, green,
				blue, radius);

		//글자를 저장할 텍스쳐의 크기는 어느정도로 잡아야 하는가?
		width = 0.8f;
		height = 0.8f;
		// 글자저장을 위한 저장...
		vertexArrayText = GroupHelper.getTextVertices(x, y, width, height);
	}

	/**
	 * 그룹 제목을 그린다.
	 */
	public void setGroupTitleTexture() {
		this.textTexture = TextureHelper.loadTextBitmpTexture(this);
		
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

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		if (groupTitle == null) {
			groupTitle = "test";
		}
		this.groupTitle = groupTitle;
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

	public float getRadius() {
		return radius;
	}

	public void setRadius(float radius) {
		this.radius = radius;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof Group) {
			Group t = (Group) o;
			if (this.getGroupId().equals(t.getGroupId())) {
				return true;
			} else {
				return false;
			}
		} else {
			return super.equals(o);
		}
	}

	@Override
	public String toString() {
		return "Group [vertexArray=" + vertexArray + ", vertexArrayText="
				+ vertexArrayText + ", textTexture=" + textTexture
				+ ", groupId=" + groupId + ", groupTitle=" + groupTitle
				+ ", groupMemoList=" + groupMemoList + ", groupDate="
				+ groupDate + ", groupTime=" + groupTime + ", radius=" + radius
				+ ", x=" + x + ", y=" + y + ", width=" + width + ", height="
				+ height + ", red=" + red + ", green=" + green + ", blue="
				+ blue + "]";
	}

	// ################
	// 소포!!!

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {

		// 기본정보
		dest.writeString(groupId);
		dest.writeString(groupTitle);

		dest.writeString(groupDate);
		dest.writeString(groupTime);

		dest.writeFloat(radius);

		// 위치,크기,색상정보
		dest.writeFloat(x);
		dest.writeFloat(y);
		dest.writeFloat(width);
		dest.writeFloat(height);
		dest.writeFloat(red);
		dest.writeFloat(green);
		dest.writeFloat(blue);

	}

	/**
	 * 소포에서 자료를 꺼내온다!
	 *
	 * @param in
	 */
	private void readFromParcel(Parcel in) {
		groupId = in.readString();
		groupTitle = in.readString();

		groupDate = in.readString();
		groupTime = in.readString();

		radius = in.readFloat();

		x = in.readFloat();
		y = in.readFloat();
		width = in.readFloat();
		height = in.readFloat();
		red = in.readFloat();
		green = in.readFloat();
		blue = in.readFloat();

	}

	public static final Parcelable.Creator<Group> CREATOR = new Parcelable.Creator<Group>() {
		@Override
		public Group createFromParcel(Parcel src) {
			return new Group(src);
		}

		@Override
		public Group[] newArray(int size) {
			return new Group[size];
		}
	};

}
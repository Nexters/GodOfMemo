package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.COLOR_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.COLOR_STRIDE;
import static com.nexters.godofmemo.util.Constants.POSITION_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.STRIDE;
import static com.nexters.godofmemo.util.Constants.TEXTURE_COORDINATES_COMPONENT_COUNT;

import org.joda.time.DateTime;
import org.joda.time.Duration;
import org.joda.time.Interval;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.nexters.godofmemo.R;
import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.object.helper.MemoHelper;
import com.nexters.godofmemo.programs.ColorShaderProgram;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.TextureHelper;

/**
 * 메모!
 *
 * @author lifenjoy51
 *
 */
public class Memo extends MovableObject implements Parcelable {
	// opengl로 그리기 위해 필요한 변수들.
	private VertexArray vertexArrayMemoBg; // 메모지.
	private VertexArray vertexArrayMemoText; // 글씨.

	// 텍스처
	public int textTexture; // 글씨 텍스쳐. 렌더러에서 접근한다.
	public int borderTexture; // 테두리 텍스쳐. 렌더러에서 접근한다.

	// 메모가 그룹안에 있는지 체크하기 위한 변수들. --문규
	public static final float ratioMarginTop = 150f / 512f;
	public static final float ratioMarginBottom = 160f / 512f;
	public static final float ratioMarginLeft = 15f / 512f;

	// ##########################
	// *********************
	// 메모에 대한 정보들.
	// 기본정보 시작.
	private String memoId;
	private String memoTitle;
	private String memoContent;
	private String memoDate;
	private String memoTime;
	private String groupId;

	// 기본정보 끝.
	// ******************************
	// ##########################
	
	private boolean isNew = false;

	/**
	 * 기존에 있던 메모들을 생성할때 사용.
	 */
	public Memo() {
		// 메모 크기지정.
		this.width = 0.4f;
		this.height = 0.4f;
	}

	/**
	 * Parcelable로 만들어질 때.
	 *
	 * @param src
	 */
	public Memo(Parcel src) {
		readFromParcel(src);
	}

	/**
	 * OpenGL위에 그리기 위한 위치정보 + (텍스트정보 or 색상정보)를 입력한다.
	 */
	@Override
	public void setVertices() {

		// 메모지를 그리기 위한 위치정보 + 색상정보 입력.
		vertexArrayMemoBg = MemoHelper.getColorVertices(x, y, red, green, blue,
				width, height);

		// 메모내용을 그리기 위한 위치정보 + 텍스쳐위치정보를 입력한다.
		vertexArrayMemoText = MemoHelper.getTextVertices(x, y, width, height);

	}

	/**
	 * 메모내용을 텍스쳐로 저장한다.
	 */
	public void setMemoContentTexture() {
		this.textTexture = TextureHelper.loadTextBitmpTexture(this);
	}
	
	/**
	 *  메모 테두리 텍스쳐를 저장한다.
	 * @param context 
	 */
	public void setBorderTexture(Context context) {
		this.borderTexture = TextureHelper.loadTexture(context, R.drawable.round);
	}

	/**
	 * 메모지를 그린다.
	 *
	 * @param colorProgram
	 */
	public void drawMemo(ColorShaderProgram colorProgram) {

		vertexArrayMemoBg.setVertexAttribPointer(0,
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, COLOR_STRIDE);

		vertexArrayMemoBg.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				colorProgram.getColorAttributeLocation(),
				COLOR_COMPONENT_COUNT, COLOR_STRIDE);

		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}

	/**
	 * 메모내용을 그린다.
	 *
	 * @param textureProgram
	 */
	public void drawText(TextureShaderProgram textureProgram) {

		// 텍스트에 대한 처리...
		vertexArrayMemoText.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArrayMemoText.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
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
		if(memoTitle == null){
			memoTitle = "메모제목";
		}
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

	@Override
	public String toString() {
		return "Memo [vertexArrayMemoBg=" + vertexArrayMemoBg
				+ ", vertexArrayMemoText=" + vertexArrayMemoText
				+ ", textTexture=" + textTexture + ", memoId=" + memoId
				+ ", memoTitle=" + memoTitle + ", memoContent=" + memoContent
				+ ", memoDate=" + memoDate + ", memoTime=" + memoTime
				+ ", groupId=" + groupId + ", x=" + x + ", y=" + y + ", width="
				+ width + ", height=" + height + ", red=" + red*255f + ", green="
				+ green*255f + ", blue=" + blue*255f + "]";
	}

	// ######################
	// #############
	// Parcelable

	@Override
	public int describeContents() {
		// 자식 클래스가 있을 때 사용한다고 한다.
		return 0;
	}

	/**
	 * 소포에 자료를 담는다!
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {

		// 기본정보
		dest.writeString(memoId);
		dest.writeString(memoTitle);
		dest.writeString(memoContent);
		dest.writeString(memoDate);
		dest.writeString(memoTime);
		dest.writeString(groupId);

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
		memoId = in.readString();
		memoTitle = in.readString();
		memoContent = in.readString();
		memoDate = in.readString();
		memoTime = in.readString();
		groupId = in.readString();

		x = in.readFloat();
		y = in.readFloat();
		width = in.readFloat();
		height = in.readFloat();
		red = in.readFloat();
		green = in.readFloat();
		blue = in.readFloat();

	}

	public static final Parcelable.Creator<Memo> CREATOR = new Parcelable.Creator<Memo>() {
		@Override
		public Memo createFromParcel(Parcel src) {
			return new Memo(src);
		}

		@Override
		public Memo[] newArray(int size) {
			return new Memo[size];
		}
	};

	/**
	 * 시간을 계산해서 새로운 메모여부를 판별한다.
	 * @param context 
	 */
	public void chkNewStatus(Context context) {
		// isNew
		if(this.memoDate != null && this.memoTime != null){

			String[] memoDates = this.memoDate.split("\\.");
			String memoTime = this.memoTime.substring(this.memoTime.indexOf(" "), this.memoTime.length());
			String dayAndNight = this.memoTime.substring(0, 2);
			String[] memoTimes = memoTime.split(":");
			
			Log.i("isNew", this.memoTitle);
			Log.i("isNew", this.memoDate);
			Log.i("isNew", this.memoTime);
			
			
			int year = Integer.parseInt(memoDates[0].trim());
			int month = Integer.parseInt(memoDates[1].trim());
			int day = Integer.parseInt(memoDates[2].trim());
			
			int hour = Integer.parseInt(memoTimes[0].trim());
			int minute = Integer.parseInt(memoTimes[1].trim());
			int second = Integer.parseInt(memoTimes[2].trim());
			
			//오전오후 검사
			if(dayAndNight.equals("오후")){
				hour += 12;
			}
			
			DateTime made = new DateTime(year, month, day, hour, minute, second);
			DateTime now = DateTime.now();
			
			Interval interval = new Interval(made, now);
			Duration duration = interval.toDuration();
			long memoAge = duration.getMillis();
			
			Log.i("isNew",String.valueOf(memoAge));
			
			//5분 아래면. 
			if(memoAge < 1000*60*1){
				this.isNew = true;
				setBorderTexture(context);
				Log.i("isNew","isNew!!!");
			}else{
				this.isNew = false;
			}
			
		}
		
	}

	/**
	 * 새 메모 판별로직.
	 * @return
	 */
	public boolean isNew() {
		return isNew;
	}

}
package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.BYTES_PER_FLOAT;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;

import com.nexters.godofmemo.R;
import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.BitmapHelper;
import com.nexters.godofmemo.util.TextureHelper;
import com.nexters.godofmemo.view.MemoGLView;

public class Group {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	private static float[] VERTEX_DATA;
	private VertexArray vertexArray;
	
	//텍스트 입력을 위한 정보
	private static float[] VERTEX_DATA_TEXT;
	private VertexArray vertexArrayText;
	
	//기본정보
	//그룹의 기본 정보 
	private String groupId;
	private int groupColor; //일단은 case를 나누는 용도. 단계를 나눌 필요가 없다면 삭제.
	private String groupTitle;
	private String groupSymbolId;
	private HashMap<String,Memo> groupMemoList; //memoId를 key로 Memo를 value로 정리.
	// Symbol 에 대한 논의도 필요. 
	private String groupDate;
	private String groupTime;	
	
	//위치, 크기정보
	private float x;
	private float y;
	private float width;
	private float height;
	
	//생성시
	private long prodTime=0;
	
	//텍스쳐 정보
	public int texture;
	//글씨 텍스처
	public int textTexture;
	
	//텍스쳐 원본
	public int textureSource;
	
	//텍스트를 입력한 비트맵
	public Bitmap textBitmap;
	//비트맵 아이디
	public int textBitmapId;
	
	//텍스쳐 설정에 필요한 변수
	private Context context;
	
	/**
	 * 위치와 크기를 지정한다
	 */
	public void setVertices() {
		//System.out.println("setVertices");

		VERTEX_DATA = new float[24];

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

		vertexArray = new VertexArray(VERTEX_DATA);
		
		//글자저장을 위한 저장...
		setTextVertices();
	}
	
	private void setTextVertices(){
		//System.out.println("setTextVertices");
		VERTEX_DATA_TEXT = new float[24];
		
		float x = this.x;
		float y = this.y;
		float width = this.width;
		float height = this.height;

		width = width * 8 / 10;
		height = height * 6 / 10;

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

		vertexArrayText = new VertexArray(VERTEX_DATA_TEXT);
	}

	// 텍스쳐 설정
	public void setTexture() {
		if (textureSource != 0) {
			// 텍스쳐를 불러보고
			this.texture = TextureHelper.loadTexture(context, textureSource);
		} else if (textBitmap != null) {
			// 비트맵이 있으면 비트맵 텍스쳐를 입힌다.
			this.texture = TextureHelper.loadBitmpTexture(textBitmap, textBitmapId);
			this.textTexture = TextureHelper.loadTextBitmpTexture(groupTitle);
		}
	}

	/**
	 * 생성자
	 * 
	 * @param context
	 */
	public Group(Context context){
		this.context = context;
	}
	
	//신규입력시
	public Group(Context context, String text, int colorMarker, MemoGLView memoGLView ) {
		this.context = context;
		//input title and color
		setGroupTitle(text);
		setGroupColor(colorMarker);
		// TODO Need a selecting logic that finds appropriate color.
		
		//위치와 크기
		setWidth(0.8f);
		setHeight(0.8f);
		
		float tempX = (memoGLView.mr.width)/2; //폰의 보여지는 width 값 
		float tempY = (memoGLView.mr.height)/2;
		
		float nx = memoGLView.getNormalizedX(tempX);
		float ny = memoGLView.getNormalizedY(tempY);
		
		setX(nx);
		setY(ny);
		
		setVertices();
		
	}

	public void drawGroup(TextureShaderProgram textureProgram) {
		//메모지 그리기
		if(vertexArray == null){
			//System.out.println("vertexArray null!!!!"+this.getMemoContent());
		}else{
			//System.out.println("vertexArray not null....."+this.getMemoContent());
		}
		vertexArray.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoordinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
	
	public void drawTitle(TextureShaderProgram textureProgram) {
	//텍스트에 대한 처리...
		vertexArrayText.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArrayText.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoordinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
	
	//##############
	// Getter, Setter
	//##############

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getGroupColor() {
		return groupColor;
	}

	public void setGroupColor(int groupColor) {
		this.groupColor = groupColor;
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		if(groupTitle == null){
			groupTitle = "test";
		}

		//creating bitmap that containing a title of group.
		this.textBitmapId = R.drawable.whitememo;
		this.textBitmap = BitmapHelper.drawBitmap(context, this.textBitmapId);
		
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
	
}
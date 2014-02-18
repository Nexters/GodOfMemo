package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.BYTES_PER_FLOAT;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.FloatMath;

import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.TextureHelper;

public class Circle {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;
	
	//Circle 형태로 그릴 때 필요한 변수들. 
	private static final int FLOATS_PER_VERTEX = 4; 
	private int offset = 0;
	
	//Vertex 정보를 담는 곳.
	private static float[] VERTEX_DATA;
	private VertexArray vertexArray;
	
	//그룹의 기본 정보 
	private String groupId;
	private int groupColor; //일단은 case를 나누는 용도. 단계를 나눌 필요가 없다면 삭제.
	private String groupTitle;
	private HashMap<String,Memo> groupMemoList; //memoId를 key로 Memo를 value로 정리.
	// Symbol 에 대한 논의도 필요. 
	
	//그룹 위치, 크기정보
	private float gx;
	private float gy;
	private float radius;
	private int numPoints;
	
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
	@SuppressLint("FloatMath")
	public void setVertices() {
		int size = sizeOfCircleInVertices(numPoints);
		
        
		VERTEX_DATA = new float[size * FLOATS_PER_VERTEX];
		// Order of coordinates: X, Y, texture's X, texture's Y
		// 일단은 texture 배제. 
		
		// Center point of fan
	    VERTEX_DATA[offset++] = gx; 
	    VERTEX_DATA[offset++] = gy;
	    VERTEX_DATA[offset++] = 0.5f; 
	    VERTEX_DATA[offset++] = 0.5f;
	   

	    // Fan around center point. <= is used because we want to generate 
	    // the point at the starting angle twice to complete the fan.
	    for (int i = 0; i <= numPoints; i++) {
	    
	    // radian 계산하기. 360도에 대한 각도의 비율을 통해서 radian 을 구할 수 있다. l = r * 2PI * (angle/360) = r * 2PI * (i/numPoints)
	    float angleInRadians = ((float) i / (float) numPoints) * ((float) Math.PI * 2f);
	    // center = (gx,gy) 이후에 center class 생성. 
	    VERTEX_DATA[offset++] = gx + radius * FloatMath.cos(angleInRadians);
	    VERTEX_DATA[offset++] = gy + radius * FloatMath.sin(angleInRadians);
	    VERTEX_DATA[offset++] = 0.5f + 0.5f * FloatMath.cos(-angleInRadians);
	    VERTEX_DATA[offset++] = 0.5f + 0.5f * FloatMath.sin(-angleInRadians);
	    }
		
		vertexArray = new VertexArray(VERTEX_DATA);
	}
	
	// Return size of a circle built out of a triangle fan
	private static int sizeOfCircleInVertices(int numPoints) { 
		return 1 + (numPoints + 1);
	}

	
	//텍스쳐 설정
	public void setTexture(int texture){
		this.texture = texture;
	}
	
	//텍스쳐 설정
	public void setTexture(){
		this.texture = TextureHelper.loadTexture(context, textureSource);
		if(textBitmap != null){
			this.texture = TextureHelper.loadBitmpTexture(textBitmap);
		}
	}

	public Circle(Context context, float gx, float gy, float radius, int numPoints, int texture) {
		this.context = context;
		this.gx = gx;
		this.gy = gy;
		this.radius = radius;
		this.numPoints = numPoints;
		this.textureSource = texture;
		
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
		final int startVertex = offset / FLOATS_PER_VERTEX;
		final int numVertices = sizeOfCircleInVertices(numPoints);
		glDrawArrays(GL_TRIANGLE_FAN, 0, numVertices);

	}

	
	/////////////////////////////////////////////////
	////////////Getter, Setter///////////////////////
	/////////////////////////////////////////////////
	
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
		this.groupTitle = groupTitle;
	}

	public HashMap<String, Memo> getGroupMemoList() {
		return groupMemoList;
	}

	public void setGroupMemoList(HashMap<String, Memo> groupMemoList) {
		this.groupMemoList = groupMemoList;
	}
	
	
}

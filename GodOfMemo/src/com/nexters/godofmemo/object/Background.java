package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.BYTES_PER_FLOAT;
import android.content.Context;
import android.graphics.Bitmap;

import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.Constants;
import com.nexters.godofmemo.util.TextureHelper;

public class Background {
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

		VERTEX_DATA = new float[20];

		// top left
		int s = 0;
		VERTEX_DATA[s * 4 + 0] = px - pWidth / 2; // x
		VERTEX_DATA[s * 4 + 1] = py - pHeight / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = Constants.DOT_SIZE; // z

		// 오른쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = px + pWidth / 2; // x
		VERTEX_DATA[s * 4 + 1] = py - pHeight / 2; // y
		VERTEX_DATA[s * 4 + 2] = Constants.DOT_SIZE; // z
		VERTEX_DATA[s * 4 + 3] = Constants.DOT_SIZE; // z

		// 오른쪽 위에 
		s++;
		VERTEX_DATA[s * 4 + 0] = px + pWidth / 2; // x
		VERTEX_DATA[s * 4 + 1] = py + pHeight / 2; // y
		VERTEX_DATA[s * 4 + 2] = Constants.DOT_SIZE; // z
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
		VERTEX_DATA[s * 4 + 3] = Constants.DOT_SIZE; // z

		vertexArray = new VertexArray(VERTEX_DATA);
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

	public Background(Context context, float px, float py, float pWidth, float pHeight, int texture) {
		this.context = context;
		this.px = px;
		this.py = py;
		this.pWidth = pWidth;
		this.pHeight = pHeight;
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
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
}
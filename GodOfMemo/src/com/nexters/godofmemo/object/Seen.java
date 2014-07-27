package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.COLOR_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.COLOR_STRIDE;
import static com.nexters.godofmemo.util.Constants.POSITION_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.STRIDE;
import static com.nexters.godofmemo.util.Constants.TEXTURE_COORDINATES_COMPONENT_COUNT;
import android.content.Context;
import android.content.SharedPreferences;

import com.nexters.godofmemo.R;
import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.programs.ColorShaderProgram;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.TextureHelper;

public class Seen {

	// 배경 위치와 색상 정보를 저장.
	private VertexArray vertexArraySeen; // 글씨.

	// 위치, 크기정보
	private final float x;
	private final float y;
	private final float width;
	private final float height;

	public int seenTexture;

	/**
	 * 생성자.
	 *
	 * @param context
	 * @param px
	 * @param py
	 * @param pWidth
	 * @param pHeight
	 * @param texture
	 */
	public Seen(Context context) {
		//위치 및 크기.
		x = 2.00f;
		y = -3.00f;
		width = 0.9f;
		height = 0.9f;

		//좌표지정.
		setVertices();
	}
	
	public void setSeenTexture(Context context){

		
		//텍스쳐 지정.
		this.seenTexture = TextureHelper.loadTexture(context, R.drawable.seen);
	}

	/**
	 * 위치와 크기를 지정한다
	 */
	public void setVertices() {
		// System.out.println("setTextVertices");
		float[] VERTEX_DATA_SEEN = new float[24];

		// 중심.
		int s = 0;
		VERTEX_DATA_SEEN[0] = x; // x
		VERTEX_DATA_SEEN[1] = y; // y
		VERTEX_DATA_SEEN[2] = 0.5f; // S
		VERTEX_DATA_SEEN[3] = 0.5f; // T

		// 왼쪽 아래
		s++;
		VERTEX_DATA_SEEN[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_SEEN[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_SEEN[s * 4 + 2] = 0f; // z
		VERTEX_DATA_SEEN[s * 4 + 3] = 1f; // z

		// 오른쪽 아래
		s++;
		VERTEX_DATA_SEEN[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA_SEEN[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_SEEN[s * 4 + 2] = 1f; // z
		VERTEX_DATA_SEEN[s * 4 + 3] = 1f; // z

		// 오른쪽 위에
		s++;
		VERTEX_DATA_SEEN[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA_SEEN[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA_SEEN[s * 4 + 2] = 1f; // z
		VERTEX_DATA_SEEN[s * 4 + 3] = 0f; // z

		// 왼쪽 위에
		s++;
		VERTEX_DATA_SEEN[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_SEEN[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA_SEEN[s * 4 + 2] = 0f; // z
		VERTEX_DATA_SEEN[s * 4 + 3] = 0f; // z

		// 왼쪽 아래
		s++;
		VERTEX_DATA_SEEN[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_SEEN[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_SEEN[s * 4 + 2] = 0f; // z
		VERTEX_DATA_SEEN[s * 4 + 3] = 1f; // z

		vertexArraySeen =  new VertexArray(VERTEX_DATA_SEEN);
	}

	/**
	 * 그린다.
	 *
	 * @param colorProgram
	 */
	public void draw(TextureShaderProgram textureProgram) {

		// 텍스트에 대한 처리...
		vertexArraySeen.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArraySeen.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoordinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);

		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

	}
}
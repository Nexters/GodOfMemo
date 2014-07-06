package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.COLOR_COMPONENT_COUNT;
import static com.nexters.godofmemo.util.Constants.COLOR_STRIDE;
import static com.nexters.godofmemo.util.Constants.POSITION_COMPONENT_COUNT;
import android.content.Context;
import android.content.SharedPreferences;

import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.programs.ColorShaderProgram;

public class Background {

	// 배경 위치와 색상 정보를 저장.
	private VertexArray vertexArrayColor;

	// 위치, 크기정보
	private final float px;
	private final float py;
	private final float pWidth;
	private final float pHeight;

	// 설정 저장소
	SharedPreferences pref;

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
	public Background(Context context, float px, float py, float pWidth,
			float pHeight, int texture) {
		this.px = px;
		this.py = py;
		this.pWidth = pWidth;
		this.pHeight = pHeight;

		pref = context.getSharedPreferences("memo", Context.MODE_PRIVATE);

		setVertices();
	}

	/**
	 * 위치와 크기를 지정한다
	 */
	public void setVertices() {

		int ai = 128;
		int ri = pref.getInt("bg_color_r", 255);
		int gi = pref.getInt("bg_color_g", 255);
		int bi = pref.getInt("bg_color_b", 255);

		// rgb 253, 245, 229
		// rgb 140, 211, 156
		float a = ai / 255.0f;
		float r = ri / 255.0f;
		float g = gi / 255.0f;
		float b = bi / 255.0f;

		float[] VERTEX_DATA_COLOR = new float[36];

		// Order of coordinates: X, Y, R, G, B

		// point 1
		int s = 0;
		VERTEX_DATA_COLOR[s * 6 + 0] = px; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = py; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = r; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = g; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = b; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 2
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = px - pWidth / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = py - pHeight / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = r; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = g; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = b; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 3
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = px + pWidth / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = py - pHeight / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = r; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = g; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = b; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 4
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = px + pWidth / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = py + pHeight / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = r; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = g; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = b; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 5
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = px - pWidth / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = py + pHeight / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = r; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = g; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = b; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		// point 6
		s++;
		VERTEX_DATA_COLOR[s * 6 + 0] = px - pWidth / 2; // x
		VERTEX_DATA_COLOR[s * 6 + 1] = py - pHeight / 2; // y
		VERTEX_DATA_COLOR[s * 6 + 2] = r; // r
		VERTEX_DATA_COLOR[s * 6 + 3] = g; // g
		VERTEX_DATA_COLOR[s * 6 + 4] = b; // b
		VERTEX_DATA_COLOR[s * 6 + 5] = a; // a

		vertexArrayColor = new VertexArray(VERTEX_DATA_COLOR);
	}

	/**
	 * 그린다.
	 *
	 * @param colorProgram
	 */
	public void draw(ColorShaderProgram colorProgram) {
		vertexArrayColor.setVertexAttribPointer(0,
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, COLOR_STRIDE);

		vertexArrayColor.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				colorProgram.getColorAttributeLocation(),
				COLOR_COMPONENT_COUNT, COLOR_STRIDE);

		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);

	}
}
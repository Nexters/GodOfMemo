/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.opengl;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

/**
 * A two-dimensional square for use as a drawn object in OpenGL ES 2.0.
 */
public class Square {
	// 에러로그 태그
	private static String TAG = "RectangleCreateProgram";

	// vec4 는 실제로 네개의 값을 의미. 위에서 삼각형의 x,y,z 와 w값을 가진다.
	// aPosition으로 초기 삼각형의 위치를 설정해 놓는다.
	// uMVPMatrix가 삼각형의 세 점의 위치를 계속 옮기고 있다.
	// 어느폰에서나 삼각형의 모약이 같도록 유지도 해준다.
	private final String mVertexShader = "uniform mat4 uMVPMatrix;\n"
			+ "attribute vec4 aPosition;\n" + "attribute vec2 aTextureCoord;\n"
			+ "varying vec2 vTextureCoord;\n" + "void main() {\n"
			+ "  gl_Position = uMVPMatrix * aPosition;\n"
			+ "  vTextureCoord = aTextureCoord;\n" + "}\n";

	private final String mFragmentShader = "precision mediump float;\n"
			+ "varying vec2 vTextureCoord;\n" + "uniform sampler2D sTexture;\n"
			+ "void main() {\n"
			+ "  gl_FragColor = texture2D(sTexture, vTextureCoord);\n" + "}\n";

	private FloatBuffer vertexBuffer;
	private FloatBuffer textureBuffer;
	private ShortBuffer drawListBuffer;
	private int mProgram;
	private int mTextureID;

	private int muMVPMatrixHandle;
	private int maPositionHandle;
	private int maTextureHandle;

	// number of coordinates per vertex in this array
	static final int COORDS_PER_VERTEX = 3;
	static float squareCoords[];
	static float textureCoords[];
	

	/*
	 * { -0.5f, 0.5f, 0.0f, // top left -0.5f, -0.5f, 0.0f, // bottom left 0.5f,
	 * -0.5f, 0.0f, // bottom right 0.5f, 0.5f, 0.0f }; // top right
	 */
	private final short drawOrder[] = { 0, 1, 2, 0, 2, 3 }; // order to draw
															// vertices

	private final int vertexStride = COORDS_PER_VERTEX * 4; // 4 bytes per
															// vertex

	public float px;
	public float py;
	public float pWidth;
	public float pHeight;
	public float color[];

	/**
	 * Sets up the drawing object data for use in an OpenGL ES context.
	 */
	public Square(Context mContext, float px, float py, float pWidth,
			float pHeight, int cc[], int texture) {
		// 위치랑 컬러값 초기 설정
		this.px = px;
		this.py = py;
		this.pWidth = pWidth;
		this.pHeight = pHeight;

		if (cc == null) {
			int[] dc = { 49, 101, 156, 255 }; // 임시 초기값
			cc = dc;
		}
		float[] cColor = { cc[0] / 255f, cc[1] / 255f, cc[2] / 255f,
				cc[3] / 255f };
		color = cColor;

		// 좌표설정
		squareCoords = new float[12];
		textureCoords = new float[12];
		setVertices();

		// shader source를 활용해서 program을 만듭니다.
		mProgram = this.createProgram(mVertexShader, mFragmentShader);

		// get handle (position, texture)
		setHandle();
		// create our texture
		createTexture(mContext, texture);
	}

	private void setHandle() {
		maPositionHandle = GLES20.glGetAttribLocation(mProgram, "aPosition");
		MyGLRenderer.checkGlError("glGetAttribLocation aPosition");
		if (maPositionHandle == -1) {
			throw new RuntimeException(
					"Could not get attrib location for aPosition");
		}
		maTextureHandle = GLES20.glGetAttribLocation(mProgram, "aTextureCoord");
		MyGLRenderer.checkGlError("glGetAttribLocation aTextureCoord");
		if (maTextureHandle == -1) {
			throw new RuntimeException(
					"Could not get attrib location for aTextureCoord");
		}

		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		MyGLRenderer.checkGlError("glGetUniformLocation uMVPMatrix");
		if (muMVPMatrixHandle == -1) {
			throw new RuntimeException(
					"Could not get attrib location for uMVPMatrix");
		}
	}

	private int createProgram(String vertexSource, String fragmentSource) {

		// shader 생성.
		int vertexShader = MyGLRenderer.loadShader(
				GLES20.GL_VERTEX_SHADER, vertexSource);
		if (vertexShader == 0) {
			return 0;
		}
		int pixelShader = MyGLRenderer.loadShader(
				GLES20.GL_FRAGMENT_SHADER, fragmentSource);
		if (pixelShader == 0) {
			return 0;
		}

		// program 생성하고 error체크
		int program = GLES20.glCreateProgram();
		if (program != 0) {
			GLES20.glAttachShader(program, vertexShader);
			GLES20.glAttachShader(program, pixelShader);
			GLES20.glLinkProgram(program);
			int[] linkStatus = new int[1];
			GLES20.glGetProgramiv(program, GLES20.GL_LINK_STATUS, linkStatus, 0);
			if (linkStatus[0] != GLES20.GL_TRUE) {
				Log.e(TAG, "Could not link program: ");
				Log.e(TAG, GLES20.glGetProgramInfoLog(program));
				GLES20.glDeleteProgram(program);
				program = 0;
			}
		}
		return program;
	}

	private void createTexture(Context mContext, int texture) {
		/*
		 * Create our texture. This has to be done each time the surface is
		 * created.
		 */

		int[] textures = new int[1];
		GLES20.glGenTextures(1, textures, 0);

		mTextureID = textures[0];
		GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
		GLES20.glTexParameterf(GLES20.GL_TEXTURE_2D,
				GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);
		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);

		InputStream is = mContext.getResources().openRawResource(texture);
		Bitmap bitmap;
		try {
			bitmap = BitmapFactory.decodeStream(is);
			System.out.println("bitmap!!!!!!!");
			System.out.println(bitmap.getByteCount());
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				// Ignore.
			}
		}

		GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
		bitmap.recycle();
	}

	public void setVertices() {

		// top left
		int s = 0;
		squareCoords[s * 3 + 0] = px; // x
		squareCoords[s * 3 + 1] = py; // y
		squareCoords[s * 3 + 2] = 0; // z

		// bottom left
		s++;
		squareCoords[s * 3 + 0] = px; // x
		squareCoords[s * 3 + 1] = py - pHeight; // y
		squareCoords[s * 3 + 2] = 0; // z

		// bottom right
		s++;
		squareCoords[s * 3 + 0] = px + pWidth; // x
		squareCoords[s * 3 + 1] = py - pHeight; // y
		squareCoords[s * 3 + 2] = 0; // z

		// top right
		s++;
		squareCoords[s * 3 + 0] = px + pWidth; // x
		squareCoords[s * 3 + 1] = py; // y
		squareCoords[s * 3 + 2] = 0; // z
		
		/**
		 * 
		 */
		//textureCoords
		s=0;
		textureCoords[s * 3 + 0] = 0; // x
		textureCoords[s * 3 + 1] = 0; // y
		textureCoords[s * 3 + 2] = 0; // z

		// bottom left
		s++;
		textureCoords[s * 3 + 0] = 0; // x
		textureCoords[s * 3 + 1] = 1; // y
		textureCoords[s * 3 + 2] = 0; // z

		// bottom right
		s++;
		textureCoords[s * 3 + 0] = 1; // x
		textureCoords[s * 3 + 1] = 1; // y
		textureCoords[s * 3 + 2] = 0; // z

		// top right
		s++;
		textureCoords[s * 3 + 0] = 1; // x
		textureCoords[s * 3 + 1] = 0; // y
		textureCoords[s * 3 + 2] = 0; // z
		

		// initialize vertex byte buffer for shape coordinates
		ByteBuffer bb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 4 bytes per float)
				squareCoords.length * 4);
		bb.order(ByteOrder.nativeOrder());
		vertexBuffer = bb.asFloatBuffer();
		vertexBuffer.put(squareCoords);
		vertexBuffer.position(0);
		
		ByteBuffer bbb = ByteBuffer.allocateDirect(
				// (# of coordinate values * 4 bytes per float)
				textureCoords.length * 4);
		bbb.order(ByteOrder.nativeOrder());
		textureBuffer = bbb.asFloatBuffer();
		textureBuffer.put(textureCoords);
		textureBuffer.position(0);

		// initialize byte buffer for the draw list
		ByteBuffer dlb = ByteBuffer.allocateDirect(
		// (# of coordinate values * 2 bytes per short)
				drawOrder.length * 2);
		dlb.order(ByteOrder.nativeOrder());
		drawListBuffer = dlb.asShortBuffer();
		drawListBuffer.put(drawOrder);
		drawListBuffer.position(0);
	}

	/**
	 * Encapsulates the OpenGL ES instructions for drawing this shape.
	 * 
	 * @param mvpMatrix
	 *            - The Model View Project matrix in which to draw this shape.
	 */
	public void draw(float[] mvpMatrix) {
		// Add program to OpenGL environment
		GLES20.glUseProgram(mProgram);

        GLES20.glActiveTexture(GLES20.GL_TEXTURE0);
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, mTextureID);

		final int RECTANGLE_VERTICES_DATA_POS_OFFSET = 0;
		final int RECTANGLE_VERTICES_DATA_UV_OFFSET = 6;

		// Position offset
		vertexBuffer.position(RECTANGLE_VERTICES_DATA_POS_OFFSET);
        
		// Prepare the triangle coordinate data
		GLES20.glVertexAttribPointer(maPositionHandle, COORDS_PER_VERTEX,
				GLES20.GL_FLOAT, false, vertexStride, vertexBuffer);
		MyGLRenderer.checkGlError("glVertexAttribPointer");
		
		  //UV?
		textureBuffer.position(0);
        //rectangle의 position을 다룰 수 있도록 
        GLES20.glEnableVertexAttribArray(maPositionHandle);
        MyGLRenderer.checkGlError("glEnableVertexAttribArray maPositionHandle");
        
        GLES20.glVertexAttribPointer(maTextureHandle, 2, GLES20.GL_FLOAT, false,
        		vertexStride, textureBuffer);
        MyGLRenderer.checkGlError("glVertexAttribPointer maTextureHandle");
        
        GLES20.glEnableVertexAttribArray(maTextureHandle);
        MyGLRenderer.checkGlError("glEnableVertexAttribArray maTextureHandle");
        
        
		// get handle to shape's transformation matrix
		muMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
		MyGLRenderer.checkGlError("glGetUniformLocation");

		// Apply the projection and view transformation
		GLES20.glUniformMatrix4fv(muMVPMatrixHandle, 1, false, mvpMatrix, 0);
		MyGLRenderer.checkGlError("glUniformMatrix4fv");

		// Draw the square
		GLES20.glDrawElements(
				GLES20.GL_TRIANGLES, drawOrder.length,
				GLES20.GL_UNSIGNED_SHORT, drawListBuffer);

	}

}
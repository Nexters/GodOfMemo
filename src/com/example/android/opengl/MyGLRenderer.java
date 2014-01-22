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

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.Map;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.graphics.Color;
import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.util.Log;

/**
 * Provides drawing instructions for a GLSurfaceView object. This class
 * must override the OpenGL ES drawing lifecycle methods:
 * <ul>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceCreated}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onDrawFrame}</li>
 *   <li>{@link android.opengl.GLSurfaceView.Renderer#onSurfaceChanged}</li>
 * </ul>
 */
public class MyGLRenderer implements GLSurfaceView.Renderer {

    private static final String TAG = "MyGLRenderer";
    public Square mSquare;
    public Square mSquare2;
    public Circle mCircle;
    public Circle mCircle2;
    public Circle mCircle3;

    // mMVPMatrix is an abbreviation for "Model View Projection Matrix"
    private final float[] mMVPMatrix = new float[16];
    private final float[] mProjectionMatrix = new float[16];
    private final float[] mViewMatrix = new float[16];

    public float mViewX = 0.0f;
    public float mViewY = 0.0f;
    public float mViewZoom = 4f;
    
    //선택된 위치
    public float sx=0f;
    public float sy=0f; 
    public byte[] sb;
    public FinishDrawListener fdl;
    
    //캔버스 크기
    int width, height;
    
    //그룹 저장
    Map<Integer, Circle> circleMap;
    Map<Integer, Square> squareMap;
    
    //텍스쳐
    private Context mContext;
    
    
    public MyGLRenderer(Context context){
    	this.mContext = context;
    }
    

    @Override
    public void onSurfaceCreated(GL10 unused, EGLConfig config) {

        // Set the background frame color
		GLES20.glClearColor(0.1f, 0.2f, 0.3f, 1.0f);
		
		int[] c1 = {49, 101, 156, 255};
		int[] c2 = {156,81,231,255};
		int[] c3 = {107,207,49,255};
        
		mSquare = new Square(mContext,-0.3f, 0.5f, 0.4f, 0.5f, c1, R.drawable.ic_launcher);
		mSquare2 = new Square(mContext,-0.5f, -0.5f, 0.2f, 0.3f, c1, R.drawable.ic_launcher);
        //mCircle = new Circle(0.1f, 0.3f, 0.4f, c1);
        mCircle2 = new Circle(-0.1f, -0.3f, 0.2f, c2);
        mCircle3 = new Circle(-0.8f, -0.7f, 0.2f, c3);
        
        circleMap = new HashMap<Integer, Circle>();
        //groupMap.put(Color.argb(c1[0], c1[1], c1[2], c1[3]), mCircle);
        circleMap.put(Color.argb(c2[0], c2[1], c2[2], c2[3]), mCircle2);
        circleMap.put(Color.argb(c3[0], c3[1], c3[2], c3[3]), mCircle3);
        
        squareMap = new HashMap<Integer, Square>();
        squareMap.put(Color.argb(c1[0], c1[1], c1[2], c1[3]), mSquare);
        squareMap.put(Color.argb(c1[0], c1[1], c1[2], c1[3]), mSquare2);
        

    }

    @Override
    public void onDrawFrame(GL10 unused) {

        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT | GLES20.GL_DEPTH_BUFFER_BIT);
        

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mViewMatrix, 0, 
        		mViewX, mViewY, mViewZoom,
        		mViewX, mViewY, 0, 
        		0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjectionMatrix, 0, mViewMatrix, 0);

        // Draw circles
        mSquare.draw(mMVPMatrix);
        mSquare2.draw(mMVPMatrix);
        
        //mCircle.draw(mMVPMatrix);
        mCircle2.draw(mMVPMatrix);
        mCircle3.draw(mMVPMatrix);
        
        //픽셀읽기
    	readPixels();
    }

    @Override
    public void onSurfaceChanged(GL10 unused, int width, int height) {
        // Adjust the viewport based on geometry changes,
        // such as screen rotation
        GLES20.glViewport(0, 0, width, height);
        
        this.width = width;
        this.height = height;
        System.out.format("width, height = %d, %d \n", width, height);
        
        float ratio = (float) width / height;

        // this projection matrix is applied to object coordinates
        // in the onDrawFrame() method
        Matrix.frustumM(mProjectionMatrix, 0, -ratio, ratio, -1, 1, 3, 7);

    }

    /**
     * Utility method for compiling a OpenGL shader.
     *
     * <p><strong>Note:</strong> When developing shaders, use the checkGlError()
     * method to debug shader coding errors.</p>
     *
     * @param type - Vertex or fragment shader type.
     * @param shaderCode - String containing the shader code.
     * @return - Returns an id for the shader.
     */
    public static int loadShader(int type, String shaderCode){

        // create a vertex shader type (GLES20.GL_VERTEX_SHADER)
        // or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
        int shader = GLES20.glCreateShader(type);

        // add the source code to the shader and compile it
        GLES20.glShaderSource(shader, shaderCode);
        GLES20.glCompileShader(shader);

        return shader;
    }

    /**
    * Utility method for debugging OpenGL calls. Provide the name of the call
    * just after making it:
    *
    * <pre>
    * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
    * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
    *
    * If the operation is not successful, the check throws an error.
    *
    * @param glOperation - Name of the OpenGL call to check.
    */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }
    
    
    /*
     * 위치선택
     * onDrawFrame에서만 픽셀을 읽을 수 있다.
     * 매번 onDrawFrame를 호출할 때마다 픽셀값을 sb에 저장한다.
     * 대상이 되는 픽셀은 onDrawFrame호출 전에 미리 정한다. sx, sy.
     */
	private void readPixels() {
		ByteBuffer pixelBuffer;
		try {
			pixelBuffer = ByteBuffer.allocateDirect(4).order(
					ByteOrder.nativeOrder());
			//System.out.format("selected pixel %d %d \n", (int)sx, (int)sy);
			
			//좌표 시작점이 다르기때문에 x좌표를 살짝 바꿔준다
			//화면은 왼쪽 위가 시작인데
			//opengl은 왼쪽 아래가 시작이다.... 샹
			GLES20.glReadPixels((int)sx, height-(int)sy, 1, 1, GLES20.GL_RGBA,
					GLES20.GL_UNSIGNED_BYTE, pixelBuffer);
			checkGlError("glReadPixels");
			
			sb = new byte[4];
			pixelBuffer.get(sb);

			System.out.format("readPixels1 %d %d %d %d \n", sb[0]& 0xFF, sb[1]& 0xFF, sb[2]& 0xFF, sb[3]& 0xFF);
			
	    	//화면그리기가 끝나면 호출
			//시간차 때문에 콜백을 사용했다.
	    	if(fdl != null){
	    		fdl.postDraw();
	    	}

		} catch (OutOfMemoryError e) {
			pixelBuffer = null;
		}
	}


}
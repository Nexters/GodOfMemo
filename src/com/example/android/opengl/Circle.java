package com.example.android.opengl;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import android.opengl.GLES20;
import android.util.Log;

public class Circle {
	
	private  int mProgram, mPositionHandle, mColorHandle, mMVPMatrixHandle ;
	private FloatBuffer mVertexBuffer;
	int vertexCount = 364;
	private float vertices[] = new float[vertexCount * 3];
	
	private final String vertexShaderCode =
	// This matrix member variable provides a hook to manipulate
	// the coordinates of the objects that use this vertex shader
	"uniform mat4 uMVPMatrix;" +
	"attribute vec4 vPosition;" +
	"void main() {" +
	// the matrix must be included as a modifier of gl_Position
	// Note that the uMVPMatrix factor *must be first* in order
	// for the matrix multiplication product to be correct.
	"  gl_Position = uMVPMatrix * vPosition;" +
	"}";
	
	private final String fragmentShaderCode =
	"precision mediump float;" +
	"uniform vec4 vColor;" +
	"void main() {" +
	"  gl_FragColor = vColor;" +
	"}";
	
	
	public float px;
	public float py;
	public float radius;
	public float color[];
	
	public Circle(){
		px = 0.0f;
		py = 0.0f;
		radius = 0.3f;
	}
	
	public Circle(float px, float py, float radius, int color[]){
		
		float[] c = { color[0] / 255f, color[1] / 255f, color[2] / 255f, color[3] / 255f };
	
		this.px = px;
		this.py = py;
		this.radius = radius;
		this.color = c;
				
		setVertices();
	    
	    int vertexShader = MyGLRenderer.loadShader(GLES20.GL_VERTEX_SHADER, vertexShaderCode);
	    int fragmentShader = MyGLRenderer.loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentShaderCode);
	
	    mProgram = GLES20.glCreateProgram();             // create empty OpenGL ES Program
	    GLES20.glAttachShader(mProgram, vertexShader);   // add the vertex shader to program
	    GLES20.glAttachShader(mProgram, fragmentShader); // add the fragment shader to program
	    GLES20.glLinkProgram(mProgram);  
	
	 }
	
	public void setVertices(){
		
		vertices[0] = px;	//x
		vertices[1] = py;	//y
		vertices[2] = 0;	//z
	
		for(int i =1; i <vertexCount; i++){
		    vertices[(i * 3)+ 0] = (float) (radius * Math.cos((3.14/180) * (float)i ) + vertices[0]);
		    vertices[(i * 3)+ 1] = (float) (radius * Math.sin((3.14/180) * (float)i ) + vertices[1]);
		    vertices[(i * 3)+ 2] = 0;
		}
	
	
	    Log.v("Thread",""+vertices[0]+","+vertices[1]+","+vertices[2]);
	    ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(vertices.length * 4);
	    vertexByteBuffer.order(ByteOrder.nativeOrder());
	    mVertexBuffer = vertexByteBuffer.asFloatBuffer();
	    mVertexBuffer.put(vertices);
	    mVertexBuffer.position(0);
		
	}
	
	public static int loadShader(int type, String shaderCode){
	
	    int shader = GLES20.glCreateShader(type);
	    GLES20.glShaderSource(shader, shaderCode);
	    GLES20.glCompileShader(shader);
	    return shader;
	}
	
	
	public void draw (float[] mvpMatrix){
		//Log.d("draw", "ModelView: " + floatArrayAsString(mvpMatrix));
		
	    GLES20.glUseProgram(mProgram);
	
	    // get handle to vertex shader's vPosition member
	     mPositionHandle = GLES20.glGetAttribLocation(mProgram, "vPosition");
	
	    // Enable a handle to the triangle vertices
	    GLES20.glEnableVertexAttribArray(mPositionHandle);
	
	    // Prepare the triangle coordinate data
	    GLES20.glVertexAttribPointer(mPositionHandle, 3,
	                                 GLES20.GL_FLOAT, false,12
	                                 ,mVertexBuffer);
	
	    // get handle to fragment shader's vColor member
	    mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
	
	
	
	    // Set color for drawing the triangle
	    GLES20.glUniform4fv(mColorHandle, 1, color, 0);
	
	    mMVPMatrixHandle = GLES20.glGetUniformLocation(mProgram, "uMVPMatrix");
	    MyGLRenderer.checkGlError("glGetUniformLocation");
	
	    // Apply the projection and view transformation
	    GLES20.glUniformMatrix4fv(mMVPMatrixHandle, 1, false, mvpMatrix, 0);
	    MyGLRenderer.checkGlError("glUniformMatrix4fv");
	
	    // Draw the triangle
	    GLES20.glDrawArrays(GLES20.GL_TRIANGLE_FAN, 0, vertexCount);
	
	    // Disable vertex array
	    GLES20.glDisableVertexAttribArray(mPositionHandle);
	
	}


}
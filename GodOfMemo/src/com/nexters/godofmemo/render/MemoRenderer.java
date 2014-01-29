package com.nexters.godofmemo.render;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import java.util.LinkedList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;

import com.nexters.godofmemo.R;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.MatrixHelper;

public class MemoRenderer implements Renderer {
    private final Context context;

    private final float[] projectionMatrix = new float[16];
    private final float[] modelMatrix = new float[16];
    private final float[] mvpMatrix = new float[16];

    public List<Memo> memoList;
    private Memo memo;
    private Memo memo2;
    //private Mallet mallet;
    
    private TextureShaderProgram textureProgram;
    
    private int texture;
    
    //바라보는 화면 위치를 저장하는 변수
    public float px = 0f;
    public float py = 0f;
    
    //화면의 높이 너비를 저장
    public int width;
    public int height;
    
    //줌 배율
    public float zoom = 2.5f;
    
    //fov
    public float fov = 0.6f;

    public MemoRenderer(Context context) {
        this.context = context;
        memoList = new LinkedList<Memo>();
        
        memoList.add(new Memo(context, 0f, 0f, 0.606f, 0.494f, R.drawable.memo02));
        memoList.add(new Memo(context, 0.3f, -0.5f, 0.606f, 0.494f, R.drawable.memo03));
        memoList.add(new Memo(context, -0.6f, -1.0f, 0.606f, 0.494f, R.drawable.memo04));
    }

    @Override
    public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

        for(Memo memo: memoList){
            // 텍스쳐를 입힌다.
        	memo.setTexture();
        	memo.setBitmapTexture();
        }
        
        textureProgram = new TextureShaderProgram(context);
    }

    @Override
    public void onSurfaceChanged(GL10 glUnused, int width, int height) {
    	//높이 너비 저장
    	this.width = width;
    	this.height = height;
    	
        // Set the OpenGL viewport to fill the entire surface.
        glViewport(0, 0, width, height);

        MatrixHelper.perspectiveM(projectionMatrix, fov*100, (float) width/ (float) height, 1f, 8f);

        setIdentityM(modelMatrix, 0);
        translateM(modelMatrix, 0, 0f, 0f, -zoom);
        //rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

        //final float[] temp = new float[16];
        multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
        //System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);               
    }

    @Override
    public void onDrawFrame(GL10 glUnused) {
        // Clear the rendering surface.
        glClear(GL_COLOR_BUFFER_BIT);

        //카메라이동
        setLookAtM(modelMatrix, 0, 
        		px, py, zoom,
        		px, py, 0, 
        		0f, 1.0f, 0.0f);
        multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
        
        //메모들을 그린다
        for(Memo memo: memoList){
            // Draw the memo.
            textureProgram.useProgram();
            textureProgram.setUniforms(mvpMatrix, memo.texture);
            memo.bindData(textureProgram);
            memo.draw();
        }
    }
}
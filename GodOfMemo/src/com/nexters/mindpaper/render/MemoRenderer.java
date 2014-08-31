package com.nexters.mindpaper.render;

import static android.opengl.GLES20.GL_COLOR_BUFFER_BIT;
import static android.opengl.GLES20.glClear;
import static android.opengl.GLES20.glClearColor;
import static android.opengl.GLES20.glViewport;
import static android.opengl.Matrix.multiplyMM;
import static android.opengl.Matrix.setIdentityM;
import static android.opengl.Matrix.setLookAtM;
import static android.opengl.Matrix.translateM;

import java.util.concurrent.ConcurrentLinkedQueue;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.opengl.GLSurfaceView.Renderer;

import com.nexters.mindpaper.R;
import com.nexters.mindpaper.dao.GroupDAO;
import com.nexters.mindpaper.dao.MemoDAO;
import com.nexters.mindpaper.object.Background;
import com.nexters.mindpaper.object.Group;
import com.nexters.mindpaper.object.Memo;
import com.nexters.mindpaper.object.Seen;
import com.nexters.mindpaper.programs.ColorShaderProgram;
import com.nexters.mindpaper.programs.TextureShaderProgram;
import com.nexters.mindpaper.util.Constants;
import com.nexters.mindpaper.util.MatrixHelper;

public class MemoRenderer implements Renderer {
	private final Context context;

	private final float[] projectionMatrix = new float[16];
	private final float[] modelMatrix = new float[16];
	private final float[] mvpMatrix = new float[16];

	public ConcurrentLinkedQueue<Memo> memoList;
	public ConcurrentLinkedQueue<Group> groupList;
	private final Background background;
	private final Seen seen;

	private TextureShaderProgram textureProgram;
	private ColorShaderProgram colorProgram;

	// 바라보는 화면 위치를 저장하는 변수
	public float px = 0f;
	public float py = 0f;

	// 화면의 높이 너비를 저장
	public int width;
	public int height;

	// 줌 배율
	public float zoom = 8f;

	// fov
	public float fov = 0.6f;

	// 설정 저장소
	SharedPreferences pref;

	public MemoRenderer(Context context) {
		this.context = context;

		MemoDAO memoDao = new MemoDAO(context);
		memoList = memoDao.getMemoList();

		GroupDAO groupDao = new GroupDAO(context);
		groupList = groupDao.getGroupList();
		// memoList.add(new Memo(context, 0f, 0f, 0.6f, 0.6f, "test1"));
		// memoList.add(new Memo(context, 0.3f, -0.5f, 0.8f, 0.8f, "test2"));
		// memoList.add(new Memo(context, -0.6f, -1.0f, 0.5f, 0.5f, "test3"));

		// 배경화면
		background = new Background(context, 0, 0,
				Constants.DOT_BACKGROUND_SIZE, Constants.DOT_BACKGROUND_SIZE,
				R.drawable.background);
		
		//신이
		seen = new Seen(context);

		// TODO 마지막 봤던 위치와 확대정도를 저장했다가 다시 보여준다.

		// 설정.
		pref = context.getSharedPreferences("memo", Context.MODE_PRIVATE);

	}

	@Override
	public void onSurfaceCreated(GL10 glUnused, EGLConfig config) {
		//
		float rf = 252f/255f;
		float gf = 236f/255f;
		float bf = 210f/255f;
		
		//set bg color
		glClearColor(rf, gf, bf, 1.0f);

		// 배경깔기. ->  bug. 
		//int ri = pref.getInt("bg_color_r", 255);
		//int gi = pref.getInt("bg_color_g", 255);
		//int bi = pref.getInt("bg_color_b", 255);
		//glClearColor(ri, gi, bi, 0.5f);
		// 117, 166, 132

		//배경설정.
		background.setVertices();
		
		//신이 그리기.
		seen.setSeenTexture(context);

		for (Memo memo : memoList) {
			// Log.i("memo", memo.toString());
			// 텍스쳐를 입힌다.
			memo.setMemoContentTexture();
			
			//새 메모를 체크한다!!
			memo.chkNewStatus(context);
		}

		for (Group group : groupList) {
			// 텍스쳐를 입힌다.
			group.setGroupTitleTexture();
		}

		textureProgram = new TextureShaderProgram(context);
		colorProgram = new ColorShaderProgram(context);
	}

	@Override
	public void onSurfaceChanged(GL10 glUnused, int width, int height) {
		// //System.out.println(width);
		// //System.out.println(height);
		// 높이 너비 저장
		this.width = width;
		this.height = height;

		// //System.out.println(Constants.actionbarHeight);

		// Set the OpenGL viewport to fill the entire surface.
		glViewport(0, 0, width, height);

		MatrixHelper.perspectiveM(projectionMatrix, fov * 100, (float) width
				/ (float) height, 1f, Constants.SCREEN_SIZE);

		setIdentityM(modelMatrix, 0);
		translateM(modelMatrix, 0, 0f, 0f, -zoom);
		// rotateM(modelMatrix, 0, -60f, 1f, 0f, 0f);

		// final float[] temp = new float[16];
		multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0);
		// System.arraycopy(temp, 0, projectionMatrix, 0, temp.length);
	}

	@Override
	public void onDrawFrame(GL10 glUnused) {
		// Clear the rendering surface.
		glClear(GL_COLOR_BUFFER_BIT);

		// ########################
		// 배경 그리기

		// 카메라이동
		setLookAtM(modelMatrix, 0, px, py, zoom, px, py, 0, 0f, 1.0f, 0.0f);
		multiplyMM(mvpMatrix, 0, projectionMatrix, 0, modelMatrix, 0);

		// textureProgram.useProgram();
		// textureProgram.setUniforms(mvpMatrix, background.texture);
		// background.bindData(textureProgram);

		//배경그리기 
		colorProgram.useProgram();
		colorProgram.setUniforms(mvpMatrix);
		background.draw(colorProgram);
		
		//신이 그리기 
		textureProgram.useProgram();
		textureProgram.setUniforms(mvpMatrix, seen.seenTexture);
		seen.draw(textureProgram);

		//Log.i("texture",".seenTexture");
		//Log.i("texture",String.valueOf(seen.seenTexture));
		
		// 그룹들을 그린다
		for (Group group : groupList) {
			//Log.i("group", group.toString());
			// Draw the memo.
			// textureProgram.useProgram();
			// textureProgram.setUniforms(mvpMatrix, group.texture);

			colorProgram.useProgram();
			colorProgram.setUniforms(mvpMatrix);
			group.drawGroup(colorProgram);

			// 그룹제목을 그린다.
			textureProgram.useProgram();
			textureProgram.setUniforms(mvpMatrix, group.textTexture);
			group.drawTitle(textureProgram);
			//Log.i("texture","group.textTexture");
			//Log.i("texture",String.valueOf(group.textTexture));

			// group.drawGroup(colorProgram, textureProgram);
		}

		// 메모들을 그린다
		for (Memo memo : memoList) {
			//Log.i("memo", memo.toString());

			// 메모지를 그리기 위한 색상 프로그램.
			colorProgram.useProgram();
			colorProgram.setUniforms(mvpMatrix);
			memo.drawMemo(colorProgram);

			// 메모내용을 그리기 위한 텍스쳐 프로그램.
			textureProgram.useProgram();
			textureProgram.setUniforms(mvpMatrix, memo.textTexture);
			memo.drawText(textureProgram);
			//Log.i("texture","memo.textTexture");
			//Log.i("texture",String.valueOf(memo.textTexture));

			// 새로 작성한 메모 확인해서 그린다.
			// 시간을 계산한다.
			if (memo.isNew()) {
				textureProgram.useProgram();
				textureProgram.setUniforms(mvpMatrix, memo.borderTexture);
				memo.drawText(textureProgram);
				//Log.i("texture","memo.borderTexture");
				//Log.i("texture",String.valueOf(memo.borderTexture));
				
			}

		}
	}
}
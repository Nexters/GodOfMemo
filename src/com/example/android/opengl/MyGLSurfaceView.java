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

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

/**
 * A view container where OpenGL ES graphics can be drawn on screen.
 * This view can also be used to capture touch events, such as a user
 * interacting with drawn objects.
 */
public class MyGLSurfaceView extends GLSurfaceView {

	private MyGLSurfaceView sv;
    private final MyGLRenderer mRenderer;

    public MyGLSurfaceView(Context context) {
        super(context);
        
        sv = this;

        // Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new MyGLRenderer(context);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
    }


	private static final String TAG = "Touch";
	
	// We can be in one of these 3 states
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	
	int mode = NONE;
	
	// Remember some things for zooming
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	float px;
	float py;

	final Handler handler = new Handler(); 
	Runnable mLongPressed;

	
    @Override
    public boolean onTouchEvent(MotionEvent event) {

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		case MotionEvent.ACTION_DOWN:
			mLongPressed = new LongClickControll(event);
			handler.postDelayed(mLongPressed , 100);
			
			Log.d(TAG, "mode=DRAG");
			
			start.set(event.getX(), event.getY());
			mode = DRAG;
			px = event.getX();
			py = event.getY();
			break;
			
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.d(TAG, "oldDist=" + oldDist);
			
			oldDist = spacing(event);
			// pause();
			if (oldDist > 50f) {
				Log.d(TAG, "mode=ZOOM");
				
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
			
		case MotionEvent.ACTION_UP:
			handler.removeCallbacks(mLongPressed);
			//선택모드 해제
			postSelectMode();
			if (mode == DRAG) {
			}
			break;
			
		case MotionEvent.ACTION_POINTER_UP:
			Log.d(TAG, "mode=NONE");
			
			mode = NONE;
			break;
			
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {

				float dx= event.getX() - px;
				float dy = event.getY() - py;
				//System.out.format("%f %f \n", dx, dy);
				
				//일정범위 이상 움직였을때는, 롱클릭 이벤트를 해제함
				float dLimit = 10f;
				if(Math.abs(dx)>dLimit || Math.abs(dy)> dLimit){
					handler.removeCallbacks(mLongPressed);
				}

				
				//이동변수
				float dM = 0.0001f;
				
				//그룹 이동모드인지 체크!
				if(selectedCircle != null || selectedSquare != null){
					onMoveSelectedObject(dx, dy, dM);
				}else {					
					//이동하는 로직
					mRenderer.mViewX += dx>0 ? -(dx*dM) : -(dx*dM);
					mRenderer.mViewY += dy>0 ? +(dy*dM) : +(dy*dM);
				}
				
			} else if (mode == ZOOM) {
				Log.d(TAG, "ZOOM");
				float newDist = spacing(event);
				float scale = newDist / oldDist;

				//줌 변수
				float dZ = 0.05f;
				
				//줌하는 로직
				//System.out.format("scale %f \n", mRenderer.mViewZoom);
				if(mRenderer.mViewZoom>3.1f){
					mRenderer.mViewZoom += scale>1 ? -dZ : +dZ;
				}else{
					mRenderer.mViewZoom += scale>1 ? 0 : +dZ;
				}
			}
			
			//화면에 그리기
			requestRender();
			
			break;
		}

		return true;
	}
    
    

	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/** Calculate the mid point of the first two fingers */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}
	

	
	/**
	 * LongClick이벤트를 처리하는 클래스
	 * GLSurfaceView Renderer에 접근해야 하기 때문에 inner클래스로 작성했다.
	 * @author lifenjoy51
	 *
	 */
	public class LongClickControll implements Runnable, FinishDrawListener{
		MotionEvent event;
		
		LongClickControll(MotionEvent event){
			this.event = event;
		}

		@Override
		public void run() {
			Log.e("", "view position!" + String.valueOf(sv.getLeft()) + ", "
					+ String.valueOf(sv.getTop()));
			
			mRenderer.sx = event.getX();
			mRenderer.sy = event.getY();
			mRenderer.fdl = this;
			
			//위치 이동 테스트
			//mRenderer.mCircle.px += 0.01f;
			//mRenderer.mCircle.setVertices();
					
			Log.e("", "Long press!" + String.valueOf(event.getX()) + ", "
					+ String.valueOf(event.getY()));	
	        
			requestRender();
		}

		@Override
		public void postDraw() {
			byte[] b = mRenderer.sb;			
			int[] color = {b[0]& 0xFF, b[1]& 0xFF, b[2]& 0xFF, b[3]& 0xFF};
			System.out.format("readPixels2 %d %d %d %d \n", color[0], color[1],
					color[2], color[3]);
			int c = Color.argb(color[0], color[1], color[2], color[3]);
			selectedCircle = mRenderer.circleMap.get(c);
			selectedSquare = mRenderer.squareMap.get(c);
			preSelectMode();
			
			mRenderer.fdl = null;
	    	
	    	//픽셀 초기화
			mRenderer.sx = 0f;
			mRenderer.sy = 0f;
			
		}
		
	}
    
    //원 이동 임시
    Circle selectedCircle;
    //사각형 이동 임시
    Square selectedSquare;
    
    /**
     * 물체 선택 전 로직
     */
    public void preSelectMode(){
    	if(selectedCircle != null){
			selectedCircle.radius += 0.1f;
			selectedCircle.setVertices();
			requestRender();
		}
    	if(selectedSquare != null){
			selectedSquare.px -= 0.05f;
			selectedSquare.py += 0.05f;
			selectedSquare.pHeight += 0.1f;
			selectedSquare.pWidth += 0.1;
			selectedSquare.setVertices();
			requestRender();
		}
    }
    
    /**
     * 물체 선택 후 로직
     */
    public void postSelectMode(){
    	if(selectedCircle != null){
    		selectedCircle.radius -= 0.1f;
    		selectedCircle.setVertices();
    		requestRender();
    		selectedCircle = null;
    	}
    	if(selectedSquare != null){
			selectedSquare.px += 0.05f;
			selectedSquare.py -= 0.05f;
			selectedSquare.pHeight -= 0.1f;
			selectedSquare.pWidth -= 0.1;
			selectedSquare.setVertices();
			requestRender();
			selectedSquare = null;
    	}
    }
    
    public void onMoveSelectedObject(float dx, float dy, float dM){

    	if(selectedCircle != null){
        	selectedCircle.px += dx>0 ? +(dx*dM) : +(dx*dM);
        	selectedCircle.py += dy>0 ? -(dy*dM) : -(dy*dM);
        	selectedCircle.setVertices();
    	}
    	if(selectedSquare != null){
    		selectedSquare.px += dx>0 ? +(dx*dM) : +(dx*dM);
    		selectedSquare.py += dy>0 ? -(dy*dM) : -(dy*dM);
    		selectedSquare.setVertices();
    	}	
    }
}

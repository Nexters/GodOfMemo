package com.nexters.godofmemo.view;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.nexters.godofmemo.render.AirHockeyRenderer;
import com.nexters.godofmemo.util.MultisampleConfigChooser;

public class MemoGLView extends GLSurfaceView {
	

    private final Renderer mRenderer;
	
	public MemoGLView(Context context) {
		super(context);
		
		// Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mRenderer = new AirHockeyRenderer(context);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setRenderer(mRenderer);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
	}

	private static final String TAG = "MemoGLView";
	
	//터치이벤트 유형
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	int mode = NONE;	//처음상태는 NONE
	
	//롱클릭 이벤트 처리를 위한 변수들
	private final Handler handler = new Handler(); 
	private Runnable mLongPressed;
	private final long longClickTimeLimit = 200;	//얼마동안 누르고 있어야 롱클릭이벤트로 판단할지(ms)
	
	// 위치정보 기억!!
	PointF start = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	
	//줌관련 변수
	private final float zoomSensitivity = 50f;
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {

		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
		//손이 화면에 닿을때
		case MotionEvent.ACTION_DOWN:
			//롱클릭이벤트 처리를 위해 등록
			//일정시간 이상 클릭시 롱클릭 이벤트 발생
			mLongPressed = new LongClickControll(event);
			handler.postDelayed(mLongPressed , longClickTimeLimit);
			
			//위치저장
			start.set(event.getX(), event.getY());
			mode = DRAG;
			break;
			
		//또 다른 손이 화면에 닿을때
		case MotionEvent.ACTION_POINTER_DOWN:
			Log.d(TAG, "oldDist=" + oldDist);
			
			oldDist = spacing(event);
			// pause();
			if (oldDist > zoomSensitivity) {
				Log.d(TAG, "mode=ZOOM");
				
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
			
		//손을 화면에서 뗄때
		case MotionEvent.ACTION_UP:
			handler.removeCallbacks(mLongPressed);
			
			if (mode == DRAG) {
			}
			break;
		
		//두 손을 화면에 댓다가 한손을 뗄때
		case MotionEvent.ACTION_POINTER_UP:
			Log.d(TAG, "mode=NONE");
			
			mode = NONE;
			break;
			
		//손을 대고 움직일때
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				Log.d(TAG, "DRAG");
				float dx= event.getX() - start.x;
				float dy = event.getY() - start.y;
				
				//일정범위 이상 움직였을때는, 롱클릭 이벤트를 해제함
				float dLimit = 10f;
				if(Math.abs(dx)>dLimit || Math.abs(dy)> dLimit){
					handler.removeCallbacks(mLongPressed);
				}
				
				
			} else if (mode == ZOOM) {
				Log.d(TAG, "ZOOM");
				float newDist = spacing(event);
				float scale = newDist / oldDist;
				
				if(scale>1){
					//확대
				}else{
					//축소
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
	 * @author lifenjoy51
	 *
	 */
	public class LongClickControll implements Runnable{
		MotionEvent event;
		
		LongClickControll(MotionEvent event){
			this.event = event;
		}

		@Override
		public void run() {
			
			requestRender();
		}
		
	}

}

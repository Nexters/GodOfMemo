package com.nexters.godofmemo.view;

import android.content.Context;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;

import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.render.MemoRenderer;
import com.nexters.godofmemo.util.MultisampleConfigChooser;

public class MemoGLView extends GLSurfaceView {
	

    public MemoRenderer mr;
	
	public MemoGLView(Context context) {
		super(context);
		
		// Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mr = new MemoRenderer(context);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setRenderer(mr);

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
	
	//선택된 메모
	private Memo selectedMemo;
	private float selectedAnimationSize = 0.1f;
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		
		//터치한 좌표
		float x = event.getX();
		float y = event.getY();
		
		//정규화된 좌표
		float nx = getNormalizedX(x);
    	float ny = getNormalizedY(y);
    	System.out.format("point %f %f %f %f \n", nx, ny, mr.px,  mr.py);
    	
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
		//손이 화면에 닿을때
		case MotionEvent.ACTION_DOWN:
			//롱클릭이벤트 처리를 위해 등록
			//일정시간 이상 클릭시 롱클릭 이벤트 발생
			mLongPressed = new LongClickControll(event);
			handler.postDelayed(mLongPressed , longClickTimeLimit);
			
			//위치저장
			start.set(x, y);
			mode = DRAG;
			
			//위치표시
			//System.out.format("%f %f \n", x, y);
			
			break;
			
		//또 다른 손이 화면에 닿을때
		case MotionEvent.ACTION_POINTER_DOWN:
			//Log.d(TAG, "oldDist=" + oldDist);
			
			oldDist = spacing(event);
			// pause();
			if (oldDist > zoomSensitivity) {
				//Log.d(TAG, "mode=ZOOM");
				
				midPoint(mid, event);
				mode = ZOOM;
			}
			break;
			
		//손을 화면에서 뗄때
		case MotionEvent.ACTION_UP:
			handler.removeCallbacks(mLongPressed);
			
			//메모 선택시
			if(selectedMemo != null){
				selectedMemo.pHeight -= selectedAnimationSize;
				selectedMemo.pWidth -= selectedAnimationSize;
				selectedMemo.setVertices();
				requestRender();
				selectedMemo = null;
			}
			
			
			break;
		
		//두 손을 화면에 댓다가 한손을 뗄때
		case MotionEvent.ACTION_POINTER_UP:
			//Log.d(TAG, "mode=NONE");
			
			mode = NONE;
			break;
			
		//손을 대고 움직일때
		case MotionEvent.ACTION_MOVE:
			if (mode == DRAG) {
				//Log.d(TAG, "DRAG");
				float dx= x - start.x;
				float dy = y - start.y;
				
				//일정범위 이상 움직였을때는, 롱클릭 이벤트를 해제함
				float dLimit = 10f;
				if(Math.abs(dx)>dLimit || Math.abs(dy)> dLimit){
					handler.removeCallbacks(mLongPressed);
				}
				
				if(selectedMemo != null){
					//메모 이동
					selectedMemo.px = nx;
					selectedMemo.py = ny;
					selectedMemo.setVertices();
				}else{
					//화면 이동
					//TODO 추후 개선 필요
					float dM = 0.0004f;
					mr.px += dx>0 ? -(dx*dM) : -(dx*dM);
					mr.py += dy>0 ? +(dy*dM) : +(dy*dM);
				}
				
			} else if (mode == ZOOM) {
				//Log.d(TAG, "ZOOM");
				float newDist = spacing(event);
				float scale = newDist / oldDist;	//확대,축소 여부

				float dZ = 0.05f;	//줌가속 변수
				float min = 1f;	//줌 최소
				float max = 5f;	//줌최대
				
				//TODO 줌 버그잇음. 특정 상황에 확대축소가 반대로 작용함.	
				//줌 최대최소 판별 후
				if (min < mr.zoom  && mr.zoom < max) {				
					if(scale > 1){
						//확대
						if(min<(mr.zoom-dZ)) mr.zoom -= dZ*mr.zoom;
					}else{
						//축소
						if(max>(mr.zoom+dZ)) mr.zoom += dZ*mr.zoom;
					}
				} else {
					//최대최소 줌 범위를 넘어갔을때를 위한 로직
					mr.zoom += scale > 1 ? +dZ : -dZ;
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
	 * 정규화된 좌표를 구한다 
	 */
	//TODO 식이 너무 복잡해...
	//TODO 액션바 위치에 따라서 좌표가 달라지는듯?? 버그
	
	public float getNormalizedX(float x){
		return ((((x/mr.width)* 2)-1)*mr.zoom*mr.width/mr.height*mr.fov)+mr.px;
	}
	public float getNormalizedY(float y){
		return ((-(((y/mr.height)*2)-1))*mr.zoom*mr.fov)+mr.py;
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
			//터치한 좌표
			float x = event.getX();
			float y = event.getY();
			
			//정규화된 좌표
			float nx = getNormalizedX(x);
	    	float ny = getNormalizedY(y);
	    	
	    	//선택된 원을 확인
			for(Memo memo : mr.memoList){
				float chkX = Math.abs(nx-memo.px)/(memo.pWidth/2);
				float chkY = Math.abs(ny-memo.py)/(memo.pHeight/2);

				System.out.format("x,y %f %f \n",memo.px, memo.py);
				System.out.format("nx, ny %f %f \n",nx, ny);
				System.out.format("chk x,y %f %f \n",chkX, chkY);
				if(chkX <= 1 && chkY <= 1){
					//선택됨
					selectedMemo = memo;
					selectedMemo.px = nx;
					selectedMemo.py = ny;
					selectedMemo.pHeight += selectedAnimationSize;
					selectedMemo.pWidth += selectedAnimationSize;
					selectedMemo.setVertices();
					requestRender();
					return;
				}
			}
			
			requestRender();
		}
		
	}

}

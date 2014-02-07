package com.nexters.godofmemo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;

import com.nexters.godofmemo.MemoActivity;
import com.nexters.godofmemo.dao.MemoDAO;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.render.MemoRenderer;
import com.nexters.godofmemo.util.Constants;
import com.nexters.godofmemo.util.MultisampleConfigChooser;

public class MemoGLView extends GLSurfaceView {
	
    public MemoRenderer mr;
    private Context context;
	
	public MemoGLView(Context context) {
		super(context);
		
		this.context = context;		
		
		// Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mr = new MemoRenderer(context);
        setEGLConfigChooser(new MultisampleConfigChooser());
        setRenderer(mr);

        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        //진동 초기화
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
	}

	private static final String TAG = "MemoGLView";
	
	//터치이벤트 유형
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	
	int mode = NONE;	//처음상태는 NONE
	
	//tab 유형.
	static final int TAB = 3;
	static final int DOUBLETAB = 4;
	static final int LONGTAB = 5;
	
	int tabMode = NONE;//처음상태는 NONE 
	
	//롱클릭 이벤트 처리를 위한 변수들
	private final Handler handler = new Handler(); 
	private Runnable mLongPressed;
	private final long longClickTimeLimit = 200;	//얼마동안 누르고 있어야 롱클릭이벤트로 판단할지(ms)
	
	//tab 하기 위한 정보
	private long startMilliSecond;
	private long dMilliSecond;
	
	// 위치정보 기억!!
	PointF start = new PointF();
	PointF pre = new PointF();
	PointF mid = new PointF();
	float oldDist = 1f;
	
	//줌관련 변수
	private final float zoomSensitivity = 50f;
	
	//선택된 메모
	private Memo selectedMemo;
	private float selectedAnimationSize = 0.1f;
    
    //진동관리
    private Vibrator vibrator;
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		
		//터치한 좌표
		float x = event.getX();
		float y = event.getY();
		//System.out.println("111  "+x +", "+ y);
		
		//정규화된 좌표
		float nx = getNormalizedX(x);
    	float ny = getNormalizedY(y);
    	//System.out.format("point %f %f %f %f \n", nx, ny, mr.px,  mr.py);
    	
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
		//손이 화면에 닿을때
		case MotionEvent.ACTION_DOWN:
			//롱클릭이벤트 처리를 위해 등록
			//일정시간 이상 클릭시 롱클릭 이벤트 발생
			//줌상태가 아니면
			if (mode != ZOOM) {
				// 경과 시간을 재기.
				startMilliSecond = System.currentTimeMillis();
				
				mLongPressed = new LongClickControll(x,y);
				handler.postDelayed(mLongPressed , longClickTimeLimit);
			}
			
			//선택된 원을 확인
			//터치한 곳이 메모라면 selectedMemo에 추가. 
			for(Memo memo : mr.memoList){
				float chkX = Math.abs(nx-memo.getX())/(memo.getWidth()/2);
				float chkY = Math.abs(ny-memo.getY())/(memo.getHeight()/2);

				//이미지 여백을 고려하여 클릭 이벤트를 적용한다.
				if(chkX <= 0.9f && chkY <= 0.5f){
					//선택됨
					selectedMemo = memo;
				}
			}
			
			requestRender();
			
			//위치저장
			start.set(x, y);
			pre.set(x, y);
			mode = DRAG;
			
			//위치표시
			////System.out.format("%f %f \n", x, y);
			
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
			
			float dx= x - pre.x;
			float dy = y - pre.y;
			pre.set(x,y);
			
			// 화면을 누른 시간을 구한다.
			dMilliSecond = System.currentTimeMillis() - startMilliSecond;
			
			float moveLimit = 0.005f;
			boolean isMoved = (Math.abs(dx)>moveLimit || Math.abs(dy)>moveLimit);
			
			//메모 선택시
			//LongClickEvent에서 selectedMemo를 설정.
			if(selectedMemo != null && !isMoved ){
				if(0< dMilliSecond && dMilliSecond < 100){
					tabMode= TAB;
					Intent intent = new Intent(context, MemoActivity.class);
					//보기, 수정 화면으로 넘어가기. 
					intent.putExtra("selectedMemoContent", selectedMemo.getMemoContent());
					
					((Activity)context).startActivityForResult(intent, 1);
				}else if(tabMode == LONGTAB){
					//selectedMemo.setWidth(selectedMemo.getWidth() - selectedAnimationSize);
					//selectedMemo.setHeight(selectedMemo.getHeight() - selectedAnimationSize);
					selectedMemo.setVertices();
					requestRender();
					
					//이동한 정보를 DB에 입력한다.
					MemoDAO memoDao = new MemoDAO(context);
					memoDao.updateMemo(selectedMemo);
				}
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
				dx= x - pre.x;
				dy = y - pre.y;
				pre.set(x,y);
				
				//일정범위 이상 움직였을때는, 롱클릭 이벤트를 해제함
				float dLimit = 10f;
				if(Math.abs(dx)>dLimit || Math.abs(dy)> dLimit){
					handler.removeCallbacks(mLongPressed);

				}
				
				if(selectedMemo != null){
					//메모 이동
					selectedMemo.setX(nx);
					selectedMemo.setY(ny);
					selectedMemo.setVertices();
				}else{
					//화면 이동
					//TODO 추후 개선 필요
					
					float dM = 0.0015f*mr.zoom;
					
					float tempX = mr.px;
					float tempY = mr.py;
					
					tempX += -(dx*dM);
					tempY += +(dy*dM);
					
					//화면을 넘어간지 확인한다.
					if(!isOutOfBoundary(tempX, tempY, mr.zoom)){
						mr.px = tempX;
						mr.py = tempY;
					}else{
						System.out.println("true");
					}
					
					System.out.format(" x y %f %f \t", x, y);
					System.out.format(" nx ny %f %f \t", nx, ny);
					System.out.format(" px py %f %f \n", mr.px, mr.py);
					//mr.px = nx;
					//mr.py = ny;
				}
			} else if (mode == ZOOM) {
				//줌모드로 들어오면 롱클릭 이벤트를 해제함
				handler.removeCallbacks(mLongPressed);
				
				//Log.d(TAG, "ZOOM");
				float newDist = spacing(event);
				float scale = newDist / oldDist;	//확대,축소 여부

				float dZ = 0.05f;	//줌가속 변수
				float min = 1f;	//줌 최소
				float max = 5f;	//줌최대
				
				float tempZoom = 0;
				tempZoom = mr.zoom;
				
				//TODO 줌 버그잇음. 특정 상황에 확대축소가 반대로 작용함.	
				//줌 최대최소 판별 후
				if (min < mr.zoom  && mr.zoom < max) {				
					if(scale > 1){
						//확대
						if(min<(mr.zoom-dZ)) tempZoom -= dZ*mr.zoom;
					}else{
						//축소
						if(max>(mr.zoom+dZ)) tempZoom += dZ*mr.zoom;
					}
				} else {
					if (mr.zoom <= min){
						//축소
						if(max>(mr.zoom+dZ)) tempZoom += dZ;
					}else if (mr.zoom >= max){
						//확대
						if(min<(mr.zoom-dZ)) tempZoom -= dZ;
					}
				}
				
				if(!isOutOfBoundary(mr.px, mr.py, tempZoom)){
					mr.zoom = tempZoom;
				}
			}
			
			//화면에 그리기
			requestRender();
			
			break;
		}

		return true;
	}
	
	/**
	 * 화면이동 or 줌인/아웃 시 뒷 배경을 넘어가는지 여부 확인.
	 * @param tempX
	 * @param tempY
	 * @param tempZoom
	 * @return
	 */
	private boolean isOutOfBoundary(float tempX, float tempY, float tempZoom){
		
		//허용되는 최대 한계치
		float margin = Constants.DOT_BACKGROUND_SIZE/5f;
		
		float ratioX = 1;
		float ratioY = 1;

		if (mr.width > mr.height) {
			ratioX = (float) mr.width / mr.height;
		} else {
			ratioY = (float) mr.height / mr.width ;
		}
		
		System.out.format("w h %d %d \n",mr.width, mr.height);
		
		float leftBoundary = -((Constants.DOT_BACKGROUND_SIZE/2) + margin)*ratioX;
		float rightBoundary = +((Constants.DOT_BACKGROUND_SIZE/2) + margin)*ratioX;
		float topBoundary = +((Constants.DOT_BACKGROUND_SIZE/2) + margin)*ratioY;
		float bottomBoundary = -((Constants.DOT_BACKGROUND_SIZE/2) + margin)*ratioY;
		
		System.out.format("boundary %f %f %f %f \n", leftBoundary, rightBoundary, topBoundary, bottomBoundary);
		
		//현재 화면의 상태
		float left = 0 ;
		float right = mr.width;
		float top = 0;
		float bottom = mr.height;
		
		float normalizedLeft = getNormalizedX(left, tempX, tempY, tempZoom);
		float normalizedRight = getNormalizedX(right, tempX, tempY, tempZoom);
		float normalizedTop = getNormalizedY(top, tempX, tempY, tempZoom);
		float normalizedBottom = getNormalizedY(bottom, tempX, tempY, tempZoom);
		
		System.out.format("normalized %f %f %f %f \n", normalizedLeft, normalizedRight, normalizedTop, normalizedBottom);
		
		//자동줌
		if(mode == ZOOM){
			float autoMoveDist = 0.08f;
			if(normalizedLeft < leftBoundary){
				mr.px += autoMoveDist;
			}
			if(normalizedRight > rightBoundary){
				mr.px -= autoMoveDist;
			}
			if(normalizedTop > topBoundary){
				mr.py -= autoMoveDist;
			}
			if(normalizedBottom < bottomBoundary){
				mr.py += autoMoveDist;
			}	
		}
		
		//영역이 초과했는지 확인한다.
		if(normalizedLeft < leftBoundary) return true;
		if(normalizedRight > rightBoundary) return true;
		if(normalizedTop > topBoundary) return true;
		if(normalizedBottom < bottomBoundary) return true;
		
		System.out.println("false!!");
		
		return false;
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
	//TODO 터치한 픽셀좌표(기기 해상도)를 OpenGL상의 좌표 (-1,-1부터 1,1같은)로 변환한다.
	public float getNormalizedX(float x){
		return ((((x/mr.width)* 2)-1)*mr.zoom*mr.width/mr.height*mr.fov)+mr.px;
	}
	public float getNormalizedY(float y){
		return ((-(((y/mr.height)*2)-1))*mr.zoom*mr.fov)+mr.py;
	}
	
	/**
	 * 
	 * @param x
	 * @return
	 */
	public float getNormalizedX(float x, float px, float py, float zoom){
		return ((((x/mr.width)* 2)-1)*zoom*mr.width/mr.height*mr.fov)+px;
	}
	public float getNormalizedY(float y, float px, float py, float zoom){
		return ((-(((y/mr.height)*2)-1))*zoom*mr.fov)+py;
	}
	
	/**
	 * LongClick이벤트를 처리하는 클래스
	 * @author lifenjoy51
	 *
	 */
	public class LongClickControll implements Runnable{
		MotionEvent event;
		
		float x;
		float y;
		
/*		LongClickControll(MotionEvent event){
			this.event = event;
		}*/
		
		LongClickControll(float x, float y){
			this.x = x;
			this.y = y;
		}

		@Override
		public void run() {
//			//터치한 좌표
//			float x = event.getX();
//			float y = event.getY();
			
			//TODO 버그인가? 위에서 선택했을때랑 여기서 선택했을때랑 y좌표를 다르게 가져온다.
			//MotionEvent를 등록할때에는 액션봐와 상단메뉴를 제외한 y좌표를 받는데
			//여기선 액션바와 상단메뉴를 포함한 y좌표를 받는다.
			//왜일까?
			//System.out.println("222  "+x +", "+ y);
			
			//정규화된 좌표
			float nx = getNormalizedX(x);
	    	float ny = getNormalizedY(y);
	    	
	    	//선택된 원을 확인
			for(Memo memo : mr.memoList){
				
				//System.out.format("x,y %f %f \n",memo.getX(), memo.getY());
				//System.out.format("nx, ny %f %f \n",nx, ny);
				//System.out.format("chk x,y %f %f \n",chkX, chkY);
				
				//이미지 여백을 고려하여 클릭 이벤트를 적용한다.
				if(selectedMemo != null){
					//선택시 진동
					vibrator.vibrate(100);
					
					//선택된걸 상위로
					mr.memoList.remove(memo);
					mr.memoList.add(memo);
					
					selectedMemo.setX(nx);
					selectedMemo.setY(ny);
					//selectedMemo.setHeight(selectedMemo.getHeight()+ selectedAnimationSize);
					//selectedMemo.setWidth(selectedMemo.getWidth()+ selectedAnimationSize);
					selectedMemo.setVertices();
					tabMode=LONGTAB;
					requestRender();
					return;
				}
			}
			
			requestRender();
		}
		
	}

}

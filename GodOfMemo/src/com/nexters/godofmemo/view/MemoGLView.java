package com.nexters.godofmemo.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.PointF;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Vibrator;
import android.view.MotionEvent;

import com.nexters.godofmemo.GroupActivity;
import com.nexters.godofmemo.MainActivity;
import com.nexters.godofmemo.MemoActivity;
import com.nexters.godofmemo.dao.GroupDAO;
import com.nexters.godofmemo.object.Group;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.render.MemoRenderer;
import com.nexters.godofmemo.util.Constants;
import com.nexters.godofmemo.util.MultisampleConfigChooser;
import com.nexters.godofmemo.util.PositionHelper;

public class MemoGLView extends GLSurfaceView {
	
    public MemoRenderer mr;
    private Context context;
    public PositionHelper positionHelper;
	
	public MemoGLView(Context context) {
		super(context);
		this.context = context;		
		
		// Create an OpenGL ES 2.0 context.
        setEGLContextClientVersion(2);

        // Set the Renderer for drawing on the GLSurfaceView
        mr = new MemoRenderer(context);
        // Set the Helper for checking object's position.
        positionHelper = new PositionHelper(context, mr);
        setEGLConfigChooser(new MultisampleConfigChooser());
       
        setRenderer(mr);
        
        // Render the view only when there is a change in the drawing data
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        
        //진동 초기화
        vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);  
        
        //memo 초기화 
        //memo 들을 돌면서 그룹안에 있는지 체크 .
        this.initializePosition();
        
	}

	public void initializePosition() {
		// TODO Auto-generated method stub
		for(Memo memo: mr.memoList){
        	positionHelper.updateSpecificMemoForSetGroupId(memo);
        }
	}

	private static final String TAG = "MemoGLView";
	
	//터치이벤트 유형
	static final int NONE = 0;
	static final int DRAG = 1;
	static final int ZOOM = 2;
	
	private int mode = NONE;	//처음상태는 NONE
	
	//tab 유형.
	static final int TAB = 3;
	static final int DOUBLETAB = 4;
	static final int LONGTAB = 5;
	
	private int tabMode = NONE;//처음상태는 NONE 
	
	//롱클릭 이벤트 처리를 위한 변수들
	private final Handler handler = new Handler(); 
	private Runnable mLongPressed;
	private final long longClickTimeLimit = 300;	//얼마동안 누르고 있어야 롱클릭이벤트로 판단할지(ms)
	private final long clickEventLimit = 200;//얼마까지 클릭으로 판단할지.
	
	//tab 하기 위한 정보
	private long startMilliSecond;
	private long dMilliSecond;
	
	// 위치정보 기억!!
	PointF start = new PointF();
	PointF pre = new PointF();
	PointF nPre = new PointF();
	float oldDist = 1f;
	
	//줌관련 변수
	private final float zoomSensitivity = 50f;
	
	//선택된 메모
	private Memo selectedMemo;
	//선택된 그
	private Group selectedGroup;
    //진동관리
    private Vibrator vibrator;
	
	@Override
    public boolean onTouchEvent(MotionEvent event) {
		
		//터치한 좌표
		float x = event.getX();
		float y = event.getY();
		////////System.out.println("111  "+x +", "+ y);
		
		//정규화된 좌표
		float nx = getNormalizedX(x);
    	float ny = getNormalizedY(y);
    	////////System.out.format("point %f %f %f %f \n", nx, ny, mr.px,  mr.py);
    	
    	//이동한 다음의 차이.
    	float movedDistanceX =  0;
		float movedDistanceY =  0;
		
		// Handle touch events here...
		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		
		//손이 화면에 닿을때
		case MotionEvent.ACTION_DOWN:
			//줌상태가 아니면
			if (mode != ZOOM) {
				// 경과 시간을 재기.
				startMilliSecond = System.currentTimeMillis();
				
				//롱클릭이벤트 처리를 위해 등록
				//일정시간 이상 클릭시 롱클릭 이벤트 발생
				mLongPressed = new LongClickControll(x,y);
				handler.postDelayed(mLongPressed , longClickTimeLimit);
			}
			
			//드래그에서 사용하기 위해 초기값으로 세팅
			start.set(x, y);
			pre.set(x, y);
			nPre.set(nx, ny);
			mode = DRAG;
			
			//위치표시
			//////////System.out.format("%f %f \n", x, y);
			
			break;
			
		//또 다른 손이 화면에 닿을때
		case MotionEvent.ACTION_POINTER_DOWN:
			//Log.d(TAG, "oldDist=" + oldDist);
			
			oldDist = spacing(event);
			if (oldDist > zoomSensitivity) {
				mode = ZOOM;
			}
			break;
			
		//손을 화면에서 뗄때
		case MotionEvent.ACTION_UP:
			handler.removeCallbacks(mLongPressed);
			
			//System.out.format("x y preX preY %f %f %f %f \n",x, y,start.x ,start.y);
			
			float dx = x - start.x;
			float dy = y - start.y;
			
			// 화면을 누른 시간을 구한다.
			dMilliSecond = System.currentTimeMillis() - startMilliSecond;
			
			float moveLimit = 10f;
			boolean isMoved = (Math.abs(dx)>moveLimit || Math.abs(dy)>moveLimit);
			////System.out.format("%f %f ",Math.abs(dx), Math.abs(dy));
			////System.out.println("isMoved : "+isMoved);
			
			//메모를 선택했는지 확인 로직
			if(!isMoved ){
				//롱클릭이벤트를 하면 드래그로 인식하는데, 그 사이시간안에 손가락을 띄우면 클릭으로 간주한다. 
				if(dMilliSecond < clickEventLimit){
					//Seperate what you got!
					Intent intent;
					selectedMemo = positionHelper.isMemoChecked(nx, ny);
					selectedGroup = positionHelper.isGroupChecked(nx, ny);
					if(selectedMemo != null){
						intent = new Intent(context, MemoActivity.class);
						//보기, 수정 화면으로 넘어가기. 
						intent.putExtra("selectedMemoTitle", selectedMemo.getMemoTitle());
						intent.putExtra("selectedMemoContent", selectedMemo.getMemoContent());
						intent.putExtra("selectedMemoId", selectedMemo.getMemoId());
						
						//color
						intent.putExtra("selectedMemoR", (int)selectedMemo.getRed()*255);
						intent.putExtra("selectedMemoG", (int)selectedMemo.getGreen()*255);
						intent.putExtra("selectedMemoB", (int)selectedMemo.getBlue()*255);
						
						((Activity)context).startActivityForResult(intent, MainActivity.UPDATE_MEMO_RESULT);
					}else if(selectedGroup!=null){
						intent = new Intent(context, GroupActivity.class);
						//보기, 수정 화면으로 넘어가기. 
						intent.putExtra("selectedGroupTitle", selectedGroup.getGroupTitle());
						intent.putExtra("selectedGroupId", selectedGroup.getGroupId());
						intent.putExtra("selectedGroupColor", selectedGroup.getGroupColor());
						intent.putExtra("selectedGroupSize", selectedGroup.getWidth());

						//color
						intent.putExtra("selectedGroupR", selectedGroup.getRed()*255);
						intent.putExtra("selectedGroupG", selectedGroup.getGreen()*255);
						intent.putExtra("selectedGroupB", selectedGroup.getBlue()*255);
						
						((Activity)context).startActivityForResult(intent, MainActivity.UPDATE_GROUP_RESULT);
					}
					tabMode= TAB;
				}
			}
			
			//오브젝트가 이동을 끝내고 주변 오브젝트들 간의 관계를 설정한다.
			if(selectedMemo != null){
				System.out.println("before position"+selectedMemo.getX()+", "+selectedMemo.getY());
				positionHelper.checkMemoCollision(selectedMemo);
				System.out.println("after position"+selectedMemo.getX()+", "+selectedMemo.getY());
				positionHelper.updateSpecificMemoForSetGroupId(selectedMemo);
			}

			if(selectedGroup != null){
				positionHelper.checkGroupCollision(selectedGroup);
				positionHelper.updateAllMemosInsideSpecificGroupForSetGroupId();
				GroupDAO groupDao = new GroupDAO(context);
				groupDao.updateGroup(selectedGroup);	
			}
			//화면에 그리기
			requestRender();
			
			//손가락을 떼면 선택 해제.
			selectedGroup = null;
			selectedMemo = null;
			tabMode = NONE;
			
			break;
		
		//두 손을 화면에 댓다가 한손을 뗄때
		case MotionEvent.ACTION_POINTER_UP:
			//Log.d(TAG, "mode=NONE");
			
			mode = NONE;
			break;
			
		//손을 대고 움직일때
		case MotionEvent.ACTION_MOVE:
			
			//1. 한손가락으로 선택된 메모를 움직이는 경우
			//2. 한손가락으로 화면을 이동하는 경우
			//3. 두손가락으로 줌인 줌아웃을 하는 경우 
			
			//한 손가락만 움직일 때
			if (mode == DRAG) {
				//Log.d(TAG, "DRAG");
				//////System.out.println(tabMode);
				dx = x - pre.x;
				dy = y - pre.y;
				pre.set(x,y);
				
				//일정범위 이상 움직였을때는, 롱클릭 이벤트를 해제함
				moveLimit = 10f;
				isMoved = (Math.abs(dx)>moveLimit || Math.abs(dy)>moveLimit);

				if(isMoved){
					handler.removeCallbacks(mLongPressed);

				}
				
				// 여기가 1번 
				// 객체가 선택된 상태+ 롱탭일 때는 객체를 이동시킨다.
				if(selectedMemo != null && tabMode == LONGTAB){
					//메모 이동 
					//처음 탭했을 때의 위치와 바로다음의 위치 사이거리를 계산하고 중점도 그 만큼 이동하도록 설정한다.
					
					//TODO dx와 movedDistanceX 와의 차이를 생각해보기.
					movedDistanceX =  nx - nPre.x;
					movedDistanceY =  ny - nPre.y;
					positionHelper.moveMemo(selectedMemo, movedDistanceX, movedDistanceY);
					
					//바로 이전 위치롤 setting을 해주어야 한다.
					nPre.set(nx, ny);
				}else if(selectedGroup != null && tabMode == LONGTAB){
					movedDistanceX =  nx -nPre.x;
					movedDistanceY =  ny -nPre.y;
					positionHelper.moveMemoInGroup(movedDistanceX, movedDistanceY);
					//그룹이동
					positionHelper.moveGroup(selectedGroup, movedDistanceX, movedDistanceY);
					
					//바로 이전 위치롤 setting을 해주어야 한다.
					nPre.set(nx, ny);
				}else{
					//여기는 2
					//화면 이동
					
					//얼마만큼 이동 할 것인가.
					float dM = 0.0015f*mr.zoom;
					
					//현재 화면 좌표를 임시값에 저장한다.
					float tempX = mr.px;
					float tempY = mr.py;
					
					//이동할 좌표를 임시값에 넣는다.
					tempX += -(dx*dM);
					tempY += +(dy*dM);
					
					//화면이 넘어가는지 확인한다.
					// TODO 버그 수정. 
					if(!isOutOfBoundary(tempX, tempY, mr.zoom)){
						mr.px = tempX;
						mr.py = tempY;
					}
					
					//////System.out.format(" x y %f %f \t", x, y);
					//////System.out.format(" nx ny %f %f \t", nx, ny);
					//////System.out.format(" px py %f %f \n", mr.px, mr.py);
					
				}
			} else if (mode == ZOOM) {
				//줌모드로 들어오면 롱클릭 이벤트를 해제함
				handler.removeCallbacks(mLongPressed);
				
				//Log.d(TAG, "ZOOM");
				float newDist = spacing(event);
				float scale = newDist / oldDist;	//확대,축소 여부

				float dZ = 0.05f;	//줌가속 변수
				float min = 1f;	//줌 최대
				float max = 12f;	//줌 최소
				
				float tempZoom = 0;
				tempZoom = mr.zoom;
				
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
						//최소경계보다 작아지면 확대한다 
						tempZoom += dZ;
					}else if (mr.zoom >= max){
						//최대경계보다 커지면 축소한다 
						tempZoom -= dZ;
					}
				}
				
				//화면경계를 검사해서 유효하면 줌을 적용한다 
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
		
		//////System.out.format("w h %d %d \n",mr.width, mr.height);
		
		float leftBoundary = -((Constants.DOT_BACKGROUND_SIZE/2) + margin)*ratioX;
		float rightBoundary = +((Constants.DOT_BACKGROUND_SIZE/2) + margin)*ratioX;
		float topBoundary = +((Constants.DOT_BACKGROUND_SIZE/2) + margin)*ratioY;
		float bottomBoundary = -((Constants.DOT_BACKGROUND_SIZE/2) + margin)*ratioY;
		
		//////System.out.format("boundary %f %f %f %f \n", leftBoundary, rightBoundary, topBoundary, bottomBoundary);
		
		//현재 화면의 상태
		float left = 0 ;
		float right = mr.width;
		float top = 0;
		float bottom = mr.height;
		
		float normalizedLeft = getNormalizedX(left, tempX, tempY, tempZoom);
		float normalizedRight = getNormalizedX(right, tempX, tempY, tempZoom);
		float normalizedTop = getNormalizedY(top, tempX, tempY, tempZoom);
		float normalizedBottom = getNormalizedY(bottom, tempX, tempY, tempZoom);
		
		//////System.out.format("normalized %f %f %f %f \n", normalizedLeft, normalizedRight, normalizedTop, normalizedBottom);
		
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
		
		//////System.out.println("false!!");
		
		return false;
	}
	
	/** Determine the space between the first two fingers */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	
	/**
	 * 정규화된 좌표를 구한다 
	 */
	//식이 너무 복잡해...
	//터치한 픽셀좌표(기기 해상도)를 OpenGL상의 좌표 (-1,-1부터 1,1같은)로 변환한다.
	public float getNormalizedX(float x){
		return ((((x/mr.width)* 2)-1)*mr.zoom*mr.width/mr.height*mr.fov)+mr.px;
	}
	public float getNormalizedY(float y){
		return ((-(((y/mr.height)*2)-1))*mr.zoom*mr.fov)+mr.py;
	}
	
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
		
		float x;
		float y;
		
		LongClickControll(float x, float y){
			this.x = x;
			this.y = y;
		}

		@Override
		public void run() {
			//정규화된 좌표
			float nx = getNormalizedX(x);
	    	float ny = getNormalizedY(y);
	    	
	    	selectedMemo = positionHelper.isMemoChecked(nx, ny);
	    	selectedGroup = positionHelper.isGroupChecked(nx, ny);
	    	//선택된 원을 확인
			if(selectedMemo!=null){
				//선택된걸 상위로
				System.out.println("long");
				setLongTab();
				mr.memoList.remove(selectedMemo);
				mr.memoList.add(selectedMemo);
				return;
			}else if(selectedGroup != null){
				//선택된걸 상위로
				setLongTab();
				mr.groupList.remove(selectedGroup);
				mr.groupList.add(selectedGroup);
				return;
			}
		}
		
		private void setLongTab(){
			tabMode=LONGTAB;
			//선택시 진동
			System.out.println("long");
			vibrator.vibrate(100);
		}	
	}
}

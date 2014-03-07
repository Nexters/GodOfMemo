package com.nexters.godofmemo.util;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import android.content.Context;

import com.nexters.godofmemo.dao.MemoDAO;
import com.nexters.godofmemo.object.Group;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.render.MemoRenderer;

public class PositionHelper {
	
	private Memo selectedMemo;
	private Group selectedGroup;
	private MemoRenderer mr;
	private Context context;
	private MemoDAO memoDao; 
	private static final int LeftTop = 0;
	private static final int LeftBottom = 1;
	private static final int RightTop = 2;
	private static final int RightBottom = 3;
	
	public PositionHelper(Context context, MemoRenderer mr){ 
		this.context = context;
		this.mr = mr;
		memoDao = new MemoDAO(context);
	}
	/**
	 * FILO 를 구현하기 위해서 queue를 deque로 변경.
	 * 
	 * chkX 탭을 한 위치와 메모의 중심(x) 사이의 차이를 구한다.
	 * chkY 탭을 한 위치와 메모의 중심(y) 사이의 차이를 구한다. 
	 * @param nx
	 * @param ny
	 * @return
	 */
	public Memo isMemoChecked(float nx, float ny){
		Deque<Memo> deque = new LinkedBlockingDeque<Memo>(mr.memoList); 
		
		while(deque.size()>0){
			Memo memo = deque.pollLast();
	
			float chkX = Math.abs(nx-memo.getX())/(memo.getWidth()/2);
			float chkY = Math.abs(ny-memo.getY())/(memo.getHeight()/2);

			////System.out.format(" nx ny chkX chkY%f %f %f %f \n", nx, ny, chkX, chkY);
			
			//이미지 여백을 고려하여 클릭 이벤트를 적용한다.
			if(chkX <= 0.9f && chkY <= 0.5f){
				//선택됨
				selectedMemo = memo;
				return selectedMemo;
			}
		}
		return null;
	}
	
	/**
	 * 문제점 : 메모 선택 로직이랑 동일하다.
	 * 
	 * nx, ny : tab한 좌표를 정규화한 것.
	 * chkX, chkY : 반지름에 대한 중점과의 거리 비율. 
	 * 선택 여부를 판단하기 위한 로직: chkX값이 그룹의 지름의 곱하기 1.2 보다 작다?
	 * @param nx
	 * @param ny
	 * @return
	 */
	public Group isGroupChecked(float nx, float ny){
		Deque<Group> deque = new LinkedBlockingDeque<Group>(mr.groupList);
		
		while(deque.size()>0){
			Group group = deque.pollLast();
	
			if(distanceBetweenCenters(nx, ny, group.getX(), group.getY()) <= group.getWidth()/2){
				//선택됨
				selectedGroup = group;
				return selectedGroup;
			}
		}
		return null;
	}
	
	/**
	 * How to find that selected memo exist in group boundary? 
	 * 그룹 리스트의 모든 그룹 x,y값을 찾아 경계 안에 있는 지 
	 * 
	 * @param selectedMemo
	 * @author skyler.shin
	 * @return
	 */
	public boolean isInGroup(Memo selectedMemo, Group group){
		float memoTexturePaddingBottom = selectedMemo.getHeight()/2 - selectedMemo.getHeight()*selectedMemo.ratioMarginBottom;
		float memoTexturePaddingTop = selectedMemo.getHeight()/2 - selectedMemo.getHeight()*selectedMemo.ratioMarginTop;
		float memoTexturePaddingLeft = selectedMemo.getWidth()/2 - selectedMemo.getWidth()*selectedMemo.ratioMarginLeft;
		
		float memoLeftTopVertexX = selectedMemo.getX() - selectedMemo.getWidth()/2;
		float memoLeftTopVertexY = selectedMemo.getY() + memoTexturePaddingTop;
		float memoLeftBottomVertexX = selectedMemo.getX() - selectedMemo.getWidth()/2;
		float memoLeftBottomVertexY = selectedMemo.getY() - memoTexturePaddingBottom;
		float memoRightTopVertexX = selectedMemo.getX() + selectedMemo.getWidth()/2;
		float memoRightTopVertexY = selectedMemo.getY() + memoTexturePaddingTop;
		float memoRightBottomVertexX = selectedMemo.getX() + selectedMemo.getWidth()/2;
		float memoRightBottomVertexY = selectedMemo.getY() - memoTexturePaddingBottom;
		
		float distanceBetweenGroupCenterAndMemoLeftTopVertex = (float)Math.sqrt(Math.pow((double)(group.getX() - memoLeftTopVertexX) , 2.0)+Math.pow((double)(group.getY() - memoLeftTopVertexY), 2.0));
		float distanceBetweenGroupCenterAndMemoRightTopVertex = (float)Math.sqrt(Math.pow((double)(group.getX() - memoRightTopVertexX) , 2.0)+Math.pow((double)(group.getY() - memoRightTopVertexY), 2.0));
		float distanceBetweenGroupCenterAndMemoLeftBottomVertex = (float)Math.sqrt(Math.pow((double)(group.getX() - memoLeftBottomVertexX) , 2.0)+Math.pow((double)(group.getY() - memoLeftBottomVertexY), 2.0));
		float distanceBetweenGroupCenterAndMemoRightBottomVertex = (float)Math.sqrt(Math.pow((double)(group.getX() - memoRightBottomVertexX) , 2.0)+Math.pow((double)(group.getY() - memoRightBottomVertexY), 2.0));
		
		
		if(relativeDirection(group.getX(), selectedMemo.getX(),group.getY(),selectedMemo.getY()) == LeftBottom && distanceBetweenGroupCenterAndMemoLeftTopVertex > group.getWidth()/2 ){
			//오른쪽 하단 모서리
			//System.out.println(selectedMemo.getMemoContent()+ "isInGroup memo is located on the right bottom side of"+ group.getGroupTitle());
			return false;
		}else if(relativeDirection(group.getX(), selectedMemo.getX(),group.getY(),selectedMemo.getY()) == LeftTop &&distanceBetweenGroupCenterAndMemoLeftBottomVertex > group.getWidth()/2){
			//오른쪽 상단 모서리 
			//System.out.println(selectedMemo.getMemoContent()+ "isInGroup memo is located on the right top side of"+ group.getGroupTitle());
			return false;
		}else if(relativeDirection(group.getX(), selectedMemo.getX(),group.getY(),selectedMemo.getY()) == RightBottom && distanceBetweenGroupCenterAndMemoRightTopVertex > group.getWidth()/2){
			//왼쪽 하단 모서리 
			//System.out.println(selectedMemo.getMemoContent()+ "isInGroup memo is located on the left bottom side of"+ group.getGroupTitle());
			return false;
		}else if(relativeDirection(group.getX(), selectedMemo.getX(),group.getY(),selectedMemo.getY()) == RightTop && distanceBetweenGroupCenterAndMemoRightBottomVertex > group.getWidth()/2){
			//왼쪽 상단 모서리
			//System.out.println(selectedMemo.getMemoContent()+ "isInGroup memo is located on the left top side of"+ group.getGroupTitle());
			return false;
		}
		/**
		if(distanceX>distanceY && memoTexturePaddingLeft+group.getWidth()/2 > distanceX){
			//X축의 차이가 Y축보다 클 때는 X축을 기준으로 계산. 
			System.out.println("isInGroup memo is located on the right side of group");
			result = true;
		}else if(distanceX>distanceY && selectedMemo.getWidth()/2+group.getWidth()/2 > distanceX){
			//메모가 왼쪽에.
			System.out.println("isInGroup memo is located on the left side of group");
			result = true;
		}else if(distanceX<distanceY && memoTexturePaddingBottom+group.getHeight()/2 > distanceY){
			//X축의 차이가 Y축보다 작을 때는 Y축을 기준으로 계산. 
			System.out.println("isInGroup memo is located on the top side of group");
			result = true;
		}else if(distanceX<distanceY && memoTexturePaddingTop+group.getHeight()/2 > distanceY){
			//X축의 차이가 Y축보다 작을 때는 Y축을 기준으로 계산. 
			System.out.println("isInGroup memo is located on the bottom side of group");
			result = true;
		}
		**/	
		/**
		 * 정보 출력 확인. 
		System.out.println("memo_x"+selectedMemo.getX());
		System.out.println("group_x"+group.getX());
		System.out.println("distance: "+distance);
		System.out.println("distanceX: "+distanceX);
		System.out.println("distanceY: "+distanceY);
		System.out.println("memoTexturePaddingLeft: "+memoTexturePaddingLeft+"marginLeft: "+selectedMemo.ratioMarginLeft*selectedMemo.getWidth());
		System.out.println("memoTexturePaddingBottom: "+memoTexturePaddingBottom+"marginBottom: "+selectedMemo.ratioMarginBottom*selectedMemo.getHeight());
		System.out.println("group_height_half: "+ group.getHeight()/2);
		System.out.println("group_width_half: "+ group.getWidth()/2);
		System.out.println("group_width"+group.getWidth()+"group_height"+group.getHeight());
		System.out.println("selectedMemo_width"+selectedMemo.getWidth()+"selected_height"+selectedMemo.getHeight());
		System.out.println("selectedMemo_x"+selectedMemo.getX()+"group_x"+group.getX());
		System.out.println("selectedMemo_y"+selectedMemo.getY()+"group_y"+group.getY());	
		 **/		
		 
		return true;
	}
	/**
	 * 화면에 그려지는 모든 그룹의 위치를 계산. 
	 * => 메모에 아이디가 있다면, (보증을 할려면 가까이 있는 순서부터 체크. 그럼 순서개념이 정립되어야 합니다.)
	 * 리스트에는 먼저 생성된 순서 + 마지막 선택하는 것. 
	 * @param memo
	 */
	public void updateSpecificMemoForSetGroupId(Memo memo){
		String tempGroupId = null;
		for(Group group : mr.groupList){
			if(isInGroup(memo, group)){
				// 그룹에 있는 지 체크하는 로직. 
				tempGroupId = group.getGroupId();
				memo.setGroupId(group.getGroupId());
			}
			// 이전에 한 번이라도 세팅이 되었다면 다음에 검사할 때 넘어갈 수 있도록. 
			// 기존에 메모에 Id가 저장되어 있을 수 도 있으니 새로운 변수를 만들고 저장하는 게 좋은 방법 일 듯. 
			if(tempGroupId == null){
				memo.setGroupId(null);
			}
		}
		MemoDAO memoDao = new MemoDAO(context);
		memoDao.updateMemo(memo);
	}
	
	public void updateAllMemosInsideSpecificGroupForSetGroupId(){
		//현재 그룹안에 있는 것만 업데이트 
		for(Memo memo : mr.memoList){
			if(isInGroup(memo, selectedGroup)){
				memo.setGroupId(selectedGroup.getGroupId());
				memoDao.updateMemo(memo);
			}
		}
	}
	
	/**
	 * group안에 있는 것도 일종의 충돌난 경우. 
	 * 총 4가지 케이스가 있다. 
	 * 그룹이 그룹위에 있는 경우
	 * 그룹이 메모위에 있는 경우
	 * 메모가 그룹위에 있는 경우
	 * 메모가 메모위에 있는 경우
	 * 
	 * 추가되면 좋을 로직.
	 * 움직인 오브젝트 아래에 있는 것이 그룹인지, 메모인지 구분할 수 있는 방법.
	 * : 상황을 좀 더 생각해보면, 메모위에 올려져 있고 그룹안에도 속해 있는 경우가 있다. 
	 * 두개의 상황을 나눌 필요가 없는 듯. if else로 ... 
	 * 그렇다면 메모에 있는 지도 체크해야하지만 그룹 안에 있는 경우도 체크해야 한다. 
	 * 
	 * isInGroup 로직은 메모가 있을 때 모든 그룹과의 거리를 계산해보고
	 * 반지름보다 거리가 작은 그룹이 있는 지 체크한다. 
	 * 충돌이 일어남을 다르게 계산한다. 
	 * 
	 * 1) 메모를 움직였을 때 
	 * -메모? 그룹?
	 * 메모를 움직이고 나서
	 * 그룹안에 있는 지도 판별하고 
	 * 메모안에 있는 지도 판뵬. 
	 * 
	 * 메모 안에 있는 경우가 문제가 되는 경우는 중신간의 거리가 매우 가까워졌을 때가 문제다. 
	 * 해결방안. 
	 * 일단 중심사이의 거리를 구하는 식을 적용하여 
	 * 계산을 한다. 
	 * 그 후에 거리가 0.1f보다 작다면 0.1f만큼 왼쪽 대각선 방향으로 밀어낸다. 
	 * 
	 * ver 2 
	 * 방향성을 계산. 
	 * 1. 모든 메모들을 대해서?
	 * 2. 거리가 0.1f인 메모에 대해서 방향을 계산. 
	 * @param memo
	 */
	public void checkMemoCollision(Memo memo){
		float distance = 0;
		float preX = memo.getX();
		float preY = memo.getY();
		float movedDistanceX = 0;
		float movedDistanceY = 0;
		int direction = LeftBottom;
		for(Memo anotherMemo: mr.memoList){
			distance = distanceBetweenCenters(preX, preY, anotherMemo.getX(), anotherMemo.getY());
			//direction = relativeDirection(anotherMemo.getX(), anotherMemo.getY(), preX, preY);
			direction = relativeDirection(preX, preY,anotherMemo.getX(), anotherMemo.getY());
			if(distance < 0.1 && distance > 0) break;
		}
		if(distance != 0){
			switch (direction) {
			case LeftBottom:
				movedDistanceX = preX-0.1f;
				movedDistanceY = preY-0.1f;
				break;
			case LeftTop:
				movedDistanceX = preX-0.1f;
				movedDistanceY = preY+0.1f;
				break;
			case RightBottom:
				movedDistanceX = preX+0.1f;
				movedDistanceY = preY-0.1f;
				break;
			case RightTop:
				movedDistanceX = preX+0.1f;
				movedDistanceY = preY+0.1f;
				break;
			default:
				break;
			}
			
			moveMemo(memo, movedDistanceX, movedDistanceY);
		}
	}
	
	public void checkGroupCollision(Group group){
		float distance = 0;
		float preX = group.getX();
		float preY = group.getY();
		int direction = LeftBottom;
		float movedDistanceX = 0;
		float movedDistanceY = 0;
		
		for(Group anotherGroup: mr.groupList){
			distance = distanceBetweenCenters(preX, preY, anotherGroup.getX(), anotherGroup.getY());
			direction = relativeDirection(preX, preY,anotherGroup.getX(), anotherGroup.getY());
			if(distance < 0.3 && distance > 0) break;
		}
		if(distance != 0){
			if(distance != 0){
				switch (direction) {
				case LeftBottom:
					movedDistanceX = preX-0.3f;
					movedDistanceY = preY-0.3f;
					break;
				case LeftTop:
					movedDistanceX = preX-0.3f;
					movedDistanceY = preY+0.3f;
					break;
				case RightBottom:
					movedDistanceX = preX+0.3f;
					movedDistanceY = preY-0.3f;
					break;
				case RightTop:
					movedDistanceX = preX+0.3f;
					movedDistanceY = preY+0.3f;
					break;
				default:
					break;
				}
			}
			moveGroup(group, movedDistanceX, movedDistanceY);
			moveMemoInGroup(movedDistanceX, movedDistanceY);
		}
	}
	
	public void moveMemoInGroup(float movedDistanceX, float movedDistanceY){
		//그룹에 속해있는 메모 이동. 
		for(Memo memo: mr.memoList){
			if(memo.getGroupId() == null){
			}else{
				if(memo.getGroupId().equals(selectedGroup.getGroupId())){
					memo.setX(memo.getX() + movedDistanceX);
					memo.setY(memo.getY() + movedDistanceY);
					memo.setVertices();
				}	
			}
		}
	}
	
	public void moveGroup(Group group, float movedDistanceX, float movedDistanceY){
		group.setX(movedDistanceX);
		group.setY(movedDistanceY);
		group.setVertices();
	}
	public void moveMemo(Memo memo, float movedDistanceX, float movedDistanceY){
		memo.setX(movedDistanceX);
		memo.setY(movedDistanceY);
		memo.setVertices();
	}
	
	private float distanceBetweenCenters(float cx1, float cy1,float cx2,float cy2){
		float distanceX = Math.abs(cx1 - cx2); 
		float distanceY = Math.abs(cy1 - cy2);	
		// 중심점 사이의 거리 
		float distance = (float)Math.sqrt(Math.pow((double)distanceX, 2.0)+Math.pow((double)distanceY, 2.0));
		return distance;
	}
	
	/**
	 * default direction is LeftBottom
	 * @param cx1
	 * @param cy1
	 * @param cx2
	 * @param cy2
	 * @return
	 */
	private int relativeDirection(float cx1, float cy1,float cx2,float cy2){
		int direction = LeftBottom;
		if(cx1 < cx2 && cy1 < cy2){
			direction =  LeftBottom;
		}else if(cx1 < cx2 && cy1 > cy2){
			direction =  LeftTop;
		}else if(cx1 > cx2 && cy1 < cy2){
			direction = RightBottom;
		}else if(cx1 > cx2 && cy1 > cy2){
			direction =  RightTop;
		}
		return direction;
	}
}

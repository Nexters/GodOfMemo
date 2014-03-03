package com.nexters.godofmemo.util;

import java.util.Deque;
import java.util.concurrent.LinkedBlockingDeque;

import android.content.Context;

import com.nexters.godofmemo.dao.MemoDAO;
import com.nexters.godofmemo.object.Group;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.render.MemoRenderer;

public class PositionCheckHelper {
	
	private Memo selectedMemo;
	private Group selectedGroup;
	private MemoRenderer mr;
	private Context context;
	
	public PositionCheckHelper(Context context, MemoRenderer mr){ 
		this.context = context;
		this.mr = mr;
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
	
	public Group isGroupChecked(float nx, float ny){
		Deque<Group> deque = new LinkedBlockingDeque<Group>(mr.groupList);
		
		while(deque.size()>0){
			Group group = deque.pollLast();
	
			float chkX = Math.abs(nx-group.getX())/(group.getWidth()/2);
			float chkY = Math.abs(ny-group.getY())/(group.getHeight()/2);

			////System.out.format(" nx ny chkX chkY%f %f %f %f \n", nx, ny, chkX, chkY);
			
			//이미지 여백을 고려하여 클릭 이벤트를 적용한다.
			//절대적인 값 vs 상대적인 값. 고민을 해야겠다.
			if(chkX <= group.getWidth()*1.2 && chkY <= group.getHeight()*1.2){
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
		float distanceX = Math.abs(selectedMemo.getX() - group.getX()); 
		float distanceY = Math.abs(selectedMemo.getY()-group.getY());	
		// 중심점 사이의 거리 
		float distance = (float)Math.sqrt(Math.pow((double)distanceX, 2.0)+Math.pow((double)distanceY, 2.0));
		
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
		
		
		if(group.getX() < selectedMemo.getX() && group.getY() > selectedMemo.getY() && distanceBetweenGroupCenterAndMemoLeftTopVertex > group.getWidth()/2 ){
			//오른쪽 하단 모서리
			System.out.println(selectedMemo.getMemoContent()+ "isInGroup memo is located on the right bottom side of"+ group.getGroupTitle());
			return false;
		}else if(group.getX() < selectedMemo.getX() && group.getY() < selectedMemo.getY() &&distanceBetweenGroupCenterAndMemoLeftBottomVertex > group.getWidth()/2){
			//오른쪽 상단 모서리 
			System.out.println(selectedMemo.getMemoContent()+ "isInGroup memo is located on the right top side of"+ group.getGroupTitle());
			return false;
		}else if(group.getX() > selectedMemo.getX() && group.getY() > selectedMemo.getY() && distanceBetweenGroupCenterAndMemoRightTopVertex > group.getWidth()/2){
			//왼쪽 하단 모서리 
			System.out.println(selectedMemo.getMemoContent()+ "isInGroup memo is located on the left bottom side of"+ group.getGroupTitle());
			return false;
		}else if(group.getX() > selectedMemo.getX() && group.getY() < selectedMemo.getY() && distanceBetweenGroupCenterAndMemoRightBottomVertex > group.getWidth()/2){
			//왼쪽 상단 모서리
			System.out.println(selectedMemo.getMemoContent()+ "isInGroup memo is located on the left top side of"+ group.getGroupTitle());
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
		MemoDAO memoDao = new MemoDAO(context);
		//현재 그룹안에 있는 것만 업데이트 
		for(Memo memo : mr.memoList){
			if(isInGroup(memo, selectedGroup)){
				memo.setGroupId(selectedGroup.getGroupId());
				memoDao.updateMemo(memo);
			}
		}
	}
}

package com.nexters.godofmemo;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ConfigurationInfo;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import com.nexters.godofmemo.dao.GroupDAO;
import com.nexters.godofmemo.dao.MemoDAO;
import com.nexters.godofmemo.object.Group;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.object.helper.MemoHelper;
import com.nexters.godofmemo.util.Constants;
import com.nexters.godofmemo.util.Font;
import com.nexters.godofmemo.view.MemoGLView;

public class MainActivity extends ActionBarActivity implements OnClickListener {
	/**
	 * Hold a reference to our GLSurfaceView
	 */
	private MemoGLView glSurfaceView;
	private boolean rendererSet = false;
	public static final int CREATE_MEMO_RESULT = 0;
	public static final int UPDATE_MEMO_RESULT = 1;
	public static final int CREATE_GROUP_RESULT = 2;
	public static final int UPDATE_GROUP_RESULT = 3;
	private String memoId;
	private String groupId;
	private String groupTitle;
	private float groupSize;
	private MemoDAO memoDao;
	private GroupDAO groupDao;

	// group color rgb
	private int groupColorR;
	private int groupColorG;
	private int groupColorB;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		// 폰트 초기화
		Font.setTf(Typeface.createFromAsset(getAssets(), "fonts/nanum.ttf"));

		// 커스톰 액션바 구현.
		getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
		getSupportActionBar().setCustomView(R.layout.actionbar_memoboard);

		// 쓰기버튼에 클릭이벤트를 등록한다
		findViewById(R.id.action_write).setOnClickListener(this);
		findViewById(R.id.action_group).setOnClickListener(this);
		findViewById(R.id.memoBoardTitle).setOnClickListener(this);

		// Check if the system supports OpenGL ES 2.0.
		final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);

		final ConfigurationInfo configurationInfo = activityManager
				.getDeviceConfigurationInfo();

		/*
		 * final boolean supportsEs2 = configurationInfo.reqGlEsVersion >=
		 * 0x20000;
		 */

		// Even though the latest emulator supports OpenGL ES 2.0,
		// it has a bug where it doesn't set the reqGlEsVersion so
		// the above check doesn't work. The below will detect if the
		// app is running on an emulator, and assume that it supports
		// OpenGL ES 2.0.
		final boolean supportsEs2 = configurationInfo.reqGlEsVersion >= 0x20000
				|| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1 && (Build.FINGERPRINT
						.startsWith("generic")
						|| Build.FINGERPRINT.startsWith("unknown")
						|| Build.MODEL.contains("google_sdk")
						|| Build.MODEL.contains("Emulator") || Build.MODEL
							.contains("Android SDK built for x86")));

		if (supportsEs2) {
			glSurfaceView = new MemoGLView(this);
			rendererSet = true;
		} else {
			/*
			 * This is where you could create an OpenGL ES 1.x compatible
			 * renderer if you wanted to support both ES 1 and ES 2. Since we're
			 * not doing anything, the app will crash if the device doesn't
			 * support OpenGL ES 2.0. If we publish on the market, we should
			 * also add the following to AndroidManifest.xml:
			 *
			 * <uses-feature android:glEsVersion="0x00020000"
			 * android:required="true" />
			 *
			 * This hides our app from those devices which don't support OpenGL
			 * ES 2.0.
			 */
			Toast.makeText(this, "This device does not support OpenGL ES 2.0.",
					Toast.LENGTH_LONG).show();
			return;
		}

		setContentView(glSurfaceView);

	}

	@Override
	protected void onPause() {
		super.onPause();

		if (rendererSet) {
			glSurfaceView.onPause();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();

		if (rendererSet) {
			glSurfaceView.onResume();
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.action_write:
			// 메모작성!
			intent = new Intent(this, MemoActivity.class);
			startActivityForResult(intent, CREATE_MEMO_RESULT);
			break;

		case R.id.action_group:
			// 그룹생성!
			intent = new Intent(this, GroupActivity.class);
			startActivityForResult(intent, CREATE_GROUP_RESULT);
			break;

		case R.id.memoBoardTitle:
			// 튜토리얼
			intent = new Intent(this, TutorialActivity.class);
			startActivity(intent);

		}

	}

	/**
	 * 메모작성 화면이나 그룹작성 화면에 다녀온 후 실행함.
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		// 비정상종료면?
		// 뒤로가기 버튼을 눌렀는지 체크
		if (resultCode != Activity.RESULT_OK
				|| data.getIntExtra("checkBack", 0) != 0) {
			return;
		}

		// 또 다른 액티비티를 사용하고 나서 결과 값으로 생성화면과 수정화면을 구분한다.
		switch (requestCode) {
		case CREATE_MEMO_RESULT:
			createMemo(data);
			break;

		case UPDATE_MEMO_RESULT:
			updateMemo(data);
			break;

		case CREATE_GROUP_RESULT:
			createGroup(data);
			break;
		// TODO Please write Update logic @Subin
		case UPDATE_GROUP_RESULT:
			updateGroup(data);
			break;
		}

		// the position of objects initialize
		glSurfaceView.initializePosition();
	}

	/**
	 * 그룹 수정
	 *
	 * @param data
	 */
	private void updateGroup(Intent data) {
		groupDao = new GroupDAO(getApplicationContext());

		groupTitle = data.getStringExtra("groupTitle");
		groupId = data.getStringExtra("selectedGroupId");
		// groupColor = data.getIntExtra("selectedGroupColor",
		// Group.GROUP_COLOR_BLUE);
		groupSize = data.getFloatExtra("groupSize",
				Constants.GROUP_DEFAULT_SIZE);

		// ccolor
		groupColorR = data.getIntExtra("selectedGroupR", 100);
		groupColorG = data.getIntExtra("selectedGroupG", 100);
		groupColorB = data.getIntExtra("selectedGroupB", 100);

		System.out.println("MainActivity  UPDATE_GROUP_RESULT " + groupSize);
		// 휴지통 버튼을 눌렀는지 체크
		if (data.getBooleanExtra("delete", false)) {
			Group deleteGroup = groupDao.getGroupInfo(groupId);
			groupDao.delGroup(deleteGroup);
			removeGroup(deleteGroup);
			createToast("그룹 삭제");
			return;
		}

		// 수정된 메모 정보를 갱신한다.
		Group updateGroup = groupDao.getGroupInfo(groupId);
		updateGroup.setGroupTitle(groupTitle);
		// updateGroup.setGroupColor(groupColor);
		updateGroup.setColor(groupColorR, groupColorG, groupColorB);
		updateGroup.setWidth(groupSize);
		updateGroup.setHeight(groupSize);
		updateGroup.setVertices();
		groupDao.updateGroup(updateGroup);

		// 새로 그리기 위해.
		removeGroup(updateGroup);
		glSurfaceView.mr.groupList.add(updateGroup);
	}

	/**
	 * 신규 그룹 생성!!
	 *
	 * @param data
	 */
	private void createGroup(Intent data) {
		groupDao = new GroupDAO(getApplicationContext());
		// You need to check whether write code in Group Activity.
		groupTitle = data.getStringExtra("groupTitle");
		// groupColor = data.getIntExtra("selectedGroupColor",
		// Group.GROUP_COLOR_BLUE);
		groupSize = data.getFloatExtra("groupSize",
				Constants.GROUP_DEFAULT_SIZE);

		// ccolor
		groupColorR = data.getIntExtra("selectedGroupR", 100);
		groupColorG = data.getIntExtra("selectedGroupG", 100);
		groupColorB = data.getIntExtra("selectedGroupB", 100);

		// TODO 새 메모 체크하기
		// 메모를 저장한다.
		System.out.println("MainActivity  " + groupSize);
		Group newGroup = new Group();
		newGroup.setColor(groupColorR, groupColorG, groupColorB);

		// 새로 생성한 메모에 아이디를 설정.
		long groupIdL = groupDao.insertGroup(newGroup);
		groupId = String.valueOf(groupIdL);
		newGroup.setGroupId(groupId);

		// 새 메모가 생겼을때 토스트
		String newText = "새 그룹!";
		createToast(newText);

		// 화면에 그릴 목록에 추가
		glSurfaceView.mr.groupList.add(newGroup);

	}

	/**
	 * 메모 수정!
	 *
	 * @param data
	 */
	private void updateMemo(Intent data) {
		//소포에 담겨온 메모~
		Memo memo = data.getParcelableExtra("memo");

		// 휴지통 버튼을 눌렀는지 체크 => 메모삭제.
		if (data.getBooleanExtra("delete", false)) {
			removeMemo(memo);
			createToast("메모 삭제");
			return;
		}else{
			// 새로 그리기 위해.
			removeMemo(memo);
			memo.setVertices();
			glSurfaceView.mr.memoList.add(memo);
		}

	}

	/**
	 * 메모 생성!!
	 *
	 * @param data
	 */
	private void createMemo(Intent data) {
		//소포에 받아온 메모~
		Memo newMemo = data.getParcelableExtra("memo");

		// 위치를 지정하고.
		MemoHelper.setInitPosition(glSurfaceView, newMemo);

		// 화면에 그릴 목록에 추가
		glSurfaceView.mr.memoList.add(newMemo);

		// 새 메모가 생겼을때 토스트
		String newText = "새 메모!";
		createToast(newText);
	}

	/**
	 * renderer의 memoList안에 있는 memo를 지우는 로직을 메서드화.
	 */
	private void removeMemo(Memo updateMemo) {
		for (Memo memo : glSurfaceView.mr.memoList) {
			if (memo.getMemoId().equals(updateMemo.getMemoId())) {
				glSurfaceView.mr.memoList.remove(memo);
			}
		}
	}

	/**
	 * ....
	 */
	private void removeGroup(Group updateGroup) {
		for (Group group : glSurfaceView.mr.groupList) {
			if (group.getGroupId().equals(updateGroup.getGroupId())) {
				glSurfaceView.mr.groupList.remove(group);
			}
		}
	}

	/**
	 * 토스트를 굽는다.
	 *
	 * @param text
	 */
	private void createToast(String text) {
		int duration = Toast.LENGTH_SHORT;
		Toast toast = Toast.makeText(this, text, duration);
		toast.show();
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
	}

}

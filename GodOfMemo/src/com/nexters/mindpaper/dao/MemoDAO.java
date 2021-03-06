package com.nexters.mindpaper.dao;

import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.nexters.mindpaper.db.AllDBHelper;
import com.nexters.mindpaper.db.AllSQL;
import com.nexters.mindpaper.object.Memo;
import com.nexters.mindpaper.util.Util;

/**
 * 이야기에 대한 정보를 읽고/쓰는 객체. DataAccessObject.
 *
 * @author lifenjoy51
 *
 */
public class MemoDAO {

	Context context;

	/**
	 *  Database fields
	 */
	private SQLiteDatabase database;
	private final AllDBHelper dbHelper;
	private final String[] allColumns = { AllSQL.COL_MEMO_ID, AllSQL.COL_MEMO_TITLE,
			AllSQL.COL_MEMO_CONTENT, AllSQL.COL_MEMO_COLOR,
			AllSQL.COL_MEMO_DATE, AllSQL.COL_MEMO_TIME, AllSQL.COL_MEMO_X,
			AllSQL.COL_MEMO_Y, AllSQL.COL_MEMO_WIDTH, AllSQL.COL_MEMO_HEIGHT,
			AllSQL.COL_MEMO_RED, AllSQL.COL_MEMO_GREEN, AllSQL.COL_MEMO_BLUE };

	/**
	 * 생성할때 dbHelper 초기화
	 * @param context
	 */
	public MemoDAO(Context context) {
		this.context = context;
		this.dbHelper = new AllDBHelper(context);
	}

	/**
	 *  메모 목록 조회
	 * @return
	 */
	public ConcurrentLinkedQueue<Memo> getMemoList() {
		database = dbHelper.getReadableDatabase();
		ConcurrentLinkedQueue<Memo> memoList = new ConcurrentLinkedQueue<Memo>();

		Cursor cursor = database.query(AllSQL.TABLE_MEMO_INFO,
				allColumns, null, null, null, null, AllSQL.COL_MEMO_DATE
						+ " ASC, " + AllSQL.COL_MEMO_TIME + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Memo memo = getMemo(cursor);
			memoList.add(memo);
			cursor.moveToNext();
		}

		// Make sure to close the cursor
		cursor.close();
		database.close();

		return memoList;
	}

	/**
	 *  Memo 한개 가져오기
	 * @param memoId
	 * @return
	 */
	public Memo getMemoInfo(String memoId) {
		database = dbHelper.getReadableDatabase();
		Cursor cur = database.query(AllSQL.TABLE_MEMO_INFO,
				allColumns, AllSQL.COL_MEMO_ID + " = " + memoId, null, null,
				null, null);
		cur.moveToFirst(); // 커서 처음으로

		Memo returnedMemo = getMemo(cur); // 반환할 객체
		cur.close();
		database.close();

		return returnedMemo;
	}

	/**
	 *  Memo 입력
	 *
	 * @param memo
	 * @return
	 */
	public Long insertMemo(Memo memo) {
		database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();

		values.put(AllSQL.COL_MEMO_TITLE, memo.getMemoTitle());
		values.put(AllSQL.COL_MEMO_CONTENT, memo.getMemoContent());
		values.put(AllSQL.COL_MEMO_DATE, Util.getDate());
		values.put(AllSQL.COL_MEMO_TIME, Util.getTime());

		values.put(AllSQL.COL_MEMO_X, memo.getX());
		values.put(AllSQL.COL_MEMO_Y, memo.getY());
		values.put(AllSQL.COL_MEMO_WIDTH, memo.getWidth());
		values.put(AllSQL.COL_MEMO_HEIGHT, memo.getHeight());

		values.put(AllSQL.COL_MEMO_RED, memo.getRed());
		values.put(AllSQL.COL_MEMO_GREEN, memo.getGreen());
		values.put(AllSQL.COL_MEMO_BLUE, memo.getBlue());

		long insertedId = database.insert(AllSQL.TABLE_MEMO_INFO,
				null, values);
		database.close();

		//Log.i("memo is inserted",String.valueOf(insertedId));
		return insertedId;
	}

	/**
	 *  Memo 수정
	 *
	 * @param memo
	 * @return
	 */
	public Integer updateMemo(Memo memo) {
		database = dbHelper.getWritableDatabase();
		String memoId = memo.getMemoId();
		ContentValues values = new ContentValues();

		values.put(AllSQL.COL_MEMO_TITLE, memo.getMemoTitle());
		values.put(AllSQL.COL_MEMO_CONTENT, memo.getMemoContent());
		values.put(AllSQL.COL_MEMO_DATE, memo.getMemoDate());
		values.put(AllSQL.COL_MEMO_TIME, memo.getMemoTime());

		values.put(AllSQL.COL_MEMO_X, memo.getX());
		values.put(AllSQL.COL_MEMO_Y, memo.getY());
		values.put(AllSQL.COL_MEMO_WIDTH, memo.getWidth());
		values.put(AllSQL.COL_MEMO_HEIGHT, memo.getHeight());

		values.put(AllSQL.COL_MEMO_RED, memo.getRed());
		values.put(AllSQL.COL_MEMO_GREEN, memo.getGreen());
		values.put(AllSQL.COL_MEMO_BLUE, memo.getBlue());

		int rtn = database.update(AllSQL.TABLE_MEMO_INFO, values,
				AllSQL.COL_MEMO_ID + " = " + memoId, null);
		database.close();

		//Log.i("memo is updated",String.valueOf(rtn));

		return rtn;
	}


	/**
	 *  Memo 삭제
	 *
	 * @param memo
	 * @return
	 */
	public Integer delMemo(Memo memo) {
		database = dbHelper.getWritableDatabase();
		String memoId = memo.getMemoId();
		int rtn = database.delete(AllSQL.TABLE_MEMO_INFO,
				AllSQL.COL_MEMO_ID + " = " + memoId, null);
		database.close();

		//Log.i("memo is deleted",String.valueOf(rtn));
		return rtn;
	}

	/**
	 *  커서에서 자료 받아오기
	 *
	 * @param cursor
	 * @return
	 */
	private Memo getMemo(Cursor cursor) {
		Memo memo = new Memo(); // 객체 초기화

		//System.out.println("getMemo");
		//System.out.println(cursor.getCount());

		if(cursor.getCount()==0) return memo;

		//기본정보
		memo.setMemoId(cursor.getString(0));
		memo.setMemoTitle(cursor.getString(1));
		memo.setMemoContent(cursor.getString(2));
		//memo.setMemoColor(cursor.getInt(3));
		memo.setMemoDate(cursor.getString(4));
		memo.setMemoTime(cursor.getString(5));

		//위치 크기
		memo.setX(cursor.getFloat(6));
		memo.setY(cursor.getFloat(7));
		memo.setWidth(cursor.getFloat(8));
		memo.setHeight(cursor.getFloat(9));

		//색정보
		memo.setRed(cursor.getFloat(10));
		memo.setGreen(cursor.getFloat(11));
		memo.setBlue(cursor.getFloat(12));

		//좌표설정
		memo.setVertices();

		//로그
		Log.i("memomemo", memo.toString());

		return memo;
	}

}
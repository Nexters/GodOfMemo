package com.nexters.godofmemo.dao;

import java.util.LinkedList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.nexters.godofmemo.db.MemoDBHelper;
import com.nexters.godofmemo.object.Memo;

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
	private MemoDBHelper dbHelper;
	private String[] allColumns = { MemoDBHelper.COL_MEMO_ID,
			MemoDBHelper.COL_MEMO_CONTENT, MemoDBHelper.COL_MEMO_DATE,
			MemoDBHelper.COL_MEMO_TIME, MemoDBHelper.COL_MEMO_X,
			MemoDBHelper.COL_MEMO_Y, MemoDBHelper.COL_MEMO_WIDTH,
			MemoDBHelper.COL_MEMO_HEIGHT };

	/**
	 * 생성할때 dbHelper 초기화
	 * @param context
	 */
	public MemoDAO(Context context) {
		this.context = context; 
		dbHelper = new MemoDBHelper(context);
	}

	/**
	 * 쓰기전에 open해주고
	 * @throws SQLException
	 */
	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	/**
	 *  다쓰면 close한다
	 */
	public void close() {
		dbHelper.close();
	}

	/**
	 *  메모 목록 조회
	 * @return
	 */
	public List<Memo> getMemoList() {
		this.open();
		List<Memo> memoList = new LinkedList<Memo>();

		Cursor cursor = database.query(MemoDBHelper.TABLE_MEMO_INFO,
				allColumns, null, null, null, null, MemoDBHelper.COL_MEMO_DATE
						+ " ASC, " + MemoDBHelper.COL_MEMO_TIME + " ASC");

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Memo memo = getMemo(cursor);
			memoList.add(memo);
			cursor.moveToNext();
		}
		
		// Make sure to close the cursor
		cursor.close();
		this.close();
		
		return memoList;
	}

	/**
	 *  Memo 한개 가져오기
	 * @param memoId
	 * @return
	 */
	public Memo getMemoInfo(String memoId) {
		this.open();
		Cursor cur = database.query(MemoDBHelper.TABLE_MEMO_INFO,
				allColumns, MemoDBHelper.COL_MEMO_ID + " = " + memoId, null, null,
				null, null);
		cur.moveToFirst(); // 커서 처음으로

		Memo returnedMemo = getMemo(cur); // 반환할 객체
		cur.close();
		this.close();
		//Log.i("memo info",returnedMemo.toString());

		return returnedMemo;
	}

	/**
	 *  Memo 입력
	 *  
	 * @param memo
	 * @return
	 */
	public Long insertMemo(Memo memo) {
		this.open();
		ContentValues values = new ContentValues();
		
		values.put(MemoDBHelper.COL_MEMO_CONTENT, memo.getMemoContent());
		values.put(MemoDBHelper.COL_MEMO_DATE, "");
		values.put(MemoDBHelper.COL_MEMO_TIME, "");
		
		values.put(MemoDBHelper.COL_MEMO_X, memo.getX());
		values.put(MemoDBHelper.COL_MEMO_Y, memo.getY());
		values.put(MemoDBHelper.COL_MEMO_WIDTH, memo.getWidth());
		values.put(MemoDBHelper.COL_MEMO_HEIGHT, memo.getHeight());
		
		long insertedId = database.insert(MemoDBHelper.TABLE_MEMO_INFO,
				null, values);
		this.close();
		
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
		this.open();
		String memoId = memo.getMemoId();
		ContentValues values = new ContentValues();
		
		values.put(MemoDBHelper.COL_MEMO_CONTENT, memo.getMemoContent());
		values.put(MemoDBHelper.COL_MEMO_DATE, "");
		values.put(MemoDBHelper.COL_MEMO_TIME, "");
		
		values.put(MemoDBHelper.COL_MEMO_X, memo.getX());
		values.put(MemoDBHelper.COL_MEMO_Y, memo.getY());
		values.put(MemoDBHelper.COL_MEMO_WIDTH, memo.getWidth());
		values.put(MemoDBHelper.COL_MEMO_HEIGHT, memo.getHeight());
		
		int rtn = database.update(MemoDBHelper.TABLE_MEMO_INFO, values,
				MemoDBHelper.COL_MEMO_ID + " = " + memoId, null);
		this.close();
		
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
		this.open();
		String memoId = memo.getMemoId();
		int rtn = database.delete(MemoDBHelper.TABLE_MEMO_INFO,
				MemoDBHelper.COL_MEMO_ID + " = " + memoId, null);
		this.close();
		
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
		Memo memo = new Memo(context); // 객체 초기화
		
		if(cursor.getCount()==0) return memo;
		
		//기본정보
		memo.setMemoId(cursor.getString(0));
		memo.setMemoContent(cursor.getString(1));
		memo.setMemoDate(cursor.getString(2));
		memo.setMemoTime(cursor.getString(3));
		
		//위치 크기
		memo.setX(cursor.getFloat(4));
		memo.setY(cursor.getFloat(5));
		memo.setWidth(cursor.getFloat(6));
		memo.setHeight(cursor.getFloat(7));
		
		//좌표설정
		memo.setVertices();
		
		return memo;
	}
	
}
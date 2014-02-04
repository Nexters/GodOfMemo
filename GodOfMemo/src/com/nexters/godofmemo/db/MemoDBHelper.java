package com.nexters.godofmemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 메모를 저장할 Sqlite DB 테이블
 * 
 * @author lifenjoy51
 * 
 */
public class MemoDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_MEMO_INFO = "memo"; // TABLE이름

	/**
	 * 컬럼정보 
	 * COL_MEMO_ID 고유번호 
	 * COL_MEMO_CONTENT 메모 내용 
	 * COL_MEMO_DATE 메모 작성일자
	 */
	//기본정보
	public static final String COL_MEMO_ID = "memo_id";
	public static final String COL_MEMO_CONTENT = "memo_content";	
	public static final String COL_MEMO_DATE = "memo_date";
	public static final String COL_MEMO_TIME = "memo_time";
	
	//위치정보
	public static final String COL_MEMO_X = "memo_x";
	public static final String COL_MEMO_Y = "memo_y";
	public static final String COL_MEMO_WIDTH = "memo_width";
	public static final String COL_MEMO_HEIGHT = "memo_height";

	//DB정보
	private static final String DATABASE_NAME = "memo.db";
	private static final int DATABASE_VERSION = 1;

	/**
	 *  TABLE 생성문
	 */
	private static final String DATABASE_CREATE = 
			new StringBuilder().append("CREATE TABLE ")
			.append(TABLE_MEMO_INFO)
			.append("(")
				.append(COL_MEMO_ID)
				.append( " integer primary key autoincrement, ")
				.append(COL_MEMO_CONTENT)
				.append( " text, ")
				.append(COL_MEMO_DATE)
				.append( " text, ")
				.append(COL_MEMO_TIME)
				.append( " text, ")
				
				.append(COL_MEMO_X)
				.append( " real, ")
				.append(COL_MEMO_Y)
				.append( " real, ")
				.append(COL_MEMO_WIDTH)
				.append( " real, ")
				.append(COL_MEMO_HEIGHT)
				.append( " real ")
			.append(");")
			.toString();
	
	/**
	 *  Constructor
	 * @param context
	 */
	public MemoDBHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE); // TABLE생성
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(MemoDBHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);

		/**
		 *  TABLE 생성문
		 */
		final String TABLE_MEMO_INFO_TEMP = "memo_temp"; // TABLE이름
		final String CREATE_TEMP_TABLE = new StringBuilder().append("CREATE TABLE ")
				.append(TABLE_MEMO_INFO_TEMP)
				.append("(")
					.append(COL_MEMO_ID)
					.append( " integer primary key, ")
					.append(COL_MEMO_CONTENT)
					.append( " text, ")
					.append(COL_MEMO_DATE)
					.append( " text, ")
					.append(COL_MEMO_TIME)
					.append( " text, ")
					
					.append(COL_MEMO_X)
					.append( " real, ")
					.append(COL_MEMO_Y)
					.append( " real, ")
					.append(COL_MEMO_WIDTH)
					.append( " real, ")
					.append(COL_MEMO_HEIGHT)
					.append( " real ")
				.append(");")
				.toString();

		final String OLD_TO_TEMP = 
				new StringBuilder().append("INSERT INTO ")
				.append(TABLE_MEMO_INFO_TEMP)
				.append("(")
					.append(COL_MEMO_ID)
					.append( ", ")
					.append(COL_MEMO_CONTENT)
					.append( ", ")
					.append(COL_MEMO_DATE)
					.append( ", ")
					.append(COL_MEMO_TIME)
					.append( ", ")					
					.append(COL_MEMO_X)
					.append( ", ")
					.append(COL_MEMO_Y)
					.append( ", ")
					.append(COL_MEMO_WIDTH)
					.append( ", ")
					.append(COL_MEMO_HEIGHT)
				.append(") ")
					.append(" SELECT ")				
					.append(COL_MEMO_ID)
					.append( ", ")
					.append(COL_MEMO_CONTENT)
					.append( ", ")
					.append(COL_MEMO_DATE)
					.append( ", ")
					.append(COL_MEMO_TIME)
					.append( ", ")					
					.append(COL_MEMO_X)
					.append( ", ")
					.append(COL_MEMO_Y)
					.append( ", ")
					.append(COL_MEMO_WIDTH)
					.append( ", ")
					.append(COL_MEMO_HEIGHT)					
					.append( " FROM ")
					.append(TABLE_MEMO_INFO)
				.toString();

		final String TEMP_TO_NEW = 
				new StringBuilder()
				.append("INSERT INTO ")
					.append(TABLE_MEMO_INFO)
				.append("(")
					.append(COL_MEMO_ID)
					.append( ", ")
					.append(COL_MEMO_CONTENT)
					.append( ", ")
					.append(COL_MEMO_DATE)
					.append( ", ")
					.append(COL_MEMO_TIME)
					.append( ", ")					
					.append(COL_MEMO_X)
					.append( ", ")
					.append(COL_MEMO_Y)
					.append( ", ")
					.append(COL_MEMO_WIDTH)
					.append( ", ")
					.append(COL_MEMO_HEIGHT)
				.append(") ")
					.append(" SELECT ")				
						.append(COL_MEMO_ID)
						.append( ", ")
						.append(COL_MEMO_CONTENT)
						.append( ", ")
						.append(COL_MEMO_DATE)
						.append( ", ")
						.append(COL_MEMO_TIME)
						.append( ", ")					
						.append(COL_MEMO_X)
						.append( ", ")
						.append(COL_MEMO_Y)
						.append( ", ")
						.append(COL_MEMO_WIDTH)
						.append( ", ")
						.append(COL_MEMO_HEIGHT)					
					.append( " FROM ")
						.append(TABLE_MEMO_INFO_TEMP)
				.toString();

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMO_INFO_TEMP);		
		db.execSQL(CREATE_TEMP_TABLE);		
		db.execSQL(OLD_TO_TEMP);		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMO_INFO);		
		db.execSQL(DATABASE_CREATE);			
		db.execSQL(TEMP_TO_NEW);		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MEMO_INFO_TEMP);
		
	}

}

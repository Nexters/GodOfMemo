package com.nexters.godofmemo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * 이야기를 저장할 Sqlite DB 테이블
 * 
 * @author lifenjoy51
 * 
 */
public class MemoDBHelper extends SQLiteOpenHelper {

	public static final String TABLE_STORY_MASTER = "mystory"; // TABLE이름

	/**
	 * 컬럼정보 
	 * COL_IDX 고유번호 
	 * COL_CONTENT 이야기 내용 
	 * COL_STORY_DATE 이야기 작성일자
	 */
	public static final String COL_IDX = "idx";
	public static final String COL_CONTENT = "content";
	public static final String COL_STORY_DATE = "story_date";
	public static final String COL_STORY_TIME = "story_time";
	public static final String COL_STAMP_YN= "stamp_yn";
	public static final String COL_STAMPS= "stamps";
	public static final String COL_COMMENT= "comment";
	public static final String COL_COMMENT_ID= "comment_id";

	private static final String DATABASE_NAME = "eting_mystory.db";
	private static final int DATABASE_VERSION = 3;

	/**
	 *  TABLE 생성문
	 */
	private static final String DATABASE_CREATE = "CREATE TABLE "
			+ TABLE_STORY_MASTER + "(" 
			+ COL_IDX + " integer primary key, "
			+ COL_CONTENT + " text not null, " 
			+ COL_STORY_DATE + " text, "
			+ COL_STORY_TIME + " text, "
			+ COL_STAMP_YN + " text, "
			+ COL_STAMPS + " text, "
			+ COL_COMMENT + " text, "
			+ COL_COMMENT_ID + " integer "
			+ ");";

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
				+ oldVersion + " to " + newVersion
				+ ", which will destroy all old data");

		// TODO StoryDBHelper 버젼이 다를때 임시로 처리. 추후 변경 필요.
		/**
		 *  TABLE 생성문
		 */
		final String TABLE_STORY_MASTER_TEMP = "mystory_temp"; // TABLE이름
		final String CREATE_TEMP_TABLE = "CREATE TABLE "
				+ TABLE_STORY_MASTER_TEMP + "(" 
				+ COL_IDX + " integer primary key, "
				+ COL_CONTENT + " text not null, " 
				+ COL_STORY_DATE + " text, "
				+ COL_STORY_TIME + " text, "
				+ COL_STAMP_YN + " text, "
				+ COL_STAMPS + " text, "
				+ COL_COMMENT + " text, "
				+ COL_COMMENT_ID + " integer "
				+ ");";

		final String OLD_TO_TEMP = "INSERT INTO " + TABLE_STORY_MASTER_TEMP
				+ " ( " + COL_IDX + ", " + COL_CONTENT + ", " + COL_STORY_DATE
				+ ", " + COL_STORY_TIME + ", " + COL_STAMP_YN + ","
				+ COL_STAMPS + ", " + COL_COMMENT + " ) " + " SELECT "
				+ COL_IDX + ", " + COL_CONTENT + ", " + COL_STORY_DATE + ", "
				+ COL_STORY_TIME + ", " + COL_STAMP_YN + " , " + COL_STAMPS
				+ ", " + COL_COMMENT + "  FROM " + TABLE_STORY_MASTER;

		final String TEMP_TO_NEW = "INSERT INTO " + TABLE_STORY_MASTER + " ( "
				+ COL_IDX + ", " + COL_CONTENT + ", " + COL_STORY_DATE + ", "
				+ COL_STORY_TIME + ", " + COL_STAMP_YN + " , " + COL_STAMPS
				+ ", " + COL_COMMENT + " ) " + " SELECT " + COL_IDX + ", "
				+ COL_CONTENT + ", " + COL_STORY_DATE + ", " + COL_STORY_TIME
				+ ", " + COL_STAMP_YN + " , " + COL_STAMPS + ", " + COL_COMMENT
				+ " FROM " + TABLE_STORY_MASTER_TEMP;

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORY_MASTER_TEMP);		
		db.execSQL(CREATE_TEMP_TABLE);		
		db.execSQL(OLD_TO_TEMP);		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORY_MASTER);		
		db.execSQL(DATABASE_CREATE);			
		db.execSQL(TEMP_TO_NEW);		
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORY_MASTER_TEMP);
		
	}

}

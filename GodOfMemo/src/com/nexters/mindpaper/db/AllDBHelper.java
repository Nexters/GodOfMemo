package com.nexters.mindpaper.db;

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
public class AllDBHelper extends SQLiteOpenHelper {

	
	/**
	 *  Constructor
	 * @param context
	 */
	public AllDBHelper(Context context) {
		super(context, AllSQL.DATABASE_NAME, null, AllSQL.DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase database) {
		System.out.println("Create database");
		database.execSQL(AllSQL.GROUP_DATABASE_CREATE); // GROUP TABLE생성
		database.execSQL(AllSQL.MEMO_DATABASE_CREATE); // MEMO TABLE생성
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.w(AllDBHelper.class.getName(), "Upgrading database from version "
				+ oldVersion + " to " + newVersion);
		// TODO Handling case that existing old version database.
		db.execSQL("DROP TABLE IF EXISTS " + AllSQL.TABLE_GROUP_INFO_TEMP);		
		db.execSQL(AllSQL.CREATE_GROUP_TEMP_TABLE);		
		db.execSQL(AllSQL.GROUP_OLD_TO_TEMP);		
		db.execSQL("DROP TABLE IF EXISTS " + AllSQL.TABLE_GROUP_INFO);		
		db.execSQL(AllSQL.GROUP_DATABASE_CREATE);			
		db.execSQL(AllSQL.GROUP_TEMP_TO_NEW);		
		db.execSQL("DROP TABLE IF EXISTS " + AllSQL.TABLE_GROUP_INFO_TEMP);
		
		db.execSQL("DROP TABLE IF EXISTS " + AllSQL.TABLE_MEMO_INFO_TEMP);		
		db.execSQL(AllSQL.CREATE_MEMO_TEMP_TABLE);		
		db.execSQL(AllSQL.MEMO_OLD_TO_TEMP);		
		db.execSQL("DROP TABLE IF EXISTS " + AllSQL.TABLE_MEMO_INFO);		
		db.execSQL(AllSQL.MEMO_DATABASE_CREATE);			
		db.execSQL(AllSQL.MEMO_TEMP_TO_NEW);		
		db.execSQL("DROP TABLE IF EXISTS " + AllSQL.TABLE_MEMO_INFO_TEMP);
		
	}

}

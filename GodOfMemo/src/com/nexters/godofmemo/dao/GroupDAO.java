package com.nexters.godofmemo.dao;


import java.util.concurrent.ConcurrentLinkedQueue;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.nexters.godofmemo.db.AllDBHelper;
import com.nexters.godofmemo.db.AllSQL;
import com.nexters.godofmemo.object.Group;

/**
 * This object enable to control data about group
 * @author skyler.shin
 *
 */
public class GroupDAO {
	Context context; 
	
	/**
	 * database's fields
	 */
	private SQLiteDatabase database;
	private AllDBHelper dbHelper;
	private String[] allColumns = {AllSQL.COL_GROUP_ID,
			AllSQL.COL_GROUP_TITLE, AllSQL.COL_GROUP_COLOR,
			AllSQL.COL_GROUP_SYMBOLID, AllSQL.COL_GROUP_DATE,
			AllSQL.COL_GROUP_TIME, AllSQL.COL_GROUP_X,
			AllSQL.COL_GROUP_Y, AllSQL.COL_GROUP_WIDTH,
			AllSQL.COL_GROUP_HEIGHT,
			AllSQL.COL_GROUP_RED, AllSQL.COL_GROUP_GREEN,
			AllSQL.COL_GROUP_BLUE};
	
	/**
	 * In constructor, I initiate dbHelper object.
	 */
	public GroupDAO(Context context){
		this.context = context;
		this.dbHelper = new AllDBHelper(context);
	}
	
	
	/**
	 * This method get list of group.
	 */
	public ConcurrentLinkedQueue<Group> getGroupList(){
		database = dbHelper.getReadableDatabase();
		ConcurrentLinkedQueue<Group> groupList = new ConcurrentLinkedQueue<Group>();
		
		Cursor cursor = database.query(AllSQL.TABLE_GROUP_INFO, 
				allColumns, null, null, null, null, AllSQL.COL_GROUP_DATE
				+" ASC, "+ AllSQL.COL_GROUP_TIME +" ASC");
		cursor.moveToFirst();
		while(!cursor.isAfterLast()){
			Group group = getGroup(cursor);
			groupList.add(group);
			cursor.moveToNext();
		}
		
		//Make sure to close the cursor
		cursor.close();
		database.close();
				
		return groupList;
	}
	
	/**
	 *  Getting one of the groups
	 * @param groupId
	 * @return
	 */
	public Group getGroupInfo(String groupId) {
		database = dbHelper.getReadableDatabase();
		Cursor cur = database.query(AllSQL.TABLE_GROUP_INFO,
				allColumns, AllSQL.COL_GROUP_ID + " = " + groupId, null, null,
				null, null);
		cur.moveToFirst(); // 커서 처음으로

		Group returnedGroup = getGroup(cur); // 반환할 객체
		cur.close();
		database.close();
		//Log.i("memo info",returnedMemo.toString());

		return returnedGroup;
	}
	/**
	 * Use this method to insert group data. 
	 * @param group
	 * @return insertedId
	 */
	public Long insertGroup(Group group) {
		database = dbHelper.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put(AllSQL.COL_GROUP_TITLE , group.getGroupTitle());
		values.put(AllSQL.COL_GROUP_COLOR , group.getGroupColor());
		values.put(AllSQL.COL_GROUP_SYMBOLID , group.getGroupSymbolId());
		values.put(AllSQL.COL_GROUP_DATE , group.getGroupDate());
		values.put(AllSQL.COL_GROUP_TIME , group.getGroupTime());
		
		values.put(AllSQL.COL_GROUP_X , group.getX());
		values.put(AllSQL.COL_GROUP_Y , group.getY());
		values.put(AllSQL.COL_GROUP_WIDTH, group.getWidth());
		values.put(AllSQL.COL_GROUP_HEIGHT, group.getHeight());
		
		values.put(AllSQL.COL_GROUP_RED, group.getRed());
		values.put(AllSQL.COL_GROUP_GREEN, group.getGreen());
		values.put(AllSQL.COL_GROUP_BLUE, group.getBlue());
		
		long insertedId = database.insert(AllSQL.TABLE_GROUP_INFO,
				null, values);
		database.close();
		
		return insertedId;
	}
	/**
	 * Use this method to update group data. 
	 * @param group
	 * @return rtn
	 */
	public Integer updateGroup(Group group){
		database = dbHelper.getWritableDatabase();
		String groupId = group.getGroupId();
		ContentValues values = new ContentValues();
		
		values.put(AllSQL.COL_GROUP_TITLE , group.getGroupTitle());
		values.put(AllSQL.COL_GROUP_COLOR , group.getGroupColor());
		values.put(AllSQL.COL_GROUP_SYMBOLID , group.getGroupSymbolId());
		values.put(AllSQL.COL_GROUP_DATE , group.getGroupDate());
		values.put(AllSQL.COL_GROUP_TIME , group.getGroupTime());
		
		values.put(AllSQL.COL_GROUP_X , group.getX());
		values.put(AllSQL.COL_GROUP_Y , group.getY());
		values.put(AllSQL.COL_GROUP_WIDTH, group.getWidth());
		values.put(AllSQL.COL_GROUP_HEIGHT, group.getHeight());
		
		values.put(AllSQL.COL_GROUP_RED, group.getRed());
		values.put(AllSQL.COL_GROUP_GREEN, group.getGreen());
		values.put(AllSQL.COL_GROUP_BLUE, group.getBlue());
		
		int rtn = database.update(AllSQL.TABLE_GROUP_INFO, values,
				AllSQL.COL_GROUP_ID+" = "+ groupId, null);
		database.close();
		return rtn;
	}
	/**
	 * Use this method to delete group data. 
	 * @param group
	 * @return rtn;
	 */
	public Integer delGroup(Group group){
		database = dbHelper.getWritableDatabase();
		String groupId = group.getGroupId();
		int rtn = database.delete(AllSQL.TABLE_GROUP_INFO, 
				AllSQL.COL_GROUP_ID + "=" + groupId, null);
		database.close();
		return rtn;
	}
	
	/**
	 * To get group's data from cursor
	 */
	private Group getGroup(Cursor cursor){
		Group group = new Group(context); // initiate
		
		if(cursor.getCount() == 0) return group;
		
		//basic information
		group.setGroupId(cursor.getString(0));
		group.setGroupTitle(cursor.getString(1));
		//group.setGroupColor(cursor.getInt(2));
		group.setGroupSymbolId(cursor.getString(3));
		group.setGroupDate(cursor.getString(4));
		group.setGroupTime(cursor.getString(5));
		
		//position, size
		group.setX(cursor.getFloat(6));
		group.setY(cursor.getFloat(7));
		group.setWidth(cursor.getFloat(8));
		group.setHeight(cursor.getFloat(9));
		
		//color
		group.setRed(cursor.getFloat(10));
		group.setGreen(cursor.getFloat(11));
		group.setBlue(cursor.getFloat(12));
		
		group.setVertices();
		
		return group;
	}
	
	
}

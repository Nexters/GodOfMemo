package com.nexters.mindpaper.db;

public interface AllSQL {
	//DB정보
	public static final String DATABASE_NAME = "memo.db";
	public static final int DATABASE_VERSION = 1;

	public static final String TABLE_GROUP_INFO = "memo_group"; // Group table name

	//basic column
	public static final String COL_GROUP_ID = "group_id";
	public static final String COL_GROUP_TITLE = "group_title";
	public static final String COL_GROUP_RADIUS = "group_radius";
	public static final String COL_GROUP_SYMBOLID = "group_symbol_id";
	public static final String COL_GROUP_DATE = "group_date";
	public static final String COL_GROUP_TIME = "group_time";

	//position information. assuming we use rectangle for group's shape.
	public static final String COL_GROUP_X = "group_x";
	public static final String COL_GROUP_Y = "group_y";
	public static final String COL_GROUP_WIDTH = "group_width";
	public static final String COL_GROUP_HEIGHT = "group_height";

	//group color
	public static final String COL_GROUP_RED = "group_color_r";
	public static final String COL_GROUP_GREEN= "group_color_g";
	public static final String COL_GROUP_BLUE = "group_color_b";

	/**
	 *  TABLE 생성문
	 */
	public static final String GROUP_DATABASE_CREATE =
			new StringBuilder().append("CREATE TABLE ")
			.append(TABLE_GROUP_INFO)
			.append("(")
				.append(COL_GROUP_ID)
				.append( " integer primary key autoincrement, ")
				.append(COL_GROUP_TITLE)
				.append( " text, ")
				.append(COL_GROUP_RADIUS)
				.append( " integer, ")
				.append(COL_GROUP_SYMBOLID)
				.append("  text,  ")
				.append(COL_GROUP_DATE)
				.append( " text, ")
				.append(COL_GROUP_TIME)
				.append( " text, ")

				.append(COL_GROUP_X)
				.append( " real, ")
				.append(COL_GROUP_Y)
				.append( " real, ")
				.append(COL_GROUP_WIDTH)
				.append( " real, ")
				.append(COL_GROUP_HEIGHT)
				.append( " real, ")

				.append(COL_GROUP_RED)
				.append( " real, ")
				.append(COL_GROUP_GREEN)
				.append( " real, ")
				.append(COL_GROUP_BLUE)
				.append( " real ")


			.append(");")
			.toString();
	/**
	 * TABLE 생성.
	 */
	final String TABLE_GROUP_INFO_TEMP = "group_temp"; // temporary table.
	final String CREATE_GROUP_TEMP_TABLE = new StringBuilder().append("CREATE TABLE ")
			.append(TABLE_GROUP_INFO_TEMP)
			.append("(")
				.append(COL_GROUP_ID)
				.append( " integer primary key, ")
				.append(COL_GROUP_TITLE)
				.append( " text, ")
				.append(COL_GROUP_RADIUS)
				.append( " integer, ")
				.append(COL_GROUP_SYMBOLID)
				.append("  text,  ")
				.append(COL_GROUP_DATE)
				.append( " text, ")
				.append(COL_GROUP_TIME)
				.append( " text, ")

				.append(COL_GROUP_X)
				.append( " real, ")
				.append(COL_GROUP_Y)
				.append( " real, ")
				.append(COL_GROUP_WIDTH)
				.append( " real, ")
				.append(COL_GROUP_HEIGHT)
				.append( " real ")
			.append(");")
			.toString();

	final String GROUP_OLD_TO_TEMP =
			new StringBuilder().append("INSERT INTO ")
			.append(TABLE_GROUP_INFO_TEMP)
			.append("(")
				.append(COL_GROUP_ID)
				.append( ", ")
				.append(COL_GROUP_TITLE)
				.append( ", ")
				.append(COL_GROUP_RADIUS)
				.append( ", ")
				.append(COL_GROUP_SYMBOLID)
				.append(" ,  ")
				.append(COL_GROUP_DATE)
				.append( ", ")
				.append(COL_GROUP_TIME)
				.append( ", ")
				.append(COL_GROUP_X)
				.append( ", ")
				.append(COL_GROUP_Y)
				.append( ", ")
				.append(COL_GROUP_WIDTH)
				.append( ", ")
				.append(COL_GROUP_HEIGHT)
			.append(") ")
				.append(" SELECT ")
				.append(COL_GROUP_ID)
				.append( ", ")
				.append(COL_GROUP_TITLE)
				.append( ", ")
				.append(COL_GROUP_RADIUS)
				.append( ", ")
				.append(COL_GROUP_SYMBOLID)
				.append(" ,  ")
				.append(COL_GROUP_DATE)
				.append( ", ")
				.append(COL_GROUP_TIME)
				.append( ", ")
				.append(COL_GROUP_X)
				.append( ", ")
				.append(COL_GROUP_Y)
				.append( ", ")
				.append(COL_GROUP_WIDTH)
				.append( ", ")
				.append(COL_GROUP_HEIGHT)
				.append( " FROM ")
				.append(TABLE_GROUP_INFO)
			.toString();

	final String GROUP_TEMP_TO_NEW =
			new StringBuilder()
			.append("INSERT INTO ")
				.append(TABLE_GROUP_INFO)
			.append("(")
				.append(COL_GROUP_ID)
				.append( ", ")
				.append(COL_GROUP_TITLE)
				.append( ", ")
				.append(COL_GROUP_RADIUS)
				.append( ", ")
				.append(COL_GROUP_SYMBOLID)
				.append(" ,  ")
				.append(COL_GROUP_DATE)
				.append( ", ")
				.append(COL_GROUP_TIME)
				.append( ", ")
				.append(COL_GROUP_X)
				.append( ", ")
				.append(COL_GROUP_Y)
				.append( ", ")
				.append(COL_GROUP_WIDTH)
				.append( ", ")
				.append(COL_GROUP_HEIGHT)
			.append(") ")
				.append(" SELECT ")
					.append(COL_GROUP_ID)
					.append( ", ")
					.append(COL_GROUP_TITLE)
					.append( ", ")
					.append(COL_GROUP_RADIUS)
					.append( ", ")
					.append(COL_GROUP_SYMBOLID)
					.append(" ,  ")
					.append(COL_GROUP_DATE)
					.append( ", ")
					.append(COL_GROUP_TIME)
					.append( ", ")
					.append(COL_GROUP_X)
					.append( ", ")
					.append(COL_GROUP_Y)
					.append( ", ")
					.append(COL_GROUP_WIDTH)
					.append( ", ")
					.append(COL_GROUP_HEIGHT)
				.append( " FROM ")
					.append(TABLE_GROUP_INFO_TEMP)
			.toString();


	/////////////////////////////////////////////////////////
	////////////////Memo////////////////////////////////////
	///////////////////////////////////////////////////////
	public static final String TABLE_MEMO_INFO = "memo"; // TABLE이름

	/**
	 * 컬럼정보
	 * COL_MEMO_ID 고유번호
	 * COL_MEMO_CONTENT 메모 내용
	 * COL_MEMO_DATE 메모 작성일자
	 */
	//기본정보
	public static final String COL_MEMO_ID = "memo_id";
	public static final String COL_MEMO_TITLE = "memo_title";
	public static final String COL_MEMO_CONTENT = "memo_content";
	public static final String COL_MEMO_COLOR = "memo_color";
	public static final String COL_MEMO_DATE = "memo_date";
	public static final String COL_MEMO_TIME = "memo_time";
	public static final String COL_MEMO_GROUP_ID = "group_id";

	//위치정보
	public static final String COL_MEMO_X = "memo_x";
	public static final String COL_MEMO_Y = "memo_y";
	public static final String COL_MEMO_WIDTH = "memo_width";
	public static final String COL_MEMO_HEIGHT = "memo_height";

	//color
	public static final String COL_MEMO_RED = "memo_color_r";
	public static final String COL_MEMO_GREEN= "memo_color_g";
	public static final String COL_MEMO_BLUE = "memo_color_b";


	/**
	 *  TABLE 생성문
	 */
	public static final String MEMO_DATABASE_CREATE =
			new StringBuilder().append("CREATE TABLE ")
			.append(TABLE_MEMO_INFO)
			.append("(")
				.append(COL_MEMO_ID)
				.append( " integer primary key autoincrement, ")

				.append(COL_MEMO_TITLE)
				.append( " text, ")
				.append(COL_MEMO_CONTENT)
				.append( " text, ")
				.append(COL_MEMO_COLOR)
				.append( " integer, ")
				.append(COL_MEMO_DATE)
				.append( " text, ")
				.append(COL_MEMO_TIME)
				.append( " text, ")
				.append(COL_MEMO_GROUP_ID)
				.append( " text, ")

				.append(COL_MEMO_X)
				.append( " real, ")
				.append(COL_MEMO_Y)
				.append( " real, ")
				.append(COL_MEMO_WIDTH)
				.append( " real, ")
				.append(COL_MEMO_HEIGHT)
				.append( " real, ")

				.append(COL_MEMO_RED)
				.append( " real, ")
				.append(COL_MEMO_GREEN)
				.append( " real, ")
				.append(COL_MEMO_BLUE)
				.append( " real ")


			.append(");")
			.toString();

	/**
	 *  TABLE 생성문
	 */
	final String TABLE_MEMO_INFO_TEMP = "memo_temp"; // TABLE이름
	final String CREATE_MEMO_TEMP_TABLE = new StringBuilder().append("CREATE TABLE ")
			.append(TABLE_MEMO_INFO_TEMP)
			.append("(")
				.append(COL_MEMO_ID)
				.append( " integer primary key, ")
				.append(COL_MEMO_CONTENT)
				.append( " text, ")
				.append(COL_MEMO_COLOR)
				.append( " integer, ")
				.append(COL_MEMO_DATE)
				.append( " text, ")
				.append(COL_MEMO_TIME)
				.append( " text, ")
				.append(COL_MEMO_GROUP_ID)
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

	final String MEMO_OLD_TO_TEMP =
			new StringBuilder().append("INSERT INTO ")
			.append(TABLE_MEMO_INFO_TEMP)
			.append("(")
				.append(COL_MEMO_ID)
				.append( ", ")
				.append(COL_MEMO_CONTENT)
				.append( ", ")
				.append(COL_MEMO_COLOR)
				.append( ", ")
				.append(COL_MEMO_DATE)
				.append( ", ")
				.append(COL_MEMO_TIME)
				.append( ", ")
				.append(COL_MEMO_GROUP_ID)
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
				.append(COL_MEMO_COLOR)
				.append( ", ")
				.append(COL_MEMO_DATE)
				.append( ", ")
				.append(COL_MEMO_TIME)
				.append( ", ")
				.append(COL_MEMO_GROUP_ID)
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

	final String MEMO_TEMP_TO_NEW =
			new StringBuilder()
			.append("INSERT INTO ")
				.append(TABLE_MEMO_INFO)
			.append("(")
				.append(COL_MEMO_ID)
				.append( ", ")
				.append(COL_MEMO_CONTENT)
				.append( ", ")
				.append(COL_MEMO_COLOR)
				.append( ", ")
				.append(COL_MEMO_DATE)
				.append( ", ")
				.append(COL_MEMO_TIME)
				.append( ", ")
				.append(COL_MEMO_GROUP_ID)
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
					.append(COL_MEMO_COLOR)
					.append( ", ")
					.append(COL_MEMO_DATE)
					.append( ", ")
					.append(COL_MEMO_TIME)
					.append( ", ")
					.append(COL_MEMO_GROUP_ID)
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


}

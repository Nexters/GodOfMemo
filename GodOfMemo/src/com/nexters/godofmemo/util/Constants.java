package com.nexters.godofmemo.util;

public class Constants {
	public static final int POSITION_COMPONENT_COUNT = 2; // 점들의 위치정보. x,y 총 2개.
	public static final int BYTES_PER_FLOAT = 4;
	public static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2; // 텍스쳐 위치
																		// x,y 총
																		// 2개.
																		// 텍스트에
																		// 사용.
	public static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* BYTES_PER_FLOAT; // 한 점당 필요한 변수 개수는 2+2=4개. * 1float에 필요한 byte수
								// 4바이트.

	public static final int COLOR_COMPONENT_COUNT = 4; // color. 알파체널도 포함.
														// a,r,g,b. 4개.
	public static final int COLOR_STRIDE = (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT)
			* BYTES_PER_FLOAT; // 한 점당 필요한 변수. 2+4 = 6개.

	public static final int FLOATS_PER_VERTEX = 6;

	// 배경관련 변수
	public static final int DOT_SIZE = 100;
	public static final int DOT_BACKGROUND_SIZE = Constants.DOT_SIZE / 20;
	public static final int SCREEN_SIZE = 32;

	// 메모관련변수.
	public static final int MEMO_MAX_LINE = 7;

	//그룹관련 변수
	public static final float GROUP_DEFAULT_SIZE = 0.8f;

	// 뒤로가기 확인.
	public final static int BACK = 3;

}
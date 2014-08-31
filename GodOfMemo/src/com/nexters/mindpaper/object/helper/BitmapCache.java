package com.nexters.mindpaper.object.helper;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

public class BitmapCache {
	
	private static BitmapCache instance = new BitmapCache();

	public static Map<Integer, ByteBuffer> bitmapCache;
	
	private BitmapCache(){
		bitmapCache = new HashMap<Integer, ByteBuffer>();
	}
	
	public static BitmapCache getInstance(){
		return instance;
	}
	
}

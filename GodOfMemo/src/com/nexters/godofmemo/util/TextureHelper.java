package com.nexters.godofmemo.util;

import static android.opengl.GLES20.GL_LINEAR;
import static android.opengl.GLES20.GL_LINEAR_MIPMAP_LINEAR;
import static android.opengl.GLES20.GL_TEXTURE_2D;
import static android.opengl.GLES20.GL_TEXTURE_MAG_FILTER;
import static android.opengl.GLES20.GL_TEXTURE_MIN_FILTER;
import static android.opengl.GLES20.glBindTexture;
import static android.opengl.GLES20.glDeleteTextures;
import static android.opengl.GLES20.glGenTextures;
import static android.opengl.GLES20.glGenerateMipmap;
import static android.opengl.GLES20.glTexParameteri;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.Map;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.os.Build;
import android.util.Log;

import com.nexters.godofmemo.object.Group;
import com.nexters.godofmemo.object.Memo;
import com.nexters.godofmemo.object.helper.MemoHelper;

public class TextureHelper {
	private static final String TAG = "TextureHelper";

	private static Map<Integer, ByteBuffer> bitmapCache = new HashMap<Integer, ByteBuffer>();

	/**
	 * Loads a texture from a resource ID, returning the OpenGL ID for that
	 * texture. Returns 0 if the load failed.
	 *
	 * @param context
	 * @param resourceId
	 * @return
	 */
	public static int loadTexture(Context context, int resourceId) {

		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inScaled = false;

		// Read in the resource
		final Bitmap bitmap = BitmapFactory.decodeResource(
				context.getResources(), resourceId, options);

		int texture = loadBitmpTexture(bitmap);

		return texture;
	}

	/**
	 * Bitmap에서 텍스쳐를 가져온다.
	 *
	 * @param bitmap
	 * @return
	 */
	@TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
	public static int loadBitmpTexture(Bitmap bitmap) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR1) {
			return loadBitmpTexture(bitmap, bitmap.getGenerationId());
		} else {
			return loadBitmpTextureNoAlpha(bitmap);
		}
	}

	/**
	 * Bitmap에서 텍스쳐를 가져온다. 캐쉬사용.
	 *
	 * @param bitmap
	 * @param id
	 * @return
	 */
	public static int loadBitmpTexture(Bitmap bitmap, int id) {
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);

		if (textureObjectIds[0] == 0) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "Could not generate a new OpenGL texture object.");
			}
			return 0;
		}

		if (bitmap == null) {
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}

		ByteBuffer byteBuffer;
		int bitmapWidth = bitmap.getWidth();
		int bitmapHeight = bitmap.getHeight();

		if (bitmapCache.containsKey(id)) {
			byteBuffer = bitmapCache.get(id);
			// System.out.println("cached!!");
		} else {
			byteBuffer = ByteBuffer.allocateDirect(bitmapWidth * bitmapHeight
					* 4);
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
			IntBuffer ib = byteBuffer.asIntBuffer();

			int[] pixels = new int[bitmapWidth * bitmapHeight];
			bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth,
					bitmapHeight);
			for (int i = 0; i < pixels.length; i++) {
				ib.put(pixels[i] << 8 | pixels[i] >>> 24);
			}
			bitmapCache.put(id, byteBuffer);
		}

		// bitmap.recycle();

		byteBuffer.position(0);

		/*
		 * int[] pixels = new int[bitmapWidth * bitmapHeight];
		 * bitmap.getPixels(pixels, 0, bitmapWidth, 0, 0, bitmapWidth,
		 * bitmapHeight); for(int i=0; i<pixels.length; i++){ ib.put(pixels[i]
		 * << 8 | pixels[i] >>> 24); }
		 *
		 * byte[] buffer = new byte[bitmapWidth * bitmapHeight * 4]; for ( int y
		 * = 0; y < bitmapHeight; y++ ) for ( int x = 0; x < bitmapWidth; x++ )
		 * { int pixel = bitmap.getPixel(x, y); buffer[(y * bitmapWidth + x) * 4
		 * + 0] = (byte)((pixel >> 16) & 0xFF); buffer[(y * bitmapWidth + x) * 4
		 * + 1] = (byte)((pixel >> 8) & 0xFF); buffer[(y * bitmapWidth + x) * 4
		 * + 2] = (byte)((pixel >> 0) & 0xFF); buffer[(y * bitmapWidth + x) * 4
		 * + 3] = (byte)((pixel >> 24) & 0xFF); }
		 *
		 * ByteBuffer byteBuffer = ByteBuffer.allocateDirect(bitmapWidth *
		 * bitmapHeight * 4); byteBuffer.put(buffer).position(0);
		 */

		// Bind to the texture in OpenGL
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

		// alpha 주기위한 옵션
		GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
		GLES20.glEnable(GLES20.GL_BLEND);

		// Set filtering: a default must be set, or the texture will be
		// black.
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
				GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);

		// Load the bitmap into the bound texture.
		GLES20.glTexImage2D(GLES20.GL_TEXTURE_2D, 0, GLES20.GL_RGBA,
				bitmapWidth, bitmapHeight, 0, GLES20.GL_RGBA,
				GLES20.GL_UNSIGNED_BYTE, byteBuffer);

		// Note: Following code may cause an error to be reported in the
		// ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
		// Failed to generate texture mipmap levels (error=3)
		// No OpenGL error will be encountered (glGetError() will return
		// 0). If this happens, just squash the source image to be
		// square. It will look the same because of texture coordinates,
		// and mipmap generation will work.

		glGenerateMipmap(GL_TEXTURE_2D);

		// Recycle the bitmap, since its data has been loaded into
		// OpenGL.
		// TODO 임시로 막아놓음..
		// bitmap.recycle();

		// Unbind from the texture.
		glBindTexture(GL_TEXTURE_2D, 0);

		return textureObjectIds[0];
	}

	/**
	 * 알파정보 없는 텍스쳐를 불러온다.
	 *
	 * @param bitmap
	 * @return
	 */
	public static int loadBitmpTextureNoAlpha(Bitmap bitmap) {
		final int[] textureObjectIds = new int[1];
		glGenTextures(1, textureObjectIds, 0);

		if (textureObjectIds[0] == 0) {
			if (LoggerConfig.ON) {
				Log.w(TAG, "Could not generate a new OpenGL texture object.");
			}
			return 0;
		}

		if (bitmap == null) {
			glDeleteTextures(1, textureObjectIds, 0);
			return 0;
		}

		// bitmap.recycle();

		// Bind to the texture in OpenGL
		glBindTexture(GL_TEXTURE_2D, textureObjectIds[0]);

		// Set filtering: a default must be set, or the texture will be
		// black.
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
				GL_LINEAR_MIPMAP_LINEAR);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_S,
				GLES20.GL_REPEAT);

		GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D, GLES20.GL_TEXTURE_WRAP_T,
				GLES20.GL_REPEAT);

		// Load the bitmap into the bound texture.
		GLUtils.texImage2D(GL_TEXTURE_2D, 0, bitmap, 0);

		// Note: Following code may cause an error to be reported in the
		// ADB log as follows: E/IMGSRV(20095): :0: HardwareMipGen:
		// Failed to generate texture mipmap levels (error=3)
		// No OpenGL error will be encountered (glGetError() will return
		// 0). If this happens, just squash the source image to be
		// square. It will look the same because of texture coordinates,
		// and mipmap generation will work.

		glGenerateMipmap(GL_TEXTURE_2D);

		// Recycle the bitmap, since its data has been loaded into
		// OpenGL.
		// TODO 임시로 막아놓음..
		// bitmap.recycle();

		// Unbind from the texture.
		glBindTexture(GL_TEXTURE_2D, 0);

		return textureObjectIds[0];
	}

	/**
	 * 텍스트 텍스쳐
	 *
	 * @param text
	 * @return
	 */
	public static int loadTextBitmpTexture(Object o) {
		Bitmap bitmap = null;
		// null pointer exception 처리 해야하나?
		if(o instanceof Memo){
			Memo memo = (Memo)o;
			// 꼭 static 써야 하나?
			bitmap = MemoHelper.drawTextToBitmap(memo.getMemoTitle(), memo.getMemoContent());
		}else if (o instanceof Group){
			Group group = (Group)o;
			bitmap = Group.drawTextToBitmap(group.getGroupTitle());
		}

		return loadBitmpTexture(bitmap);
	}
}
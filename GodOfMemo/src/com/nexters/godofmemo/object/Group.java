package com.nexters.godofmemo.object;

import static android.opengl.GLES20.GL_TRIANGLE_FAN;
import static android.opengl.GLES20.glDrawArrays;
import static com.nexters.godofmemo.util.Constants.BYTES_PER_FLOAT;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.nexters.godofmemo.R;
import com.nexters.godofmemo.data.VertexArray;
import com.nexters.godofmemo.programs.ColorShaderProgram;
import com.nexters.godofmemo.programs.TextureShaderProgram;
import com.nexters.godofmemo.util.BitmapHelper;
import com.nexters.godofmemo.util.Font;
import com.nexters.godofmemo.util.TextureHelper;
import com.nexters.godofmemo.view.MemoGLView;

public class Group {
	private static final int POSITION_COMPONENT_COUNT = 2;
	private static final int TEXTURE_COORDINATES_COMPONENT_COUNT = 2;
	private static final int STRIDE = (POSITION_COMPONENT_COUNT + TEXTURE_COORDINATES_COMPONENT_COUNT)
			* BYTES_PER_FLOAT;

	private static float[] VERTEX_DATA;
	private VertexArray vertexArray;
	
	//텍스트 입력을 위한 정보
	private static float[] VERTEX_DATA_TEXT;
	private VertexArray vertexArrayText;
	
	//기본정보
	//그룹의 기본 정보 
	private String groupId;
	private int groupColor; //일단은 case를 나누는 용도. 단계를 나눌 필요가 없다면 삭제.
	private String groupTitle;
	private String groupSymbolId;
	private HashMap<String,Memo> groupMemoList; //memoId를 key로 Memo를 value로 정리.
	// Symbol 에 대한 논의도 필요. 
	private String groupDate;
	private String groupTime;	
	//색깔 
	public static final int GROUP_COLOR_RED=0;
	public static final int GROUP_COLOR_BLUE=1;
	public static final int GROUP_COLOR_YELLOW=2;
	
	//색상정보
	private float red;
	private float green;
	private float blue;
	
	//크기 기본값
	public static final float GROUP_DEFAULT_SIZE = 0.8f;
	//위치, 크기정보
	private float x;
	private float y;
	private float width;
	private float height;
	
	//TODO 최대 줄 개수는 임시값.
	private static final int maxLine= 3;	
	
	//생성시
	private long prodTime=0;
	
	//텍스쳐 정보
	public int texture;
	//글씨 텍스처
	public int textTexture;
	
	//텍스쳐 원본
	public int textureSource;
	
	//텍스트를 입력한 비트맵
	public Bitmap textBitmap;
	//비트맵 아이디
	public int textBitmapId;
	
	//텍스쳐 설정에 필요한 변수
	private Context context;
	private VertexArray vertexArrayColor;
	
	//텍스트가 들어갈 상자의 비율
	public static float ratioW = 8f / 10f;
	public static float ratioH = 6f / 10f;
	
	/**
	 * 위치와 크기를 지정한다
	 */
	public void setVertices() {
		//System.out.println("setVertices");

		VERTEX_DATA = new float[24];

		// 중심. 
		int s = 0;
		VERTEX_DATA[0] = x; // x
		VERTEX_DATA[1] = y; // y
		VERTEX_DATA[2] = 0.5f; // S
		VERTEX_DATA[3] = 0.5f; // T

		// 왼쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		// 오른쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 1f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		// 오른쪽 위에 
		s++;
		VERTEX_DATA[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 1f; // z
		VERTEX_DATA[s * 4 + 3] = 0f; // z

		// 왼쪽 위에
		s++;
		VERTEX_DATA[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 0f; // z

		// 왼쪽 아래
		s++;
		VERTEX_DATA[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA[s * 4 + 2] = 0f; // z
		VERTEX_DATA[s * 4 + 3] = 1f; // z

		vertexArray = new VertexArray(VERTEX_DATA);
		
		//글자저장을 위한 저장...
		setTextVertices();
	}
	
	private void setTextVertices(){
		//System.out.println("setTextVertices");
		VERTEX_DATA_TEXT = new float[24];
		
		float x = this.x;
		float y = this.y;
		float width = this.width;
		float height = this.height;

		width = width * ratioW;
		height = height * ratioH;

		// 중심. 
		int s = 0;
		VERTEX_DATA_TEXT[0] = x; // x
		VERTEX_DATA_TEXT[1] = y; // y
		VERTEX_DATA_TEXT[2] = 0.5f * ratioW;// S
		VERTEX_DATA_TEXT[3] = 0.5f * ratioH; // T

		// 왼쪽 아래
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 0f * ratioW; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 1f * ratioH; // z

		// 오른쪽 아래
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 1f * ratioW; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 1f * ratioH; // z

		// 오른쪽 위에 
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x + width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 1f * ratioW; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 0f * ratioH; // z

		// 왼쪽 위에
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y + height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 0f * ratioW; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 0f * ratioH; // z

		// 왼쪽 아래
		s++;
		VERTEX_DATA_TEXT[s * 4 + 0] = x - width / 2; // x
		VERTEX_DATA_TEXT[s * 4 + 1] = y - height / 2; // y
		VERTEX_DATA_TEXT[s * 4 + 2] = 0f * ratioW; // z
		VERTEX_DATA_TEXT[s * 4 + 3] = 1f * ratioH; // z

		vertexArrayText = new VertexArray(VERTEX_DATA_TEXT);
	}
	
	//색깔을 설정한다.
	public void setColor(int ri, int gi, int bi){
		//rgb 253, 245, 229
		//rgb 140, 211, 156
		red = ri/255.0f;
		green = gi/255.0f;
		blue = bi/255.0f;
		
	}
	
	public void setColorVertices(){
		
		float[] VERTEX_DATA_COLOR = new float[30];
		
		// Order of coordinates: X, Y, R, G, B

		// point 1
		int s = 0;
		VERTEX_DATA_COLOR[s * 5 + 0] = x; // x
		VERTEX_DATA_COLOR[s * 5 + 1] = y; // y
		VERTEX_DATA_COLOR[s * 5 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 5 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 5 + 4] = blue; // b

		// point 2
		s++;
		VERTEX_DATA_COLOR[s * 5 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 5 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 5 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 5 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 5 + 4] = blue; // b

		// point 3
		s++;
		VERTEX_DATA_COLOR[s * 5 + 0] = x + width / 2; // x
		VERTEX_DATA_COLOR[s * 5 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 5 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 5 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 5 + 4] = blue; // b

		// point 4
		s++;
		VERTEX_DATA_COLOR[s * 5 + 0] = x + width / 2; // x
		VERTEX_DATA_COLOR[s * 5 + 1] = y + height / 2; // y
		VERTEX_DATA_COLOR[s * 5 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 5 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 5 + 4] = blue; // b
		
		// point 5
		s++;
		VERTEX_DATA_COLOR[s * 5 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 5 + 1] = y + height / 2; // y
		VERTEX_DATA_COLOR[s * 5 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 5 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 5 + 4] = blue; // b
		
		// point 6
		s++;
		VERTEX_DATA_COLOR[s * 5 + 0] = x - width / 2; // x
		VERTEX_DATA_COLOR[s * 5 + 1] = y - height / 2; // y
		VERTEX_DATA_COLOR[s * 5 + 2] = red; // r
		VERTEX_DATA_COLOR[s * 5 + 3] = green; // g
		VERTEX_DATA_COLOR[s * 5 + 4] = blue; // b
		
		vertexArrayColor = new VertexArray(VERTEX_DATA_COLOR);
	}

	// 텍스쳐 설정
	public void setTexture() {
		if (textureSource != 0) {
			// 텍스쳐를 불러보고
			this.texture = TextureHelper.loadTexture(context, textureSource);
		} else if (textBitmap != null) {
			// 비트맵이 있으면 비트맵 텍스쳐를 입힌다.
			this.texture = TextureHelper.loadBitmpTexture(textBitmap, textBitmapId);
			this.textTexture = TextureHelper.loadTextBitmpTexture(this);
		}
	}
	
	/**
	 * 텍스트만 그리는 함수
	 * @param gContext
	 * @param gResId
	 * @param gText
	 * @return
	 */
	public static Bitmap drawTextToBitmap(String gText) {
		
		int width = 512;
		int height = 512;

		// Read in the resource
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		// Canvas
		Canvas canvas = new Canvas(bitmap);
		// new antialised Paint
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		// text color - #3D3D3D
		paint.setColor(Color.rgb(61, 61, 61));
		// text size in pixels
		int textSize = (int) (32);
		paint.setTextSize(textSize);
		// text shadow
		//paint.setShadowLayer(1f, 0f, 1f, Color.WHITE);
		
		//##########
		//텍스트 여러줄 처리
		//##########
		String dividedText = BitmapHelper.getDividedText(gText);
		
		//텍스트를 줄바꿈 단위로 쪼갠다.
		String[] dividedTextArray = dividedText.split("\n");

		// draw text to the Canvas center
		// TODO group에 적합하도록. 
		int x = (int) (width * ratioW / 2);
		int y = (int) (height * ratioH / 2);

		int loopCnt = 0;
		int textoffsetYY = 0;
		int marginY = 4;
		int marginX = 10;
		int offsetY = (textSize + marginY)/1;
		
		//몇번 포문을 수행할지 결정
		if(dividedTextArray.length < maxLine){
			loopCnt = dividedTextArray.length;
		}else{
			loopCnt = maxLine;
		}
		
		//시작 높이 위치 정하기
		textoffsetYY = y - (offsetY/2)*(loopCnt-1) + marginY;
		
		//폰트 설정
		paint.setTypeface(Font.getTf());
		
		//여러줄 출력하기
		for(int i=0; i<loopCnt; i++){
			String text = dividedTextArray[i];
			int px = x - (text.length() * textSize)/2 + marginX;
			int py = textoffsetYY + (i*offsetY);
			canvas.drawText(text, px, py, paint);
		}
		
		return bitmap;
	}


	/**
	 * 생성자
	 * 
	 * @param context
	 */
	public Group(Context context){
		this.context = context;
	}
	
	//신규입력시
	public Group(Context context, String text, int colorMarker, float groupSize, MemoGLView memoGLView ) {
		this.context = context;
		//input title and color
		setGroupTitle(text);
		setGroupColor(colorMarker);
		// TODO Need a selecting logic that finds appropriate color.
		
		//위치와 크기
		setWidth(groupSize);
		setHeight(groupSize);
		
		float tempX = (memoGLView.mr.width)/2; //폰의 보여지는 width 값 
		float tempY = (memoGLView.mr.height)/2;
		
		float nx = memoGLView.getNormalizedX(tempX);
		float ny = memoGLView.getNormalizedY(tempY);
		
		setX(nx);
		setY(ny);
		
		setVertices();
		
	}

	public void drawGroup(TextureShaderProgram textureProgram) {
		
		vertexArray.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArray.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoordinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
	
	public void drawTitle(TextureShaderProgram textureProgram) {
	//텍스트에 대한 처리...
		vertexArrayText.setVertexAttribPointer(0,
				textureProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, STRIDE);

		vertexArrayText.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				textureProgram.getTextureCoordinatesAttributeLocation(),
				TEXTURE_COORDINATES_COMPONENT_COUNT, STRIDE);
		
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
	
    private static final int COLOR_COMPONENT_COUNT = 3;
    private static final int COLOR_STRIDE = 
        (POSITION_COMPONENT_COUNT + COLOR_COMPONENT_COUNT) 
        * BYTES_PER_FLOAT;
	
	public void drawGroup(ColorShaderProgram colorProgram) {
		vertexArrayColor.setVertexAttribPointer(0,
				colorProgram.getPositionAttributeLocation(),
				POSITION_COMPONENT_COUNT, COLOR_STRIDE);

		vertexArrayColor.setVertexAttribPointer(POSITION_COMPONENT_COUNT,
				colorProgram.getColorAttributeLocation(),
				COLOR_COMPONENT_COUNT, COLOR_STRIDE);
		
		glDrawArrays(GL_TRIANGLE_FAN, 0, 6);
	}
	
	//##############
	// Getter, Setter
	//##############

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public int getGroupColor() {
		return groupColor;
	}

	public void setGroupColor(int groupColor) {
		this.groupColor = groupColor;
		
		switch(groupColor){
		case GROUP_COLOR_RED:
			//creating bitmap that containing a title of group.
			this.textBitmapId = R.drawable.circle_red;
			break;
		case GROUP_COLOR_BLUE:
			//creating bitmap that containing a title of group.
			this.textBitmapId = R.drawable.circle_blue;
			break;
		case GROUP_COLOR_YELLOW:
			//creating bitmap that containing a title of group.
			this.textBitmapId = R.drawable.circle_yellow;
			break;
		}
		
		this.textBitmap = BitmapHelper.drawBitmap(context, this.textBitmapId);
				
	}

	public String getGroupTitle() {
		return groupTitle;
	}

	public void setGroupTitle(String groupTitle) {
		if(groupTitle == null){
			groupTitle = "test";
		}
		this.groupTitle = groupTitle;
	}

	public String getGroupSymbolId() {
		return groupSymbolId;
	}

	public void setGroupSymbolId(String groupSymbolId) {
		this.groupSymbolId = groupSymbolId;
	}

	public HashMap<String, Memo> getGroupMemoList() {
		return groupMemoList;
	}

	public void setGroupMemoList(HashMap<String, Memo> groupMemoList) {
		this.groupMemoList = groupMemoList;
	}

	public String getGroupDate() {
		return groupDate;
	}

	public void setGroupDate(String groupDate) {
		this.groupDate = groupDate;
	}

	public String getGroupTime() {
		return groupTime;
	}

	public void setGroupTime(String groupTime) {
		this.groupTime = groupTime;
	}

	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getWidth() {
		return width;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public float getHeight() {
		return height;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public long getProdTime() {
		
		return prodTime;
	}

	public void setProdTime(long prodTime) {
		this.prodTime = prodTime;
	}

	public float getRed() {
		return red;
	}

	public void setRed(float red) {
		this.red = red;
	}

	public float getGreen() {
		return green;
	}

	public void setGreen(float green) {
		this.green = green;
	}

	public float getBlue() {
		return blue;
	}

	public void setBlue(float blue) {
		this.blue = blue;
	}
	
	
}
package com.nexters.godofmemo.object;


public abstract class MovableObject {

	// 위치, 크기정보
	protected float x;
	protected float y;
	protected float width;
	protected float height;

	// 색깔 동적으로.
	protected float red;
	protected float green;
	protected float blue;

	/**
	 * 색깔을 설정한다. int값을 받아서 float으로 저장함.
	 *
	 * @param ri
	 * @param gi
	 * @param bi
	 */
	public void setColor(int ri, int gi, int bi) {
		this.red = ri / 255.0f;
		this.green = gi / 255.0f;
		this.blue = bi / 255.0f;
	}

	public abstract void setVertices();

	// #############
	// Getter , Setter
	// #############

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

	// 메모 색깔...
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

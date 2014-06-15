package com.nexters.godofmemo.object;


public class Color {

	private String hex;
	private Integer r;
	private Integer g;
	private Integer b;

	public Color(String hex, Integer r, Integer g, Integer b) {
		this.hex = hex;
		this.r = r;
		this.g = g;
		this.b = b;
	}

	public String getHex() {
		return hex;
	}

	public void setHex(String hex) {
		this.hex = hex;
	}

	public Integer getR() {
		return r;
	}

	public void setR(Integer r) {
		this.r = r;
	}

	public Integer getG() {
		return g;
	}

	public void setG(Integer g) {
		this.g = g;
	}

	public Integer getB() {
		return b;
	}

	public void setB(Integer b) {
		this.b = b;
	}

	public static void main(String[] args) {
		//ColorDB.getInstance();
		System.out.println("test");
	}

}

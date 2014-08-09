package com.nexters.godofmemo.data;

import java.util.ArrayList;
import java.util.List;

import com.nexters.godofmemo.object.Color;

public class ColorDB {

	private List<Color> colorList;

	private static ColorDB colorDB = new ColorDB();

	private ColorDB() {
		colorList = new ArrayList<Color>();
		colorList.add(new Color("8cd39c", 140, 211, 156));
		colorList.add(new Color("f4ce6e", 244, 206, 110));
		colorList.add(new Color("75a684", 117, 166, 132));
		colorList.add(new Color("fa8ebb", 250, 142, 187));
		colorList.add(new Color("f2a17e", 242, 161, 126));
		colorList.add(new Color("ed7d7a", 237, 125, 122));
		colorList.add(new Color("008fd0", 0, 143, 208));
		colorList.add(new Color("3c7dce", 60, 125, 206));
		colorList.add(new Color("d09cd9", 208, 156, 217));
		colorList.add(new Color("bcc58d", 188, 197, 141));
		colorList.add(new Color("4ebda2", 78, 189, 162));
		colorList.add(new Color("aba290", 171, 162, 144));
	}

	public static ColorDB getInstance() {
		return colorDB;
	}

	public List<Color> getColorList() {
		return colorList;
	}

}

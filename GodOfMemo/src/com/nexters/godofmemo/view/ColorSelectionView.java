package com.nexters.godofmemo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.TypedValue;
import android.view.View;

import com.nexters.godofmemo.object.Color;

public class ColorSelectionView extends View {

	@SuppressWarnings("unused")
	private final Context context;
	private Color color;
	private boolean isSelected = false;
	private int type = 0; // memo=0, bg=1, group=2;

	public ColorSelectionView(Context context) {
		super(context);
		this.context = context;
	}

	public ColorSelectionView(Context context, Color c, String type) {
		super(context);
		this.context = context;

		this.color = c;
		if ("memo".equals(type)) {
			this.type = 0;
		} else if ("bg".equals(type)) {
			this.type = 1;
		} else if ("group".equals(type)) {
			this.type = 2;
		}

	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int parentHeight = MeasureSpec.getSize(heightMeasureSpec);
		// size
		int width = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, parentHeight/2, getResources()
						.getDisplayMetrics());
		int height = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, parentHeight/2, getResources()
						.getDisplayMetrics());
		setMeasuredDimension(width, height);

	}

	@SuppressLint("DrawAllocation")
	@Override
	protected void onDraw(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(getColorI());
		canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

		// border
		if (isSelected) {
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(15f);
			paint.setColor(android.graphics.Color.WHITE);
			canvas.drawRect(0, 0, getWidth(), getHeight(), paint);

		}
	}

	public int getColorI() {

		int c = android.graphics.Color.argb(255, color.getR(), color.getG(),
				color.getB());
		return c;
	}

	public int getColorBG() {

		int c = android.graphics.Color.argb(128, color.getR(), color.getG(),
				color.getB());
		return c;
	}

	@Override
	public boolean isSelected() {
		return isSelected;
	}

	@Override
	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public Color getColor() {
		return color;
	}

}

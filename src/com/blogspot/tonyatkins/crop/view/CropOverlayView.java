/**
 * Copyright 2013 Tony Atkins <duhrer@gmail.com>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are
 * permitted provided that the following conditions are met:
 *
 *    1. Redistributions of source code must retain the above copyright notice, this list of
 *       conditions and the following disclaimer.
 *
 *    2. Redistributions in binary form must reproduce the above copyright notice, this list
 *       of conditions and the following disclaimer in the documentation and/or other materials
 *       provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY Tony Atkins ''AS IS'' AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL Tony Atkins OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 */
package com.blogspot.tonyatkins.crop.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.view.View;

public class CropOverlayView extends View {
	
	public static final int MIN_WIDTH = 100;
	public static final int MIN_HEIGHT = 100; 
	
	int startx = 150;
	int starty = 150;
	int endx = 300;
	int endy = 300;

	Paint strokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint broadStrokePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	Paint semiTransparentPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public CropOverlayView(Context context) {
		super(context);
		init();
	}

	public CropOverlayView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public CropOverlayView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	public int getEndx() {
		return endx;
	}
	public int getEndy() {
		return endy;
	}
	
	public int getStartx() {
		return startx;
	}
	public int getStarty() {
		return starty;
	}
	private void init() {
		strokePaint.setColor(Color.WHITE);
		strokePaint.setStyle(Style.STROKE);
		
		broadStrokePaint.setColor(Color.BLACK);
		broadStrokePaint.setStrokeWidth(3);
		broadStrokePaint.setStyle(Style.STROKE);
		
		semiTransparentPaint.setColor(Color.BLACK);
		semiTransparentPaint.setStyle(Style.FILL);
		semiTransparentPaint.setAlpha(50);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// Draw the "dimmed" outline in four parts: (left, right, top, bottom)
		canvas.drawRect(0, 0, startx, getMeasuredHeight(), semiTransparentPaint); // left
		canvas.drawRect(endx, 0, getMeasuredWidth(), getMeasuredHeight(), semiTransparentPaint); // right
		canvas.drawRect(startx, 0, endx, starty, semiTransparentPaint); // top
		canvas.drawRect(startx, endy, endx, getMeasuredHeight(), semiTransparentPaint); // bottom
		
		canvas.drawRect(startx,starty,endx,endy,broadStrokePaint);
		canvas.drawRect(startx,starty,endx,endy,strokePaint);
	}
	
	public void setRect(float left, float top, float right, float bottom) {
		setRect(Math.round(left),Math.round(top),Math.round(right),Math.round(bottom));
	}

	public void setRect(int left, int top, int right, int bottom) {
		this.startx = left;
		this.starty=top;
		this.endx=right;
		this.endy=bottom;
		invalidate();
	}
	
	public void setEndx(int endx) {
		this.endx = endx;
		invalidate();
	}

	public void setEndy(int endy) {
		this.endy = endy;
		invalidate();
	}

	public void setStartx(int startx) {
		this.startx = startx;
		invalidate();
	}

	public void setStarty(int starty) {
		this.starty = starty;
		invalidate();
	}
}

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
package com.blogspot.tonyatkins.crop.listener;
import com.blogspot.tonyatkins.crop.Constants;
import com.blogspot.tonyatkins.crop.view.CropOverlayView;

import android.util.Log;
import android.view.MotionEvent;
import android.view.MotionEvent.PointerCoords;
import android.view.View;
import android.view.View.OnTouchListener;


public class MoveAndCropListener implements OnTouchListener {
	private boolean allowResizing = true;
	
	
	public MoveAndCropListener(boolean allowResizing) {
		super();
		this.allowResizing = allowResizing;
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (!(view instanceof CropOverlayView)) {
			Log.e(Constants.TAG, "Move and crop listener only works with CropOverlayView objects.");
			return false;
		}
		
		CropOverlayView overlayView = (CropOverlayView) view;
		int current_width = overlayView.getEndx() - overlayView.getStartx();
		int current_height = overlayView.getEndy() - overlayView.getStarty();
		
		if (event.getPointerCount() <= 1) {
			PointerCoords finger0Coords = new PointerCoords();
			event.getPointerCoords(0, finger0Coords);

			// Center the current viewport on the current location
			overlayView.setRect(finger0Coords.x - current_width/2, finger0Coords.y - current_height/2, finger0Coords.x + current_width/2, finger0Coords.y + current_height/2);
		}
		else if (event.getPointerCount() == 2 && allowResizing) {
			PointerCoords finger0Coords = new PointerCoords();
			event.getPointerCoords(0, finger0Coords);
			
			PointerCoords finger1Coords = new PointerCoords();
			event.getPointerCoords(1, finger1Coords);
			
			int newStartX = (int) Math.min(finger0Coords.x, finger1Coords.x);
			int newStartY = (int) Math.min(finger0Coords.y, finger1Coords.y);
			int newEndX = (int) Math.max(finger0Coords.x, finger1Coords.x);
			int newEndY = (int) Math.max(finger0Coords.y, finger1Coords.y);

			// Force minimum dimensions
			if ((newEndX - newStartX) < CropOverlayView.MIN_WIDTH) {
				Log.d(Constants.TAG, "Enforcing minimum width requirement for cropped images.");
				int midX = (newEndX+newStartX)/2;
				newStartX = midX - (CropOverlayView.MIN_WIDTH/2);
				newEndX   = midX + (CropOverlayView.MIN_WIDTH/2);
			}
			if ((newEndY - newStartY) < CropOverlayView.MIN_HEIGHT) {
				Log.d(Constants.TAG, "Enforcing minimum height requirement for cropped images.");
				int midY = (newEndY+newStartY)/2;
				newStartY = midY - (CropOverlayView.MIN_HEIGHT/2);
				newEndY   = midY + (CropOverlayView.MIN_HEIGHT/2);
			}
			
			// The overlay view should now cover a square of at least the minimum size centered on the average X and Y positions of both fingers
			overlayView.setRect(newStartX, newStartY, newEndX, newEndY);
		}
		
		return false;
	}

}

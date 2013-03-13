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
package com.blogspot.tonyatkins.crop.activity;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.blogspot.tonyatkins.crop.R;
import com.blogspot.tonyatkins.crop.listener.MoveAndCropListener;
import com.blogspot.tonyatkins.crop.view.CropOverlayView;

public class CropActivity extends Activity {
	public static final String KEY_OUTPUT_X = "outputX";
	public static final String KEY_OUTPUT_Y = "outputY";
	public static final String KEY_DATA = "data";

	private int outputX = 0;
	private int outputY = 0;
	private Bitmap bitmap;
	private CropOverlayView cropOverlayView;
	private FrameLayout frameLayout;
	private ImageView cropImageView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.crop);
		frameLayout = (FrameLayout) findViewById(R.id.cropImageFrame);
		cropOverlayView = (CropOverlayView) findViewById(R.id.cropOverlayView);
		cropImageView = (ImageView) findViewById(R.id.cropImageView);

		Bundle extras = getIntent().getExtras();

		if (extras != null)
		{

			/*
			 * TODO: Add support for the full range of parameters providing by
			 * the old gallery, including:
			 * 
			 * aspectX aspectY scale noFaceDetection output
			 */
			Uri uri = getIntent().getData();
			bitmap = BitmapFactory.decodeFile(uri.getPath());

			if (bitmap == null)
			{
				Toast.makeText(this, "Can't continue without image data to crop.", Toast.LENGTH_LONG).show();
				finish();
			}
			else
			{
				// If we exit the activity without saving, we should return the
				// original image.
				Intent intent = new Intent();
				intent.putExtra(KEY_DATA, bitmap);
				setResult(RESULT_CANCELED, intent);

				resizeBitmapAndWireToView();

				// If we have size guidelines, set the dimensions and don't
				// allow resizing
				boolean allowResizing = true;
				outputX = extras.getInt(KEY_OUTPUT_X, outputX);
				outputY = extras.getInt(KEY_OUTPUT_Y, outputY);
				if (outputX != 0 || outputY != 0)
				{
					int startX = cropOverlayView.getStartx();
					int startY = cropOverlayView.getStarty();

					cropOverlayView.setRect(startX, startX, startX + Math.min(outputX, CropOverlayView.MIN_WIDTH), startY + Math.min(outputY, CropOverlayView.MIN_HEIGHT));
					allowResizing = false;
				}

				cropOverlayView.setOnTouchListener(new MoveAndCropListener(allowResizing));

				// Wire up the "save" button
				Button saveButton = (Button) findViewById(R.id.cropSaveButton);
				saveButton.setOnClickListener(new SaveClickListener());

				// Wire up the "cancel" button
				Button cancelButton = (Button) findViewById(R.id.cropCancelButton);
				cancelButton.setOnClickListener(new CancelClickListener());
			}

		}
		else
		{
			Toast.makeText(this, "Can't continue without image data to crop.", Toast.LENGTH_LONG).show();
			finish();
		}
	}

	private void resizeBitmapAndWireToView() {
		cropImageView.setImageBitmap(bitmap);
	}

	private class CancelClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			finish();
		}
	}

	private class SaveClickListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			Bitmap croppedBitmap = getCroppedBitmap();
			Intent intent = new Intent();
			intent.putExtra(KEY_DATA, croppedBitmap);
			setResult(RESULT_OK, intent);
			finish();
		}

	}

	private Bitmap getCroppedBitmap() {
		int startx = cropOverlayView.getStartx();
		int starty = cropOverlayView.getStarty();
		int width = cropOverlayView.getEndx() - startx;
		int height = cropOverlayView.getEndy() - starty;

		return Bitmap.createBitmap(bitmap, startx, starty, width, height);
	}
}

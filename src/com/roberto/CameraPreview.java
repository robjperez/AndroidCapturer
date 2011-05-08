package com.roberto;

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

//package com.example.android.apis.graphics;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
//import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.List;

// Need the following import to get access to the app resources, since this
// class is in a sub-package.
//import com.example.android.apis.R;

// ----------------------------------------------------------------------

public class CameraPreview extends Activity {
    private SurfaceView sview = null;
    private SurfaceHolder sviewHolder = null;
    Camera mCamera;  
    private boolean inPreview = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);        
        // Hide the window title.
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.preview);
        
        sview = (SurfaceView) findViewById(R.id.surfaceView1);
        sviewHolder = sview.getHolder();
        sviewHolder.addCallback(surfaceCallback);
        sviewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        sview.setOnClickListener(surfaceClick); 
    }

	@Override
	public void onResume() {
		super.onResume();

		mCamera = Camera.open();
		Camera.Parameters parameters = mCamera.getParameters();
		parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
		mCamera.setParameters(parameters);
	}

	@Override
	public void onPause() {
		if (inPreview) {
			mCamera.stopPreview();
		}

		mCamera.release();
		mCamera = null;
		inPreview = false;

		super.onPause();
	}
    
	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}
		return (result);
	}
	
	View.OnClickListener surfaceClick = new View.OnClickListener() {		
		public void onClick(View v) {
			mCamera.takePicture(null, null, new PictureCallback() {				
				public void onPictureTaken(byte[] data, Camera camera) {
					CapturedPhotoHolder.sharedInstance().setJPEGData(data);
					Intent i = new Intent(sview.getContext(), Main.class);
					startActivityForResult(i, 0);
				}
			});
		}
	};
    
	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				mCamera.setPreviewDisplay(sviewHolder);
			} catch (Throwable t) {
				Log.e("PreviewDemo-surfaceCallback",
						"Exception in setPreviewDisplay()", t);				
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = mCamera.getParameters();
			Camera.Size size = getBestPreviewSize(width, height, parameters);						

			if (size != null) {
				/*Display display = ((WindowManager) getSystemService(WINDOW_SERVICE)).getDefaultDisplay();				
				mCamera.setDisplayOrientation(display.getRotation());*/
				parameters.setPreviewSize(size.width, size.height);
				parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);				
				mCamera.setParameters(parameters);
				mCamera.startPreview();
				mCamera.autoFocus(null);
				inPreview = true;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// no-op			
		}
	};
}
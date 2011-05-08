package com.roberto;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class Main extends Activity {
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        Button b = (Button) findViewById(R.id.Button01);
        b.setOnClickListener(new OnClickListener() {			
			public void onClick(View arg0) {
				openCamera(arg0);				
			}
		});
        
        CapturedPhotoHolder ph = CapturedPhotoHolder.sharedInstance();
        if ( ph.hasData()) {
        	ImageView im = (ImageView) findViewById(R.id.imageView);
        	BitmapFactory.Options options = new BitmapFactory.Options();
        	  options.inSampleSize = 2;
        	  byte[] jpegData = ph.getJPEGData();
        	  Bitmap bm = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.length);
        	  im.setImageBitmap(bm); 
        }
    }
    
    public void openCamera(View view) {
    	Intent in = new Intent(view.getContext(), CameraPreview.class);
    	startActivityForResult(in, 0);
    }
}
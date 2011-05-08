package com.roberto;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteOrder;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.googlecode.leptonica.android.Pix;
import com.googlecode.leptonica.android.ReadFile;
import com.googlecode.tesseract.android.TessBaseAPI;

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
		if (ph.hasData()) {
			ImageView im = (ImageView) findViewById(R.id.imageView);
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 2;
			byte[] jpegData = ph.getJPEGData();

			Bitmap bm = BitmapFactory.decodeByteArray(jpegData, 0,
					jpegData.length);
			im.setImageBitmap(bm);
			FileOutputStream fos = null;
			File f = null;
			try {
				f = new File("/mnt/sdcard/captura.jpg");
				f.createNewFile();
				fos = new FileOutputStream(f);
				bm.compress(CompressFormat.JPEG, 100, fos);
				fos.flush();
				fos.close();				
			} catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
					
			TessBaseAPI t = new TessBaseAPI();
			try {

				t.init("/mnt/sdcard/tesseract/", "eng");
				
				t.setImage(f);
				// t.setImage(bm);
				String decodedString = t.getUTF8Text();
				if (decodedString != null)
					Log.d("Converter", decodedString);
				else
					Log.d("Converter", "No String decoded");
				
				TextView tv = (TextView) findViewById(R.id.textView);
				tv.setText("Text: " + decodedString);

			} catch (Exception e) {
				Log.d("Converter", "Failed grabbing text", e);
			} finally {
				t.end();
			}
			f.delete();
			ph.clearData();
		}

	}

	public void openCamera(View view) {
		Intent in = new Intent(view.getContext(), CameraPreview.class);
		startActivityForResult(in, 0);
	}
}
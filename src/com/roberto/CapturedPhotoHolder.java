package com.roberto;

public class CapturedPhotoHolder {
	private byte[] _photoData;
	private static CapturedPhotoHolder _instance = null;
	private boolean _hasData = false;
	
	public CapturedPhotoHolder() {
		
	}
	
	public static CapturedPhotoHolder sharedInstance() {
		if ( _instance == null)
			_instance = new CapturedPhotoHolder();
		
		return _instance;
	}
	
	public void setJPEGData(byte[] data) {
		this._photoData = data;
		_hasData = true;
	}
	
	public byte[] getJPEGData() {
		return this._photoData;
	}
	
	public boolean hasData() {
		return _hasData;
	}
}

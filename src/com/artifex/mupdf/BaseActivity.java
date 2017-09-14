package com.artifex.mupdf;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

public class BaseActivity extends Activity{
	public Handler handler = new myHandler();

	protected void dispatchMsgOP(Message msg) {
	}
	
	class myHandler extends Handler{
		@Override
		public void dispatchMessage(Message msg) {
			super.dispatchMessage(msg);
			dispatchMsgOP(msg);
		}
	}
}

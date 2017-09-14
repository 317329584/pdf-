package com.artifex.mupdf;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class TestMuPDFActivity extends BaseActivity {
	MuPDFCore core;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main);
		final LinearLayout mainLy = (LinearLayout) findViewById(R.id.mainLy);

		try {
			core = new MuPDFCore(Environment.getExternalStorageDirectory()
					+ "/" + "/MyMobileDownlod/" + "test.pdf");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (null != core) {
			final int position = 0;
			final MyMuPDFPageView pageView = new MyMuPDFPageView(
					TestMuPDFActivity.this, core, new Point(1280, 800));
			AsyncTask<Void, Void, PointF> sizingTask = new AsyncTask<Void, Void, PointF>() {
				@Override
				protected PointF doInBackground(Void... arg0) {
					return core.getPageSize(position);
				}

				@Override
				protected void onPostExecute(PointF result) {
					super.onPostExecute(result);
					pageView.drawOncePage(position, result);
					// mainLy.addView(pageView);
					mainLy.addView(
							new StephenPicScaleView(TestMuPDFActivity.this,
									pageView.getmEntireBm(), getWindowManager()
											.getDefaultDisplay().getWidth(),
									getWindowManager().getDefaultDisplay()
											.getHeight()), 0,
							new LinearLayout.LayoutParams(
									LinearLayout.LayoutParams.FILL_PARENT,
									LinearLayout.LayoutParams.MATCH_PARENT));
				}
			};
			sizingTask.execute((Void) null);
		}
	}

	@Override
	protected void dispatchMsgOP(Message msg) {
		super.dispatchMsgOP(msg);
		if (1 == msg.what) {
			Toast.makeText(TestMuPDFActivity.this, "loading",
					Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(TestMuPDFActivity.this, "load compleate",
					Toast.LENGTH_SHORT).show();
		}
	}
}

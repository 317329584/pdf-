package com.artifex.mupdf;

import android.graphics.Point;
import android.graphics.PointF;
import android.os.AsyncTask;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyMuPDFPageAdapter extends BaseAdapter {
	private final BaseActivity mContext;
	private final MuPDFCore mCore;
	public final SparseArray<PointF> mPageSizes = new SparseArray<PointF>();

	private int pagewidth = 1280;
	private int pageheight = 1740;

	public MyMuPDFPageAdapter(BaseActivity c, MuPDFCore core, int pagewidth,
			int pageheight) {
		mContext = c;
		mCore = core;
		this.pageheight = pageheight;
		this.pagewidth = pagewidth;
	}

	public MyMuPDFPageAdapter(BaseActivity c, MuPDFCore core) {
		mContext = c;
		mCore = core;
	}

	public int getCount() {
		int size = mCore.countPages();
		return size;
	}

	public Object getItem(int position) {
		return position;
	}

	public long getItemId(int position) {
		return position;
	}

	public View getView(final int position, View convertView, ViewGroup parent) {
		final MyMuPDFPageView pageView;
		if (convertView == null) {
			pageView = new MyMuPDFPageView(mContext, mCore, new Point(
					pagewidth, pageheight));
			pageView.DefalueHeight= pageheight;
		} else {
			pageView = (MyMuPDFPageView) convertView;
		}
		PointF pageSize = mPageSizes.get(position);// pageSize是原图大小
		if (null != pageSize) {
			pageView.drawOncePage(position, pageSize);
		} else {
			pageView.initReset(position);
			AsyncTask<Void, Void, PointF> sizingTask = new AsyncTask<Void, Void, PointF>() {
				@Override
				protected PointF doInBackground(Void... arg0) {
					return mCore.getPageSize(position);
				}

				@Override
				protected void onPostExecute(PointF result) {
					super.onPostExecute(result);
					mPageSizes.put(position, result);
					if (pageView.getCurPageNum() == position) {
						pageView.drawOncePage(position, result);
					}
				}
			};
			sizingTask.execute((Void) null);
		}
		return pageView;
	}
}

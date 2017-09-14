package com.artifex.mupdf;

import java.io.File;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class MuPDFActivity extends BaseActivity {
	private MuPDFCore core;
	private String mFileName;
	private ListView mDocListView;
	private View mButtonsView;
	private boolean mButtonsVisible;
	private EditText mPasswordView;
	private TextView mFilenameView;
	private SeekBar mPageSlider;
	private TextView mPageNumberView;
	private ViewSwitcher mTopBarSwitcher;

	private ProgressBar loadingPB;

	private MyMuPDFPageAdapter pdfPageAdapter;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		if (core == null) {
			File sdcardDir = Environment.getExternalStorageDirectory();
			// 得到一个路径，内容是sdcard的文件夹路径和名字
			String path = sdcardDir.getPath() + "/MyMobileDownlod/test.pdf";

			int lastSlashPos = path.lastIndexOf('/');
			mFileName = new String(lastSlashPos == -1 ? path
					: path.substring(lastSlashPos + 1));
			System.out.println("Trying to open " + path);
			try {
				core = new MuPDFCore(path);
			} catch (Exception e) {
			}

			if (core != null && core.needsPassword()) {
				return;
			}
		}
		if (core == null) {
			AlertDialog alert = new AlertDialog.Builder(this).create();
			alert.setTitle(R.string.open_failed);
			alert.setButton(AlertDialog.BUTTON_POSITIVE, "Dismiss",
					new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							finish();
						}
					});
			alert.show();
			return;
		}

		createUI(savedInstanceState);
	}


	public void createUI(Bundle savedInstanceState) {
		mDocListView = new ListView(this);
		pdfPageAdapter = new MyMuPDFPageAdapter(this, core);
		mDocListView.setAdapter(pdfPageAdapter);

		mButtonsView = getLayoutInflater().inflate(R.layout.buttons, null);
		mFilenameView = (TextView) mButtonsView.findViewById(R.id.docNameText);
		loadingPB = (ProgressBar) mButtonsView.findViewById(R.id.loadingPB);
		mTopBarSwitcher = (ViewSwitcher) mButtonsView
				.findViewById(R.id.switcher);
		mTopBarSwitcher.setDisplayedChild(0);
		mTopBarSwitcher.setVisibility(View.VISIBLE);
		loadingPB.setVisibility(View.INVISIBLE);

		mFilenameView.setText("Name:" + mFileName
				+ String.format("    SumPage:%d", core.countPages()));

		RelativeLayout layout = new RelativeLayout(this);
		layout.addView(mDocListView);
		layout.addView(mButtonsView);
		layout.setBackgroundResource(R.drawable.tiled_background);
		setContentView(layout);
	}

	public void onDestroy() {
		if (core != null)
			core.onDestroy();
		core = null;
		super.onDestroy();
	}

	@Override
	protected void dispatchMsgOP(Message msg) {
		super.dispatchMsgOP(msg);
		if (1 == msg.what) {
			Toast.makeText(MuPDFActivity.this, "loading", Toast.LENGTH_SHORT)
					.show();
			loadingPB.setVisibility(View.VISIBLE);
		} else {
			loadingPB.setVisibility(View.INVISIBLE);
		}
	}
}

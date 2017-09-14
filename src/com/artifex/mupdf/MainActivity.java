package com.artifex.mupdf;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {
	private Button btn_select, test_stephenPicScalView;
	private Handler handler;
	private ProgressDialog dialog;
	// urlpath是网络的PDF地址，也可以是服务器端的PDF地址
	private String urlpath = "http://www.adobe.com/content/dam/Adobe/en/devnet/acrobat/pdfs/pdf_open_parameters.pdf";

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);// 去掉标题栏
		setContentView(R.layout.layout_main);
		dialog = new ProgressDialog(this);
		btn_select = (Button) findViewById(R.id.btn_select);
		test_stephenPicScalView = (Button) findViewById(R.id.test_stephenPicScalView);
		test_stephenPicScalView.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				startActivity(new Intent(MainActivity.this,
						TestMuPDFActivity.class));
			}
		});
		btn_select.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				dialog.setTitle("正在联网下载数据...");
				dialog.setMessage("请稍后...");
				// 设置进度条风格，风格为长形
				dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

				// 设置ProgressDialog 标题
				dialog.setTitle("提示");

				// 设置ProgressDialog 提示信息
				dialog.setMessage("正在下载数据，请稍等....");

				// 设置ProgressDialog 进度条进度
				dialog.setProgress(0);

				// 设置ProgressDialog 的进度条是否不明确
				dialog.setIndeterminate(false);

				// 设置ProgressDialog 是否可以按退回按键取消
				dialog.setCancelable(true);
				dialog.show();

				handler = new Handler() {
					public void handleMessage(android.os.Message msg) {
						dialog.cancel();
						dialog.setProgress(0);
					}
				};
				// Thread thread = new Thread(new loadDateThreah());
				loadDataThreah ldt = new loadDataThreah();
				ldt.start();
			}
		});

	}

	// 进度条线程
	class loadDataThreah extends Thread {
		public void run() {
			try {
				showPDF(urlpath);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			handler.sendEmptyMessage(0);
		}
	}

	// 从服务器下载PDF并且跳转到MUPDF的ACTIVITY
	public void showPDF(String urlpath) throws Exception {
		URL u = new URL(urlpath);
		String path = createDir("test.pdf");
		byte[] buffer = new byte[1024 * 8];
		int read;
		int ava = 0;
		long start = System.currentTimeMillis();
		BufferedInputStream bin;
		try {
			HttpURLConnection urlcon = (HttpURLConnection) u.openConnection();
			double fileLength = (double) urlcon.getContentLength();
			bin = new BufferedInputStream(u.openStream());
			BufferedOutputStream bout = new BufferedOutputStream(
					new FileOutputStream(path));
			while ((read = bin.read(buffer)) > -1) {
				bout.write(buffer, 0, read);
				ava += read;
				int a = (int) Math.floor((ava / fileLength * 100));
				dialog.setProgress(a);
				long speed = ava / (System.currentTimeMillis() - start);
				System.out.println("Download: " + ava + " byte(s)"
						+ "    avg speed: " + speed + "  (kb/s)");
			}
			bout.flush();
			bout.close();
			Uri uri = Uri.parse(path);
			Intent intent = new Intent(MainActivity.this, MuPDFActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			intent.setData(uri);
			startActivity(intent);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private String createDir(String filename) {
		File sdcardDir = Environment.getExternalStorageDirectory();
		// 得到一个路径，内容是sdcard的文件夹路径和名字
		String path = sdcardDir.getPath() + "/MyMobileDownlod";
		File path1 = new File(path);
		if (!path1.exists())
			// 若不存在，创建目录，可以在应用启动的时候创建
			path1.mkdirs();
		path = path + "/" + filename;
		return path;
	}

}

package com.zhy.graph.utils;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import com.zhy.graph.app.AppContext;
import com.zhy.graph.app.AppManager;
import com.zhy.graph.widget.RoundProgressBar;

import com.zhy.graph.bean.VersionInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import gra.zhy.com.graph.R;

public class UpdateManager {
	private Context context;
	// 发起版本检测的url
	private final static String CHECKURL = "http://....";
	// 服务器返回的apk安装包下载路径
	private static String downloadUrl = "";
	private Dialog noticeDialog;
	private RoundProgressBar mProgress;
	private boolean interceptFlag = false;
	private Dialog downloadDialog;
	private static final int DOWN_UPDATE = 1;
	private static final int DOWN_OVER = 2;
	private int progress;
	VersionInfo info;
	// 下载包安装本地存放路径
	private static final String savePath = Environment
			.getExternalStorageDirectory() + "/bianbiangou/";
	private static final String saveFileName = savePath + "bianbiangou.apk";

	public UpdateManager(Context context) {
		this.context = context;
	}

	/**
	 * 更新UI的Handler
	 */
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case DOWN_UPDATE:
				mProgress.setProgress(progress);
				break;
			case DOWN_OVER:
				if (downloadDialog != null)
					downloadDialog.dismiss();
				installApk();
				break;
			default:
				break;
			}
		};
	};

	public int checkVersion(VersionInfo info) {
		try {
			this.info = info;
			String currentVersion = getVersionName(context);
			Log.e("getVersionName", currentVersion);
			int oldOne = Integer.parseInt(currentVersion.split("\\.")[0]);
			int oldTwo = Integer.parseInt(currentVersion.split("\\.")[1]);
			int oldThree = Integer.parseInt(currentVersion.split("\\.")[2]);
			int oldFour = Integer.parseInt(currentVersion.split("\\.")[3]);

			String nowVersion = info.getClient();
			int nowOne = Integer.parseInt(nowVersion.split("\\.")[0]);
			int nowTwo = Integer.parseInt(nowVersion.split("\\.")[1]);
			int nowThree = Integer.parseInt(nowVersion.split("\\.")[2]);
			int nowFour = Integer.parseInt(nowVersion.split("\\.")[3]);
			// 版本号规定，第一个、第二个数字，为强制更新，第三个、第四个数字为可选更新（非强制）
			if ((nowOne * 10 + nowTwo) > (oldOne * 10 + oldTwo)) {
				return 1;
			} else if (nowThree > oldThree
					|| (nowThree == oldThree && nowFour > oldFour)) {
				return 2;
			} else {
				return 3;
			}

			/*
			 * if(nowOne>oldOne){ // showNoticeDialog(true,handlers); return 1;
			 * }else if(nowTwo > oldTwo){ // showNoticeDialog(true,handlers);
			 * return 1; }else if(nowThree > oldThree){ //
			 * showNoticeDialog(false,handlers); return 2; }else if(nowFour >
			 * oldFour){ // showNoticeDialog(false,handlers); return 2; }else{
			 * // handlers.sendEmptyMessage(111); return 3; }
			 */
			// currentVersion = currentVersion.replace(".", "");
			// int oldVersion = Integer.parseInt(currentVersion);
			// int minVersion = Integer.parseInt(info.getMin().replace(".",
			// ""));
			// int maxVersion = Integer.parseInt(info.getMax().replace(".",
			// ""));
			// if (oldVersion < minVersion) {
			// showNoticeDialog(true,handlers);
			// return true;
			// } else if (oldVersion > minVersion && oldVersion < maxVersion) {
			// showNoticeDialog(false,handlers);
			// return true;
			// } else{
			// handlers.sendEmptyMessage(111);
			// }
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 3;
	}

	/**
	 * 获取本地版本名称
	 * 
	 * @param context
	 * @return
	 */
	private String getVersionName(Context context) {
		String versionName = "";
		try {
			// 包名改为自己应用的包名即可
			versionName = context.getPackageManager().getPackageInfo(
					"com.xjtu.enet", 0).versionName;
			AppContext.VERSION = versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return versionName;
	}

	/**
	 * 弹出更新提示对话框
	 */
	private void showNoticeDialog(final Boolean flag, final Handler handler) {
		Builder builder = new Builder(context);

		builder.setTitle("更新提示！");
		if (flag) {
			builder.setMessage("您的版本过低，请下载更新！");
		} else {
			builder.setMessage("您有可更新的版本！");
		}

		builder.setPositiveButton("下载", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				showDownloadDialog();
			}
		});
		builder.setNegativeButton("以后再说", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				if (flag) {
					System.exit(0);
				} else {
					handler.sendEmptyMessage(111);
				}
			}
		});
		noticeDialog = builder.create();
		noticeDialog.show();

	}

	/**
	 * 下载进度对话框
	 */
	public void showDownloadDialog() {
		Builder builder = new Builder(context);
		builder.setTitle("正在下载，请稍后...");
		final LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.progress, null);
		mProgress = (RoundProgressBar) v.findViewById(R.id.progress);
		builder.setView(v);
		builder.setNegativeButton("取消", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
				interceptFlag = true;
				AppManager.getAppManager().AppExit(context);
				// System.exit(0);
			}
		});
		downloadDialog = builder.create();
		downloadDialog.getWindow().setType(
				WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		downloadDialog.setCancelable(false);
		downloadDialog.show();
		downloadApk();
	}

	/**
	 * 启动线程下载apk
	 */
	private void downloadApk() {
		Thread downLoadThread = new Thread(downApkRunnable);
		downLoadThread.start();
	}

	/**
	 * 安装包下载线程
	 */
	private Runnable downApkRunnable = new Runnable() {
		@Override
		public void run() {
			try {
				URL url = new URL(MyProperUtil.getProperties(context,
						"appConfigDebugHost.properties").getProperty("Host")
						+ "version/"+info.getUrl());
				HttpURLConnection conn = (HttpURLConnection) url
						.openConnection();
				conn.connect();
				int length = conn.getContentLength();
				InputStream is = conn.getInputStream();
				File file = new File(savePath);
				if (!file.exists()) {
					file.mkdirs();
				}
				String apkFile = saveFileName;
				File ApkFile = new File(apkFile);
				FileOutputStream fos = new FileOutputStream(ApkFile);

				int count = 0;
				byte buf[] = new byte[1024];
				do {
					int numread = is.read(buf);
					count += numread;
					progress = (int) (((float) count / length) * 100);
					// 更新进度
					handler.sendEmptyMessage(DOWN_UPDATE);
					if (numread <= 0) {
						// 下载完成通 知安装
						handler.sendEmptyMessage(DOWN_OVER);
						break;
					}
					fos.write(buf, 0, numread);
				} while (!interceptFlag);// 点击取消就停止下载.
				fos.close();
				is.close();
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	};

	/**
	 * 安装APK
	 */
	private void installApk() {
		File apkfile = new File(saveFileName);
		if (!apkfile.exists()) {
			return;
		}
		Intent i = new Intent(Intent.ACTION_VIEW);
		i.setDataAndType(Uri.parse("file://" + apkfile.toString()),
				"application/vnd.android.package-archive");
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		context.startActivity(i);
	}
}
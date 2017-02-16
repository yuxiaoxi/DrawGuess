package com.zhy.graph.utils;

/*  * 文 件 名:  DataCleanManager.java  * 描    述:  主要功能有清除内/外缓存，清除数据库，清除sharedPreference，清除files和清除自定义目录  */

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

/** * 本应用数据清除管理器 */
public class DataCleanManager {
	
	static long sizeFile =0;
	
	/** * 清除本应用内部缓存(/data/data/com.xxx.xxx/cache) * * @param context */
	public static void cleanInternalCache(Context context) {
		deleteFilesByDirectory(context.getCacheDir());
	}

	public static long sizeInternalCache(Context context) {
		return sizeFilesByDirectory(context.getCacheDir());
	}

	/** * 清除本应用所有数据库(/data/data/com.xxx.xxx/databases) * * @param context */
	public static void cleanDatabases(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/databases"));
	}

	/**
	 * * 清除本应用SharedPreference(/data/data/com.xxx.xxx/shared_prefs) * * @param
	 * context
	 */
	public static void cleanSharedPreference(Context context) {
		deleteFilesByDirectory(new File("/data/data/"
				+ context.getPackageName() + "/shared_prefs"));
	}

	/** * 按名字清除本应用数据库 * * @param context * @param dbName */
	public static void cleanDatabaseByName(Context context, String dbName) {
		context.deleteDatabase(dbName);
	}

	/** * 清除/data/data/com.xxx.xxx/files下的内容 * * @param context */
	public static void cleanFiles(Context context) {
		deleteFilesByDirectory(context.getFilesDir());
	}

	public static long sizeFiles(Context context) {
		return sizeFilesByDirectory(context.getFilesDir());
	}

	/**
	 * * 清除外部cache下的内容(/mnt/sdcard/android/data/com.xxx.xxx/cache) * * @param
	 * context
	 */
	public static void cleanExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			deleteFilesByDirectory(context.getExternalCacheDir());
		}
	}

	public static long sizeExternalCache(Context context) {
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			return sizeFilesByDirectory(context.getExternalCacheDir());
		}
		return 0;
	}

	/** * 清除自定义路径下的文件，使用需小心，请不要误删。而且只支持目录下的文件删除 * * @param filePath */
	public static void cleanCustomCache(String filePath) {
		// deleteFilesByDirectory(new File(filePath));
		Log.e("MJJ", filePath + "                =====>>>>>");
		deleteFilesByFile(new File(filePath));
	}

	public static long sizeCustomCache(String filePath) {
		 long size = getSizes(new File(filePath));
		 Log.e("MJJ", "sizeCustomCache  ===>"+size);
		 return size;
	}

	/** * 清除本应用所有的数据 * * @param context * @param filepath */
	public static void cleanApplicationData(Context context, String... filepath) {
		cleanInternalCache(context);
		cleanExternalCache(context);
		cleanDatabases(context);
		// cleanSharedPreference(context);
		cleanFiles(context);
		for (String filePath : filepath) {
			cleanCustomCache(filePath);
		}
	}

	private static void deleteFilesByFile(File directory) {
		if (directory != null && directory.exists()) {
			if (directory.isDirectory()) {
				for (File items : directory.listFiles()) {
					deleteFilesByFile(items);
				}
			} else {
				directory.delete();
			}
		}
	}

	/** * 删除方法 这里只会删除某个文件夹下的文件，如果传入的directory是个文件，将不做处理 * * @param directory */
	private static void deleteFilesByDirectory(File directory) {
		if (directory != null && directory.exists() && directory.isDirectory()) {
			for (File item : directory.listFiles()) {
				item.delete();
			}
		}
	}

	public static String totalCache(Context context, String... filepath) {
		long totlaSize = 0;
		totlaSize += sizeInternalCache(context);
		totlaSize += sizeExternalCache(context);
		totlaSize += sizeFiles(context);
		for (String filePath : filepath) {
			totlaSize =totlaSize+sizeCustomCache(filePath);
		}
		return FormetFileSize(totlaSize);
	}

	private static long sizeFilesByDirectory(File directory) {
		if (directory != null && directory.exists()) {
			long size = directory.length() / 1024 / 1024;
			Log.e("MJJ", size + "  size      ===================>>>>");
			return size;
		}
		return 0;
	}

	public static long getSizes(File directory) {
		sizeFile = 0;
		if (directory != null && directory.exists()) {
			ArrayList<File> filelist = new ArrayList<File>();
			for (File file : directory.listFiles()) {
				if (file.isDirectory()) {
					/*
					 * 递归调用
					 */
					getSizes(file);
				} else {
					filelist.add(file);
				}
			}
			if(filelist.size()>0){
				for(int i=0;i<filelist.size();i++){
					sizeFile += getFileSizes(filelist.get(i));
				}
			}
		}
		return sizeFile;
	}

	public static String FormetFileSize(long fileS) {// 转换文件大小
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		if(fileS == 0){
			fileSizeString ="0.00B";
		}else if (fileS < 1024) {
			fileSizeString = df.format((double) fileS) + "B";
		} else if (fileS < 1048576) {
			fileSizeString = df.format((double) fileS / 1024) + "K";
		} else if (fileS < 1073741824) {
			fileSizeString = df.format((double) fileS / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileS / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static long getFileSizes(File f) {
		long s = 0;
		if (f.exists()) {
			FileInputStream fis = null;
			try {
				fis = new FileInputStream(f);
			} catch (FileNotFoundException e) {
				Log.e("MJJ", s
						+ "FileNotFoundException ===================>>>>");
				e.printStackTrace();
			}
			try {
				s = fis.available();
				Log.e("MJJ", s + "  size      ===================>>>>");
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			try {
				f.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("文件不存在");
		}
		return s;
	}

}
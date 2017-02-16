package com.zhy.graph.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

/**
 * 
 * @ClassName: PicturesUtil
 * @Description: 图片处理工具
 * @author 余卓
 * @date 2014年10月21日 下午2:37:35
 * 
 */
public class PicturesUtil {
	// Bitmap base64转字符上传
	public String picToStr(List<Bitmap> bitMaps) {
		StringBuffer upLoadImage = new StringBuffer();
		for (int i = 0; i < bitMaps.size(); i++) {
			String imageurl = "";
			Bitmap bitmap = bitMaps.get(i);
			if (bitmap != null) {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				// 将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
				try {
					baos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				byte[] buffer = baos.toByteArray();
				// 将图片的字节流数据加密成base64字符输出
				imageurl = Base64.encodeToString(buffer, 0, buffer.length,
						Base64.NO_WRAP);
				upLoadImage.append(imageurl);
				upLoadImage.append("αβγ");
			}
		}
		return upLoadImage.substring(0, upLoadImage.length() - 3);
	}

	public static String bitmapToStr(Bitmap bitmap) {
		String imageurlStr = null;
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			// 将bitmap一字节流输出 Bitmap.CompressFormat.PNG 压缩格式，100：压缩率，baos：字节流
			bitmap.compress(Bitmap.CompressFormat.JPEG, 40, baos);
			try {
				baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

			byte[] buffer = baos.toByteArray();
			// 将图片的字节流数据加密成base64字符输出
			imageurlStr = Base64.encodeToString(buffer, 0, buffer.length,
					Base64.NO_WRAP);
		}
		return imageurlStr;
	}

	public static Bitmap stringtoBitmap(String string) {
		// 将字符串转换成Bitmap类型
		Bitmap bitmap = null;
		/*
		 * BitmapFactory.Options options = new BitmapFactory.Options();
		 * options.inJustDecodeBounds = true;
		 */
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		try {
			byte[] bitmapArray;
			bitmapArray = Base64.decode(string, Base64.NO_WRAP);
			bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
					bitmapArray.length, options);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * file 2 base64
	 * 
	 * @param path
	 * @return
	 * @throws Exception
	 */
	public static String encodeBase64File(String path) throws Exception {
		File file = new File(path);
		FileInputStream inputFile = new FileInputStream(file);
		byte[] buffer = new byte[(int) file.length()];
		inputFile.read(buffer);
		inputFile.close();
		return Base64.encodeToString(buffer, Base64.NO_WRAP);
	}
}

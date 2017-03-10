package com.zhy.graph.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * 
* @ClassName: Utils 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 梅健健
* @date 2014年10月28日 下午5:27:41 
*
 */
public class Utils {
	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if(count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}
	
	 /** 
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素) 
     */  
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (dpValue * scale + 0.5f);  
    }  
  
    /** 
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp 
     */  
    public static int px2dip(Context context, float pxValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        return (int) (pxValue / scale + 0.5f);  
    }  
    
	/**
	 * 验证手机格式
	 * 
	 * @Title: isMobileNO
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param mobiles
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public static boolean isMobileNO(String mobiles) {
		/*
		 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
		 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
		 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
		 */
		String telRegex = "[1][34578]\\d{9}";// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
		if (TextUtils.isEmpty(mobiles))
			return false;
		else
			return mobiles.matches(telRegex);
	}

	public static String MyIP="112.74.174.121";
	/**
	 * @param imageView  ImagView×ÊÔ´
	 * @param width		 ÆÁÄ»¿í
	 * @param height	ÆÁÄ»¸ß
	 * @return
	 */
	public static Bitmap GetImgBitmap(ImageView imageView, int width, int height) {

		BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
		Bitmap img_bigmap = drawable.getBitmap();

		int widthDrawable = img_bigmap.getWidth();
		int heightDrawable = img_bigmap.getHeight();//
		// Log.v("franco-widthDrawable", "widthDrawable = " + widthDrawable);
		// Log.v("franco-heightDrawable", "heightDrawable = " +
		// heightDrawable);//

		float scaleWidth = (float) width / widthDrawable;
		float scaleHeight = (float) height / heightDrawable;
		// Log.v("franco", "scaleWidth = " + scaleWidth);
		// Log.v("franco", "scaleHeight = " + scaleHeight);//1.0:1.5

		Bitmap resizeBmp;
		Matrix matrix = new Matrix();
		if (scaleWidth < scaleHeight) {
			float scale = scaleHeight;
			matrix.postScale(scale, scale);
			int xStart = (int) (widthDrawable - widthDrawable / scale) / 2;
			resizeBmp = Bitmap
					.createBitmap(img_bigmap, xStart, 0,
							(int) (widthDrawable / scale), heightDrawable,
							matrix, true);
		} else {
			float scale = scaleWidth;
			matrix.postScale(scale, scale);
			int yStart = (int) (scaleHeight - scaleHeight / scale) / 2;
			// Log.v("franco-yStart", "yStart = " + yStart);
			resizeBmp = Bitmap
					.createBitmap(img_bigmap, 0, yStart, widthDrawable,
							(int) (heightDrawable / scale), matrix, true);
		}

		return resizeBmp;
	}

	/**
	 * @param imageView
	 * @param bitmap
	 * @param imageName
	 */
	public static void saveBitmap(ImageView imageView, Bitmap bitmap, String imageName){
		if(imageView!=null){
			imageView.setDrawingCacheEnabled(true);
			bitmap = imageView.getDrawingCache();
		}

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG , 100 , stream);
		byte[] byteArray = stream.toByteArray();
		File dir=new File(Environment.getExternalStorageDirectory ().getAbsolutePath()+"/picture" );
		if(!dir.isFile()){
			dir.mkdir();
		}
		File file=new File(dir,imageName +".png");
		try {
			FileOutputStream fos=new FileOutputStream(file);
			fos.write(byteArray, 0 , byteArray.length);
			fos.flush();
			fos.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();

		}
	}

	public static void saveImageToGallery(Context context, Bitmap bmp) {
		// 首先保存图片
		File appDir = new File(Environment.getExternalStorageDirectory(), "draw");
		if (!appDir.exists()) {
			appDir.mkdir();
		}
		String fileName = System.currentTimeMillis() + ".jpg";
		File file = new File(appDir, fileName);
		try {
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		// 其次把文件插入到系统图库
		try {
			MediaStore.Images.Media.insertImage(context.getContentResolver(),
					file.getAbsolutePath(), fileName, null);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// 最后通知图库更新
		context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(new File(file.getPath()))));
		Toast.makeText(context,"图片保存成功!",Toast.LENGTH_SHORT).show();
	}
}

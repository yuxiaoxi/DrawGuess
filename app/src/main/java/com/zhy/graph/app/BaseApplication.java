package com.zhy.graph.app;

import android.app.Application;
import android.graphics.Bitmap;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import net.duohuo.dhroid.Dhroid;
import net.duohuo.dhroid.dialog.DialogImpl;
import net.duohuo.dhroid.dialog.IDialog;
import net.duohuo.dhroid.ioc.Instance.InstanceScope;
import net.duohuo.dhroid.ioc.IocContainer;

import gra.zhy.com.graph.R;

/**
 * 
 * @ClassName: BaseApplication
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 余卓
 * @date 2014年12月24日 上午11:44:12
 * 
 */
public class BaseApplication extends Application {

	public static DisplayImageOptions defaultOptions;

	public static boolean isLogin = false;
	@Override
	public void onCreate() {
		super.onCreate();
		Dhroid.init(this);

		// 对话框对象
		IocContainer.getShare().bind(DialogImpl.class).to(IDialog.class)
		// 这是单例
				.scope(InstanceScope.SCOPE_SINGLETON);

		// //位置提醒相关代码
		// mNotifyer = new NotifyLister();
		// mNotifyer.SetNotifyLocation(40.047883,116.312564,3000,"gps");//4个参数代表要位置提醒的点的坐标，具体含义依次为：纬度，经度，距离范围，坐标系类型(gcj02,gps,bd09,bd09ll)
		// mLocationClient.registerNotify(mNotifyer);
		// 这个基本不需要,主要用于被注入的对象的属性也是注入时,可以注入的包
		// String[] pcks={"net.duohuo.xxxx"};
		// Const.ioc_instal_pkg=pcks;

		/***** 下面是测试的对象相互依赖的注入问题与配置无关 ******/

		defaultOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.ic_launcher) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.ic_launcher)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.ic_launcher) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(true)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.build();// 构建完成
		/*
		 * ImageLoaderConfiguration config = new
		 * ImageLoaderConfiguration.Builder( getApplicationContext())
		 * .threadPoolSize(3) // 线程池内加载的数量 .threadPriority(Thread.NORM_PRIORITY
		 * - 2) .denyCacheImageMultipleSizesInMemory()
		 * .defaultDisplayImageOptions(defaultOptions) .memoryCacheSize(3 * 1024
		 * * 1024) .discCacheSize(100 * 1024 * 1024)//
		 * .discCacheFileCount(100)// 缓存一百张图片 .writeDebugLogs().build();
		 * ImageLoader.getInstance().init(config);
		 */

		DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.photo_bg) // 设置图片在下载期间显示的图片
				.showImageForEmptyUri(R.drawable.photo_bg)// 设置图片Uri为空或是错误的时候显示的图片
				.showImageOnFail(R.drawable.photo_bg) // 设置图片加载/解码过程中错误时候显示的图片
				.cacheInMemory(false)// 设置下载的图片是否缓存在内存中
				.cacheOnDisc(true)// 设置下载的图片是否缓存在SD卡中
				.considerExifParams(true) // 是否考虑JPEG图像EXIF参数（旋转，翻转）
				.imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)// 设置图片以如何的编码方式显示
				.bitmapConfig(Bitmap.Config.RGB_565)// 设置图片的解码类型//
				.resetViewBeforeLoading(true)// 设置图片在下载前是否重置，复位
				.build();// 构建完成
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.defaultDisplayImageOptions(defaultOptions)
				.threadPoolSize(3)
				// 线程池内加载的数量
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCacheSize(1 * 1024 * 1024)
				.discCacheSize(50 * 1024 * 1024)//
				.discCacheFileCount(100)// 缓存一百张图片
				.writeDebugLogs().build();
		ImageLoader.getInstance().init(config);

	}


}

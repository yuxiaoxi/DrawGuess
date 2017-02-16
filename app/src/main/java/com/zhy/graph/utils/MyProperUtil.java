package com.zhy.graph.utils;

import android.content.Context;

import java.io.InputStream;
import java.util.Properties;

/**
 * 
* @ClassName: MyProperUtil 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 余卓 
* @date 2014年12月24日 下午3:46:56 
*
 */
public class MyProperUtil {
	private static Properties urlProps;

	public static Properties getProperties(Context c,String fileName) {
		Properties props = new Properties();
		try {
			// 方法一：通过activity中的context攻取setting.properties的FileInputStream
			InputStream in = c.getAssets().open(fileName);
			// 方法二：通过class获取setting.properties的FileInputStream
			// InputStream in =
			// PropertiesUtill.class.getResourceAsStream("/assets/  setting.properties "));
			props.load(in);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		urlProps = props;
		return urlProps;
	}
	


}

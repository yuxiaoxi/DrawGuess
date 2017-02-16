package com.zhy.graph.app;

import android.os.Environment;


/**
 * 
 * @ClassName: AppContext
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 余卓
 * @date 2014年12月24日 上午11:45:08
 * 
 */
public class AppContext {

	/**
	 * 精确到毫秒
	 */
	public static final String MS_FORMART = "yyyy-MM-dd HH:mm:ss";
	public static final String RE_FORMART = "MM.dd HH:mm 发布";
	public static String latitude = "0";
	public static String longtude = "0";
	public static String address = "";
	public static String city = "";
	public static String uid = "";
	public static String username = "";
	public static String type = "";
	public static String AVATAR = "aaaa";// 头像
	public static String CHARACTER = "";// 角色 1为教师，0为家长
	public static String USERNAME = "";// 昵称
	public static String VERSION = "";

	public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;// 拍照
	public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;// 本地图片
	public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;// 位置
	public static final int REQUESTCODE_INVITEACT = 0x000004;// 位置

	public static final int REPHONE = 10000;
	public static final int RESULTE = 20000;
	public static final int LOGIN = 30000;
	public static final int FORGETPSW = 40000;
	public static final int FORGETPSWVERITY = 50000;
	public static final int GETUSERINFO = 60000;
	public static final int SETUSERINFO = 70000;
	public static final int THREELOGIN = 80000;
	public static final int THREEREGISTER = 90000;
	public static final int INFORMATION = 100000;
	public static final int OLDSEND = 110000;
	public static final int OLDVER = 120000;
	public static final int NEWSEND = 110000;
	public static final int NEWVER = 120000;
	public static final int PICDELETE = 130000;
	public static final int PICDEMORE = 140000;
	public static final int ACATARUPLOAD = 150000;
	public static final int SETAVATAR = 160000;

	/**
	 * 存放发送图片的目录
	 */
	public static String BMOB_PICTURE_PATH = Environment
			.getExternalStorageDirectory() + "/supertutor/image/";
	public static final String SINGLE_CHAT_NEW_MESSAGE = "Singlechat";

	public static final String REFRESH_MESSAGE_RECORD = "REFRESH_MESSAGE_RECORD";
	public static final String SYSTEM_MESSAGE = "SYSTEM_MESSAGE";
	public static final String SINGLE_CHAT_OPEN_NEW_MESSAGE = "SINGLE_CHAT_OPEN_NEW_MESSAGE";
	public static final String LOGIN_SET = "eim_login_set";// 登录设置

	// 填写从短信SDK应用后台注册得到的APPKEY
	public static String APPKEY = "64767f86a4f5";

	// 填写从短信SDK应用后台注册得到的APPSECRET
	public static String APPSECRET = "9f88b92bc827858cd5c27de064b6733b";

	public static String GETBOOKINFOURL = "https://api.douban.com/v2/book/isbn/:";
	
	public static String REFRESHBOOKEDIT = "refreshbookedit";
	
	public static String session_key = "";
	

	public static boolean isRefreshHome = false;
	
	public static boolean isRefreshUniversiy = false;
	
	public static String university = "";
	
}

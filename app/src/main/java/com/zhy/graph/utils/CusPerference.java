package com.zhy.graph.utils;
import net.duohuo.dhroid.util.Perference;

/**
 * 
* @ClassName: CusPerference 
* @Description: TODO(这里用一句话描述这个类的作用) 
* @author 余卓 
* @date 2014年12月24日 下午2:23:26 
*
 */
public class CusPerference extends Perference {
	// 必须是public的属性不然不会赋值的
	
	//perference 保存用户帐号和密码的account的属性

	public String userName = ""; // 用户名
	public String password = ""; // 密码
	public String uid = ""; //uid
	public String nickName = "";
	public String avatar = "";
	public int threelogintype = -1;//第三方帐号类型

}

package com.zhy.graph.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.graph.R;
import com.zhy.graph.utils.MyProperUtil;
import com.zhy.graph.widget.NewBasicSingleItem;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

public class InviteFriendActivity extends BaseAct implements View.OnClickListener{

	public String TAG = "InviteFriendActivity";
	@InjectView(id = R.id.title_bar_view)
	private RelativeLayout title_bar_view;

	@InjectView(id = R.id.txt_title_name)
	private TextView txt_title_name;

	@InjectView(id = R.id.image_title_left, click = "onClickCallBack")
	private ImageView backLayout;

	@InjectView(id = R.id.item_self_center_invite_friend)
	private NewBasicSingleItem item_self_center_invite_friend;

	@InjectView(id = R.id.item_self_center_distribution_question)
	private NewBasicSingleItem item_self_center_distribution_question;

	@InjectView(id = R.id.item_self_center_feed_back)
	private NewBasicSingleItem item_self_center_feed_back;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_invite_friend);

		initView();
	}

	public void initView(){
		backLayout.setImageResource(R.drawable.back_left_icon);
		backLayout.setVisibility(View.VISIBLE);
		txt_title_name.setTextColor(Color.parseColor("#000000"));
		txt_title_name.setText("邀请好友");
		txt_title_name.setVisibility(View.VISIBLE);
		title_bar_view.setBackgroundColor(Color.parseColor("#ffffff"));
		item_self_center_invite_friend.setOnClickListener(this);
		item_self_center_distribution_question.setOnClickListener(this);
		item_self_center_feed_back.setOnClickListener(this);
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {

		case R.id.image_title_left:
			finish();
			break;




		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}


	/**
	 * 
	 * @Title: doLogin
	 * @Description: 登录
	 * @param @param uid
	 * @return void 返回类型
	 * @throws
	 */
	void doLogin(final String uid, final String password) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", uid);
		map.put("passwd", password);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("login");
		DhNet net = new DhNet(url);
		net.addParams(map).doPost(new NetTask(this) {

			@Override
			public void onErray(Response response) {

				super.onErray(response);
				Toast.makeText(InviteFriendActivity.this,"数据请求错误！请您重新再试！",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void doInUI(Response response, Integer transfer) {

			}
		});

	}

	/**
	 * 
	 * @Title: isEmpty
	 * @Description: 判断字符串是否为空
	 * @param @param str
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public boolean isEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}
	
	/**
	 * 判断帐号是否可用
	 * 
	 * @Title: isUserExist
	 * @Description: TODO(这里用一句话描述这个方法的作用)
	 * @param @param uid 设定文件
	 * @return void 返回类型
	 * @throws
	 */
	public void isUserExist(final String userId, final String name, final String avatar,final String type) {
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", userId);
		String url = MyProperUtil.getProperties(this,
				"appConfigDebugHost.properties").getProperty("Host")
				+ MyProperUtil.getProperties(this, "appConfigDebug.properties")
						.getProperty("isUserExist");
		DhNet net = new DhNet(url);
		net.addParams(map).doPost(new NetTask(this) {

			@Override
			public void onErray(Response response) {

				super.onErray(response);
			}

			@Override
			public void doInUI(Response response, Integer transfer) {


			}
		});

	}

	@Override
	public void onClick(View v) {
		Platform plat = null;
		if (v.getId() == R.id.item_self_center_invite_friend) {
			plat = ShareSDK.getPlatform(WechatMoments.NAME);

		} else if (v.getId() == R.id.item_self_center_distribution_question) {
			plat = ShareSDK.getPlatform(Wechat.NAME);
		} else if (v.getId() == R.id.item_self_center_feed_back) {
			plat = ShareSDK.getPlatform(QQ.NAME);
		}
		showShare(plat.getName());
	}


	private void showShare(String platform) {
		//指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
		if (platform == null) {
			return;
		}
		Platform.ShareParams sp = new Platform.ShareParams();
		sp.setShareType(Platform.SHARE_WEBPAGE);
		sp.setTitle("你画我猜go!go!go!");
		sp.setTitleUrl("http://sj.qq.com/myapp/detail.htm?apkName=com.zhy.graph"); // 标题的超链接
		sp.setText("菊长手把手教你如何撩妹!");
		sp.setImageUrl("https://thumbnail0.baidupcs.com/thumbnail/076045b6d6b35945be577217c71fea0b?fid=622204658-250528-925177685278487&time=1493190000&rt=sh&sign=FDTAER-DCb740ccc5511e5e8fedcff06b081203-FNzxHV9ATlNiyG3bCmE%2BWHjZg40%3D&expires=8h&chkv=0&chkbd=0&chkpc=&dp-logid=2681861173436973243&dp-callid=0&size=c710_u400&quality=100");
		sp.setUrl("http://sj.qq.com/myapp/detail.htm?apkName=com.zhy.graph");
		Platform pf = ShareSDK.getPlatform (platform);
// 设置分享事件回调（注：回调放在不能保证在主线程调用，不可以在里面直接处理UI操作）
		pf.setPlatformActionListener (new PlatformActionListener() {
			public void onError(Platform arg0, int arg1, Throwable arg2) {
				//失败的回调，arg:平台对象，arg1:表示当前的动作，arg2:异常信息
				Log.e(TAG,arg2.toString());
			}
			public void onComplete(Platform arg0, int arg1, HashMap<String, Object> arg2) {
				//分享成功的回调
				Toast.makeText(InviteFriendActivity.this, "分享成功!", Toast.LENGTH_SHORT).show();
				Log.e(TAG,arg2.toString());
			}
			public void onCancel(Platform arg0, int arg1) {
				//取消分享的回调
				Toast.makeText(InviteFriendActivity.this, "分享已取消!", Toast.LENGTH_SHORT).show();

			}
		});
		// 执行图文分享
		pf.share(sp);
	}
}

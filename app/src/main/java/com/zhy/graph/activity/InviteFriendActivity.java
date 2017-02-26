package com.zhy.graph.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.graph.utils.MyProperUtil;
import com.zhy.graph.widget.NewBasicSingleItem;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;
import gra.zhy.com.graph.R;

public class InviteFriendActivity extends BaseAct implements View.OnClickListener{


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
		final OnekeyShare oks = new OnekeyShare();
		//指定分享的平台，如果为空，还是会调用九宫格的平台列表界面
		if (platform != null) {
			oks.setPlatform(platform);
		}
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
		oks.setTitle("标题");
		// titleUrl是标题的网络链接，仅在Linked-in,QQ和QQ空间使用
		oks.setTitleUrl("http://sharesdk.cn");
		// text是分享文本，所有平台都需要这个字段
		oks.setText("我是分享文本");
		//分享网络图片，新浪微博分享网络图片需要通过审核后申请高级写入接口，否则请注释掉测试新浪微博
		oks.setImageUrl("http://f1.sharesdk.cn/imgs/2014/02/26/owWpLZo_638x960.jpg");
		// imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
		//oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
		// url仅在微信（包括好友和朋友圈）中使用
		oks.setUrl("http://sharesdk.cn");
		// comment是我对这条分享的评论，仅在人人网和QQ空间使用
		oks.setComment("我是测试评论文本");
		// site是分享此内容的网站名称，仅在QQ空间使用
		oks.setSite("ShareSDK");
		// siteUrl是分享此内容的网站地址，仅在QQ空间使用
		oks.setSiteUrl("http://sharesdk.cn");

		//启动分享
		oks.show(this);
	}
}

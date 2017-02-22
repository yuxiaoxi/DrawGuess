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

import gra.zhy.com.graph.R;

public class SelfCenterActivity extends BaseAct implements View.OnClickListener{


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

	@InjectView(id = R.id.item_self_center_about)
	private NewBasicSingleItem item_self_center_about;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_self_center);

		initView();
	}

	public void initView(){
		backLayout.setImageResource(R.drawable.back_left_icon);
		backLayout.setVisibility(View.VISIBLE);
		txt_title_name.setTextColor(Color.parseColor("#000000"));
		txt_title_name.setText("个人中心");
		txt_title_name.setVisibility(View.VISIBLE);
		title_bar_view.setBackgroundColor(Color.parseColor("#ffffff"));
		item_self_center_invite_friend.setOnClickListener(this);
		item_self_center_distribution_question.setOnClickListener(this);
		item_self_center_feed_back.setOnClickListener(this);
		item_self_center_about.setOnClickListener(this);
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
				Toast.makeText(SelfCenterActivity.this,"数据请求错误！请您重新再试！",
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
		if (v.getId() == R.id.item_self_center_invite_friend) {

		} else if (v.getId() == R.id.item_self_center_distribution_question) {

		} else if (v.getId() == R.id.item_self_center_feed_back) {

		} else if (v.getId() == R.id.item_self_center_about) {

		}
	}
}

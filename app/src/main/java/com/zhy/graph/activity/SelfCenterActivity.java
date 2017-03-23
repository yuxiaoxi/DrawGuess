package com.zhy.graph.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mob.tools.utils.UIHandler;
import com.zhy.graph.utils.MyProperUtil;
import com.zhy.graph.widget.NewBasicSingleItem;
import com.zhy.graph.widget.PopDialog;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;
import net.duohuo.dhroid.view.megwidget.CircleImageView;

import java.util.HashMap;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;
import gra.zhy.com.graph.R;

public class SelfCenterActivity extends BaseAct implements Callback,
		View.OnClickListener, PlatformActionListener {

	private static final int MSG_USERID_FOUND = 1;
	private static final int MSG_LOGIN = 2;
	private static final int MSG_AUTH_CANCEL = 3;
	private static final int MSG_AUTH_ERROR= 4;
	private static final int MSG_AUTH_COMPLETE = 5;

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

	@InjectView(id = R.id.img_self_center_avatar)
	private CircleImageView img_self_center_avatar;


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
		img_self_center_avatar.setOnClickListener(this);
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {

		case R.id.image_title_left:
			setResult(2);
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


	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		if (v.getId() == R.id.item_self_center_invite_friend) {
			intent.setClass(SelfCenterActivity.this, InviteFriendActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.item_self_center_distribution_question) {
			final PopDialog popDialog = new PopDialog(SelfCenterActivity.this,R.style.inputDialog).setGravity(Gravity.CENTER).setResources(R.layout.pop_distribution_words);
			EditText popEdit = (EditText)popDialog.findViewById(R.id.edit_distribution_describe);
			popEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if(actionId == EditorInfo.IME_ACTION_SEND){
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
						if(popDialog.isShowing()) {
							popDialog.dismiss();
						}
					}
					return false;
				}
			});
			popDialog.setCanceledOnTouchOutside(true);
			if(popDialog!=null&&!popDialog.isShowing()) {
				popDialog.show();
			}
		} else if (v.getId() == R.id.item_self_center_feed_back) {
			intent.setClass(SelfCenterActivity.this, FeedBackActivity.class);
			startActivity(intent);
		} else if (v.getId() == R.id.item_self_center_about) {
			intent.setClass(SelfCenterActivity.this, AboutActivity.class);
			startActivity(intent);
		} else if(v.getId() == R.id.img_self_center_avatar) {
			final PopDialog loginDialog  = new PopDialog(SelfCenterActivity.this,R.style.CustomProgressDialog).setGravity(Gravity.CENTER).setResources(R.layout.pop_select_login_type);
//
			loginDialog.findViewById(R.id.btn_login_qq_bg).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					authorize(new QQ(SelfCenterActivity.this));
				}
			});

			loginDialog.findViewById(R.id.btn_login_weixin_bg).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					authorize(new Wechat(SelfCenterActivity.this));
				}
			});

			loginDialog.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(loginDialog.isShowing()) {
						loginDialog.dismiss();
					}
				}
			});

			loginDialog.setCanceledOnTouchOutside(false);

			if(!loginDialog.isShowing()) {
				loginDialog.show();
			}
		}
	}


	private void authorize(Platform plat) {
		if(plat.isValid()) {
			String userId = plat.getDb().getUserId();
			if (!TextUtils.isEmpty(userId)) {
				UIHandler.sendEmptyMessage(MSG_USERID_FOUND, this);
				login(plat.getName(), userId, null);
				return;
			}
		}
		plat.setPlatformActionListener(this);
		plat.SSOSetting(true);
		plat.showUser(null);
	}

	public void onComplete(Platform platform, int action,
						   HashMap<String, Object> res) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_COMPLETE, this);
			login(platform.getName(), platform.getDb().getUserId(), res);
		}
		System.out.println(res);
		System.out.println("------User Name ---------" + platform.getDb().getUserName());
		System.out.println("------User ID ---------" + platform.getDb().getUserId());
	}

	public void onError(Platform platform, int action, Throwable t) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_ERROR, this);
		}
		t.printStackTrace();
	}

	public void onCancel(Platform platform, int action) {
		if (action == Platform.ACTION_USER_INFOR) {
			UIHandler.sendEmptyMessage(MSG_AUTH_CANCEL, this);
		}
	}

	private void login(String plat, String userId, HashMap<String, Object> userInfo) {
		Message msg = new Message();
		msg.what = MSG_LOGIN;
		msg.obj = plat;
		UIHandler.sendMessage(msg, this);
	}

	public boolean handleMessage(Message msg) {
		switch(msg.what) {
			case MSG_USERID_FOUND: {
				Toast.makeText(this, R.string.userid_found, Toast.LENGTH_SHORT).show();
			}
			break;
			case MSG_LOGIN: {

				String text = getString(R.string.logining, msg.obj);
				Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
				System.out.println("---------------");

//				Builder builder = new Builder(this);
//				builder.setTitle(R.string.if_register_needed);
//				builder.setMessage(R.string.after_auth);
//				builder.setPositiveButton(R.string.ok, null);
//				builder.create().show();
			}
			break;
			case MSG_AUTH_CANCEL: {
				Toast.makeText(this, R.string.auth_cancel, Toast.LENGTH_SHORT).show();
				System.out.println("-------MSG_AUTH_CANCEL--------");
			}
			break;
			case MSG_AUTH_ERROR: {
				Toast.makeText(this, R.string.auth_error, Toast.LENGTH_SHORT).show();
				System.out.println("-------MSG_AUTH_ERROR--------");
			}
			break;
			case MSG_AUTH_COMPLETE: {
				Toast.makeText(this, R.string.auth_complete, Toast.LENGTH_SHORT).show();
				System.out.println("--------MSG_AUTH_COMPLETE-------");
			}
			break;
		}
		return false;
	}
}

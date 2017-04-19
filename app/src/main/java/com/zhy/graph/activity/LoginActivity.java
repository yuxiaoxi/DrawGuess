package com.zhy.graph.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.graph.R;
import com.zhy.graph.utils.CusPerference;
import com.zhy.graph.utils.MyProperUtil;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import java.util.HashMap;
import java.util.Map;


public class LoginActivity extends BaseAct {


	@InjectView(id = R.id.login_linear)
	private LinearLayout login_linear;
	@InjectView(id = R.id.weixin_login, click = "onClickCallBack")
	private ImageView weixin_login;
	@InjectView(id = R.id.qqzone_login, click = "onClickCallBack")
	private ImageView qqzone_login;

	@InjectView(id = R.id.login_edite_phone)
	private EditText login_edite_phone;
	@InjectView(id = R.id.login_edite_password)
	private EditText login_edite_password;
	@InjectView(id = R.id.login_btn_login, click = "onClickCallBack")
	private TextView login_btn_login;

	@InjectView(id = R.id.image_title_left, click = "onClickCallBack")
	private ImageView backLayout;
	@Inject
	CusPerference cusPerference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_login);

		cusPerference.load();
		backLayout.setImageResource(R.drawable.skyblue_editpage_close);
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {

		case R.id.image_title_left:
			finish();
			break;


		case R.id.login_btn_login:

			if (isEmpty(login_edite_phone.getText().toString().trim())
					|| isEmpty(login_edite_password.getText().toString().trim())) {
				Toast.makeText(this, "帐号或密码为空，请输入", Toast.LENGTH_SHORT).show();
			} else {
				doLogin(login_edite_phone.getText().toString().trim(),
						login_edite_password.getText().toString().trim());
			}

			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		login_edite_phone.setText(cusPerference.uid);
		login_edite_password.setText(cusPerference.password);
		if (!"".equals(login_edite_phone.getText().toString().trim())
				&& !"".equals(login_edite_password.getText().toString().trim())) {
			doLogin(login_edite_phone.getText().toString().trim(),
					login_edite_password.getText().toString().trim());
		}
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
				Toast.makeText(LoginActivity.this,"数据请求错误！请您重新再试！",
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

}

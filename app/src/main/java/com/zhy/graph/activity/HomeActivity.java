package com.zhy.graph.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.graph.adapter.HomePlayerGridAdapter;
import com.zhy.graph.bean.PlayerInfo;
import com.zhy.graph.utils.MyProperUtil;
import com.zhy.graph.widget.PopDialog;

import net.duohuo.dhroid.ioc.annotation.InjectView;
import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import org.java_websocket.WebSocket;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import gra.zhy.com.graph.R;
import rx.Observer;
import rx.Subscriber;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

public class HomeActivity extends BaseAct {

	@InjectView(id = R.id.image_title_left, click = "onClickCallBack")
	private ImageView left_image;
	@InjectView(id = R.id.txt_home_create_player_room, click = "onClickCallBack")
	private TextView txt_home_create_player_room;

	@InjectView(id = R.id.image_title_right, click = "onClickCallBack")
	private ImageView right_image;

	@InjectView(id = R.id.txt_home_ready_ready_btn, click = "onClickCallBack")
	private TextView txt_home_ready_ready_btn;

	@InjectView(id = R.id.txt_home_join_player_room, click = "onClickCallBack")
	private TextView txt_home_join_player_room;

	@InjectView(id = R.id.grid_home)
	private GridView grid_home;

	@InjectView(id = R.id.txt_title_name)
	private TextView titleTextView;

	@InjectView(id = R.id.txt_home_ready_time_down)
	private TextView txt_home_ready_time_down;


	private Timer daoTimer;
	private HomePlayerGridAdapter adapter;
	private StompClient mStompClient;
	private String TAG = "HomeActivity";
	private Mythread mythread = null;
	private PopDialog popDialog = null;
	private boolean roomOwner;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_home_view);

		initView();

	}

	public void initView(){
		left_image.setVisibility(View.VISIBLE);
		left_image.setImageResource(R.drawable.title_bar_self_center_icon);
		right_image.setVisibility(View.VISIBLE);
		right_image.setImageResource(R.drawable.title_bar_share_icon);
		titleTextView.setVisibility(View.VISIBLE);
		titleTextView.setText("房间666");

		adapter = new HomePlayerGridAdapter(HomeActivity.this,getData());
		grid_home.setAdapter(adapter);
		countDown(18);
	}


	public List<PlayerInfo> getData(){
		List<PlayerInfo> list = new ArrayList<>();
		for (int i = 0; i<6; i++){
			PlayerInfo info = new PlayerInfo();
			info.setNickName("haha"+i);
			info.setReady(i%2==0);
			info.setYouke(i%2==1);
			if(i == 0){
				info.setMe(true);
				roomOwner = true;
			}
			list.add(info);
		}
		return list;
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {

			case R.id.image_title_left:
				intent.setClass(HomeActivity.this,LoginActivity.class);
				startActivity(intent);
				break;

			case R.id.txt_home_create_player_room:
				intent.setClass(HomeActivity.this,PlayerRoomActivity.class);
				startActivity(intent);
				break;

			case R.id.txt_home_ready_ready_btn:
				daoTimer.cancel();
				if("开始".equals(txt_home_ready_ready_btn.getText().toString())){//是房主
					popDialog = PopDialog.createDialog(HomeActivity.this, R.layout.pop_select_guess_word, Gravity.CENTER, R.style.CustomProgressDialog);
					Window win = popDialog.getWindow();
					win.getDecorView().setPadding(0, 0, 0, 0);
					WindowManager.LayoutParams lp = win.getAttributes();
					lp.width = WindowManager.LayoutParams.FILL_PARENT;
					lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
					win.setAttributes(lp);
					popDialog.setCanceledOnTouchOutside(false);

					if(!popDialog.isShowing()) {
						popDialog.show();
					}
				}else{
					txt_home_ready_time_down.setVisibility(View.GONE);
					adapter.clickReady();
					txt_home_ready_ready_btn.setText("已准备");
					txt_home_ready_ready_btn.setTextColor(Color.parseColor("#ffffff"));
					txt_home_ready_ready_btn.setBackgroundResource(R.drawable.btn_shape_ready_gray);
					txt_home_ready_ready_btn.setEnabled(false);
				}

				break;

			case R.id.txt_home_join_player_room:

				popDialog = PopDialog.createDialog(HomeActivity.this, R.layout.pop_join_play_room, Gravity.CENTER,R.style.inputDialog);
				Window win = popDialog.getWindow();
				win.getDecorView().setPadding(0, 0, 0, 0);
				WindowManager.LayoutParams lp = win.getAttributes();
				lp.width = WindowManager.LayoutParams.FILL_PARENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				win.setAttributes(lp);
				popDialog.findViewById(R.id.btn_join_play_room).setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						popDialog.findViewById(R.id.txt_warn_room_not_exist).setVisibility(View.VISIBLE);
					}
				});


				if(!popDialog.isShowing()) {
					popDialog.show();
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
		if(mythread == null){
			mythread = new Mythread();
			mythread.start();
		}else{
			mythread.run();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		mStompClient.disconnect();
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
				Toast.makeText(HomeActivity.this,"数据请求错误！请您重新再试！",
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

	private void countDown(int countTime){
		txt_home_ready_time_down.setVisibility(View.VISIBLE);
		txt_home_ready_time_down.setText(countTime+"s");
		daoTimer = new Timer();
		TimerTask task = new TimerTask() {
			public void run() {
				Message msg = new Message();
				msg.what = 0x10;
				msg.arg1 = Integer.parseInt(txt_home_ready_time_down.getText().toString().split("s")[0])-1;
				if(msg.arg1 == 0){
					daoTimer.cancel();
				}
				changeUI.sendMessage(msg);
			}
		};
		daoTimer.schedule(task, 0, 1000);
	}

	private Handler changeUI = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x10){
				if(msg.arg1 == 0){
					txt_home_ready_time_down.setVisibility(View.GONE);
				}else{
					txt_home_ready_time_down.setText(msg.arg1+"s");
				}
			} else if(msg.what == 0x11){
				if(roomOwner){
					txt_home_ready_ready_btn.setText("开始");
				}
			}
		}
	};
	public class Mythread extends Thread {
		@Override
		public void run() {
			conn();

		}
	}
	private void conn() {
		try {

			if(mStompClient!=null&&mStompClient.isConnected())
				return;
			Map<String, String> connectHttpHeaders = new HashMap<>();
			connectHttpHeaders.put("user-name", new Date().toString());
			mStompClient = Stomp.over(WebSocket.class, "ws://112.74.174.121:8080/ws/websocket", connectHttpHeaders);
			mStompClient.topic("/topic/user.login").subscribe(new Subscriber<StompMessage>() {
				@Override
				public void onCompleted() {
					Log.e(TAG, "/topic/user.login/ onCompleted: ");
				}

				@Override
				public void onError(Throwable e) {
					Log.e(TAG, "/topic/user.login/ onError: " + e.getMessage());
				}

				@Override
				public void onNext(StompMessage stompMessage) {
					Log.e(TAG, "login onNext: " + stompMessage.getPayload());
				}

			});

			mStompClient.topic("/topic/draw/pts").subscribe(new Subscriber<StompMessage>() {
				@Override
				public void onCompleted() {
					Log.i(TAG, "/topic/pts/ onCompleted: ");
				}

				@Override
				public void onError(Throwable e) {
					Log.i(TAG, "/topic/pts/ onError: " + e.getMessage());
				}

				@Override
				public void onNext(StompMessage stompMessage) {
					Log.e(TAG, "response onNext: " + stompMessage.getPayload()
					);

				}

			});

			mStompClient.lifecycle().subscribe(new Observer<LifecycleEvent>() {
				@Override
				public void onCompleted() {
					Log.e(TAG, "lifecycle onCompleted: ");
				}

				@Override
				public void onError(Throwable e) {
					Log.e(TAG, "lifecycle onError: ");
				}

				@Override
				public void onNext(LifecycleEvent lifecycleEvent) {
					switch (lifecycleEvent.getType()) {

						case OPENED:
							Log.e(TAG, "Stomp connection opened");
							Message msg = new Message();
							msg.what = 0x11;
							changeUI.sendMessage(msg);
							break;

						case ERROR:
							Log.e(TAG, "Error", lifecycleEvent.getException());
							break;

						case CLOSED:
							Log.e(TAG, "Stomp connection closed");
							break;
					}
				}
			});

			mStompClient.connect();
			Log.i(TAG, "end of program,mStompClient status:" + mStompClient.isConnected());

		} catch (Exception e) {
			e.printStackTrace();
			Log.i("IOException", "IOException");
		}
	}

}

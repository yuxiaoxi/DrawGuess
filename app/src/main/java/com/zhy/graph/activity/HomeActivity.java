package com.zhy.graph.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.graph.adapter.HomePlayerGridAdapter;
import com.zhy.graph.app.BaseApplication;
import com.zhy.graph.bean.PlayerInfo;
import com.zhy.graph.bean.ResultBean;
import com.zhy.graph.bean.RoomInfoBean;
import com.zhy.graph.utils.DomainUtils;
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

import cn.sharesdk.onekeyshare.OnekeyShare;
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
		handleUserCreateFormUsingPOST(String.valueOf(new Date().getTime()),"123456","");
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
				intent.setClass(HomeActivity.this,SelfCenterActivity.class);
				startActivity(intent);
				break;

			case R.id.txt_home_create_player_room:
				createRoomUsingGET(BaseApplication.username);
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
				((EditText)popDialog.findViewById(R.id.edit_input_room_id)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
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
				Window win = popDialog.getWindow();
				win.getDecorView().setPadding(0, 0, 0, 0);
				WindowManager.LayoutParams lp = win.getAttributes();
				lp.width = WindowManager.LayoutParams.FILL_PARENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				win.setAttributes(lp);

				if(!popDialog.isShowing()) {
					popDialog.show();
				}
				break;

			case R.id.image_title_right:
				if(BaseApplication.isLogin){
					showShare();
				}else{

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
		if(mythread != null){
			mythread.run();
		}

	}

	@Override
	protected void onStop() {
		super.onStop();
		if(mStompClient!=null){
			mStompClient.disconnect();
		}
	}

	/**
	 * 用户进入随机房间接口
	 * @param username
     */
	void getRandomRoomUsingGET(final String username) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", username);
		String url = DomainUtils.SERVER_HOST+"/api/v1/room/into";
		DhNet net = new DhNet(url);
		net.addParams(map).doGet(new NetTask(HomeActivity.this) {

			@Override
			public void onErray(Response response) {

				super.onErray(response);
				Toast.makeText(HomeActivity.this,"数据请求错误！请您重新再试！",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				if("1".equals(response.code)) {//获取成功
					RoomInfoBean roomInfo = response.modelFromData(RoomInfoBean.class);
					Log.e(TAG,roomInfo.getNowUserNum());

					mythread = new Mythread(username,roomInfo.getRoomId());
					mythread.start();
				}
			}
		});

	}

	/**
	 * createRoomUsingGET
	 * @param username
     */
	void createRoomUsingGET(final String username) {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", username);
		String url = DomainUtils.SERVER_HOST+"/api/v1/room/create";
		DhNet net = new DhNet(url);
		net.addParams(map).doGet(new NetTask(HomeActivity.this) {

			@Override
			public void onErray(Response response) {

				super.onErray(response);
				Toast.makeText(HomeActivity.this,"数据请求错误！请您重新再试！",
						Toast.LENGTH_SHORT).show();
			}

			@Override
			public void doInUI(Response response, Integer transfer) {
				if("1".equals(response.code)) {//获取成功
					RoomInfoBean roomInfo = response.modelFromData(RoomInfoBean.class);
					Log.e(TAG,roomInfo.getNowUserNum());
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this,PlayerRoomActivity.class);
					intent.putExtra("data",roomInfo);
					startActivity(intent);

				}
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
	 * 游客创建
	 * @param userName
	 * @param password
	 * @param vcode
     */
	public void handleUserCreateFormUsingPOST(final String userName, final String password, final String vcode) {

		Map<String, Object> map = new HashMap<String, Object>();
		map.put("username", userName);
		map.put("password",password);
		map.put("vcode",vcode);
		System.out.println(map.toString());
		String url = DomainUtils.SERVER_HOST+"/api/v1/create";
		DhNet net = new DhNet(url);
		net.addParams(map).doPost(new NetTask(HomeActivity.this) {

			@Override
			public void onErray(Response response) {

				super.onErray(response);
			}

			@Override
			public void doInUI(Response response, Integer transfer) {

				Log.e(TAG,response.result);
				ResultBean result = response.model(ResultBean.class);

				if("1".equals(result.getCode())){//创建成功
					BaseApplication.username = userName;
					getRandomRoomUsingGET(userName);
//
				}else if("0".equals(result.getCode())){//创建失败

				}

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
		private String userName;
		private String roomId;
		public Mythread(String username,String roomId){
			this.userName = username;
			this.roomId = roomId;
		}
		@Override
		public void run() {
			conn(userName,roomId);

		}
	}
	private void conn(String userName,String roomId) {
		try {

			if(mStompClient!=null&&mStompClient.isConnected())
				return;
			Map<String, String> connectHttpHeaders = new HashMap<>();
			connectHttpHeaders.put("user-name", userName);
			mStompClient = Stomp.over(WebSocket.class, "ws://112.74.174.121:8080/ws/websocket", connectHttpHeaders);

			mStompClient.topic("/topic/room."+roomId+"/out").subscribe(new Subscriber<StompMessage>() {
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


	private void showShare() {
		OnekeyShare oks = new OnekeyShare();
		//关闭sso授权
		oks.disableSSOWhenAuthorize();
		// title标题，印象笔记、邮箱、信息、微信、人人网、QQ和QQ空间使用
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

// 启动分享GUI
		oks.show(this);
	}

}

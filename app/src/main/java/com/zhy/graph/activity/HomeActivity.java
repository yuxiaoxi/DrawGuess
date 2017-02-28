package com.zhy.graph.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import com.zhy.graph.adapter.HomePlayerGridAdapter;
import com.zhy.graph.app.BaseApplication;
import com.zhy.graph.bean.PlayerBean;
import com.zhy.graph.bean.PlayerInfo;
import com.zhy.graph.bean.RoomInfoBean;
import com.zhy.graph.network.MessageObserveUtil;
import com.zhy.graph.network.NetRequestUtil;
import com.zhy.graph.widget.PopDialog;

import net.duohuo.dhroid.ioc.annotation.InjectView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.onekeyshare.OnekeyShare;
import gra.zhy.com.graph.R;
import ua.naiksoftware.stomp.client.StompClient;

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
	private MessageObserveUtil obserUitl = null;
	private PopDialog popDialog = null;
	private boolean roomOwner,onStop;
	private List<PlayerInfo> dataList;
	private String roomId;
	private NetRequestUtil netUitl = null;
	private TimerTask task = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_home_view);
		initView();
		netUitl.handleUserCreateFormUsingPOST(String.valueOf(new Date().getTime()),"123456","");

	}

	public void initView(){
		dataList = new ArrayList<>();
		left_image.setVisibility(View.VISIBLE);
		left_image.setImageResource(R.drawable.title_bar_self_center_icon);
		right_image.setVisibility(View.VISIBLE);
		right_image.setImageResource(R.drawable.title_bar_share_icon);
		titleTextView.setVisibility(View.VISIBLE);


		adapter = new HomePlayerGridAdapter(HomeActivity.this,dataList);
		grid_home.setAdapter(adapter);
		netUitl = new NetRequestUtil(HomeActivity.this,netRequest);
	}


	public void initData(RoomInfoBean roomInfoBean){
		dataList.clear();
		titleTextView.setText("房间"+roomInfoBean.getRoomId());
		if(roomInfoBean.getAddedUserList()==null)
			return;
		for (int i = 0; i<roomInfoBean.getAddedUserList().size(); i++){
			PlayerInfo info = new PlayerInfo();
			info.setNickName(roomInfoBean.getAddedUserList().get(i).getNickname());
			info.setReady(false);
			info.setYouke(true);
			info.setId(roomInfoBean.getAddedUserList().get(i).getId());
			info.setUsername(roomInfoBean.getAddedUserList().get(i).getUsername());
			if((i+1) == Integer.parseInt(roomInfoBean.getNowUserNum()))
				info.setMe(true);
			if(i==0&&Integer.parseInt(roomInfoBean.getNowUserNum())==1){
				roomOwner = true;
			}
			dataList.add(info);
		}
		adapter.notifyDataSetChanged();
		if(!roomOwner){
			countDown(18);
		}

	}

	public void updateData(PlayerBean playerBean){
		if(playerBean==null)
			return;
		PlayerInfo info = new PlayerInfo();
		info.setNickName(playerBean.getNickname());
		info.setReady(false);
		info.setYouke(true);
		info.setMe(false);
		info.setId(playerBean.getId());
		info.setUsername(playerBean.getUsername());
		dataList.add(info);
		adapter.notifyDataSetChanged();
	}

	public void logout(PlayerBean playerBean){
		if(playerBean==null)
			return;
		for (PlayerInfo info: dataList
			 ) {
			if(info.getId().equals(playerBean.getId())){
				dataList.remove(info);
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}

	public void toReady(PlayerBean playerBean,boolean ready){
		if(playerBean==null)
			return;
		for (PlayerInfo info: dataList
				) {
			if(info.getId().equals(playerBean.getId())){
				info.setReady(ready);
				break;
			}
		}
		adapter.notifyDataSetChanged();
	}


	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {

			case R.id.image_title_left:
				intent.setClass(HomeActivity.this,SelfCenterActivity.class);
				startActivity(intent);
				break;

			case R.id.txt_home_create_player_room:
				daoTimer.cancel();
				netUitl.leaveRoomUsingGET(BaseApplication.username,1,null);
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
				}else if("准备".equals(txt_home_ready_ready_btn.getText().toString())){
					netUitl.userReadyUsingGET(BaseApplication.username,roomId);
				}else if("已准备".equals(txt_home_ready_ready_btn.getText().toString())){
					netUitl.userReadyCancelUsingGET(BaseApplication.username,roomId);
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
							daoTimer.cancel();
							String roomId = ((EditText)popDialog.findViewById(R.id.edit_input_room_id)).getText().toString().trim();
							netUitl.leaveRoomUsingGET(BaseApplication.username,2,roomId);
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
		onStop = false;

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 1 && resultCode == 1){
			netUitl.getRandomRoomUsingGET(BaseApplication.username);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		onStop = true;
		netUitl.leaveRoomUsingGET(BaseApplication.username,0,null);
		if(mStompClient!=null){
			mStompClient.disconnect();
		}
		if(daoTimer != null){
			daoTimer.cancel();
		}

	}


	private void countDown(int countTime){
		txt_home_ready_time_down.setVisibility(View.VISIBLE);
		txt_home_ready_time_down.setText(countTime+"s");
		daoTimer = new Timer();
		task = new TimerTask() {
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
					txt_home_ready_time_down.setText("0");
					txt_home_ready_time_down.setVisibility(View.GONE);
					netUitl.leaveRoomUsingGET(BaseApplication.username,0,null);
				}else{
					txt_home_ready_time_down.setText(msg.arg1+"s");
				}
			} else if(msg.what == 0x11){
				if(roomOwner){
					txt_home_ready_ready_btn.setText("开始");
				}else{
					txt_home_ready_ready_btn.setText("准备");
				}
			} else if(msg.what == 0x12){
				updateData((PlayerBean) msg.obj);
			} else if(msg.what == 0x13){
				logout((PlayerBean) msg.obj);
			} else if(msg.what == 0x14){
				toReady((PlayerBean) msg.obj,true);
			} else if(msg.what == 0x16){
				toReady((PlayerBean) msg.obj,false);
			}
		}
	};

	private Handler netRequest = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			if(msg.what == 0x10){
				RoomInfoBean roomInfo = (RoomInfoBean) msg.obj;
				roomId = roomInfo.getRoomId();
				initData(roomInfo);
				if(obserUitl == null){
					obserUitl = new MessageObserveUtil(BaseApplication.username,roomInfo.getRoomId(),mStompClient,changeUI);
					obserUitl.start();
				}else{
					obserUitl.setRoomId(roomInfo.getRoomId());
					obserUitl.run();
				}
			} else if(msg.what == 0x11){
				if(!onStop){//非退出app,倒计时到了自动退出房间
					mStompClient.disconnect();
					netUitl.getRandomRoomUsingGET(BaseApplication.username);
				}
			} else if(msg.what == 0x12){
				txt_home_ready_time_down.setVisibility(View.GONE);
//					adapter.clickReady();
				txt_home_ready_ready_btn.setText("已准备");
				txt_home_ready_ready_btn.setTextColor(Color.parseColor("#ffffff"));
				txt_home_ready_ready_btn.setBackgroundResource(R.drawable.btn_shape_ready_gray);
				daoTimer.cancel();
			} else if(msg.what == 0x13){
				txt_home_ready_time_down.setVisibility(View.VISIBLE);
				txt_home_ready_time_down.setText("");
//					adapter.cancelReady();
				txt_home_ready_ready_btn.setText("准备");
				txt_home_ready_ready_btn.setTextColor(Color.parseColor("#ffffff"));
				txt_home_ready_ready_btn.setBackgroundResource(R.drawable.pink_ring_shape);
				countDown(18);
			} else if(msg.what == 0x15){
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this,PlayerRoomActivity.class);
				intent.putExtra("data",(RoomInfoBean)msg.obj);
				startActivityForResult(intent,1);
			}
		}
	};

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

package com.zhy.graph.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
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

import com.zhy.graph.R;
import com.zhy.graph.adapter.HomePlayerGridAdapter;
import com.zhy.graph.app.BaseApplication;
import com.zhy.graph.bean.PlayerBean;
import com.zhy.graph.bean.PlayerInfo;
import com.zhy.graph.bean.RoomInfoBean;
import com.zhy.graph.network.HomeNetHelper;
import com.zhy.graph.network.HomeObserverHepler;
import com.zhy.graph.utils.CusPerference;
import com.zhy.graph.widget.PopDialog;

import net.duohuo.dhroid.ioc.annotation.Inject;
import net.duohuo.dhroid.ioc.annotation.InjectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.onekeyshare.OnekeyShare;

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

	@InjectView(id = R.id.txt_roomer_count_down)
	private TextView txt_roomer_count_down;

	@InjectView(id = R.id.text_player_nickname)
	private TextView text_player_nickname;

	@Inject
	private CusPerference perference;

	private Timer daoTimer;
	private HomePlayerGridAdapter adapter;
	private String TAG = "HomeActivity";
	private PopDialog popDialog = null;
	private boolean roomOwner,onStop,popDismiss,leaveRoom;
	private List<PlayerInfo> dataList;
	private String roomId;
	private HomeNetHelper netUitl = null;
	private TimerTask task = null;
	private boolean clickReady;
	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_home_view);
		perference.load();
		initView();
		if(perference.uid.length()>0){
			BaseApplication.isLogin = true;
			Log.e(TAG,perference.uid);
			netUitl.handleUserCreateFormUsingPOST(perference.uid,"123456",perference.nickName,perference.avatar,"");
		}else{
			String imei = ((TelephonyManager) context.getSystemService(TELEPHONY_SERVICE)).getDeviceId();
			netUitl.handleUserCreateFormUsingPOST(imei,"123456","","","");
		}

	}

	public void initView(){
		dataList = new ArrayList<>();
		left_image.setVisibility(View.VISIBLE);
		left_image.setImageResource(R.drawable.title_bar_self_center_icon);
		right_image.setVisibility(View.VISIBLE);
		right_image.setImageResource(R.drawable.title_bar_share_icon);
		titleTextView.setVisibility(View.VISIBLE);

		netUitl = new HomeNetHelper(HomeActivity.this,netRequest);
		adapter = new HomePlayerGridAdapter(HomeActivity.this,dataList,netUitl,changeUI);
		grid_home.setAdapter(adapter);

	}


	public void initData(RoomInfoBean roomInfoBean){
		dataList.clear();
		titleTextView.setText("房间"+roomInfoBean.getRoomId());

		if(roomInfoBean.getAddedUserList()==null)
			return;
		for (int i = 0; i<roomInfoBean.getAddedUserList().size(); i++){
			PlayerInfo info = new PlayerInfo();
			info.setNickName(roomInfoBean.getAddedUserList().get(i).getNickname());
			if(roomInfoBean.getAddedUserList().get(i).getImage()!=null&&roomInfoBean.getAddedUserList().get(i).getImage().length()>0){
				info.setYouke(false);
			}else{
				info.setYouke(true);
			}
			info.setAvater(roomInfoBean.getAddedUserList().get(i).getImage());
			info.setId(roomInfoBean.getAddedUserList().get(i).getId());
			info.setUsername(roomInfoBean.getAddedUserList().get(i).getUsername());
			if((i+1) == Integer.parseInt(roomInfoBean.getNowUserNum())){
				info.setMe(true);
				text_player_nickname.setText(info.getNickName());
			}

			if(i==0&&Integer.parseInt(roomInfoBean.getNowUserNum())==1){
				roomOwner = true;
			}else{
				roomOwner = false;
			}
			if("Ready".equals(roomInfoBean.getAddedUserList().get(i).getStatus())){
				info.setReady(true);
			}else if("Empty".equals(roomInfoBean.getAddedUserList().get(i).getStatus())){
				info.setReady(false);
			}
			dataList.add(info);
		}
		adapter.notifyDataSetChanged();
		txt_home_ready_ready_btn.setVisibility(View.VISIBLE);
		txt_home_ready_ready_btn.setTextColor(Color.parseColor("#ffffff"));
		txt_home_ready_ready_btn.setBackgroundResource(R.drawable.pink_ring_shape);
		if(!roomOwner){
			txt_home_ready_ready_btn.setText("准备");
			countDown(18);
		}else{
			txt_home_ready_time_down.setVisibility(View.VISIBLE);
			txt_home_ready_time_down.setText("--");
			txt_home_ready_ready_btn.setText("开始");
		}


	}

	public void updateData(PlayerBean playerBean){
		if(playerBean==null)
			return;
		boolean isInit = false;
		for (PlayerInfo info: dataList
				) {
			if(info.getId().equals(playerBean.getId())){
				isInit = true;
				break;
			}
		}
		if(isInit)
			return;
		PlayerInfo info = new PlayerInfo();
		info.setNickName(playerBean.getNickname());
		if("Ready".equals(playerBean.getStatus())){
			info.setReady(true);
		}else if("Empty".equals(playerBean.getStatus())){
			info.setReady(false);
		}
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
		if(dataList.size()==1){
			if(!roomOwner&&daoTimer!=null){
				daoTimer.cancel();
			}
			clickReady = false;
			roomOwner = true;
			dataList.get(0).setReady(false);
			dataList.get(0).setMe(true);
			txt_home_ready_ready_btn.setVisibility(View.VISIBLE);
			txt_home_ready_ready_btn.setTextColor(Color.parseColor("#ffffff"));
			txt_home_ready_ready_btn.setBackgroundResource(R.drawable.pink_ring_shape);
			txt_home_ready_time_down.setVisibility(View.VISIBLE);
			txt_home_ready_time_down.setText("--");
			txt_home_ready_ready_btn.setText("开始");
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

	public boolean haveStartAble(){

		boolean startAble = true;
		if(dataList.size()<3){
			startAble = false;
			Toast.makeText(HomeActivity.this,"房间人数不够^~^",Toast.LENGTH_SHORT).show();
			return startAble;
		}
		for (int i = 1; i < dataList.size(); i++) {
			if(!dataList.get(i).isReady()) {
				startAble = false;
				Toast.makeText(HomeActivity.this,"有玩家还没准备,请您稍等^~^",Toast.LENGTH_SHORT).show();
				break;
			}
		}
		return startAble;
	}


	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {

			case R.id.image_title_left:
				if(!roomOwner&&daoTimer!=null){
					daoTimer.cancel();
				}
				netUitl.leaveRoomUsingGET(BaseApplication.username,3,null);

				break;

			case R.id.txt_home_create_player_room:
				if(!roomOwner&&daoTimer!=null){
					daoTimer.cancel();
				}
				netUitl.leaveRoomUsingGET(BaseApplication.username,1,null);
				break;

			case R.id.txt_home_ready_ready_btn:

				if("开始".equals(txt_home_ready_ready_btn.getText().toString())){//是房主
					if(haveStartAble()){
						netUitl.gameStartUsingGET(BaseApplication.username,roomId);
					}

				}else if("准备".equals(txt_home_ready_ready_btn.getText().toString())){
					clickReady = true;
//					daoTimer.cancel();
					netUitl.userReadyUsingGET(BaseApplication.username,roomId);
				}else if("已准备".equals(txt_home_ready_ready_btn.getText().toString())){
//					daoTimer.cancel();
//					netUitl.userReadyCancelUsingGET(BaseApplication.username,roomId);
				}

				break;

			case R.id.txt_home_join_player_room:
				if(!roomOwner&&daoTimer!=null){
					daoTimer.cancel();
				}
				popDialog = new PopDialog(HomeActivity.this,R.style.inputDialog).setGravity(Gravity.CENTER).setResources(R.layout.pop_join_play_room);
				((EditText)popDialog.findViewById(R.id.edit_input_room_id)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
					@Override
					public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
						if(actionId == EditorInfo.IME_ACTION_SEND){

							String roomId = ((EditText)popDialog.findViewById(R.id.edit_input_room_id)).getText().toString().trim();
							netUitl.leaveRoomUsingGET(BaseApplication.username,2,roomId);
						}
						return false;
					}
				});
				popDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
					@Override
					public void onDismiss(DialogInterface dialog) {
						if(!popDismiss){
							netUitl.getRandomRoomUsingGET(BaseApplication.username);
						}

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

				showShare();

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
		popDismiss = false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 1){
			onStop = false;
			if(BaseApplication.isLogin){//已经登录过了
				netUitl.handleUserCreateFormUsingPOST(perference.uid,"123456",perference.nickName,perference.avatar,"");
			}else{
				netUitl.getRandomRoomUsingGET(BaseApplication.username);
			}

		}else if(requestCode == 2){//游戏退出来后回调
			onStop = false;
			if(data!=null){
				netUitl.leaveRoomUsingGET(BaseApplication.username,0,data.getStringExtra("roomId"));
			}

		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		onStop = true;
		if(daoTimer != null){
			daoTimer.cancel();
		}

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();


	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			onStop = true;
			netUitl.leaveRoomUsingGET(BaseApplication.username,0,null);
			if(BaseApplication.obserUitl!=null&&BaseApplication.obserUitl.getmStompClient()!=null){
				BaseApplication.obserUitl.getmStompClient().disconnect();
			}
			finish();
			return false;
		}else {
			return super.onKeyDown(keyCode, event);
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
					if(clickReady){
						countDown(18);
					}else{
						netUitl.leaveRoomUsingGET(BaseApplication.username,0,null);
					}

				}else{
					txt_home_ready_time_down.setText(msg.arg1+"s");
				}
			} else if(msg.what == 0x11){
				Log.e(TAG, "Stomp reconnection opened");
			} else if(msg.what == 0x12){
				updateData((PlayerBean) msg.obj);
			} else if(msg.what == 0x13){
				logout((PlayerBean) msg.obj);
			} else if(msg.what == 0x14){
				toReady((PlayerBean) msg.obj,true);
			} else if(msg.what == 0x15){//房主倒计时开始
				if(!roomOwner){
					daoTimer.cancel();
					txt_home_ready_time_down.setVisibility(View.GONE);
					dataList.get(0).setCountDown(8);
					dataList.get(0).setShowCount(true);
					adapter.countDown();
				}else{
					countDown(8);
				}

			}else if(msg.what == 0x16){
				toReady((PlayerBean) msg.obj,false);
			}else if(msg.what == 0x18){
//				BaseApplication.obserUitl.getmStompClient().disconnect();
				if(!roomOwner){
					daoTimer.cancel();
				}
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this, PlayerRoomActivity.class);
				intent.putExtra("roomInfoData", (RoomInfoBean) msg.obj);
				intent.putExtra("roomOwner", roomOwner);
				intent.putExtra("roomType",0);
				startActivityForResult(intent, 2);
			}else if (msg.what == 0x100) {
				if (msg.arg1 == 0) {
					adapter.getDataList().get(0).setShowCount(false);
					if(!roomOwner){
						countDown(18);
					}
				}
				adapter.notifyDataSetChanged();
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
				if(BaseApplication.obserUitl == null){
					BaseApplication.obserUitl = new HomeObserverHepler(BaseApplication.username,roomInfo.getRoomId(),changeUI);
					BaseApplication.obserUitl.start();
				}else{
					BaseApplication.obserUitl.setChangeUI(changeUI);
					BaseApplication.obserUitl.setRoomId(roomInfo.getRoomId());
					BaseApplication.obserUitl.getmStompClient().disconnect();
					BaseApplication.obserUitl.run();
				}
			} else if(msg.what == 0x11){
				if(!onStop){//非退出app,倒计时到了自动退出房间
					leaveRoom = true;
					netUitl.getRandomRoomUsingGET(BaseApplication.username);
				}
			} else if(msg.what == 0x12){
				txt_home_ready_time_down.setVisibility(View.VISIBLE);
//					adapter.clickReady();
				txt_home_ready_ready_btn.setText("已准备");
				txt_home_ready_ready_btn.setTextColor(Color.parseColor("#ffffff"));
				txt_home_ready_ready_btn.setBackgroundResource(R.drawable.btn_shape_ready_gray);
//				daoTimer.cancel();
			} else if(msg.what == 0x13){
				txt_home_ready_time_down.setVisibility(View.VISIBLE);
//					adapter.cancelReady();
				txt_home_ready_ready_btn.setText("准备");
				txt_home_ready_ready_btn.setTextColor(Color.parseColor("#ffffff"));
				txt_home_ready_ready_btn.setBackgroundResource(R.drawable.pink_ring_shape);
				countDown(18);
			} else if(msg.what == 0x14){
				((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE)).toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
				if(popDialog!=null&&popDialog.isShowing()) {
					popDialog.dismiss();
				}
				popDismiss = true;
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this,PlayerRoomActivity.class);
				intent.putExtra("roomInfoData",(RoomInfoBean)msg.obj);
				intent.putExtra("roomType",1);
				startActivityForResult(intent,1);
			} else if(msg.what == 0x15){
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this,PlayerRoomActivity.class);
				intent.putExtra("roomInfoData",(RoomInfoBean)msg.obj);
				intent.putExtra("roomType",1);
				startActivityForResult(intent,1);
			} else if(msg.what == 0x16){
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this,SelfCenterActivity.class);
				startActivityForResult(intent,1);
			} else if(msg.what == 0x17){
				(popDialog.findViewById(R.id.txt_warn_room_not_exist)).setVisibility(View.VISIBLE);
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

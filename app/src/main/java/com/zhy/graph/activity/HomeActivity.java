package com.zhy.graph.activity;

import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.zhy.graph.adapter.HomePlayerGridAdapter;
import com.zhy.graph.adapter.QuestionsSelectListAdapter;
import com.zhy.graph.app.BaseApplication;
import com.zhy.graph.bean.PlayerBean;
import com.zhy.graph.bean.PlayerInfo;
import com.zhy.graph.bean.QuestionInfo;
import com.zhy.graph.bean.RoomInfoBean;
import com.zhy.graph.network.HomeNetHelper;
import com.zhy.graph.network.HomeObserverHepler;
import com.zhy.graph.widget.PopDialog;

import net.duohuo.dhroid.ioc.annotation.InjectView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import cn.sharesdk.onekeyshare.OnekeyShare;
import gra.zhy.com.graph.R;

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
	private String TAG = "HomeActivity";
	private PopDialog popDialog = null;
	private PopDialog questionDialog = null;
	private boolean roomOwner,onStop,popDismiss;
	private List<PlayerInfo> dataList;
	private String roomId;
	private HomeNetHelper netUitl = null;
	private TimerTask task = null;
	private QuestionsSelectListAdapter questionsAdapter = null;
	private ListView questionListView;
	private RoomInfoBean playerInfo;
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
		netUitl = new HomeNetHelper(HomeActivity.this,netRequest);
	}


	public void initData(RoomInfoBean roomInfoBean){
		dataList.clear();
		titleTextView.setText("房间"+roomInfoBean.getRoomId());

		if(roomInfoBean.getAddedUserList()==null)
			return;
		for (int i = 0; i<roomInfoBean.getAddedUserList().size(); i++){
			PlayerInfo info = new PlayerInfo();
			info.setNickName(roomInfoBean.getAddedUserList().get(i).getNickname());
			info.setYouke(true);
			info.setId(roomInfoBean.getAddedUserList().get(i).getId());
			info.setUsername(roomInfoBean.getAddedUserList().get(i).getUsername());
			if((i+1) == Integer.parseInt(roomInfoBean.getNowUserNum()))
				info.setMe(true);
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
					netUitl.gameStartUsingGET(BaseApplication.username,roomId);
				}else if("准备".equals(txt_home_ready_ready_btn.getText().toString())){
					daoTimer.cancel();
					netUitl.userReadyUsingGET(BaseApplication.username,roomId);
				}else if("已准备".equals(txt_home_ready_ready_btn.getText().toString())){
					daoTimer.cancel();
					netUitl.userReadyCancelUsingGET(BaseApplication.username,roomId);
				}

				break;

			case R.id.txt_home_join_player_room:
				if(!roomOwner&&daoTimer!=null){
					daoTimer.cancel();
				}
				popDialog = PopDialog.createDialog(HomeActivity.this, R.layout.pop_join_play_room, Gravity.CENTER,R.style.inputDialog);
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
		popDismiss = false;
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if(requestCode == 1){
			onStop = false;
			netUitl.getRandomRoomUsingGET(BaseApplication.username);
		}else if(requestCode == 2){//游戏退出来后回调
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
					netUitl.leaveRoomUsingGET(BaseApplication.username,0,null);
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
			} else if(msg.what == 0x16){
				toReady((PlayerBean) msg.obj,false);
			} else if(msg.what == 0x18){
				if(roomOwner) {
					netUitl.questionListUsingGET((RoomInfoBean) msg.obj, 4);
				}else {
					BaseApplication.obserUitl.getmStompClient().disconnect();
					Intent intent = new Intent();
					intent.setClass(HomeActivity.this, PlayerRoomActivity.class);
					intent.putExtra("roomInfoData", (RoomInfoBean) msg.obj);
					intent.putExtra("roomOwner", roomOwner);
					intent.putExtra("roomType",0);
					startActivityForResult(intent, 2);
				}
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
			} else if(msg.what == 0x18){
				final List<QuestionInfo> questionList = (List<QuestionInfo>)msg.obj;
				playerInfo = (RoomInfoBean) msg.getData().get("data");
				questionsAdapter = new QuestionsSelectListAdapter(HomeActivity.this,questionList);
				questionDialog = PopDialog.createDialog(HomeActivity.this, R.layout.pop_select_guess_word, Gravity.CENTER, R.style.CustomProgressDialog);
				Window win = questionDialog.getWindow();
				win.getDecorView().setPadding(0, 0, 0, 0);
				WindowManager.LayoutParams lp = win.getAttributes();
				lp.width = WindowManager.LayoutParams.FILL_PARENT;
				lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
				win.setAttributes(lp);
				questionDialog.setCanceledOnTouchOutside(false);
				questionListView = (ListView)questionDialog.findViewById(R.id.list_guess_word_pop);
				questionListView.setAdapter(questionsAdapter);
				questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						netUitl.questionOkUsingGE(roomId,questionList.get(position).getId());
					}
				});
				if(!questionDialog.isShowing()) {
					questionDialog.show();
				}
			} else if(msg.what == 0x19){
				if(questionDialog.isShowing()) {
					questionDialog.dismiss();
				}
				Intent intent = new Intent();
				intent.setClass(HomeActivity.this,PlayerRoomActivity.class);
				intent.putExtra("roomInfoData",playerInfo);
				intent.putExtra("questionData",(QuestionInfo)msg.obj);
				intent.putExtra("roomOwner",roomOwner);
				intent.putExtra("roomType",0);
				startActivityForResult(intent,2);
			} else if(msg.what == 20){
				txt_home_ready_time_down.setText("取题中...");
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

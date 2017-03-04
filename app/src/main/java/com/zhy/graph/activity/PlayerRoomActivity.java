package com.zhy.graph.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.zhy.graph.adapter.ChatListAdapter;
import com.zhy.graph.adapter.HomePlayerGridAdapter;
import com.zhy.graph.adapter.PlayerRoomGridAdapter;
import com.zhy.graph.app.BaseApplication;
import com.zhy.graph.bean.ChatInfo;
import com.zhy.graph.bean.CoordinateBean;
import com.zhy.graph.bean.PlayerBean;
import com.zhy.graph.bean.QuestionInfo;
import com.zhy.graph.bean.RoomInfoBean;
import com.zhy.graph.utils.PtsReceiverUtils;
import com.zhy.graph.widget.ChatInputDialog;
import com.zhy.graph.widget.HuaBanView;
import com.zhy.graph.widget.PopDialog;

import net.duohuo.dhroid.ioc.annotation.InjectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import gra.zhy.com.graph.R;

/**
 * Created by yuzhuo on 2017/2/10.
 */
public class PlayerRoomActivity extends BaseAct{

    private float startX, startY, stopX, stopY;
    private HuaBanView hbView;
    private GridView playerroomGrid;

    private int paintWidth;

    private int currentTime;
    private Timer timer,coTimer;

    private String TAG = "PlayRoomActivity";

    private PlayerRoomGridAdapter adapter;

    private ChatListAdapter chatAdapter;

    private PtsReceiverUtils ptsReceiverUtils ;

    private ChatInputDialog chatDialog = null;
    @InjectView(id = R.id.txt_player_room_answer, click = "onClickCallBack")
    private TextView txt_player_room_answer;

    @InjectView(id = R.id.img_to_right_btn, click = "onClickCallBack")
    private ImageView img_to_right_btn;

    @InjectView(id = R.id.img_setting_panel, click = "onClickCallBack")
    private ImageView img_setting_panel;

    @InjectView(id = R.id.img_change_color_black_btn, click = "onClickCallBack")
    private ImageView img_change_color_black_btn;

    @InjectView(id = R.id.img_change_color_white_btn, click = "onClickCallBack")
    private ImageView img_change_color_white_btn;

    @InjectView(id = R.id.img_change_color_red_btn, click = "onClickCallBack")
    private ImageView img_change_color_red_btn;

    @InjectView(id = R.id.img_clear_screen_btn, click = "onClickCallBack")
    private ImageView img_clear_screen_btn;

    @InjectView(id = R.id.img_eraser_btn, click = "onClickCallBack")
    private ImageView img_eraser_btn;

    @InjectView(id = R.id.txt_player_room_send_message, click = "onClickCallBack")
    private TextView txt_player_room_send_message;

    @InjectView(id = R.id.img_close_game, click = "onClickCallBack")
    private ImageView img_close_game;


    @InjectView(id = R.id.rel_room_owner_select_question)
    private RelativeLayout rel_room_owner_select_question;

    @InjectView(id = R.id.txt_room_owner_select_question_name)
    private TextView txt_room_owner_select_question_name;


    @InjectView(id = R.id.viewswitch)
    private ViewSwitcher viewswitch;

    @InjectView(id = R.id.lv_player_room_chat)
    private ListView lv_player_room_chat;

    private ListView pop_player_room_chat;

    @InjectView(id = R.id.txt_play_room_warn_describe)
    private TextView txt_play_room_warn_describe;


    private List<CoordinateBean> paintList;

    private boolean destroyed,connectClosed;

    private PopDialog popDialog = null;

    //抢答框
    private PopDialog answerDialog = null;

    private RoomInfoBean roomInfoBean = null;

    private List<PlayerBean> dataList;

    private List<ChatInfo> chatList;

    private boolean roomOwner;
    private QuestionInfo questionData = null;

    private int roomType ;//区分游客房间和玩家创建的房间 0为游客,1为玩家创建
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing);
        roomInfoBean = (RoomInfoBean) getIntent().getSerializableExtra("roomInfoData");
        roomOwner = getIntent().getBooleanExtra("roomOwner",false);
        questionData = (QuestionInfo) getIntent().getSerializableExtra("questionData");
        roomType = getIntent().getIntExtra("roomType",0);
        if(roomInfoBean!=null){
            initView();
            BaseApplication.obserUitl.setRoomId(roomInfoBean.getRoomId());
            BaseApplication.obserUitl.setChangeUI(changeUI);
            BaseApplication.obserUitl.run();
        }
        if(questionData!=null){
            txt_play_room_warn_describe.setText(questionData.getKeyword1());
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        reConnect();
    }

    public void initView() {
        dataList = new ArrayList<>();
        chatList = new ArrayList<>();
        chatDialog = ChatInputDialog.createDialog(PlayerRoomActivity.this,roomInfoBean.getRoomId());
        pop_player_room_chat = (ListView)chatDialog.findViewById(R.id.chat_bottom_player_room_chat);
        paintList = new ArrayList<>();

        hbView = (HuaBanView) findViewById(R.id.huaBanView1);
        if(roomOwner&&roomType == 0) {
            txt_player_room_answer.setVisibility(View.GONE);
            txt_player_room_answer.setText("开始");
            viewswitch.setVisibility(View.VISIBLE);
            hbView.setOnTouchListener(new View.OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    int action = event.getAction();
                    switch (action) {
                        case MotionEvent.ACTION_DOWN:
                            paintList.clear();
                            changeTime();
                            hbView.path.moveTo(event.getX(), event.getY());
                            hbView.pX = event.getX();
                            hbView.pY = event.getY();
                            CoordinateBean startBean = new CoordinateBean();
                            startBean.setX(event.getX());
                            startBean.setY(event.getY());
                            paintList.add(startBean);
                            break;
                        case MotionEvent.ACTION_MOVE:
                            timer.cancel();
                            hbView.path.moveTo(hbView.pX, hbView.pY);

                            hbView.path.quadTo(hbView.pX, hbView.pY, event.getX(), event.getY());
                            hbView.pX = event.getX();
                            hbView.pY = event.getY();
                            CoordinateBean moveBean = new CoordinateBean();
                            moveBean.setX(event.getX());
                            moveBean.setY(event.getY());
                            paintList.add(moveBean);
                            break;
                        case MotionEvent.ACTION_UP:
                            timer.cancel();
                            hbView.cacheCanvas.drawPath(hbView.path, hbView.paint);
                            hbView.path.reset();
                            break;
                    }
                    hbView.invalidate();
                    Log.e(TAG, "/app/room." + roomInfoBean.getRoomId() + "/draw/paint");
                    BaseApplication.obserUitl.getmStompClient().send("/app/room." + roomInfoBean.getRoomId() + "/draw/paint", ptsReceiverUtils.sendPaintData(paintList,hbView.getPaintWidth(),hbView.getPaintColor())).subscribe();
                    return true;
                }
            });
        }else{
            txt_player_room_answer.setText("抢答");
            viewswitch.setVisibility(View.GONE);
            hbView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        playerroomGrid = (GridView) findViewById(R.id.grid_play_room_player);

        adapter = new PlayerRoomGridAdapter(PlayerRoomActivity.this,roomInfoBean.getAddedUserList());

        playerroomGrid.setAdapter(adapter);

        ptsReceiverUtils = new PtsReceiverUtils(PlayerRoomActivity.this,hbView);

        chatAdapter = new ChatListAdapter(PlayerRoomActivity.this,chatList);
        lv_player_room_chat.setAdapter(chatAdapter);

        pop_player_room_chat.setAdapter(chatAdapter);
    }

    @SuppressLint("HandlerLeak")
    Handler changeUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0x11) {//连接后发出handler告诉activity
                // dialog.show();
                Log.e(TAG, "Stomp reconnection opened");
            } else if (msg.what == 0x23) {
                String result = (String)msg.obj;
                ptsReceiverUtils.updateView(result);
            } else if (msg.what == 0x14) {//重新连接stomp
                BaseApplication.obserUitl.run();
            } else if (msg.what == 0x15) {//聊天
                ChatInfo info = (ChatInfo) msg.obj;
                chatAdapter.update(info);
                lv_player_room_chat.setSelection(chatAdapter.getDataList().size()-1);
                pop_player_room_chat.setSelection(chatAdapter.getDataList().size()-1);
            }else if (msg.what == 0x16) {//连接断开
                connectClosed = true;
            }else if (msg.what == 0x19) {//收到房主选题广播
                rel_room_owner_select_question.setVisibility(View.VISIBLE);
                txt_room_owner_select_question_name.setVisibility(View.VISIBLE);
                txt_room_owner_select_question_name.setText(roomInfoBean.getRoomOwnerName());
            }else if (msg.what == 0x20) {
                questionData = (QuestionInfo) msg.obj;
                if(questionData!=null){
                    txt_play_room_warn_describe.setText(questionData.getKeyword1());
                }
                rel_room_owner_select_question.setVisibility(View.GONE);
                txt_room_owner_select_question_name.setVisibility(View.GONE);
                Toast.makeText(PlayerRoomActivity.this,"房主题目选择完成!",Toast.LENGTH_SHORT).show();
            }
        }
    };


    @SuppressLint("UseValueOf")
    private synchronized void handleDraws(Message msg) {
        Bundle bundle = msg.getData();
        String string = bundle.getString("msg").trim();
        String[] str = string.split(",");
        if (str.length == 4) {
            startX = new Float(str[0]);
            startY = new Float(str[1]);
            stopX = new Float(str[2]);
            stopY = new Float(str[3]);
            hbView.path.moveTo(startX, startY);
            hbView.path.quadTo(startX, startY, stopX, stopY);
            hbView.cacheCanvas.drawPath(hbView.path, hbView.paint);
            hbView.path.reset();
            hbView.invalidate();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroyed = true;
        coTimer.cancel();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            Intent intent = new Intent(PlayerRoomActivity.this, HomePlayerGridAdapter.class);
            intent.putExtra("roomId",roomInfoBean.getRoomId());
            setResult(1,intent);
            finish();
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }

    }

    private void changeTime() {
        currentTime = 0;
        timer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if (++currentTime > 1) {
                    Log.d("The Time:", currentTime + "");
                    Message message = new Message();
                    message.what = 0x14;
                    changeUI.sendMessage(message);
                    timer.cancel();
                }
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void reConnect(){

        if(coTimer != null)
            return;
        coTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                if(BaseApplication.obserUitl.getmStompClient().isConnected()){
                    Log.e(TAG,"every 8s later heart beat to test is connected....");
                }else{
                    Log.e(TAG,"every 8s later heart beat to test isn't connected....");
                }


                if(!destroyed&&connectClosed){
                    changeUI.sendEmptyMessage(0x14);
                }
            }
        };
        coTimer.schedule(task, 0, 8000);
    }

    public void answer(){
        answerDialog = PopDialog.createDialog(PlayerRoomActivity.this, R.layout.pop_ask_answer, Gravity.BOTTOM, R.style.inputDialog);
        Window win = answerDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);

        ((EditText)answerDialog.findViewById(R.id.edit_input_answer)).setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    String answer = ((EditText)answerDialog.findViewById(R.id.edit_input_answer)).getText().toString().trim();
                    BaseApplication.obserUitl.getmStompClient().send("/app/room." + roomInfoBean.getRoomId()+"/"+BaseApplication.username + "/draw/answer", answer).subscribe();
                    if(answerDialog.isShowing()) {
                        answerDialog.dismiss();
                    }
                }
                return false;
            }
        });

        if(!answerDialog.isShowing()) {
            answerDialog.show();
        }
    }

    public void saveDraw(){
        popDialog = PopDialog.createDialog(PlayerRoomActivity.this, R.layout.pop_save_or_share_draw, Gravity.CENTER, R.style.CustomProgressDialog);
        Window win = popDialog.getWindow();
        win.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = win.getAttributes();
        lp.width = WindowManager.LayoutParams.FILL_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        win.setAttributes(lp);
        popDialog.setCanceledOnTouchOutside(false);

        ((ImageView)popDialog.findViewById(R.id.img_draw_bitmap)).setImageBitmap(hbView.getBitmap());
        if(!popDialog.isShowing()) {
            popDialog.show();
        }
    }


    public void onClickCallBack(View view) {
        switch (view.getId()) {

            case R.id.txt_player_room_answer:
                answer();
                break;

            case R.id.img_setting_panel:
                Log.e("mmmm","已点击");
                viewswitch.setDisplayedChild(1);
                break;

            case R.id.img_to_right_btn:

                viewswitch.setDisplayedChild(0);

                break;

            case R.id.img_change_color_black_btn:
                hbView.setPaintWidth(5);
                hbView.setColor(Color.parseColor("#000000"));
                viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择黑色画笔",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_change_color_white_btn:
                hbView.setPaintWidth(5);
                hbView.setColor(Color.parseColor("#ffffff"));
                viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择白色画笔",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_change_color_red_btn:
                hbView.setPaintWidth(5);
                hbView.setColor(Color.parseColor("#FF0000"));
                viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择红色画笔",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_eraser_btn:
                hbView.setColor(Color.parseColor("#FFFDED"));
                hbView.setPaintWidth(20);
                viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择橡皮擦",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_clear_screen_btn:
                hbView.clearScreen();
                viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择重绘画板",Toast.LENGTH_SHORT).show();
                break;

            case R.id.txt_player_room_send_message:
                if(chatDialog!=null&&!chatDialog.isShowing()){
                    chatDialog.show();
                }
                break;

            case R.id.img_close_game:
                Intent intent = new Intent(PlayerRoomActivity.this, HomePlayerGridAdapter.class);
                intent.putExtra("roomId",roomInfoBean.getRoomId());
                setResult(1,intent);
                finish();
                break;

            default:
                break;
        }
    }


}

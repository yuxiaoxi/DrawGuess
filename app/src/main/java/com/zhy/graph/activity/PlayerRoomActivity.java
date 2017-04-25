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
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.zhy.graph.R;
import com.zhy.graph.adapter.ChatListAdapter;
import com.zhy.graph.adapter.HomePlayerGridAdapter;
import com.zhy.graph.adapter.PlayerRoomGridAdapter;
import com.zhy.graph.adapter.QuestionsSelectListAdapter;
import com.zhy.graph.app.BaseApplication;
import com.zhy.graph.bean.AnswerInfo;
import com.zhy.graph.bean.ChatInfo;
import com.zhy.graph.bean.CoordinateBean;
import com.zhy.graph.bean.PlayerBean;
import com.zhy.graph.bean.QuestionInfo;
import com.zhy.graph.bean.RoomInfoBean;
import com.zhy.graph.network.PlayerRoomNetHelper;
import com.zhy.graph.utils.PtsReceiverUtils;
import com.zhy.graph.utils.Utils;
import com.zhy.graph.widget.HuaBanView;
import com.zhy.graph.widget.PopDialog;

import net.duohuo.dhroid.ioc.annotation.InjectView;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by yuzhuo on 2017/2/10.
 */
public class PlayerRoomActivity extends BaseAct{

    private float startX, startY, stopX, stopY;
    private HuaBanView hbView;
    private GridView playerroomGrid;

    private Timer timer,coTimer,saveDrawTimer,nextCountTimer;

    private String TAG = "PlayRoomActivity";

    private PlayerRoomGridAdapter adapter;

    private ChatListAdapter chatAdapter;

    private PtsReceiverUtils ptsReceiverUtils ;

    private PopDialog chatDialog = null;
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

    @InjectView(id = R.id.txt_play_room_time)
    private TextView txt_play_room_time;

    @InjectView(id = R.id.lv_player_room_chat)
    private ListView lv_player_room_chat;

    private ListView pop_player_room_chat;

    @InjectView(id = R.id.txt_play_room_warn_describe)
    private TextView txt_play_room_warn_describe;


    private List<CoordinateBean> paintList;

    private boolean destroyed,connectClosed;
    private boolean answerRight;
    private PopDialog popDialog = null;
    private RelativeLayout rel_pop_save_draw;
    //抢答框
    private PopDialog answerDialog = null;

    //倒计时弹框
    private PopDialog countdownDialog = null;
    private LinearLayout linear_save_image;
    private RoomInfoBean roomInfoBean = null;
    private List<ChatInfo> chatList;

    private boolean roomOwner;
    private QuestionInfo questionData = null;

    private int currentDrawer,nowPosition,nowUserNum;

    private int roomType ;//区分游客房间和玩家创建的房间 0为游客,1为玩家创建

    private PlayerRoomNetHelper playerRoomNetHelper = null;
    private QuestionsSelectListAdapter questionsAdapter = null;
    private ListView questionListView;
    private PopDialog questionDialog = null;
    private TimerTask task = null;
    int times ;
    private TextView txt_count_down;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing);
        roomInfoBean = (RoomInfoBean) getIntent().getSerializableExtra("roomInfoData");
        roomOwner = getIntent().getBooleanExtra("roomOwner",false);
        roomType = getIntent().getIntExtra("roomType",0);
        if(roomInfoBean!=null){
            initView();
            roomInfoBean.getAddedUserList().get(0).setDrawNow(true);
            BaseApplication.obserUitl.setRoomId(roomInfoBean.getRoomId());
            BaseApplication.obserUitl.setChangeUI(changeUI);
            currentDrawer = 0;
            nowUserNum = Integer.parseInt(roomInfoBean.getNowUserNum());
            nowPosition = getPosition();
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        reConnect();
    }

    public void initView() {
        playerRoomNetHelper = new PlayerRoomNetHelper(PlayerRoomActivity.this,netRequest);
        chatList = new ArrayList<>();
        chatDialog = new PopDialog(PlayerRoomActivity.this,
                R.style.inputDialog).setGravity(Gravity.BOTTOM).setResources(R.layout.include_chat_bottom_bar);
        final EditText chatEdit = (EditText)chatDialog.findViewById(R.id.edit_user_comment);
        chatDialog.findViewById(R.id.btn_chat_send).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = chatEdit.getText().toString().trim();
                if(content == null ||content.length() ==0)
                    return;

                BaseApplication.obserUitl.getmStompClient().send("/app/room."+roomInfoBean.getRoomId()+"/"+BaseApplication.username+"/talk",content).subscribe();

                chatEdit.setText("");
            }
        });
        pop_player_room_chat = (ListView)chatDialog.findViewById(R.id.chat_bottom_player_room_chat);
        View view_click_pop_dismiss = chatDialog.findViewById(R.id.view_click_pop_dismiss);
        view_click_pop_dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(chatDialog.isShowing()){
                    chatDialog.dismiss();
                }
            }
        });

        paintList = new ArrayList<>();

        hbView = (HuaBanView) findViewById(R.id.huaBanView1);

        playerroomGrid = (GridView) findViewById(R.id.grid_play_room_player);

        adapter = new PlayerRoomGridAdapter(PlayerRoomActivity.this,roomInfoBean.getAddedUserList());

        playerroomGrid.setAdapter(adapter);

        ptsReceiverUtils = new PtsReceiverUtils(PlayerRoomActivity.this,hbView);

        chatAdapter = new ChatListAdapter(PlayerRoomActivity.this,chatList);
        lv_player_room_chat.setAdapter(chatAdapter);

        pop_player_room_chat.setAdapter(chatAdapter);
        initToDrawer();
    }

    public int getPosition(){
        int position = -1;
        for (int i = 0; i < roomInfoBean.getAddedUserList().size(); i++) {
            if(BaseApplication.username.equals(roomInfoBean.getAddedUserList().get(i).getUsername())){
                position = i;
                break;
            }
        }
        return position;

    }

    /**
     * 初始化为画画者
     */
    public void initToDrawer(){
        hbView.clearScreen();

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
                            hbView.path.moveTo(event.getX(), event.getY());
                            hbView.pX = event.getX();
                            hbView.pY = event.getY();
                            CoordinateBean startBean = new CoordinateBean();
                            startBean.setX(event.getX());
                            startBean.setY(event.getY());
                            paintList.add(startBean);
                            break;
                        case MotionEvent.ACTION_MOVE:
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
            playerRoomNetHelper.questionListUsingGET(roomInfoBean,4);
        }else{
            txt_player_room_answer.setVisibility(View.VISIBLE);
            txt_player_room_answer.setText("抢答");
            viewswitch.setVisibility(View.GONE);
            hbView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });
        }
        countDown(79);
    }

    @SuppressLint("HandlerLeak")
    Handler changeUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0x10) {//连接后发出handler告诉activity
                if(msg.arg1 == 0){
                    txt_play_room_time.setText("0");
                    txt_play_room_time.setVisibility(View.INVISIBLE);

                    saveCount(5);

                }else{
                    if(msg.arg1 == 59&&!roomOwner){
                        txt_play_room_warn_describe.setText(questionData.getKeyword2());
                    }
                    txt_play_room_time.setText(msg.arg1+"s");
                }
            } else if (msg.what == 0x11) {//连接后发出handler告诉activity
                // dialog.show();
                Log.e(TAG, "Stomp reconnection opened");
            } else if(msg.what == 0x13){//有玩家退出
                logout((PlayerBean)msg.obj);
            }else if(msg.what == 0x133){
                if(msg.arg1 == 0){
                    Toast.makeText(PlayerRoomActivity.this, "新一轮开始了", Toast.LENGTH_SHORT).show();
                    changeToDrawer();
                }else{
                    txt_count_down.setText(msg.arg1+"s");
                }
            }else if (msg.what == 0x122) {
                if(popDialog != null && popDialog.isShowing()){
                    popDialog.dismiss();
                }
                if(answerDialog != null && answerDialog.isShowing()){
                    answerDialog.dismiss();
                }
                if((currentDrawer+1)%nowUserNum == 0){//一轮结束了
                    countDownDialog();

                }else{
                    changeToDrawer();
                }


            } else if (msg.what == 0x23) {
                String result = (String)msg.obj;
                ptsReceiverUtils.updateView(result);
            } else if (msg.what == 0x14) {//重新连接stomp
                BaseApplication.obserUitl.run();
            } else if (msg.what == 0x15) {//聊天
                ChatInfo info = (ChatInfo) msg.obj;
                String name = info.getNickname()+":";
                info.setNickname(name);
                chatAdapter.update(info);
                lv_player_room_chat.setSelection(chatAdapter.getDataList().size()-1);
                pop_player_room_chat.setSelection(chatAdapter.getDataList().size()-1);
            }else if (msg.what == 0x16) {//连接断开
                connectClosed = true;
            }else if (msg.what == 0x19) {//收到房主选题广播
                if(!roomOwner){
                    rel_room_owner_select_question.setVisibility(View.VISIBLE);
                    txt_room_owner_select_question_name.setVisibility(View.VISIBLE);
                    txt_room_owner_select_question_name.setText(roomInfoBean.getRoomOwnerName());
                }

            }else if (msg.what == 0x20) {
                questionData = (QuestionInfo) msg.obj;
                if(questionData!=null){
                    if(roomOwner)
                        txt_play_room_warn_describe.setText(questionData.getQuestion());
                    else {
                        txt_play_room_warn_describe.setText(questionData.getKeyword1());
                    }
                }
                if(roomOwner)
                    return;
                rel_room_owner_select_question.setVisibility(View.GONE);
                txt_room_owner_select_question_name.setVisibility(View.GONE);
                Toast.makeText(PlayerRoomActivity.this,"房主题目选择完成!",Toast.LENGTH_SHORT).show();
            } else if (msg.what == 0x24) {//回答正确
                for (PlayerBean bean:
                roomInfoBean.getAddedUserList()) {
                    if(bean.getNickname().equals(((AnswerInfo)msg.obj).getNickname())){
                        int score = bean.getCurrentScore()+questionData.getScore();
                        bean.setAnsser(null);
                        bean.setCurrentScore(score);
                        if(BaseApplication.nickname.equals(bean.getNickname())){
                            answerRight = true;
                            txt_player_room_answer.setClickable(false);
                        }
                    }

                }
                Toast.makeText(PlayerRoomActivity.this,"恭喜玩家"+((AnswerInfo)msg.obj).getNickname()+"答对了!",Toast.LENGTH_SHORT).show();
                adapter.setData(roomInfoBean.getAddedUserList());
                adapter.notifyDataSetChanged();
                ChatInfo info = new ChatInfo();
                info.setNickname("法官:");
                info.setContent(((AnswerInfo)msg.obj).getNickname()+"回答正确");
                chatAdapter.update(info);
                lv_player_room_chat.setSelection(chatAdapter.getDataList().size()-1);
            } else if (msg.what == 0x25) {//回答错误
                for (PlayerBean bean:
                        roomInfoBean.getAddedUserList()) {
                    if(bean.getNickname().equals(((AnswerInfo)msg.obj).getNickname())){
                        bean.setAnsser(((AnswerInfo)msg.obj).getAnswer());
                    }
                }
                adapter.setData(roomInfoBean.getAddedUserList());
                adapter.notifyDataSetChanged();
                ChatInfo info = new ChatInfo();
                info.setNickname("法官:");
                info.setContent(((AnswerInfo)msg.obj).getNickname()+"回答错误");
                chatAdapter.update(info);
                lv_player_room_chat.setSelection(chatAdapter.getDataList().size()-1);
            } else if (msg.what == 0x26) {//清除画板
                hbView.clearScreen();
            } else if(msg.what == 0x27){//推送分数结果
                roomInfoBean = (RoomInfoBean) msg.obj;
            }
        }
    };


    public void logout(PlayerBean playerBean){
        if(playerBean==null)
            return;
        for (PlayerBean bean: adapter.getData()
                ) {
            if(bean.getId().equals(playerBean.getId())){
               bean.setStatus("Empty");
                break;
            }
        }
        adapter.notifyDataSetChanged();
    }

    public PlayerBean getMaxScore() {
        PlayerBean maxBean = null;
        for (PlayerBean bean :
                roomInfoBean.getAddedUserList()) {
            if (maxBean == null) {
                maxBean = bean;
            } else {
                if(bean.getCurrentScore()>maxBean.getCurrentScore()){
                    maxBean = bean;
                }
            }
        }
        return maxBean;
    }
    public PlayerBean getMinScore() {
        PlayerBean minBean = null;
        for (PlayerBean bean :
                roomInfoBean.getAddedUserList()) {
            if (minBean == null) {
                minBean = bean;
            } else {
                if(bean.getCurrentScore()<minBean.getCurrentScore()){
                    minBean = bean;
                }
            }
        }
        return minBean;
    }

    private Handler netRequest = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if(msg.what == 0x18){
                final List<QuestionInfo> questionList = (List<QuestionInfo>)msg.obj;
                questionsAdapter = new QuestionsSelectListAdapter(PlayerRoomActivity.this,questionList);
                questionDialog = new PopDialog(PlayerRoomActivity.this,R.style.CustomProgressDialog).setGravity(Gravity.CENTER).setResources(R.layout.pop_select_guess_word);
                questionDialog.setCanceledOnTouchOutside(false);
                questionListView = (ListView)questionDialog.findViewById(R.id.list_guess_word_pop);
                questionListView.setAdapter(questionsAdapter);
                questionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        playerRoomNetHelper.questionOkUsingGE(roomInfoBean.getRoomId(),questionList.get(position).getId());
                    }
                });
                if(!questionDialog.isShowing()) {
                    questionDialog.show();
                }
            }else if(msg.what == 0x19){
                questionData = (QuestionInfo) msg.obj;
                if(questionDialog.isShowing()) {
                    questionDialog.dismiss();
                }
            }
        }
    };

    private void countDown(int countTime){
        txt_play_room_time.setVisibility(View.VISIBLE);
        txt_play_room_time.setText(countTime+"s");
        timer = new Timer();
        task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = 0x10;
                msg.arg1 = Integer.parseInt(txt_play_room_time.getText().toString().split("s")[0])-1;
                if(msg.arg1 == 0){
                    timer.cancel();
                }
                changeUI.sendMessage(msg);
            }
        };
        timer.schedule(task, 0, 1000);
    }

    private void countDownNext(int countTime){
        txt_count_down.setText(countTime+"s");
        nextCountTimer = new Timer();
        TimerTask task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = 0x133;
                msg.arg1 = Integer.parseInt(txt_count_down.getText().toString().split("s")[0])-1;
                if(msg.arg1 == 0){
                    if(countdownDialog.isShowing()){
                        countdownDialog.dismiss();
                    }
                    nextCountTimer.cancel();
                }
                changeUI.sendMessage(msg);
            }
        };
        nextCountTimer.schedule(task, 0, 1000);
    }

    private void saveCount(int countTime){
        saveDrawTimer = new Timer();
        times = countTime;
        if(questionData!=null){
            saveDraw(questionData.getQuestion());
        }else{
            saveDraw("");
        }

        TimerTask task = new TimerTask() {
            public void run() {
                times --;

                if(times == 0){
                    saveDrawTimer.cancel();
                    Message msg = new Message();
                    msg.what = 0x122;
                    changeUI.sendMessage(msg);
                }

            }
        };
        saveDrawTimer.schedule(task, 0, 1000);
    }


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
        if(popDialog != null && popDialog.isShowing()){
            popDialog.dismiss();
        }
        if(answerDialog != null && answerDialog.isShowing()){
            answerDialog.dismiss();
        }
        if(countdownDialog != null && countdownDialog.isShowing()){
            countdownDialog.dismiss();
        }
        if(questionDialog != null && questionDialog.isShowing()){
            questionDialog.dismiss();
        }
        if(timer != null)
            timer.cancel();
        if(saveDrawTimer!= null){
            saveDrawTimer.cancel();
        }
        if(nextCountTimer!=null){
            nextCountTimer.cancel();
        }
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
        answerDialog = new PopDialog(PlayerRoomActivity.this,R.style.inputDialog).setGravity(Gravity.BOTTOM).setResources(R.layout.pop_ask_answer);
        answerDialog.setCanceledOnTouchOutside(true);
        final EditText answerEdit = (EditText)answerDialog.findViewById(R.id.edit_input_answer);
        answerEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if(actionId == EditorInfo.IME_ACTION_SEND){
                    String answer = answerEdit.getText().toString().trim();
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

    public void countDownDialog(){
        countdownDialog = new PopDialog(PlayerRoomActivity.this,R.style.CustomProgressDialog).setGravity(Gravity.CENTER).setResources(R.layout.pop_player_batter_performance);
        txt_count_down  = (TextView)countdownDialog.findViewById(R.id.txt_count_down);
        ((TextView)countdownDialog.findViewById(R.id.txt_winer_score)).setText(getMaxScore().getCurrentScore()+"");
        ((TextView)countdownDialog.findViewById(R.id.txt_loser_score)).setText(getMinScore().getCurrentScore()+"");
        countdownDialog.setCanceledOnTouchOutside(false);
        if(!countdownDialog.isShowing()) {
            countdownDialog.show();
        }
        countDownNext(5);
    }

    public void saveDraw(final String qustion){
        popDialog = new PopDialog(PlayerRoomActivity.this,R.style.CustomProgressDialog).setGravity(Gravity.CENTER).setResources(R.layout.pop_save_or_share_draw);
        popDialog.setCanceledOnTouchOutside(true);
        rel_pop_save_draw = (RelativeLayout)popDialog.findViewById(R.id.rel_pop_save_draw);
        rel_pop_save_draw.setDrawingCacheEnabled(true);
        rel_pop_save_draw.buildDrawingCache();
        ((ImageView)popDialog.findViewById(R.id.img_draw_bitmap)).setImageBitmap(hbView.getBitmap());
        ((TextView)popDialog.findViewById(R.id.txt_select_word_describe)).setText(qustion);
        linear_save_image = (LinearLayout) popDialog.findViewById(R.id.linear_save_image);
        popDialog.findViewById(R.id.linear_save_image).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_save_image.setVisibility(View.GONE);
                Utils.saveImageToGallery(PlayerRoomActivity.this,rel_pop_save_draw.getDrawingCache());
            }
        });
        if(!popDialog.isShowing()) {
            popDialog.show();
        }
    }


    public void changeToDrawer(){
        txt_player_room_answer.setClickable(true);
        answerRight = false;
        if(questionDialog!=null &&questionDialog.isShowing()) {
            questionDialog.dismiss();
        }
        int playerCount = 0;
        for (PlayerBean bean :
                roomInfoBean.getAddedUserList()) {
            bean.setAnsser(null);
            if(!"Empty".equals(bean.getStatus())){
                playerCount++;
            }
        }
        if(playerCount<2){
            if(timer != null)
                timer.cancel();
            Toast.makeText(PlayerRoomActivity.this,"在线玩家已少于两个,请退出重新开始游戏^~^",Toast.LENGTH_SHORT).show();
            return;
        }
        roomInfoBean.getAddedUserList().get(currentDrawer%nowUserNum).setDrawNow(false);

        currentDrawer ++;

        while("Empty".equals(roomInfoBean.getAddedUserList().get(currentDrawer%nowUserNum).getStatus())){//离线了
            currentDrawer ++;
        }
        roomInfoBean.getAddedUserList().get(currentDrawer%nowUserNum).setDrawNow(true);
        if(nowPosition == currentDrawer%nowUserNum){//角色变成画画者
            roomOwner = true;
        }else{
            roomOwner = false;
        }
        initToDrawer();
        hbView.setPaintWidth(10);
        hbView.setColor(Color.parseColor("#000000"));
        adapter.setData(roomInfoBean.getAddedUserList());
        adapter.notifyDataSetChanged();
    }


    public void onClickCallBack(View view) {
        switch (view.getId()) {

            case R.id.txt_player_room_answer:
                if(answerRight){
                    Toast.makeText(PlayerRoomActivity.this,"您已回答正确!不需要重复抢答了^~^",Toast.LENGTH_SHORT).show();
                    return;
                }
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
                hbView.setPaintWidth(10);
                hbView.setColor(Color.parseColor("#000000"));
                // viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择黑色画笔",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_change_color_white_btn:
                hbView.setPaintWidth(10);
                hbView.setColor(Color.parseColor("#ffffff"));
                // viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择白色画笔",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_change_color_red_btn:
                hbView.setPaintWidth(10);
                hbView.setColor(Color.parseColor("#FF0000"));
                // viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择红色画笔",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_eraser_btn:
                hbView.setColor(Color.parseColor("#FFFDED"));
                hbView.setPaintWidth(30);
                // viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择橡皮擦",Toast.LENGTH_SHORT).show();
                break;

            case R.id.img_clear_screen_btn:
                hbView.clearScreen();
                // viewswitch.setDisplayedChild(0);
                Toast.makeText(PlayerRoomActivity.this,"已选择重绘画板",Toast.LENGTH_SHORT).show();
                BaseApplication.obserUitl.getmStompClient().send("/app/room."+roomInfoBean.getRoomId()+"/draw/paint/clear","").subscribe();
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

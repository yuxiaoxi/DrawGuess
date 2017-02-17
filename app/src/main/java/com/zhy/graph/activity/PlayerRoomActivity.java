package com.zhy.graph.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.zhy.graph.adapter.ChatListAdapter;
import com.zhy.graph.adapter.PlayerRoomGridAdapter;
import com.zhy.graph.bean.ChatInfo;
import com.zhy.graph.bean.CoordinateBean;
import com.zhy.graph.bean.PlayerRoomInfo;
import com.zhy.graph.utils.PtsReceiverUtils;
import com.zhy.graph.utils.Utils;
import com.zhy.graph.widget.ChatInputDialog;
import com.zhy.graph.widget.HuaBanView;
import com.zhy.graph.widget.PopDialog;

import net.duohuo.dhroid.ioc.annotation.InjectView;

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

/**
 * Created by yuzhuo on 2017/2/10.
 */
public class PlayerRoomActivity extends BaseAct{

    private float startX, startY, stopX, stopY;
    // private int RandomID = new Random().nextInt(10);
    private HuaBanView hbView;
    private GridView playerroomGrid;

    private AlertDialog dialog;
    private View dialogView;
    private TextView shouWidth;
    private SeekBar widthSb;
    private int paintWidth;

    private int currentTime;
    private Timer timer,coTimer;

    private StompClient mStompClient;
    private String TAG = "PlayRoomActivity";

    private PlayerRoomGridAdapter adapter;

    private ChatListAdapter chatAdapter;

    private PtsReceiverUtils ptsReceiverUtils ;

    private ChatInputDialog chatDialog = null;
    @InjectView(id = R.id.txt_player_room_answer, click = "onClickCallBack")
    private TextView txt_player_room_answer;

    @InjectView(id = R.id.txt_player_huabi_setting, click = "onClickCallBack")
    private TextView txt_player_huabi_setting;

    @InjectView(id = R.id.txt_player_clear_screen, click = "onClickCallBack")
    private TextView txt_player_clear_screen;

    @InjectView(id = R.id.txt_player_room_send_message, click = "onClickCallBack")
    private TextView txt_player_room_send_message;


    @InjectView(id = R.id.lv_player_room_chat)
    private ListView lv_player_room_chat;

    private List<CoordinateBean> paintList;

    private Mythread mythread = null;

    private boolean destroyed,connectClosed;
    private PopDialog popDialog = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawing);
        initView();
        mythread = new Mythread();
        mythread.start();
    }


    @Override
    protected void onResume() {
        super.onResume();
        reConnect();
    }

    public void initView() {

        chatDialog = ChatInputDialog.createDialog(PlayerRoomActivity.this,changeUI);
        paintList = new ArrayList<>();

        hbView = (HuaBanView) findViewById(R.id.huaBanView1);

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
                mStompClient.send("/app/draw/paint", ptsReceiverUtils.sendPaintData(paintList)).subscribe();
                return true;
            }
        });
        dialogView = getLayoutInflater().inflate(R.layout.dialog_width_set, null);
        shouWidth = (TextView) dialogView.findViewById(R.id.textView1);
        widthSb = (SeekBar) dialogView.findViewById(R.id.seekBar1);
        widthSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                shouWidth.setText("��ǰѡ�п�ȣ�" + (progress + 1));
                paintWidth = progress + 1;
            }
        });
        dialog = new AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_info).setTitle("���û��ʿ��")
                .setView(dialogView) //
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        hbView.setPaintWidth(paintWidth);
                    }
                }).setNegativeButton("取消", null).create();

        playerroomGrid = (GridView) findViewById(R.id.grid_play_room_player);

        adapter = new PlayerRoomGridAdapter(PlayerRoomActivity.this,getData());

        playerroomGrid.setAdapter(adapter);

        ptsReceiverUtils = new PtsReceiverUtils(PlayerRoomActivity.this,hbView);

        chatAdapter = new ChatListAdapter(PlayerRoomActivity.this,getChatList());
        lv_player_room_chat.setAdapter(chatAdapter);
    }

    public List<PlayerRoomInfo> getData(){
        List<PlayerRoomInfo> list = new ArrayList<>();

        for (int i = 0 ;i<6;i++){
            PlayerRoomInfo bean = new PlayerRoomInfo();
            bean.setGuessWords("逗逼咯1"+i);
            bean.setScore(i*2+"");

            list.add(bean);
        }

        return list;

    }

    public List<ChatInfo> getChatList(){
        List<ChatInfo> list = new ArrayList<>();

        for (int i = 0 ;i<4;i++){
            ChatInfo bean = new ChatInfo();
            bean.setNickName("逗逼:");
            bean.setContent("你是猴子派来的吧!"+i);

            list.add(bean);
        }

        return list;
    }

    @SuppressLint("HandlerLeak")
    Handler changeUI = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (msg.what == 0x11) {
                handleDraws(msg);
            } else if (msg.what == 0x12) {
                // dialog.show();
                openOptionsMenu();
            } else if (msg.what == 0x13) {
                String result = (String)msg.obj;
                ptsReceiverUtils.updateView(result);
            } else if (msg.what == 0x14) {
                mythread.run();
            } else if (msg.what == 0x15) {
                ChatInfo info = (ChatInfo) msg.obj;
                chatAdapter.update(info);
                lv_player_room_chat.setSelection(chatAdapter.getDataList().size()-1);
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
                        Message msg = new Message();
                        msg.obj = stompMessage.getPayload();
                        msg.what = 0x13;
                        changeUI.sendMessage(msg);
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
                                break;

                            case ERROR:
                                Log.e(TAG, "Error", lifecycleEvent.getException());
                                break;

                            case CLOSED:
                                connectClosed = true;
                                Log.e(TAG, "Stomp connection closed");
                                break;
                        }
                    }
                });

                mStompClient.connect();
                Log.i(TAG, "end of program,mStompClient status:" + mStompClient.isConnected());

//            }

        } catch (Exception e) {
            e.printStackTrace();
            Log.i("IOException", "IOException");
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
        mStompClient.disconnect();
        coTimer.cancel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        SubMenu colorSm = menu.addSubMenu(1, 1, 1, "ѡ�񻭱���ɫ");
        colorSm.add(2, 200, 200, "��ɫ");
        colorSm.add(2, 210, 210, "��ɫ");
        colorSm.add(2, 220, 220, "��ɫ");
        colorSm.add(2, 230, 230, "��ɫ");
        colorSm.add(2, 240, 240, "��ɫ");
        colorSm.add(2, 250, 250, "��ɫ");
        menu.add(1, 2, 2, "���û��ʴ�ϸ");
        SubMenu widthSm = menu.addSubMenu(1, 3, 3, "���û�����ʽ");
        widthSm.add(3, 300, 300, "��״����");
        widthSm.add(3, 301, 301, "��仭��");
        // menu.add(1, 4, 4, "��ջ���");
        menu.add(1, 5, 5, "���滭��");
        menu.add(1, 6, 6, "�˳�Ӧ��");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int index = item.getItemId();
        switch (index) {
            case 200:
                hbView.setColor(Color.RED);
                break;
            case 210:
                hbView.setColor(Color.GREEN);
                break;
            case 220:
                hbView.setColor(Color.BLUE);
                break;
            case 230:
                hbView.setColor(Color.MAGENTA);
                break;
            case 240:
                hbView.setColor(Color.YELLOW);
                break;
            case 250:
                hbView.setColor(Color.BLACK);
                break;
            case 2:
                dialog.show();
                break;
            case 300:
                hbView.setStyle(HuaBanView.PEN);
                break;
            case 301:
                hbView.setStyle(HuaBanView.PAIL);
                break;
            case 4:
                hbView.clearScreen();
                break;
            case 5: { //
                Utils.saveBitmap(null, hbView.getBitmap(), "test");

                Message msg = new Message();
                msg.what = 0x13;
                changeUI.sendMessage(msg);
            }
            break;
            case 6:
                finish();
                break;
        }
        return true;
    }

    @Override
    public void openOptionsMenu() {
        super.openOptionsMenu();
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

                Log.e(TAG,"every 5s later heart beat to test is connected....");

                if(!destroyed&&connectClosed){
                    changeUI.sendEmptyMessage(0x14);
                }
            }
        };
        coTimer.schedule(task, 0, 5000);
    }


    public void onClickCallBack(View view) {
        switch (view.getId()) {

            case R.id.txt_player_room_answer:
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
                break;

            case R.id.txt_player_clear_screen:
                Log.e("mmmm","已点击");
//                hbView.clearScreen();
                hbView.setColor(Color.parseColor("#ff0000"));
                hbView.setPaintWidth(10);
                txt_player_clear_screen.setVisibility(View.GONE);
                txt_player_huabi_setting.setVisibility(View.VISIBLE);
                break;

            case R.id.txt_player_huabi_setting:
                hbView.setColor(Color.parseColor("#FFFDED"));
                hbView.setPaintWidth(20);
                txt_player_huabi_setting.setVisibility(View.GONE);
                txt_player_clear_screen.setVisibility(View.VISIBLE);

                break;

            case R.id.txt_player_room_send_message:
                if(chatDialog!=null&&!chatDialog.isShowing()){
                    chatDialog.show();
                }
                break;

            default:
                break;
        }
    }


}

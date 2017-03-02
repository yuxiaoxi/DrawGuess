package com.zhy.graph.network;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.zhy.graph.bean.PlayerBean;
import com.zhy.graph.bean.RoomInfoBean;

import net.duohuo.dhroid.net.Response;

import org.java_websocket.WebSocket;

import java.util.HashMap;
import java.util.Map;

import rx.Observer;
import rx.Subscriber;
import ua.naiksoftware.stomp.LifecycleEvent;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.client.StompClient;
import ua.naiksoftware.stomp.client.StompMessage;

/**
 * Created by yuzhuo on 2017/2/28.
 */
public class HomeObserverHepler extends Thread{

    private final String TAG = "HomeObserverHepler";
    private String userName;
    private String roomId;
    private StompClient mStompClient;
    private Handler changeUI;
    public HomeObserverHepler(String username, String roomId, StompClient stompClient, Handler handler){
        this.userName = username;
        this.roomId = roomId;
        this.mStompClient = stompClient;
        this.changeUI = handler;
    }
    public void setRoomId(String roomId) {
        this.roomId = roomId;
    }

    @Override
    public void run() {
        conn();

    }

    private void conn() {
        try {

            if(mStompClient!=null&&mStompClient.isConnected())
                return;
            Map<String, String> connectHttpHeaders = new HashMap<>();
            connectHttpHeaders.put("user-name", userName);
            mStompClient = Stomp.over(WebSocket.class, "ws://112.74.174.121:8080/ws/websocket", connectHttpHeaders);

            mStompClient.topic("/topic/room."+roomId+"/in").subscribe(new Subscriber<StompMessage>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "/topic/roomin/ onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "/topic/roomin/ onError: " + e.getMessage());
                }

                @Override
                public void onNext(StompMessage stompMessage) {
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
                    msg.obj = response.model(PlayerBean.class);
                    msg.what = 0x12;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "in onNext: " + stompMessage.getPayload());
                }

            });

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
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
                    msg.obj = response.model(PlayerBean.class);
                    msg.what = 0x13;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "out onNext: " + stompMessage.getPayload());
                }

            });

            mStompClient.topic("/topic/room."+roomId+"/ready").subscribe(new Subscriber<StompMessage>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "/topic/user.ready/ onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "/topic/user.ready/ onError: " + e.getMessage());
                }

                @Override
                public void onNext(StompMessage stompMessage) {
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
                    msg.obj = response.model(PlayerBean.class);
                    msg.what = 0x14;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "ready onNext: " + stompMessage.getPayload());
                }

            });

            mStompClient.topic("/topic/room."+roomId+"/readycancel").subscribe(new Subscriber<StompMessage>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "/topic/readycancel/ onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "/topic/readycancel/ onError: " + e.getMessage());
                }

                @Override
                public void onNext(StompMessage stompMessage) {
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
                    msg.obj = response.model(PlayerBean.class);
                    msg.what = 0x16;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "readycancel onNext: " + stompMessage.getPayload());
                }

            });

            mStompClient.topic("/topic/room."+roomId+"/owner.countdown").subscribe(new Subscriber<StompMessage>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "/topic/user.owner.countdown/ onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "/topic/user.owner.countdown/ onError: " + e.getMessage());
                }

                @Override
                public void onNext(StompMessage stompMessage) {
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
//					msg.obj = response.model(PlayerBean.class);
                    msg.what = 0x15;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "owner.countdown onNext: " + stompMessage.getPayload());
                }

            });

            mStompClient.topic("/topic/room."+roomId+"/owner.countdown.cancel").subscribe(new Subscriber<StompMessage>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "/topic/owner.countdown.cancel/ onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "/topic/owner.countdown.cancel/ onError: " + e.getMessage());
                }

                @Override
                public void onNext(StompMessage stompMessage) {
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
//					msg.obj = response.model(PlayerBean.class);
                    msg.what = 0x17;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "owner.countdown.cancel onNext: " + stompMessage.getPayload());
                }

            });

            mStompClient.topic("/topic/room."+roomId+"/start.game").subscribe(new Subscriber<StompMessage>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "/topic/start.game/ onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "/topic/start.game/ onError: " + e.getMessage());
                }

                @Override
                public void onNext(StompMessage stompMessage) {
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
					msg.obj = response.model(RoomInfoBean.class);
                    msg.what = 0x18;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "start.game onNext: " + stompMessage.getPayload());
                }

            });

            mStompClient.topic("/topic/room."+roomId+"/questions").subscribe(new Subscriber<StompMessage>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "/topic/questions/ onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "/topic/questions/ onError: " + e.getMessage());
                }

                @Override
                public void onNext(StompMessage stompMessage) {
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
//					msg.obj = response.model(PlayerBean.class);
                    msg.what = 0x19;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "questionsList -----> onNext: " + stompMessage.getPayload());
                }

            });

            mStompClient.topic("/topic/room."+roomId+"/question/ok").subscribe(new Subscriber<StompMessage>() {
                @Override
                public void onCompleted() {
                    Log.e(TAG, "/topic/question/ok/ onCompleted: ");
                }

                @Override
                public void onError(Throwable e) {
                    Log.e(TAG, "/topic/question/ok/ onError: " + e.getMessage());
                }

                @Override
                public void onNext(StompMessage stompMessage) {
                    Response response = new Response(stompMessage.getPayload());
                    Message msg = new Message();
//					msg.obj = response.model(PlayerBean.class);
                    msg.what = 0x20;
                    changeUI.sendMessage(msg);
                    Log.e(TAG, "/question/ok -----> onNext: " + stompMessage.getPayload());
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

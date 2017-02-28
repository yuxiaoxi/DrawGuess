package com.zhy.graph.network;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zhy.graph.app.BaseApplication;
import com.zhy.graph.bean.ResultBean;
import com.zhy.graph.bean.RoomInfoBean;
import com.zhy.graph.utils.DomainUtils;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yuzhuo on 2017/2/28.
 */
public class NetRequestUtil {

    private Context mContext;

    private Handler mHandler;

    public NetRequestUtil(Context context, Handler handler){
        this.mContext = context;
        this.mHandler = handler;
    }

    private final String TAG = "NetRequestUtil";
    /**
     * 用户进入随机房间接口
     * @param username
     */
    public void getRandomRoomUsingGET(final String username) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        String url = DomainUtils.SERVER_HOST+"/api/v1/room/into";
        DhNet net = new DhNet(url);
        net.addParams(map).doGet(new NetTask(mContext) {

            @Override
            public void onErray(Response response) {

                super.onErray(response);
                Toast.makeText(mContext,"数据请求错误！请您重新再试！",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doInUI(Response response, Integer transfer) {
                if("1".equals(response.code)) {//获取成功

                    RoomInfoBean roomInfo = response.modelFromData(RoomInfoBean.class);
                    Message msg = new Message();
                    msg.obj = roomInfo;
                    msg.what = 0x10;
                    mHandler.sendMessage(msg);
                    Log.e(TAG,roomInfo.getNowUserNum());
                }
            }
        });

    }

    /**
     * 用户离开房间
     * @param username
     * @param type 0游客退出,1是创建新房间
     */
    public void leaveRoomUsingGET(final String username,final int type, final String roomId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        String url = DomainUtils.SERVER_HOST+"/api/v1/room/leave";
        DhNet net = new DhNet(url);
        net.addParams(map).doGet(new NetTask(mContext) {

            @Override
            public void onErray(Response response) {

                super.onErray(response);
                Toast.makeText(mContext,"数据请求错误！请您重新再试！",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doInUI(Response response, Integer transfer) {
                if("1".equals(response.code)) {//获取成功
                    Message msg = new Message();
                    if(type== 0) {
                        msg.what = 0x11;
                        mHandler.sendMessage(msg);
                    }else if(type == 1){
                        createRoomUsingGET(username);
                    }else if(type == 2){
                        intoRoomUsingGET(username,roomId);
                    }else if(type == 3){
                        msg.what = 0x16;
                        mHandler.sendMessage(msg);
                    }


                }
            }
        });

    }

    /**
     * 用户准备
     * @param username
     * @param roomId
     */
    public void userReadyUsingGET(final String username, final String roomId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        map.put("roomId", roomId);
        String url = DomainUtils.SERVER_HOST+"/api/v1/room/"+roomId+"/ready";
        DhNet net = new DhNet(url);
        net.addParams(map).doGet(new NetTask(mContext) {

            @Override
            public void onErray(Response response) {

                super.onErray(response);
                Toast.makeText(mContext,"数据请求错误！请您重新再试！",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doInUI(Response response, Integer transfer) {
                if("1".equals(response.code)) {//获取成功
                    Message msg = new Message();
                    msg.what = 0x12;
                    mHandler.sendMessage(msg);

                }
            }
        });

    }

    /**
     * 用户取消准备
     * @param username
     * @param roomId
     */
    public void userReadyCancelUsingGET(final String username, final String roomId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        map.put("roomId", roomId);
        String url = DomainUtils.SERVER_HOST+"/api/v1/room/"+roomId+"/readycancel";
        DhNet net = new DhNet(url);
        net.addParams(map).doGet(new NetTask(mContext) {

            @Override
            public void onErray(Response response) {

                super.onErray(response);
                Toast.makeText(mContext,"数据请求错误！请您重新再试！",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doInUI(Response response, Integer transfer) {
                if("1".equals(response.code)) {//获取成功
                    Message msg = new Message();
                    msg.what = 0x13;
                    mHandler.sendMessage(msg);

                }
            }
        });

    }

    /**
     * createRoomUsingGET
     * @param username
     */
    public void createRoomUsingGET(final String username) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        String url = DomainUtils.SERVER_HOST+"/api/v1/room/create";
        DhNet net = new DhNet(url);
        net.addParams(map).doGet(new NetTask(mContext) {

            @Override
            public void onErray(Response response) {

                super.onErray(response);
                Toast.makeText(mContext,"数据请求错误！请您重新再试！",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doInUI(Response response, Integer transfer) {
                if("1".equals(response.code)) {//获取成功

                    RoomInfoBean roomInfo = response.modelFromData(RoomInfoBean.class);
                    Log.e(TAG,roomInfo.getNowUserNum());
                    Message msg = new Message();
                    msg.what = 0x15;
                    msg.obj = roomInfo;
                    mHandler.sendMessage(msg);
                }
            }
        });

    }

    /**
     * intoRoomUsingGET
     * @param username
     */
    public void intoRoomUsingGET(final String username, final String roomId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("username", username);
        map.put("roomId", roomId);
        String url = DomainUtils.SERVER_HOST+"/api/v1/room/into/"+roomId;
        DhNet net = new DhNet(url);
        net.addParams(map).doGet(new NetTask(mContext) {

            @Override
            public void onErray(Response response) {

                super.onErray(response);
                Toast.makeText(mContext,"数据请求错误！请您重新再试！",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void doInUI(Response response, Integer transfer) {
                if("1".equals(response.code)) {//获取成功

                    RoomInfoBean roomInfo = response.modelFromData(RoomInfoBean.class);
                    Message msg = new Message();
                    msg.obj = roomInfo;
                    msg.what = 0x15;
                    mHandler.sendMessage(msg);
                    Log.e(TAG,roomInfo.getNowUserNum());
                }
            }
        });

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
        net.addParams(map).doPost(new NetTask(mContext) {

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
}

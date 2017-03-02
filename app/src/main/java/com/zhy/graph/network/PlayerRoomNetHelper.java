package com.zhy.graph.network;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

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
public class PlayerRoomNetHelper {

    private Context mContext;

    private Handler mHandler;

    public PlayerRoomNetHelper(Context context, Handler handler){
        this.mContext = context;
        this.mHandler = handler;
    }

    private final String TAG = "PlayerRoomNetHelper";
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

}

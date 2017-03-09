package com.zhy.graph.network;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.zhy.graph.bean.QuestionInfo;
import com.zhy.graph.bean.RoomInfoBean;
import com.zhy.graph.utils.DomainUtils;

import net.duohuo.dhroid.net.DhNet;
import net.duohuo.dhroid.net.NetTask;
import net.duohuo.dhroid.net.Response;

import java.util.HashMap;
import java.util.List;
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
     * 请求题目列表
     * @param roomInfo
     * @param size
     */
    public void questionListUsingGET(final RoomInfoBean roomInfo, final int size) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("size", size);
        map.put("roomId", roomInfo.getRoomId());
        String url = DomainUtils.SERVER_HOST+"/api/v1/room/"+roomInfo.getRoomId()+"/question/list";
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

                    List<QuestionInfo> questionList = response.listFromData(QuestionInfo.class);
                    Message msg = new Message();
                    msg.obj = questionList;
                    msg.what = 0x18;
                    mHandler.sendMessage(msg);
                    Log.e(TAG,response.result);
                }
            }
        });

    }

    /**
     * 选择题目请求
     * @param roomId
     * @param questionId
     */
    public void questionOkUsingGE(final String roomId, final String questionId) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("questionId", questionId);
        map.put("roomId", roomId);
        String url = DomainUtils.SERVER_HOST+"/api/v1/room/"+roomId+"/question/ok";
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

                    QuestionInfo questionInfo = response.modelFromData(QuestionInfo.class);
                    Message msg = new Message();
                    msg.obj = questionInfo;
                    msg.what = 0x19;
                    mHandler.sendMessage(msg);
                    Log.e(TAG,response.result);
                }
            }
        });

    }

}

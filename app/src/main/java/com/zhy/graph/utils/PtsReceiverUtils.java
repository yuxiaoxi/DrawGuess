package com.zhy.graph.utils;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zhy.graph.bean.CoordinateBean;
import com.zhy.graph.bean.ReceiveService;
import com.zhy.graph.widget.HuaBanView;

import java.util.List;

/**
 * Created by yuzhuo on 2017/2/13.
 */
public class PtsReceiverUtils {

    String TAG = "PtsReceiverUtils";
    private Context mContext;
    private HuaBanView huaBanView;
    public float pX,huBanX;
    public float pY,buBanY;
    public PtsReceiverUtils(Context context ,HuaBanView huaBanView){
        this.mContext = context;
        this.huaBanView = huaBanView;
        if(huaBanView!=null){
            this.huBanX = huaBanView.getX();
            this.buBanY = huaBanView.getY();
        }
    }

    public void updateView(String msg){
        ReceiveService mReService = null;
        JSONObject json = JSON.parseObject(msg);
        if(json.containsKey("data")){
            mReService = JSON.parseObject(json.getString("data"),
                    ReceiveService.class);
        }else{
            mReService = JSON.parseObject(msg,
                    ReceiveService.class);
        }

        if(mReService==null||mReService.getPts().size()<1){
            return;
        }
        pX = mReService.getPts().get(0).getX();
        pY = mReService.getPts().get(0).getY();
        this.huaBanView.path.moveTo(pX, pY);

        for (int i = 1; i < mReService.getPts().size(); i++) {
            this.huaBanView.path.quadTo(pX, pY, mReService.getPts().get(i).getX(), mReService.getPts().get(i).getY());
            pX = mReService.getPts().get(i).getX();
            pY = mReService.getPts().get(i).getY();
        }
        this.huaBanView.setColor(mReService.getColor());
        this.huaBanView.setPaintWidth(mReService.getLw());
        this.huaBanView.cacheCanvas.drawPath(this.huaBanView.path, this.huaBanView.paint);
        this.huaBanView.path.reset();
        this.huaBanView.invalidate();

    }

    public String sendPaintData(List<CoordinateBean> list, int lw, int color){

        if(list == null)
            return null;
        StringBuilder sb = new StringBuilder();
        sb.append("{\"data\":{\"pts\":[");

        for (int i = 0; i < list.size(); i++) {
            sb.append("{\"x\":").append(list.get(i).getX()).append(",\"y\":").append(list.get(i).getY()).append("}");
            if(i!=list.size()-1){
                sb.append(",");
            }
        }

        sb.append("],\"lw\":"+lw+",\"color\":"+color+"},\"status\":\"end\"}");

        Log.e("paintDataString---->",sb.toString());
        return sb.toString();
    }

}

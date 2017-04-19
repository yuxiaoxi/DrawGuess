package com.zhy.graph.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.graph.R;
import com.zhy.graph.bean.PlayerInfo;
import com.zhy.graph.network.HomeNetHelper;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by yuzhuo on 2017/2/9.
 */
public class HomePlayerGridAdapter extends BaseAdapter{

    private Context mContext;
    private List<PlayerInfo> dataList;
    private LayoutInflater mInflater;
    private Timer daoTimer;
    private TimerTask task = null;
    private HomeNetHelper netUitl;
    private Handler changeUI;
    public HomePlayerGridAdapter(Context context, List<PlayerInfo> data, HomeNetHelper netUitl, Handler handler){
        this.mContext = context;
        this.dataList = data;
        this.netUitl = netUitl;
        this.changeUI = handler;
        if(context != null){
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public PlayerInfo getItem(int position) {
        return dataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getCount() {
        return dataList != null?dataList.size() : 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = this.mInflater.inflate(R.layout.item_grid_player,null,false);
            viewHolder = new ViewHolder();
            viewHolder.avatarImageView = (ImageView) convertView.findViewById(R.id.img_player_avatar);
            viewHolder.isReadyTextView = (TextView) convertView.findViewById(R.id.txt_player_is_ready);
            viewHolder.nickNameTextView = (TextView) convertView.findViewById(R.id.txt_player_nickname);
            viewHolder.youkeNameTextView = (TextView) convertView.findViewById(R.id.txt_player_youke_nickname);
            viewHolder.txt_roomer_count_down = (TextView) convertView.findViewById(R.id.txt_roomer_count_down);
            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }
        if(dataList.get(position).isShowCount()){
            viewHolder.txt_roomer_count_down.setVisibility(View.VISIBLE);
            viewHolder.txt_roomer_count_down.setText(dataList.get(0).getCountDown()+"");
        }else{
            viewHolder.txt_roomer_count_down.setVisibility(View.GONE);
        }

        if(dataList.get(position).isMe()){
            viewHolder.avatarImageView.setBackgroundResource(R.drawable.border_ring_shape);
        }else{
            viewHolder.avatarImageView.setBackgroundColor(mContext.getResources().getColor(R.color.transparent));
        }
        if(dataList.get(position).isYouke()){
            viewHolder.youkeNameTextView.setVisibility(View.VISIBLE);
            viewHolder.youkeNameTextView.setText(dataList.get(position).getNickName());
            viewHolder.avatarImageView.setImageResource(R.drawable.white_ring_shape);
        }else{
            viewHolder.youkeNameTextView.setVisibility(View.GONE);
            viewHolder.avatarImageView.setImageResource(R.drawable.default_avatar);
        }

        if(dataList.get(position).isReady()&&position>0){
            viewHolder.isReadyTextView.setVisibility(View.VISIBLE);
        }else{
            viewHolder.isReadyTextView.setVisibility(View.GONE);
        }

        viewHolder.nickNameTextView.setText(dataList.get(position).getNickName());
        return convertView;
    }


    public List<PlayerInfo> getDataList(){
        return dataList;
    }

    public void update(List<PlayerInfo> data){
        this.dataList.clear();
        this.dataList.addAll(data);
        notifyDataSetChanged();
    }

    public void clickReady(){
        if(this.dataList == null)
            return;
        for (int i = 0; i < this.dataList.size(); i++) {
            if(this.dataList.get(i).isMe()){
                this.dataList.get(i).setReady(true);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void cancelReady(){
        if(this.dataList == null)
            return;
        for (int i = 0; i < this.dataList.size(); i++) {
            if(this.dataList.get(i).isMe()){
                this.dataList.get(i).setReady(false);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder{
        private ImageView avatarImageView;
        private TextView nickNameTextView;
        private TextView isReadyTextView;
        private TextView youkeNameTextView;
        private TextView txt_roomer_count_down;
    }

    public void countDown(){
        daoTimer = new Timer();
        task = new TimerTask() {
            public void run() {
                Message msg = new Message();
                msg.what = 0x100;
                int count = dataList.get(0).getCountDown()-1;
                dataList.get(0).setCountDown(count);
                msg.arg1 = count;
                if(msg.arg1 == 0){
                    daoTimer.cancel();
                }
                changeUI.sendMessage(msg);
            }
        };
        daoTimer.schedule(task, 0, 1000);
    }
}

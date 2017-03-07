package com.zhy.graph.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.graph.bean.PlayerBean;

import java.util.List;

import gra.zhy.com.graph.R;

/**
 * Created by yuzhuo on 2017/2/9.
 */
public class PlayerRoomGridAdapter extends BaseAdapter{

    private Context mContext;
    private List<PlayerBean> dataList;
    private LayoutInflater mInflater;

    public PlayerRoomGridAdapter(Context context, List<PlayerBean> data){
        this.mContext = context;
        this.dataList = data;
        if(context != null){
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public PlayerBean getItem(int position) {
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
        PlayerBean bean = getItem(position);
        if(convertView == null){
            convertView = this.mInflater.inflate(R.layout.item_grid_player_room,null,false);
            viewHolder = new ViewHolder();
            viewHolder.avatarImageView = (ImageView) convertView.findViewById(R.id.img_player_room_avatar);
            viewHolder.guessWordsTextView = (TextView) convertView.findViewById(R.id.txt_player_room_guess_word);
            viewHolder.scoreTextView = (TextView) convertView.findViewById(R.id.txt_player_room_score);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if(bean.getAnsser()!=null){
            viewHolder.guessWordsTextView.setVisibility(View.VISIBLE);
            viewHolder.guessWordsTextView.setText(bean.getAnsser());
        }else{
            viewHolder.guessWordsTextView.setVisibility(View.INVISIBLE);
        }

        if(dataList.get(position).isDrawNow()){//正在画的玩家
            viewHolder.avatarImageView.setBackgroundResource(R.drawable.red_ring_rectangle_shape);
        }else{
            viewHolder.avatarImageView.setBackground(null);
        }

        viewHolder.scoreTextView.setText(bean.getCurrentScore());

        return convertView;
    }

    public static class ViewHolder{
        private ImageView avatarImageView;
        private TextView guessWordsTextView;
        private TextView scoreTextView;
    }
}

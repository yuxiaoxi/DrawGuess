package com.zhy.graph.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.zhy.graph.bean.PlayerRoomInfo;

import java.util.List;

import gra.zhy.com.graph.R;

/**
 * Created by yuzhuo on 2017/2/9.
 */
public class PlayerRoomGridAdapter extends BaseAdapter{

    private Context mContext;
    private List<PlayerRoomInfo> dataList;
    private LayoutInflater mInflater;

    public PlayerRoomGridAdapter(Context context, List<PlayerRoomInfo> data){
        this.mContext = context;
        this.dataList = data;
        if(context != null){
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public PlayerRoomInfo getItem(int position) {
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
            convertView = this.mInflater.inflate(R.layout.item_grid_player_room,null,false);
            viewHolder = new ViewHolder();
            viewHolder.avatarImageView = (ImageView) convertView.findViewById(R.id.img_player_room_avatar);
            viewHolder.guessWordsTextView = (TextView) convertView.findViewById(R.id.txt_player_room_guess_word);
            viewHolder.scoreTextView = (TextView) convertView.findViewById(R.id.txt_player_room_score);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.guessWordsTextView.setText(dataList.get(position).getGuessWords());
        viewHolder.scoreTextView.setText(dataList.get(position).getScore());

        return convertView;
    }

    public static class ViewHolder{
        private ImageView avatarImageView;
        private TextView guessWordsTextView;
        private TextView scoreTextView;
    }
}

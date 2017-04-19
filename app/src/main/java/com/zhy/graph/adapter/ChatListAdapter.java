package com.zhy.graph.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhy.graph.R;
import com.zhy.graph.bean.ChatInfo;

import java.util.List;


/**
 * Created by yuzhuo on 2017/2/9.
 */
public class ChatListAdapter extends BaseAdapter{

    private Context mContext;
    private List<ChatInfo> dataList;
    private LayoutInflater mInflater;

    public ChatListAdapter(Context context, List<ChatInfo> data){
        this.mContext = context;
        this.dataList = data;
        if(context != null){
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public ChatInfo getItem(int position) {
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
            convertView = this.mInflater.inflate(R.layout.item_listview_chat,null,false);
            viewHolder = new ViewHolder();
            viewHolder.nameTextView = (TextView) convertView.findViewById(R.id.txt_player_name);
            viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.txt_chat_content);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.nameTextView.setText(dataList.get(position).getNickName());
        viewHolder.contentTextView.setText(dataList.get(position).getContent());

        return convertView;
    }

    public List<ChatInfo> getDataList(){
        return this.dataList;
    }

    public static class ViewHolder{
        private TextView nameTextView;
        private TextView contentTextView;
    }

    public void update(ChatInfo chatInfo){
        this.dataList.add(chatInfo);
        notifyDataSetChanged();
    }
}

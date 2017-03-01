package com.zhy.graph.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.zhy.graph.bean.QuestionInfo;

import java.util.List;

import gra.zhy.com.graph.R;

/**
 * Created by yuzhuo on 2017/2/9.
 */
public class QuestionsSelectListAdapter extends BaseAdapter{

    private Context mContext;
    private List<QuestionInfo> dataList;
    private LayoutInflater mInflater;

    public QuestionsSelectListAdapter(Context context, List<QuestionInfo> data){
        this.mContext = context;
        this.dataList = data;
        if(context != null){
            mInflater = LayoutInflater.from(context);
        }
    }

    @Override
    public QuestionInfo getItem(int position) {
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
            convertView = this.mInflater.inflate(R.layout.item_list_questions,null,false);
            viewHolder = new ViewHolder();
            viewHolder.contentTextView = (TextView) convertView.findViewById(R.id.txt_guess_word);

            convertView.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.contentTextView.setText(dataList.get(position).getQuestion());

        return convertView;
    }

    public static class ViewHolder{
        private TextView contentTextView;
    }

}

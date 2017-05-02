package com.zhy.graph.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zhy.graph.R;

import net.duohuo.dhroid.ioc.annotation.InjectView;


public class FeedBackActivity extends BaseAct {


	@InjectView(id = R.id.title_bar_view)
	private RelativeLayout title_bar_view;

	@InjectView(id = R.id.txt_title_name)
	private TextView txt_title_name;

	@InjectView(id = R.id.image_title_left, click = "onClickCallBack")
	private ImageView backLayout;

	@InjectView(id = R.id.text_title_right, click = "onClickCallBack")
	private TextView text_title_right;

	@InjectView(id = R.id.edit_feed_back_suggest)
	private EditText edit_feed_back_suggest;

	@InjectView(id = R.id.txt_input_count)
	private TextView txt_input_count;


	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.act_feed_back_suggestion);

		initView();
	}

	public void initView(){
		backLayout.setImageResource(R.drawable.back_left_icon);
		backLayout.setVisibility(View.VISIBLE);
		txt_title_name.setTextColor(Color.parseColor("#000000"));
		txt_title_name.setText("意见反馈");
		txt_title_name.setVisibility(View.VISIBLE);
		title_bar_view.setBackgroundColor(Color.parseColor("#ffffff"));
		text_title_right.setVisibility(View.VISIBLE);
		text_title_right.setTextColor(Color.parseColor("#000000"));
		text_title_right.setText("提交");
		edit_feed_back_suggest.addTextChangedListener(new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				int amount = edit_feed_back_suggest.getText().toString().trim().length();
				if(amount == 200){
					edit_feed_back_suggest.setBackgroundResource(R.color.btn_gray_bg);
				}
				txt_input_count.setText((200-amount)+"");
			}
		});
	}

	public void onClickCallBack(View view) {
		Intent intent = new Intent();
		switch (view.getId()) {

		case R.id.image_title_left:
			finish();
			break;



		case R.id.text_title_right:
			if(isEmpty(edit_feed_back_suggest.getText().toString().trim())){
				Toast.makeText(FeedBackActivity.this,"反馈内容不能为空!",Toast.LENGTH_SHORT).show();
				return;
			}
			sendMailByIntent(edit_feed_back_suggest.getText().toString());
			break;


		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();

	}

	/**
	 * 
	 * @Title: isEmpty
	 * @Description: 判断字符串是否为空
	 * @param @param str
	 * @param @return 设定文件
	 * @return boolean 返回类型
	 * @throws
	 */
	public boolean isEmpty(String str) {
		if (str == null || "".equals(str)) {
			return true;
		}
		return false;
	}


	public int sendMailByIntent(String body) {
		String[] reciver = new String[] { "suao1989@163.com" };
		String[] mySbuject = new String[] { "意见反馈" };
		String myCc = "cc";
		Intent myIntent = new Intent(android.content.Intent.ACTION_SEND);
		myIntent.setType("plain/text");
		myIntent.putExtra(android.content.Intent.EXTRA_EMAIL, reciver);
		myIntent.putExtra(android.content.Intent.EXTRA_CC, myCc);
		myIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, mySbuject);
		myIntent.putExtra(android.content.Intent.EXTRA_TEXT, body);
		startActivity(Intent.createChooser(myIntent, "意见反馈"));

		return 1;

	}


}

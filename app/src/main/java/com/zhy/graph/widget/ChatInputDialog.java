package com.zhy.graph.widget;


import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import com.zhy.graph.bean.ChatInfo;

import gra.zhy.com.graph.R;


public class ChatInputDialog extends Dialog {
	private Context context = null;
	private static ChatInputDialog customProgressDialog = null;
	public ChatInputDialog(Context context) {
		super(context);
		this.context = context;
	}

	public ChatInputDialog(Context context, int theme) {
		super(context, theme);
	}

	public static ChatInputDialog createDialog(Context context, final Handler handler) {
		customProgressDialog = new ChatInputDialog(context,
				R.style.inputDialog);
		customProgressDialog.setContentView(R.layout.include_chat_bottom_bar);
		customProgressDialog.getWindow().getAttributes().gravity = Gravity.BOTTOM;
		customProgressDialog.findViewById(R.id.btn_chat_send).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String content = ((EditText)customProgressDialog.findViewById(R.id.edit_user_comment)).getText().toString().trim();
				if(content == null ||content.length() ==0)
					return;
				ChatInfo info = new ChatInfo();
				info.setContent(content);
				info.setNickName("二二");
				Message msg = new Message();
				msg.what = 0x15;
				msg.obj = info;
				handler.sendMessage(msg);
				((EditText)customProgressDialog.findViewById(R.id.edit_user_comment)).setText("");
			}
		});

		Window win = customProgressDialog.getWindow();
		win.getDecorView().setPadding(0,0,0,0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);
		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}
	}

}

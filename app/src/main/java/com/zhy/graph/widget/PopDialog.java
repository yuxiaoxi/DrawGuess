package com.zhy.graph.widget;


import android.app.Dialog;
import android.content.Context;


public class PopDialog extends Dialog {
	private Context context = null;
	private static PopDialog customProgressDialog = null;
	public PopDialog(Context context) {
		super(context);
		this.context = context;
	}

	public PopDialog(Context context, int theme) {
		super(context, theme);
	}

	public static PopDialog createDialog(Context context, int resouces, int location,int theme) {
		customProgressDialog = new PopDialog(context,
				theme);
		customProgressDialog.setContentView(resouces);
		customProgressDialog.getWindow().getAttributes().gravity = location;

		return customProgressDialog;
	}

	public void onWindowFocusChanged(boolean hasFocus) {

		if (customProgressDialog == null) {
			return;
		}
	}

}

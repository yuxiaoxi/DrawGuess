package com.zhy.graph.widget;


import android.app.Dialog;
import android.content.Context;
import android.view.Window;
import android.view.WindowManager;

/**
 * @author yuzhuo
 */
public class PopDialog extends Dialog {

	public PopDialog(Context context) {
		super(context);
	}

	public PopDialog(Context context, int theme) {
		super(context, theme);
	}

	public PopDialog setGravity(int location){
		getWindow().getAttributes().gravity = location;
		return this;
	}

	public PopDialog setResources(int resources){
		setContentView(resources);
		Window win = getWindow();
		win.getDecorView().setPadding(0, 0, 0, 0);
		WindowManager.LayoutParams lp = win.getAttributes();
		lp.width = WindowManager.LayoutParams.FILL_PARENT;
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
		win.setAttributes(lp);
		return this;
	}

}

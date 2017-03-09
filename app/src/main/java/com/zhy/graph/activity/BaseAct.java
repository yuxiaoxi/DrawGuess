package com.zhy.graph.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.umeng.analytics.MobclickAgent;
import com.zhy.graph.app.AppManager;

import net.duohuo.dhroid.activity.BaseActivity;

/**
 * 
 * @ClassName: BaseAct
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author 余卓
 * @date 2014年12月24日 上午11:36:22
 * 
 */
public class BaseAct extends BaseActivity {
	public Context context;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View decorView = getWindow().getDecorView();
		int option = View.SYSTEM_UI_FLAG_FULLSCREEN;
		decorView.setSystemUiVisibility(option);
		if (Build.VERSION.SDK_INT >= 21) {
			getWindow().setStatusBarColor(Color.WHITE);
		}

		Log.e("activity", getRunningActivityName());
		context = getApplicationContext();
		AppManager.getAppManager().addActivity(this);
	}



	private String getRunningActivityName() {
		ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity
				.getClassName();
		return runningActivity;
	}

	@Override
	protected void onDestroy() {

		AppManager.getAppManager().finishActivity(this);
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPause(this);
	}

}

package com.zhy.graph.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.media.MediaRecorder;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

import gra.zhy.com.graph.R;

/**
 * 
* @ClassName: RecordButton 
* @Description: 重写的记录按钮
* @author 余卓 
* @date 2014年10月21日 上午11:19:31 
*
 */
public class RecordButton extends Button {

	public RecordButton(Context context) {
		super(context);
		init();
	}

	public RecordButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public RecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public void setSavePath(String path) {
		mFileName = path;
	}

	public void setOnFinishedRecordListener(OnFinishedRecordListener listener) {
		finishedListener = listener;
	}

	private String mFileName = null;

	private OnFinishedRecordListener finishedListener;

	private static final int MIN_INTERVAL_TIME = 2000;// 2s
	private long startTime;

	private Dialog recordIndicator;

	private static int[] res = { R.drawable.chat_icon_voice1,
			R.drawable.chat_icon_voice2, R.drawable.chat_icon_voice3,
			R.drawable.chat_icon_voice4, R.drawable.chat_icon_voice5 };

	private static ImageView view;

	private MediaRecorder recorder;

	private ObtainDecibelThread thread;

	private Handler volumeHandler;
	private int mYpositon;
	
	public  long intervalTime;
	
	private void init() {
		volumeHandler = new ShowVolumeHandler();
		
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		int[] location = new int[2];  
        getLocationOnScreen(location);  
        mYpositon= location[1]; 
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mFileName == null)
			return false;

		int action = event.getAction();

		switch (action) {
		case MotionEvent.ACTION_DOWN:
			initDialogAndStartRecord();
			break;
		case MotionEvent.ACTION_UP:
			finishRecord();
			break;
		case MotionEvent.ACTION_CANCEL:// 当手指移动到view外面，会cancel
				cancelRecord();
			break;
		}

		return true;
	}

	private void initDialogAndStartRecord() {

		startTime = System.currentTimeMillis();
		recordIndicator = new Dialog(getContext(),
				R.style.like_toast_dialog_style);
		view = new ImageView(getContext());
		view.setImageResource(R.drawable.chat_icon_voice1);
		recordIndicator.setContentView(view, new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		recordIndicator.setOnDismissListener(onDismiss);
		LayoutParams lp = recordIndicator.getWindow().getAttributes();
		lp.gravity = Gravity.CENTER;

		startRecording();
		recordIndicator.show();
	}

	private void finishRecord() {
		stopRecording();
		recordIndicator.dismiss();

		intervalTime = System.currentTimeMillis() - startTime;
		if (intervalTime < MIN_INTERVAL_TIME) {
			Toast.makeText(getContext(), "时间太短！", Toast.LENGTH_SHORT).show();
			File file = new File(mFileName);
			file.delete();
			return;
		}
		intervalTime = Math.round((intervalTime/1000));
		if (finishedListener != null)
			finishedListener.onFinishedRecord(mFileName);
	}

	private void cancelRecord() {
		stopRecording();
		recordIndicator.dismiss();

		Toast.makeText(getContext(), "取消录音！", Toast.LENGTH_SHORT).show();
		File file = new File(mFileName);
		file.delete();
	}

	private void startRecording() {
		Log.e("mFileName", mFileName);
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.RAW_AMR);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		recorder.setOutputFile(mFileName);

		try {
			recorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}

		recorder.start();
		thread = new ObtainDecibelThread();
		thread.start();

	}

	private void stopRecording() {
		if (thread != null) {
			thread.exit();
			thread = null;
		}
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}
	}

	private class ObtainDecibelThread extends Thread {

		private volatile boolean running = true;

		public void exit() {
			running = false;
		}

		@Override
		public void run() {
			while (running) {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (recorder == null || !running) {
					break;
				}
				int x = recorder.getMaxAmplitude();
				if (x != 0) {
					int f = (int) (10 * Math.log(x) / Math.log(10));
					if (f < 34)
						volumeHandler.sendEmptyMessage(0);
					else if (f < 36)
						volumeHandler.sendEmptyMessage(1);
					else if (f < 38)
						volumeHandler.sendEmptyMessage(2);
					else if (f < 40)
						volumeHandler.sendEmptyMessage(3);
					else
						volumeHandler.sendEmptyMessage(4);
				}

			}
		}

	}

	private OnDismissListener onDismiss = new OnDismissListener() {

		@Override
		public void onDismiss(DialogInterface dialog) {
			stopRecording();
		}
	};

	static class ShowVolumeHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			view.setImageResource(res[msg.what]);
		}
	}

	public interface OnFinishedRecordListener {
		public void onFinishedRecord(String audioPath);
	}

}

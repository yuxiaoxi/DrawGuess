package com.zhy.graph.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

public class HuaBanView extends View {

	private Bitmap cacheBitmap;
	public Canvas cacheCanvas;
	public Paint paint;
	private Paint BitmapPaint;
	public Path path;
	private int height;
	private int width;
	
	public float pX;
	public float pY;
	
	private int paintColor = Color.BLACK;
	private static Paint.Style paintStyle = Paint.Style.STROKE;
	private static int paintWidth = 10;
	
	private Canvas canvas;
	

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		height = h;
		width = w;
		init();
	}
	
	private void init(){
		cacheBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		cacheCanvas = new Canvas(cacheBitmap);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		path = new Path();
		BitmapPaint = new Paint();
		updatePaint();
	}
	
	private void updatePaint(){
		paint.setColor(paintColor);
		paint.setStyle(paintStyle);
		paint.setStrokeWidth(paintWidth);
	}
	
	public HuaBanView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public HuaBanView(Context context){
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			path.moveTo(event.getX(), event.getY());
			pX = event.getX();
			pY = event.getY();
			break;
		case MotionEvent.ACTION_MOVE:
			path.quadTo(pX, pY, event.getX(), event.getY());
			pX = event.getX();
			pY = event.getY();
			break;
		case MotionEvent.ACTION_UP:
			cacheCanvas.drawPath(path, paint);
			path.reset();
			break;
		}
		invalidate();

		return true;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		this.canvas = canvas;
		BitmapPaint = new Paint();
		canvas.drawBitmap(cacheBitmap, 0,0, BitmapPaint);
		canvas.drawPath(path, paint);
		
	}
	

	public void setColor(int color){
		paintColor = color;
		updatePaint();
	}

	public int getPaintColor() {
		return paintColor;
	}

	public void setPaintWidth(int width){
		paintWidth = width;
		updatePaint();
	}

	public static int getPaintWidth() {
		return paintWidth;
	}

	public static final int PEN = 1;
	public static final int PAIL = 2;
	

	public void setStyle(int style){
		switch(style){
		case PEN:
			paintStyle = Paint.Style.STROKE;
			break;
		case PAIL:
			paintStyle = Paint.Style.FILL;
			break;
		}
		updatePaint();
	}
	

	public void clearScreen(){
		if(cacheCanvas != null){
			Paint backPaint = new Paint();
			backPaint.setColor(Color.parseColor("#FFFDED"));
			cacheCanvas.drawRect(new Rect(0, 0, width, height), backPaint);
		}
		invalidate();
	}

	public Bitmap getBitmap(){
		return this.cacheBitmap;
	}
	
}

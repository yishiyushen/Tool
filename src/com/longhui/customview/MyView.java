package com.longhui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.View;

public class MyView extends View {

	public MyView(Context context) {
		// TODO Auto-generated constructor stub
		super(context);
	}
	public MyView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onDragEvent(DragEvent event) {
		// TODO Auto-generated method stub
		
		return super.onDragEvent(event);
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		//super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		setMeasuredDimension(300, 200);
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint paint = new Paint();
		paint.setTextSize(33);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawColor(Color.WHITE);
		canvas.drawText("sldlkdienke来\\n看山东佛嗯哦问饿哦家文饿哦家历\\n" +
				"sssieonowneowjeowjo\\n" +
				"lsjdldjldjdldkd\\nlkdjkddldj" +
				"史看东方文饿哦佛阿胶无法\\n动物叫饿哦家是两块多\\n佛诶我嗯哦问饿哦饿哦", 0, 35, paint);
		super.onDraw(canvas);
	}
}

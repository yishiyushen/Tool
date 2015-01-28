package com.longhui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Scroller;

public class ScrollViewGroup extends LinearLayout {
	
	private Scroller mScroller;

	public ScrollViewGroup(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	private void init(Context context){
		mScroller = new Scroller(context);
		setWillNotDraw(false);
		//addView(new MyView(context));
//		Button view = new Button(context);
//		view.setText("按钮");
		MyView  view = new MyView(context);
		LinearLayout.LayoutParams params = new LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		addView(view, params);
	}
	
//	@Override
//	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
//		// TODO Auto-generated method stub
//		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//
//		View child = getChildAt(0);
//		child.measure(widthMeasureSpec, heightMeasureSpec);
//	}
//	
//	@Override
//	protected void onLayout(boolean changed, int l, int t, int r, int b) {
//		// TODO Auto-generated method stub
//		Log.e("TAG", "l= "+l+" t="+t+" r= "+r+" b="+b);
//		View child = getChildAt(0);
//		child.layout(l, t, r, b);
//		
//		
//	}
	
	public void startMove(int x,int y,int dx,int dy,int duration){
		mScroller.startScroll(x, y, dx, dy, duration);
		invalidate();
	}
	
	@Override
	public void computeScroll() {
		// TODO Auto-generated method stub
		if(mScroller.computeScrollOffset()){
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			postInvalidate();
		}
		super.computeScroll();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		Paint paint = new Paint();
		paint.setTextSize(33);
		paint.setColor(Color.BLACK);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawColor(Color.WHITE);
//		canvas.drawText("sldlkdienke来看山东佛嗯哦问饿哦家文饿哦家历" +
//				"sssieonowneowjeowjo" +
//				"lsjdldjldjdldkdlkdjkddldj" +
//				"史看东方文饿哦佛阿胶无法动物叫饿哦家是两块多佛诶我嗯哦问饿哦饿哦", 100, 100, paint);
		super.onDraw(canvas);
	}
	


	
}

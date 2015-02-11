package com.longhui.customview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.RectF;
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
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	//	setMeasuredDimension(300, 200);
	}
	
	
	public void drawCompass(Canvas canvas){
		Paint paint = new Paint();
        paint.setAntiAlias(true);  
        paint.setStyle(Style.STROKE);  
        paint.setColor(Color.RED);
        canvas.translate(canvas.getWidth() / 2, 300); // 平移罗盘  
        canvas.drawCircle(0, 0, 200, paint); // 画圆圈  

        // 使用path绘制路径文字  
        canvas.save();  
        canvas.translate(-155, -155);  
        Path path = new Path();  
        path.addArc(new RectF(0, 0, 300, 300), -180, 180);  
        Paint citePaint = new Paint(paint);  
        citePaint.setTextSize(30);  
        citePaint.setStrokeWidth(1);  
       // canvas.drawTextOnPath("http://blog.csdn.net/lemon_tree", path, 35, 0, citePaint);  
        canvas.restore();  

        Paint tmpPaint = new Paint(paint); // 小刻度画笔对象  
        tmpPaint.setStrokeWidth(2);  
        tmpPaint.setTextSize(30);  
        tmpPaint.setColor(Color.RED);

        float y = 200;  
        int count = 60; // 总刻度数  
        
        for (int i = 0; i < count; i++) {  
            if (i % 5 == 0) {  
                canvas.drawLine(0f, y, 0, y + 20f, paint);  
                canvas.drawText(String.valueOf(i / 5 + 1), -4f, y + 55f, tmpPaint);  
            } else {  
                canvas.drawLine(0f, y, 0f, y + 15f, tmpPaint);  
            }  
            canvas.rotate(360 / count, 0f, 0f); // 旋转画纸  
        }  

        // 绘制指针  
        tmpPaint.setColor(Color.GRAY);  
        tmpPaint.setStrokeWidth(4);  
        canvas.drawCircle(0, 0, 20, tmpPaint);  
        tmpPaint.setStyle(Style.FILL);  
        tmpPaint.setColor(Color.YELLOW);  
          
        canvas.drawCircle(0, 0, 5, tmpPaint);  
        canvas.drawLine(0, 30, 0, -135, paint); 
        
	}
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		
//		paint.setTextSize(33);
//		paint.setColor(Color.BLACK);
//		paint.setStyle(Paint.Style.STROKE);
//		canvas.drawColor(Color.WHITE);
//		canvas.drawText("sldlkdienke来\\n看山东佛嗯哦问饿哦家文饿哦家历\\n" +
//				"sssieonowneowjeowjo\\n" +
//				"lsjdldjldjdldkd\\nlkdjkddldj" +
//				"史看东方文饿哦佛阿胶无法\\n动物叫饿哦家是两块多\\n佛诶我嗯哦问饿哦饿哦", 0, 35, paint);
		drawCompass(canvas);
	//	super.onDraw(canvas);
	}
}

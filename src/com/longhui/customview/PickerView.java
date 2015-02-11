package com.longhui.customview;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetricsInt;
import android.graphics.Paint.Style;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class PickerView extends View {
	
	private String TAG = "PickerView";
	private static final float MARGIN_ALPHA = 2.8f;
	private static final float SPEED = 2.0f;
	private List<String> mDataList;
	/**选中的位置，这个位置是mDataList的中心位置，一直不变*/
	private int mCurrentSelected;
	private Paint mPaint;
	private float mMaxTextSize = 80;
	private float mMinTextSize = 40;
	private float mMaxTextAlpha = 255;
	private float mMinTextAlpha = 120;
	private int mColorText = 0x333333;
	private int mViewHeight;
	private int mViewWidth;
	/**滑动距离*/
	private int mMoveLen;
	private boolean isInit = false;
	private Timer timer;  
	private MyTimerTask mTimerTask;
	private float mLastDownY;
	private onSelectListener mSelectListener;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			if(Math.abs(mMoveLen) < SPEED){
				mMoveLen = 0;
		    	if(mTimerTask != null){
		    		mTimerTask.cancel();
		    		mTimerTask = null;
		    	}
			}else{
				mMoveLen = (int) (mMoveLen - mMoveLen/Math.abs(mMoveLen)*SPEED);
				invalidate();
			}
		};
	};
	
	public PickerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public PickerView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init();
	}
	
	public void setData(List<String> datas){
		mDataList = datas;
		mCurrentSelected = mDataList.size()/2;
		invalidate();
	}
    public void setOnSelectListener(onSelectListener listener)  
    {  
        mSelectListener = listener;  
    }  
  
    private void performSelect()  
    {  
        if (mSelectListener != null)  
            mSelectListener.onSelect(mDataList.get(mCurrentSelected));  
    } 
	public void setSelected(int selected)  
    {  
        mCurrentSelected = selected;  
    } 
	
	private void init(){
		timer = new Timer();
        mDataList = new ArrayList<String>();  
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);  
        mPaint.setStyle(Style.FILL);  
        mPaint.setTextAlign(Align.CENTER);  
        mPaint.setColor(mColorText); 
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// TODO Auto-generated method stub
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewHeight = this.getHeight();
		mViewWidth = this.getWidth();
		mMaxTextSize = mViewHeight/4.0f;
		mMinTextSize = mMaxTextSize/2.0f;
		isInit = true;
		invalidate();
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		if(isInit){
			drawData(canvas);
		}
	}
	
	private void drawData(Canvas canvas){
		// 先绘制选中的text再往上往下绘制其余的text
		float scare = parabola(mViewHeight/4.0f,mMoveLen);
		float size = (mMaxTextSize - mMinTextSize)*scare+mMinTextSize;
		float alpha = (mMaxTextAlpha - mMinTextAlpha)*scare + mMinTextAlpha;
		mPaint.setTextSize(size);
		mPaint.setAlpha((int) alpha);
		 // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标  
	    float x = (float) (mViewWidth / 2.0);  
        float y = (float) (mViewHeight / 2.0 + mMoveLen); 
        FontMetricsInt fmi = mPaint.getFontMetricsInt();  
        float baseline = (float)(y-(fmi.bottom-fmi.top)/2-fmi.top);//fmi.top是负值
        if(mDataList.size() != 0){
        	canvas.drawText(mDataList.get(mCurrentSelected), x, baseline, mPaint);
        }
        /**绘制上方的data*/
        for(int i=1;mCurrentSelected-i>=0;i++){
        	drawOtherText(canvas, i, -1);
        }
        
        /**绘制下方的data*/
        for(int i=1;mCurrentSelected+i<mDataList.size();i++){
        	drawOtherText(canvas, i, 1);
        }
	}
	
	private float parabola(float zero, float x){
		float f = (float)(1- Math.pow(x/zero, 2));
		return f > 0 ? f:0;
	}
	
	  /** 
     * @param canvas 
     * @param position 
     *            距离mCurrentSelected的差值 
     * @param type 
     *            1表示向下绘制，-1表示向上绘制 
     */  
    private void drawOtherText(Canvas canvas, int position, int type) {
    	float d = (float)(MARGIN_ALPHA*mMinTextSize*position+type*mMoveLen);
    	float scare = parabola(mViewHeight/4,d);
		float size = (mMaxTextSize - mMinTextSize)*scare+mMinTextSize;
		float alpha = (mMaxTextAlpha - mMinTextAlpha)*scare + mMinTextAlpha;
		mPaint.setTextSize(size);
		mPaint.setAlpha((int) alpha);
		 // text居中绘制，注意baseline的计算才能达到居中，y值是text中心坐标  
	    float x = (float) (mViewWidth / 2.0);  
        float y = (float) (mViewHeight / 2.0 + type*d); 
        FontMetricsInt fmi = mPaint.getFontMetricsInt();  
        float baseline = (float)(y-(fmi.bottom-fmi.top)/2-fmi.top);//fmi.top是负值
        if(mDataList.size() != 0){
        canvas.drawText(mDataList.get(mCurrentSelected+type*position), x, baseline, mPaint);
        }
       }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	// TODO Auto-generated method stub
    	switch(event.getActionMasked()){
        case MotionEvent.ACTION_DOWN:  
            doDown(event);  
            break;  
        case MotionEvent.ACTION_MOVE:  
            doMove(event);  
            break;  
        case MotionEvent.ACTION_UP:  
            doUp(event); 
    	}
    	return true;
    }
    
    private void moveTailToHead(){
    	String tail = mDataList.get(mDataList.size()-1);
    	mDataList.remove(mDataList.size()-1);
    	mDataList.add(0, tail);
    }
    
    private void moveHeadToTail(){
    	String head = mDataList.get(0);
    	mDataList.remove(0);
    	mDataList.add(head);
    }
    
    private void doDown(MotionEvent event){
    	if(mTimerTask != null){
    		mTimerTask.cancel();
    		mTimerTask = null;
    	}
    	mLastDownY = event.getY();
    }
    
    private void doMove(MotionEvent event){
    	mMoveLen +=(event.getY()- mLastDownY);
    	if(mMoveLen >(MARGIN_ALPHA*mMinTextSize)/2){
    		moveTailToHead();
    	   mMoveLen = (int) (mMoveLen - MARGIN_ALPHA*mMinTextSize);
    	}else if(mMoveLen < -(MARGIN_ALPHA*mMinTextSize)/2){
    		moveHeadToTail();
    		  mMoveLen = (int) (mMoveLen + MARGIN_ALPHA*mMinTextSize);
    	}
    	mLastDownY = event.getY();
    	invalidate();
    }
    
    private void doUp(MotionEvent event){
        // 抬起手后mCurrentSelected的位置由当前位置move到中间选中位置  
        if (Math.abs(mMoveLen) < 0.0001)  
        {  
            mMoveLen = 0;  
            return;  
        } 
    	if(mTimerTask != null){
    		mTimerTask.cancel();
    		mTimerTask = null;
    	}
    	mTimerTask = new MyTimerTask(mHandler);
    	timer.schedule(mTimerTask, 0,10);
    }
    
    private class MyTimerTask extends TimerTask{
    	 Handler handler; 
    	 public MyTimerTask(Handler handler) {
			// TODO Auto-generated constructor stub
    		 this.handler = handler;
		}
    	@Override
    	public void run() {
    		// TODO Auto-generated method stub
    		 handler.sendMessage(handler.obtainMessage());  
    	}
    }
    
    public interface onSelectListener  
    {  
        void onSelect(String text);  
    } 
}

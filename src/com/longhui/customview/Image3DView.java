package com.longhui.customview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Camera;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.View;

public class Image3DView extends View {
    /** 
     * 源视图，用于生成图片对象。 
     */  
	private View sourceView;
    /** 
     * 根据传入的源视图生成的图片对象。 
     */ 
	private Bitmap sourceBitmap;
    /** 
     * 源视图的宽度。 
     */ 
	private float sourceWith;
    /** 
     * Matrix对象，用于对图片进行矩阵操作。 
     */ 
	private Matrix matrix = new Matrix();
    /** 
     * Camera对象，用于对图片进行三维操作。 
     */ 
	private Camera camera = new Camera();
	
	public Image3DView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public Image3DView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}
	
    /** 
     * 提供外部接口，允许向Image3dView传入源视图。 
     *  
     * @param view 
     *            传入的源视图 
     */
	public void setSourceView(View sourceView){
		this.sourceView = sourceView;
		this.sourceWith = sourceView.getWidth();
	}
	
    /** 
     * 清除掉缓存的图片对象。 
     */  
    public void clearSourceBitmap() {  
        if (sourceBitmap != null) {  
            sourceBitmap = null;  
        }  
    }
    
    /** 
     * 获取源视图对应的图片对象。 
     */  
    public void getSourceBitmap(){
    	if(sourceView != null){
    		sourceView.setDrawingCacheEnabled(true);
    		sourceView.layout(0, 0, sourceView.getWidth(), sourceView.getHeight());
    		sourceView.buildDrawingCache();
    		sourceBitmap = sourceView.getDrawingCache();
    	}
    }
    
    @Override
    protected void onDraw(Canvas canvas) {
    	// TODO Auto-generated method stub
    	if(sourceBitmap == null){
    		getSourceBitmap();
    	}
    	float degree = 90 - (90/sourceWith)*getWidth();
    	camera.save();
    	camera.rotateY(degree);
    	camera.getMatrix(matrix);
    	camera.restore();
    	matrix.preTranslate(0, -getHeight()/2);
    	matrix.postTranslate(0, getHeight()/2);
    	canvas.drawBitmap(sourceBitmap, matrix, null);
    	super.onDraw(canvas);
    }
	

}

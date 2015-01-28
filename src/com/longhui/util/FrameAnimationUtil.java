package com.longhui.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * 帧动画工具
 * @author zhonglh
 *
 */
public class FrameAnimationUtil {
	
	public static boolean bPlayingAnim = false;
	
	public static void recycleAnim(View view, int setIndex){
 		Drawable viewDrawable =  view.getBackground();
		if(viewDrawable instanceof AnimationDrawable){
			AnimationDrawable animD = (AnimationDrawable)viewDrawable;
			for(int i=0;i<animD.getNumberOfFrames();i++){
				Drawable drawable =  animD.getFrame(i);
				BitmapDrawable bd = (BitmapDrawable) drawable;
				if(bd!= null  && bd.getBitmap()!=null && !bd.getBitmap().isRecycled()){
					Bitmap bm = bd.getBitmap();
					if(i == setIndex){
						view.setBackgroundDrawable(bd);
					}
					else{
						bm.recycle();
					}
				}
			}
		}
	}
	
	public static void startFrameAnimation(Activity activ, final View view, int resid){
		if(view == null){
			return ;
		}
		try {
			Drawable dd = activ.getResources().getDrawable(resid); 
			
			if(dd instanceof AnimationDrawable){
				AnimationDrawable animD = (AnimationDrawable)dd;
		        final AnimationDrawable newAnimD = new AnimationDrawable();
		        for (int i = 0; i < animD.getNumberOfFrames(); i++) {
					Drawable drawable =  animD.getFrame(i);
					BitmapDrawable bd = (BitmapDrawable) drawable;
					Bitmap bm = bd.getBitmap();
					if(bm != null && !bm.isRecycled()){
						newAnimD.addFrame(bd, animD.getDuration(i));
					}
				}
		        
		        view.setBackgroundDrawable(newAnimD);
				if(newAnimD != null){
					if(newAnimD.isRunning()){
						newAnimD.stop();
					}
					bPlayingAnim = true;
					newAnimD.start();
				}
				int delayMillis = 0;
				for(int i=0; i< newAnimD.getNumberOfFrames(); i++){
					delayMillis += newAnimD.getDuration(i);
				}
				view.postDelayed(new Runnable() {
					
					@Override
					public void run() {
						recycleAnim(view, newAnimD.getNumberOfFrames()-1);
						bPlayingAnim =false;
					}
				}, delayMillis);
			}
			else{
				view.setBackgroundResource(resid);
			}
			
			
		 
		}
		catch(Exception e){
			
		}
		
	}
}

package com.longhui.customview;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;

import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;

public class CustomBitmapDisplay implements BitmapDisplayer{
	private int w;
	private int h;
	private int degree;
	public CustomBitmapDisplay(int w, int h, int degree) {
		// TODO Auto-generated constructor stub
		this.w = w;
		this.h = h;
		this.degree = degree;
	}
	
	@Override
	public void display(Bitmap bitmap, ImageAware imageAware, LoadedFrom arg2) {
		// TODO Auto-generated method stub
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, w, h);
		imageAware.setImageDrawable(new RoundedDrawable(bitmap, degree, 0));
	}
	
    //获得圆角图片的方法  
   public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){  
         
	   if(bitmap == null){
		   return null;
	   }
       Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap  
               .getHeight(), Config.ARGB_8888);  
       Canvas canvas = new Canvas(output);  
  
       final int color = 0xff424242;  
       final Paint paint = new Paint();  
       final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());  
       final RectF rectF = new RectF(rect);  
  
       paint.setAntiAlias(true);  
       canvas.drawARGB(0, 0, 0, 0);  
       paint.setColor(color);  
       canvas.drawRoundRect(rectF, roundPx, roundPx, paint);  
  
       paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));  
       canvas.drawBitmap(bitmap, rect, rect, paint); 
       if(!bitmap.isRecycled())
    	   bitmap.recycle();
       return output;  
   } 
   
   public static class RoundedDrawable extends Drawable {

		protected final float cornerRadius;
		protected final int margin;

		protected final RectF mRect = new RectF(),
				mBitmapRect;
		protected final BitmapShader bitmapShader;
		protected final Paint paint;

		public RoundedDrawable(Bitmap bitmap, int cornerRadius, int margin) {
			this.cornerRadius = cornerRadius;
			this.margin = margin;

			bitmapShader = new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			mBitmapRect = new RectF (margin, margin, bitmap.getWidth() - margin, bitmap.getHeight() - margin);
			
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setShader(bitmapShader);
		}

		@Override
		protected void onBoundsChange(Rect bounds) {
			super.onBoundsChange(bounds);
			mRect.set(margin, margin, bounds.width() - margin, bounds.height() - margin);
			
			// Resize the original bitmap to fit the new bound
			Matrix shaderMatrix = new Matrix();
			shaderMatrix.setRectToRect(mBitmapRect, mRect, Matrix.ScaleToFit.FILL);
			bitmapShader.setLocalMatrix(shaderMatrix);
			
		}

		@Override
		public void draw(Canvas canvas) {
			canvas.drawRoundRect(mRect, cornerRadius, cornerRadius, paint);
		}

		@Override
		public int getOpacity() {
			return PixelFormat.TRANSLUCENT;
		}

		@Override
		public void setAlpha(int alpha) {
			paint.setAlpha(alpha);
		}

		@Override
		public void setColorFilter(ColorFilter cf) {
			paint.setColorFilter(cf);
		}
	}
}

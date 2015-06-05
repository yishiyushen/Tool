package com.longhui.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.DisplayMetrics;
import android.util.Log;

public class UiUtils {

	public static Bitmap bytes2Bimap(byte[] data) {
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		return bm;
	}

	public static Drawable bytes2Drawable(Resources res, byte[] data) {
		Drawable d = null;
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		d = new BitmapDrawable(res, bm);
		return d;
	}
	
	public static Drawable bytes2Drawable(Resources res, byte[] data, int scope) {
		Drawable d = null;
		Bitmap bm = BitmapFactory.decodeByteArray(data, 0, data.length);
		Bitmap newBm = zoom(bm, scope);
		d = new BitmapDrawable(res, newBm);
		return d;
	}

	public static final int LI_IMG_S = 120;
	public static final int SLT_IMG_S = 180;

	public static Bitmap zoom(Bitmap bitmap, int scope) {

		if (bitmap == null) {
			return null;
		}

		int orgW = bitmap.getWidth();
		int orgH = bitmap.getHeight();

		int maxSide = Math.max(orgW, orgH);

		if (scope >= maxSide) {
			return bitmap;
		} else {
			Matrix matrix = new Matrix();
			float scale = (float) scope / maxSide;
			matrix.postScale(scale, scale);
			Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0,
					bitmap.getWidth(), bitmap.getHeight(), matrix, true);
			return resizeBmp;
		}
	}

	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static Drawable readDrawable(Context context, int resId) {
		InputStream is = context.getResources().openRawResource(resId);
		Drawable dr = Drawable.createFromStream(is, null);
		return dr;
	}
	
	public static Drawable readDrawable(Context context, String path) {
		Bitmap bmp = readBitMap(context, path);
		Drawable dr = new BitmapDrawable(context.getResources(), bmp);
		return dr;
	}

	public static Bitmap readBitMap(Context context, String path) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		opt.outHeight = 240;

		File f = new File(path);
		InputStream is = null;
		try {
			is = new FileInputStream(f);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		return BitmapFactory.decodeStream(is, null, opt);
	}

	public static Bitmap readAssetsBitmap(Context context, String fileName) {
		Bitmap image = null;
		AssetManager am = context.getResources().getAssets();
		try {
			InputStream is = am.open(fileName);
			image = BitmapFactory.decodeStream(is);
			is.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return image;
	}

	// 在指定的位图上添加图标
	public static Bitmap complexBitmap(int x, int y, Bitmap baseMap,
			Bitmap markBitmap) {
		// 创建一个和原图同样大小的位图
		// Bitmap newBitmap = Bitmap.createBitmap(imgMapWidth,imgMapHeight,
		// Bitmap.Config.RGB_565);
		Bitmap newBitmap = baseMap;
		if (!newBitmap.isMutable()) {
			newBitmap = baseMap.copy(Bitmap.Config.RGB_565, true);
		}

		Canvas canvas = new Canvas(newBitmap);
		canvas.drawBitmap(markBitmap, x, y, null);// 插入图标
		canvas.save(Canvas.ALL_SAVE_FLAG);
		// 存储新合成的图片
		canvas.restore();
		return newBitmap;
	}

	/**
	 * 图片上画字
	 * */
	public static Bitmap drawTextAtBitmap(Bitmap bitmap, String text) {

		int x = bitmap.getWidth();
		int y = bitmap.getHeight();

		// 创建一个和原图同样大小的位图
		Bitmap newbit = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);

		Canvas canvas = new Canvas(newbit);

		Paint paint = new Paint();

		// 在原始位置0，0插入原图
		canvas.drawBitmap(bitmap, 0, 0, paint);

		paint.setColor(Color.parseColor("#dedbde"));

		paint.setTextSize(20);

		// 在原图指定位置写上字
		canvas.drawText(text, 53, 30, paint);

		canvas.save(Canvas.ALL_SAVE_FLAG);

		// 存储
		canvas.restore();

		return newbit;
	}

	public static Bitmap drawable2Bitmap(Drawable drawable) {
		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		} else if (drawable instanceof NinePatchDrawable) {
			Bitmap bitmap = Bitmap
					.createBitmap(
							drawable.getIntrinsicWidth(),
							drawable.getIntrinsicHeight(),
							drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
									: Bitmap.Config.RGB_565);
			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, drawable.getIntrinsicWidth(),
					drawable.getIntrinsicHeight());
			drawable.draw(canvas);
			return bitmap;
		} else {
			return null;
		}
	}
	
    //获得圆角图片的方法  
   public static Bitmap getRoundedCornerBitmap(Bitmap bitmap,float roundPx){  
         
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
  
       return output;  
   } 

	public static void logScreenInfo(Activity activ) {
		final String TAG = "SCREEN-INFO";

		DisplayMetrics dm = new DisplayMetrics();
		dm = activ.getResources().getDisplayMetrics();

		float density = dm.density; // 屏幕密度（像素比例：0.75/1.0/1.5/2.0）
		int densityDPI = dm.densityDpi; // 屏幕密度（每寸像素：120/160/240/320）
		float xdpi = dm.xdpi;
		float ydpi = dm.ydpi;
		float screenWidth = dm.widthPixels; // 屏幕宽（像素，如：480px）
		float screenHeight = dm.heightPixels; // 屏幕高（像素，如：800px）

		Log.e(TAG + "  DisplayMetrics", "xdpi=" + xdpi + "; ydpi=" + ydpi);
		Log.e(TAG + "  DisplayMetrics", "density=" + density + "; densityDPI="
				+ densityDPI);
		Log.e(TAG + "  DisplayMetrics(111)", "screenWidth=" + screenWidth
				+ "; screenHeight=" + screenHeight);
	}
}

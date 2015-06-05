package com.longhui.util;

import java.io.FileDescriptor;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.R.integer;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;
/**
 * 网络图片下载缓存工具类
 * @author zhonglh
 *
 */
public class LoadImgUtil {
	/** 最大并行线程数 */
	//private static final int Max = 5;
	/** java 自带的线程池 */
	//private ExecutorService threadPools = null;
	
	private LruCacheUtil<Bitmap> mMemoryCache;

	private DiskLruCacheUtil mDiskCache;
	
	private static LoadImgUtil loadImgUtil;
	
	private String dirName;
	
	private boolean bOpen = false;

	private LoadImgUtil(Context context, String uniqueName) {
		// TODO Auto-generated constructor stub
		mMemoryCache = new LruCacheUtil<Bitmap>();
		mDiskCache = new DiskLruCacheUtil(context);
		dirName = uniqueName;
		
	}
	
	public static LoadImgUtil getSingleInstance(Context context, String uniqueName){
		if(loadImgUtil == null){
			loadImgUtil = new LoadImgUtil(context, uniqueName);
		}
		
		return loadImgUtil;
	}
	
	public boolean openCache(){
		bOpen = mDiskCache.open(dirName);
		return bOpen;
	}
	
	public static int calculateInSampleSize(BitmapFactory.Options options,  
	        int reqWidth, int reqHeight) {  
	    // 源图片的高度和宽度  
	    final int height = options.outHeight;  
	    final int width = options.outWidth;  
	    int inSampleSize = 1;  
	    if (height > reqHeight || width > reqWidth) {  
	        // 计算出实际宽高和目标宽高的比率  
	        final int heightRatio = Math.round((float) height / (float) reqHeight);  
	        final int widthRatio = Math.round((float) width / (float) reqWidth);  
	        // 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高  
	        // 一定都会大于等于目标的宽和高。  
	        inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;  
	    }  
	    return inSampleSize;  
	}  
	
	public static Bitmap decodeSampledBitmapFromFd(FileDescriptor fd,  
	        int reqWidth, int reqHeight) {  
	    // 第一次解析将inJustDecodeBounds设置为true，来获取图片大小  
		if(fd == null){
			return null;
		}
	    final BitmapFactory.Options options = new BitmapFactory.Options();  
	    options.inJustDecodeBounds = true;  
	    BitmapFactory.decodeFileDescriptor(fd, null, options);  
	    // 调用上面定义的方法计算inSampleSize值  
	    options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);  
	    // 使用获取到的inSampleSize值再次解析图片  
	    options.inJustDecodeBounds = false; 
	    Bitmap bitmap =  BitmapFactory.decodeFileDescriptor(fd, null, options); ; 
	    if(bitmap == null){
	    	Log.d("decodeSampledBitmapFromStream"," ----bitmap - - null");
	    }
	    
	    return  bitmap;
	} 
	
	public Bitmap loadImagFromMenmory(String imageUrl,int w, int h){
		Bitmap bitmap = null;
		/** 从内存缓存中读取 */
		bitmap = mMemoryCache.getBitmapFromMemoryCache(imageUrl);
		if(bitmap != null){
			return bitmap;
		}
		
		/** 从磁盘缓存中读取 */
		FileDescriptor fd = mDiskCache.readFromDiskCacheRtFileDescriptor(imageUrl);
	
		if (fd != null) {
			//Log.d("loadImagFromMenmory", "------url="+imageUrl);
			bitmap = decodeSampledBitmapFromFd(fd,w,h);
			
			if (bitmap != null) {
				//Log.d("loadImagFromMenmory", "-----img--not null");
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, w, h);
				bitmap = getRoundedCornerBitmap(bitmap ,10);
				mMemoryCache.addBitmapToMemoryCache(imageUrl, bitmap);
				return bitmap;
			}
		}
		return bitmap;
	}
	
	public void addBitmapToMemmory(String imageUrl,Bitmap bitmap){
		mMemoryCache.addBitmapToMemoryCache(imageUrl, bitmap);
	}
	
	public Bitmap writeToDiskCache(String key,InputStream in,int w,int h){
		return mDiskCache.writeToDiskCache(key, in, w, h);
	}

	public Bitmap loadImage(final ImageView imageView, final String imageUrl,final int w,final int h,
			final ImageDownloadCallBack imageDownloadCallBack) {
		Bitmap bitmap = null;
		/** 从内存缓存中读取 */
		bitmap = mMemoryCache.getBitmapFromMemoryCache(imageUrl);
		
		if (bitmap != null) {
			return bitmap;
		}
		

		/** 从磁盘缓存中读取 */
		FileDescriptor fd = mDiskCache.readFromDiskCacheRtFileDescriptor(imageUrl);
	
		if (fd != null) {
			//Log.d("loadImage", "------url="+imageUrl);
			bitmap = decodeSampledBitmapFromFd(fd,w,h);
			
			if (bitmap != null) {
				//Log.d("loadImage", "-----img--not null");
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, w, h);
				bitmap = getRoundedCornerBitmap(bitmap ,10);
				mMemoryCache.addBitmapToMemoryCache(imageUrl, bitmap);
				return bitmap;
			}
		}

		
		//		if (in != null) {
//			BitmapFactory.Options options = new BitmapFactory.Options();
//			options.inSampleSize = 2;
//			bitmap = BitmapFactory.decodeStream(in, null, options);
//			
//			if (bitmap != null) {
//				
//				bitmap = ThumbnailUtils.extractThumbnail(bitmap, w, h);
//				bitmap = getRoundedCornerBitmap(bitmap ,10);
//				mMemoryCache.addBitmapToMemoryCache(imageUrl, bitmap);
//				return bitmap;
//			}
//		}

		if (imageUrl != null && !"".equals(imageUrl)) {
//			if (threadPools == null) {
//				threadPools = Executors.newFixedThreadPool(Max);
//			}

			final Handler hand = new Handler() {
				@Override
				public void handleMessage(Message msg) {

					if (msg.what == 111 && imageDownloadCallBack != null) {
						Bitmap bit = (Bitmap) msg.obj;
						imageDownloadCallBack.onImageDownload(imageView, bit);
					}
					super.handleMessage(msg);
				}
			};

			Thread thread = new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					InputStream in = getInputStreamFromURL(imageUrl);
					if(in != null){
						Bitmap bitmap = mDiskCache.writeToDiskCache(imageUrl,in,w,h);
						
						Message msg = hand.obtainMessage();
						bitmap = ThumbnailUtils.extractThumbnail(bitmap, w, h);
						bitmap = getRoundedCornerBitmap(bitmap ,10);
						mMemoryCache.addBitmapToMemoryCache(imageUrl, bitmap);
						msg.what = 111;
						msg.obj = bitmap;
						hand.sendMessage(msg);
						try {
							in.close();
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}

	//				loadBitmapFromNet(imageUrl,hand);
				}
			});

			//threadPools.execute(thread);
			ThreadPoolUtils.execute(thread);
		}

		return bitmap;
	}
	
	
//	public void executeThread(Thread thread){
//		if (threadPools == null) {
//			threadPools = Executors.newFixedThreadPool(Max);
//		}
//		threadPools.execute(thread);
//	}
	
	public void flushDiskCache(){
		if(mDiskCache != null){
			mDiskCache.flush();
		}
	}
	
	public void closeDiskCache(){
		if(mDiskCache != null){
			mDiskCache.close();
			mDiskCache = null;
		}
		
		if(mMemoryCache != null){
			mMemoryCache.clearCache();
			mMemoryCache = null;
		}
		loadImgUtil = null;
	}
	


	public InputStream getInputStreamFromURL(String url) {
		Log.e("-------", "url= "+url);
		HttpGet httpGet = new HttpGet(url);
		HttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, 30 * 1000);
		HttpConnectionParams.setSoTimeout(httpParams, 30 * 1000);
		HttpClient httpClient = new DefaultHttpClient(httpParams);
		try {
			HttpResponse res = httpClient.execute(httpGet);
			if (res.getStatusLine().getStatusCode() == 200) {
				return res.getEntity().getContent();
			}
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private void loadBitmapFromNet(String imageUrl,Handler hand){
		HttpURLConnection urlConnection = null;  
		InputStream in = null;
        try {  
            final URL url = new URL(imageUrl);  
            urlConnection = (HttpURLConnection) url.openConnection();  
            in = urlConnection.getInputStream();
			if(in != null){
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 2;
				Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
				mMemoryCache.addBitmapToMemoryCache(imageUrl, bitmap);
				mDiskCache.writeToDiskCache(imageUrl,in);
				Message msg = hand.obtainMessage();
				msg.what = 111;
				msg.obj = bitmap;
				hand.sendMessage(msg);
			}
        } catch (final IOException e) {  
            e.printStackTrace();  
        } finally {  
            if (urlConnection != null) {  
                urlConnection.disconnect();  
            }  
            try {  

                if (in != null) {  
                    in.close();  
                }  
            } catch (final IOException e) {  
                e.printStackTrace();  
            }  
        }  
	}

	public interface ImageDownloadCallBack {
		public void onImageDownload(ImageView imageview, Bitmap bitmap);
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

}

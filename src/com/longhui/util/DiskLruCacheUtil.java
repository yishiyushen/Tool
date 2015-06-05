package com.longhui.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;
/**
 *硬盘缓存
 * @author zhonglh
 *
 */
public class DiskLruCacheUtil {
	private DiskLruCache mDiskCache = null;
	private Context context;
	private static final int MAX_DISK_MEMORY_CACHE = 30 * 1024 * 1024; // 30M;

	public DiskLruCacheUtil(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	/**
	 * 只需打开一次
	 * 
	 * @param uniqueName
	 *            缓存目录名
	 */
	public boolean open(String uniqueName) {
		if(mDiskCache != null){
			return true;
		}
		File cacheDir = getDiskCacheDir(context, uniqueName);
		if(cacheDir == null){
			return false;
		}
		if (!cacheDir.exists()) {
			cacheDir.mkdir();
		}
		try {
			mDiskCache = DiskLruCache.open(cacheDir, getApplication(context),
					1, MAX_DISK_MEMORY_CACHE);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 与open对应关闭缓存
	 */
	public void close() {
		if (mDiskCache != null) {
			try {
				mDiskCache.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 将数据写入缓存中
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap writeToDiskCache(String key,InputStream in){
		OutputStream outputStream = null;
		if(mDiskCache != null){
			String md5Key = hashKeyForDisk(key);
			try {
				 DiskLruCache.Editor mEditor = mDiskCache.edit(md5Key);
				if(mEditor != null){
					outputStream = mEditor.newOutputStream(0);
				}
				saveToDiskCache(in,outputStream, mEditor);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		FileDescriptor fd =readFromDiskCacheRtFileDescriptor(key);
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 2;
		Bitmap bitmap = BitmapFactory.decodeFileDescriptor(fd, null, options);
		return bitmap;
	}

	/**
	 * 将数据写入缓存中
	 * 
	 * @param key
	 * @return
	 */
	public Bitmap writeToDiskCache(String key,InputStream in,int w,int h){
		OutputStream outputStream = null;
		if(mDiskCache != null){
			String md5Key = hashKeyForDisk(key);
			try {
				 DiskLruCache.Editor mEditor = mDiskCache.edit(md5Key);
				if(mEditor != null){
					outputStream = mEditor.newOutputStream(0);
				}
				saveToDiskCache(in,outputStream, mEditor);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		FileDescriptor fd = readFromDiskCacheRtFileDescriptor(key);
		if(fd == null){
			Log.d("writeToDiskCache", "-----fd==null");
		}
		Bitmap bitmap = LoadImgUtil.decodeSampledBitmapFromFd(fd, w, h);
		if(bitmap == null){
			Log.d("writeToDiskCache", "-----bitmap==null");
		}
		return bitmap;
	}
	
	private void saveToDiskCache(InputStream in, OutputStream out,
			DiskLruCache.Editor editor) {
		if (in == null || out == null) {
			return;
		}
		boolean bflag = false;
		BufferedInputStream bufIn = new BufferedInputStream(in,8*1024);
		BufferedOutputStream bufOut = new BufferedOutputStream(out,8*1024);
		int b;
		try {
			while ((b = bufIn.read()) != -1) {
				bufOut.write(b);
			}
			bflag = true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				bufIn.close();
				bufOut.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			if (bflag) {
				editor.commit();
				Log.d("-----diskcache---", " wirte to disk cache succeed---");
			} else {
				Log.d("-----diskcache---", " wirte to disk cache failed---");
				editor.abort();
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	/**
	 * 从缓存中读取数据
	 * 
	 * @param key
	 * @return
	 */
	public InputStream readFromDiskCache(String key) {
		InputStream inputStream = null;
		if (mDiskCache != null) {
			String md5Key = hashKeyForDisk(key);
			DiskLruCache.Snapshot snapshot;
			try {
				snapshot = mDiskCache.get(md5Key);
				if (snapshot != null) {
					inputStream = snapshot.getInputStream(0);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return inputStream;
	}

	/**
	 * 从缓存中读取数据
	 * 
	 * @param key
	 * @return
	 */
	public FileDescriptor readFromDiskCacheRtFileDescriptor(String key) {
		FileInputStream fileInputStream = null;
		FileDescriptor fileDescriptor = null;
		 Bitmap bitmap = null;  
		if (mDiskCache != null) {
			String md5Key = hashKeyForDisk(key);
			DiskLruCache.Snapshot snapshot;
			try {
				snapshot = mDiskCache.get(md5Key);
				if (snapshot != null) {
					fileInputStream = (FileInputStream) snapshot.getInputStream(0);  
					fileDescriptor = fileInputStream.getFD();  
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		     // 将缓存数据解析成Bitmap对象  
           
		}
		return fileDescriptor;
	}
	/**
	 * 获取缓存数据总的大小
	 * 
	 * @return
	 */
	public long sizeOf() {
		long size = 0;
		if (mDiskCache != null) {
			size = mDiskCache.size();
		}
		return size;
	}

	/**
	 * 移除缓存数据大小
	 * 
	 * @param key
	 * @return
	 */
	public boolean remove(String key) {
		if (mDiskCache != null) {
			String md5Key = hashKeyForDisk(key);
			try {
				return mDiskCache.remove(md5Key);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return false;
	}

	/**
	 * 移除所有数据
	 */
	public void removeAll() {
		if (mDiskCache != null) {
			try {
				mDiskCache.delete();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 这个方法用于将内存中的操作记录同步到日志文件（也就是journal文件）当中。
	 * 这个方法非常重要，因为DiskLruCache能够正常工作的前提就是要依赖于journal文件中的内容。
	 * 其实并不是每次写入缓存都要调用一次flush()方法的，频繁地调用并不会带来任何好处，只会额外增加同步journal文件的时间。
	 * 比较标准的做法就是在Activity的onPause()方法中去调用一次flush()方法就可以了。
	 */
	public void flush() {
		if (mDiskCache != null) {
			try {
				mDiskCache.flush();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 获取缓存目录文件
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath = null;
		File file;
		if (Environment.MEDIA_MOUNTED.equals(Environment
				.getExternalStorageState())
				|| !Environment.isExternalStorageRemovable()) {
			file = context.getExternalCacheDir();
		} else {
			file = context.getCacheDir();			
		}
		if(file == null){
			return null;
		}
		cachePath = file.getPath();
		

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取应用版本号
	 * 
	 * @param context
	 * @return
	 */
	public int getApplication(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
}

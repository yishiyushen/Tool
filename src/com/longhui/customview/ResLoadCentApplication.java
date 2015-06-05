package com.longhui.customview;

import java.io.File;

import android.app.Application;
import android.graphics.Bitmap;

import com.longhui.tool.R;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;

public class ResLoadCentApplication extends Application {
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		initImageLoadConfig();
		super.onCreate();
	}
	
	public void initImageLoadConfig(){
		ImageLoaderConfiguration cogfig = new ImageLoaderConfiguration.Builder(this)
				.memoryCacheExtraOptions(400, 800)
				.threadPoolSize(3)
				.threadPriority(Thread.NORM_PRIORITY-2)
				.denyCacheImageMultipleSizesInMemory()
				.memoryCache(new UsingFreqLimitedMemoryCache(5*1024*1024))
				.memoryCacheSize(5*1024*1024)
				.discCacheSize(60*1024*1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.discCacheFileCount(150)
				.discCache(new UnlimitedDiskCache(new File(this.getExternalCacheDir()+"image")))
				.defaultDisplayImageOptions(getDisplayOptions())
				.imageDownloader(new BaseImageDownloader(this, 5*1000, 30*1000))
				.writeDebugLogs()
				.build();
		ImageLoader.getInstance().init(cogfig);
	}
	
	private DisplayImageOptions getDisplayOptions(){

		DisplayImageOptions options = null;
		options = new DisplayImageOptions.Builder()
				  .cacheInMemory(true)
				  .cacheOnDisc(true)
				  .considerExifParams(true)
				  .imageScaleType(ImageScaleType.EXACTLY_STRETCHED)
				  .bitmapConfig(Bitmap.Config.RGB_565)
				  .resetViewBeforeLoading(true)
				  .build();
				  
		return options;
	}
}

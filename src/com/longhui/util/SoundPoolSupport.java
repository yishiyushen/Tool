package com.longhui.util;
 
import java.util.HashMap;
import java.util.Map;
import android.annotation.SuppressLint;
import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.media.SoundPool.OnLoadCompleteListener;
/**
 * 短小促声音播放工具类
 * @author zhonglh
 *
 */
public class SoundPoolSupport {
	
    
	private final int MAX_STREAMS = 5;
	
	private SoundPool spool;
	private Context context;
	
	private Map<Integer, Integer> soundIDMap;
	private Map<Integer, Integer> streamIDMap;
	
	public void releaseAll(){
		if(spool!= null) {
			spool.release();
			spool= null;
		}
		if(soundIDMap!=null){
			soundIDMap.clear();
			soundIDMap= null;
		}
		if(streamIDMap!=null){
			streamIDMap.clear();
			streamIDMap= null;
		}
	}
	@SuppressLint("UseSparseArrays")
	public SoundPoolSupport(Context context) {
		this.context = context;
		 soundIDMap = new HashMap<Integer, Integer>();
		 streamIDMap = new HashMap<Integer, Integer>();
		 // 第一个参数为同时播放数据流的最大个数，第二数据流类型，第三为声音质量
//		 spool = new SoundPool(MAX_STREAMS,  AudioManager.STREAM_MUSIC, 0);
		 spool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
	}
 
	private void spPlay(final int resId, final int priority, final int loop){
		final int soundID = spool.load(context, resId,priority);
		soundIDMap.put(resId, soundID);
		OnLoadCompleteListener listener = new OnLoadCompleteListener() {
			
			@Override
			public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
				if(0 == status){
				int streamID = soundPool.play(sampleId, 0.4f, 0.4f, priority, loop, 1);
				streamIDMap.put(resId, streamID);
				}
				}
		};
		
		spool.setOnLoadCompleteListener(listener);
		
	}
	
/*	public void spPause(int resId){
		final int streamID = plMap.get(resId);
		spool.pause(streamID);
	}*/
	
	public void play(int resId){
		play(resId, 0, 0);
	}
	/**
	 * resId资源名
	 * 播放短、小的声音资源
	 * @param priority stream priority (0 = lowest priority)
	 * @param loop loop mode (0 = no loop, -1 = loop forever)
	 * */
	public void play(int resId, final int priority, final int loop){
		if(soundIDMap==null){
			soundIDMap = new HashMap<Integer, Integer>();
		}
		if(streamIDMap==null){
			streamIDMap = new HashMap<Integer, Integer>();
		}
		if(spool == null){
			spool = new SoundPool(MAX_STREAMS,  AudioManager.STREAM_MUSIC, 0);
		}
		if(soundIDMap.get(resId) == null){
			spPlay(resId, priority, loop);
		}
		else{//存在，即已经load过，只需要play即可。
			final Integer soundID = soundIDMap.get(resId);
			int streamID = spool.play(soundID, 0.4f, 0.4f, priority, loop, 1);
			streamIDMap.put(resId, streamID);
		}
	}
	
	public void stop(int resId){
		if(streamIDMap==null || spool == null) return ;
		if(streamIDMap.get(resId) != null){
			spool.stop(streamIDMap.get(resId));
		}
	}
	
	public SoundPool getSoundPool(){
		return spool;
	}
	 
	
}

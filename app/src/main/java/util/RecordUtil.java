package util;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.IOException;


public class RecordUtil
{
	private static MediaRecorder mediaRecorder = null;
	private static MediaPlayer mediaPlayer = null;
	
	public static final void startRecord(String name)
	{
		mediaRecorder = new MediaRecorder();
		mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC); 
    //设置封装格式  
		mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP); 
		mediaRecorder.setOutputFile(name); 
		//设置编码格式  
		mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB); 
		
		try 
		{ 
			mediaRecorder.prepare(); 
        } catch (IOException e)
        { 
        	e.printStackTrace();
        } 
 
		mediaRecorder.start(); 
	}
	
	public static final void stopRecord()
	{
		if(mediaRecorder!=null)
		{
			mediaRecorder.stop(); 
			mediaRecorder.release(); 
			mediaRecorder = null;
			
		}
	}
	
	
	public static final void play(String name)
	{
		mediaPlayer = new MediaPlayer();
		try
		{
			Log.v("xxx","音频文件名:"+name);
			mediaPlayer.setDataSource(name);
			mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener()
			{
				
				@Override
				public void onCompletion(MediaPlayer mp)
				{
					
					if(mp!=null)
					{
						mp.stop();
						mp.release();
						mp = null;
					}
					
				}
			});
			mediaPlayer.prepare();
			mediaPlayer.start();
			
		} catch (Exception e)
		{
			e.printStackTrace();
			
			if(mediaPlayer!=null)
			{
				mediaPlayer.stop();
				mediaPlayer.release();
				mediaPlayer = null;
			}
		
		}
	}
	
}

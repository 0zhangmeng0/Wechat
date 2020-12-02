package service;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.os.IBinder;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import bean.Message;
import config.Config;
import db.MessageDB;
import util.HttpURLConnectionUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;


public class MessageService extends Service 
{
	public static final String MESSAGE_ACTION = "org.pzn.wxmsg.service.MessageService";
	
	private Timer messageTimer = null;
	private MessageDB messageDB = null;

	@Override
	public IBinder onBind(Intent intent)
	{
		return null;
	}
	
	@Override
	public void onCreate()
	{
		super.onCreate();
		
		try
		{
			messageDB = new MessageDB(this);
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		messageTimer = new Timer();
		messageTimer.schedule(new TimerTask()
		{
			@Override
			public void run()
			{
				loadMessage();
			}
		}, 1000, 20000);
	}
	
	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		if(messageTimer!=null)
		{
			messageTimer.cancel();
		}
		super.onDestroy();
	}

	
	private void loadMessage()
	{
		Map<String, String> params = new HashMap<String, String>();
		try
		{
			params.put("userId", Config.user.getString("id"));
			String json = HttpURLConnectionUtil.doPost(Config.URL_QUERY_UNREAD_MESSAGE, params);
			Log.v("xxx", json);
			JSONObject jsonObject = new JSONObject(json);
			if(jsonObject.getBoolean("success"))
			{
				JSONArray array = jsonObject.getJSONArray("data");
				
				for(int i = 0;i<array.length();i++)
				{
					JSONObject jo = array.getJSONObject(i);
					Message message = new Message(null, jo.getInt("type"), jo.getString("text"), jo.getJSONObject("toUser").getLong("id"), jo.getJSONObject("fromUser").getString("username"), jo.getJSONObject("fromUser").getLong("id"), false, jo.getLong("sendTime"));
					messageDB.addMessage(message);
				}
				
				if(array.length()>0)
				{
					Log.v("xxxx", "发送消息广播");
					MessageService.this.sendBroadcast(new Intent(MESSAGE_ACTION));
					voiceAlert();
				}
			}
			
		} catch (Exception e)
		{
			e.printStackTrace();
		}
				
	}
	

	private void voiceAlert()
	{
		try
		{
			MediaPlayer mp = new MediaPlayer();
			mp.reset();                    
			mp.setDataSource(this,RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION));
			mp.prepare();
			mp.start();
			
		} catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}
	}
}

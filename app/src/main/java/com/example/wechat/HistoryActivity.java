package com.example.wechat;

import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;

import bean.Message;
import db.MessageDB;

import java.util.List;

import bean.Message;


public class HistoryActivity extends BaseMessageActivity
{
	private MessageDB db = null;
	private JSONObject userJsonObject = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_history);
		try
		{
			db = new MessageDB(this);
		} catch (JSONException e1)
		{
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		listView = (ListView)findViewById(R.id.listView);
		messageAdapter = new MyMessageAdapter();
		listView.setAdapter(messageAdapter);
		
		try
		{
			userJsonObject = new JSONObject(getIntent().getStringExtra("data"));
			setTitle("与\""+userJsonObject.getString("username")+"\"聊天中");
			
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		new Thread()
		{
			public void run() 
			{
				queryAllMesasge();
			};
		}.start();
	}


	
	private void queryAllMesasge()
	{
		try
		{
			final List<Message> messages = db.queryAllMessageByFromUserid(userJsonObject.getLong("id"));
			Log.v("xxx", "共有历史数据:"+messages.size());
			if(messages.size()>0)
			{
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						showMessage(messages);	
					}
				});
			}
			
			db.updateMessageBeRead(userJsonObject.getLong("id"));
			
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}
	
}

package com.example.wechat;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import org.json.JSONException;
import org.json.JSONObject;
import bean.Message;
import config.Config;
import db.MessageDB;
import service.MessageService;
import util.HttpURLConnectionUtil;
import util.RecordUtil;
import util.StringUtil;
import util.UIUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ChatActivity extends BaseMessageActivity implements OnClickListener
{ 
	private static final int REQUEST_IMAGE = 1;
	
	
	private JSONObject userJsonObject = null;
	
	private Button sendTextButton = null;
	private EditText messageEditText = null;
	private MessageDB db = null;
	
	
	private MessageBroadcastReciever messageBroadcastReciever = null;
	
	private String voiceName = null;
	
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		sendTextButton = (Button)findViewById(R.id.sendTextButton);
		messageEditText = (EditText)findViewById(R.id.messageEditText);
		listView = (ListView)findViewById(R.id.listView);

		sendTextButton.setOnClickListener(this);
		
		try
		{
			userJsonObject = new JSONObject(getIntent().getStringExtra("data"));
			setTitle("与\""+userJsonObject.getString("username")+"\"聊天中");
			
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		try
		{
			db = new MessageDB(this);
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		messageAdapter = new MyMessageAdapter();
		listView.setAdapter(messageAdapter);
		
		messageBroadcastReciever = new MessageBroadcastReciever();
		queryUnreadMesasge();
		

		 
	}
	
	@Override
	protected void onResume()
	{
		UIUtil.initUIUtil(this);

		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageService.MESSAGE_ACTION);
		this.registerReceiver(messageBroadcastReciever, intentFilter);
		super.onResume();
	}
	
	@Override
	protected void onPause()
	{
		unregisterReceiver(messageBroadcastReciever);
		super.onPause();
	}
	
	


	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_chat, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.menu_voice:
			traceVoice();
			break;
			
		case R.id.menu_image:
			Intent intent = new Intent(Intent.ACTION_GET_CONTENT); 
			intent.setType("image/*"); 
			startActivityForResult(intent,REQUEST_IMAGE); //xxx 为自定义的一个整数
			break;
			
		case R.id.menu_records:
			Intent inte = new Intent(this, HistoryActivity.class);
			inte.putExtra("data", userJsonObject.toString());
			startActivity(inte);
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		super.onActivityResult(requestCode, resultCode, data);
		
		if ( requestCode == REQUEST_IMAGE ) 
		{ 
			if (resultCode != RESULT_OK) 
			{   
				return;   
			}

			if(data == null)    
			{
				return;
			}

			Uri selectedImage = data.getData();
			String[] filePathColumn = { MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(selectedImage,filePathColumn, null, null, null);
			cursor.moveToFirst();
			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			Log.v("xxx", picturePath);
			sendImage(picturePath);
		}
	}
	
	/**
	 * 发送图片
	 * @param fileName
	 */
	private void sendImage(String fileName)
	{
		File f = new File(fileName);
		if(f.length() > 1024*800)
		{
			UIUtil.toast("图片太大，无法发送");
			return;
		}
		sendFile(2, fileName);
	}
	
	
	/**
	 * 开始录音
	 */
	private void traceVoice()
	{
		ProgressDialog pd = new ProgressDialog(this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setTitle("请说话");
		pd.setMessage("请对准手机说话,尽量在20秒内...");
		pd.setButton("发送", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				RecordUtil.stopRecord();
				sendFile(3,Config.PATH_BASE+voiceName);
			}
		});
		pd.setButton2("取消", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				RecordUtil.stopRecord();
				new File(Config.PATH_BASE+voiceName).delete();
			}
		});
		pd.show();
		
		voiceName = "/voice/"+System.currentTimeMillis()+".mp3";
		File f = new File(Config.PATH_BASE+voiceName);
		if(!f.getParentFile().exists())
		{
			f.getParentFile().mkdirs();
		}
		RecordUtil.startRecord(Config.PATH_BASE+voiceName);
	}
	
	
	
	/**
	 * 发送语音消息
	 */
	private void sendFile(final int type,final String fileName)
	{
		UIUtil.showProgressDialog("请稍后", "正在发送消息...", 100, true, false, null);
		new Thread()
		{
			public void run() 
			{
				Map<String, String> params = new HashMap<String, String>();
				Map<String, File> files = new HashMap<String, File>();
				try
				{
					params.put("message.type", type+"");
					params.put("message.text", fileName);
					params.put("message.fromUser.id", Config.user.getLong("id")+"");
					params.put("message.toUser.id", userJsonObject.getLong("id")+"");
					files.put("message.file", new File(fileName));
					String json = HttpURLConnectionUtil.doPost(Config.URL_SEND_TEXT_MESSAGE, params,files);
					Log.v("xxx", json);
					JSONObject jsonObject = new JSONObject(json);
					if(!jsonObject.getBoolean("success"))
					{
						UIUtil.toast("语音消息发送失败");
					}
					
					final Message m1 = new Message(null, type, fileName,userJsonObject.getLong("id"), "我", Config.user.getLong("id"), true, SystemClock.currentThreadTimeMillis());
					db.addMessage(m1);
					
					runOnUiThread(new Runnable()
					{
						
						@Override
						public void run()
						{
							showOneMessage(m1);
						}
					});
					
				} catch (Exception e)
				{
					e.printStackTrace();
					UIUtil.showMesssageDialog("网络异常");
				}
				
				UIUtil.cancelProgress();
				
			};
		}.start(); 
	}
	
	/**
	 * 发送文本消息
	 */
	private void sendText()
	{
		final String message = messageEditText.getText().toString();
		if(StringUtil.isEmptyOrNull(message))
		{
			UIUtil.toast("消息为空");
			return;
		}
		
		messageEditText.setText(null);

		UIUtil.showProgressDialog("请稍后", "正在发送消息...", 100, true, false, null);
		new Thread()
		{
			public void run() 
			{
				Map<String, String> params = new HashMap<String, String>();
				try
				{
					params.put("message.type", "1");
					params.put("message.text", message);
					params.put("message.fromUser.id", Config.user.getLong("id")+"");
					params.put("message.toUser.id", userJsonObject.getLong("id")+"");
					String json = HttpURLConnectionUtil.doPost(Config.URL_SEND_TEXT_MESSAGE, params);
					Log.v("xxx", json);
					JSONObject jsonObject = new JSONObject(json);
					if(!jsonObject.getBoolean("success"))
					{
						UIUtil.toast("消息\""+message+"\"发送失败");
						
					}
					
					final Message m1 = new Message(null, 1, message,userJsonObject.getLong("id"), "我", Config.user.getLong("id"), true, SystemClock.currentThreadTimeMillis());
					db.addMessage(m1);
					
					runOnUiThread(new Runnable()
					{
						
						@Override
						public void run()
						{
							showOneMessage(m1);
						}
					});
					
				} catch (Exception e)
				{
					e.printStackTrace();
					UIUtil.showMesssageDialog("网络异常");
				}
				
				UIUtil.cancelProgress();
				
			};
		}.start(); 
	}

	
	private void showOneMessage(Message message)
	{
		dataList.add(message);
		messageAdapter.notifyDataSetChanged();
		listView.setSelection(dataList.size()-1);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.sendTextButton:
			sendText();
			break;

		default:
			break;
		}
	};
	
	private void queryUnreadMesasge()
	{
		try
		{
			final List<Message> messages = db.queryUnreadMessageByUserid(userJsonObject.getLong("id"));
			
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
	
	private class MessageBroadcastReciever extends BroadcastReceiver
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.v("xxx", "=======================接收到广播============================");
			queryUnreadMesasge();
		}
	}
}

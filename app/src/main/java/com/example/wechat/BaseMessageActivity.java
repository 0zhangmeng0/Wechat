package com.example.wechat;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;


import bean.Message;
import config.Config;
import util.HttpURLConnectionUtil;
import util.RecordUtil;
import util.UIUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;



public class BaseMessageActivity extends Activity implements OnItemClickListener
{
	
	protected List<Message> dataList = new ArrayList<>();
	protected MyMessageAdapter messageAdapter = null;
	protected ListView listView = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	}
	
	
	@Override
	protected void onResume()
	{
		UIUtil.initUIUtil(this);
		super.onResume();
	}

	
	protected void showMessage(List<Message> messages)
	{
		dataList.addAll(messages);
		messageAdapter.notifyDataSetChanged();
		listView.setSelection(dataList.size()-1);
	}
	
	protected void playVoice(final Message m)
	{
		if(!"我".equals(m.getFromUser()))
		{
			final File f = new File(Config.PATH_BASE+m.getContent());
			Log.v("xxx", f.getAbsolutePath());
			if(f.exists())
			{
				RecordUtil.play(f.getAbsolutePath());
				
			}else 
			{
				new Thread()
				{
					public void run() 
					{
						if(HttpURLConnectionUtil.downLoadFile(Config.URL_BASE+m.getContent(), f, false))
						{
							RecordUtil.play(f.getAbsolutePath());
						
						}else
						{
							UIUtil.toast("声音文件加载失败，无法播放");
						}
						
					};
				}.start();
				
			}
			
		}else
		{
			File f = new File(m.getContent());
			if(f.exists())
			{
				RecordUtil.play(f.getAbsolutePath());
				
			}else
			{
				UIUtil.toast("声音文件加载失败，无法播放");
			}
			
		}
	}
	
	
	protected class MyMessageAdapter extends BaseAdapter
	{

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			if(convertView == null)
			{
				convertView = LayoutInflater.from(BaseMessageActivity.this).inflate(R.layout.activity_chat_item, null);
			}
			
			ViewGroup vg = (ViewGroup)convertView;
			for(int i = 0;i<vg.getChildCount();i++)
			{
				vg.getChildAt(i).setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						onItemClick(null, null, position, position);
					}
				});
			}

			TextView myMessage = (TextView) convertView.findViewById(R.id.myMessageTextView);
			TextView herMessage = (TextView) convertView.findViewById(R.id.herMessageTextView);
			View myImageLayout = convertView.findViewById(R.id.myImageLayout);
			View herImageLayout = convertView.findViewById(R.id.herImageLayout);
			View myVoiceLayout = convertView.findViewById(R.id.myVoiceLayout);
			View herVoiceLayout = convertView.findViewById(R.id.herVoiceLayout);

			ImageView myImageView = (ImageView)convertView.findViewById(R.id.myImageView);
			final ImageView herImageView = (ImageView)convertView.findViewById(R.id.herImageView);

			myMessage.setVisibility(View.GONE);
			herMessage.setVisibility(View.GONE);
			myImageLayout.setVisibility(View.GONE);
			herImageLayout.setVisibility(View.GONE);
			myVoiceLayout.setVisibility(View.GONE);
			herVoiceLayout.setVisibility(View.GONE);
			
			final Message m = dataList.get(position);
			
			switch (m.getType())
			{
			case 1:
				if("我".equals(m.getFromUser()))
				{
					myMessage.setVisibility(View.VISIBLE);
					myMessage.setText(m.getContent());
					
				}else
				{
					herMessage.setVisibility(View.VISIBLE);
					herMessage.setText(m.getContent());
				}
				break;
			case 2:
				if("我".equals(m.getFromUser()))
				{
					myImageLayout.setVisibility(View.VISIBLE);
					File file = new File(m.getContent());
					Log.v("xxx", file.getAbsolutePath());
					if(file.exists())
					{
						myImageView.setImageBitmap(BitmapFactory.decodeFile(file.getAbsolutePath()));
						
					}else
					{
						myImageView.setImageResource(R.drawable.load_error);
					}
					
				}else
				{
					herImageLayout.setVisibility(View.VISIBLE);
					new Thread()
					{
						public void run() 
						{
							final File f = new File(Config.PATH_BASE+m.getContent());
							if(!f.getParentFile().exists())
							{
								f.getParentFile().mkdirs();
							}
							
							if(!f.exists())
							{
								if(!HttpURLConnectionUtil.downLoadFile(Config.URL_BASE+m.getContent(), f, false))
								{//图片下载失败
									runOnUiThread(new Runnable()
									{
										public void run()
										{
											herImageView.setImageResource(R.drawable.load_error);
										}
									});
									return;
								}
							}
							
							runOnUiThread(new Runnable()
							{
								public void run()
								{
									herImageView.setImageBitmap(BitmapFactory.decodeFile(f.getAbsolutePath()));
								}
							});
							
						};
					}.start();
				}
				
				break;
			case 3:

				if("我".equals(m.getFromUser()))
				{
					myVoiceLayout.setVisibility(View.VISIBLE);
					
				}else
				{
					herVoiceLayout.setVisibility(View.VISIBLE);
				}
				break;

			default:
				break;
			}
			
			return convertView;
		}

		@Override
		public int getCount()
		{
			return dataList.size();
		}

		@Override
		public Object getItem(int arg0)
		{
			return dataList.get(arg0);
		}

		@Override
		public long getItemId(int position)
		{
			return position;
		}
	}

	



	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int pos, long arg3)
	{
		Message m = dataList.get(pos);
		
		switch (m.getType())
		{
		case 2:
			
			break;
			
		case 3:
			playVoice(m);
			break;

		default:
			break;
		}
	}
}

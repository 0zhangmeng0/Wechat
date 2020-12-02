package com.example.wechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import config.Config;
import db.MessageDB;
import service.MessageService;
import util.HttpURLConnectionUtil;
import util.StringUtil;
import util.UIUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FriendActivity extends Activity implements OnItemClickListener,OnItemLongClickListener
{
	private ListView friendListView = null;
	private JSONArray friendArray = null;

	private MessageBroadcastReciever messageBroadcastReciever = null;
	private MessageDB db = null;
	private List<Map<String, String>> data = new ArrayList<Map<String,String>>();
	private MySimpleAdapter mySimpleAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_friend);
		
		friendListView = (ListView)findViewById(R.id.friendList);
		friendListView.setDivider(getResources().getDrawable(R.drawable.divider));
		friendListView.setDividerHeight(2);
		friendListView.setOnItemClickListener(this);
		
		friendListView.setOnItemLongClickListener(this);
		
		try
		{
			db = new MessageDB(this);
		} catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setTitle("好友列表");
		loadFriendList();
		messageBroadcastReciever = new MessageBroadcastReciever();
		startService(new Intent(this, MessageService.class));
		
		mySimpleAdapter = new MySimpleAdapter(FriendActivity.this, data, R.layout.item_friend, new String[]{"title" ,"username","sex"}, new int[]{R.id.titleButton,R.id.usernameTextView,R.id.sexTextView});
		
		friendListView.setAdapter(mySimpleAdapter);
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub

		UIUtil.initUIUtil(this);
		super.onResume();
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(MessageService.MESSAGE_ACTION);
		this.registerReceiver(messageBroadcastReciever, intentFilter);
		refreshUnreadMessage();
	}
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		unregisterReceiver(messageBroadcastReciever);
		super.onPause();
	}
	
	@Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
		stopService(new Intent(this, MessageService.class));
		super.onDestroy();
	}
	
	private void loadFriendList()
	{
		UIUtil.showProgressDialog("请稍后", "正在加载好友列表...", 100, true, false, null);
		Log.v("xxx",  "正在加载好友列表...");
		
		new Thread()
		{
			public void run() 
			{
				Map<String, String> params = new HashMap<String, String>();
				try
				{
					params.put("userId", Config.user.getString("id"));
					String json = HttpURLConnectionUtil.doPost(Config.URL_LOAD_FRIEND, params);
					Log.v("好友列表Json", json);
					JSONObject jsonObject = new JSONObject(json);
					if(jsonObject.getBoolean("success"))
					{
						friendArray = jsonObject.getJSONArray("data");
						showFriendList();
						refreshUnreadMessage();
						
					}else
					{
						UIUtil.showMesssageDialog("加载失败", jsonObject.getString("message"));
					}
					
				} catch (Exception e)
				{
					e.printStackTrace();
					UIUtil.showMesssageDialog("网络异常");
				}
				
				UIUtil.cancelProgress();
				
			};
		}.start();
	}
	
	private void showFriendList()
	{
		if(friendArray == null || friendArray.length()<=0)
		{
			Log.v("xxx", "friendArray == null");
			runOnUiThread(new Runnable()
			{
				@Override
				public void run()
				{
					data.clear();
					mySimpleAdapter.notifyDataSetInvalidated();
				}
			});
			return ;
		}
		
		data.clear();
		for(int i = 0;i<friendArray.length();i++)
		{
			Map<String, String> map = new HashMap<String, String>();
			try
			{
				map.put("id", friendArray.getJSONObject(i).getLong("id")+"");
				map.put("unread", db.queryUnreadMessageCountByUserid(friendArray.getJSONObject(i).getLong("id"))+"");
				map.put("title", friendArray.getJSONObject(i).getString("username").charAt(0)+"");
				map.put("username", friendArray.getJSONObject(i).getString("username"));
				map.put("sex", friendArray.getJSONObject(i).getString("sex"));
				
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			
			data.add(map);
		}
		
		runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				mySimpleAdapter.notifyDataSetInvalidated();
			}
		});
	}
	
	
	private void onAddFriend()
	{

		final EditText ipEditText = new EditText(this);
		
		new AlertDialog.Builder(this)
		.setTitle("请输入对方用户名")
		.setView(ipEditText)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(!StringUtil.isEmptyOrNull(ipEditText.getText().toString()))
				{
					addFriend(ipEditText.getText().toString());
					
				}else
				{
					UIUtil.toast("用户名不能为空");
					onAddFriend();
				}
			}
		})
		.setNegativeButton("取消", null)
		.show();
	}
	
	private void addFriend(final String username)
	{
		UIUtil.showProgressDialog("请稍后", "正在添加好友...", 100, true, false, null);
		
		new Thread()
		{
			public void run() 
			{
				Map<String, String> params = new HashMap<String, String>();
				try
				{
					params.put("userId", Config.user.getString("id"));
					params.put("herUsername", username);
					String json = HttpURLConnectionUtil.doPost(Config.URL_ADD_FRIEND, params);
					Log.v("xxx", json);
					JSONObject jsonObject = new JSONObject(json);
					if(jsonObject.getBoolean("success"))
					{
						UIUtil.showMesssageDialog( jsonObject.getString("message"));
						loadFriendList();
						
					}else
					{
						UIUtil.showMesssageDialog("添加失败", jsonObject.getString("message"));
					}
					
				} catch (Exception e)
				{
					e.printStackTrace();
					UIUtil.showMesssageDialog("网络异常");
				}
				
				UIUtil.cancelProgress();
				
			};
		}.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.activity_friend, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		switch (item.getItemId())
		{
		case R.id.addFriend:
			onAddFriend();
			break;
			
		case R.id.selfinfo:
			Intent intent = new Intent(this, RegisterActivity.class);
			intent.putExtra("action", "update");
			startActivity(intent);
			break;
			
		case R.id.logout:
			new AlertDialog.Builder(this)
			.setTitle("信息确认")
			.setMessage("确定退出么?")
			.setPositiveButton("确定", new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface arg0, int arg1)
				{
					finish();
				}
			})
			.setNegativeButton("取消", null)
			.show();
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}


	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int ps, long id)
	{
		Intent intent = new Intent(this, ChatActivity.class);
		try
		{
			intent.putExtra("data", friendArray.getJSONObject(ps).toString());
			
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
		startActivity(intent);
	}
	
	
	private void refreshUnreadMessage()
	{
		new Thread()
		{
			@Override
			public void run()
			{
				for(int i = 0;i<data.size();i++)
				{
					int unread = db.queryUnreadMessageCountByUserid(Long.parseLong(data.get(i).get("id")));
					Log.v("xx", "unread = "+unread);	
					data.get(i).put("unread", unread+"");
				}
				
				runOnUiThread(new Runnable()
				{
					
					@Override
					public void run()
					{
						mySimpleAdapter.notifyDataSetInvalidated();
					}
				});
			}
		}.start();
		
	}
	
	
	private class MySimpleAdapter extends SimpleAdapter
	{
		public MySimpleAdapter(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to)
		{
			super(context, data, resource, from, to);
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent)
		{
			convertView = super.getView(position, convertView, parent);
			TextView messageCount = (TextView) convertView.findViewById(R.id.messageCount);
			int unread = Integer.parseInt(data.get(position).get("unread"));
			
			if(unread>0)
			{
				messageCount.setVisibility(View.VISIBLE);
				messageCount.setText(unread+"");
				
			}else
			{
				messageCount.setVisibility(View.GONE);
			}
			
			convertView.findViewById(R.id.titleButton).setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					Intent intent = new Intent(FriendActivity.this, RegisterActivity.class);
					intent.putExtra("action", "detail");
					try
					{
						intent.putExtra("data", friendArray.getJSONObject(position).toString());
					} catch (JSONException e)
					{
						e.printStackTrace();
					}
					
					startActivity(intent);
				}
			});
			return convertView;
		}
	}
	
	private void onRemoveFriend(final JSONObject jsonObject)
	{
		try
		{
			new AlertDialog.Builder(this)
			.setTitle("注意")
			.setMessage("确定要和\""+jsonObject.getString("username")+"\"解除好友关系吗?")
			.setPositiveButton("确定", new DialogInterface.OnClickListener()
			{
				
				@Override
				public void onClick(DialogInterface dialog, int which)
				{
					removeFriend(jsonObject);
				}
			})
			.setNegativeButton("取消", null)
			.show();
			
		} catch (JSONException e)
		{
			e.printStackTrace();
		}
	}

	private void removeFriend(final JSONObject jsonObject)
	{
		UIUtil.showProgressDialog("请稍后", "正在解除好友关系...", 100, true, false, null);
		
		new Thread()
		{
			public void run() 
			{
				Map<String, String> params = new HashMap<String, String>();
				try
				{
					params.put("friend.myId", Config.user.getString("id"));
					params.put("friend.herId", jsonObject.getLong("id")+"");
					String json = HttpURLConnectionUtil.doPost(Config.URL_REMOVE_FRIEND, params);
					Log.v("xxx", json);
					JSONObject jsonObject = new JSONObject(json);
					if(jsonObject.getBoolean("success"))
					{
						loadFriendList();
						
					}else
					{
						UIUtil.showMesssageDialog("操作失败", jsonObject.getString("message"));
					}
					
				} catch (Exception e)
				{
					e.printStackTrace();
					UIUtil.showMesssageDialog("网络异常");
				}
				
				UIUtil.cancelProgress();
				
			};
		}.start();
	
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> adapterView, View view,final int ps, long id)
	{
		new AlertDialog.Builder(this)
		.setItems(new String[]{"解除好友关系","查看聊天记录","查看好友资料"}, new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				switch (which)
				{
				case 0:
					try
					{
						onRemoveFriend(friendArray.getJSONObject(which));
					} catch (JSONException e)
					{
						e.printStackTrace();
					}
					break;
				case 1:
					Intent inte = new Intent(FriendActivity.this, HistoryActivity.class);
					try
					{
						inte.putExtra("data", friendArray.getJSONObject(ps).toString());
					} catch (JSONException e)
					{
						e.printStackTrace();
					}
					startActivity(inte);
					break;
				case 2:
					Intent intent = new Intent(FriendActivity.this, RegisterActivity.class);
					intent.putExtra("action", "detail");
					try
					{
						intent.putExtra("data", friendArray.getJSONObject(ps).toString());
					} catch (JSONException e)
					{
						e.printStackTrace();
					}
					startActivity(intent);
					break;

				default:
					break;
				}
			}
		}).show();
		return true;
	}
	

	
	private class MessageBroadcastReciever extends BroadcastReceiver
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			Log.v("xxx", "=======================接收到广播============================");
			refreshUnreadMessage();
		}
	}
	
	@Override
	public void onBackPressed()
	{
		new AlertDialog.Builder(this)
		.setTitle("提示")
		.setMessage("确定退出么?")
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				stopService(new Intent(FriendActivity.this, MessageService.class));
				finish();
			}
		})
		.setNegativeButton("取消", null)
		.show();
	}
}

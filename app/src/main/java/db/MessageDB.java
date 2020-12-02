package db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONException;
import bean.Message;
import config.Config;

import java.util.ArrayList;
import java.util.List;


public class MessageDB extends SQLiteOpenHelper
{
	private static final String CREATE_MESSAGE_TABLE = "create table message(id integer primary key,type integer,fromId long,toId long,fromUser text,isRead boolean,sendTime long,content text)";
	private static final String DROP_MESSAGE_TABLE = "drop table if exists message;";
	
	private SQLiteDatabase db = null;

	public MessageDB(Context context) throws JSONException
	{
		super(context, Config.user.getString("username"), null, 5);
		db = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DROP_MESSAGE_TABLE);
		db.execSQL(CREATE_MESSAGE_TABLE);
		
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{
		onCreate(db);
	}
	
	/**
	 * 添加一条消息到数据库
	 * @param message
	 */
	public void addMessage(Message message)
	{	
		ContentValues values = new ContentValues();

		values.put("type", message.getType());
		values.put("content", message.getContent());
		values.put("fromUser", message.getFromUser());
		values.put("fromId", message.getFromId());
		values.put("toId", message.getToId());
		values.put("isRead", message.isRead());
		values.put("sendTime", message.getSendTime());
		
		db.insert("message", null, values);
	}
	
	/**
	 * 获取userid的未读信息条数
	 * @param userId
	 * @return
	 */
	public int queryUnreadMessageCountByUserid(long userId)
	{
		int rs = 0;
		
		Cursor c = db.rawQuery("select count(*) from message where fromId="+userId+" and isRead=0",null);
		if(c.moveToNext())
		{
			rs = c.getInt(0);
		}
		c.close();
		return rs;
	}
	
	/**
	 * 将用户Id为userId的消息置为已读
	 * @param userId
	 */
	public void updateMessageBeRead(long userId)
	{
		db.execSQL("update message set isRead=1 where fromId="+userId);
	}
	
	/**
	 * 获取用户Id为userId的未读消息
	 * @param userId
	 * @return
	 */
	public List<Message> queryUnreadMessageByUserid(long userId)
	{
		List<Message> messages = new ArrayList<Message>();
		
		Cursor c = db.rawQuery("select * from message where fromId="+userId+" and isRead=0",null);
		while(c.moveToNext())
		{
			Message m = new Message();
			m.setContent(c.getString(c.getColumnIndex("content")));
			m.setType(c.getInt(c.getColumnIndex("type")));
			m.setFromUser(c.getString(c.getColumnIndex("fromUser")));
			m.setFromId(c.getLong(c.getColumnIndex("fromId")));
			m.setSendTime(c.getLong(c.getColumnIndex("sendTime")));
			m.setToId(c.getLong(c.getColumnIndex("toId")));
			
			messages.add(m);
		}
		c.close();
		updateMessageBeRead(userId);
		return messages;
	}
	
	/**
	 * 查询用户Id为userId的所有聊天记录
	 * @param userId
	 * @return
	 */
	public List<Message> queryAllMessageByFromUserid(long userId)
	{
		List<Message> messages = new ArrayList<Message>();
		
		Cursor c = db.rawQuery("select * from message where fromId="+userId+" or toId="+userId,null);
		while(c.moveToNext())
		{
			Message m = new Message();
			m.setContent(c.getString(c.getColumnIndex("content")));
			m.setType(c.getInt(c.getColumnIndex("type")));
			m.setFromUser(c.getString(c.getColumnIndex("fromUser")));
			m.setFromId(c.getLong(c.getColumnIndex("fromId")));
			m.setSendTime(c.getLong(c.getColumnIndex("sendTime")));
			m.setToId(c.getLong(c.getColumnIndex("toId")));
			
			messages.add(m);
		}
		c.close();
		return messages;
	}

}

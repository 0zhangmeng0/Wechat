package config;

import android.os.Environment;

import org.json.JSONObject;


public class Config
{
	public static final String PATH_BASE = Environment.getExternalStorageDirectory()+"/qxin";
	
	
	public static String URL_BASE = "http://192.168.1.9:8001/QxinServer";
	public static String URL_REGISTER = URL_BASE+"/action/UserAction!register.do";
	public static String URL_UPDATE = URL_BASE+"/action/UserAction!update.do";
	public static String URL_LOGIN = URL_BASE+"/action/UserAction!register.do";
	public static String URL_ADD_FRIEND = URL_BASE+"/action/FriendAction!addFriend.do";
	public static String URL_REMOVE_FRIEND = URL_BASE+"/action/FriendAction!removeFriend.do";
	public static String URL_LOAD_FRIEND = URL_BASE+"/action/FriendAction!getFriendList.do";
	public static String URL_SEND_TEXT_MESSAGE = URL_BASE+"/action/MessageAction!addMessage.do";
	public static String URL_QUERY_UNREAD_MESSAGE = URL_BASE+"/action/MessageAction!queryUnReadMessage.do";
	public static JSONObject user = null;
	
	public static final void reload(String ip)
	{
		URL_BASE = ip;
		URL_REGISTER = URL_BASE+"/action/UserAction!register.do";
		URL_LOGIN = URL_BASE+"/action/UserAction!login.do";
		URL_ADD_FRIEND = URL_BASE+"/action/FriendAction!addFriend.do";
		URL_LOAD_FRIEND = URL_BASE+"/action/FriendAction!getFriendList.do";
		URL_REMOVE_FRIEND = URL_BASE+"/action/FriendAction!removeFriend.do";
		URL_UPDATE = URL_BASE+"/action/UserAction!update.do";
		URL_SEND_TEXT_MESSAGE = URL_BASE+"/action/MessageAction!addMessage.do";
		URL_QUERY_UNREAD_MESSAGE = URL_BASE+"/action/MessageAction!queryUnReadMessage.do";
	}
}

package bean;


 
public class Message
{
	private Integer id = null;
	private Integer type = null;//消息类型  1-文本  2-图片  3-语音
	private String content = null;//消息文本

	private Long toId = null;
	private String fromUser = null;//发送人
	private Long fromId = null;//发送人
	
	private boolean isRead= false;//自己是否接收到
	
	private Long sendTime = null;

	public Message()
	{
		super();
		// TODO Auto-generated constructor stub
	}


	public Message(Integer id, Integer type, String content, Long toId, String fromUser, Long fromId, boolean isRead, Long sendTime)
	{
		super();
		this.id = id;
		this.type = type;
		this.content = content;
		this.toId = toId;
		this.fromUser = fromUser;
		this.fromId = fromId;
		this.isRead = isRead;
		this.sendTime = sendTime;
	}


	public Long getToId()
	{
		return toId;
	}


	public void setToId(Long toId)
	{
		this.toId = toId;
	}


	public Integer getId()
	{
		return id;
	}

	public void setId(Integer id)
	{
		this.id = id;
	}

	public Integer getType()
	{
		return type;
	}

	public void setType(Integer type)
	{
		this.type = type;
	}

	public String getFromUser()
	{
		return fromUser;
	}

	public void setFromUser(String fromUser)
	{
		this.fromUser = fromUser;
	}

	public Long getFromId()
	{
		return fromId;
	}

	public void setFromId(Long fromId)
	{
		this.fromId = fromId;
	}

	public boolean isRead()
	{
		return isRead;
	}

	public void setRead(boolean isRead)
	{
		this.isRead = isRead;
	}

	public Long getSendTime()
	{
		return sendTime;
	}

	public void setSendTime(Long sendTime)
	{
		this.sendTime = sendTime;
	}

	public String getContent()
	{
		return content;
	}

	public void setContent(String content)
	{
		this.content = content;
	}
	
	
}

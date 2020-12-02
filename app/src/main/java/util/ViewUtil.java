 package util;


 import android.app.Activity;
 import android.app.AlertDialog;
 import android.app.Notification;
 import android.app.NotificationManager;
 import android.app.PendingIntent;
 import android.app.ProgressDialog;
 import android.app.Service;
 import android.content.BroadcastReceiver;
 import android.content.Context;
 import android.content.DialogInterface;
 import android.content.DialogInterface.OnCancelListener;
 import android.content.Intent;
 import android.graphics.drawable.BitmapDrawable;
 import android.graphics.drawable.Drawable;
 import android.graphics.drawable.StateListDrawable;
 import android.util.Log;
 import android.view.View;
 import android.view.View.MeasureSpec;
 import android.view.ViewGroup;
 import android.widget.Toast;

 import java.io.File;
 import java.io.FileInputStream;
 import java.io.FileNotFoundException;
 import java.io.InputStream;


 public class ViewUtil
{
	/**
	 * 最后一次按钮点击的时间
	 */
    private static long lastClickTime;
    /**
     * 默认按钮多次点击的间隔
     */
	private static long BUTTON_CLICK_TIME_DIFF = 1000;
	
	/**
	 * 进度条对话框
	 */
	private static ProgressDialog progressDialog = null;
	
	/**
	 * 将控件的id转化为String
	 * @param view
	 * @return
	 */
	public static String getViewIdByIdString(View view)
	{
		return view.getContext().getResources().getResourceEntryName(view.getId());
	}

	/**
	 * 通过String的名称获取id
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getStringIdByName(Context context,String name)
	{
		return getResourceIdByName(context, name, "string");
	}

	/**
	 * 通过Layout的名称获取id
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getLayoutIdByName(Context context,String name)
	{
		return getResourceIdByName(context, name, "layout");
	}

	/**
	 * 通过Drawable的名称获取id
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getDrawableIdByName(Context context,String name)
	{
		return getResourceIdByName(context, name, "drawable");
	}

	/**
	 * 将控件的String转化为id
	 * @param context
	 * @param name
	 * @return
	 */
	public static int getIdByName(Context context,String name)
	{
		return getResourceIdByName(context, name, "id");
	}
	
	/**
	 * 通过字符串获取对象的资源id
	 * @param context 上下文
	 * @param name 资源id的名称
	 * @param type 类型，如：id，drawable，layout，string，array,attr等
	 * @return
	 */
	public static int getResourceIdByName(Context context,String name,String type)
	{
		return context.getResources().getIdentifier(name, type, context.getPackageName());
	}
	
	
	
	/**
	 * toast消息
	 * @param context 上下文
	 * @param text 显示的文本
	 */
	public static void toast(Context context,String text)
	{
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
	
	/**
	 * 取消Id为notificationId的通知
	 * @param context 上下文
	 * @param notificationId 通知的id
	 */
	public static void cancelNotification(Context context,int notificationId)
	{
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		
		notificationManager.cancel(notificationId);
	}
	
	/**
	 * 显示通知
	 * @param context 上下文
	 * @param drawableIconId 图片ID
	 * @param title 标题
	 * @param content 内容
	 * @param notificationDefault 默认的通知方式，比如震动、声音
	 * @param notificationFlag 通知标记，如点击后是否自动取消
	 * @param remoteClass 需要启动的类
	 * @return 消息的ID
	 */
	@SuppressWarnings("rawtypes")
	public static int showNotification(Context context,int drawableIconId,String title,String content,int notificationDefault,int notificationFlag,Class remoteClass)
	{
		NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		int notificationId = (int) System.currentTimeMillis();
		
		Notification notification = new Notification(drawableIconId,title, System.currentTimeMillis()/*显示的时间*/);
		
		PendingIntent intent = null;
		
		
		if(Activity.class.isAssignableFrom(remoteClass))
		{
			intent = PendingIntent.getActivity(context, 0, new Intent(context, remoteClass), 0);//设置当点击通知栏的条目的时候显示的Activity
			
		}else if(BroadcastReceiver.class.isAssignableFrom(remoteClass))
		{
			intent = PendingIntent.getBroadcast(context, 0, new Intent(context, remoteClass), 0);//设置当点击通知栏的条目的时候显示的Activity
			
		}else if(Service.class.isAssignableFrom(remoteClass))
		{
			intent = PendingIntent.getService(context, 0, new Intent(context, remoteClass), 0);//设置当点击通知栏的条目的时候显示的Activity
			
		}


		notification.flags = notificationFlag;
		notification.defaults = notificationDefault;//当现实的时候，发出全部的提示，包括声音、震动等
		notificationManager.notify(notificationId, notification);//发出通知
		
		return notificationId;
	
	}
	
	// 此方法直接照搬自网络上的一个下拉刷新的demo，此处是“估计”headView的width以及height
	public static  void measureView(View child)
	{
		ViewGroup.LayoutParams p = child.getLayoutParams();
		if (p == null)
		{
			p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
		}
		int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
		int lpHeight = p.height;
		int childHeightSpec;
		if (lpHeight > 0)
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,MeasureSpec.EXACTLY);
		} else
		{
			childHeightSpec = MeasureSpec.makeMeasureSpec(0,MeasureSpec.UNSPECIFIED);
		}
		child.measure(childWidthSpec, childHeightSpec);
	}
	
	/**
	 * 消息弹出框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 */
	public static void showMesssageDialog(Context context,String message)
	{
		showMesssageDialog(context, "消息", message);
	}
	
	/**
	 * 消息弹出框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 */
	public static void showMesssageDialog(Context context,String title,String message)
	{
		showMesssageDialog(context, title, message, null);
	}
	
	/**
	 * 消息弹出框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param okListener 确定监听器
	 */
	public static void showMesssageDialog(Context context,String title,String message,DialogInterface.OnClickListener okListener)
	{
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton("确定", okListener)
		.show();
	}
	
	/**
	 * 确定取消对话框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param okListener 确定按钮监听器
	 * @param cancelListener 取消按钮监听器
	 */
	public static void showConfirmDialog(Context context,String title,String message,DialogInterface.OnClickListener okListener,DialogInterface.OnClickListener cancelListener)
	{
		showConfirmDialog(context, title, message, "确定", okListener, "取消", cancelListener);
	}
	
	/**
	 * 确定取消对话框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param ok 确定文本
	 * @param okListener 确定事件监听
	 * @param cancel 取消文本
	 * @param cancelListener 取消事件监听
	 */
	public static void showConfirmDialog(Context context,String title,String message,String ok,DialogInterface.OnClickListener okListener,String cancel,DialogInterface.OnClickListener cancelListener)
	{
		new AlertDialog.Builder(context)
		.setTitle(title)
		.setMessage(message)
		.setPositiveButton(ok, okListener)
		.setNegativeButton(cancel, cancelListener)
		.setCancelable(false)
		.show();
	}
	
	
	
	/**
	 * 可取消的滚动条对话框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param max 最大值
	 * @param indeterminate 是否可确定的
	 * @param cancelListener 取消事件
	 */
	public static void showCancelableProgressDialog(Context context,String title,String message,int max,boolean indeterminate,OnCancelListener cancelListener)
	{
		showProgressDialog(context, title, message, max, indeterminate, true, cancelListener);
	}
	
	
	/**
	 * 不可取消的滚动条对话框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param max 最大值
	 * @param indeterminate 是否可确定的
	 */
	public static void showDisCancelableProgressDialog(Context context,String title,String message,int max,boolean indeterminate)
	{
		showProgressDialog(context, title, message, max, indeterminate, false, null);
	}
	
	/**
	 * 进度条对话框
	 * @param context 上下文
	 * @param title 标题
	 * @param message 内容
	 * @param max 最大进度
	 * @param indeterminate 是否可确定的
	 * @param cancelable 是否可以取消
	 * @param cancelListener 取消事件监听
	 */
	public static void showProgressDialog(Context context,String title,String message,int max,boolean indeterminate,boolean cancelable,OnCancelListener cancelListener)
	{
		if(progressDialog!=null)
		{
			progressDialog.dismiss();
		}
		
		progressDialog = new ProgressDialog(context);
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setIndeterminate(indeterminate);
		if(!indeterminate)
			progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setCancelable(cancelable);
		progressDialog.setOnCancelListener(cancelListener);
		progressDialog.setMax(max);
		progressDialog.show();
	}
	
	
	/**
	 * 设置进度条进度
	 * @param progress 进度
	 */
	public static void setProgress(int progress)
	{
		if(progressDialog == null)
			return;
		progressDialog.setProgress(progress);
		
	}
	
	/**
	 * 设置进度条进度
	 * @param message 消息
	 * @param progress 进度
	 */
	public static void setProgress(String message,int progress)
	{
		if(progressDialog == null)
			return;
		progressDialog.setMessage(message);
		progressDialog.setProgress(progress);
		
	}
	
	/**
	 * 设置进度条的进度
	 * @param title 标题
	 * @param message 内容
	 * @param progress 进度
	 */
	public static void setProgress(String title,String message,int progress)
	{
		if(progressDialog == null)
			return;
		
		progressDialog.setTitle(title);
		progressDialog.setMessage(message);
		progressDialog.setProgress(progress);
	}
	
	/**
	 * 取消进度条
	 */
	public static void cancelProgress()
	{
		if(progressDialog!=null)
		{
			progressDialog.cancel();
			progressDialog = null;
		}
	}
	
	/**
	 * 判断两次点击的间隔，如果小于1000，则认为是多次无效点击
	 * @return
	 */
	public static boolean isFastDoubleClick()
	{
		return isFastDoubleClick(BUTTON_CLICK_TIME_DIFF);
	}
	
	/**
	 * 判断两次点击的间隔，如果小于diff，则认为是多次无效点击
	 * @param diff
	 * @return
	 */
	public static boolean isFastDoubleClick(long diff)
	{
		long time = System.currentTimeMillis();
		long timeD = time - lastClickTime;
		
		if (lastClickTime>0 && timeD < 1000)
		{
			Log.v("isFastDoubleClick", "短时间内按钮多次触发");
			return true;
		}
		
		lastClickTime = time;
		
		return false;
	}
	


    /**
     * 代码生成Selector
     * @param context 上下文参数
     * @param idNormal 正常状态的资源ID
     * @param idPressed 按下状态的资源ID
     * @param idFocused 选中状态的资源ID
     * @param idUnable 无效状态的资源ID
     * @return
     */
    public static StateListDrawable newSelector(Context context, int idNormal, int idPressed, int idFocused,  int idUnable)
    {  
        Drawable normal = idNormal == -1 ? null : context.getResources().getDrawable(idNormal);  
        Drawable pressed = idPressed == -1 ? null : context.getResources().getDrawable(idPressed);  
        Drawable focused = idFocused == -1 ? null : context.getResources().getDrawable(idFocused);  
        Drawable unable = idUnable == -1 ? null : context.getResources().getDrawable(idUnable);  
        return newSelector(context, normal, pressed, focused, unable);  
    }  
    
    /**
     * 代码生成Selector
     * @param context 上下文参数
     * @param idNormal 正常状态的资源文件
     * @param idPressed 按下状态的资源文件
     * @param idFocused 选中状态的资源文件
     * @param idUnable 无效状态的资源文件
     * @return
     */
    public static StateListDrawable newSelector(Context context, File normal, File pressed, File focused,  File unable)
    {  
    	InputStream normalInputStream = null;
    	InputStream pressedInputStream = null;
    	InputStream focusedInputStream = null;
    	InputStream unableInputStream = null;
    	
    	try
		{
			normalInputStream = new FileInputStream(normal);
			pressedInputStream = new FileInputStream(pressed);
			focusedInputStream = new FileInputStream(focused);
			unableInputStream = new FileInputStream(unable);
			
		} catch (FileNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
    	
        return newSelector(context, normalInputStream, pressedInputStream, focusedInputStream, unableInputStream);  
    }  
    /**
     * 代码生成Selector
     * @param context 上下文参数
     * @param idNormal 正常状态的资源文件
     * @param idPressed 按下状态的资源文件
     * @param idFocused 选中状态的资源文件
     * @param idUnable 无效状态的资源文件
     * @return
     */
    public static StateListDrawable newSelector(Context context,InputStream normalInputStream,InputStream pressedInputStream,InputStream focusedInputStream, InputStream unableInputStream)
    {  
        return newSelector(context, new BitmapDrawable(normalInputStream), new BitmapDrawable(pressedInputStream), new BitmapDrawable(focusedInputStream), new BitmapDrawable(unableInputStream));  
    }  
    
    /**
     * 代码生成Selector
     * @param context 上下文参数
     * @param idNormal 正常状态的Drawable
     * @param idPressed 按下状态的Drawable
     * @param idFocused 选中状态的Drawable
     * @param idUnable 无效状态的Drawable
     * @return
     */
    public static StateListDrawable newSelector(Context context, Drawable idNormal, Drawable idPressed, Drawable idFocused,  Drawable idUnable)
    {
        StateListDrawable bg = new StateListDrawable();  
        bg.addState(new int[] { android.R.attr.state_pressed, android.R.attr.state_enabled }, idPressed);  
        // View.ENABLED_FOCUSED_STATE_SET  
        bg.addState(new int[] { android.R.attr.state_enabled, android.R.attr.state_focused }, idFocused);  
        // View.ENABLED_STATE_SET  
        bg.addState(new int[] { android.R.attr.state_enabled }, idNormal);  
        // View.FOCUSED_STATE_SET  
        bg.addState(new int[] { android.R.attr.state_focused }, idFocused);  
        // View.WINDOW_FOCUSED_STATE_SET  
        bg.addState(new int[] { android.R.attr.state_window_focused }, idUnable);  
        // View.EMPTY_STATE_SET  
        bg.addState(new int[] {}, idNormal);  
        return bg; 
    }
}


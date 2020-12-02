
package util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.widget.Toast;

/**
 * 类说明:UI类，主要实现在非UI线程中更新UI界面
 *
 */

public class UIUtil
{
	/**
	 * View所在的Activity，建议每次使用在acitivity的onCreate中初始化
	 */
	private static Activity activity = null;
	
	public static void initUIUtil(Activity ac)
	{
		activity = ac;
	}
	
	
	/**
	 * 消息弹出框
	 * @param title 标题
	 * @param message 内容
	 */
	public static void showMesssageDialog(String message)
	{
		showMesssageDialog("消息", message);
	}
	
	/**
	 * 消息弹出框
	 * @param title 标题
	 * @param message 内容
	 */
	public static void showMesssageDialog(String title,String message)
	{
		showMesssageDialog(title, message, null);
	}
	
	/**
	 * 消息弹出框
	 * @param title 标题
	 * @param message 内容
	 * @param okListener 确定监听器
	 */
	public static void showMesssageDialog(final String title,final String message,final DialogInterface.OnClickListener okListener)
	{
		activity.runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				ViewUtil.showMesssageDialog(activity, title, message, okListener);
			}
		});
	}
	
	/**
	 * 确定对话框
	 * @param title 标题
	 * @param message 内容
	 * @param okListener 确定按钮监听器
	 * @param cancelListener 取消按钮监听器
	 */
	public static void showConfirmDialog(final String title,final String message,final DialogInterface.OnClickListener okListener,final DialogInterface.OnClickListener cancelListener)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				ViewUtil.showConfirmDialog(activity, title, message, okListener, cancelListener);
			}
		});
	}
	
	/**
	 * 确定取消对话框
	 * @param title 标题
	 * @param message 内容
	 * @param ok 确定字符
	 * @param okListener 确定事件
	 * @param cancel 取消字符
	 * @param cancelListener 取消事件
	 */
	public static void showConfirmDialog(final String title,final String message,final String ok,final DialogInterface.OnClickListener okListener,final String cancel,final DialogInterface.OnClickListener cancelListener)
	{
		activity.runOnUiThread(new Runnable()
		{
			@Override
			public void run()
			{
				ViewUtil.showConfirmDialog(activity, title, message, ok,okListener, cancel,cancelListener);
			}
		});
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
		showProgressDialog(title, message, max, indeterminate, true, cancelListener);
	}
	
	
	/**
	 * 不可取消的滚动条对话框
	 * @param title 标题
	 * @param message 内容
	 * @param max 最大值
	 * @param indeterminate 是否可确定的
	 */
	public static void showDisCancelableProgressDialog(String title,String message,int max,boolean indeterminate)
	{
		showProgressDialog(title, message, max, indeterminate, false, null);
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
	public static void showProgressDialog(final String title,final String message,final int max,final boolean indeterminate,final boolean cancelable,final OnCancelListener cancelListener)
	{
		activity.runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				ViewUtil.showProgressDialog(activity, title, message, max, indeterminate, cancelable, cancelListener);
				
			}
		});
	}
	
	/**
	 * 设置进度条进度
	 * @param progress 进度
	 */
	public static void setProgress(final int progress)
	{
		activity.runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				ViewUtil.setProgress(progress);
			}
		});
	}
	
	/**
	 * 设置进度条进度
	 * @param message 消息
	 * @param progress 进度
	 */
	public static void setProgress(final String message,final int progress)
	{

		activity.runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				ViewUtil.setProgress(message,progress);
			}
		});
	}
	
	/**
	 * 设置进度条的进度
	 * @param title 标题
	 * @param message 内容
	 * @param progress 进度
	 */
	public static void setProgress(final String title,final String message,final int progress)
	{
		activity.runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				ViewUtil.setProgress(title, message, progress);
			}
		});
	}
	
	/**
	 * 取消进度条
	 */
	public static void cancelProgress()
	{
		activity.runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				ViewUtil.cancelProgress();
			}
		});
	}

	/**
	 * 吐司
	 * @param message 消息内容
	 */
	public static   void toast(final String message)
	{
		toast(message, Toast.LENGTH_LONG);
	}
	
	
	/**
	 * 吐司
	 * @param message 消息内容
	 * @param time 显示时间
	 */
	public static  void toast(final String message,final int time)
	{
		activity.runOnUiThread(new Runnable()
		{
			
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				Toast.makeText(activity, message, time).show();
			}
		});
	}
}

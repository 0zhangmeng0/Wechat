package com.example.wechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;

import org.json.JSONObject;

import config.Config;
import util.HttpURLConnectionUtil;
import util.StringUtil;
import util.UIUtil;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity implements OnClickListener
{

	private EditText usernameText = null;
	private EditText passwordText = null;
	
	private ImageView welcomeView = null;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		
		UIUtil.initUIUtil(this);

		findViewById(R.id.registerText).setOnClickListener(this);
		findViewById(R.id.setting).setOnClickListener(this);
		findViewById(R.id.loginButton).setOnClickListener(this);
		
		welcomeView = (ImageView)findViewById(R.id.welcomView);

		usernameText = (EditText)findViewById(R.id.usernameEditText);
		passwordText = (EditText) findViewById(R.id.passwordEditText);
		

		Config.reload(getSharedPreferences("config", MODE_PRIVATE).getString("ip", ""));
		
		setTitle("用户登录");
		
		new Thread()
		{
			public void run() 
			{
				try
				{
					Thread.sleep(5000);
				} catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				runOnUiThread(new Runnable()
				{
					public void run()
					{
						
						Animation animation = AnimationUtils.loadAnimation(LoginActivity.this, R.anim.anima_down_to_up);
						animation.setAnimationListener(new Animation.AnimationListener()
						{
							
							@Override
							public void onAnimationStart(Animation animation)
							{
								
							}
							
							@Override
							public void onAnimationRepeat(Animation animation)
							{
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animation animation)
							{
								// TODO Auto-generated method stub
								welcomeView.setVisibility(View.GONE);
								
							}
						});
						
						welcomeView.startAnimation(animation);
					}
				});
				
			};
		}.start();
	}
	
	@Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		UIUtil.initUIUtil(this);
	}
	
	
	private void login()
	{
		final String username = usernameText.getText().toString();
		final String password = passwordText.getText().toString();
		
		if(StringUtil.isEmptyOrNull(username) || StringUtil.isEmptyOrNull(password))
		{
			UIUtil.showMesssageDialog("用户名和密码不能为空");
			return;
		}
		
		
		UIUtil.showProgressDialog("请稍后", "正在登陆中...", 100, true, false, null);
		new Thread()
		{
			public void run() 
			{
				Map<String, String> params = new HashMap<String, String>();
				params.put("user.username", username);
				params.put("user.password", password);
				
				try
				{
					Log.v("url", Config.URL_LOGIN);
					String json = HttpURLConnectionUtil.doPost(Config.URL_LOGIN, params);
					JSONObject jsonObject = new JSONObject(json);
					
					if(jsonObject.getBoolean("success"))
					{
						UIUtil.toast("登陆成功");
						Config.user = jsonObject.getJSONObject("user");
						startActivity(new Intent(LoginActivity.this, FriendActivity.class));
					}else
					{
						UIUtil.showMesssageDialog("登陆失败",jsonObject.getString("message"));
					}
					Log.v("xxx", json);
					
				} catch (Exception e)
				{
					e.printStackTrace();
					UIUtil.showMesssageDialog("网络错误，请检查网络是否正常");
				}
				
				UIUtil.cancelProgress();
			};
		}.start();
	}
	

	private void setting()
	{
		final EditText ipEditText = new EditText(this);
		ipEditText.setHint("如:http://192.168.1.9:8080");
		ipEditText.setText(getSharedPreferences("config", MODE_PRIVATE).getString("ip", ""));
		
		new AlertDialog.Builder(this)
		.setTitle("请输入服务器地址")
		.setView(ipEditText)
		.setPositiveButton("确定", new DialogInterface.OnClickListener()
		{
			
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				if(!StringUtil.isEmptyOrNull(ipEditText.getText().toString()))
				{
					getSharedPreferences("config", MODE_PRIVATE).edit().putString("ip", ipEditText.getText().toString()).commit();
					Config.reload(ipEditText.getText().toString());
					UIUtil.toast("设置成功");
					
				}else
				{
					UIUtil.toast("设置失败");
				}
			}
		})
		.setNegativeButton("取消", null)
		.show();
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.loginButton:
			login();
			break;
		case R.id.setting:
			setting();
			break;
		case R.id.registerText:
			startActivity(new Intent(this, RegisterActivity.class));
			break;
 
		default:
			break;
		}
	}

}

package com.example.wechat;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;

import org.json.JSONException;
import org.json.JSONObject;

import config.Config;
import util.HttpURLConnectionUtil;
import util.StringUtil;
import util.UIUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends Activity implements OnClickListener
{
	private Button registerButton = null;
	private EditText usernameText = null;
	private EditText password1Text = null;
	private EditText password2Text = null;
	private RadioButton manRadioButton = null;
	private RadioButton womenRadioButton = null;
	private EditText signatureText = null;
	private DatePicker birthdayPicker = null;
	
	private String action = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setTitle("用户注册");
		setContentView(R.layout.activity_register);
		
		UIUtil.initUIUtil(this);

		registerButton = (Button)findViewById(R.id.registerButton);
		usernameText = (EditText)findViewById(R.id.username);
		password1Text = (EditText)findViewById(R.id.password1);
		password2Text = (EditText)findViewById(R.id.password2);
		manRadioButton = (RadioButton)findViewById(R.id.manRadio);
		womenRadioButton = (RadioButton)findViewById(R.id.womenRadio);
		signatureText = (EditText)findViewById(R.id.signature);
		birthdayPicker = (DatePicker)findViewById(R.id.dateBirthdate);
		
		registerButton.setOnClickListener(this);
		
		
		action = getIntent().getStringExtra("action");
		if("detail".equals(action))
		{
			JSONObject jsonObject = null;
			try
			{
				jsonObject= new JSONObject(getIntent().getStringExtra("data"));
				usernameText.setText(jsonObject.getString("username"));
				
				if("男".equals(jsonObject.getString("sex")))
				{
					manRadioButton.setChecked(true);
					
				}else
				{
					womenRadioButton.setChecked(true);
				}
				
				signatureText.setText(jsonObject.getString("signature"));
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = null;
				try
				{
					date = sdf.parse(jsonObject.getString("birthday"));
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				Log.v("xxx", "生日:"+jsonObject.getString("birthday") +"   date.getYear() = "+date.getYear()+"   date.getMonth()="+date.getMonth()+"  date.getDate() = "+date.getDate());
				birthdayPicker.init(1900+date.getYear(), date.getMonth(), date.getDate(), null);
				signatureText.setEnabled(false);
				usernameText.setEnabled(false);
				womenRadioButton.setEnabled(false);
				manRadioButton.setEnabled(false);
				birthdayPicker.setEnabled(false);
				
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			

			findViewById(R.id.label_password1).setVisibility(View.GONE);
			findViewById(R.id.label_password2).setVisibility(View.GONE);
			password1Text.setVisibility(View.GONE);
			password2Text.setVisibility(View.GONE);
			registerButton.setVisibility(View.GONE);
		
		}else if("update".equals(action))
		{
			usernameText.setEnabled(false);
			
			try
			{
				usernameText.setText(Config.user.getString("username"));
				password1Text.setText(Config.user.getString("password"));
				password2Text.setText(Config.user.getString("password"));
				
				if("男".equals(Config.user.getString("sex")))
				{
					manRadioButton.setChecked(true);
					
				}else
				{
					womenRadioButton.setChecked(true);
				}
				
				signatureText.setText(Config.user.getString("signature"));
				registerButton.setText("确定修改");
				
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				Date date = null;
				try
				{
					date = sdf.parse(Config.user.getString("birthday"));
				} catch (ParseException e)
				{
					e.printStackTrace();
				}
				Log.v("xxx", "生日:"+Config.user.getString("birthday") +"   date.getYear() = "+date.getYear()+"   date.getMonth()="+date.getMonth()+"  date.getDate() = "+date.getDate());
				birthdayPicker.init(1900+date.getYear(), date.getMonth(), date.getDate(), null);
				
			} catch (JSONException e)
			{
				e.printStackTrace();
			}
			
		}
			
	}

	
	private void onRegister()
	{
		final String username = usernameText.getText().toString();
		final String password1 = password1Text.getText().toString();
		final String password2 = password2Text.getText().toString();
		final String signature = signatureText.getText().toString();
		final String sex = womenRadioButton.isChecked() ?"女":"男";
		final String birthDay = birthdayPicker.getYear()+"-"+(birthdayPicker.getMonth()+1)+"-"+birthdayPicker.getDayOfMonth();
		
		Log.v("birthDay", "birthDay = "+birthDay);
		
		if(StringUtil.isEmptyOrNull(username))
		{
			UIUtil.showMesssageDialog("请输入用户名");
			return ;
		}
		
		if(StringUtil.isEmptyOrNull(password1))
		{
			UIUtil.showMesssageDialog("请输入密码");
			return ;
		}
		
		if(StringUtil.isEmptyOrNull(password2))
		{
			UIUtil.showMesssageDialog("请输入确认密码");
			return ;
		}
		
		if(!password1.equals(password2))
		{
			UIUtil.showMesssageDialog("两次密码不一致，请重输");
			return ;
		}
		
		UIUtil.showProgressDialog("请稍后", "正在提交注册...", 100, true, false, null);
		new Thread()
		{
			public void run() 
			{
				Map<String, String> params = new HashMap<String, String>();
				params.put("user.username", username);
				params.put("user.password", password1);
				params.put("user.sex", sex);
				params.put("user.signature", signature);
				params.put("user.birthday", birthDay);
				
				try
				{
					Log.v("url", Config.URL_REGISTER);
					String json = HttpURLConnectionUtil.doPost(Config.URL_REGISTER, params);
					JSONObject jsonObject = new JSONObject(json);
					
					if(jsonObject.getBoolean("success"))
					{
						UIUtil.toast("注册成功，请登录");
						finish();
						
					}else
					{
						UIUtil.showMesssageDialog("注册失败",jsonObject.getString("message"));
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

	
	private void onUpdate()
	{
		final String username = usernameText.getText().toString();
		final String password1 = password1Text.getText().toString();
		final String password2 = password2Text.getText().toString();
		final String signature = signatureText.getText().toString();
		final String sex = womenRadioButton.isChecked() ?"女":"男";
		final String birthDay = birthdayPicker.getYear()+"-"+(birthdayPicker.getMonth()+1)+"-"+birthdayPicker.getDayOfMonth();
		
		if(StringUtil.isEmptyOrNull(username))
		{
			UIUtil.showMesssageDialog("请输入用户名");
			return ;
		}
		
		if(StringUtil.isEmptyOrNull(password1))
		{
			UIUtil.showMesssageDialog("请输入密码");
			return ;
		}
		
		if(StringUtil.isEmptyOrNull(password2))
		{
			UIUtil.showMesssageDialog("请输入确认密码");
			return ;
		}
		
		if(!password1.equals(password2))
		{
			UIUtil.showMesssageDialog("两次密码不一致，请重输");
			return ;
		}
		
		UIUtil.showProgressDialog("请稍后", "正在提交修改...", 100, true, false, null);
		new Thread()
		{
			public void run() 
			{
				Map<String, String> params = new HashMap<String, String>();
				params.put("user.username", username);
				params.put("user.password", password1);
				params.put("user.sex", sex);
				params.put("user.signature", signature);
				params.put("user.birthday", birthDay);
				
				try
				{
					params.put("user.id", Config.user.getLong("id")+"");
					Log.v("url", Config.URL_UPDATE);
					String json = HttpURLConnectionUtil.doPost(Config.URL_UPDATE, params);
					JSONObject jsonObject = new JSONObject(json);
					
					if(jsonObject.getBoolean("success"))
					{
						UIUtil.showMesssageDialog("修改成功");
						Config.user = jsonObject.getJSONObject("data");
						
					}else
					{
						UIUtil.showMesssageDialog("修改失败",jsonObject.getString("message"));
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
	
	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.registerButton:
			if(action == null)
			{
				onRegister();
				
			}else if("update".equals(action))
			{
				onUpdate();
			}
			break;

		default:
			break;
		}
	}
}

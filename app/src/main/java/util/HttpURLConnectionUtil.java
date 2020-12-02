package util;
  

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class HttpURLConnectionUtil
{
	private static final int CONNECT_TIMEOUT = 30000;
	private static final int READ_TIMEOUT = 60000;
	
	private HttpURLConnectionUtil(){}

	public static String doGet(String path,Map<String, String> params) throws Exception 
	{
		return doGet(path, params, "UTF-8");
	}
	
	public static  String doGet(String path,Map<String, String> params,String encode) throws Exception 
	{
		String returnValue = null;
		StringBuilder urlPath = new StringBuilder(path);
		
		if(encode==null||encode.length()<=0)
		{
			encode = "UTF-8";
		}
		
		urlPath.append('?');

		for(Map.Entry<String, String> entry:params.entrySet())
		{
			urlPath.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), encode)).append('&');
		}
		
		urlPath.deleteCharAt(urlPath.length()-1);
		
		URL url = new URL(urlPath.toString());
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("GET");
		connection.setConnectTimeout(CONNECT_TIMEOUT);
		connection.setReadTimeout(READ_TIMEOUT);
		
		
		if(connection.getResponseCode() == 200)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream is = connection.getInputStream();
			byte[] buf = new byte[1024*10];
			int len = 0;
			while((len=is.read(buf))>0)
			{
				baos.write(buf,0,len);
			}
			
			returnValue = new String(baos.toByteArray());
			return returnValue;

		}else
		{
			throw new Exception("请求响应错误，错误编号"+connection.getResponseCode());
		}
		
	}
	
	public static  String doPost(String path,Map<String, String> params) throws Exception
	{
		return doPost(path, params, "UTF-8");
	}
	
	public static  String doPost(String path,Map<String, String> params,String encode) throws Exception 
	{
		String returnValue = null;
		StringBuilder body = new StringBuilder();
		
		if(encode==null||encode.length()<=0)
		{
			encode = "UTF-8";
		}
		
		if(params!=null&&params.size()>0)
		{
			for(Map.Entry<String, String> entry:params.entrySet())
			{
				body.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue(), encode)).append('&');
			}
			
			body.deleteCharAt(body.length()-1);
		}
		
		byte[] bodyContent = body.toString().getBytes();
		
		URL url = new URL(path);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		connection.setRequestMethod("POST");
		connection.setConnectTimeout(CONNECT_TIMEOUT);
		connection.setReadTimeout(READ_TIMEOUT);
		connection.setDoOutput(true);
		connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length", bodyContent.length+"");
		
		OutputStream os = connection.getOutputStream();
		os.write(bodyContent);
		os.flush();
		os.close();
		
		if(connection.getResponseCode() == 200)
		{
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			InputStream is = connection.getInputStream();
			byte[] buf = new byte[1024*10];
			int len = 0;
			while((len=is.read(buf))>0)
			{
				baos.write(buf,0,len);
			}
			
			returnValue = new String(baos.toByteArray());
			return returnValue;

		}else
		{
			throw new Exception("请求响应错误，错误编号"+connection.getResponseCode());
		}
	}
	
	
	
	
	 public static String doPost(String path, Map<String, String> params,Map<String, File> files) throws IOException
	 {
		 return doPost(path, params, files, "UTF-8");
	 }
		
	 public static String doPost(String path, Map<String, String> params,Map<String, File> files,String encode) throws IOException 
	 {        
		 String BOUNDARY = java.util.UUID.randomUUID().toString();        
		 String PREFIX = "--", LINEND = "\r\n";        
		 String MULTIPART_FROM_DATA = "multipart/form-data";        
		 String CHARSET = encode;        
		 URL uri = new URL(path);        
		 HttpURLConnection conn = (HttpURLConnection) uri.openConnection();      
		 conn.setConnectTimeout(CONNECT_TIMEOUT);
		 conn.setReadTimeout(READ_TIMEOUT);     
		 conn.setDoInput(true);
		 conn.setDoOutput(true);
		 conn.setUseCaches(false);         
		 conn.setRequestMethod("POST");  
		 conn.setRequestProperty("connection", "keep-alive");        
		 conn.setRequestProperty("Charsert", CHARSET);                
		 conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA+ ";boundary=" + BOUNDARY); 
		 StringBuilder sb = new StringBuilder();   
		 
		 if(params!=null)
			 for (Map.Entry<String, String> entry : params.entrySet()) 
			 {            
				 sb.append(PREFIX);            
				 sb.append(BOUNDARY);            
				 sb.append(LINEND);           
				 sb.append("Content-Disposition: form-data; name=\""+ entry.getKey() + "\"" + LINEND);            
				 sb.append("Content-Type: text/plain; charset=" + CHARSET + LINEND);            
				 sb.append("Content-Transfer-Encoding: 8bit" + LINEND);            
				 sb.append(LINEND);           
				 sb.append(entry.getValue());            
				 sb.append(LINEND);        
			}  
		
		DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());       
		outStream.write(sb.toString().getBytes());       
		if (files != null)            
			for (Map.Entry<String, File> file : files.entrySet()) 
			{               
				StringBuilder sb1 = new StringBuilder();                
				sb1.append(PREFIX);                
				sb1.append(BOUNDARY);                
				sb1.append(LINEND);                
				sb1.append("Content-Disposition: form-data; name=\""+file.getKey()+"\"; filename=\""+ file.getValue().getName() + "\"" + LINEND);                
				sb1.append("Content-Type: application/octet-stream; charset="+ CHARSET + LINEND);                
				sb1.append(LINEND);                
				outStream.write(sb1.toString().getBytes());                
				InputStream is = new FileInputStream(file.getValue());                
				byte[] buffer = new byte[1024];                
				int len = 0;                
				while ((len = is.read(buffer)) != -1) 
				{                    
					outStream.write(buffer, 0, len);               
				}                
				is.close();                
				outStream.write(LINEND.getBytes());           
			}              
		byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();        
		outStream.write(end_data);        
		outStream.flush();        
		int res = conn.getResponseCode();

		String data = ""; 
		if (res == 200) 
		{             
			InputStream in = conn.getInputStream();        
			InputStreamReader isReader = new InputStreamReader(in);               
			BufferedReader bufReader = new BufferedReader(isReader);               
			String line = null;         
			while((line = bufReader.readLine())!=null)            
			{
				data += line;                
			}
			bufReader.close();
		}else
		{
			throw new IOException();
		}
		
		outStream.close();        
		conn.disconnect();        
		return  data;   
	 }
	

	public static boolean downLoadFile(String urlStr,File file,boolean showProgressBar)
	{
		boolean flag = true;
		InputStream inputStream = null;
		OutputStream outputStream = null;
		
		if(showProgressBar)
		{
			UIUtil.showProgressDialog("请稍后", "正在下载中...", 100, false, false, null);
		}
		
		try
		{
			URL url = new URL(urlStr);
			HttpURLConnection connection = (HttpURLConnection)url.openConnection();
			connection.setConnectTimeout(CONNECT_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			
			int fileSize = connection.getContentLength();
			int currentSize = 0;
			
			inputStream = connection.getInputStream();
			outputStream =  new FileOutputStream(file);
			
			byte buf[] = new byte[1024];
			int len = 0;
			
			while((len=inputStream.read(buf))>0)
			{
				outputStream.write(buf,0,len);
				currentSize +=len;
				
				UIUtil.setProgress(currentSize*100/fileSize);
			}
			
			
		} catch (MalformedURLException e)
		{
			e.printStackTrace();
			flag = false;
			
		} catch (IOException e)
		{
			e.printStackTrace();
			flag = false;
			
		}finally
		{
			try
			{
				if(inputStream!=null)
				{
					inputStream.close();
					inputStream = null;
				}
				
				if(outputStream!=null)
				{
					outputStream.close();
					outputStream = null;
				}
				
			} catch (IOException e)
			{
				e.printStackTrace();
				flag = false;
			}
		}
		UIUtil.cancelProgress();
		return flag;
	}
}


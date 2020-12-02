package util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil
{
	public static boolean isEmptyOrNull(String str)
	{
		return str==null||str.trim().length()<=0;
	}
	
	public static boolean isNotEmpty(String str)
	{
		return !isEmptyOrNull(str);
	}
	
	public static final boolean isPattern(String str,String pattern)
	{
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(str);
		return m.find();
	}
	
	public static String formatLong(Long l,String pattern)
	{
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(l);
	}

	public static String formatDouble(Double d,String pattern)
	{
		DecimalFormat df = new DecimalFormat(pattern);
		return df.format(d);
	}
	
	public static String formatDate(Date date)
	{
		return formatDate(date,"yyyy-MM-dd HH:mm:ss");
	}
	
	public static String formatDate(Date date,String format)
	{
		if(date == null)
		{
			return null;
		}
		return new SimpleDateFormat(format).format(date);
	}
}


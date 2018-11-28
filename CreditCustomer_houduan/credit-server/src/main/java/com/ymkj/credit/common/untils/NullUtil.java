package com.ymkj.credit.common.untils;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 判断对象是否为空
 */
public class NullUtil {
	
	/**
	 * 数组是否为空
	 */
	public static boolean isEmpty(Object objs){
		if(objs==null){
			return true;
		}
		return false;
	}
	
	/**
	 * 数组是否为空
	 */
	public static boolean isEmpty(Object[] objs){
		if(objs==null || objs.length==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 数组是否为空
	 */
	public static boolean isEmpty(List<?> objs){
		if(objs==null || objs.size()<=0){
			return true;
		}
		return false;
	}
	
	/**
	 * byte是否为空
	 */
	public static boolean isEmpty(byte[] objs){
		if(objs==null || objs.length==0){
			return true;
		}
		return false;
	}
	
	/**
	 * 字符串是否为空
	 */
	public static boolean isEmpty(String str){
		if(str==null || str.equals("null") || str.trim().length()==0){
			return true;
		}
		return false;
	}
	
	/**
	 * long是否为空
	 */
	public static boolean isEmpty(Long l){
		if(l==null || l.longValue()==0L){
			return true;
		}
		return false;
	}
	
	/**
	 * integer是否为空
	 */
	public static boolean isEmpty(Integer i){
		if(i==null || i==0){
			return true;
		}
		return false;
	}

	/**
	 * 身份证合法性校验
	 * @param idCard
	 * @return
	 */
	public static boolean is18ByteIdCard(String idCard){
		Pattern pattern1 = Pattern.compile("(^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$)|(^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}[0-9Xx]$)"); //粗略的校验
		Matcher matcher = pattern1.matcher(idCard);
		if(matcher.matches()){
			return true;
		}
		return false;
	}
}

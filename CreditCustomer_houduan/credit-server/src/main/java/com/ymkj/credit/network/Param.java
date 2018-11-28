package com.ymkj.credit.network;

import java.text.MessageFormat;
import java.util.Date;
import java.util.UUID;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.untils.DateUtil;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.common.untils.SpringContextHelper;
import com.ymkj.credit.config.ApplicationBean;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;


/**
 * webservice的传递参数
 * 
 */
@Slf4j
public class Param {
	
    private static final String SIGN_SEPARATOR = "|";

    private static ApplicationBean applicationBean;

    static{
    	applicationBean = (ApplicationBean) SpringContextHelper.getBean("applicationBean");
    }
    
	/**
	 * 获取验发转签接口请求的参数对象
	 * 
	 * @param params 参数
	 * @return
	 */
	public static String getReqMain(JSONObject params) {
		JSONObject json = new JSONObject();
		String projectNo = "credit";
		String sn = projectNo + createSn();
		json.put(Constants.PROGECT_NO, projectNo);
		json.put(Constants.REQ_RUL, "");
		json.put(Constants.REQ_PARAM, params);
		json.put(Constants.REQ_HEAD_PARAM, getReqHeadParams());
		json.put(Constants.REQ_TIMESTAMP, "123"); 
		json.put(Constants.SN, sn);
		return json.toString();
	}
	public static String getReqMain4ZYBefore(JSONObject params,Long currentTime) {
		JSONObject json = new JSONObject();
		String projectNo = "Lc_WS2015";
		String sn = projectNo + createSn();
		json.put(Constants.REQ_RUL, "");
		json.put(Constants.PROGECT_NO, projectNo);
		json.put(Constants.deviceId, "0D4DCD6B-5216-49B3-A460-11C3B0BB2283");
		json.put(Constants.REQ_HEAD_PARAM, getReqHeadParams());
		json.put(Constants.operatorName, "zdqq");
		json.put(Constants.SN, sn);
		json.put(Constants.REQ_TIMESTAMP, currentTime); 
		json.put(Constants.operatorCode, "zdqq");
		json.put(Constants.REQ_PARAM, params);
		return json.toString();
	}
	public static String getReqMain4ZY(JSONObject params,String sign,Long currentTime) {
		JSONObject json = new JSONObject();
		String projectNo = "Lc_WS2015";
		String sn = projectNo + createSn();
		json.put(Constants.REQ_RUL, "");
		json.put(Constants.PROGECT_NO, projectNo);
		json.put(Constants.deviceId, "0D4DCD6B-5216-49B3-A460-11C3B0BB2283");
		json.put(Constants.REQ_HEAD_PARAM, getReqHeadParams());
		json.put(Constants.operatorName, "zdqq");
		json.put(Constants.SN, sn);
		json.put(Constants.REQ_TIMESTAMP, currentTime); 
		json.put(Constants.operatorCode, "zdqq");
		json.put(Constants.sign, sign);
		json.put(Constants.REQ_PARAM, params);
		return json.toString();
	}
	/**
	 * 报文头
	 * 
	 * @return
	 */
	public static JSONObject getReqHeadParams() {
		JSONObject json = new JSONObject();
		json.put(Constants.OPENCHANNEL, "credit");
		json.put(Constants.SESSIONTOKEN, "50caefb0-b863-4524-950f-3a3143b2b247");
		json.put(Constants.PLATFORM, "web");
		json.put(Constants.USERAGENT, "信贷展业-Develop;2.2.1;Android;4.4.2;PE-TL20");
		json.put(Constants.VERSION, "2.3.0");
		json.put(Constants.TOGATHERTYPE, "证大钱钱");
		json.put(Constants.MECHANISM, "证大");
		json.put(Constants.TOKEN, "");
		return json;
	}

	/**
	 * 生成流水号
	 * 
	 * @return
	 */
	private static String createSn() {
		String sign = DateUtil
				.format(new Date(), DateUtil.DATE_TIME_SSS_FORMAT)
				+ UUID.randomUUID().toString().substring(0, 8);
		return sign;
	}
	
	/**
	 * 请求入参
	 * 
	 * @param functionId 功能号
	 * @param model 请求参数
	 * @return<arg0=&arg1=&arg2=>
	 */
	public static String getParam(String functionId, JSONObject model) {
		String arg0 = functionId;// 功能号
		String arg1 = getReqMain(model);// 请求参数
		String arg2 = applicationBean.getMd5SignKey();
		StringBuilder sb = null;
		if (!ApplicationBean.SWITCH_OFF.equals(applicationBean
				.getCeValidateSwitch())) {
			sb = new StringBuilder();
			sb.append(arg0).append(SIGN_SEPARATOR).append(arg1).append(SIGN_SEPARATOR).append(arg2);
			arg2 = MD5.MD5Encode(sb.toString());
		}
		sb = new StringBuilder();
		sb.append("arg0=").append(arg0).append("&arg1=").append(arg1)
				.append("&arg2=").append(arg2);
		String params = sb.toString();
		log.info(MessageFormat.format("功能号: {0}；入参: {1}", functionId, params));
		return params;
	}
	/**
	 * 请求入参
	 * 
	 * @param functionId 功能号
	 * @param model 请求参数
	 * @return<arg0=&arg1=&arg2=>
	 */
	public static String getParam4Zy(String functionId, JSONObject model,Long currentTime) {
		String arg0 = functionId;// 功能号
		String arg1 = getReqMain4ZYBefore(model,currentTime);// 请求参数
		String arg2 = currentTime+"";
		String arg3 = applicationBean.getMd5SignKey4ZY();
		String sign = getGenerateSign(functionId,arg1,currentTime+"",arg3);
		arg1 = getReqMain4ZY(model, sign, currentTime);
		StringBuilder sb = null;
		if (!ApplicationBean.SWITCH_OFF.equals(applicationBean
				.getCeValidateSwitch())) {
			sb = new StringBuilder();
			sb.append(arg0).append(arg1).append(arg2).append(arg3);
			arg3 = MD5.MD5Encode(sb.toString());
		}
		sb = new StringBuilder();
		sb.append("arg0=").append(arg0).append("&arg1=").append(arg1)
				.append("&arg2=").append(arg2).append("&arg3=").append(arg3);
		String params = sb.toString();
		log.info(MessageFormat.format("功能号: {0}；入参: {1}", functionId, params));
		return params;
	}

	public static void main(String[] args) {
		JSONObject param = new JSONObject();
		param.put("loginName", "zhangsan");
		param.put("password", "123456");
		System.out.println(getReqMain(param));
	}
	
	  public static String getGenerateSign( String func_No,String param_In,String key,String md5SignKey) {
	    String sign = MD5.MD5Encode(func_No + param_In + key + md5SignKey);
	    return sign;
	  }
}

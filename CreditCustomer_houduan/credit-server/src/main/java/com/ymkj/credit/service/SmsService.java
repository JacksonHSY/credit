package com.ymkj.credit.service;


import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.springside.modules.exception.BusinessException;

import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.ymkj.credit.common.constants.Constants;
import com.zendaimoney.ice.uc.client.CustInfo;
import com.zendaimoney.ice.uc.client.MsgBody;
import com.zendaimoney.ice.uc.client.UInfo;
import com.zendaimoney.ice.uc.client.service.IDataSendService;
import com.zendaimoney.ice.uc.client.service.impl.ShortSendService;

import lombok.extern.log4j.Log4j;

@Log4j
@Service
public class SmsService {

	@Value("${send_sms_switch}")
	private String sendSmsSwitch;
	
	@Value("${SMS_SYS_UID}")
	private String sysUID;
	
	@Value("${SMS_EMP_Id}")
	private String empId;
	/**
	 * 短信发送
	 * @param markInfo
	 * @param message
	 * @param cmarkeType
	 * @param channalType
	 * @return
	 */
	public String sendSmsOld(String markInfo, String message) {
		log.info("短信发送内容:"+message);
		if(Constants.SWITCH_OFF.equals(sendSmsSwitch)){
			log.info("短信发送开关处于关闭状态");
			JSONObject result = new JSONObject();
			result.put("ucCode",Constants.SMS_RESULT_SUCCESS_CODE);
			result.put("ucMessage","短信发送成功");
			return result.toString();
		}
		IDataSendService dataSendService = new ShortSendService();
		UInfo uInfo = new UInfo();
		Gson gson = new Gson();
		log.info("用户接受唯一标识:"+markInfo);
		System.out.println("用户接受唯一标识:"+markInfo);
		CustInfo[] custInfos = new CustInfo[1];
		if (StringUtils.isBlank(markInfo)) {
			log.info("用户接受唯一标识不能为空");
			throw new BusinessException(BussErrorCode.ERROR_CODE_3101.getErrorcode(),BussErrorCode.ERROR_CODE_3101.getErrordesc());
		}
		CustInfo custInfo = new CustInfo();
		custInfo.setcMarkType(Constants.TYTX_CMARKETYPE_1);
		custInfo.setcType(Constants.TYTX_CTYPE_0);
		custInfo.setMarkInfo(markInfo);
		custInfos[0] = custInfo;

		uInfo.setCustInfo(custInfos);
		String unifo = gson.toJson(uInfo);
		if (StringUtils.isBlank(message)) {
			log.error("发送内容不能为空");
			throw new BusinessException(BussErrorCode.ERROR_CODE_3102.getErrorcode(),BussErrorCode.ERROR_CODE_3102.getErrordesc());
		}
		// 构造短信内容json字符
		MsgBody msgBody = new MsgBody();
		msgBody.setBody(message);
		msgBody.setBodyFormat(Constants.TYTX_BODYFORMAT_TEXT);
		String jsonMsg = gson.toJson(msgBody);

		String delaySendTimeString = null;
		String lastSendTimeString = Constants.TYTX_LATESENDTIME;
		String resultStr = dataSendService.sendMsg(Constants.TYTX_SYS_UID, Constants.TYTX_EMP_Id,
				unifo, Constants.TYTX_POLICY, Constants.TYTX_CHANNEL_SMS, null, null,
				Constants.TYTX_M_TYPE_SMS, jsonMsg, delaySendTimeString,
				lastSendTimeString, null, "UTF-8");
		log.info("发送短信结果"+resultStr);
		
		return resultStr;
	}

	/**
	 * 消息推送
	 * @param markInfo
	 * @param message
	 * @param cmarkeType
	 * @param channalType
	 * @return
	 */
	public String pushMessage(String markInfo, String message,String subject,String summary,String cmarkeType) {
		IDataSendService dataSendService = new ShortSendService();
		UInfo uInfo = new UInfo();
		Gson gson = new Gson();
		log.info("用户接受唯一标识:"+markInfo);
		System.out.println("用户接受唯一标识:"+markInfo);
		CustInfo[] custInfos = new CustInfo[1];
		if (StringUtils.isBlank(markInfo)) {
			log.info("用户接受唯一标识不能为空");
			throw new BusinessException(BussErrorCode.ERROR_CODE_3101.getErrorcode(),BussErrorCode.ERROR_CODE_3101.getErrordesc());
		}
		CustInfo custInfo = new CustInfo();
		if(Constants.PLATFORM_TYPE_IOS.equals(cmarkeType.toUpperCase())){
			custInfo.setcMarkType(Constants.TYTX_CMARKETYPE_5);
		}else{
			custInfo.setcMarkType(Constants.TYTX_CMARKETYPE_4);
		}
		custInfo.setcType(Constants.TYTX_CTYPE_0);
		custInfo.setMarkInfo(markInfo);
		custInfos[0] = custInfo;

		uInfo.setCustInfo(custInfos);
		String unifo = gson.toJson(uInfo);
		if (StringUtils.isBlank(message)) {
			log.error("发送内容不能为空");
			throw new BusinessException(BussErrorCode.ERROR_CODE_3102.getErrorcode(),BussErrorCode.ERROR_CODE_3102.getErrordesc());
		}
		// 构造短信内容json字符
		MsgBody msgBody = new MsgBody();
		msgBody.setSubject(subject);
		msgBody.setSummary(summary);
		msgBody.setBody(message);
		msgBody.setBodyFormat(Constants.TYTX_BODYFORMAT_TEXT);
		String jsonMsg = gson.toJson(msgBody);

		String delaySendTimeString = null;
		String lastSendTimeString = Constants.TYTX_LATESENDTIME;
		String resultStr = dataSendService.sendMsg(Constants.TYTX_SYS_UID, Constants.TYTX_EMP_Id,
				unifo, Constants.TYTX_POLICY, Constants.TYTX_CHANNEL_INFORM, null, null,
				Constants.TYTX_M_TYPE_MESSAGE, jsonMsg, delaySendTimeString,
				lastSendTimeString, null, "UTF-8");
		log.info("发送短信结果"+resultStr);

		return resultStr;
	}
	/**
	 * 
	 * @TODO
	 * @param cvMobile
	 * @param msgContent
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年4月4日
	 */
	public String sendSms(String cvMobile,String msgContent){
	    IDataSendService dataSendService = new ShortSendService();
	    UInfo u = new UInfo();
	    //待发送明细信息
	    CustInfo[] arrCustInfo = new CustInfo[1];
	    CustInfo custInfo = new CustInfo();
	    custInfo.setcType("-1"); //客户类型: 1员工 3交易用户 10体验用户 11潜在客户-1匿名用户
	    custInfo.setcMarkType("1"); //客户标识类型: 1手机号码2邮箱地址3接收用户唯一标识(客户号、工号、昵称等) 4  Android手机设备号 5 IOS手机设备号 6 微信号
	    custInfo.setMarkInfo(cvMobile); //标识信息,手机号码、邮箱地址、接收用户唯一标识、手机设备号、微信号
	    arrCustInfo[0] = custInfo;

	    u.setCustInfo(arrCustInfo);

	    Gson gson = new Gson();
	    String uinfo = gson.toJson(u);
	    log.info("-----------uinfo   JSON----------->"+uinfo);

	    String policy = "0";//发送策略，只能为以下几个值：0指定通道发送
	    String channel = "sms";//发送渠道 发送策略=0(指定通道发送)：格式为:sms或者mail或inform
	    String templateId = "";
	    String specialId = "";
	    String mtype = Constants.TYTX_M_TYPE_SMS;//信息类别10000002

	    MsgBody msgObj = new MsgBody();
	    msgObj.setBody(msgContent); //正文内容
	    msgObj.setBodyFormat("text"); //正文内容格式类型,分别为text、html、url，
	    msgObj.setIsTop("0"); //,消息是否置顶(0,1)

	    String msgBody = gson.toJson(msgObj);; //用户信息  JSONe格式
	    log.info("-----------msgBody   JSON----------->"+msgBody);

	    String delaySendTime = null;
	    String lateSendTime = "2099-12-30 23";
	    String encoding = "UTF-8"; //字符编码方式
	    return dataSendService.sendMsg(sysUID, empId, uinfo, policy, channel, templateId, specialId,
	            mtype, msgBody, delaySendTime, lateSendTime, null, encoding);
	}
	/**
	 * 消息推送
	 * @param markInfo
	 * @param message
	 * @param cmarkeType
	 * @param channalType
	 * @return
	 */
	public String pushMessageForzdqq(String markInfo, String message,String subject,String summary,String cmarkeType) {
		IDataSendService dataSendService = new ShortSendService();
		UInfo uInfo = new UInfo();
		Gson gson = new Gson();
		log.info("用户接受唯一标识:"+markInfo);
		System.out.println("用户接受唯一标识:"+markInfo);
		CustInfo[] custInfos = new CustInfo[1];
		if (StringUtils.isBlank(markInfo)) {
			log.info("用户接受唯一标识不能为空");
			throw new BusinessException(BussErrorCode.ERROR_CODE_3101.getErrorcode(),BussErrorCode.ERROR_CODE_3101.getErrordesc());
		}
		CustInfo custInfo = new CustInfo();
		if(Constants.PLATFORM_TYPE_IOS.equals(cmarkeType.toUpperCase())){
			custInfo.setcMarkType(Constants.TYTX_CMARKETYPE_5);
		}else{
			custInfo.setcMarkType(Constants.TYTX_CMARKETYPE_4);
		}
		custInfo.setcType(Constants.TYTX_CTYPE_0);
		custInfo.setMarkInfo(markInfo);
		custInfos[0] = custInfo;

		uInfo.setCustInfo(custInfos);
		String unifo = gson.toJson(uInfo);
		if (StringUtils.isBlank(message)) {
			log.error("发送内容不能为空");
			throw new BusinessException(BussErrorCode.ERROR_CODE_3102.getErrorcode(),BussErrorCode.ERROR_CODE_3102.getErrordesc());
		}
		// 构造短信内容json字符
		MsgBody msgBody = new MsgBody();
		msgBody.setSubject(subject);
		msgBody.setSummary(summary);
		msgBody.setBody(message);
		msgBody.setBodyFormat(Constants.TYTX_BODYFORMAT_TEXT);
		String jsonMsg = gson.toJson(msgBody);

		String delaySendTimeString = null;
		String lastSendTimeString = Constants.TYTX_LATESENDTIME;
		String resultStr = dataSendService.sendMsg(sysUID, empId,
				unifo, Constants.TYTX_POLICY, Constants.TYTX_CHANNEL_INFORM, null, null,
				Constants.TYTX_M_TYPE_MESSAGE, jsonMsg, delaySendTimeString,
				lastSendTimeString, null, "UTF-8");
		log.info("发送短信结果"+resultStr);

		return resultStr;
	}
	
}
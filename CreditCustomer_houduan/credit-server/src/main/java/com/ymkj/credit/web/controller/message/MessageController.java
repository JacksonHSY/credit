package com.ymkj.credit.web.controller.message;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.MessagePushLog;
import com.ymkj.credit.common.entity.SmsMessageRecord;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.vo.ResultVo;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.MessagePushLogService;
import com.ymkj.credit.service.SmsMessageRecordService;
import com.ymkj.credit.service.SmsService;
import com.ymkj.credit.web.api.anno.PageSolver;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.orm.PageInfo;
import com.ymkj.springside.modules.utils.StrUtils;
import com.ymkj.sso.client.ShiroUtils;


@RequestMapping(value = "/manage")
@Controller
@Slf4j
public class MessageController {
	
	@Autowired
	private SmsMessageRecordService smsMessageRecordLogService;
	
	@Autowired
	private CustomerService customerService;
	
	@Autowired
	private SmsService smsService;
	
	@RequestMapping("/messageList")
	public String index(Model model,HttpSession httpSession) {
		return "message/messagelist";
	}
	/*
	@RequestMapping("/showMessage")
	@ResponseBody
	public ResultVo announcementListPage(
			@RequestParam(value = "page", defaultValue = "1") int page,
			@RequestParam(value = "rows", defaultValue = "10") int rows,
			@RequestParam(value = "sort", defaultValue = "id") String sort,
			@RequestParam(value = "order", defaultValue = "desc") String order,
			MessagePushLog messageLog,HttpServletRequest request) {
	    PageInfo<MessagePushLog> requestbody = new PageInfo<MessagePushLog>();
	    requestbody.setPageNo(page);
		requestbody.setPageSize(rows);
		requestbody.setQueryParam(messageLog);
		requestbody = messagePushLogService.getMessageListPage(requestbody);
		return ResultVo.returnPage(requestbody);
	}
	*/
	
	@RequestMapping("/message/showMessage")
	@ResponseBody
	public ResultVo announcementListPage(@PageSolver PageInfo<SmsMessageRecord> page,SmsMessageRecord smsMessageRecord) {
		return ResultVo.returnPage(smsMessageRecordLogService.getMessageListPage(page,smsMessageRecord));
	}
	
	@RequestMapping("/message/savemessage")
	@ResponseBody
	public ResultVo saveMessage(SmsMessageRecord smsMessageRecord,HttpServletRequest request) throws Exception {
		try {
			Customer record=customerService.findByPhone(smsMessageRecord.getPhone());
			if(null == record){
				return  ResultVo.returnMsg(false, "手机号不存在或已注销");
			}else{
				if(!smsMessageRecord.getCustomerName().equals(record.getCustomerName())){
					return  ResultVo.returnMsg(false, "手机号和姓名不是同一个人");
				}
			}
			String resultStr = smsService.sendSms(smsMessageRecord.getPhone(),smsMessageRecord.getContent() );
			if(StringUtils.isBlank(resultStr)){
				log.error("========>>>发送短信异常，统一通信响应信息为空");
				return  ResultVo.returnMsg(false, "发送短信异常，统一通信响应信息为空");
			}
			JSONObject json = JSONObject.fromObject(resultStr);
			if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
				log.info("短信发送失败："+json.get("ucMessage").toString());
				return  ResultVo.returnMsg(false, json.get("ucMessage").toString());
			}
			smsMessageRecord.setOperater(ShiroUtils.getCurrentUser().getName());
			smsMessageRecordLogService.saveSmsMessageRecord(smsMessageRecord);
			return  ResultVo.returnMsg(true, "发送成功");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return  ResultVo.returnMsg(false, "消息发送失败");
	}
}

package com.ymkj.credit.web.api;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.ApplyLoanInfo;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.LoanOrder;
import com.ymkj.credit.common.entity.Message;
import com.ymkj.credit.common.entity.MessagePushLog;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.untils.HttpUtils;
import com.ymkj.credit.common.untils.NumberUtil;
import com.ymkj.credit.service.ApplyLoanInfoService;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.LoanOrderService;
import com.ymkj.credit.service.MessagePushLogService;
import com.ymkj.credit.service.PushMessageService;
import com.ymkj.credit.service.SmsService;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.springside.modules.exception.BusinessException;
import org.apache.commons.codec.binary.Base64;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.conn.HttpHostConnectException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 信贷推送接口
 * 
 * @author tianx@yuminsoft.com
 */
@RequestMapping(value = "/credit")
@Controller
@Slf4j
public class CreditController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private LoanOrderService loanOrderService;
    @Autowired
    private SmsService smsService;
    @Autowired
    private MessagePushLogService messagePushLogService;
    @Autowired
    private PushMessageService messagePush;
    @Value("${urlUploadReport}")
	private String urlUploadReport;
    @Autowired
	ApplyLoanInfoService applyLoanInfoService;

    
    /**
     * ProductID密钥
     */
    @Value("${AES_KEY}")
    private String AES_KEY;

    /**
     * 偏移量
     */
    @Value("${AES_IV}")
    private String AES_IV;
    /**
     * @param httpRequest
     * @param httpResponse
     * @param idNum
     * @param content
     * @param batchNo
     * @return
     */
    @RequestMapping(value = "/pushMessage")
    @ResponseBody
	public String pushMessage(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			@RequestParam("idNum") String idNum,
            @RequestParam("subject") String subject,
            @RequestParam("summary") String summary,
			@RequestParam("content") String content,
			@RequestParam("batchNo") String batchNo) {
        log.info("===================》》》消息推送接口入参：idNum="+idNum+", subject="+subject+", summary="+summary+", content="+content+", batchNo="+batchNo);
        Result result = null;

        MessagePushLog pushLog = new MessagePushLog();
        pushLog.setIdCard(idNum);
        pushLog.setBatchNo(batchNo);
        pushLog.setContent(content);
        pushLog.setPushTime(new Date());
        pushLog.setTitle(subject);
        try {
            //根据身份证查询客户信息
            Customer query = new Customer();
            query.setIdCard(idNum);
            query.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
            Customer record = customerService.selectOne(query);
            if(null == record){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"推送客户未注册");
            }
            if(StringUtils.isBlank(record.getPushId())){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"客户推送设备号为空");
            }
            pushLog.setPushId(record.getPushId());
            if(StringUtils.isBlank(record.getPlatform())){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"客户推送平台为空");
            }
            String smsResult= smsService.pushMessage(record.getPushId(),content,subject,summary,record.getPlatform());
            if(StringUtils.isBlank(smsResult)){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            }
            net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            }
            pushLog.setReturnResult("成功");
            pushLog.setReturnMessage(json.get("ucMessage").toString());
            result = Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BusinessException) {
                String message = e.getMessage();
                result = Result.fail(message);
                pushLog.setReturnResult("失败");
                pushLog.setReturnMessage(message);
                log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
            } else {
                result = Result.fail();
                log.error("调用功能时系统异常: ", e);
            }
        }
        messagePushLogService.insert(pushLog);
        log.info("*************************响应*************************");
        log.info(JSONObject.fromObject(result).toString());
        return JSONObject.fromObject(result).toString();
    }
   /**
    * 
    * @TODO
    * @param httpRequest
    * @param httpResponse
    * @param loanNo
    * @param type  1:取消申请  0:拒绝申请
    * @return
    * String
    * @author changj@yuminsoft.com
    * @date2018年3月14日
    */
    @RequestMapping(value = "/pushLoanStatusOver")
    @ResponseBody
	public String pushMessage4Dms(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			@RequestParam(value="loanNo",required=true) String loanNo,
           @RequestParam(value="type",required=true) int type) {
        log.info("===================》》》申请单状态变更消息推送接口入参：loanNo="+loanNo+",type"+type);
        Result result = null;
        MessagePushLog pushLog = new MessagePushLog();
        pushLog.setBatchNo(NumberUtil.getNumberForPK());
        String content = type==0?"您的申请单已被拒绝":"您的申请单已取消";
        pushLog.setContent(content);
        pushLog.setPushTime(new Date());
        pushLog.setTitle("申请进度提醒");
        try {
        	LoanOrder lo = loanOrderService.queryByOrderNo(loanNo);
        	if(lo == null){
        		 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"未找到该申请单");
        	}
        	//更新申请单状态
        	if(type==0){
        		lo.setFlowstatus(Constants.ORDER_STATES_SHENQINGQQ);//取消申请
        	}else if(type==1){
        		lo.setFlowstatus(Constants.ORDER_STATES_SHENGQINGJJ);//申请拒绝
        	}else{
        	      throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"type必须为0或1");
        	}
        	lo.setUpdatetime(new Date());
        	loanOrderService.updateLoanOrder(lo);
            //根据身份证查询客户信息
            Customer query = new Customer();
            query.setId(lo.getCid());
            query.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
            Customer record = customerService.selectOne(query);
            if(null == record){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"推送客户未注册");
            }
            pushLog.setIdCard(record.getIdCard());
            if(StringUtils.isBlank(record.getPushId())){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"客户推送设备号为空");
            }
            pushLog.setPushId(record.getPushId());
            if(StringUtils.isBlank(record.getPlatform())){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"客户推送平台为空");
            }
            String smsResult= smsService.pushMessage(record.getPushId(),content,"申请进度提醒",content,record.getPlatform());
            if(StringUtils.isBlank(smsResult)){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            }
            net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            }
            pushLog.setReturnResult("成功");
            pushLog.setReturnMessage(json.get("ucMessage").toString());
            result = Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BusinessException) {
                String message = e.getMessage();
                result = Result.fail(message);
                pushLog.setReturnResult("失败");
                pushLog.setReturnMessage(message);
                log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
            } else {
                result = Result.fail();
                log.error("调用功能时系统异常: ", e);
            }
        }
        messagePushLogService.insert(pushLog);
        log.info("*************************响应*************************");
        log.info(JSONObject.fromObject(result).toString());
        return JSONObject.fromObject(result).toString();
    }
    
    
    /**
     * 还款通知
     * @TODO
     * @param httpRequest
     * @param httpResponse
     * @param idNum
     * @param status  0:成功 ,1失败
     * @return
     * String
     * @author changj@yuminsoft.com
     * @date2018年6月26日
     */
    @RequestMapping(value = "/pushMsg4Repayment")
    @ResponseBody
	public String pushMsg4Repayment(HttpServletRequest httpRequest,
			HttpServletResponse httpResponse,
			@RequestParam("idNum") String idNum,
			@RequestParam("content") String content) {
        log.info("===================》》》还款结果通知接口入参：idNum="+idNum+", content="+content);
        Result result = null;
        String title = "还款结果通知";
        MessagePushLog pushLog = new MessagePushLog();
        pushLog.setIdCard(idNum);
        pushLog.setBatchNo(NumberUtil.getNumberForPK());
        pushLog.setContent(content);
        pushLog.setPushTime(new Date());
        pushLog.setTitle(title);
        try {
            //根据身份证查询客户信息
            Customer query = new Customer();
            query.setIdCard(idNum);
            query.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
            Customer record = customerService.selectOne(query);
            if(null == record){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"推送客户未注册");
            }
            if(StringUtils.isBlank(record.getPushId())){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"客户推送设备号为空");
            }
            pushLog.setPushId(record.getPushId());
            if(StringUtils.isBlank(record.getPlatform())){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),"客户推送平台为空");
            }
            String smsResult= smsService.pushMessage(record.getPushId(),content,title,content,record.getPlatform());
            if(StringUtils.isBlank(smsResult)){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            }
            net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
                throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            }
            pushLog.setReturnResult("成功");
            pushLog.setReturnMessage(json.get("ucMessage").toString());
            result = Result.success();
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof BusinessException) {
                String message = e.getMessage();
                result = Result.fail(message);
                pushLog.setReturnResult("失败");
                pushLog.setReturnMessage(message);
                log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
            } else {
                result = Result.fail();
                log.error("调用功能时系统异常: ", e);
            }
        }
        messagePushLogService.insert(pushLog);
        log.info("*************************响应*************************");
        log.info(JSONObject.fromObject(result).toString());
        return JSONObject.fromObject(result).toString();
    }
    
    
    /**
     * 
     * @TODO
     * @param httpRequest
     * @param httpResponse
     * @param type  枚举值（4：注册，5：提交申请，6：审核退回，7：审核通过待签约，8：待放款，9：放款，渠道捞财宝，10：还款提醒，11：还款日前未还款，12：还款日前还款入账成功（包含客户提前还款），13：审核拒绝,14:实际还款,15:结清提示,16:客户经理离职提醒,17:放款，渠道费捞财宝）
     * @return
     * String
     * @author huangsy@yuminsoft.com
     * @date2018年8月07日
     */
     @RequestMapping(value = "/pushMessageBySystem", consumes = "application/json")
     @ResponseBody
 	public String pushMessageBySystem(@RequestBody String param)
     {
    	 Customer query = new Customer();
    	 JSONObject params = JSONObject.fromObject(param);
    	 JSONObject paramRequest = JSONObject.fromObject(params.get("param"));
    	 String loanNo = String.valueOf(paramRequest.get("loanNo"));
    	 String cusName = String.valueOf(paramRequest.get("cusName"));
    	 String banPhone = String.valueOf(paramRequest.get("banPhone"));
    	 String repaidNum = String.valueOf(paramRequest.get("repaidNum"));
    	 String bankCard = String.valueOf(paramRequest.get("bankCard"));
    	 String repaidTotal = String.valueOf(paramRequest.get("repaidTotal"));
    	 String idNum = String.valueOf(paramRequest.get("idNum"));
    	 String gender = String.valueOf(paramRequest.get("gender"));
    	 String realPaid = String.valueOf(paramRequest.get("realPaid"));
    	 String type = String.valueOf(params.get("type"));
    	if(paramRequest.containsKey("phone")){
    		String phone = String.valueOf(paramRequest.get("phone"));
    		query.setPhone(phone);
    	}
    	 log.info("推送接口入参："+params.toString()+"业务类型："+type);
         Result result = null;
         if(cusName!=null){
        	 try {
				cusName = new String(cusName.getBytes("utf-8"), "utf-8");
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
         }
         //MessagePushLog pushLog = new MessagePushLog();
         //pushLog.setBatchNo(NumberUtil.getNumberForPK());
         //String content = type==0?"您的申请单已被拒绝":"您的申请单已取消";
         //pushLog.setContent(content);
         //pushLog.setPushTime(new Date());
         //pushLog.setTitle("");
         try {
             //根据身份证查询客户信息
             query.setIdCard(idNum);
             //query.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
             Customer record = customerService.selectOne(query);
             if(null == record){
                 throw new BusinessException(BussErrorCode.ERROR_CODE_0113.getErrorcode(),"推送客户未注册");
             }
             //pushLog.setIdCard(record.getIdCard());
             if(StringUtils.isBlank(record.getPushId())){
                 throw new BusinessException(BussErrorCode.ERROR_CODE_0123.getErrorcode(),"客户推送设备号为空");
             }
             //pushLog.setPushId(record.getPushId());
             if(StringUtils.isBlank(record.getPlatform())){
                 throw new BusinessException(BussErrorCode.ERROR_CODE_0133.getErrorcode(),"客户推送平台为空");
             }
             if(type.equals(Constants.REGIEST)){//判断业务场景
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.SUBMIT)){//提交申请
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.REBACK)){//审核退回
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.PASS)){//审核通过待签约
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.PENDINGMONEY)){//待放款
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.LOAN)){//放款
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.REPAYMENT)){//还款
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent().replace("d",repaidTotal);
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.OVERDUE)){//还款日前未还款
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent().replace("b",repaidTotal);
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.HAVEREPALY)){//还款日前还款入账成功（包含客户提前还款）
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            		 
            	 }
             }else if(type.equals(Constants.REFUSE)){//审核拒绝
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.REPLAY)){//实际还款
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent().replace("a",realPaid);
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }
             else if(type.equals(Constants.SETTLE)){//结清提示
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent().replace("a",loanNo);
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.OUIT)){//客户经理离职提醒
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }else if(type.equals(Constants.LOANNOTLCB)){//放款，渠道费捞财宝
            	 Message msg = new Message();
            	 msg.setType(type);
            	 Message messageContent = messagePush.selectContent(msg);
            	 if(messageContent!=null){
            		 String content = messageContent.getContent();
            		 log.info("短信内容："+content);
            		 String smsResult= smsService.pushMessageForzdqq(record.getPushId(),content,"证大前前",content,record.getPlatform());
            		 if(StringUtils.isBlank(smsResult)){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),"统一通信请求出错");
            		 }
            		 net.sf.json.JSONObject json = net.sf.json.JSONObject.fromObject(smsResult);
            		 if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
            			 throw new BusinessException(BussErrorCode.ERROR_CODE_0103.getErrorcode(),json.get("ucMessage").toString());
            		 }
            	 }
             }
             //pushLog.setReturnResult("成功");
             //pushLog.setReturnMessage(json.get("ucMessage").toString());
             result = Result.success();
         } catch (Exception e) {
             e.printStackTrace();
             if (e instanceof BusinessException) {
            	 String message = e.getMessage();
                 result = Result.fail(message,((BusinessException) e).getCode());
                 //pushLog.setReturnResult("失败");
                 //pushLog.setReturnMessage(message);
                 log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
             } else {
                 result = Result.fail();
                 log.error("调用功能时系统异常: ", e);
             }
         }
         //messagePushLogService.insert(pushLog);
         log.info("*************************响应*************************");
         log.info("接口出参："+JSONObject.fromObject(result).toString());
         return JSONObject.fromObject(result).toString();
     
     }

     /**
     *@TODO 芝麻信用第三方回调地址
     * @param httpRequest
     * @param httpResponse
     * @param 
     * @return
     * String
     * @author huangsy@yuminsoft.com
      * */
     @RequestMapping(value = "/getScorefromMob/{loanNo}", consumes = "application/json")
     @ResponseBody
     public String getScorefromMob(@RequestBody String response ,@PathVariable String loanNo){
    	 String result = "";
    	 try {
    			if(StringUtils.isEmpty(response)){
    				log.error("缺少授权结果报文");
    				return "授权结果为空";
    			}

    			log.info("芝麻信用授权异步返回参数:{}", response);
    			log.info("PRODUCT ID密钥:{}", AES_KEY);
    			log.info("偏移量:{}", AES_IV);
    			// apache base64解密
    			byte[] c = Base64.decodeBase64(response);
    			
    			log.info("base64解密:{}", new String(c));
    			
    			// AES解密秘钥
    			SecretKey keyspec = new SecretKeySpec(AES_KEY.getBytes("utf8"), "AES");
    			// 偏移量
    			IvParameterSpec ivspec = new IvParameterSpec(AES_IV.getBytes("utf8"));
    			// 初始化cipher
    			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
    			cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
    			// 开始AES解密
    			byte[] original = cipher.doFinal(c);
    			// 打印解密字符
    			result = new String(original,"utf8").trim();

    			log.info("AES解密:{}", result);
    		} catch (Exception e) {
    			log.error("芝麻信用授权异步返回异常", e);
    			return "fail";
    		}
    	 	JSONObject obj = JSONObject.fromObject(result);
    	 	if(obj.containsKey("status")){
    	 		if(String.valueOf(obj.getString("status")).equals("1")){
    	 			String idNum = (String) obj.get("certNo");//获取回调的身份证
    	            Customer query = new Customer();
    	            query.setIdCard(idNum);
    	            query.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
    	            Customer record = customerService.selectOne(query);
    	 			String name = record.getCustomerName();//客户姓名
    	 			//String name = "张十二";
    	            //调征信系统查询芝麻信用分
    	 			Map<String ,Object> selectScore = new HashMap<String,Object>();
    	 			selectScore.put("bizSys", "app");
    	 			selectScore.put("name", name);
    	 			selectScore.put("idNum", idNum);
    	 			selectScore.put("appNo", loanNo);
    	 			selectScore.put("searchType", "0");
    	 			log.info("调用征信查询芝麻信用入参："+selectScore.toString());
    	 			String rusults = "";
						try {
							log.info("调用征信查询芝麻信用地址："+urlUploadReport+Constants.SELECT_ZMSCORE);
							rusults = HttpUtils.doPost(urlUploadReport+Constants.SELECT_ZMSCORE, selectScore);
							log.info("芝麻信用查询结果："+rusults.toString());
							if(StringUtils.isNotEmpty(rusults)){
								JSONObject ob = JSONObject.fromObject(rusults);
								if(ob.get("code").equals("000000")){
									String score = ob.getString("score");//芝麻信用分
									log.info("芝麻分："+score);
									ApplyLoanInfo info = new ApplyLoanInfo();
									info.setLoanNo(loanNo);
									info.setZmScore(score);
									applyLoanInfoService.updateByLoan(info);
									
								}
							}
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
    	 		}else{
    	 			log.info("调用第三方获取芝麻信用失败"+result.toString());
    	 		}
    	 	}
    		return "success";
     }
     /** 
      * 将16进制转换为二进制 
      * @method parseHexStr2Byte 
      * @param hexStr 
      * @return 
      * @throws  
      * @since v1.0 
      */  
     public static byte[] parseHexStr2Byte(String hexStr){  
         if(hexStr.length() < 1)  
             return null;  
         byte[] result = new byte[hexStr.length()/2];  
         for (int i = 0;i< hexStr.length()/2; i++) {  
             int high = Integer.parseInt(hexStr.substring(i*2, i*2+1), 16);  
             int low = Integer.parseInt(hexStr.substring(i*2+1, i*2+2), 16);  
             result[i] = (byte) (high * 16 + low);  
         }  
         return result;  
     }  
     /**
      * 提供给h5获取芝麻分
      * 
      * 
      * */
     @RequestMapping(value = "/getScore", consumes = "application/json")
     @ResponseBody
     public String getScore(@RequestBody String param){
    	 String score="";
    	 JSONObject params = JSONObject.fromObject(param);
    	 String loanNo = String.valueOf(params.get("loanNo"));
    	 log.info("借款编号："+loanNo);
		 ApplyLoanInfo info = new ApplyLoanInfo();
		 info.setLoanNo(loanNo);
		 ApplyLoanInfo a = applyLoanInfoService.selectOne(info);
		 if(a!=null){
			score = a.getZmScore();
		 }
		 log.info("芝麻分："+score);
    	 return score;
     }
}

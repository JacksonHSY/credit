package com.ymkj.credit.service;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.utils.StrUtils;

import lombok.extern.log4j.Log4j;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.ValidateCode;
import com.ymkj.credit.common.untils.NumberUtil;
import com.ymkj.credit.common.untils.PropertiesReader;
import com.ymkj.credit.mapper.ValidateCodeMapper;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_002004;
import com.ymkj.credit.web.api.model.base.Model_002005;

import net.sf.json.JSONObject;

/**
 * @Description：对类进行描述
 * @ClassName: ValidateCodeService.java
 * @Author：tianx
 * @Date：2017年8月24日
 * -----------------变更历史-----------------
 * 如：who  2017年8月24日  修改xx功能
 */
@Log4j
@Service
public class ValidateCodeService {
	
	@Autowired
	private CustomerService customerService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private ValidateCodeMapper validateCodeMapper;
	
	//验证码有效尝试次数
	@Value("${sms_try_time}")
	private Integer tryTime;
	
	//验证码有效尝试时间
	@Value("${sms_valid_try_seconds}")
	private Integer validTrySeconds;
	
	//验证码有效时间
	@Value("${sms_valid_seconds}")
	private Integer smsValidSeconds;
	
	//验证码发送间隔时间
	@Value("${sms_send_seconds}")
	private Integer sms_send_seconds;
	
	/**
	 * 功能描述：发送短信验证码
	 * 输入参数：
	 * @param model
	 * @return
	 * 返回类型：Result
	 * 创建人：tianx
	 * 日期：2017年8月24日
	 */
	public Result sendSmsCode(Model_002004 model){
		Result result = Result.success();
		Customer record=customerService.findByPhone(model.getPhone());
		if(null == record){
			if(Constants.SMS_TYPE_RESET.equals(model.getSmsType())||Constants.SMS_TYPE_LOGIN.equals(model.getSmsType())){
				result.setSuccess(Boolean.FALSE);
				result.setMessage("手机号不存在，请前去注册");
				return result;
			}
		}
		if(null != record && (Constants.SMS_TYPE_RESET.equals(model.getSmsType())||Constants.SMS_TYPE_LOGIN.equals(model.getSmsType()))){
		      if(StringUtils.isEmpty(record.getPassword())){//表示未注册
		        result.setSuccess(Boolean.FALSE);
		        result.setMessage("手机号不存在，请前去注册");
		        return result;
		      }
		    }
		if(null != record && Constants.SMS_TYPE_REGISTER.equals(model.getSmsType())){
			if(StrUtils.isEmpty(record.getPassword()) || Constants.FLOW_STATUS_NEW.equals(record.getFlowStatus())){//注册未设密 |设密但未按正常流程注册
				customerService.giveUpRegister(model.getPhone());
			}else{
				result.setSuccess(Boolean.FALSE);
				result.setMessage("手机号已存在");
				return result;
			}
		}
		ValidateCode code = findByPhone(model.getPhone(),model.getSmsType());
		if(null != code){
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(code.getUpdateTime());
			calendar.add(Calendar.SECOND, sms_send_seconds);
			Date lastTime = calendar.getTime();
			Date curTime = new Date();
			if(lastTime.after(curTime)){
				log.info("短信发送失败："+PropertiesReader.readAsString("validateCode.send.maxTime"));
				result.setSuccess(Boolean.FALSE);
				result.setMessage(PropertiesReader.readAsString("validateCode.send.maxTime"));
				return result;
			}
		}
		String msgBody = null;
		if(Constants.SMS_TYPE_REGISTER.equals(model.getSmsType())){
			msgBody = PropertiesReader.readAsString("validateCode.send.register");
		}else{
		   msgBody = PropertiesReader.readAsString("validateCode.send.login");
		}
		String smsCode = NumberUtil.generatorSmsCode();
		String resultStr = smsService.sendSms(model.getPhone(), MessageFormat.format(msgBody, smsCode));
		if(StringUtils.isBlank(resultStr)){
			log.error("========>>>发送短信异常，统一通信响应信息为空");
			throw new BusinessException(BussErrorCode.ERROR_CODE_0102.getErrorcode(),BussErrorCode.ERROR_CODE_0102.getErrordesc());
		}
		JSONObject json = JSONObject.fromObject(resultStr);
		if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
			log.info("短信发送失败："+json.get("ucMessage").toString());
			result.setSuccess(Boolean.FALSE);
			result.setMessage(json.get("ucMessage").toString());
			return result;
		}
		if(null == code){
			ValidateCode newRecord = new ValidateCode();
			newRecord.setPhone(model.getPhone());
			newRecord.setSmsType(model.getSmsType());
			newRecord.setSmsCode(smsCode);
			newRecord.setStatus(Constants.DATA_VALID);
			newRecord.setTryTime(0);
			newRecord.setCreateTime(new Date());
			newRecord.setUpdateTime(new Date());
			validateCodeMapper.insertTable(newRecord);
		}else{
			code.setUpdateTime(new Date());
			code.setSmsCode(smsCode);
			code.setTryTime(0);
			validateCodeMapper.updateByPrimaryKeyTable(code);
		}
		return result;
	}
	
	/**
	 * 功能描述：校验短信验证码
	 * 输入参数：
	 * @param model
	 * @return
	 * 返回类型：Result
	 * 创建人：tianx
	 * 日期：2017年8月24日
	 */
	public Result validateSmsCode(Model_002005 model){
		Result result = Result.success();
		Customer record=customerService.findByPhone(model.getPhone());
		if(null == record && Constants.SMS_TYPE_RESET.equals(model.getSmsType())){
			result.setSuccess(Boolean.FALSE);
			result.setMessage("手机号不存在");
			return result;
		}
		if(null != record && Constants.SMS_TYPE_REGISTER.equals(model.getSmsType())){
			result.setSuccess(Boolean.FALSE);
			result.setMessage("手机号已存在");
			return result;
		}

		ValidateCode code = findByPhone(model.getPhone(),model.getSmsType());
		if(null == code){
			result.setSuccess(Boolean.FALSE);
			result.setMessage(PropertiesReader.readAsString("validateCode.not.get"));
			return result;
		}
		//验证码校验
		if(!StringUtils.equals(model.getSmsCode(), code.getSmsCode())){
			code.setTryTime(code.getTryTime()+1);
			validateCodeMapper.updateByPrimaryKeyTable(code);
			result.setSuccess(Boolean.FALSE);
			result.setMessage(PropertiesReader.readAsString("validateCode.error"));
			return result;
		}
		//失效时间
		Calendar calendar = Calendar.getInstance();    
	    calendar.setTime(code.getUpdateTime());    
	    calendar.add(Calendar.SECOND, smsValidSeconds);    
		Date lastTime = calendar.getTime();
		Date curTime = new Date();
		if(lastTime.before(curTime)){
			code.setTryTime(code.getTryTime()+1);
			validateCodeMapper.updateByPrimaryKeyTable(code);
			result.setSuccess(Boolean.FALSE);
			result.setMessage(PropertiesReader.readAsString("validateCode.invalid"));
			return result;
		}
		//次数校验
		Calendar tryCal = Calendar.getInstance();    
		tryCal.setTime(code.getUpdateTime()); 
		calendar.add(Calendar.SECOND, validTrySeconds);
		Date tryLastTime = calendar.getTime();
		if(tryLastTime.after(curTime) && code.getTryTime() > tryTime){
			code.setTryTime(code.getTryTime()+1);
			validateCodeMapper.updateByPrimaryKeyTable(code);
			result.setSuccess(Boolean.FALSE);
			result.setMessage(MessageFormat.format(PropertiesReader.readAsString("validateCode.input.maxLimit"), tryTime));
			return result;
		}
		
		code.setUpdateTime(new Date());
		code.setTryTime(0);
		validateCodeMapper.updateByPrimaryKeyTable(code);
		Customer customer = customerService.findByPhone(model.getPhone());
		if(null != customer){
			customer.setUpdateTime(new Date());
			customerService.updateByPhone(customer);
		}else{
			customerService.insertByPhone(model.getPhone());
		}

		return result;
	}
	
	/**
	 * 功能描述：根据手机号和验证码类型查询短信验证码
	 * 输入参数：
	 * @param phone
	 * @param codeType
	 * @return
	 * 返回类型：ValidateCode
	 * 创建人：tianx
	 * 日期：2017年8月24日
	 */
	public ValidateCode findByPhone(String phone,String codeType){
		ValidateCode query = new ValidateCode();
		query.setPhone(phone);
		query.setSmsType(codeType);
		query.setStatus(Constants.DATA_VALID);
		return validateCodeMapper.selectOneTable(query);
	}
	
}
package com.ymkj.credit.web.controller.customer;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.common.untils.PropertiesReader;
import com.ymkj.credit.common.vo.ResultVo;
import com.ymkj.credit.service.CustomerService;
import com.ymkj.credit.service.OperateLogService;
import com.ymkj.credit.service.SmsService;
import com.ymkj.credit.web.api.anno.PageSolver;
import com.ymkj.springside.modules.orm.PageInfo;
import com.ymkj.sso.client.ShiroUtils;

import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

/**
 * 账户管理
 * @author YM10156
 *
 */
@Controller
@RequestMapping("/manage")
@Slf4j
public class CustomerController {
	
	@Autowired
	CustomerService customerService;
	@Autowired
	private SmsService smsService;
	@Autowired
	private OperateLogService operateLogService;

	@RequestMapping("/customerList")
	public String index(Model model,HttpSession httpSession){
		return "/customer/customerlist";
	}
	/**
	 * 分页查询
	 * @param page
	 * @param rows
	 * @param sort
	 * @param order
	 * @param customer
	 * @param request
	 * @return
	 */
	@RequestMapping("/customer/customerPage")
	@ResponseBody
	public ResultVo customerPage(@PageSolver PageInfo<Customer> page,Customer customer){
		return ResultVo.returnPage(customerService.selectPageByColumn(page,customer));
	}
	/**
	 * 新建账户
	 * @param customer
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/customer/addCustomer")
	@ResponseBody
	public ResultVo addCustomer(Customer customer,HttpServletRequest request) throws Exception{
		try {
			Customer cust = customerService.findByPhone(customer.getPhone());
			if(null != cust){
				return ResultVo.returnMsg(false,"手机号不能重复！");
			}
			Customer cst = customerService.queryByIdCard(customer.getIdCard());
			if(null != cst){
				return ResultVo.returnMsg(false,"身份证号不能重复！");
			}
			String password = String.valueOf((int)((Math.random()*9+1)*100000));
			String msgBody = PropertiesReader.readAsString("validateCode.customer.send.register.xj");
			//String smsCode = NumberUtil.generatorSmsCode();
			if(!StringUtils.isEmpty(customer.getPhone())){
				String resultStr = smsService.sendSms(customer.getPhone(),MessageFormat.format(msgBody, password));
				if(StringUtils.isBlank(resultStr)){
					log.error("========>>>发送短信异常，统一通信响应信息为空");
					return  ResultVo.returnMsg(false, "发送短信异常，统一通信响应信息为空");
				}
				JSONObject json = JSONObject.fromObject(resultStr);
				if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
					log.info("短信发送失败："+json.get("ucMessage").toString());
					return  ResultVo.returnMsg(false, "短信发送失败！");
				}
				//记录日志
				operateLogService.saveLog(ShiroUtils.getCurrentUser().getName(), "login", "新建账户密码："+password);
				//发送成功更新密码
				customer.setPassword(MD5.MD5Encode(MD5.MD5Encode(password)));
				customerService.insertCustomer(customer);
				return  ResultVo.returnMsg(true, "发送成功");
			}else{
				return ResultVo.returnMsg(false,"手机号不存在！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVo.returnMsg(false, "新建失败！");
	}
	
	/**
	 * 账户注销
	 * @param customer
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/customer/updateCancel")
	@ResponseBody
	public ResultVo updateCancel(Customer customer,HttpServletRequest request) throws Exception{
		try {
			Customer cust = customerService.findCustomerMes(customer.getId());
			if(cust != null){
				if(cust.getIdCard().equals(customer.getIdCard())){
					cust.setStatus(Constants.DATA_UNVALID);
					customerService.updateByPrimaryKeySelective(cust);
					//记录日志
					operateLogService.saveLog(ShiroUtils.getCurrentUser().getName(), "login", "身份证为："+cust.getIdCard()+"的账户被注销");
					return  ResultVo.returnMsg(true, "注销成功");
				}else{
					return ResultVo.returnMsg(false,"身份证输入错误，请重新输入！");
				}
			}else{
				return ResultVo.returnMsg(false,"该条数据不存在，请重新选择！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVo.returnMsg(false, "任务处理失败！");
	}
	
	/**
	 * 重置密码
	 * @param customer
	 * @param request
	 * @return
	 * @throws Exception
	 */
	@RequestMapping("/customer/updateReset")
	@ResponseBody
	public ResultVo updateReset(Customer customer,HttpServletRequest request) throws Exception{
		try {
			Customer cust = customerService.findCustomerMes(customer.getId());
			if(cust != null){
				if(cust.getIdCard().equals(customer.getIdCard()) || !cust.getFlowStatus().equals(Constants.FLOW_STATUS_IDCARD)){
					String password = String.valueOf((int)((Math.random()*9+1)*100000));
					String msgBody = PropertiesReader.readAsString("validateCode.customer.send.register");
					//String smsCode = NumberUtil.generatorSmsCode();
					if(!StringUtils.isEmpty(cust.getPhone())){
						String resultStr = smsService.sendSms(cust.getPhone(),MessageFormat.format(msgBody, password));
						if(StringUtils.isBlank(resultStr)){
							log.error("========>>>发送短信异常，统一通信响应信息为空");
							return  ResultVo.returnMsg(false, "发送短信异常，统一通信响应信息为空");
						}
						JSONObject json = JSONObject.fromObject(resultStr);
						if(!(json.containsKey("ucCode")&&String.valueOf(json.get("ucCode")).equals(Constants.SMS_RESULT_SUCCESS_CODE))){
							log.info("短信发送失败："+json.get("ucMessage").toString());
							return  ResultVo.returnMsg(false, "短信发送失败！");
						}
						//记录日志
						operateLogService.saveLog(ShiroUtils.getCurrentUser().getName(), "login", "修改密码为："+password);
						//发送成功更新密码
						cust.setPassword(MD5.MD5Encode(MD5.MD5Encode(password)));
						customerService.updateByPrimaryKeySelective(cust);
						return  ResultVo.returnMsg(true, "发送成功");
					}else{
						return ResultVo.returnMsg(false,"手机号不存在！");
					}
				}else{
					return ResultVo.returnMsg(false,"身份证输入错误，请重新输入！");
				}
			}else{
				return ResultVo.returnMsg(false,"该条数据不存在，请重新选择！");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ResultVo.returnMsg(false, "任务处理失败！");
	}
}

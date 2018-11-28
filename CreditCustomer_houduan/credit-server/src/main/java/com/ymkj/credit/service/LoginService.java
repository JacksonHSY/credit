package com.ymkj.credit.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import lombok.extern.log4j.Log4j;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.BannerList;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.entity.Dictionary;
import com.ymkj.credit.common.entity.EquipmentInfo;
import com.ymkj.credit.common.entity.LoginLog;
import com.ymkj.credit.common.ex.BussErrorCode;
import com.ymkj.credit.common.ex.JsonException;
import com.ymkj.credit.common.untils.CacherContainer;
import com.ymkj.credit.common.untils.DateUtil;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.mapper.DictionaryMapper;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.network.Param;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.web.api.dto.CustomerDto;
import com.ymkj.credit.web.api.dto.CustomerInfoDto;
import com.ymkj.credit.web.api.dto.ImageDto;
import com.ymkj.credit.web.api.dto.InitDto;
import com.ymkj.credit.web.api.dto.UploadBannerRecord;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_001001;
import com.ymkj.credit.web.api.model.base.Model_002001;
import com.ymkj.credit.web.api.model.base.Model_002002;
import com.ymkj.credit.web.api.model.base.Model_002003;
import com.ymkj.credit.web.api.model.base.Model_002005;
import com.ymkj.credit.web.api.model.base.Model_002018;
import com.ymkj.credit.web.api.model.base.Model_004001;
import com.ymkj.credit.web.api.model.base.Model_004002;
import com.ymkj.credit.web.api.model.base.Model_004003;
import com.ymkj.credit.web.api.model.base.Model_004005;
import com.ymkj.credit.web.api.model.base.Model_004007;
import com.ymkj.credit.web.api.model.base.Model_004008;
import com.ymkj.credit.web.api.model.base.Model_004026;
import com.ymkj.springside.modules.utils.StrUtils;


/**
 * 登录功能
 * @author liangj@yuminsoft.com
 *
 */
@Log4j
@Service
public class LoginService {
	
//	@Autowired
//	private  ServerAdaptor serverAdaptor;
	@Inject
	private BasicRedisOpts basicRedisOpts;
	@Autowired
	private CustomerService customerService;
	@Autowired
	private DictionaryService dictionaryService;
	@Autowired
	private DictionaryMapper dictionaryMapoper;
	@Autowired
	private  ZyService zyService;
	@Autowired
	private ValidateCodeService validateCodeService;
	
	@Autowired
	private UploadPicture uploadpicture;
	
	@Autowired
	private EquipmentInfoService equipmentinfoservice;
	
	@Autowired
	private LoginLogService loginlogservice;
	//账号自动登录的期限
	@Value("${automatic_logon_days}")
	private Integer automatic_logon_days;
	 /**
     * 客服电话
     */
    @Value("${service_tel_number}")
    private String serviceTelNumber;
    /**
     * 借新还旧h5地址
     */
    @Value("${jxhjH5Url}")
    private String jxhjH5Url;
    
    
    @Value("${show_product_name}")
    private String showProductName;
    
    @Value("${show_product_code}")
    private String showProductCode;
    
    @Value("${applyTerm}")
    private String applyTerm;
    
    @Value("${isLivenessOpen}")
    private String isLivenessOpen;
    
    @Value("${jumpLocalPic}")
	private String jumpLocalPic;
    
	/**
	 * 功能描述：APP初始化
	 * 输入参数：
	 * @param model
	 * @param deviceNum
	 * @return
	 * 返回类型：Result
	 * 创建人：tianx
	 * 日期：2017年8月28日
	 */
	@SuppressWarnings("unchecked")
	public Result init(Model_001001 model,String deviceNum,String platform,String version){
		//String customerId = basicRedisOpts.getSingleResult(deviceNum);
		String customerId = null;
		Map<String,Object> city = dictionaryService.getProvicesList();
		InitDto initDto = new InitDto();
		if(StringUtils.isNotBlank(customerId)){
			Customer customer = customerService.findCustomerMes(Long.parseLong(customerId));
			if(null != customer){
				CustomerDto customerDto = new CustomerDto();
				customerDto.setId(customer.getId());
				customerDto.setPhone(customer.getPhone());
				customerDto.setCustomerName(customer.getCustomerName());
				if(StrUtils.isNotEmpty(customer.getIdCard())){
					customerDto.setIdCard(customer.getIdCard().substring(0,4)+"********"+customer.getIdCard().substring(customer.getIdCard().length()-4,customer.getIdCard().length()));
					customerDto.setIdCardName(customer.getIdCard());
				}
				if("001002".equals(customer.getFlowStatus())){
					customerDto.setIsIdentityAuthentication(Constants.DATA_VALID);
				}else{
					customerDto.setIsIdentityAuthentication(Constants.DATA_UNVALID);	
				}
				customerDto.setGestureSwitch(customer.getGestureSwitch());
				if(Constants.AGREED.equals(customer.getMemo())){
					customerDto.setIsAgreement("true");
				}else{
					customerDto.setIsAgreement("false");
				}
				initDto.setCustomerDto(customerDto);
			}
		}
		initDto.setCity(city);
		String result = zyService.getBaseInfo();
		log.info(result);//limitListData 借款申请期限, OccupationType 职业 productListData 产品  CreditApplication 资金用途
		String resultString = zyService.getBaseInfo();
		if(StringUtils.isNotBlank(resultString)){
			com.alibaba.fastjson.JSONObject resultJson = com.alibaba.fastjson.JSONObject.parseObject(resultString);
			if(null != resultJson && resultJson.get("code").equals("0000")){
				resultString = com.alibaba.fastjson.JSONObject.toJSONString(resultJson.get("msgEx"));
				com.alibaba.fastjson.JSONObject object = com.alibaba.fastjson.JSONObject.parseObject(resultString);
				if(null != object){
					resultString = com.alibaba.fastjson.JSONObject.toJSONString(object.get("infos"));
					object = com.alibaba.fastjson.JSONObject.parseObject(resultString);
					if(null != object){
						resultString = com.alibaba.fastjson.JSONObject.toJSONString(object.get("listDate"));
						object = com.alibaba.fastjson.JSONObject.parseObject(resultString);
						if(null != object){
							JSONArray purposeList = JSONArray.fromObject(object.get("creditApplication"));
							initDto.setPurposeList(purposeList);//资金用途
//							JSONArray limitListData = JSONArray.fromObject(object.get("limitListData"));
//							initDto.setDeadlineList (limitListData);// 借款申请期限
							JSONArray professionList = JSONArray.fromObject(object.get("occupationType"));
							initDto.setProfessionList(professionList);// 职业
							JSONArray productListData = JSONArray.fromObject(object.get("productListData"));
							initDto.setProductListData(productListData);// 产品
//							JSONArray channelListData = JSONArray.fromObject(object.get("channelListData"));
//							initDto.setChannelListData(channelListData);// 渠道
						}
					}
				}
			}
		}
		List<Dictionary> propertyList = CacherContainer.sysSysParameterMap.get("capital");//资产列表
		List<Dictionary> limitListData = CacherContainer.sysSysParameterMap.get("applyTerm");//借款期限
		JSONArray jsonArray = new JSONArray();   
		for(Dictionary dic:limitListData){
			jsonArray.add(dic.getMemo());
		}
		initDto.setDeadlineList(jsonArray);
		initDto.setPropertyList(propertyList);
		
		initDto.setIsAuditOver("0");
		if("ios".equals(platform)){
			initDto.setIsAuditOver(checkAppVersion(version));
		}
		initDto.setNextExpiry(getNextExpiry());
		initDto.setShowProductName(showProductName);
		initDto.setShowProductCode(showProductCode);
		initDto.setApplyTerm(applyTerm);
		return Result.success(initDto);
	}
	
	public static String checkAppVersion(String appVersion) {
	     String vers[] = appVersion.split("\\.");//入参
		for (Dictionary param : CacherContainer.sysSysParameterMap.get("appVersion")) {
			if ("ios".equals(param.getDataName())) {
				String prValue = param.getDataValue();
				String dbvs[] = prValue.split("\\.");//db的值
				if (Integer.parseInt(dbvs[0]) > Integer.parseInt(vers[0])) {//dbv>pv = 1
					return "1";
				} else if (Integer.parseInt(dbvs[0]) == Integer.parseInt(vers[0])) {//
					if (Integer.parseInt(dbvs[1]) > Integer.parseInt(vers[1])) {
						return "1";
					} else if (Integer.parseInt(dbvs[1]) == Integer.parseInt(vers[1])) {
						if(Integer.parseInt(dbvs[2]) < Integer.parseInt(vers[2])) {
							return "0";
						} else {
							return "1";
						}
					} else {
						return "0";
					}
				} else {
					return "0";
				}
			}
		}
		return "0";
	}
	
	/**
	 * 登录
	 * @param model
	 * @return
	 * Result
	 * @author liangj@yuminsoft.com
	 * @date  2017年8月25日
	 */
	public Result login(Model_002001 model,String deviceNum,String platform){
		String loginType = model.getLoginType();
		String identityNo = model.getIdentityNo();
		//根据入参的账号,业务状态和数据有效性查询客户是否存在
		//根据入参的账号,业务状态和数据有效性查询客户是否存在
		List<Customer> list = null;
		Example example = new Example(Customer.class);
		if(identityNo.trim().length()>11){
			/*Criteria criteria = example.createCriteria();
			criteria.andEqualTo("idCard", identityNo);
			criteria.andEqualTo("status", Constants.DATA_VALID);
			criteria.andEqualTo("flowStatus", Constants.FLOW_STATUS_IDCARD);*/
			Customer cus = new Customer();
			cus.setIdCard(identityNo);
			cus.setStatus(Constants.DATA_VALID);
			cus.setFlowStatus(Constants.FLOW_STATUS_IDCARD);
			list = customerService.selectByExample(cus);
		}else{
			/*Criteria criteria = example.createCriteria();
			criteria.andEqualTo("phone", identityNo);
			criteria.andEqualTo("status", Constants.DATA_VALID);
			criteria.andNotEqualTo("flowStatus", Constants.FLOW_STATUS_NEW);*/
			Customer cus = new Customer();
			cus.setPhone(identityNo);
			cus.setStatus(Constants.DATA_VALID);
			cus.setFlowStatus(Constants.FLOW_STATUS_NEW);
			list = customerService.selectByExampleByPhone(cus);
		}
		//List<Customer> list = customerService.selectByExample(example);
		if(null==list||list.isEmpty()){
			return Result.fail("账号不存在,请重新注册");
		}
		if(list.size()>1){
			return Result.fail("此账号对应了多个用户");
		}
		Customer customer  = list.get(0);
		if(Constants.LOGIN_TYPE_SMS.equals(loginType)){//短信登录
			Model_002005 smsModel = new Model_002005();
			smsModel.setPhone(identityNo);
			smsModel.setSmsCode(model.getValidateCode());
			smsModel.setSmsType(loginType);
			Result res = validateCodeService.validateSmsCode(smsModel);
			if(!res.getSuccess()){
				return res;
			}
		}else{
			String password = MD5.MD5Encode(model.getPassword());
			if(Constants.LOGIN_TYPE_COMMON.equals(loginType)){
				if(!password.equals(customer.getPassword())){
					return Result.fail("密码错误");
				}	
			}else if(Constants.LOGIN_TYPE_GESTURE.equals(loginType)){
				if(Constants.GESTURE_SWITCH_OFF.equals(customer.getGestureSwitch())){
					return Result.fail("手势密码开关处于关闭状态");
				}
				if (!password.equals(customer.getGesturePassword())) {
					return Result.fail("手势密码错误");
				}	
			}else{
				return Result.fail("登录类型错误");
			}
		}
		/**登入成功记录设备信息start*/
		if(identityNo.trim().length()>11){
			//身份证登入
			if(basicRedisOpts.exists(deviceNum+"_idcard")){//存在
				String account = basicRedisOpts.getSingleResult(deviceNum+"_idcard");
				if(account.equals(identityNo)){
					//不记录
				}else{
					basicRedisOpts.persist(deviceNum+"_idcard", identityNo);
					//保存
					EquipmentInfo equip = new EquipmentInfo();
					equip.setAccount(identityNo);
					equip.setDeviceId(deviceNum);
					Date date = new Date();
					equip.setLoginDate(date);
					equipmentinfoservice.insert(equip);
				}
				
			}else{//不存在
				basicRedisOpts.persist(deviceNum+"_idcard", identityNo);
				//保存
				EquipmentInfo equip = new EquipmentInfo();
				equip.setAccount(identityNo);
				equip.setDeviceId(deviceNum);
				Date date = new Date();
				equip.setLoginDate(date);
				equipmentinfoservice.insert(equip);
			}
		}else{
			//手机号登入
			if(basicRedisOpts.exists(deviceNum+"_phone")){//存在
				String account = basicRedisOpts.getSingleResult(deviceNum+"_phone");
				if(account.equals(identityNo)){
					//不记录
				}else{
					basicRedisOpts.persist(deviceNum+"_phone", identityNo);
					//保存
					EquipmentInfo equip = new EquipmentInfo();
					equip.setAccount(identityNo);
					equip.setDeviceId(deviceNum);
					Date date = new Date();
					equip.setLoginDate(date);
					equipmentinfoservice.insert(equip);
				}
				
			}else{//不存在
				basicRedisOpts.persist(deviceNum+"_phone", identityNo);
				//保存
				EquipmentInfo equip = new EquipmentInfo();
				equip.setAccount(identityNo);
				equip.setDeviceId(deviceNum);
				Date date = new Date();
				equip.setLoginDate(date);
				equipmentinfoservice.insert(equip);
			}
		}
		/**登入成功记录设备信息end*/
		
		String pushId = model.getPushId();
		if(!ArrayUtils.contains(Constants.PLATFORM_TYPE, platform.toUpperCase())){
			return Result.fail("平台类型错误");
		}
		//如果数据库里的设备号不为空，清空redis之前的这个设备的登录数据
		if(StringUtils.isNotBlank(customer.getDeviceNum())){
			basicRedisOpts.remove(customer.getDeviceNum());
		}			
		customer.setDeviceNum(deviceNum);		
		if(StringUtils.isNotBlank(pushId)){
			customer.setPushId(pushId);
		}
		customer.setPlatform(platform);
		customerService.updateByPrimaryKeySelective(customer);
		
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DATE, automatic_logon_days);
		log.info("redie设置登入过期时间："+now.getTime());
		basicRedisOpts.persist(deviceNum, customer.getId().toString(),now.getTime());
		//返回客户的部分信息
		CustomerDto customerDto = new CustomerDto();
		customerDto.setId(customer.getId());
		customerDto.setPhone(customer.getPhone());
		customerDto.setCustomerName(customer.getCustomerName());
		if(StrUtils.isNotEmpty(customer.getIdCard())){
			customerDto.setIdCard(customer.getIdCard().substring(0,4)+"********"+customer.getIdCard().substring(customer.getIdCard().length()-4,customer.getIdCard().length()));
			customerDto.setIdCardName(customer.getIdCard());
		}else{
			customerDto.setIdCard(customer.getIdCard());
		}
		if("001002".equals(customer.getFlowStatus())){
			customerDto.setIsIdentityAuthentication(Constants.DATA_VALID);
		}else{
			customerDto.setIsIdentityAuthentication(Constants.DATA_UNVALID);	
		}
		customerDto.setGestureSwitch(customer.getGestureSwitch());
		customerDto.setNextExpiry(getNextExpiry());
		customerDto.setShowProductName(showProductName);
		customerDto.setShowProductCode(showProductCode);
		customerDto.setApplyTerm(applyTerm);
		
		if(Constants.AGREED.equals(customer.getMemo())){
			customerDto.setIsAgreement("true");
		}else{
			customerDto.setIsAgreement("false");
		}
		//设置是否人脸识别开关
		customerDto.setIsLivenessOpen(isLivenessOpen);
		//记录登入用户的信息，登入时间，设备号，设备类型
		LoginLog log = new LoginLog();
		log.setPhone(customer.getPhone());
		LoginLog lis = loginlogservice.selectCustomer(log);
		if(lis!=null){
			lis.setCustomerName(customer.getCustomerName());
			lis.setLoginDate(new Date());
			lis.setDeviceId(deviceNum);
			lis.setType(platform);
			loginlogservice.update(lis);
		}else{
			log.setCustomerName(customer.getCustomerName());
			log.setDeviceId(deviceNum);
			log.setType(platform);
			log.setLoginDate(new Date());
			loginlogservice.insert(log);
		}
		return Result.success(customerDto);
	}
	
	/**
	 * 登录密码校验
	 * @param model
	 * @return
	 * Result
	 * @author liangj@yuminsoft.com
	 * @date  2017年9月5日
	 */
	public Result validateLogin(Model_002002 model) {
		Long customerId = model.getCustomerId();
		String password = MD5.MD5Encode(model.getPassword());
		
		Customer customer = customerService.findCustomerMes(customerId);
		if(null==customer){
			return Result.fail("用户不存在");
		}
		if(!password.equals(customer.getPassword())){
			return Result.fail("登录密码错误");
		}
		
		return Result.success();
	}

	/**
	 * 忘记密码
	 * @param model
	 * @param deviceNum
	 * @return
	 * Result
	 * @author liangj@yuminsoft.com
	 * @date  2017年8月28日
	 */
	public Result forgotPassword(Model_002003 model, String deviceNum) {
		String phone = model.getPhone();
		String password = MD5.MD5Encode(model.getPassword());
//		String password = model.getPassword();//dc483e80a7a0bd9ef71d8cf973673924
		//根据手机号,业务状态和数据有效性查询客户是否存在
		Customer customer = customerService.findByPhone(phone);
		if(null==customer){
			return Result.fail("该手机号不属于任何用户");
		}
		
		//手机号手机号存在清空redis里的数据,并修改密码
		if(StringUtils.isNotBlank(customer.getDeviceNum())){
			basicRedisOpts.remove(customer.getDeviceNum());//清空redis之前的这个用户登录数据
		}
		
		customer.setPassword(password);
		customer.setUpdateTime(new Date());
		customerService.updateByPrimaryKeySelective(customer);
		return Result.success();
	}

	/**
	 * 个人信息查询
	 * @param model
	 * @param deviceNum
	 * @return
	 * Result
	 * @author liangj@yuminsoft.com
	 * @date  2017年8月29日
	 */
	public Result queryPersonInfo(Model_004001 model) {
		Long customerId = model.getCustomerId();
		Customer customer = customerService.findCustomerMes(customerId);
		if(null==customer){
			return Result.fail("用户不存在");
		}	
		//调核心的接口
		//查询借款客户信息接口
//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("idNum", customer.getIdCard());
//		jsonObject.put("userName", customer.getCustomerName());
//		JSONObject attchment = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_CUSTOMERINFO, jsonObject.toString());
//
//		if (!attchment.containsKey("customer")) {
//			return Result.fail("接口响应信息异常");
//		}	
		//查询客户经理信息
//		JSONObject jsonObject1 = new JSONObject();
//		jsonObject1.put("userCode", customer.getAccountManagerNo());
//		JSONObject attchment1 = HttpClientUtil.sendHttpPostForCredit(Constants.CREDIT_URL_EMPLOYEEINFO, jsonObject1.toString());
//
//		if (!attchment1.containsKey("userCode")) {
//	           return Result.fail("客户经理不存在");
//	       }
//		
//		EmployeeBo employeeBo = (EmployeeBo) JSONObject.toBean(attchment1, EmployeeBo.class);
//		DepartmentBo departBo = (DepartmentBo) JSONObject.toBean(JSONObject.fromObject(attchment.getString("department")), DepartmentBo.class);
		CustomerInfoDto customerInfoDto = new CustomerInfoDto();
		customerInfoDto.setId(customer.getId());
		customerInfoDto.setPhone(customer.getPhone());
		customerInfoDto.setCustomerName(customer.getCustomerName());
		if(StrUtils.isNotEmpty(customer.getIdCard())){
			customerInfoDto.setIdCard(customer.getIdCard().substring(0,4)+"********"+customer.getIdCard().substring(customer.getIdCard().length()-4,customer.getIdCard().length()));
			customerInfoDto.setIdCardName(customer.getIdCard());
		}else{
			customerInfoDto.setIdCard(customer.getIdCard());
			customerInfoDto.setIdCardName(customer.getIdCard());
		}
	//	customerInfoDto.setIdCard(customer.getIdCard().substring(0,4)+"********"+customer.getIdCard().substring(customer.getIdCard().length()-4,customer.getIdCard().length()));
		customerInfoDto.setGestureSwitch(customer.getGestureSwitch());
//		customerInfoDto.setAccountManagerNo(employeeBo.getUserCode());
//		customerInfoDto.setAccountManagerName(employeeBo.getEmpName());
//		customerInfoDto.setAccountManagerphone(employeeBo.getMobile());
//		customerInfoDto.setSalesOffice(departBo.getDeptName());
//		customerInfoDto.setOfficePhone(departBo.getTelephone());
//		customerInfoDto.setAddress(departBo.getSite());
		customerInfoDto.setServiceTelNumber(serviceTelNumber);
		
		if("001002".equals(customer.getFlowStatus())){
		    customerInfoDto.setIsIdentityAuthentication(Constants.DATA_VALID);
		}else{
			customerInfoDto.setIsIdentityAuthentication(Constants.DATA_UNVALID);	
		}
		return Result.success(customerInfoDto);
	}

	/**
	 * 修改密码
	 * @param model
	 * @param deviceNum
	 * @return
	 * Result
	 * @author liangj@yuminsoft.com
	 * @date  2017年8月29日
	 */
	public Result modifyPassword(Model_004002 model) {
		Long customerId = model.getCustomerId();
		String oldPassword = MD5.MD5Encode(model.getOldPassword());
		String newPassword = MD5.MD5Encode(model.getNewPassword());
		
		Customer customer = customerService.findCustomerMes(customerId);
		if(null==customer){
			return Result.fail("用户不存在");
		}	
		if(!oldPassword.equals(customer.getPassword())){
			return Result.fail("原密码错误");
		}
		
		customer.setPassword(newPassword);
		//手机号手机号存在清空redis里的数据,并修改密码
		if(StringUtils.isNotBlank(customer.getDeviceNum())){
			basicRedisOpts.remove(customer.getDeviceNum());//清空redis之前的这个用户登录数据
		}
		customerService.updateByPrimaryKeySelective(customer);
		
		return Result.success();
	}

	/**
	 * 设置手势密码
	 * @param model
	 * @return
	 * Result
	 * @author liangj@yuminsoft.com
	 * @date  2017年8月29日
	 */
	public Result setGesturePassword(Model_004003 model) {
		Long customerId = model.getCustomerId();
		String password = MD5.MD5Encode(model.getLoginPassword());
		String gesturePassword = MD5.MD5Encode(model.getGesturePassword());
		
		Customer customer = customerService.findCustomerMes(customerId);
		if(null==customer){
			return Result.fail("用户不存在");
		}
		if(!password.equals(customer.getPassword())){
			return Result.fail("登录密码错误");
		}
		if(Constants.GESTURE_SWITCH_OFF.equals(customer.getGestureSwitch())){
			return Result.fail("手势密码处于关闭状态不能设置");
		}
		customer.setGesturePassword(gesturePassword);
		customer.setGestureSwitch(Constants.GESTURE_SWITCH_ON);//设置手势密码默认打开
		customerService.updateByPrimaryKeySelective(customer);
		return Result.success();
	}

	/**
	 * 登出
	 * @param deviceNum
	 * @return
	 * Result
	 * @author liangj@yuminsoft.com
	 * @date  2017年8月29日
	 */
	public Result logout(Model_004005 model,String deviceNum) {
		Long customerId = model.getCustomerId();
		Customer customer = customerService.findCustomerMes(customerId);
		if(null==customer){
			return Result.fail("用户不存在");
		}
		// 清空客户表中推送的唯一标识和redis之前的这个设备用户登录数据
		customer.setPushId("");
		customerService.updateByPrimaryKey(customer);
		basicRedisOpts.remove(deviceNum);
		return Result.success();
	}

	/**
	 * 手势密码开关
	 * @param model
	 * @return
	 * Result
	 * @author liangj@yuminsoft.com
	 * @date  2017年8月30日
	 */
	public Result changeGestureSwitch(Model_004007 model) {
		Long customerId = model.getCustomerId();
		String gestureSwitch = model.getGestureSwitch();
		
		Customer customer = customerService.findCustomerMes(customerId);
		if(null==customer){
			return Result.fail("用户不存在");
		}
		customer.setGestureSwitch(gestureSwitch);
		customerService.updateByPrimaryKeySelective(customer);
		return Result.success();
	}
	/**
	 * 首页轮播图
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月8日
	 */
	public Result queryPageInfor(Model_004008 model,String platform,String version) {
		String customerId = model.getId();
		Customer customer = customerService.findCustomerMes(Long.parseLong(customerId));
		if(null==customer){
			return Result.fail("用户不存在");
		}
		List<ImageDto> adImgUrlList = new ArrayList<ImageDto>();
		BannerList banner = new BannerList();
		List<BannerList> listpicture = uploadpicture.selectRecode(banner);
		for(BannerList u : listpicture){
			//区分是安卓还是ios
			if(platform.equals("ios")){
				//判断
				if(u.getUrl().startsWith("http")){
					//活动页
					ImageDto imageDto= new ImageDto();
					imageDto.setName(u.getCodekey());
					imageDto.setUrl(u.getPicture());
					imageDto.setIsActivityPage("1");
					imageDto.setJumpType(Constants.JUMP_TYPE_WEB);
					if(u.getUrl().endsWith(".jpg")||u.getUrl().endsWith(".png")||u.getUrl().endsWith(".bmp")){
						
						imageDto.setJumpUrl(jumpLocalPic+"imgurl="+u.getUrl());
					}else{
						imageDto.setJumpUrl(u.getUrl());
					}
					adImgUrlList.add(imageDto);
					
				}else if(u.getUrl().startsWith("TTBankCardVC")){
					//绑卡页
					ImageDto imageDto= new ImageDto();
					imageDto.setName(u.getCodekey());
					imageDto.setUrl(u.getPicture());
					imageDto.setIsActivityPage("0");
					imageDto.setJumpType(Constants.JUMP_TYPE_LOCAL);
					imageDto.setJumpUrl(u.getUrl());
					adImgUrlList.add(imageDto);
				}else if(u.getUrl().startsWith("BankCardAdminActivity")){
					//安卓绑卡页不显示
					
				}else{
					ImageDto imageDto= new ImageDto();
					imageDto.setName(u.getCodekey());
					imageDto.setUrl(u.getPicture());
					imageDto.setJumpUrl("");
					adImgUrlList.add(imageDto);
				}
				
			}else{
				//判断
				if(u.getUrl().startsWith("http")){
					//活动页
					ImageDto imageDto= new ImageDto();
					imageDto.setName(u.getCodekey());
					imageDto.setUrl(u.getPicture());
					imageDto.setIsActivityPage("1");
					imageDto.setJumpType(Constants.JUMP_TYPE_WEB);
					if(u.getUrl().endsWith(".jpg")||u.getUrl().endsWith(".png")||u.getUrl().endsWith(".bmp")){
						
						imageDto.setJumpUrl(jumpLocalPic+"imgurl="+u.getUrl());
					}else{
						imageDto.setJumpUrl(u.getUrl());
					}
					adImgUrlList.add(imageDto);
					
				}else if(u.getUrl().startsWith("TTBankCardVC")){
					//ios绑卡页不显示
					
				}else if(u.getUrl().startsWith("BankCardAdminActivity")){
					//安卓绑卡页
					ImageDto imageDto= new ImageDto();
					imageDto.setName(u.getCodekey());
					imageDto.setUrl(u.getPicture());
					imageDto.setIsActivityPage("0");
					imageDto.setJumpType(Constants.JUMP_TYPE_LOCAL);
					imageDto.setJumpUrl(u.getUrl());
					adImgUrlList.add(imageDto);
				}else{
					ImageDto imageDto= new ImageDto();
					imageDto.setName(u.getCodekey());
					imageDto.setUrl(u.getPicture());
					imageDto.setJumpUrl("");
					adImgUrlList.add(imageDto);
				}
			}
			
		}
		//查询登录信息
		String idCard = customer.getIdCard();
		String phone = customer.getPhone();
		List<Dictionary> adImgList = CacherContainer.sysSysParameterMap.get("AdImgUrl");
		//List<ImageDto> adImgUrlList = new ArrayList<ImageDto>();
		//获取图片
		
		/*for(Dictionary dictionary : adImgList) {
			ImageDto imageDto= new ImageDto();
			imageDto.setName(dictionary.getDataName());
			imageDto.setUrl(dictionary.getDataValue());
			imageDto.setIsActivityPage("0");//广告
			if(dictionary.getDataCode().startsWith("ac")){
				imageDto.setJumpUrl(jxhjH5Url+"/oldRepay.html?idCard="+idCard+"&phone="+phone+"&random="+Math.random()+"&platform="+platform+"&from=app");
				imageDto.setIsActivityPage("1");//活动
				if(!"1.4.0".equals(version)){
					continue;
				}
			}
			if(dictionary.getDataCode().startsWith("ad")){
				imageDto.setJumpType(Constants.JUMP_TYPE_LOCAL);
				if(Constants.PLATFORM_TYPE_IOS.equals(platform.toUpperCase())){
					imageDto.setJumpUrl("TTBankCardVC");
				}else{
					imageDto.setJumpUrl("BankCardAdminActivity");
				}
			}
			adImgUrlList.add(imageDto);
		}*/
		
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("adImgUrlList", adImgUrlList);
	    return Result.success(map);
	}
	
	public String queryOrderDetail(String taskCode){
		JSONObject model = new JSONObject();
		model.put("taskCode", taskCode);
		String params = Param.getParam("003003", model);
		String result = HttpClientUtil.sendHttpPost(params);
		return result;
	}
	
	/**
	 * 
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月16日
	 */
	public Result checkLogin4h5(Model_002018 model) {
		// 查询登录信息
		if (null == basicRedisOpts.getSingleResult("h5_"+model.getIdCard())) {
			Result.fail("会话已过期,请重新登录");
		}
		return Result.success();
	}
	/**
	 * 下一还款日
	 * @TODO
	 * @return
	 * String
	 * @author changj@yuminsoft.com
	 * @date2018年5月15日
	 */
	public  static String getNextExpiry(){
		Date currentDate = new Date();
		Date first = DateUtil.getDayOfMonth(currentDate, 1);//当月1号之前
		Date end = DateUtil.getDayOfMonth(currentDate, 16);//当月16号之前
		if(DateUtil.getIntervalDays(first, currentDate)>0){//如果在本月1号之前 下一还款日是本月1号
			return DateUtil.format(first, DateUtil.DATAFORMAT_YYYY_MM_DD);
		}else if(DateUtil.getIntervalDays(end, currentDate)>0){//如果在本月16号之前 下一还款日是本月1号
			return DateUtil.format(end, DateUtil.DATAFORMAT_YYYY_MM_DD);
		}else{//本月16号之后  下月1号
			return DateUtil.format(	DateUtil.getNextMonthFirstDay(currentDate), DateUtil.DATAFORMAT_YYYY_MM_DD);
		}
	}
	/**
	 * 更新客户同意注册协议
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年6月14日
	 */
	public Result updateAgreement(Model_004026 model) {
		Long customerId = Long.parseLong(model.getCustomerId());
		Customer customer = customerService.findCustomerMes(customerId);
		if(null==customer){
			return Result.fail("用户不存在");
		}
		customer.setMemo(Constants.AGREED);
		customerService.updateByPrimaryKeySelective(customer);
		return Result.success();
	}
}

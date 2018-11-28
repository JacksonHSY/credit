package com.ymkj.credit.service;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import net.sf.json.JSONObject;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.ApplyContractInfo;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.untils.HttpKit;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.common.util.DateUtil;
import com.ymkj.credit.mapper.ApplyContractInfoMapper;
import com.ymkj.credit.mapper.BankInfoMapper;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.service.Zs.ZsService;
import com.ymkj.credit.service.dms.DmsService;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_002020;
import com.ymkj.credit.web.api.model.base.Model_002021;
import com.ymkj.credit.web.api.model.base.Model_002022;
import com.ymkj.credit.web.api.model.base.Model_002023;
import com.ymkj.credit.web.api.model.base.Model_002024;
import com.ymkj.credit.web.api.model.base.Model_002025;
import com.ymkj.credit.web.api.model.base.Model_002029;
import com.ymkj.credit.web.api.model.base.Model_006004;
import com.ymkj.dms.api.vo.response.sign.ResBMSNameaAndIdAndphone;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.utils.StrUtils;



/**
* SignService
* <p/>
* Author: 
* Date: 2018-02-28 10:23:46
* Mail: 
*/
@Service
@Slf4j
public class SignService {
	
	    @Autowired
	    private DmsService dmsService;
	    
	    
	    @Autowired
	    private ZsService zService;
	    
	    @Autowired
		private LoanOrderService loanOrderService;
	    
	    @Autowired
	 	private CustomerService customerService;
	    
	    @Autowired
		private BankInfoMapper bankInfoMapper;
	    @Autowired
	    SignLcbService signLcbService;
	    @Autowired
	    ApplyContractInfoMapper applyMapper;
	    
	    @Value("${lcb.key}")
		private String creditReqKey;
	    @Value("${lcbwg.url}")
		private String lcbwgUrl;
	    @Value("${h5.url.backUrl}")
	    private String backUrl;
		/**
		 * H5征审获取合同签名结果
		 */
		@Autowired
		private BasicRedisOpts basicRedisOpts;
		
		@SuppressWarnings("static-access")
		public Result getSignContractStatus(Model_002020 model) {
	        log.info("===================》》》H5征审获取合同签名结果接口入参：loanNo="+model.getLoanNo());
	        Result result = null;
	        try {
	        	String contractType = basicRedisOpts.getSingleResult(model.getLoanNo());
	        	if(StrUtils.isNotBlank(contractType)){
	        		JSONObject jsonObject = new JSONObject();
	        		jsonObject.put("appNo", model.getLoanNo());
	        		jsonObject.put("sysCode", Constants.SYSCODE);
					jsonObject.put("infoCategory", Constants.INFOCATEGORY);
	        		String ret = "OK";
	        		if(Constants.contractType_dianzi.equals(contractType)){//电子合同需查询内部合同是否签订
	                	ret = zService.sendHttpPost4GetSignContractStatus(jsonObject.toString());
	                	jsonObject.put("status", ret);
	                	if(!"OK".equals(ret)){
	                		boolean flag = dmsService.getTargetStatusByDms(model.getLoanNo());
	    	        		if(!flag){
	    	        			jsonObject.put("lcbstatus", flag);//false  待签约
	    	        		}
	                	}
	        		}
	        		jsonObject.put("status", ret);
	        		jsonObject.put("urlLcb", HttpClientUtil.getLcbLogin4Pc());
//	        		else{
//	        			result = result.success(jsonObject);
//	        			result = result.fail("捞财宝合同未到待签约状态,请联系客服");
//	        		}
	        		result = result.success(jsonObject);
	        	}else{
	        	   result = Result.fail("系统异常,请联系客服");
	        	}
	        } catch (Exception e) {
	            e.printStackTrace();
	            if (e instanceof BusinessException) {
	                String message = e.getMessage();
	                result = Result.fail(message);
	                log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
	            } else {
	                result = Result.fail();
	                log.error("调用功能时系统异常: ", e);
	            }
	        }
	        log.info("*************************响应*************************");
	        //log.info(JSONObject.fromObject(result).toString());
	        return result;
	    }
		
		
		 /**
	     * 查询合同列表
	     * @TODO
	     * @param httpRequest
	     * @param httpResponse
	     * @param loanNo
	     * @return
	     * String
	     * @author changj@yuminsoft.com
	     * @date2018年3月14日
	     */
		@SuppressWarnings({ "static-access" })
		public Result findSignContractListInfos(Model_002021 model) {
			log.info("===================》》》H5征审查询合同列表接口入参：loanNo=" + model.getLoanNo());
			Result result = null;
			try {
				// 查询登录信息
				ResBMSNameaAndIdAndphone customer = null;
				String oldLoanNo = basicRedisOpts.getSingleResult("jxhj_"+model.getLoanNo());
				if (StrUtils.isNotBlank(oldLoanNo)) {//借旧换新活动
					log.info("借旧换新征审查询合同列表");
					customer = dmsService.getCustomerInfoByDms(oldLoanNo);
				}else{
					log.info("正常签约征审查询合同列表");
					customer = dmsService.getCustomerInfoByDms(model.getLoanNo());
				}
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("appNo", model.getLoanNo());
				jsonObject.put("sysCode", Constants.SYSCODE);
				jsonObject.put("infoCategory", Constants.INFOCATEGORY);
				// 先调登录注册
				zService.sendHttpPost4UserLoginToRegisterSignature(jsonObject.toString());
				String ret = zService.sendHttpPost4FindSignContractListInfos(jsonObject.toString());
				jsonObject.put("attachment", ret);// 合同列表
				jsonObject.put("idNum", customer.getIdNo());
				jsonObject.put("contractNum", model.getLoanNo());
				jsonObject.put("name", customer.getName());
				jsonObject.put("phone", customer.getCellPhoen());
				jsonObject.put("date", DateUtil.format(new Date(), DateUtil.CN_YYYY_MM_DD));
				result = result.success(jsonObject);
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof BusinessException) {
					String message = e.getMessage();
					result = Result.fail(message);
					log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
				} else {
					result = Result.fail();
					log.error("调用功能时系统异常: ", e);
				}
			}
			log.info("*************************响应*************************");
			log.info(JSONObject.fromObject(result).toString());
			return result;
		}
		
		/**
		 * 查询核心合同是否签完
		 * @param loanNo
		 * @return
		 */
		@SuppressWarnings({ "static-access" })
		public boolean queryHxContract(String loanNo){
			JSONObject jsonObject = new JSONObject();
    		jsonObject.put("appNo", loanNo);
    		jsonObject.put("sysCode", Constants.SYSCODE);
			jsonObject.put("infoCategory", Constants.INFOCATEGORY);
			String ret = zService.sendHttpPost4GetSignContractStatus(jsonObject.toString());
			if("OK".equals(ret)){
				return true;
			}
			return false;
		}
		
		/**
		 * 加载合同文件
		 * @TODO
		 * @param httpRequest
		 * @param httpResponse
		 * @param fileNum
		 * @return
		 * String
		 * @author changj@yuminsoft.com
		 * @date2018年3月14日
		 */
		@SuppressWarnings({ "static-access" })
		public String loadContractFile(Model_002022 model) {
			log.info("===================》》》H5征审加载合同文件接口入参：fileNum=" + model.getFileNum());
			Result result = Result.success();
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("fileNum", model.getFileNum());
				return zService.sendHttpPost4LoadContractFile(jsonObject.toString());
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof BusinessException) {
					String message = e.getMessage();
					result = Result.fail(message);
					log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
				} else {
					result = Result.fail();
					log.error("调用功能时系统异常: ", e);
				}
			}
			log.info("*************************响应*************************");
			log.info(JSONObject.fromObject(result).toString());
			return JSONObject.fromObject(result).toString();
		}
		/**
		 * 发送短信验证码
		 * @TODO
		 * @param httpRequest
		 * @param httpResponse
		 * @param loanNo
		 * @return
		 * String
		 * @author changj@yuminsoft.com
		 * @date2018年3月14日
		 */
		@SuppressWarnings({ "static-access" })
		public Result sendValidCode(Model_002023 model) {
			log.info("===================》》》H5征审 发送短信验证码口入参：mobile=" + model.getMobile());
			Result result = null;
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("mobile", model.getMobile());
				jsonObject.put("validType", "3");//1-注册 2-登录 3-签名验证 默认为3
				JSONObject respModel =  zService.sendHttpPost4SendValidCode(jsonObject.toString());
				result = result.success(respModel);
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof BusinessException) {
					String message = e.getMessage();
					result = Result.fail(message);
					log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
				} else {
					result = Result.fail();
					log.error("调用功能时系统异常: ", e);
				}
			}
			log.info("*************************响应*************************");
			log.info(JSONObject.fromObject(result).toString());
			return result;
		}
		
		/**
		 * 校验短信验证码
		 * @TODO
		 * @param httpRequest
		 * @param httpResponse
		 * @param mobile
		 * @return
		 * String
		 * @author changj@yuminsoft.com
		 * @date2018年3月14日
		 */
		@SuppressWarnings({ "static-access" })
		public Result checkValidCode(Model_002024 model) {
			log.info("===================》》》H5征审校验短信验证码接口入参：mobile=" + model.getMobile()+",validateCode="+model.getValidateCode());
			Result result = null;
			try {
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("mobile", model.getMobile());
				jsonObject.put("validateCode", model.getValidateCode());
				jsonObject.put("validType", "3");//1-注册 2-登录 3-签名验证 默认为3
				JSONObject respModel =  zService.sendHttpPost4CheckValidCode(jsonObject.toString());
				result = result.success(respModel);
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof BusinessException) {
					String message = e.getMessage();
					result = Result.fail(message);
					log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
				} else {
					result = Result.fail();
					log.error("调用功能时系统异常: ", e);
				}
			}
			log.info("*************************响应*************************");
			log.info(JSONObject.fromObject(result).toString());
			return result;
		}
		
		/**
		 * 客户签章
		 * @TODO
		 * @param httpRequest
		 * @param httpResponse
		 * @param loanNo
		 * @return
		 * String
		 * @author changj@yuminsoft.com
		 * @date2018年3月14日
		 */
		@SuppressWarnings({ "static-access" })
		public Result clientSinature(Model_002025 model) {
			log.info("===================》》》H5征审客户签章接口入参：loanNo=" + model.getLoanNo()+"身份证号="+model.getIdCard());
			Result result = null;
			try {
				Customer cust = customerService.queryByIdCard(model.getIdCard());
				//判断以个人签约信息
				ApplyContractInfo aci = new ApplyContractInfo();
				aci.setIdNo(model.getIdCard());
				aci.setIsNotSign(Constants.DATA_UNVALID);
				List<ApplyContractInfo> list = applyMapper.selectContactTable(aci);
				if(list.isEmpty()){
					Result re = signLcbService.idCardContract(model.getIdCard());
					if(re.getSuccess()){
						list = applyMapper.selectContactTable(aci);
					}
				}
				if(list != null && !list.isEmpty()){
					Result re = signLcbService.contractTS(list, model.getIdCard(), cust.getPhone(), cust.getCustomerName(), "");
					if(!re.getSuccess()){
						return re;
					}
					Model_006004 mo = new Model_006004();
					mo.setIdCard(model.getIdCard());
					mo.setLoanNo(model.getIdCard());
					mo.setPhone(cust.getPhone());
					mo.setUserName(cust.getCustomerName());
					Result res = signLcbService.signContract(mo);
					if(!res.getSuccess()){
						return res;
					}
				}
				
				//签约其他合同
				JSONObject jsonObject = new JSONObject();
				jsonObject.put("appNo", model.getLoanNo());
				jsonObject.put("sysCode", Constants.SYSCODE);
				jsonObject.put("infoCategory", Constants.INFOCATEGORY);
				zService.sendHttpPost4ClientSinature(jsonObject.toString());
				// 查询登录信息
				ResBMSNameaAndIdAndphone customer = null;
				String mobilePhone = "";
				String oldLoanNo = basicRedisOpts.getSingleResult("jxhj_"+model.getLoanNo());
				if (StrUtils.isNotBlank(oldLoanNo)) {//借旧换新活动
					log.info("借旧换新征审查询合同列表");
					customer = dmsService.getCustomerInfoByDms(oldLoanNo);
					if(customer ==null){
	        			 result = Result.fail("系统异常,请联系客服");
	        		}
					Customer localCustomer = customerService.queryByIdCard(customer.getIdNo());
					mobilePhone = localCustomer.getLcbAccount();
					log.info("mobilePhone=="+mobilePhone);
	        		jsonObject.put("urlLcb", HttpClientUtil.getLcbLogin()+"mobilePhone="+mobilePhone);
				}else{
					log.info("正常签约征审查询合同列表");
					//查询签章所需信息
					customer = dmsService.getCustomerInfoByDms(model.getLoanNo());
					if(customer == null){
	        			 Result.fail("系统异常,请联系客服");
	        		}
					
					//签约捞财宝
					boolean flag = dmsService.getTargetStatusByDms(model.getLoanNo());
					if(!flag){
						//法大大连接跳转签章
						String callbackURL = new String(Base64.encodeBase64URLSafe(backUrl.getBytes()));
						log.info("回调地址加密结果："+callbackURL);
						TreeMap<String, String> reqParams = new TreeMap<>();
						reqParams.put("borrowNo", customer.getBorrowNo());
						reqParams.put("callbackURL",callbackURL);
						String sign = MD5.getSign(reqParams, creditReqKey);
						
						Map<String, String> map = new HashMap<>();
						map.put("borrowNo", customer.getBorrowNo());
						map.put("callbackURL", callbackURL);
						map.put("sign", sign);
						log.info("捞财宝合同签章入参："+map);
						String  res  = HttpKit.post(lcbwgUrl+"/contractSign", map);
						JSONObject js = JSONObject.fromObject(res);
						log.info("捞财宝合同签章出参："+js);
						if(!Constants.RESP_CODE_SUCCESS.equals(js.getString("repCode"))){
							log.info("捞财宝出参错误内容："+js.getString("repMsg"));
							jsonObject.put("urlLcb", backUrl);
							return Result.fail("捞财宝签章失败");
						}
						String url = js.getString("url");
						
						//mobilePhone = customer.getCellPhoen();
						jsonObject.put("urlLcb", url);
						log.info("获取捞财宝地址："+ jsonObject);
					}else{
						//捞财宝签完过后
						jsonObject.put("urlLcb", backUrl);
						log.info("获取回退地址："+jsonObject);
						return Result.success(jsonObject);
					}
				}
				result = result.success(jsonObject);
			} catch (Exception e) {
				e.printStackTrace();
				if (e instanceof BusinessException) {
					String message = e.getMessage();
					result = Result.fail(message);
					log.error(MessageFormat.format("调用功能时业务异常: {0}", message));
				} else {
					result = Result.fail();
					log.error("调用功能时系统异常: ", e);
				}
			}
			log.info("*************************响应*************************");
			log.info(JSONObject.fromObject(result).toString());
			return result;
		}
		/**
		 * 捞财宝签章
		 * @param model
		 * @return
		 */
		@SuppressWarnings({ "static-access" })
		public Result contractLcb(Model_002029 model){
			log.info("===================》》》H5征审客户签章接口入参：loanNo=" + model.getLoanNo());
			//查询签章所需信息
			ResBMSNameaAndIdAndphone customer = dmsService.getCustomerInfoByDms(model.getLoanNo());
			if(customer == null){
    			 Result.fail("系统异常,请联系客服");
    		}
			JSONObject jsonObject = new JSONObject();
			//签约捞财宝
			boolean flag = dmsService.getTargetStatusByDms(model.getLoanNo());
			if(!flag){
				//法大大连接跳转签章
				String callbackURL = new String(Base64.encodeBase64URLSafe(backUrl.getBytes()));
				log.info("回调地址加密结果："+callbackURL);
				TreeMap<String, String> reqParams = new TreeMap<>();
				reqParams.put("borrowNo", customer.getBorrowNo());
				reqParams.put("callbackURL", callbackURL);
				String sign = MD5.getSign(reqParams, creditReqKey);
				
				Map<String, String> map = new HashMap<>();
				map.put("borrowNo", customer.getBorrowNo());
				map.put("callbackURL", callbackURL);
				map.put("sign", sign);
				log.info("捞财宝合同签章入参："+map);
				String  res  = HttpKit.post(lcbwgUrl+"/contractSign", map);
				JSONObject js = JSONObject.fromObject(res);
				log.info("捞财宝合同签章出参："+js);
				if(!Constants.RESP_CODE_SUCCESS.equals(js.getString("repCode"))){
					log.info("捞财宝出参错误内容："+js.getString("repMsg"));
					jsonObject.put("urlLcb", backUrl);
					return Result.fail("捞财宝签章失败");
				}
				String url = js.getString("url");
				//mobilePhone = customer.getCellPhoen();
				jsonObject.put("urlLcb", url);
				return Result.success(jsonObject);
			}else{
				jsonObject.put("urlLcb", backUrl);
				return Result.fail("捞财宝签章成功");
			}
			
		}
}
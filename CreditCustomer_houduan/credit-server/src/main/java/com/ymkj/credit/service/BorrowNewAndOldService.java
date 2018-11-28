package com.ymkj.credit.service;

import java.util.Calendar;
import java.util.List;








import javax.inject.Inject;

import lombok.extern.log4j.Log4j;
import net.sf.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;

import com.ymkj.credit.common.constants.Constants;
import com.ymkj.credit.common.entity.Customer;
import com.ymkj.credit.common.untils.MD5;
import com.ymkj.credit.network.HttpClientUtil;
import com.ymkj.credit.service.dms.DmsService;
import com.ymkj.credit.service.redis.BasicRedisOpts;
import com.ymkj.credit.web.api.dto.Customer4H5Dto;
import com.ymkj.credit.web.api.model.Result;
import com.ymkj.credit.web.api.model.base.Model_002010;
import com.ymkj.credit.web.api.model.base.Model_002011;
import com.ymkj.credit.web.api.model.base.Model_002012;
import com.ymkj.credit.web.api.model.base.Model_002013;
import com.ymkj.dms.api.common.base.AppNewLoanInfo;
import com.ymkj.springside.modules.exception.BusinessException;
import com.ymkj.springside.modules.utils.StrUtils;


@Log4j
@Service
public class BorrowNewAndOldService {
	

	@Inject
	private BasicRedisOpts basicRedisOpts;
	
	@Autowired
	private CustomerService customerService;
	
    @Autowired
    private DmsService dmsService;
    
	//账号自动登录的期限
	@Value("${automatic_logon_days}")
	private Integer automatic_logon_days;
	

	public Result login(Model_002010 model){
		String identityNo = model.getIdentityNo();//dc483e80a7a0bd9ef71d8cf973673924
//		String password = MD5.MD5Encode(model.getPassword());
		//根据入参的账号,业务状态和数据有效性查询客户是否存在
		Example example = new Example(Customer.class);
		Customer cus = new Customer();
		if(identityNo.trim().length()>11){
			/*Criteria criteria = example.createCriteria();
			criteria.andEqualTo("idCard", identityNo);
			criteria.andEqualTo("status", Constants.DATA_VALID);*/
			cus.setIdCard(identityNo);
			cus.setStatus(Constants.DATA_VALID);
		}else{
			/*Criteria criteria = example.createCriteria();
			criteria.andEqualTo("phone", identityNo);
			criteria.andEqualTo("status", Constants.DATA_VALID);*/
			cus.setPhone(identityNo);
			cus.setStatus(Constants.DATA_VALID);
		}
		List<Customer> list = customerService.selectByExample(cus);
		if(null==list||list.isEmpty()){
			return Result.fail("账号不存在,请重新注册");
		}
		if(list.size()>1){
			return Result.fail("此账号对应了多个用户");
		}
		Customer customer  = list.get(0);
		if(StrUtils.isEmpty(customer.getIdCard())){
			return Result.fail("此账号未绑定身份证");
		}
//		String passwordIdCard = MD5.MD5Encode(customer.getIdCard().substring(12, 18));//英文小写  密码小写
		if(!MD5.MD5Encode(model.getPassword()).equals(customer.getPassword())){
			return Result.fail("密码错误");
		}
		Customer4H5Dto dto = new Customer4H5Dto();
		dto.setCustomerName(customer.getCustomerName());
		dto.setId(customer.getId()+"");
		dto.setIdCard(customer.getIdCard());
		dto.setPhone(customer.getPhone());
		//每次登录生成新token  7日内
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, automatic_logon_days);
		String token = customer.getId().toString();
		basicRedisOpts.persist("h5_"+customer.getIdCard(), token,now.getTime());
		if(Constants.AGREED.equals(customer.getMemo())){
			dto.setIsAgreement(true);
		}else{
			dto.setIsAgreement(false);
		}
		return Result.success(dto);
	}
	
	/**
	 * 获取贷款信息
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月9日
	 */
	public Result queryCreditInfo(Model_002011 model) {
		/**
		 * 获取贷款信息
		 */
		JSONObject loanParam = new JSONObject();
		loanParam.put("idNum", model.getIdCard());
		JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4Jxhj(
				Constants.CREDIT_URL_LOANINFO_JXHJ, loanParam.toString());
		if (loanJson != null) {
			loanJson.put("idCard", model.getIdCard());
			loanJson.put("phone", model.getPhone());
			return Result.success(loanJson);
		}
		return Result.fail("未查询到贷款信息");
	}
	/**
	 * 检查参与权限
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月9日
	 */
	public Result checkJoinRole(Model_002012 model) {
		JSONObject joinRole = new JSONObject();
		String joinType = "-1";
		JSONObject loanParam = new JSONObject();
		loanParam.put("idNum", model.getIdCard());
		JSONObject loanJson = HttpClientUtil.sendHttpPostForCredit4Jxhj(
				Constants.CREDIT_URL_ACTIVITINFO_JXHJ, loanParam.toString());
		if (loanJson != null ) {
			if(!"800001".equals(loanJson.get("resCode"))){
				if(loanJson.getBoolean("acitityStatus")){
					joinType = "3";
				}else{
					joinType = loanJson.getString("activityType");
				}
			}
		}
		joinRole.put("joinType", joinType);
		log.info(joinRole);
		return Result.success(joinRole);
	}
	
	/**
	 * 
	 * @TODO
	 * @param model
	 * @return
	 * Result
	 * @author changj@yuminsoft.com
	 * @date2018年4月10日
	 */
	public Result sign(Model_002013 model) {
		AppNewLoanInfo loan = new AppNewLoanInfo();
		loan.setOldLoanNo(model.getOldLoanNo());
		loan.setIdNo(model.getIdCard());
		loan.setSysCode("app");
		String newLoanNo = dmsService.borrowNew(loan);
		JSONObject joinRole = new JSONObject();
		joinRole.put("newLoanNo", newLoanNo);
		//缓存老借款编号 用于投资咨询合同列表查询客户信息
		Calendar now = Calendar.getInstance();
		now.add(Calendar.DAY_OF_MONTH, 1);
		basicRedisOpts.persist("jxhj_"+newLoanNo, loan.getOldLoanNo(),now.getTime());
		return Result.success(joinRole);
	}
}
